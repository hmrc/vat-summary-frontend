/*
 * Copyright 2022 HM Revenue & Customs
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
import common.SessionKeys
import config.AppConfig
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import javax.inject.{Inject, Singleton}
import models.{CustomerInformation, User}
import models.payments.{OpenPaymentsModel, Payment, PaymentOnAccount}
import models.viewModels.OpenPaymentsViewModel
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.twirl.api.Html
import services.{AccountDetailsService, DateService, PaymentsService, ServiceInfoService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.errors.PaymentsError
import views.html.payments.{NoPayments, OpenPayments}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OpenPaymentsController @Inject()(authorisedController: AuthorisedController,
                                       serviceInfoService: ServiceInfoService,
                                       paymentsService: PaymentsService,
                                       dateService: DateService,
                                       auditingService: AuditingService,
                                       mcc: MessagesControllerComponents,
                                       noPayments: NoPayments,
                                       paymentsError: PaymentsError,
                                       openPaymentsPage: OpenPayments,
                                       accountDetailsService: AccountDetailsService)
                                      (implicit appConfig: AppConfig,
                                       ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  def openPayments(): Action[AnyContent] = authorisedController.financialAction { implicit request =>
    implicit user =>
        for {
          serviceInfoContent <- serviceInfoService.getPartial
          accountDetailsCall <- accountDetailsService.getAccountDetails(user.vrn)
          paymentsView <- renderView(serviceInfoContent, accountDetailsCall)
        } yield paymentsView
  }

  private[controllers] def renderView(serviceInfoContent: Html,
                                      accountDetails: HttpGetResult[CustomerInformation])
                                     (implicit request: Request[_], user: User, hc: HeaderCarrier): Future[Result] = {
    val clientName = request.session.get(SessionKeys.mtdVatvcAgentClientName)
    val mandationStatus = accountDetails.fold(_ => "", _.mandationStatus)
    paymentsService.getOpenPayments(user.vrn).map {
      case Right(Some(payments)) =>
        val model = getModel(payments.financialTransactions.filterNot(_.chargeType equals PaymentOnAccount), mandationStatus)
        auditEvent(user, model.payments)
        Ok(openPaymentsPage(user, model, serviceInfoContent, clientName))
      case Right(_) =>
        auditEvent(user, Seq.empty)
        Ok(noPayments(user, serviceInfoContent, clientName, mandationStatus))
      case Left(error) =>
        logger.warn("[OpenPaymentsController][openPayments] error: " + error.toString)
        InternalServerError(paymentsError())
    }
  }

  private[controllers] def getModel(payments: Seq[Payment], mandationStatus: String): OpenPaymentsViewModel =
    OpenPaymentsViewModel(
      payments.map { payment =>
        OpenPaymentsModel(
          payment,
          isOverdue = payment.due.isBefore(dateService.now()) && !payment.ddCollectionInProgress
        )
      },
      mandationStatus
    )

  private[controllers] def auditEvent(user: User, payments: Seq[OpenPaymentsModel])(implicit hc: HeaderCarrier): Unit = {
    auditingService.extendedAudit(ViewOutstandingVatPaymentsAuditModel(user, payments), routes.OpenPaymentsController.openPayments.url)
  }
}
