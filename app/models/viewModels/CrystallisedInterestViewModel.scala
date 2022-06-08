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

package models.viewModels

import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import views.templates.payments.PaymentMessageHelper
import java.time.LocalDate

case class CrystallisedInterestViewModel(periodFrom: LocalDate,
                                         periodTo: LocalDate,
                                         chargeType: String,
                                         interestRate: BigDecimal,
                                         dueDate: LocalDate,
                                         interestAmount: BigDecimal,
                                         amountReceived: BigDecimal,
                                         leftToPay: BigDecimal,
                                         isOverdue: Boolean,
                                         chargeReference: String,
                                         isPenalty: Boolean) extends ChargeDetailsViewModel {

  override val outstandingAmount: BigDecimal = interestAmount

  val makePaymentRedirect: String = controllers.routes.MakePaymentController.makePayment(
    amountInPence = (interestAmount * 100).toLong,
    taxPeriodMonth = periodTo.getMonthValue,
    taxPeriodYear = periodTo.getYear,
    vatPeriodEnding = periodTo.toString,
    chargeType = chargeType,
    dueDate = dueDate.toString,
    chargeReference = chargeReference
  ).url

  def title(implicit messages: Messages): String = messages(PaymentMessageHelper.getChargeType(chargeType).title)

  def description(isAgent: Boolean)(implicit messages: Messages): String =
    PaymentMessageHelper.getCorrectDescription(
      PaymentMessageHelper.getChargeType(chargeType).principalUserDescription.getOrElse(""),
      PaymentMessageHelper.getChargeType(chargeType).agentDescription.getOrElse(""),
      Some(periodFrom),
      Some(periodTo),
      isAgent)
}

object CrystallisedInterestViewModel {

  val form: Form[CrystallisedInterestViewModel] = Form(mapping(
    "periodFrom" -> localDate,
    "periodTo" -> localDate,
    "chargeType" -> text,
    "interestRate" -> bigDecimal,
    "dueDate" -> localDate,
    "interestAmount" -> bigDecimal,
    "amountReceived" -> bigDecimal,
    "leftToPay" -> bigDecimal,
    "isOverdue" -> boolean,
    "chargeReference" -> text,
    "isPenalty" -> boolean
  )(CrystallisedInterestViewModel.apply)(CrystallisedInterestViewModel.unapply))
}
