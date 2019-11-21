/*
 * Copyright 2019 HM Revenue & Customs
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
import common.FinancialTransactionsConstants.{nonMTDfB, nonDigital}
import common.SessionKeys
import config.AppConfig
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import javax.inject.{Inject, Singleton}
import models._
import models.obligations.{Obligation, VatReturnObligations}
import models.payments.Payments
import models.viewModels.VatDetailsViewModel
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Request}
import services._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class VatDetailsController @Inject()(val messagesApi: MessagesApi,
                                     val enrolmentsAuthService: EnrolmentsAuthService,
                                     implicit val appConfig: AppConfig,
                                     vatDetailsService: VatDetailsService,
                                     serviceInfoService: ServiceInfoService,
                                     authorisedController: AuthorisedController,
                                     val accountDetailsService: AccountDetailsService,
                                     dateService: DateService,
                                     auditingService: AuditingService,
                                     mandationStatusService: MandationStatusService)
  extends FrontendController with I18nSupport {

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
        mandationStatus <- if(appConfig.features.submitReturnFeatures()) {
          retrieveMandationStatus(user.vrn)
        } else { Future.successful(Right(MandationStatus("Disabled"))) }
      } yield {
        val migratedDate = (request.session.get(SessionKeys.migrationToETMP), customerInfo) match {
          case (Some(date), _) => date
          case (None, Right(details)) => details.customerMigratedToETMPDate.getOrElse("")
          case (None, Left(_)) => ""
        }
        auditEvents(user, nextReturn, nextPayment)

        val newSessionVariables: Seq[(String, String)] = Seq(SessionKeys.migrationToETMP -> migratedDate) ++ (mandationStatus match {
          case Right(status) => Seq(SessionKeys.mandationStatus -> status.mandationStatus)
          case _ => Seq()
        })

        Ok(views.html.vatDetails.details(
          constructViewModel(nextReturn, nextPayment, customerInfo, mandationStatus), serviceInfoContent
        )).addingToSession(newSessionVariables: _*)
      }
  }

  private[controllers] def getObligationFlags(obligations: Seq[Obligation]): VatDetailsDataModel = {
    val hasMultiple = obligations.size > 1
    val data: String = if (hasMultiple) obligations.size.toString else obligations.head.due.toString

    VatDetailsDataModel(
      displayData = Some(data),
      hasMultiple = hasMultiple,
      isOverdue = if (obligations.size == 1) obligations.head.due.isBefore(dateService.now()) else false,
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
    val isNonMTDfBOrNonDigital: Option[Boolean] = retrieveIsNonMTDfBOrNonDigital(mandationStatus)
    val customerInfoError: Boolean = accountDetails.isLeft

    VatDetailsViewModel(
      paymentModel.displayData,
      returnModel.displayData,
      displayedName,
      dateService.now().getYear,
      returnModel.hasMultiple,
      returnModel.isOverdue,
      returnModel.hasError,
      paymentModel.hasMultiple,
      paymentModel.isOverdue,
      paymentModel.hasError,
      isHybridUser,
      isNonMTDfBOrNonDigital,
      customerInfoError,
      pendingOptOut
    )
  }


  def retrieveMandationStatus(vrn: String)(implicit request: Request[AnyContent]): Future[HttpGetResult[MandationStatus]] = {
    val mtdMandationSessionKey = SessionKeys.mandationStatus

    request.session.get(mtdMandationSessionKey) match {
      case Some(value) => Future.successful(Right(MandationStatus(value)))
      case _ => mandationStatusService.getMandationStatus(vrn)
    }
  }

  private def retrieveIsNonMTDfBOrNonDigital(mandationStatus: HttpGetResult[MandationStatus]): Option[Boolean] = {
    mandationStatus.fold(
      _ => None,
      result => Some(result.mandationStatus == nonMTDfB || result.mandationStatus == nonDigital)
    )
  }

  private def retrieveHybridStatus(accountDetails: HttpGetResult[CustomerInformation]): Boolean = {
    accountDetails match {
      case Right(model) => model.isHybridUser
      case Left(error) =>
        Logger.warn("[VatDetailsController][isHybridUser] could not retrieve hybrid status: " + error.toString)
        false
    }
  }

  private def retrieveDisplayedName(accountDetails: HttpGetResult[CustomerInformation]): Option[String] = {
    accountDetails match {
      case Right(model) => model.entityName
      case Left(error) =>
        Logger.warn("[VatDetailsController][displayedName] could not retrieve display name: " + error.toString)
        None
    }
  }

  private def retrievePayments(payments: ServiceResponse[Option[Payments]]): VatDetailsDataModel = {
    payments match {
      case Right(Some(model)) => getObligationFlags(model.financialTransactions)
      case Right(_) => VatDetailsDataModel(None, hasMultiple = false, isOverdue = false, hasError = false)
      case Left(error) =>
        Logger.warn("[VatDetailsController][constructViewModel] error: " + error.toString)
        VatDetailsDataModel(None, hasMultiple = false, isOverdue = false, hasError = true)
    }
  }

  private def retrieveReturns(obligations: ServiceResponse[Option[VatReturnObligations]]): VatDetailsDataModel = {
    obligations match {
      case Right(Some(obs)) => getObligationFlags(obs.obligations)
      case Right(_) => VatDetailsDataModel(None, hasMultiple = false, isOverdue = false, hasError = false)
      case Left(error) =>
        Logger.warn("[VatDetailsController][constructViewModel] error: " + error.toString)
        VatDetailsDataModel(None, hasMultiple = false, isOverdue = false, hasError = true)
    }
  }

  private[controllers] def auditEvents(user: User, returnObligations: ServiceResponse[Option[VatReturnObligations]],
                                       paymentObligations: ServiceResponse[Option[Payments]])(implicit hc: HeaderCarrier): Unit = {

    val returnObs: Option[VatReturnObligations] = returnObligations match {
      case Right(returns) => returns
      case _ => None
    }

    val paymentObs: Option[Payments] = paymentObligations match {
      case Right(payments) => payments
      case _ => None
    }

    auditingService.audit(ViewNextOutstandingVatPaymentAuditModel(user, paymentObs), routes.VatDetailsController.details().url)
    auditingService.audit(ViewNextOpenVatObligationAuditModel(user, returnObs), routes.VatDetailsController.details().url)
  }
}
