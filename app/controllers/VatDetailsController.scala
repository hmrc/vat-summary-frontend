/*
 * Copyright 2018 HM Revenue & Customs
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
import config.AppConfig
import javax.inject.{Inject, Singleton}
import models.{ServiceResponse, User, VatDetailsDataModel}
import models.obligations.{Obligation, VatReturnObligations}
import models.payments.Payments
import models.obligations.{Obligation, VatReturnObligations}
import models.viewModels.VatDetailsViewModel
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.{AccountDetailsService, DateService, EnrolmentsAuthService, VatDetailsService}
import uk.gov.hmrc.http.HeaderCarrier

@Singleton
class VatDetailsController @Inject()(val messagesApi: MessagesApi,
                                     val enrolmentsAuthService: EnrolmentsAuthService,
                                     implicit val appConfig: AppConfig,
                                     vatDetailsService: VatDetailsService,
                                     accountDetailsService: AccountDetailsService,
                                     dateService: DateService,
                                     auditingService: AuditingService)
  extends AuthorisedController with I18nSupport {

  def details(): Action[AnyContent] = authorisedAction { implicit request =>
    user =>
      val entityNameCall = accountDetailsService.getEntityName(user.vrn)
      val returnObligationsCall = vatDetailsService.getReturnObligations(user, dateService.now())
      val paymentObligationsCall = vatDetailsService.getPaymentObligations(user)

      val viewModel = for {
        nextReturn <- returnObligationsCall
        nextPayment <- paymentObligationsCall
        customerInfo <- entityNameCall
      } yield {
        auditEvents(user, nextReturn, nextPayment)
        constructViewModel(nextReturn, nextPayment, customerInfo)
      }

      viewModel.map { model =>
        Ok(views.html.vatDetails.details(user, model))
      }
  }

  private[controllers] def getObligationFlags(obligations: Seq[Obligation]): VatDetailsDataModel = {
    val hasMultiple = obligations.size > 1
    val data: String = if(hasMultiple) obligations.size.toString else obligations.head.due.toString

    VatDetailsDataModel(
      displayData = Some(data),
      hasMultiple = hasMultiple,
      isOverdue = if(obligations.size == 1) obligations.head.due.isBefore(dateService.now()) else false,
      hasError = false
    )
  }

  private[controllers] def constructViewModel(obligations: ServiceResponse[Option[VatReturnObligations]],
                                              payments: ServiceResponse[Option[Payments]],
                                              entityName: ServiceResponse[Option[String]]): VatDetailsViewModel = {

    val returnModel: VatDetailsDataModel = obligations match {
      case Right(Some(obs)) => getObligationFlags(obs.obligations)
      case Right(_) => VatDetailsDataModel(None, hasMultiple = false, isOverdue = false, hasError = false)
      case Left(_) => VatDetailsDataModel(None, hasMultiple = false, isOverdue = false, hasError = true)
    }

    val paymentModel: VatDetailsDataModel = payments match {
      case Right(Some(paymnts)) => getObligationFlags(paymnts.financialTransactions)
      case Right(_) => VatDetailsDataModel(None, hasMultiple = false, isOverdue = false, hasError = false)
      case Left(_) => VatDetailsDataModel(None, hasMultiple = false, isOverdue = false, hasError = true)
    }

    val displayedName = entityName match {
      case Right(name) => name
      case Left(_) => None
    }

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
      paymentModel.hasError
    )
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
