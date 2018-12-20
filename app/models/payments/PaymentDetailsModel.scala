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

import play.api.libs.json._

sealed trait PaymentDetailsModel {
  val taxType: String
  val taxReference: String
  val amountInPence: Long
  val returnUrl: String
  val backUrl: String
  val chargeType: ChargeType
  val dueDate: String

  val auditDetail: Map[String, String]
}

object PaymentDetailsModel {

  def apply(taxType: String,
            taxReference: String,
            amountInPence: Long,
            taxPeriodMonth: Int,
            taxPeriodYear: Int,
            returnUrl: String,
            backUrl: String,
            chargeType: ChargeType,
            dueDate: String): PaymentDetailsModel =
    PaymentDetailsModelWithPeriod(taxType,
    taxReference,
    amountInPence,
    taxPeriodMonth,
    taxPeriodYear,
    returnUrl,
    backUrl,
    chargeType,
    dueDate
    )

  def apply(taxType: String,
            taxReference: String,
            amountInPence: Long,
            returnUrl: String,
            backUrl: String,
            chargeType: ChargeType,
            dueDate: String): PaymentDetailsModel =
    PaymentDetailsModelNoPeriod(taxType,
      taxReference,
      amountInPence,
      returnUrl,
      backUrl,
      chargeType,
      dueDate
    )

  implicit val writes: Writes[PaymentDetailsModel] = Writes {
    case model: PaymentDetailsModelWithPeriod => PaymentDetailsModelWithPeriod.writes.writes(model)
    case model: PaymentDetailsModelNoPeriod => PaymentDetailsModelNoPeriod.writes.writes(model)
  }
}

case class PaymentDetailsModelWithPeriod(taxType: String,
                                         taxReference: String,
                                         amountInPence: Long,
                                         taxPeriodMonth: Int,
                                         taxPeriodYear: Int,
                                         returnUrl: String,
                                         backUrl: String,
                                         chargeType: ChargeType,
                                         dueDate: String) extends PaymentDetailsModel {

  val auditDetail = Map(
    "taxType" -> taxType,
    "taxReference" -> taxReference,
    "amountInPence" -> amountInPence.toString,
    "taxPeriodMonth" -> taxPeriodMonth.toString,
    "taxPeriodYear" -> taxPeriodYear.toString,
    "returnUrl" -> returnUrl,
    "backUrl" -> backUrl,
    "chargeType" -> chargeType.value,
    "dueDate" -> dueDate
  )

}

object PaymentDetailsModelWithPeriod {

  implicit val writes: Writes[PaymentDetailsModelWithPeriod] = Writes { paymentDetail =>
    Json.obj(
      "taxType" -> paymentDetail.taxType,
      "reference" -> paymentDetail.taxReference,
      "amountInPence" -> paymentDetail.amountInPence,
      "extras" -> Json.obj(
        "vatPeriod" -> Json.obj(
          "month" -> paymentDetail.taxPeriodMonth,
          "year" -> paymentDetail.taxPeriodYear
        ),
        "chargeType" -> paymentDetail.chargeType.value,
        "dueDate" -> paymentDetail.dueDate
      ),
      "returnUrl" -> paymentDetail.returnUrl,
      "backUrl" -> paymentDetail.backUrl
    )
  }
}

case class PaymentDetailsModelNoPeriod(taxType: String,
                                       taxReference: String,
                                       amountInPence: Long,
                                       returnUrl: String,
                                       backUrl: String,
                                       chargeType: ChargeType,
                                       dueDate: String) extends PaymentDetailsModel {

  val auditDetail = Map(
    "taxType" -> taxType,
    "taxReference" -> taxReference,
    "amountInPence" -> amountInPence.toString,
    "returnUrl" -> returnUrl,
    "backUrl" -> backUrl,
    "chargeType" -> chargeType.value,
    "dueDate" -> dueDate
  )

}

object PaymentDetailsModelNoPeriod {

  implicit val writes: Writes[PaymentDetailsModelNoPeriod] = Writes { paymentDetail =>
    Json.obj(
      "taxType" -> paymentDetail.taxType,
      "reference" -> paymentDetail.taxReference,
      "amountInPence" -> paymentDetail.amountInPence,
      "extras" -> Json.obj(
        "chargeType" -> paymentDetail.chargeType.value,
        "dueDate" -> paymentDetail.dueDate
      ),
      "returnUrl" -> paymentDetail.returnUrl,
      "backUrl" -> paymentDetail.backUrl
    )
  }
}
