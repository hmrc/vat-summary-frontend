/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.data.{Field, FormError}
import play.twirl.api.Html
import views.html.templates.inputs.CheckboxList
import views.templates.TemplateBaseSpec

class CheckboxListTemplateSpec extends TemplateBaseSpec {
  val fieldName: String = "fieldName"
  val question: String = "question"
  val choices: Seq[(String, String)] = Seq(("choice1", "value1"), ("choice2", "value2"), ("choice3", "value3"), ("choice4", "value4"))
  val subtext: Option[String] = Some("Select all that apply")

  val checkboxList: CheckboxList = injector.instanceOf[CheckboxList]

  def generateExpectedCheckboxMarkup(choice: (String, String), checked: Boolean = false): String = {
    val (displayText, value) = choice
    val index = choices.indexOf(choice)

    s"""
       |
       |<div class="multiple-choice">
       |  <input id="$fieldName-$index" name="$fieldName[$index]" type="checkbox" value="$value" ${if (checked) "checked" else ""}>
       |  <label for="$fieldName-$index">$displayText</label>
       |</div>
       |
     """.stripMargin
  }

  "Calling the checkbox helper" should {

    "render a list of checkbox options" in {
      val field: Field = Field(FeatureSwitchForm.form, fieldName, Seq(), None, Seq(), None)
      val expectedMarkup = Html(
        s"""
           |
           |<div class="form-group">
           |  <fieldset>
           |
           |    <legend>
           |      <h1 class="heading-medium">$question</h1>
           |      <span class="body-text">${subtext.get}</span>
           |    </legend>
           |
           |      ${generateExpectedCheckboxMarkup(("choice1", "value1"))}
           |      ${generateExpectedCheckboxMarkup(("choice2", "value2"))}
           |      ${generateExpectedCheckboxMarkup(("choice3", "value3"))}
           |      ${generateExpectedCheckboxMarkup(("choice4", "value4"))}
           |
           |  </fieldset>
           |</div>
           |
         """.stripMargin
      )

      val markup = checkboxList(field, question, choices, subtext)

      formatHtml(markup) shouldBe formatHtml(expectedMarkup)
    }
  }

  "Calling the checkbox helper with a few options checked" should {

    "render a list of checkbox options with a few options checked" in {
      val field: Field = Field(FeatureSwitchForm.form, fieldName, Seq(), None, Seq(), Some("choice4"))
      val expectedMarkup = Html(
        s"""
           |
           |<div class="form-group">
           |  <fieldset>
           |
           |    <legend>
           |      <h1 class="heading-medium">$question</h1>
           |    </legend>
           |
           |      ${generateExpectedCheckboxMarkup(("choice1", "value1"))}
           |      ${generateExpectedCheckboxMarkup(("choice2", "value2"), checked = true)}
           |      ${generateExpectedCheckboxMarkup(("choice3", "value3"))}
           |      ${generateExpectedCheckboxMarkup(("choice4", "value4"), checked = true)}
           |
           |  </fieldset>
           |</div>
           |
         """.stripMargin
      )

      val markup = checkboxList(field, question, choices, None, Seq("value2", "value4"))

      formatHtml(markup) shouldBe formatHtml(expectedMarkup)
    }
  }

  "Calling the checkbox helper with all options checked" should {

    "render a list of checkbox options with all of them checked" in {
      val field: Field = Field(FeatureSwitchForm.form, fieldName, Seq(), None, Seq(), None)
      val expectedMarkup = Html(
        s"""
           |
           |<div class="form-group">
           |  <fieldset>
           |
           |    <legend>
           |      <h1 class="heading-medium">$question</h1>
           |    </legend>
           |
           |      ${generateExpectedCheckboxMarkup(("choice1", "value1"), checked = true)}
           |      ${generateExpectedCheckboxMarkup(("choice2", "value2"), checked = true)}
           |      ${generateExpectedCheckboxMarkup(("choice3", "value3"), checked = true)}
           |      ${generateExpectedCheckboxMarkup(("choice4", "value4"), checked = true)}
           |
           |  </fieldset>
           |</div>
           |
         """.stripMargin
      )

      val markup = checkboxList(field, question, choices, None, Seq("value1", "value2", "value3", "value4"))

      formatHtml(markup) shouldBe formatHtml(expectedMarkup)
    }
  }

  "Calling the checkbox helper with an error" should {

    "render an error message" in {
      val field: Field = Field(FeatureSwitchForm.form, fieldName, Seq(), None,
        Seq(FormError("example", Seq("Please select at least one option"))), None)
      val expectedMarkup = Html(
        s"""
           |
           |<div class="form-group form-field--error">
           |  <fieldset>
           |
           |    <legend>
           |      <h1 class="heading-medium">$question</h1>
           |    </legend>
           |
           |    <span class="error-notification" role="tooltip">${field.errors.head.message}</span>
           |
           |    ${generateExpectedCheckboxMarkup(("choice1", "value1"))}
           |    ${generateExpectedCheckboxMarkup(("choice2", "value2"))}
           |    ${generateExpectedCheckboxMarkup(("choice3", "value3"))}
           |    ${generateExpectedCheckboxMarkup(("choice4", "value4"))}
           |
           |  </fieldset>
           |</div>
           |
         """.stripMargin
      )

      val markup = checkboxList(field, question, choices)

      formatHtml(markup) shouldBe formatHtml(expectedMarkup)
    }
  }
}
