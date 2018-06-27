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

class WhatYouOweChargeHelper @Inject()(chargeType: String, payment: OpenPaymentsModel, implicit val messages: Messages) {

  val description: String = chargeType match {
    case `vatReturnDebitCharge` =>
      s"${messages.apply("openPayments.forPeriod")} ${displayDateRange(payment.start, payment.end)}"
    case _ => ""
  }

  val payLinkText: String = chargeType match {
    case `vatReturnDebitCharge` => messages.apply("openPayments.makePayment")
    case _ => ""
  }

  val viewReturnEnabled: Boolean = chargeType match {
    case `vatReturnDebitCharge` => true
    case _ => false
  }
}
