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

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class PaymentDetailsModel(taxType: String,
                               taxReference: String,
                               amountInPence: Int,
                               taxPeriodMonth: Int,
                               taxPeriodYear: Int,
                               returnUrl: String)

object PaymentDetailsModel {
  implicit val reads: Reads[PaymentDetailsModel] = (
    (JsPath \ "taxType").read[String] and
      (JsPath \ "reference").read[String] and
      (JsPath \ "amountInPence").read[Int] and
      (JsPath \ "extras" \ "vatPeriod" \ "month").read[Int] and
      (JsPath \ "extras" \ "vatPeriod" \ "year").read[Int] and
      (JsPath \ "returnUrl").read[String]
    ) (PaymentDetailsModel.apply _)

  implicit val writes = new Writes[PaymentDetailsModel] {
    def writes(paymentDetail: PaymentDetailsModel): JsObject = Json.obj(
      "taxType" -> paymentDetail.taxType,
      "reference" -> paymentDetail.taxReference,
      "amountInPence" -> paymentDetail.amountInPence,
      "extras" -> Json.obj(
        "vatPeriod" -> Json.obj(
          "month" -> paymentDetail.taxPeriodMonth,
          "year" -> paymentDetail.taxPeriodYear)),
      "returnUrl" -> paymentDetail.returnUrl
    )
  }
}

