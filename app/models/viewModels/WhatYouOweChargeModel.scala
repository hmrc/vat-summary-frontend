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

case class WhatYouOweChargeModel(chargeDescription: String,
                                 chargeTitle: String,
                                 outstandingAmount: BigDecimal,
                                 originalAmount: BigDecimal,
                                 clearedAmount: Option[BigDecimal],
                                 dueDate: LocalDate,
                                 periodKey: Option[String],
                                 isOverdue: Boolean,
                                 chargeReference: Option[String],
                                 makePaymentRedirect: String,
                                 periodFrom: Option[LocalDate],
                                 periodTo: Option[LocalDate])

object WhatYouOweChargeModel {

  val form: Form[WhatYouOweChargeModel] = Form(mapping(
    "chargeDescription" -> text,
    "chargeTitle" -> text,
    "outstandingAmount" -> bigDecimal,
    "originalAmount" -> bigDecimal,
    "clearedAmount" -> optional(bigDecimal),
    "dueDate" -> localDate,
    "periodKey" -> optional(text),
    "isOverdue" -> boolean,
    "chargeReference" -> optional(text),
    "makePaymentRedirect" -> text,
    "periodFrom" -> optional(localDate),
    "periodTo" -> optional(localDate)
  )(WhatYouOweChargeModel.apply)(WhatYouOweChargeModel.unapply))
}