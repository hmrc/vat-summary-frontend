/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.i18n.Messages
import utils.LoggerUtil
import views.templates.formatters.dates.DisplayDateRangeHelper.displayDateRange

class WhatYouOweChargeHelper @Inject()(payment: OpenPaymentsModel,
                                       implicit val messages: Messages) extends LoggerUtil{

  private def paymentMessageHelper(): PaymentMessageHelper = PaymentMessageHelper.getChargeType(payment.chargeType.value)

  def description(): Option[String] = {
    (payment, paymentMessageHelper().description) match {
      case (payment: OpenPaymentsModelWithPeriod, Some(desc)) =>
        Some(PaymentMessageHelper.getFullDescription(desc, Some(payment.periodFrom), Some(payment.periodTo)))
      case (_: OpenPaymentsModelNoPeriod, Some(desc)) =>
        val descriptionText = PaymentMessageHelper.getFullDescription(desc, None, None)
        if (descriptionText.contains("{0}")) {
          logger.warn("[WhatYouOweChargeHelper][description] - " +
            s"No date period was found for ${payment.chargeType}. Omitting description.")
          None
        } else {
          Some(descriptionText)
        }
      case (_, _) => None
    }
  }

  val title: String = messages(paymentMessageHelper().title)

  val payLinkText: String = messages("openPayments.makePayment")

  val viewReturnEnabled: Boolean = payment.chargeType match {
    case ReturnDebitCharge | ErrorCorrectionDebitCharge |
         PaymentOnAccountReturnDebitCharge | AAReturnDebitCharge => true
    case _ => false
  }

  val viewReturnContext: String = payment match {
    case payment: OpenPaymentsModelWithPeriod => payment.chargeType match {
      case ReturnDebitCharge | AAReturnDebitCharge | PaymentOnAccountReturnDebitCharge =>
        messages("openPayments.vatReturn", displayDateRange(payment.periodFrom, payment.periodTo)).trim
      case ErrorCorrectionDebitCharge =>
        messages("openPayments.errorCorrectionReturnContext", displayDateRange(payment.periodFrom, payment.periodTo)).trim
      case _ => ""
    }
    case _ => ""
  }

}
