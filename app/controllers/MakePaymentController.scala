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
import audit.models.{PayFullChargeAuditModel, PayGenericChargeAuditModel, PayVatReturnChargeAuditModel}
import config.AppConfig
import models.User
import models.payments._
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.{DateService, PaymentsService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.errors.PaymentsError

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MakePaymentController @Inject()(paymentsService: PaymentsService,
                                      authorisedController: AuthorisedController,
                                      auditingService: AuditingService,
                                      mcc: MessagesControllerComponents,
                                      paymentsError: PaymentsError
                                     )(implicit ec: ExecutionContext,
                                       appConfig: AppConfig,
                                       dateService: DateService)
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
        infoLog(s"[MakePaymentController][makePayment] user has clicked to make a payment. dueDate: $dueDate")
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
        if (!isReturn(ChargeType.apply(chargeType)) && !(chargeReference == "noCR")) {
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
          infoLog(s"[MakePaymentController][makePaymentNoPeriod] user has clicked to make a payment with no period. dueDate: $dueDate")

          makePaymentHandoff(paymentDetails)

        } else if (chargeReference == "noCR") {
          warnLog("[MakePaymentController][makePaymentNoPeriod] A VAT return payment needs a charge " +
            "reference, but this payment does not have one")
          Future.successful(InternalServerError(paymentsError()))
        } else {
          warnLog("[MakePaymentController][makePaymentNoPeriod] A VAT return payment needs to have " +
            "period information")
          Future.successful(InternalServerError(paymentsError()))
        }
    }

  private def makePaymentHandoff(paymentDetails: PaymentDetailsModel)(implicit request: Request[_],
                                                                      user: User,
                                                                      hc: HeaderCarrier): Future[Result] = {
    paymentsService.setupPaymentsJourney(paymentDetails).map {
      case Right(url) =>
        infoLog(s"[MakePaymentController][makePaymentHandoff] handing user off to make a payment, dueDate ${paymentDetails.dueDate}. Redirecting to $url")
        auditingService.audit(
          PayVatReturnChargeAuditModel(user, paymentDetails, url),
          request.uri
        )
        Redirect(url)
      case Left(error) =>
        warnLog("[MakePaymentController][makePayment] error: " + error.toString)
        InternalServerError(paymentsError())
    }
  }

  def makeGenericPayment(earliestDueDate: Option[String], linkId: String): Action[AnyContent] = authorisedController.authorisedAction {
    implicit request =>
      implicit user => {
        val paymentDetails = PaymentDetailsModelGeneric(earliestDueDate)
        paymentsService.setupPaymentsJourney(paymentDetails).map {
          case Right(url) =>
            infoLog(s"[MakePaymentController][makeGenericPayment] user clicked make a payment " +
              s"link/button: $linkId, dueDate: $earliestDueDate. Redirecting to $url")
            auditingService.audit(
              PayGenericChargeAuditModel(paymentDetails, url, linkId),
              request.uri
            )
            Redirect(url)
          case Left(error) =>
            errorLog("[MakePaymentController][makeGenericPayment] error: " + error.toString)
            InternalServerError(paymentsError())
        }
      }
  }

  //This is no longer used and can be deleted once DL-11220 has been shown to be stable in production
  def makeFullPaymentHandoff: Action[AnyContent] =
    authorisedController.authorisedAction { implicit request =>
      user => {
        auditingService.audit(PayFullChargeAuditModel(user),
          controllers.routes.MakePaymentController.makeFullPaymentHandoff.url)
        Future.successful(Redirect(appConfig.unauthenticatedPaymentsUrl))


      }
    }
}
