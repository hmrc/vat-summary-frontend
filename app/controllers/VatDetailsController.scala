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

import java.time.LocalDate

import audit.AuditingService
import audit.models.{ViewNextOpenVatObligationAuditModel, ViewNextOutstandingVatPaymentAuditModel}
import config.AppConfig
import javax.inject.{Inject, Singleton}

import models.{ServiceResponse, User, VatDetailsDataModel}
import models.obligations.{Obligation, VatReturnObligation, VatReturnObligations}
import models.payments.Payment
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
      val nextReturnCall = vatDetailsService.getReturns(user, dateService.now())
      val nextPaymentCall = vatDetailsService.getNextPayment(user, dateService.now())

      val viewModel = for {
        nextReturn <- nextReturnCall
        nextPayment <- nextPaymentCall
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
                                              nextPayment: ServiceResponse[Option[Payment]],
                                              entityName: ServiceResponse[Option[String]]): VatDetailsViewModel = {

    val returnModel: VatDetailsDataModel = obligations match {
      case Right(Some(obs)) => getObligationFlags(obs.obligations)
      case Right(_) => VatDetailsDataModel(None, hasMultiple = false, isOverdue = false, hasError = false)
      case Left(_) => VatDetailsDataModel(None, hasMultiple = false, isOverdue = false, hasError = true)
    }

    val getDueDate: ServiceResponse[Option[Obligation]] => Option[LocalDate] = _.fold(_ => None, _.map(_.due))
    val getIsOverdue: Option[LocalDate] => Boolean = _.fold(false)(d => dateService.now().isAfter(d))
    val payment: Option[LocalDate] = getDueDate(nextPayment)
    val paymentIsOverdue = getIsOverdue(payment)
    val paymentHasError = nextPayment.isLeft

    val displayedName = entityName match {
      case Right(name) => name
      case Left(_) => None
    }

    VatDetailsViewModel(
      payment,
      returnModel.displayData,
      displayedName,
      dateService.now().getYear,
      returnModel.hasMultiple,
      returnModel.isOverdue,
      returnModel.hasError,
      paymentIsOverdue,
      paymentHasError
    )
  }

  private[controllers] def auditEvents(user: User, nextReturn: ServiceResponse[Option[VatReturnObligations]],
                                       nextPayment: ServiceResponse[Option[Payment]])(implicit hc: HeaderCarrier): Unit = {
    val obligation: Option[VatReturnObligation] = nextReturn match {
      case Right(data) => data.map(_.obligations.head) // TODO: Update this as part of the audit task BTAT-2777
      case _ => None
    }

    val payment: Option[Payment] = nextPayment match {
      case Right(data) => data
      case _ => None
    }

    auditingService.audit(ViewNextOpenVatObligationAuditModel(user, obligation), routes.VatDetailsController.details().url)
    auditingService.audit(ViewNextOutstandingVatPaymentAuditModel(user, payment), routes.VatDetailsController.details().url)
  }
}
