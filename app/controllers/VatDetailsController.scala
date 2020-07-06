/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.LocalDate

import audit.AuditingService
import audit.models.{ViewNextOpenVatObligationAuditModel, ViewNextOutstandingVatPaymentAuditModel}
import common.FinancialTransactionsConstants._
import common.SessionKeys
import config.{AppConfig, ServiceErrorHandler}
import connectors.httpParsers.ResponseHttpParsers
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import javax.inject.{Inject, Singleton}
import models._
import models.obligations.{Obligation, VatReturnObligation, VatReturnObligations}
import models.payments.{Payment, Payments}
import models.viewModels.VatDetailsViewModel
import org.slf4j
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import services._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.vatDetails.Details

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatDetailsController @Inject()(val enrolmentsAuthService: EnrolmentsAuthService,
                                     implicit val appConfig: AppConfig,
                                     vatDetailsService: VatDetailsService,
                                     serviceInfoService: ServiceInfoService,
                                     authorisedController: AuthorisedController,
                                     val accountDetailsService: AccountDetailsService,
                                     dateService: DateService,
                                     auditingService: AuditingService,
                                     mandationStatusService: MandationStatusService,
                                     mcc: MessagesControllerComponents,
                                     implicit val ec: ExecutionContext,
                                     detailsView: Details,
                                     serviceErrorHandler: ServiceErrorHandler)
  extends FrontendController(mcc) with I18nSupport {

  val logger: slf4j.Logger = Logger.logger

  def details(): Action[AnyContent] = authorisedController.authorisedAction { implicit request =>
    implicit user =>
      val accountDetailsCall = accountDetailsService.getAccountDetails(user.vrn)
      val returnObligationsCall = vatDetailsService.getReturnObligations(user.vrn, dateService.now())
      lazy val paymentObligationsCall = vatDetailsService.getPaymentObligations(user.vrn)

      for {
        customerInfo <- accountDetailsCall
        nextReturn <- returnObligationsCall
        nextPayment <- if (retrieveHybridStatus(customerInfo)) Future.successful(Right(None)) else paymentObligationsCall
        serviceInfoContent <- serviceInfoService.getPartial
        mandationStatus <- if (appConfig.features.submitReturnFeatures()) {
          retrieveMandationStatus(user.vrn)
        } else {
          Future.successful(Right(MandationStatus("Disabled")))
        }
      } yield {
        val migratedDate = (request.session.get(SessionKeys.migrationToETMP), customerInfo) match {
          case (Some(date), _) => date
          case (None, Right(details)) => details.customerMigratedToETMPDate.getOrElse("")
          case (None, Left(_)) => ""
        }
        auditEvents(user, nextReturn, nextPayment)

        val newSessionVariables: Seq[(String, String)] =
          Seq(SessionKeys.migrationToETMP -> migratedDate) ++ (mandationStatus match {
            case Right(status) => Seq(SessionKeys.mandationStatus -> status.mandationStatus)
            case _ => Seq()
          })

        if (redirectForMissingTrader(customerInfo)) {
          Redirect(appConfig.missingTraderRedirectUrl)
        } else {
          Ok(detailsView(
            constructViewModel(nextReturn, nextPayment, customerInfo, mandationStatus), dateService.isPreCovidDeadline(), serviceInfoContent
          )).addingToSession(newSessionVariables: _*)
        }
      }
  }

  def detailsRedirectToEmailVerification: Action[AnyContent] = authorisedController.authorisedAction { implicit request =>
    implicit user =>
      val accountDetailsCall = accountDetailsService.getAccountDetails(user.vrn)

      for {
        accountDetails <- accountDetailsCall
      } yield {
        accountDetails match {
          case Right(details) => details.emailAddress match {
            case Some(email) =>
              email.email match {
                case Some(emailAddress) => Redirect(appConfig.verifyEmailUrl).addingToSession(
                  SessionKeys.prepopulationEmailKey -> emailAddress, SessionKeys.inFlightContactKey -> "false"
                )
                case _ =>
                  logger.warn("[VatDetailsController][detailsRedirectToEmailVerification] Email address not returned from vat-subscription.")
                  serviceErrorHandler.showInternalServerError
              }
            case _ =>
              logger.warn("[VatDetailsController][detailsRedirectToEmailVerification] Email status not returned from vat-subscription.")
              serviceErrorHandler.showInternalServerError
          }
          case Left(_) =>
            logger.warn("[VatDetailsController][detailsRedirectToEmailVerification] Could not retrieve account details.")
            serviceErrorHandler.showInternalServerError
        }
      }
  }

  private def redirectForMissingTrader(customerInfo: ResponseHttpParsers.HttpGetResult[CustomerInformation]) = {
    customerInfo.fold(_ => false, details => appConfig.features.missingTraderAddressIntercept() && details.isMissingTrader)
  }

  private[controllers] def getPaymentObligationDetails(payments: Seq[Payment]): VatDetailsDataModel = {
    val isOverdue = payments.headOption.fold(false) { payment =>
      appConfig.features.ddCollectionInProgressEnabled() &&
        payment.due.isBefore(dateService.now()) &&
        !payment.ddCollectionInProgress
    }
    getObligationDetails(
      payments,
      isOverdue
    )
  }

  private[controllers] def getReturnObligationDetails(obligations: Seq[VatReturnObligation]): VatDetailsDataModel =
    getObligationDetails(
      obligations,
      obligations.headOption.fold(false)(obligation => obligation.due.isBefore(dateService.now()))
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
                                              accountDetails: HttpGetResult[CustomerInformation],
                                              mandationStatus: HttpGetResult[MandationStatus]): VatDetailsViewModel = {

    val returnModel: VatDetailsDataModel = retrieveReturns(obligations)
    val paymentModel: VatDetailsDataModel = retrievePayments(payments)
    val displayedName: Option[String] = retrieveDisplayedName(accountDetails)
    val isHybridUser: Boolean = retrieveHybridStatus(accountDetails)
    val pendingOptOut: Boolean =
      accountDetails.fold(_ => false, details => details.pendingMandationStatus.fold(false)(_ == nonMTDfB))
    val partyType: Option[String] = retrievePartyType(accountDetails)
    val customerInfoError: Boolean = accountDetails.isLeft
    val deregDate: Option[LocalDate] = retrieveDeregDate(accountDetails)
    val pendingDereg: Boolean = accountDetails.fold(_ => false, _.changeIndicators.exists(_.deregister))

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
      retrieveIsOfStatus(mandationStatus, Seq(nonMTDfB, nonDigital, mtdfbExempt)),
      customerInfoError,
      pendingOptOut,
      deregDate,
      pendingDereg,
      dateService.now(),
      partyType,
      retrieveEmailVerifiedIfExist(accountDetails)
    )
  }


  private[controllers] def retrieveEmailVerifiedIfExist(accountDetails: HttpGetResult[CustomerInformation]): Boolean = {
    accountDetails.fold(_ => true, customerInfo =>
      customerInfo.emailAddress match {
        case Some(emailInformation) =>
          (emailInformation.email, emailInformation.emailVerified) match {
            case (Some(_), Some(verified)) => verified
            case (Some(_), None) => false
            case _ => true
          }
        case _ => true
      })
  }

  def retrieveMandationStatus(vrn: String)
                             (implicit request: Request[AnyContent]): Future[HttpGetResult[MandationStatus]] = {
    val mtdMandationSessionKey = SessionKeys.mandationStatus

    request.session.get(mtdMandationSessionKey) match {
      case Some(value) => Future.successful(Right(MandationStatus(value)))
      case _ => mandationStatusService.getMandationStatus(vrn)
    }
  }

  private[controllers] def retrieveIsOfStatus(mandationStatus: HttpGetResult[MandationStatus],
                                              expectedType: Seq[String]): Option[Boolean] =
    mandationStatus.fold(
      _ => None,
      result => Some(expectedType.contains(result.mandationStatus))
    )

  private def retrieveHybridStatus(accountDetails: HttpGetResult[CustomerInformation]): Boolean =
    accountDetails match {
      case Right(model) => model.isHybridUser
      case Left(_) => false
    }

  private def retrieveDisplayedName(accountDetails: HttpGetResult[CustomerInformation]): Option[String] =
    accountDetails match {
      case Right(model) =>
        if (model.entityName.isEmpty) {
          Logger.warn("[VatDetailsController][retrieveDisplayedName] - No entity name was found on record")
        }
        model.entityName
      case Left(_) => None
    }

  private def retrieveDeregDate(accountDetails: HttpGetResult[CustomerInformation]): Option[LocalDate] =
    accountDetails match {
      case Right(model) => model.deregistration.flatMap(_.effectDateOfCancellation)
      case Left(_) => None
    }

  private def retrievePartyType(accountDetails: HttpGetResult[CustomerInformation]): Option[String] =
    accountDetails match {
      case Right(model) => model.partyType
      case Left(_) => None
    }

  private def retrievePayments(payments: ServiceResponse[Option[Payments]]): VatDetailsDataModel =
    payments match {
      case Right(Some(model)) => getPaymentObligationDetails(model.financialTransactions)
      case Right(_) => VatDetailsDataModel(None, hasMultiple = false, isOverdue = false, hasError = false)
      case Left(_) => VatDetailsDataModel(None, hasMultiple = false, isOverdue = false, hasError = true)
    }

  private def retrieveReturns(obligations: ServiceResponse[Option[VatReturnObligations]]): VatDetailsDataModel =
    obligations match {
      case Right(Some(obs)) => getReturnObligationDetails(obs.obligations)
      case Right(_) => VatDetailsDataModel(None, hasMultiple = false, isOverdue = false, hasError = false)
      case Left(_) => VatDetailsDataModel(None, hasMultiple = false, isOverdue = false, hasError = true)
    }

  private[controllers] def auditEvents(user: User,
                                       returnObligations: ServiceResponse[Option[VatReturnObligations]],
                                       paymentObligations: ServiceResponse[Option[Payments]])
                                      (implicit hc: HeaderCarrier): Unit = {

    val returnObs: Option[VatReturnObligations] = returnObligations match {
      case Right(returns) => returns
      case _ => None
    }

    val paymentObs: Option[Payments] = paymentObligations match {
      case Right(payments) => payments
      case _ => None
    }

    auditingService.audit(
      ViewNextOutstandingVatPaymentAuditModel(user, paymentObs), routes.VatDetailsController.details().url)
    auditingService.audit(
      ViewNextOpenVatObligationAuditModel(user, returnObs), routes.VatDetailsController.details().url)
  }
}
