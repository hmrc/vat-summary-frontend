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
import audit.models.ViewOutstandingVatPaymentsAuditModel
import config.AppConfig
import javax.inject.Inject

import models.User
import models.payments.{OpenPaymentsModel, Payment}
import models.viewModels.OpenPaymentsViewModel
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.{DateService, EnrolmentsAuthService, PaymentsService}
import uk.gov.hmrc.http.HeaderCarrier

class OpenPaymentsController @Inject()(val messagesApi: MessagesApi,
                                       val enrolmentsAuthService: EnrolmentsAuthService,
                                       val paymentsService: PaymentsService,
                                       val dateService: DateService,
                                       implicit val appConfig: AppConfig,
                                       auditingService: AuditingService)
extends AuthorisedController with I18nSupport {

  def openPayments(): Action[AnyContent] = authorisedAction { implicit request =>
    user =>

      def getView(hasActiveDirectDebit: Option[Boolean]) = {
        paymentsService.getOpenPayments(user.vrn).map {
          case Right(Some(payments)) =>
            val model = getModel(payments.financialTransactions, hasActiveDirectDebit)
            auditEvent(user, model.payments)
            Ok(views.html.payments.openPayments(user, model))
          case Right(_) =>
            auditEvent(user, Seq.empty)
            Ok(views.html.payments.noPayments(user))
          case Left(_) => InternalServerError(views.html.errors.paymentsError())
        }
      }

      paymentsService.getDirectDebitStatus(user.vrn).flatMap {
        case Right(status) => getView(Some(status))
        case Left(_) => getView(None)
      }
  }

  private[controllers] def getModel(payments: Seq[Payment], hasActiveDirectDebit: Option[Boolean]): OpenPaymentsViewModel = {
    OpenPaymentsViewModel(
      payments.map { payment =>
        OpenPaymentsModel(
          payment.chargeType,
          payment.outstandingAmount,
          payment.due,
          payment.start,
          payment.end,
          payment.periodKey,
          payment.due.isBefore(dateService.now())
        )
      },
      hasActiveDirectDebit
    )
  }

  private[controllers] def auditEvent(user: User, payments: Seq[OpenPaymentsModel])(implicit hc: HeaderCarrier): Unit = {
    auditingService.extendedAudit(ViewOutstandingVatPaymentsAuditModel(user, payments), routes.OpenPaymentsController.openPayments().url)
  }
}
