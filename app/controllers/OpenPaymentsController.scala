/*
 * Copyright 2021 HM Revenue & Customs
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
import javax.inject.{Inject, Singleton}
import models.User
import models.payments.{OpenPaymentsModel, Payment, PaymentOnAccount}
import models.viewModels.OpenPaymentsViewModel
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.twirl.api.Html
import services.{DateService, EnrolmentsAuthService, PaymentsService, ServiceInfoService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.errors.PaymentsError
import views.html.payments.{NoPayments, OpenPayments}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OpenPaymentsController @Inject()(val enrolmentsAuthService: EnrolmentsAuthService,
                                       authorisedController: AuthorisedController,
                                       serviceInfoService: ServiceInfoService,
                                       val paymentsService: PaymentsService,
                                       val dateService: DateService,
                                       implicit val appConfig: AppConfig,
                                       auditingService: AuditingService,
                                       mcc: MessagesControllerComponents,
                                       implicit val ec: ExecutionContext,
                                       noPayments: NoPayments,
                                       paymentsError: PaymentsError,
                                       openPaymentsPage: OpenPayments)
extends FrontendController(mcc) with I18nSupport {

  def openPayments(): Action[AnyContent] = authorisedController.authorisedMigratedUserAction { implicit request =>
    implicit user =>
      for {
        directDebitStatus <- paymentsService.getDirectDebitStatus(user.vrn)
        serviceInfoContent <- serviceInfoService.getPartial
        paymentsView <- renderView(directDebitStatus.fold(_ => None, status => Some(status)), serviceInfoContent)
      } yield paymentsView
  }

  private[controllers] def renderView(hasActiveDirectDebit: Option[Boolean], serviceInfoContent: Html)
                                     (implicit request: Request[_], user: User, hc: HeaderCarrier): Future[Result] = {
    paymentsService.getOpenPayments(user.vrn).map {
      case Right(Some(payments)) =>
        val model = getModel(payments.financialTransactions.filterNot(_.chargeType equals PaymentOnAccount), hasActiveDirectDebit)
        auditEvent(user, model.payments)
        Ok(openPaymentsPage(user, model, serviceInfoContent))
      case Right(_) =>
        auditEvent(user, Seq.empty)
        Ok(noPayments(user, hasActiveDirectDebit, serviceInfoContent))
      case Left(error) =>
        Logger.warn("[OpenPaymentsController][openPayments] error: " + error.toString)
        InternalServerError(paymentsError())
    }
  }

  private[controllers] def getModel(payments: Seq[Payment], hasActiveDirectDebit: Option[Boolean]):
  OpenPaymentsViewModel = {
    OpenPaymentsViewModel(
      payments.map { payment =>
        OpenPaymentsModel(
          payment,
          isOverdue = appConfig.features.ddCollectionInProgressEnabled() && payment.due.isBefore(dateService.now()) && !payment.ddCollectionInProgress
        )
      },
      hasActiveDirectDebit
    )
  }

  private[controllers] def auditEvent(user: User, payments: Seq[OpenPaymentsModel])(implicit hc: HeaderCarrier): Unit = {
    auditingService.extendedAudit(ViewOutstandingVatPaymentsAuditModel(user, payments), routes.OpenPaymentsController.openPayments().url)
  }
}
