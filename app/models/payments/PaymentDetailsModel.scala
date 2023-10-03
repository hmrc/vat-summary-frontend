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

package models.payments

import config.AppConfig
import helpers.JsonObjectSugar
import models.User
import play.api.libs.json._
import services.DateService
import utils.LocalDateHelper

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
            vatPeriodEnding: String,
            returnUrl: String,
            backUrl: String,
            chargeType: ChargeType,
            dueDate: String,
            chargeReference: Option[String]): PaymentDetailsModel =
    PaymentDetailsModelWithPeriod(taxType,
      taxReference,
      amountInPence,
      taxPeriodMonth,
      taxPeriodYear,
      vatPeriodEnding,
      returnUrl,
      backUrl,
      chargeType,
      dueDate,
      chargeReference
    )

  def apply(taxType: String,
            taxReference: String,
            amountInPence: Long,
            returnUrl: String,
            backUrl: String,
            chargeType: ChargeType,
            dueDate: String,
            chargeReference: Option[String]): PaymentDetailsModel =
    PaymentDetailsModelNoPeriod(taxType,
      taxReference,
      amountInPence,
      returnUrl,
      backUrl,
      chargeType,
      dueDate,
      chargeReference
    )

  implicit val writes: Writes[PaymentDetailsModel] = Writes {
    case model: PaymentDetailsModelWithPeriod => PaymentDetailsModelWithPeriod.writes.writes(model)
    case model: PaymentDetailsModelNoPeriod => PaymentDetailsModelNoPeriod.writes.writes(model)
    case model: PaymentDetailsModelGeneric => PaymentDetailsModelGeneric.writes.writes(model)
  }
}

case class PaymentDetailsModelWithPeriod(taxType: String,
                                         taxReference: String,
                                         amountInPence: Long,
                                         taxPeriodMonth: Int,
                                         taxPeriodYear: Int,
                                         vatPeriodEnding: String,
                                         returnUrl: String,
                                         backUrl: String,
                                         chargeType: ChargeType,
                                         dueDate: String,
                                         chargeReference: Option[String] = None) extends PaymentDetailsModel {

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

object PaymentDetailsModelWithPeriod extends JsonObjectSugar {

  implicit val writes: Writes[PaymentDetailsModelWithPeriod] = Writes { paymentDetail =>
    jsonObjNoNulls(
      "vrn" -> paymentDetail.taxReference,
      "chargeReference" -> paymentDetail.chargeReference,
      "amountInPence" -> paymentDetail.amountInPence,
      "dueDate" -> paymentDetail.dueDate,
      "vatPeriodEnding" -> paymentDetail.vatPeriodEnding,
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
                                       dueDate: String,
                                       chargeReference: Option[String] = None) extends PaymentDetailsModel {

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

object PaymentDetailsModelNoPeriod extends JsonObjectSugar {

  implicit val writes: Writes[PaymentDetailsModelNoPeriod] = Writes { paymentDetail =>
    jsonObjNoNulls(
      "vrn" -> paymentDetail.taxReference,
      "amountInPence" -> paymentDetail.amountInPence,
      "dueDate" -> paymentDetail.dueDate,
      "returnUrl" -> paymentDetail.returnUrl,
      "backUrl" -> paymentDetail.backUrl,
      "chargeReference" -> paymentDetail.chargeReference
    )
  }
}

case class PaymentDetailsModelGeneric(earliestDueDate: Option[String])
                                     (implicit user: User, appConfig: AppConfig, dateService: DateService) extends PaymentDetailsModel with LocalDateHelper {

  override val taxType: String = "vat"
  override val taxReference: String = user.vrn
  override val amountInPence: Long = 0
  override val returnUrl: String = appConfig.paymentsReturnUrl
  override val backUrl: String = appConfig.paymentsBackUrl
  override val chargeType: ChargeType = ChargeType.apply(PaymentOnAccount.toString)
  override val dueDate: String = earliestDueDate.fold(dateService.now().paymentDetailsFormat)(identity)

  val auditDetail: Map[String, String] = Map(
    "taxType" -> taxType,
    "taxReference" -> taxReference,
    "amountInPence" -> amountInPence.toString,
    "returnUrl" -> returnUrl,
    "backUrl" -> backUrl,
    "chargeType" -> chargeType.value,
    "dueDate" -> dueDate
  )
}

object PaymentDetailsModelGeneric extends JsonObjectSugar {

  implicit val writes: Writes[PaymentDetailsModelGeneric] = Writes { paymentDetail =>
    jsonObjNoNulls(
      "vrn" -> paymentDetail.taxReference,
      "amountInPence" -> paymentDetail.amountInPence,
      "returnUrl" -> paymentDetail.returnUrl,
      "backUrl" -> paymentDetail.backUrl,
      "chargeType" -> paymentDetail.chargeType.value,
      "dueDate" -> paymentDetail.dueDate
    )
  }
}