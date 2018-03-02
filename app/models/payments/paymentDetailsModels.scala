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

package models.payments

import play.api.libs.json.{Format, Json, _}

case class PaymentDetailsModel(taxType: String,
                               taxReference: String,
                               amountInPence: String,
                               taxPeriodMonth: String,
                               taxPeriodYear: String,
                               returnUrl: String)

object PaymentDetailsModel {
  implicit val formatAAFormatWrites = Json.reads[PaymentDetailsModel]

  implicit val parentWrites = new Writes[PaymentDetailsModel] {
    def writes(paymentDetail: PaymentDetailsModel) = Json.obj(
      "taxType" -> paymentDetail.taxType,
      "taxReference" -> paymentDetail.taxReference,
      "amountInPence" -> paymentDetail.amountInPence,
      "taxPeriod" -> Json.obj(
        "month" -> paymentDetail.taxPeriodMonth,
        "year" -> paymentDetail.taxPeriodYear),
      "returnUrl" -> paymentDetail.returnUrl
    )
  }
}

case class PaymentDetailsErrorModel(code: Int, message: String)

object PaymentDetailsErrorModel {
  implicit val format: Format[PaymentDetailsErrorModel] = Json.format[PaymentDetailsErrorModel]
}
