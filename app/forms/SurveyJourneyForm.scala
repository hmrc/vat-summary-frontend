/*
 * Copyright 2018 HM Revenue & Customs
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

import models.SurveyJourneyModel
import play.api.data.Form
import play.api.data.Forms._

object SurveyJourneyForm {
  val form: Form[SurveyJourneyModel] = Form(
    mapping(
      "anyApplicable" -> optional(text),
      "choice1" -> optional(boolean),
      "choice2" -> optional(boolean),
      "choice3" -> optional(boolean),
      "choice4" -> optional(boolean),
      "choice5" -> optional(boolean),
      "choice6" -> optional(boolean)
    )(SurveyJourneyModel.apply)(SurveyJourneyModel.unapply)
  )
}
