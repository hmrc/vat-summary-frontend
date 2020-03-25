/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.LocalDate

import models.obligations.Obligation
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

sealed trait Payment extends Obligation {
  val chargeType: ChargeType
  val due: LocalDate
  val outstandingAmount: BigDecimal
  val periodKey: String

  val auditDetails: Map[String, String]
}

case class PaymentWithPeriod(chargeType: ChargeType,
                             periodFrom: LocalDate,
                             periodTo: LocalDate,
                             due: LocalDate,
                             outstandingAmount: BigDecimal,
                             periodKey: String) extends Payment {

  val auditDetails: Map[String, String] = Map(
    "paymentOutstanding" -> (if(outstandingAmount > 0) "yes" else "no"),
    "paymentDueBy" -> due.toString,
    "paymentPeriodFrom" -> periodFrom.toString,
    "paymentPeriodTo" -> periodTo.toString
  )

}

case class PaymentNoPeriod(chargeType: ChargeType,
                           due: LocalDate,
                           outstandingAmount: BigDecimal,
                           periodKey: String) extends Payment {

  val auditDetails: Map[String, String] = Map(
    "paymentOutstanding" -> (if(outstandingAmount > 0) "yes" else "no"),
    "paymentDueBy" -> due.toString
  )

}

object Payment {

  private def createPayment(chargeType: ChargeType,
            periodFrom: Option[LocalDate],
            periodTo: Option[LocalDate],
            due: LocalDate,
            outstandingAmount: BigDecimal,
            periodKey: Option[String]): Payment = (periodFrom, periodTo) match {
    case (Some(s), Some(e)) => apply(chargeType, s, e, due, outstandingAmount, periodKey)
    case (None, None) => apply(chargeType, due, outstandingAmount, periodKey)
    case (s, e) => throw new IllegalArgumentException(s"Partial taxPeriod was supplied: periodFrom: '$s', periodTo: '$e'")
  }

  def apply(chargeType: ChargeType,
            periodFrom: LocalDate,
            periodTo: LocalDate,
            due: LocalDate,
            outstandingAmount: BigDecimal,
            periodKey: Option[String]): PaymentWithPeriod =
    PaymentWithPeriod(chargeType, periodFrom, periodTo, due, outstandingAmount, periodKey.getOrElse("0000"))

  def apply(chargeType: ChargeType,
            due: LocalDate,
            outstandingAmount: BigDecimal,
            periodKey: Option[String]): PaymentNoPeriod =
    PaymentNoPeriod(chargeType, due, outstandingAmount, periodKey.getOrElse("0000"))

  implicit val paymentReads: Reads[Payment] = (
    (JsPath \ "chargeType").read[ChargeType] and
      (JsPath \ "taxPeriodFrom").readNullable[LocalDate] and
      (JsPath \ "taxPeriodTo").readNullable[LocalDate] and
      (JsPath \ "items")(0).\("dueDate").read[LocalDate] and
      (JsPath \ "outstandingAmount").read[BigDecimal] and
      (JsPath \ "periodKey").readNullable[String]
    )(Payment.createPayment _)

}
