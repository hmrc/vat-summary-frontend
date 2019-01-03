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
import audit.models.PayVatReturnChargeAuditModel
import config.AppConfig
import javax.inject.{Inject, Singleton}
import models.User
import models.payments.{ChargeType, PaymentDetailsModel, PaymentDetailsModelNoPeriod, PaymentDetailsModelWithPeriod}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Request, Result}
import services.{EnrolmentsAuthService, PaymentsService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.errors.paymentsError

import scala.concurrent.Future

@Singleton
class MakePaymentController @Inject()(val messagesApi: MessagesApi,
                                      val enrolmentsAuthService: EnrolmentsAuthService,
                                      paymentsService: PaymentsService,
                                      implicit val appConfig: AppConfig,
                                      authorisedController: AuthorisedController,
                                      auditingService: AuditingService)
  extends FrontendController with I18nSupport {

  def makePayment(amountInPence: Long, taxPeriodMonth: Int, taxPeriodYear: Int, chargeType: String, dueDate: String): Action[AnyContent] =
    authorisedController.authorisedAction { implicit request =>
      implicit user =>

        val paymentDetails = PaymentDetailsModelWithPeriod(
          taxType = "vat",
          taxReference = user.vrn,
          amountInPence = amountInPence,
          taxPeriodMonth = taxPeriodMonth,
          taxPeriodYear = taxPeriodYear,
          returnUrl = appConfig.paymentsReturnUrl,
          backUrl = appConfig.paymentsBackUrl,
          chargeType = ChargeType.apply(chargeType),
          dueDate = dueDate
        )

        makePaymentHandoff(paymentDetails)
    }

  def makePaymentNoPeriod(amountInPence: Long, chargeType: String, dueDate: String): Action[AnyContent] =
    authorisedController.authorisedAction { implicit request =>
      implicit user =>

        val paymentDetails = PaymentDetailsModelNoPeriod(
          taxType = "vat",
          taxReference = user.vrn,
          amountInPence = amountInPence,
          returnUrl = appConfig.paymentsReturnUrl,
          backUrl = appConfig.paymentsBackUrl,
          chargeType = ChargeType.apply(chargeType),
          dueDate = dueDate
        )

        makePaymentHandoff(paymentDetails)

    }

  private def makePaymentHandoff(paymentDetails: PaymentDetailsModel)(implicit request: Request[_],
                                                                      user: User,
                                                                      hc: HeaderCarrier): Future[Result] = {
    paymentsService.setupPaymentsJourney(paymentDetails).map {
      case Right(url) =>
        auditingService.audit(
          PayVatReturnChargeAuditModel(user, paymentDetails, url),
          request.uri
        )
        Redirect(url)
      case Left(error) =>
        Logger.warn("[MakePaymentController][makePayment] error: " + error.toString)
        InternalServerError(paymentsError())
    }
  }
}
