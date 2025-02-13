/*
 * Copyright 2025 HM Revenue & Customs
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

import models.viewModels.helpers.{Enumerable, RadioOption, WithName}

sealed trait ExistingDDContinuePayment

object ExistingDDContinuePayment {

  case object Yes extends WithName("Yes") with ExistingDDContinuePayment
  case object No extends WithName("No") with ExistingDDContinuePayment

  val values: Set[ExistingDDContinuePayment] = Set(
    Yes,
    No
  )

  val options: Set[RadioOption] = values.map { value =>
    RadioOption("existingDDContinuePayment", value.toString)
  }

  implicit val enumerable: Enumerable[ExistingDDContinuePayment] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
