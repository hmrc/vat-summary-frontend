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

case class EstimatedInterestViewModel(periodFrom: LocalDate,
                                      periodTo: LocalDate,
                                      chargeType: String,
                                      interestRate: BigDecimal,
                                      numberOfDaysLate: Int,
                                      currentAmount: BigDecimal,
                                      amountReceived: BigDecimal,
                                      leftToPay: BigDecimal,
                                      isPenalty: Boolean) extends ChargeDetailsViewModel {

  override val outstandingAmount: BigDecimal = leftToPay

  def title(implicit messages: Messages): String = messages(PaymentMessageHelper.getChargeType(chargeType).title)

  def description(isAgent: Boolean)(implicit messages: Messages): String =
    PaymentMessageHelper.getCorrectDescription(
      PaymentMessageHelper.getChargeType(chargeType).principalUserDescription.getOrElse(""),
      PaymentMessageHelper.getChargeType(chargeType).agentDescription.getOrElse(""),
      Some(periodFrom),
      Some(periodTo),
      isAgent)
}

object EstimatedInterestViewModel {

  val form: Form[EstimatedInterestViewModel] = Form(mapping(
    "periodFrom" -> localDate,
    "periodTo" -> localDate,
    "chargeType" -> text,
    "interestRate" -> bigDecimal,
    "numberOfDaysLate" -> number,
    "currentAmount" -> bigDecimal,
    "amountReceived" -> bigDecimal,
    "leftToPay" -> bigDecimal,
    "isPenalty" -> boolean
  )(EstimatedInterestViewModel.apply)(EstimatedInterestViewModel.unapply))
}
