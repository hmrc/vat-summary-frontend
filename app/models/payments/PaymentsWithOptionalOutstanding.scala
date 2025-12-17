/*
 * Copyright 2025 HM Revenue & Customs
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
import play.api.libs.json.Reads._
import play.api.libs.json._

import java.time.LocalDate

case class PaymentWithOptionalOutstanding(
  chargeType: ChargeType,
  due: LocalDate,
  outstandingAmount: Option[BigDecimal],
  periodKey: Option[String],
  chargeReference: Option[String],
  ddCollectionInProgress: Boolean,
  accruingInterestAmount: Option[BigDecimal],
  accruingPenaltyAmount: Option[BigDecimal],
  penaltyType: Option[String],
  originalAmount: Option[BigDecimal],
  clearedAmount: Option[BigDecimal]
)

case class PaymentsWithOptionalOutstanding(financialTransactions: Seq[PaymentWithOptionalOutstanding])

object PaymentWithOptionalOutstanding {

  implicit val paymentReads: Reads[PaymentWithOptionalOutstanding] = (
    (JsPath \ "chargeType").read[ChargeType] and
      (JsPath \ "items")(0).\("dueDate").read[LocalDate] and
      (JsPath \ "outstandingAmount").readNullable[BigDecimal] and
      (JsPath \ "periodKey").readNullable[String] and
      (JsPath \ "chargeReference").readNullable[String] and
      (JsPath \ "items")(0).\("DDcollectionInProgress").read[Boolean].or(Reads.pure(false)) and
      (JsPath \ "accruingInterestAmount").readNullable[BigDecimal] and
      (JsPath \ "accruingPenaltyAmount").readNullable[BigDecimal] and
      (JsPath \ "penaltyType").readNullable[String] and
      (JsPath \ "originalAmount").readNullable[BigDecimal] and
      (JsPath \ "clearedAmount").readNullable[BigDecimal]
    )(PaymentWithOptionalOutstanding.apply _)
}

object PaymentsWithOptionalOutstanding {
  implicit val reads: Reads[PaymentsWithOptionalOutstanding] = Json.reads[PaymentsWithOptionalOutstanding]
}
