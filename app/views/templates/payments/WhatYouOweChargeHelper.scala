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

package views.templates.payments

import javax.inject.Inject
import models.payments._
import play.api.Logger
import play.api.i18n.Messages
import views.templates.formatters.dates.DisplayDateRangeHelper.displayDateRange

class WhatYouOweChargeHelper @Inject()(payment: OpenPaymentsModel,
                                       hasDirectDebit: Option[Boolean],
                                       implicit val messages: Messages) {

  private def paymentMessageHelper()(implicit lang: play.api.i18n.Lang): PaymentMessageHelper = PaymentMessageHelper.getChargeType(payment.chargeType.value)

  def description()(implicit lang: play.api.i18n.Lang): Option[String] = {
    (payment, paymentMessageHelper.description) match {
      case (payment: OpenPaymentsModelWithPeriod, Some(desc)) =>
        Some(PaymentMessageHelper.getFullDescription(desc, Some(payment.start), Some(payment.end), useShortDayFormat = false))
      case (_: OpenPaymentsModelNoPeriod, Some(desc)) =>
        val descriptionText = PaymentMessageHelper.getFullDescription(desc, None, None)
        if (descriptionText.contains("{0}")) {
          Logger.warn("[WhatYouOweChargeHelper][description] - " +
            s"No date period was found for ${payment.chargeType}. Omitting description.")
          None
        } else {
          Some(descriptionText)
        }
      case (_, _) => None
    }
  }

  val title: String = messages(paymentMessageHelper.title)

  val payLinkText: Option[String] = (payment.chargeType, hasDirectDebit) match {
    case (ReturnDebitCharge, Some(true)) => None
    case (CentralAssessmentCharge, _) => Some(messages("openPayments.payEstimate"))
    case (_, _) => Some(messages("openPayments.makePayment"))
  }

  val viewReturnEnabled: Boolean = payment.chargeType match {
    case ReturnDebitCharge | ErrorCorrectionDebitCharge | AAReturnDebitCharge => true
    case _ => false
  }

  val overdueContext: String = payment.chargeType match {
    case ReturnDebitCharge => if (payment.overdue) messages("common.overdue") else ""
    case _ => if (payment.overdue) messages("common.isOverdue") else ","
  }

  val viewReturnContext: String = payment match {
    case payment: OpenPaymentsModelWithPeriod => payment.chargeType match {
      case ReturnDebitCharge | AAReturnDebitCharge => messages("openPayments.vatReturn", displayDateRange(payment.start, payment.end)).trim
      case ErrorCorrectionDebitCharge => messages("openPayments.errorCorrectionReturnContext", displayDateRange(payment.start, payment.end)).trim
      case _ => ""
    }
    case _ => ""
  }

  val viewReturnGAEvent: String = payment match {
    case payment: OpenPaymentsModelWithPeriod => s"returns:view-return ${payment.start}-to-${payment.end}:open-payments"
    case _ => s"returns:view-return:open-payments"
  }
}
