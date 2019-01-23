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
import views.templates.formatters.dates.DisplayDateRangeHelper.displayDateRange


sealed trait OpenPaymentsModel {
  val chargeType: ChargeType
  val amount: BigDecimal
  val due: LocalDate
  val overdue: Boolean
  val periodKey: String

  //noinspection ScalaStyle
  def whatYouOweDescription(implicit messages: Messages): String =  chargeType match {
    case OADebitCharge => messages("openPayments.officersAssessment")
    case OADefaultInterestCharge => messages("openPayments.oaDefaultInterest")
    case OAFurtherInterestCharge => messages("openPayments.oaFurtherInterest")
    case OACharge => messages("openPayments.vatOfficersAssessment")
    case BnpRegPost2010Charge => messages("openPayments.vatBnpRegPost2010")
    case FtnMatPre2010Charge => messages("openPayments.vatFtnMatPre2010")
    case FtnMatPost2010Charge => messages("openPayments.vatFtnMatPost2010")
    case MiscPenaltyCharge => messages("openPayments.vatMiscPenalty")
    case FtnEachPartnerCharge => messages("openPayments.vatFtnEachpartner")
    case MpPre2009Charge => messages("openPayments.vatMpPre2009")
    case MpRepeatedPre2009Charge => messages("openPayments.vatMpRepeatedPre2009")
    case CivilEvasionPenaltyCharge => messages("openPayments.vatCivilEvasionPenalty")
    case WrongDoingPenaltyCharge => messages("openPayments.vatWrongDoingPenalty")
    case FailureToNotifyRCSLCharge => messages("openPayments.vatFailureToNotifyRCSL")
    case FailureToSubmitRCSLCharge => messages("openPayments.vatFailureToSubmitRCSL")
    case VatInaccuraciesInECSalesCharge => messages("openPayments.vatInaccuraciesECSales")
    case VatECDefaultInterestCharge => messages("openPayments.VatECDefaultInterest")
    case VatECFurtherInterestCharge => messages("openPayments.VatECFurtherInterest")
    case VatSecurityDepositRequestCharge => messages("openPayments.VatSecurityDepositRequest")
    case VatProtectiveAssessmentCharge => messages("openPayments.VatProtectiveAssessment")
    case VatPADefaultInterestCharge => messages("openPayments.VatPADefaultInterest")
    case VatFailureToSubmitECSalesCharge => messages("openPayments.vatFailureToSubmitECSales")
    case VatPaFurtherInterestCharge => messages("openPayments.vatPaFurtherInterest")

    case _ => throw new IllegalArgumentException("Invalid Charge Type")
  }

  def makePaymentRedirect: String
}

object OpenPaymentsModel {

  def apply(chargeType: ChargeType,
            amount: BigDecimal,
            due: LocalDate,
            start: LocalDate,
            end: LocalDate,
            periodKey: String,
            overdue: Boolean): OpenPaymentsModel = OpenPaymentsModelWithPeriod(chargeType, amount, due, start, end, periodKey, overdue)

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

case class OpenPaymentsModelWithPeriod(chargeType: ChargeType,
                                       amount: BigDecimal,
                                       due: LocalDate,
                                       start: LocalDate,
                                       end: LocalDate,
                                       periodKey: String,
                                       overdue: Boolean = false) extends OpenPaymentsModel {

  //noinspection ScalaStyle
  override def whatYouOweDescription(implicit messages: Messages): String = chargeType match {
    case ReturnDebitCharge => messages("openPayments.vatReturn", displayDateRange(start, end))
    case DefaultSurcharge => messages("openPayments.surcharge", displayDateRange(start, end))
    case CentralAssessmentCharge => s"${
      messages("openPayments.centralAssessment", displayDateRange(start, end)).trim}${
      messages("openPayments.centralAssessmentSubmit")
    }"
    case ErrorCorrectionDebitCharge => messages("openPayments.errorCorrection", displayDateRange(start, end)).trim
    case AAInterestCharge => messages("openPayments.AADefaultInterestDescription",displayDateRange(start, end))
    case BnpRegPre2010Charge => messages("openPayments.vatBNPofRegPre2010", displayDateRange(start, end))
    case AAFurtherInterestCharge => messages("openPayments.vatAAFurtherInterest", displayDateRange(start, end))
    case AACharge => messages("openPayments.vatAdditionalAssessment", displayDateRange(start, end))
    case VatOAInaccuraciesFrom2009 => messages("openPayments.vatOAInaccuraciesFrom2009", displayDateRange(start, end))
    case VatInaccuraciesInECSalesCharge=> messages("openPayments.vatInaccuraciesECSales",displayDateRange(start, end))
    case VatFailureToSubmitECSalesCharge=> messages("openPayments.vatFailureToSubmitECSales",displayDateRange(start, end))
    case InaccuraciesAssessmentsPenCharge => messages("openPayments.vatInaccuraciesAssessmentsPen", displayDateRange(start, end))
    case InaccuraciesReturnReplacedCharge => messages("openPayments.vatInaccuraciesReturnReplaced", displayDateRange(start, end))
    case CarterPenaltyCharge => messages("openPayments.vatCarterPenalty", displayDateRange(start, end))
    case _ => super.whatYouOweDescription
  }

  override def makePaymentRedirect: String = controllers.routes.MakePaymentController.makePayment(
    amountInPence = (amount * 100).toLong,
    taxPeriodMonth = end.getMonthValue,
    taxPeriodYear = end.getYear,
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
      "start" -> model.start,
      "end" -> model.end,
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
