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

package views.survey

import forms.SurveyJourneyForm
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class JourneyViewSpec extends ViewBaseSpec {
  object Selectors {
    val pageHeading = "h1"
    val yesNoLegend = "#yes-no legend:nth-of-type(1)"
    val yesRadio= "#anyApplicable-yes"
    val noRadio= "#anyApplicable-no"
    val choicesHeading= "h2"
    val choiceOne = "label[for=choice1]"
    val choiceTwo ="label[for=choice2]"
    val choiceThree = "label[for=choice3]"
    val choiceFour = "label[for=choice4]"
    val choiceFive = "label[for=choice5]"
    val choiceSix = "label[for=choice6]"
    val submitButton = "#continue-button"
  }

  "Rendering the journey view survey page" should {

    lazy val view = views.html.survey.journey(SurveyJourneyForm.form)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "About your journey"
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "About your journey"
    }

    "have the correct yes/no legend text" in {
      elementText(Selectors.yesNoLegend) shouldBe "Did you do X today?"
    }

    "render the correct yes radio button text" in {
      element(Selectors.yesRadio).attr("value") shouldBe "Yes"
    }

    "render the correct no radio button text" in {
      element(Selectors.noRadio).attr("value") shouldBe "No"
    }

    "have the correct checkbox choices heading text" in {
      elementText(Selectors.choicesHeading) shouldBe "Which of the following were applicable to your journey?"
    }

    "render the correct text for checkbox one" in {
      element(Selectors.choiceOne).text() shouldBe "choice 1"
    }


    "render the correct text for checkbox two" in {
      element(Selectors.choiceTwo).text() shouldBe "choice 2"
    }


    "render the correct text for checkbox three" in {
      element(Selectors.choiceThree).text() shouldBe "choice 3"
    }


    "render the correct text for checkbox four" in {
      element(Selectors.choiceFour).text() shouldBe "choice 4"
    }


    "render the correct text for checkbox five" in {
      element(Selectors.choiceFive).text() shouldBe "choice 5"
    }


    "render the correct text for checkbox six" in {
      element(Selectors.choiceSix).text() shouldBe "choice 6"
    }

    "render the correct text for submit button" in {
      element(Selectors.submitButton).text() shouldBe "Submit"
    }

    "have the correct type for submit button" in {
      element(Selectors.submitButton).attr("type") shouldBe "submit"
    }
  }
}
