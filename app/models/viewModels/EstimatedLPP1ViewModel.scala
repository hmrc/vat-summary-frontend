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

import play.api.libs.json.{Json, OFormat}
import java.time.LocalDate

import play.api.i18n.Messages
import views.templates.payments.PaymentMessageHelper

case class EstimatedLPP1ViewModel(part1Days: String,
                                  part2Days: String,
                                  part1PenaltyRate: BigDecimal,
                                  part2PenaltyRate: BigDecimal,
                                  part1UnpaidVAT: BigDecimal,
                                  penaltyAmount: BigDecimal,
                                  periodFrom: LocalDate,
                                  periodTo: LocalDate,
                                  chargeType: String) extends ChargeDetailsViewModel {

  override val outstandingAmount: BigDecimal = penaltyAmount

  def title(implicit messages: Messages): String = messages(PaymentMessageHelper.getChargeType(chargeType).title)

}

object EstimatedLPP1ViewModel {
  implicit val format: OFormat[EstimatedLPP1ViewModel] = Json.format[EstimatedLPP1ViewModel]
}
