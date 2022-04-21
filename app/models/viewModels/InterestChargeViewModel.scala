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

import java.time.LocalDate

case class InterestChargeViewModel(periodFrom: LocalDate,
                                   periodTo: LocalDate,
                                   chargeTitle: String,
                                   interestRate: BigDecimal,
                                   numberOfDaysLate: Int,
                                   currentAmount: BigDecimal,
                                   amountReceived: BigDecimal,
                                   leftToPay: BigDecimal,
                                   isPenalty: Boolean)

object InterestChargeViewModel {

  val form: Form[InterestChargeViewModel] = Form(mapping(
    "periodFrom" -> localDate,
    "periodTo" -> localDate,
    "chargeTitle" -> text,
    "interestRate" -> bigDecimal,
    "numberOfDaysLate" -> number,
    "currentAmount" -> bigDecimal,
    "amountReceived" -> bigDecimal,
    "leftToPay" -> bigDecimal,
    "isPenalty" -> boolean
  )(InterestChargeViewModel.apply)(InterestChargeViewModel.unapply))
}
