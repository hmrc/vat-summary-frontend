/*
 * Copyright 2022 HM Revenue & Customs
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
  val originalAmount: Option[BigDecimal]
  val chargeReference: Option[String]
  val clearedAmount: Option[BigDecimal]
  val periodKey: Option[String]
  val ddCollectionInProgress: Boolean
  val auditDetails: Map[String, String]
  val accruedInterestAmount: Option[BigDecimal]

  def isOverdue(now: LocalDate): Boolean = due.isBefore(now) && !ddCollectionInProgress

}

case class PaymentWithPeriod(chargeType: ChargeType,
                             periodFrom: LocalDate,
                             periodTo: LocalDate,
                             due: LocalDate,
                             outstandingAmount: BigDecimal,
                             periodKey: Option[String],
                             chargeReference: Option[String],
                             ddCollectionInProgress: Boolean,
                             accruedInterestAmount: Option[BigDecimal],
                             originalAmount: Option[BigDecimal] = None,
                             clearedAmount: Option[BigDecimal] = None) extends Payment {

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
                           periodKey: Option[String],
                           chargeReference: Option[String],
                           ddCollectionInProgress: Boolean,
                           accruedInterestAmount: Option[BigDecimal],
                           originalAmount: Option[BigDecimal] = None,
                           clearedAmount: Option[BigDecimal] = None) extends Payment {

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
                            periodKey: Option[String],
                            chargeReference: Option[String],
                            ddCollectionInProgress: Boolean,
                            accruedInterestAmount: Option[BigDecimal],
                            originalAmount: Option[BigDecimal],
                            clearedAmount: Option[BigDecimal]): Payment =
    (periodFrom, periodTo) match {
      case (Some(s), Some(e)) =>
        apply(chargeType, s, e, due, outstandingAmount, periodKey, chargeReference, ddCollectionInProgress, accruedInterestAmount, originalAmount, clearedAmount)
      case (None, None) =>
        apply(chargeType, due, outstandingAmount, periodKey, chargeReference, ddCollectionInProgress, accruedInterestAmount, originalAmount, clearedAmount)
      case (s, e) =>
        throw new IllegalArgumentException(s"Partial taxPeriod was supplied: periodFrom: '$s', periodTo: '$e'")
  }

  def apply(chargeType: ChargeType,
            periodFrom: LocalDate,
            periodTo: LocalDate,
            due: LocalDate,
            outstandingAmount: BigDecimal,
            periodKey: Option[String],
            chargeReference: Option[String],
            ddCollectionInProgress: Boolean,
            accruedInterestAmount: Option[BigDecimal],
            originalAmount: Option[BigDecimal],
            clearedAmount: Option[BigDecimal]): PaymentWithPeriod =
    PaymentWithPeriod(
      chargeType,
      periodFrom,
      periodTo,
      due,
      outstandingAmount,
      periodKey,
      chargeReference,
      ddCollectionInProgress,
      accruedInterestAmount,
      originalAmount,
      clearedAmount
    )

  def apply(chargeType: ChargeType,
            periodFrom: LocalDate,
            periodTo: LocalDate,
            due: LocalDate,
            outstandingAmount: BigDecimal,
            periodKey: Option[String],
            chargeReference: Option[String],
            ddCollectionInProgress: Boolean,
            accruedInterestAmount: Option[BigDecimal]): PaymentWithPeriod =
    PaymentWithPeriod(
      chargeType,
      periodFrom,
      periodTo,
      due,
      outstandingAmount,
      periodKey,
      chargeReference,
      ddCollectionInProgress,
      accruedInterestAmount
    )

  def apply(chargeType: ChargeType,
            due: LocalDate,
            outstandingAmount: BigDecimal,
            periodKey: Option[String],
            chargeReference: Option[String],
            ddCollectionInProgress: Boolean,
            originalAmount: Option[BigDecimal],
            clearedAmount: Option[BigDecimal],
            accruedInterestAmount: Option[BigDecimal]): PaymentNoPeriod =
    PaymentNoPeriod(
      chargeType,
      due,
      outstandingAmount,
      periodKey,
      chargeReference,
      ddCollectionInProgress,
      originalAmount,
      clearedAmount,
      accruedInterestAmount
    )

  def apply(chargeType: ChargeType,
            due: LocalDate,
            outstandingAmount: BigDecimal,
            periodKey: Option[String],
            chargeReference: Option[String],
            ddCollectionInProgress: Boolean,
            accruedInterestAmount: Option[BigDecimal]): PaymentNoPeriod =
    PaymentNoPeriod(
      chargeType,
      due,
      outstandingAmount,
      periodKey,
      chargeReference,
      ddCollectionInProgress,
      accruedInterestAmount
    )

  implicit val paymentReads: Reads[Payment] = (
    (JsPath \ "chargeType").read[ChargeType] and
    (JsPath \ "taxPeriodFrom").readNullable[LocalDate] and
    (JsPath \ "taxPeriodTo").readNullable[LocalDate] and
    (JsPath \ "items")(0).\("dueDate").read[LocalDate] and
    (JsPath \ "outstandingAmount").read[BigDecimal] and
    (JsPath \ "periodKey").readNullable[String] and
    (JsPath \ "chargeReference").readNullable[String] and
    (JsPath \ "items")(0).\("DDcollectionInProgress").read[Boolean].or(Reads.pure(false)) and
    (JsPath \ "accruedInterestAmount").readNullable[BigDecimal] and
    (JsPath \ "originalAmount").readNullable[BigDecimal] and
    (JsPath \ "clearedAmount").readNullable[BigDecimal]

  )(Payment.createPayment _)

}
