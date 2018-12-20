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

import common.FinancialTransactionsConstants._
import play.api.i18n.Messages
import play.api.libs.json.{Json, Writes}
import views.templates.formatters.dates.DisplayDateRangeHelper.displayDateRange


sealed trait OpenPaymentsModel {
  val paymentType: String
  val amount: BigDecimal
  val due: LocalDate
  val overdue: Boolean
  val periodKey: String

  def makePaymentRedirect: String

  def whatYouOweDescription(implicit messages: Messages): String = paymentType match {
    case `officerAssessmentDebitCharge` => messages("openPayments.officersAssessment")
    case `officerAssessmentDefaultInterest` => messages("openPayments.oaDefaultInterest")
    case `vatOfficersAssessment` => messages("openPayments.vatOfficersAssessment")
    case `vatBnpRegPost2010` => messages("openPayments.vatBnpRegPost2010")
    case `vatFtnMatPre2010` => messages("openPayments.vatFtnMatPre2010")
    case `vatFtnMatPost2010` => messages("openPayments.vatFtnMatPost2010")
    case `vatMiscPenalty` => messages("openPayments.vatMiscPenalty")
    case `vatFtnEachpartner` => messages("openPayments.vatFtnEachpartner")
    case `vatMpPre2009` => messages("openPayments.vatMpPre2009")
    case `vatMpRepeatedPre2009` => messages("openPayments.vatMpRepeatedPre2009")
    case `vatCivilEvasionPenalty` => messages("openPayments.vatCivilEvasionPenalty")
  }
}

object OpenPaymentsModel {

  def apply(paymentType: String,
            amount: BigDecimal,
            due: LocalDate,
            start: LocalDate,
            end: LocalDate,
            periodKey: String,
            overdue: Boolean): OpenPaymentsModel = OpenPaymentsModelWithPeriod(paymentType, amount, due, start, end, periodKey, overdue)

  def apply(paymentType: String,
            amount: BigDecimal,
            due: LocalDate,
            periodKey: String,
            overdue: Boolean): OpenPaymentsModel = OpenPaymentsModelNoPeriod(paymentType, amount, due, periodKey, overdue)

  def apply(payment: Payment, overdue: Boolean)(implicit messages: Messages): OpenPaymentsModel = payment match {
    case payment: PaymentWithPeriod => OpenPaymentsModelWithPeriod(
      payment.chargeType,
      payment.outstandingAmount,
      payment.due,
      payment.start,
      payment.end,
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

case class OpenPaymentsModelWithPeriod(paymentType: String,
                                       amount: BigDecimal,
                                       due: LocalDate,
                                       start: LocalDate,
                                       end: LocalDate,
                                       periodKey: String,
                                       overdue: Boolean = false) extends OpenPaymentsModel {

  override def whatYouOweDescription(implicit messages: Messages): String = paymentType match {
    case `vatReturnDebitCharge` => messages("openPayments.vatReturn", displayDateRange(start, end))
    case `vatDefaultSurcharge` => messages("openPayments.surcharge", displayDateRange(start, end))
    case `vatCentralAssessment` => s"${
      messages("openPayments.centralAssessment", displayDateRange(start, end)).trim}${
      messages("openPayments.centralAssessmentSubmit")
    }"
    case `errorCorrectionDebitCharge` => messages("openPayments.errorCorrection", displayDateRange(start, end)).trim
    case `vatAdditionalAssessmentInterest` =>
      s"${messages.apply("openPayments.AADefaultInterestDescription",{displayDateRange(start, end)}).trim}"
    case `vatBNPofRegPre2010` =>
      s"${messages.apply("openPayments.vatBNPofRegPre2010", {displayDateRange(start, end)}).trim}"
    case `vatAdditionalAssessmentFurtherInterest` =>
      s"${messages.apply("openPayments.vatAAFurtherInterest", {displayDateRange(start, end)}).trim}"
    case `vatAdditionalAssessment` =>
      s"${messages.apply("openPayments.vatAdditionalAssessment", {displayDateRange(start, end)}).trim}"
    case _ => super.whatYouOweDescription
  }

  override def makePaymentRedirect: String = controllers.routes.MakePaymentController.makePayment(
    amountInPence = (amount * 100).toLong,
    taxPeriodMonth = end.getMonthValue,
    taxPeriodYear = end.getYear,
    paymentType,
    dueDate = due.toString
  ).url

}
object OpenPaymentsModelWithPeriod {
  implicit val writes: Writes[OpenPaymentsModelWithPeriod] = Json.writes[OpenPaymentsModelWithPeriod]
}

case class OpenPaymentsModelNoPeriod(paymentType: String,
                                     amount: BigDecimal,
                                     due: LocalDate,
                                     periodKey: String,
                                     overdue: Boolean = false) extends OpenPaymentsModel {

  override def makePaymentRedirect: String = controllers.routes.MakePaymentController.makePaymentNoPeriod(
    amountInPence = (amount * 100).toLong,
    paymentType,
    dueDate = due.toString
  ).url

}
object OpenPaymentsModelNoPeriod {
  implicit val writes: Writes[OpenPaymentsModelNoPeriod] = Json.writes[OpenPaymentsModelNoPeriod]
}