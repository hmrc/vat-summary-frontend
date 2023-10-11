/*
 * Copyright 2023 HM Revenue & Customs
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

import play.api.i18n.Messages
import play.api.libs.json.{Json, OFormat}
import views.templates.payments.PaymentMessageHelper

import java.time.LocalDate

case class EstimatedInterestViewModel(periodFrom: LocalDate,
                                      periodTo: LocalDate,
                                      chargeType: String,
                                      interestAmount: BigDecimal,
                                      isPenalty: Boolean) extends EstimatedViewModel {

  // TODO*** STUB DATA VIEW ETC AFTER PARENT CHARGE TYPE IS COMPLETE 

  if (chargeType == "VAT Overpayments 1st LPP LPI") {
    println(Console.YELLOW)
    println("***********************************************")
    println("EstimatedInterestViewModel")
    println(s"${}")
    println("***********************************************")
    println(Console.RESET)
  }

  override val outstandingAmount: BigDecimal = interestAmount

  override def description(isAgent: Boolean)(implicit messages: Messages): String = PaymentMessageHelper.getCorrectDescription(
    PaymentMessageHelper.getChargeType(chargeType).principalUserDescription.getOrElse(""),
    PaymentMessageHelper.getChargeType(chargeType).agentDescription.getOrElse(""),
    Some(periodFrom),
    Some(periodTo),
    isAgent
  )
}

object EstimatedInterestViewModel {
  implicit val format: OFormat[EstimatedInterestViewModel] = Json.format[EstimatedInterestViewModel]
}
