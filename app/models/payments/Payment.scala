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

import java.time.LocalDate

import models.obligations.Obligation
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

sealed trait Payment extends Obligation {
  val chargeType: String
  val due: LocalDate
  val outstandingAmount: BigDecimal
  val periodKey: String
}

case class PaymentWithPeriod(chargeType: String,
                             start: LocalDate,
                             end: LocalDate,
                             due: LocalDate,
                             outstandingAmount: BigDecimal,
                             periodKey: String) extends Payment

case class PaymentNoPeriod(chargeType: String,
                           due: LocalDate,
                           outstandingAmount: BigDecimal,
                           periodKey: String) extends Payment

object Payment {

  private def createPayment(chargeType: String,
                            start: Option[LocalDate],
                            end: Option[LocalDate],
                            due: LocalDate,
                            outstandingAmount: BigDecimal,
                            periodKey: Option[String]): Payment ={
    (start, end) match {
      case (Some(s), Some(e)) => PaymentWithPeriod(chargeType, s, e, due, outstandingAmount, periodKey.getOrElse("0000"))
      case (None, None) => PaymentNoPeriod(chargeType, due, outstandingAmount, periodKey.getOrElse("0000"))
      case (s, e) => throw new RuntimeException(s"Partial taxPeriod was supplied: start: '$s', end: '$e'")
    }
  }

  implicit val paymentReads: Reads[Payment] = (
    (JsPath \ "chargeType").read[String] and
    (JsPath \ "taxPeriodFrom").readNullable[LocalDate] and
    (JsPath \ "taxPeriodTo").readNullable[LocalDate] and
    (JsPath \ "items")(0).\("dueDate").read[LocalDate] and
    (JsPath \ "outstandingAmount").read[BigDecimal] and
    (JsPath \ "periodKey").readNullable[String]
  )(Payment.createPayment _)

}
