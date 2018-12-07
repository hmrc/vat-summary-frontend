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
      s"${messages.apply("openPayments.vatReturn", {displayDateRange(payment.start, payment.end)})}"
    case `officerAssessmentDebitCharge` => messages.apply("openPayments.officersAssessment")
    case `vatDefaultSurcharge` =>
      s"${messages.apply("openPayments.surcharge", {displayDateRange(payment.start, payment.end)})}"
    case `vatCentralAssessment` =>
      s"${messages.apply("openPayments.centralAssessment",
        {displayDateRange(payment.start, payment.end)}).trim}${messages("openPayments.centralAssessmentSubmit")}"
    case `errorCorrectionDebitCharge` =>
      s"${messages.apply("openPayments.errorCorrection", {displayDateRange(payment.start, payment.end)}).trim}"
    case `vatAdditionalAssessmentFurtherInterest` =>
      s"${messages.apply("openPayments.vatAAFurtherInterest", {displayDateRange(payment.start, payment.end)}).trim}"
    case `vatAdditionalAssessment` =>
      s"${messages.apply("openPayments.vatAdditionalAssessment", {displayDateRange(payment.start, payment.end)}).trim}"
    case `vatAdditionalAssessmentInterest` =>
      s"${messages.apply("paymentsHistory.AADefaultInterestDescription",{displayDateRange(payment.start, payment.end)}).trim}"
    case `vatBNPofRegPre2010` =>
      s"${messages.apply("openPayments.vatBNPofRegPre2010", {displayDateRange(payment.start, payment.end)}).trim}"
    case `vatOfficersAssessment` =>
      s"${messages.apply("openPayments.vatOfficersAssessment")}"
    case `vatBnpRegPost2010` =>
      s"${messages.apply("openPayments.vatBnpRegPost2010")}"
    case `vatFtnMatPre2010` =>
      s"${messages.apply("openPayments.vatFtnMatPre2010")}"
    case `vatFtnMatPost2010` =>
      s"${messages.apply("openPayments.vatFtnMatPost2010")}"
  }


  val payLinkText: Option[String] = (payment.paymentType, hasDirectDebit) match {
    case (`vatReturnDebitCharge`, Some(true)) => None
    case (`vatReturnDebitCharge`, _) => Some(messages.apply("openPayments.makePayment"))
    case (`officerAssessmentDebitCharge`, _) => Some(messages.apply("openPayments.makePayment"))
    case (`vatDefaultSurcharge`, _) => Some(messages.apply("openPayments.makePayment"))
    case (`vatCentralAssessment`, _) => Some(messages.apply("openPayments.payEstimate"))
    case (`errorCorrectionDebitCharge`, _) => Some(messages.apply("openPayments.makePayment"))
    case (`vatAdditionalAssessmentFurtherInterest`, _) => Some(messages.apply("openPayments.makePayment"))
    case (`vatAdditionalAssessment`, _) => Some(messages.apply("openPayments.makePayment"))
    case (`vatAdditionalAssessmentInterest`, _) => Some(messages.apply("openPayments.makePayment"))
    case (`vatBNPofRegPre2010`, _) => Some(messages.apply("openPayments.makePayment"))
    case (`vatOfficersAssessment`, _) => Some(messages.apply("openPayments.makePayment"))
    case (`vatBnpRegPost2010`, _) => Some(messages.apply("openPayments.makePayment"))
    case (`vatFtnMatPre2010`, _) => Some(messages.apply("openPayments.makePayment"))
    case (`vatFtnMatPost2010`, _) => Some(messages.apply("openPayments.makePayment"))
  }

  val viewReturnEnabled: Boolean = payment.paymentType match {
    case `vatReturnDebitCharge` => true
    case `officerAssessmentDebitCharge` => false
    case `vatDefaultSurcharge` => false
    case `vatCentralAssessment` => false
    case `errorCorrectionDebitCharge` => true
    case `vatAdditionalAssessmentFurtherInterest` => false
    case `vatAdditionalAssessment` => false
    case `vatAdditionalAssessmentInterest` => false
    case `vatBNPofRegPre2010` => false
    case `vatOfficersAssessment` => false
    case `vatBnpRegPost2010` => false
    case `vatFtnMatPre2010` => false
    case `vatFtnMatPost2010` => false
  }

  val overdueContext: String = payment.paymentType match {
    case `vatReturnDebitCharge` => if(payment.overdue) messages.apply("common.overdue") else ""
    case `officerAssessmentDebitCharge` => if(payment.overdue) messages.apply("common.isOverdue") else ","
    case `vatDefaultSurcharge` => if(payment.overdue) messages.apply("common.isOverdue") else ","
    case `vatCentralAssessment` => if(payment.overdue) messages.apply("common.isOverdue") else ","
    case `errorCorrectionDebitCharge` => if(payment.overdue) messages.apply("common.isOverdue") else ","
    case `vatAdditionalAssessmentFurtherInterest` => if(payment.overdue) messages.apply("common.isOverdue") else ","
    case `vatAdditionalAssessment` => if(payment.overdue) messages.apply("common.isOverdue") else ","
    case `vatAdditionalAssessmentInterest` => if(payment.overdue) messages.apply("common.isOverdue") else ","
    case `vatBNPofRegPre2010` => if(payment.overdue) messages.apply("common.isOverdue") else ","
    case `vatOfficersAssessment` => if(payment.overdue) messages.apply("common.isOverdue") else ","
    case `vatBnpRegPost2010` => if(payment.overdue) messages.apply("common.isOverdue") else ","
    case `vatFtnMatPre2010` => if(payment.overdue) messages.apply("common.isOverdue") else ","
    case `vatFtnMatPost2010` => if(payment.overdue) messages.apply("common.isOverdue") else ","
  }

  val viewReturnContext: String = payment.paymentType match {
    case `vatReturnDebitCharge` =>
      messages.apply("openPayments.vatReturn", {displayDateRange(payment.start, payment.end)}).trim
    case `officerAssessmentDebitCharge` => ""
    case `vatDefaultSurcharge` => ""
    case `vatCentralAssessment` => ""
    case `errorCorrectionDebitCharge` =>
      messages.apply("openPayments.errorCorrectionReturnContext", {displayDateRange(payment.start, payment.end)}).trim
    case `vatAdditionalAssessmentFurtherInterest` => ""
    case `vatAdditionalAssessment` => ""
    case `vatAdditionalAssessmentInterest` => ""
    case `vatBNPofRegPre2010` => ""
    case `vatOfficersAssessment` => ""
    case `vatBnpRegPost2010` => ""
    case `vatFtnMatPre2010` => ""
    case `vatFtnMatPost2010` => ""
  }
}
