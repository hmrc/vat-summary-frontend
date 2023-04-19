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
import audit.models.{PayFullChargeAuditModel, PayVatReturnChargeAuditModel}
import config.AppConfig

import javax.inject.{Inject, Singleton}
import models.User
import models.payments._
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.PaymentsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.errors.PaymentsError

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MakePaymentController @Inject()(paymentsService: PaymentsService,
                                      authorisedController: AuthorisedController,
                                      auditingService: AuditingService,
                                      mcc: MessagesControllerComponents,
                                      paymentsError: PaymentsError)
                                     (implicit ec: ExecutionContext,
                                      appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  def makePayment(amountInPence: Long,
                  taxPeriodMonth: Int,
                  taxPeriodYear: Int,
                  vatPeriodEnding: String,
                  chargeType: String,
                  dueDate: String,
                  chargeReference: String): Action[AnyContent] =
    authorisedController.authorisedAction { implicit request =>
      implicit user =>

        val paymentDetails = PaymentDetailsModelWithPeriod(
          taxType = "vat",
          taxReference = user.vrn,
          amountInPence = amountInPence,
          taxPeriodMonth = taxPeriodMonth,
          taxPeriodYear = taxPeriodYear,
          vatPeriodEnding = vatPeriodEnding,
          returnUrl = appConfig.paymentsReturnUrl,
          backUrl = appConfig.paymentsBackUrl,
          chargeType = ChargeType.apply(chargeType),
          dueDate = dueDate,
          chargeReference =
            if (!(chargeReference == "noCR") && !isReturn(ChargeType.apply(chargeType))) {
              Some(chargeReference)
            } else {
              None
            }
        )

        makePaymentHandoff(paymentDetails)
    }

  private def isReturn(chargeType: ChargeType): Boolean = chargeType match {
    case ReturnDebitCharge => true
    case _ => false
  }

  def makePaymentNoPeriod(amountInPence: Long,
                          chargeType: String,
                          dueDate: String,
                          chargeReference: String): Action[AnyContent] =
    authorisedController.authorisedAction { implicit request =>
      implicit user =>
        if(!isReturn(ChargeType.apply(chargeType)) && !(chargeReference == "noCR")) {
          val paymentDetails = PaymentDetailsModelNoPeriod(
            taxType = "vat",
            taxReference = user.vrn,
            amountInPence = amountInPence,
            returnUrl = appConfig.paymentsReturnUrl,
            backUrl = appConfig.paymentsBackUrl,
            chargeType = ChargeType.apply(chargeType),
            dueDate = dueDate,
            chargeReference = Some(chargeReference)
          )

          makePaymentHandoff(paymentDetails)

        } else if(chargeReference == "noCR") {
          logger.warn("[MakePaymentController][makePaymentNoPeriod] A VAT return payment needs a charge " +
            "reference, but this payment does not have one")
          Future.successful(InternalServerError(paymentsError()))
        } else {
          logger.warn("[MakePaymentController][makePaymentNoPeriod] A VAT return payment needs to have " +
            "period information")
          Future.successful(InternalServerError(paymentsError()))
        }
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
        logger.warn("[MakePaymentController][makePayment] error: " + error.toString)
        InternalServerError(paymentsError())
    }
  }

  def makeFullPaymentHandoff: Action[AnyContent] =
    authorisedController.authorisedAction { implicit request => user => {

        auditingService.audit(PayFullChargeAuditModel(user))
            Future.successful(Redirect(appConfig.unauthenticatedPaymentsUrl))

      }
    }
}
