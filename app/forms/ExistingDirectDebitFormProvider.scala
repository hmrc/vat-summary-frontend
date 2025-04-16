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

package forms

import forms.mappings.Mappings
import models.viewModels.{ExistingDDContinuePayment, ExistingDirectDebitFormModel}
import play.api.data.Forms._
import play.api.data.{Form, Forms}

import javax.inject.Inject

class ExistingDirectDebitFormProvider @Inject() extends FormErrorHelper with Mappings {

  def apply(): Form[ExistingDirectDebitFormModel] =
    Form(
      mapping(
        "dueDateOrUrl"            -> optional(nonEmptyText),
        "linkId"                  -> nonEmptyText,
        "directDebitMandateFound" -> Forms.boolean,
        "value"                   -> enumerable[ExistingDDContinuePayment]("existingDD.radio.required")
      )(ExistingDirectDebitFormModel.formApply)(ExistingDirectDebitFormModel.formUnapply))

}
