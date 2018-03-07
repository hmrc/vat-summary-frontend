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

import javax.inject.{Inject, Singleton}

import config.AppConfig
import forms.MakePaymentForm
import models.payments.PaymentDetailsModel
import play.api.Logger
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import services.EnrolmentsAuthService

import scala.concurrent.Future

@Singleton
class MakePaymentController @Inject()(val messagesApi: MessagesApi,
                                      val enrolmentsAuthService: EnrolmentsAuthService,
                                      implicit val appConfig: AppConfig)
  extends AuthorisedController with I18nSupport {

  def makePayment(): Action[AnyContent] = authorisedAction { implicit request =>
    user =>

      def payment(paymentData: PaymentDetailsModel, vrn: String) = Json.toJson[PaymentDetailsModel](
        paymentData.copy(
          taxType = "mtdfb-vat",
          taxReference = vrn,
          returnUrl = appConfig.paymentsReturnUrl,
          taxPeriodYear = paymentData.taxPeriodYear.takeRight(2)))

      MakePaymentForm.form.bindFromRequest().fold(
        errors => {
          Logger.warn("[MakePaymentsController].[makePayment] invalid payment data")
          Future.successful(InternalServerError(views.html.errors.standardError(
            appConfig, Messages("paymentHandOffErrorHeading"),
            Messages("paymentHandOffErrorHeading"),
            Messages("paymentHandOffErrorMessage"))))
        },
        paymentDetail => {
          Future.successful(Redirect(appConfig.paymentsServiceUrl).addingToSession(
            "payment-data" -> payment(paymentDetail, user.vrn).toString())
          )
        }
      )
  }
}
