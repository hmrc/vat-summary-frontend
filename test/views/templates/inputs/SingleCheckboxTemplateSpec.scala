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

package views.templates.inputs

import forms.FeatureSwitchForm
import play.api.data.Field
import play.twirl.api.Html
import views.templates.TemplateBaseSpec

class SingleCheckboxTemplateSpec extends TemplateBaseSpec {

  "Rendering the singleCheckbox input" when {

    val fieldName = "fieldName"
    val label = "label"

    "field value is true" should {

      val value = "true"
      val field = Field(FeatureSwitchForm.form, fieldName, Seq(), None, Seq(), Some(value))

      val expectedMarkup = Html(
        s"""
           |<div class="multiple-choice">
           |    <input id="$fieldName" name="$fieldName" type="checkbox" value="true" checked>
           |    <label for="$fieldName">$label</label>
           |</div>
           |""".stripMargin
      )

      val markup = views.html.templates.inputs.singleCheckbox(field, label)

      "render the expected markup" in {
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }

    "field value is false" should {

      val value = "false"
      val field = Field(FeatureSwitchForm.form, fieldName, Seq(), None, Seq(), Some(value))

      val expectedMarkup = Html(
        s"""
           |<div class="multiple-choice">
           |    <input id="$fieldName" name="$fieldName" type="checkbox" value="true">
           |    <label for="$fieldName">$label</label>
           |</div>
           |""".stripMargin
      )

      val markup = views.html.templates.inputs.singleCheckbox(field, label)

      "render the expected markup" in {
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }

    "field value is not supplied" should {

      val field = Field(FeatureSwitchForm.form, fieldName, Seq(), None, Seq(), value = None)

      val expectedMarkup = Html(
        s"""
           |<div class="multiple-choice">
           |    <input id="$fieldName" name="$fieldName" type="checkbox" value="true">
           |    <label for="$fieldName">$label</label>
           |</div>
           |""".stripMargin
      )

      val markup = views.html.templates.inputs.singleCheckbox(field, label)

      "render the expected markup" in {
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }
  }
}
