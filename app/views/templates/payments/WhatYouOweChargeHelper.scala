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

package views.templates.payments

import common.FinancialTransactionsConstants._
import javax.inject.Inject
import models.payments.{OpenPaymentsModel, OpenPaymentsModelWithPeriod}
import play.api.i18n.Messages
import views.templates.formatters.dates.DisplayDateRangeHelper.displayDateRange

class WhatYouOweChargeHelper @Inject()(payment: OpenPaymentsModel,
                                       hasDirectDebit: Option[Boolean],
                                       implicit val messages: Messages) {

  val description: String = payment.whatYouOweDescription

  val payLinkText: Option[String] = (payment.paymentType, hasDirectDebit) match {
    case (`vatReturnDebitCharge`, Some(true)) => None
    case (`vatCentralAssessment`, _) => Some(messages("openPayments.payEstimate"))
    case (_, _) => Some(messages("openPayments.makePayment"))
  }

  val viewReturnEnabled: Boolean = payment.paymentType match {
    case `vatReturnDebitCharge` | `errorCorrectionDebitCharge` => true
    case _ => false
  }

  val overdueContext: String = payment.paymentType match {
    case `vatReturnDebitCharge` => if (payment.overdue) messages("common.overdue") else ""
    case _ => if (payment.overdue) messages("common.isOverdue") else ","
  }

  val viewReturnContext: String = payment match {
    case payment: OpenPaymentsModelWithPeriod => payment.paymentType match {
      case `vatReturnDebitCharge` => messages("openPayments.vatReturn", displayDateRange(payment.start, payment.end)).trim
      case `errorCorrectionDebitCharge` => messages("openPayments.errorCorrectionReturnContext", displayDateRange(payment.start, payment.end)).trim
      case _ => ""
    }
    case _ => ""
  }

  val viewReturnGAEvent: String = payment match {
    case payment: OpenPaymentsModelWithPeriod => s"returns:view-return ${payment.start}-to-${payment.end}:open-payments"
    case _ => s"returns:view-return:open-payments"
  }
}
