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

import play.api.i18n.Messages
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate

case class EstimatedLPP2ViewModel(day: String,
                                  penaltyRate: BigDecimal,
                                  penaltyAmount: BigDecimal,
                                  periodFrom: LocalDate,
                                  periodTo: LocalDate,
                                  chargeType: String) extends ChargeDetailsViewModel {

  override val outstandingAmount: BigDecimal = penaltyAmount
  override def description(isAgent: Boolean)(implicit messages: Messages): String = "" //TODO: Add description
}

object EstimatedLPP2ViewModel {

  implicit val format: OFormat[EstimatedLPP2ViewModel] = Json.format[EstimatedLPP2ViewModel]

}
