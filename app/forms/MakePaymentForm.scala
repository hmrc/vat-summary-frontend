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

package forms
import models.payments.PaymentDetailsModel
import play.api.data.Form
import play.api.data.Forms._

object MakePaymentForm {
  val form: Form[PaymentDetailsModel] = Form(
    mapping(
      "taxType" -> default(text, "mtdfb-vat"),
      "taxReference" -> default(nonEmptyText, "/"),
      "amountInPence" -> nonEmptyText,
      "taxPeriodMonth" -> nonEmptyText(minLength = 2, maxLength = 2),
      "taxPeriodYear" -> nonEmptyText(maxLength = 4),
      "returnUrl" -> default(nonEmptyText, "/")
    )(PaymentDetailsModel.apply)(PaymentDetailsModel.unapply)
  )
}
