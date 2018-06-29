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

import javax.inject.Inject

import common.FinancialTransactionsConstants._
import models.payments.OpenPaymentsModel
import play.api.i18n.Messages
import views.html.templates.formatters.dates.displayDateRange

class WhatYouOweChargeHelper @Inject()(payment: OpenPaymentsModel, hasDirectDebit: Option[Boolean],
                                       implicit val messages: Messages) {

  val description: String = payment.paymentType match {
    case `vatReturnDebitCharge` =>
      s"${messages.apply("openPayments.forPeriod")} ${displayDateRange(payment.start, payment.end)}"
    case `officerAssessmentDebitCharge` => messages.apply("openPayments.officersAssessment")
  }

  val payLinkText: Option[String] = (payment.paymentType, hasDirectDebit) match {
    case (`vatReturnDebitCharge`, Some(true)) => None
    case (`vatReturnDebitCharge`, _) => Some(messages.apply("openPayments.makePayment"))
    case (`officerAssessmentDebitCharge`, _) => Some(messages.apply("openPayments.makePayment"))
  }

  val viewReturnEnabled: Boolean = payment.paymentType match {
    case `vatReturnDebitCharge` => true
    case `officerAssessmentDebitCharge` => false
  }
}
