/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import audit.AuditingService
import audit.models.{ViewNextOpenVatObligationAuditModel, ViewNextOutstandingVatPaymentAuditModel}
import common.MandationStatus._
import common.SessionKeys
import config.{AppConfig, ServiceErrorHandler}
import connectors.httpParsers.ResponseHttpParsers
import connectors.httpParsers.ResponseHttpParsers.HttpResult
import models._
import models.obligations.{Obligation, VatReturnObligation, VatReturnObligations}
import models.payments.{Payment, Payments}
import models.penalties.PenaltiesSummary
import models.viewModels.VatDetailsViewModel
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.vatDetails.Details

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatDetailsController @Inject()(vatDetailsService: VatDetailsService,
                                     serviceInfoService: ServiceInfoService,
                                     authorisedController: AuthorisedController,
                                     accountDetailsService: AccountDetailsService,
                                     dateService: DateService,
                                     auditingService: AuditingService,
                                     penaltiesService: PenaltiesService,
                                     mcc: MessagesControllerComponents,
                                     detailsView: Details,
                                     serviceErrorHandler: ServiceErrorHandler,
                                     poaCheckService: POACheckService,
                                     paymentsOnAccountService: PaymentsOnAccountService)
                                    (implicit appConfig: AppConfig,
                                     ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  def details: Action[AnyContent] = authorisedController.authorisedAction { implicit request =>
    implicit user =>
        val accountDetailsCall = accountDetailsService.getAccountDetails(user.vrn)
        val returnObligationsCall = vatDetailsService.getReturnObligations(user.vrn)
        lazy val paymentObligationsCall = vatDetailsService.getPaymentObligations(user.vrn)
        for {
          customerInfo <- accountDetailsCall
          nextReturn <- returnObligationsCall
          nextPayment <- if (retrieveHybridStatus(customerInfo)) Future.successful(Right(None)) else paymentObligationsCall
          serviceInfoContent <- serviceInfoService.getPartial
          penaltiesCallResult <- penaltiesService.getPenaltiesInformation(user.vrn)
          today = dateService.now()
          standingRequest <- if (!isPoaActiveUser(customerInfo, today)) Future.successful(None) else paymentsOnAccountService.getPaymentsOnAccounts(user.vrn)
        } yield {

          auditEvents(user, nextReturn, nextPayment)

          val newSessionVariables: Seq[(String, String)] = customerInfo match {
            case Right(details) => Seq(
              SessionKeys.migrationToETMP -> details.customerMigratedToETMPDate.getOrElse(""),
              SessionKeys.mandationStatus -> details.mandationStatus
            )
            case Left(_) => Seq()
          }

          val penaltiesInfo = penaltiesCallResult.fold(_ => None, Some(_))
          if (redirectForMissingTrader(customerInfo)) {
            Redirect(appConfig.missingTraderRedirectUrl)
          } else {
            Ok(detailsView(
              constructViewModel(nextReturn, nextPayment, customerInfo, penaltiesInfo, standingRequest, today),
              serviceInfoContent
            )).addingToSession(newSessionVariables: _*)
          }
        }
  }

  private def isPoaActiveUser(customerInfo: HttpResult[CustomerInformation], today: LocalDate) = {
    appConfig.features.poaActiveFeatureEnabled() && poaCheckService.retrievePoaActiveForCustomer(customerInfo, today)
  }

  def detailsRedirectToEmailVerification: Action[AnyContent] = authorisedController.authorisedAction { implicit request =>
    implicit user =>
      accountDetailsService.getAccountDetails(user.vrn).flatMap {
        case Right(details) => details.emailAddress match {
          case Some(email) =>
            email.email match {
              case Some(emailAddress) =>
                val sessionValues: Seq[(String, String)] = Seq(SessionKeys.prepopulationEmailKey -> emailAddress) ++
                  (if(details.hasPendingPpobChanges) Seq() else Seq(SessionKeys.inFlightContactKey -> "false"))

                Future.successful(Redirect(appConfig.verifyEmailUrl).addingToSession(sessionValues: _*))
              case _ =>
                logger.warn("[VatDetailsController][detailsRedirectToEmailVerification] " +
                  "Email address not returned from vat-subscription.")
                serviceErrorHandler.showInternalServerError
            }
          case _ =>
            logger.warn("[VatDetailsController][detailsRedirectToEmailVerification] " +
              "Email status not returned from vat-subscription.")
            serviceErrorHandler.showInternalServerError
        }
        case Left(_) =>
          logger.warn("[VatDetailsController][detailsRedirectToEmailVerification] Could not retrieve account details.")
          serviceErrorHandler.showInternalServerError
      }
  }

  private[controllers] def redirectForMissingTrader(customerInfo: ResponseHttpParsers.HttpResult[CustomerInformation]) = {
    customerInfo.fold(
      _ => false,
      details => details.isMissingTrader && !details.hasPendingPpobChanges
    )
  }

  private[controllers] def getPaymentObligationDetails(payments: Seq[Payment], today: LocalDate): VatDetailsDataModel = {
    val isOverdue = payments.head.isOverdue(today)
    getObligationDetails(
      payments,
      isOverdue
    )
  }

  private[controllers] def getReturnObligationDetails(obligations: Seq[VatReturnObligation], today: LocalDate): VatDetailsDataModel =
    getObligationDetails(
      obligations.distinct,
      obligations.head.due.isBefore(today)
    )

  private[controllers] def getObligationDetails(obligations: Seq[Obligation], isOverdue: Boolean): VatDetailsDataModel = {
    val hasMultiple = obligations.size > 1
    val data: String = if (hasMultiple) obligations.size.toString else obligations.head.due.toString

    VatDetailsDataModel(
      displayData = Some(data),
      hasMultiple = hasMultiple,
      isOverdue = isOverdue,
      hasError = false
    )
  }

  private[controllers] def constructViewModel(obligations: ServiceResponse[Option[VatReturnObligations]],
                                              payments: ServiceResponse[Option[Payments]],
                                              accountDetails: HttpResult[CustomerInformation],
                                              penaltyInformation: Option[PenaltiesSummary],
                                              standingRequest: Option[StandingRequest],
                                              today: LocalDate
                                              ): VatDetailsViewModel = {

    val returnModel: VatDetailsDataModel = retrieveReturns(obligations, today)
    val paymentModel: VatDetailsDataModel = retrievePayments(payments, today)
    val displayedName: Option[String] = retrieveDisplayedName(accountDetails)
    val isHybridUser: Boolean = retrieveHybridStatus(accountDetails)
    val partyType: Option[String] = retrievePartyType(accountDetails)
    val customerInfoError: Boolean = accountDetails.isLeft
    val deregDate: Option[LocalDate] = retrieveDeregDate(accountDetails)
    val pendingDereg: Boolean = accountDetails.fold(_ => false, _.changeIndicators.exists(_.deregister))
    val emailAddress: Option[String] = accountDetails.fold(_ => None, _.emailAddress.flatMap(_.email))
    val mandationStatus: String = accountDetails.fold(_ => "ERROR", _.mandationStatus)
    val isPoaActiveForCustomer: Boolean = poaCheckService.retrievePoaActiveForCustomer(accountDetails, today)
    val poaChangedOn: Option[LocalDate] = poaCheckService.changedOnDateWithInLatestVatPeriod(standingRequest, today)
    val isAnnualAccountingCustomer: Boolean = obligations.fold(_ => false, _.exists(_.obligations.exists(_.periodKey.startsWith("Y"))))
    VatDetailsViewModel(
      paymentModel.displayData,
      returnModel.displayData,
      displayedName,
      returnModel.hasMultiple,
      returnModel.isOverdue,
      returnModel.hasError,
      paymentModel.hasMultiple,
      paymentModel.isOverdue,
      paymentModel.hasError,
      isHybridUser,
      retrieveIsOfStatus(accountDetails, Seq(nonMTDfB, nonDigital, mtdfbExempt)),
      customerInfoError,
      deregDate,
      pendingDereg,
      dateService.now(),
      partyType,
      retrieveEmailVerifiedIfExist(accountDetails),
      emailAddress,
      penaltyInformation,
      mandationStatus,
      isPoaActiveForCustomer,
      poaChangedOn,
      isAnnualAccountingCustomer
    )
  }

  private[controllers] def retrieveEmailVerifiedIfExist(accountDetails: HttpResult[CustomerInformation]): Boolean = {
    accountDetails match {
      case Right(details) =>
        details.emailAddress match {
          case _ if details.hasPendingPpobChanges => true
          case Some(Email(Some(_), Some(verified))) => verified
          case Some(Email(Some(_), None)) => false
          case _ => true
        }
      case _ => true
    }
  }

  private[controllers] def retrieveIsOfStatus(customerInfo: HttpResult[CustomerInformation],
                                              expectedType: Seq[String]): Option[Boolean] =
    customerInfo.fold(
      _ => None,
      result => Some(expectedType.contains(result.mandationStatus))
    )

  private def retrieveHybridStatus(accountDetails: HttpResult[CustomerInformation]): Boolean =
    accountDetails match {
      case Right(model) => model.isHybridUser
      case Left(_) => false
    }

  private def retrieveDisplayedName(accountDetails: HttpResult[CustomerInformation]): Option[String] =
    accountDetails match {
      case Right(model) =>
        if (model.details.entityName.isEmpty) {
          logger.warn("[VatDetailsController][retrieveDisplayedName] - No entity name was found on record")
        }
        model.details.entityName
      case Left(_) => None
    }

  private def retrieveDeregDate(accountDetails: HttpResult[CustomerInformation]): Option[LocalDate] =
    accountDetails match {
      case Right(model) => model.deregistration.flatMap(_.effectDateOfCancellation)
      case Left(_) => None
    }

  private def retrievePartyType(accountDetails: HttpResult[CustomerInformation]): Option[String] =
    accountDetails match {
      case Right(model) => model.partyType
      case Left(_) => None
    }

  private def retrievePayments(payments: ServiceResponse[Option[Payments]], today: LocalDate): VatDetailsDataModel =
    payments match {
      case Right(Some(model)) => getPaymentObligationDetails(model.financialTransactions, today)
      case Right(_) => VatDetailsDataModel(None, hasMultiple = false, isOverdue = false, hasError = false)
      case Left(_) => VatDetailsDataModel(None, hasMultiple = false, isOverdue = false, hasError = true)
    }

  private def retrieveReturns(obligations: ServiceResponse[Option[VatReturnObligations]], today: LocalDate): VatDetailsDataModel =
    obligations match {
      case Right(Some(obs)) => getReturnObligationDetails(obs.obligations, today)
      case Right(_) => VatDetailsDataModel(None, hasMultiple = false, isOverdue = false, hasError = false)
      case Left(_) => VatDetailsDataModel(None, hasMultiple = false, isOverdue = false, hasError = true)
    }

  private[controllers] def auditEvents(user: User,
                                       returnObligations: ServiceResponse[Option[VatReturnObligations]],
                                       paymentObligations: ServiceResponse[Option[Payments]])
                                      (implicit hc: HeaderCarrier): Unit = {
    auditingService.audit(
      ViewNextOutstandingVatPaymentAuditModel(user, paymentObligations), routes.VatDetailsController.details.url)
    auditingService.audit(
      ViewNextOpenVatObligationAuditModel(user, returnObligations), routes.VatDetailsController.details.url)
  }
}
