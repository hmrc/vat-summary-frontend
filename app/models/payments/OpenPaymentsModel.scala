/*
 * Copyright 2019 HM Revenue & Customs
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

import play.api.i18n.Messages
import play.api.libs.json.{Json, Writes}


sealed trait OpenPaymentsModel {
  val chargeType: ChargeType
  val amount: BigDecimal
  val due: LocalDate
  val overdue: Boolean
  val periodKey: String
  def makePaymentRedirect: String
}

object OpenPaymentsModel {

  def apply(chargeType: ChargeType,
            amount: BigDecimal,
            due: LocalDate,
            periodFrom: LocalDate,
            periodTo: LocalDate,
            periodKey: String,
            overdue: Boolean): OpenPaymentsModel = OpenPaymentsModelWithPeriod(chargeType, amount, due, periodFrom, periodTo, periodKey, overdue)

  def apply(chargeType: ChargeType,
            amount: BigDecimal,
            due: LocalDate,
            periodKey: String,
            overdue: Boolean): OpenPaymentsModel = OpenPaymentsModelNoPeriod(chargeType, amount, due, periodKey, overdue)

  def apply(payment: Payment, overdue: Boolean)(implicit messages: Messages): OpenPaymentsModel = payment match {
    case payment: PaymentWithPeriod => OpenPaymentsModelWithPeriod(
      payment.chargeType,
      payment.outstandingAmount,
      payment.due,
      payment.periodFrom,
      payment.periodTo,
      payment.periodKey,
      overdue
    )
    case payment: PaymentNoPeriod => OpenPaymentsModelNoPeriod(
      payment.chargeType,
      payment.outstandingAmount,
      payment.due,
      payment.periodKey,
      overdue
    )
  }

  implicit val writes: Writes[OpenPaymentsModel] = Writes {
    case model: OpenPaymentsModelWithPeriod => Json.toJson(model)(OpenPaymentsModelWithPeriod.writes)
    case model: OpenPaymentsModelNoPeriod => Json.toJson(model)(OpenPaymentsModelNoPeriod.writes)
  }
}

case class OpenPaymentsModelWithPeriod(chargeType: ChargeType,
                                       amount: BigDecimal,
                                       due: LocalDate,
                                       periodFrom: LocalDate,
                                       periodTo: LocalDate,
                                       periodKey: String,
                                       overdue: Boolean = false) extends OpenPaymentsModel {

  override def makePaymentRedirect: String = controllers.routes.MakePaymentController.makePayment(
    amountInPence = (amount * 100).toLong,
    taxPeriodMonth = periodTo.getMonthValue,
    taxPeriodYear = periodTo.getYear,
    chargeType.value,
    dueDate = due.toString
  ).url

}
object OpenPaymentsModelWithPeriod {
  implicit val writes: Writes[OpenPaymentsModelWithPeriod] = Writes { model =>
    Json.obj(
      "paymentType" -> model.chargeType,
      "amount" -> model.amount,
      "due" -> model.due,
      "periodFrom" -> model.periodFrom,
      "periodTo" -> model.periodTo,
      "periodKey" -> model.periodKey,
      "overdue" -> model.overdue
    )
  }
}

case class OpenPaymentsModelNoPeriod(chargeType: ChargeType,
                                     amount: BigDecimal,
                                     due: LocalDate,
                                     periodKey: String,
                                     overdue: Boolean = false) extends OpenPaymentsModel {

  override def makePaymentRedirect: String = controllers.routes.MakePaymentController.makePaymentNoPeriod(
    amountInPence = (amount * 100).toLong,
    chargeType.value,
    dueDate = due.toString
  ).url

}
object OpenPaymentsModelNoPeriod {
  implicit val writes: Writes[OpenPaymentsModelNoPeriod] = Writes { model =>
    Json.obj(
      "paymentType" -> model.chargeType,
      "amount" -> model.amount,
      "due" -> model.due,
      "periodKey" -> model.periodKey,
      "overdue" -> model.overdue
    )
  }
}
