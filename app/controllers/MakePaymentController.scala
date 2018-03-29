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
import models.payments.PaymentDetailsModel
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent}
import services.EnrolmentsAuthService

import scala.concurrent.Future

@Singleton
class MakePaymentController @Inject()(val messagesApi: MessagesApi,
                                      val enrolmentsAuthService: EnrolmentsAuthService,
                                      implicit val appConfig: AppConfig)
  extends AuthorisedController with I18nSupport {


  def makePayment(amountInPence: Int, taxPeriodMonth: Int, taxPeriodYear: Int): Action[AnyContent] =
    authorisedAction { implicit request =>
      user =>
        def payment(penceAmount: Int, taxMonth: Int, taxYear: Int, vrn: String): JsValue = Json.toJson[PaymentDetailsModel](
          PaymentDetailsModel(
            taxType = "mtdfb-vat",
            taxReference = vrn,
            amountInPence = penceAmount.toString,
            taxPeriodMonth = taxMonth.formatted("%02d"),
            taxPeriodYear = taxYear.toString.takeRight(2),
            returnUrl = appConfig.paymentsReturnUrl
          )
        )

        Future.successful(Redirect(appConfig.paymentsServiceUrl).addingToSession(
          "payment-data" -> payment(amountInPence, taxPeriodMonth, taxPeriodYear, user.vrn).toString())
        )
    }
}