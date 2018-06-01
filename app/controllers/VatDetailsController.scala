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
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import javax.inject.{Inject, Singleton}
import models.User
import models.errors.HttpError
import models.obligations.{Obligation, VatReturnObligation}
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
      val nextReturnCall = vatDetailsService.getNextReturn(user, dateService.now())
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

  private[controllers] def constructViewModel(nextReturn: HttpGetResult[Option[VatReturnObligation]],
                                              nextPayment: HttpGetResult[Option[Payment]], entityName: Option[String]): VatDetailsViewModel = {

    val getDueDate: Either[HttpError, Option[Obligation]] => Option[LocalDate] = _.fold(_ => None, _.map(_.due))
    val getIsOverdue: Option[LocalDate] => Boolean = _.fold(false)(d => dateService.now().isAfter(d))
    val payment: Option[LocalDate] = getDueDate(nextPayment)
    val paymentIsOverdue = getIsOverdue(payment)
    val paymentHasError = nextPayment.isLeft
    val obligation: Option[LocalDate] = getDueDate(nextReturn)
    val obligationIsOverdue = getIsOverdue(obligation)
    val obligationHasError = nextReturn.isLeft

    VatDetailsViewModel(
      payment,
      obligation,
      entityName,
      dateService.now().getYear,
      obligationIsOverdue,
      paymentIsOverdue,
      obligationHasError,
      paymentHasError
    )
  }

  private[controllers] def auditEvents(user: User, nextReturn: HttpGetResult[Option[VatReturnObligation]],
                                       nextPayment: HttpGetResult[Option[Payment]])(implicit hc: HeaderCarrier): Unit = {
    val obligation: Option[VatReturnObligation] = nextReturn match {
      case Right(data) => data
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
