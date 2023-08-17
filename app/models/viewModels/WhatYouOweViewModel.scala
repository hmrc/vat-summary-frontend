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

import utils.LocalDateHelper

import java.time.LocalDate

case class WhatYouOweViewModel(totalAmount: BigDecimal,
                               charges: Seq[ChargeDetailsViewModel],
                               mandationStatus: String,
                               containsOverduePayments: Boolean,
                               breathingSpace: Boolean) extends LocalDateHelper {

  val earliestDueDate: Option[LocalDate] = {
    val dueDates: Seq[LocalDate] = charges.collect {
      case model: ChargeDetailsViewModelWithDueDate => model.dueDate
    }
    
    if(dueDates.nonEmpty) {
      implicit val localDateOrdering: Ordering[LocalDate] = _ compareTo _
      Some(dueDates.minBy(x => x))
    } else {
      None
    }
  }

  val earliestDueDateFormatted: Option[String] = earliestDueDate.map(_.paymentDetailsFormat)
}
