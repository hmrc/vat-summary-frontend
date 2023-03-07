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

package views.payments

import common.TestModels.{estimatedLPP2Model, estimatedLPP2ModelTTP}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.EstimatedLPP2View


class EstimatedLPP2ViewSpec extends ViewBaseSpec {

  val injectedView: EstimatedLPP2View = injector.instanceOf[EstimatedLPP2View]
  val whatYouOweLink: String = controllers.routes.WhatYouOweController.show.url

  object Selectors {
    val heading = "h1"
    val caption = ".govuk-caption-xl"
    val breadcrumb1 = "#breadcrumb-1"
    val breadcrumb2 = "#breadcrumb-2"
    val breadcrumb3 = "#breadcrumb-3"
    val backLink = ".govuk-back-link"
    val explanation1 = "#explanation-p1"
    val explanation2 = "#explanation-p2"
    val explanation2BreathingSpace = "#explanation-p2-breathing-space"
    val explanation3 = "#explanation-p3"
    val summaryListRowKey: Int => String = row => s".govuk-summary-list__row:nth-child($row) > dt"
    val summaryListRowValue: Int => String = row => s".govuk-summary-list__row:nth-child($row) > dd"
    val estimatesSubheading = "#estimates-subheading"
    val estimatesDescription = "#estimates"
    val penAppealsLink = "#pen-appeals-link"
    val wyoLink = "#wyo-link"
    val ttpInset = "#ttp-inset"
  }

  "Rendering the Estimated LPP2 Page for a principal user" when {

    "the user has no time to pay arrangement and no breathing space" should {

      lazy val view = injectedView(estimatedLPP2Model, Html(""))(request, messages, mockConfig, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "Second late payment penalty - Manage your VAT account - GOV.UK"
      }

      "have the correct page heading" in {
        elementText(Selectors.heading) shouldBe "1\u00a0January\u00a02020 to 2\u00a0February\u00a02020 Second late payment penalty"
      }

      "have a period caption" in {
        elementText(Selectors.caption) shouldBe "1\u00a0January\u00a02020 to 2\u00a0February\u00a02020"
      }

      "render breadcrumbs which" should {

        "have the text 'Business tax account'" in {
          elementText(Selectors.breadcrumb1) shouldBe "Business tax account"
        }
        "link to bta" in {
          element(Selectors.breadcrumb1).attr("href") shouldBe "bta-url"
        }
        "have the text 'Your VAT account'" in {
          elementText(Selectors.breadcrumb2) shouldBe "Your VAT account"
        }
        "link to VAT overview page" in {
          element(Selectors.breadcrumb2).attr("href") shouldBe
            controllers.routes.VatDetailsController.details.url
        }
        "have the text 'What You Owe'" in {
          elementText(Selectors.breadcrumb3) shouldBe "What you owe"
        }
        "link to the what you owe page" in {
          element(Selectors.breadcrumb3).attr("href") shouldBe
            whatYouOweLink
        }
      }

      "not render a back link" in {
        elementExtinct(Selectors.backLink)
      }

      "have the correct first explanation paragraph" in {
        elementText(Selectors.explanation1) shouldBe
          "This penalty applies from day 31, if any VAT remains unpaid."
      }

      "have the correct calculation explanation statement" in {
        elementText(Selectors.explanation2) shouldBe
          "The total builds up daily until you pay your VAT or set up a payment plan."
      }

      "have the correct calculation explanation" in {
        elementText(Selectors.explanation3) shouldBe
          "The calculation we use for each day is: (Penalty rate of 4.4% × unpaid VAT) ÷ days in a year"
      }

      "have the correct heading for the first row" in {
        elementText(Selectors.summaryListRowKey(1)) shouldBe "Penalty amount (estimate)"
      }

      "display the penalty amount" in {
        elementText(Selectors.summaryListRowValue(1)) shouldBe s"£${estimatedLPP2Model.penaltyAmount}"
      }

      "have the correct heading for the second row" in {
        elementText(Selectors.summaryListRowKey(2)) shouldBe "Amount received"
      }

      "display the amount received" in {
        elementText(Selectors.summaryListRowValue(2)) shouldBe "£0.00"
      }

      "have the correct heading for the third row" in {
        elementText(Selectors.summaryListRowKey(3)) shouldBe "Left to pay"
      }

      "display the amount left to pay" in {
        elementText(Selectors.summaryListRowValue(3)) shouldBe s"£${estimatedLPP2Model.penaltyAmount}"
      }

      "have the correct subheading" in {
        elementText(Selectors.estimatesSubheading) shouldBe "Estimates"
      }

      "have the estimate description" in {
        elementText(Selectors.estimatesDescription) shouldBe
          "Penalties and interest will show as estimates until you pay the charge they relate to."
      }

      "have a link to VAT penalties and appeals" which {

        "has the correct link text" in {
          elementText(Selectors.penAppealsLink) shouldBe "View your VAT penalties and appeals"
        }

        "has the correct href" in {
          element(Selectors.penAppealsLink).attr("href") shouldBe mockConfig.penaltiesFrontendUrl
        }
      }

      "have a link to the what you owe page" which {

        "has the correct link text" in {
          elementText(Selectors.wyoLink) shouldBe "Return to what you owe"
        }

        "has the correct href" in {
          element(Selectors.wyoLink).attr("href") shouldBe whatYouOweLink
        }
      }
    }

    "the user has a time to pay arrangement and no breathing space" should {

      lazy val view = injectedView(estimatedLPP2ModelTTP, Html(""))(request, messages, mockConfig, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct first explanation paragraph" in {
        elementText(Selectors.explanation1) shouldBe
          "This penalty applies from day 31, if any VAT remains unpaid."
      }

      "have the correct calculation explanation statement" in {
        elementText(Selectors.explanation2) shouldBe
          "The total builds up daily until you pay your VAT or set up a payment plan."
      }

      "have the correct calculation explanation" in {
        elementText(Selectors.explanation3) shouldBe
          "The calculation we use for each day is: (Penalty rate of 4.4% × unpaid VAT) ÷ days in a year"
      }

      "have inset text with the correct content" in {
        elementText(Selectors.ttpInset) shouldBe "You’ve asked HMRC if you can set up a payment plan. If a payment plan " +
          "has been agreed, and you keep up with all payments, this penalty will not increase further."
      }

      "have the correct estimates paragraph content" in {
        elementText(Selectors.estimatesDescription) shouldBe "Penalties will show as estimates until you make all payments due under " +
          "the payment plan."
      }
    }

    "the user has no time to pay arrangement but breathing space" should {

      lazy val view = injectedView(estimatedLPP2Model.copy(breathingSpace = true), Html(""))(request, messages, mockConfig, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct first explanation paragraph" in {
        elementText(Selectors.explanation1) shouldBe
          "This penalty applies from day 31, if any VAT remains unpaid."
      }

      "have the correct calculation explanation statement" in {
        elementText(Selectors.explanation2BreathingSpace) shouldBe
          "The total builds up daily until you pay your VAT or set up a payment plan. " +
            "However, when we calculate your penalty we do not count the days you are in Breathing Space."
      }

      "have the correct calculation explanation" in {
        elementText(Selectors.explanation3) shouldBe
          "The calculation we use for each day is: (Penalty rate of 4.4% × unpaid VAT) ÷ days in a year"
      }

      "not have inset text" in {
        elementExtinct(Selectors.ttpInset)
      }

      "have the correct estimates paragraph content" which {

        "has the correct first sentence" in {
          elementText("#estimates") shouldBe "Penalties and interest will show as estimates until:"
        }

        "has the correct first bullet point" in {
          elementText("#bs-only-bullet1") shouldBe "you pay the charge they relate to, and"
        }

        "has the correct second bullet point" in {
          elementText("#bs-bullet2") shouldBe "Breathing Space ends"
        }
      }
    }

    "the user has a time to pay arrangement and breathing space" should {

      lazy val view = injectedView(estimatedLPP2ModelTTP.copy(breathingSpace = true), Html(""))(request, messages, mockConfig, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct first explanation paragraph" in {
        elementText(Selectors.explanation1) shouldBe
          "This penalty applies from day 31, if any VAT remains unpaid."
      }

      "have the correct calculation explanation statement" in {
        elementText(Selectors.explanation2BreathingSpace) shouldBe
          "The total builds up daily until you pay your VAT or set up a payment plan. " +
            "However, when we calculate your penalty we do not count the days you are in Breathing Space."
      }

      "have the correct calculation explanation" in {
        elementText(Selectors.explanation3) shouldBe
          "The calculation we use for each day is: (Penalty rate of 4.4% × unpaid VAT) ÷ days in a year"
      }

      "have inset text with the correct content" in {
        elementText(Selectors.ttpInset) shouldBe "You’ve asked HMRC if you can set up a payment plan. If a payment plan " +
          "has been agreed, and you keep up with all payments, this penalty will not increase further."
      }

      "have the correct estimates paragraph content" which {

        "has the correct first sentence" in {
          elementText("#estimates") shouldBe "Penalties and interest will show as estimates until:"
        }

        "has the correct first bullet point" in {
          elementText("#bs-ttp-bullet1") shouldBe "you make all payments due under the payment plan, and"
        }

        "has the correct second bullet point" in {
          elementText("#bs-bullet2") shouldBe "Breathing Space ends"
        }
      }
    }
  }

  "Rendering the Estimated LPP2 Page for an agent" when {

    "their client has no time to pay arrangement and no breathing space" should {

      lazy val view = injectedView(estimatedLPP2Model, Html(""))(request, messages, mockConfig, agentUser)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "Second late payment penalty - Your client’s VAT details - GOV.UK"
      }

      "have the correct second paragraph" in {
        elementText(Selectors.explanation2) shouldBe "The total builds up daily until your client pays their VAT or " +
          "sets up a payment plan."
      }

      "have the estimate description" in {
        elementText(Selectors.estimatesDescription) shouldBe
          "Penalties and interest will show as estimates until your client pays the charge they relate to."
      }

      "have a link to VAT penalties and appeals" which {

        "has the correct link text" in {
          elementText(Selectors.penAppealsLink) shouldBe "View your client’s VAT penalties and appeals"
        }

        "has the correct href" in {
          element(Selectors.penAppealsLink).attr("href") shouldBe mockConfig.penaltiesFrontendUrl
        }
      }

      "have a link to the what you owe page" which {

        "has the correct link text" in {
          elementText(Selectors.wyoLink) shouldBe "Return to what your client owes"
        }

        "has the correct href" in {
          element(Selectors.wyoLink).attr("href") shouldBe whatYouOweLink
        }
      }

      "not render breadcrumbs" in {
        elementExtinct(Selectors.breadcrumb1)
      }

      "have a back link" which {

        "has the text Back" in {
          elementText(Selectors.backLink) shouldBe "Back"
        }

        "has the correct href" in {
          element(Selectors.backLink).attr("href") shouldBe whatYouOweLink
        }
      }
    }

    "their client has a time to pay arrangement but no breathing space" should {

      lazy val view = injectedView(estimatedLPP2ModelTTP, Html(""))(request, messages, mockConfig, agentUser)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have inset text with the correct content" in {
        elementText(Selectors.ttpInset) shouldBe "Your client has asked HMRC if they can set up a payment plan. If a " +
          "payment plan has been agreed, and they keep up with all payments, this penalty will not increase further."
      }

      "have the correct estimates paragraph content" in {
        elementText(Selectors.estimatesDescription) shouldBe "Penalties will show as estimates until your client makes all payments " +
          "due under the payment plan."
      }

      "have the correct first explanation paragraph" in {
        elementText(Selectors.explanation1) shouldBe
          "This penalty applies from day 31, if any VAT remains unpaid."
      }

      "have the correct calculation explanation statement" in {
        elementText(Selectors.explanation2) shouldBe
          "The total builds up daily until your client pays their VAT or sets up a payment plan."
      }

      "have the correct calculation explanation" in {
        elementText(Selectors.explanation3) shouldBe
          "The calculation we use for each day is: (Penalty rate of 4.4% × unpaid VAT) ÷ days in a year"
      }
    }

    "their client has no time to pay arrangement but breathing space" should {

      lazy val view = injectedView(estimatedLPP2Model.copy(breathingSpace = true), Html(""))(request, messages, mockConfig, agentUser)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct first explanation paragraph" in {
        elementText(Selectors.explanation1) shouldBe
          "This penalty applies from day 31, if any VAT remains unpaid."
      }

      "have the correct calculation explanation statement" in {
        elementText(Selectors.explanation2BreathingSpace) shouldBe
          "The total builds up daily until your client pays their VAT or sets up a payment plan. " +
            "However, when we calculate their penalty we do not count the days they are in Breathing Space."
      }

      "have the correct calculation explanation" in {
        elementText(Selectors.explanation3) shouldBe
          "The calculation we use for each day is: (Penalty rate of 4.4% × unpaid VAT) ÷ days in a year"
      }

      "not have inset text" in {
        elementExtinct(Selectors.ttpInset)
      }

      "have the correct estimates paragraph content" which {

        "has the correct first sentence" in {
          elementText("#estimates") shouldBe "Penalties and interest will show as estimates until:"
        }

        "has the correct first bullet point" in {
          elementText("#bs-only-bullet1") shouldBe "your client pays the charge they relate to, and"
        }

        "has the correct second bullet point" in {
          elementText("#bs-bullet2") shouldBe "Breathing Space ends"
        }
      }
    }

    "their client has a time to pay arrangement and breathing space" should {

      lazy val view = injectedView(estimatedLPP2ModelTTP.copy(breathingSpace = true), Html(""))(request, messages, mockConfig, agentUser)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct first explanation paragraph" in {
        elementText(Selectors.explanation1) shouldBe
          "This penalty applies from day 31, if any VAT remains unpaid."
      }

      "have the correct calculation explanation statement" in {
        elementText(Selectors.explanation2BreathingSpace) shouldBe
          "The total builds up daily until your client pays their VAT or sets up a payment plan. " +
            "However, when we calculate their penalty we do not count the days they are in Breathing Space."
      }

      "have the correct calculation explanation" in {
        elementText(Selectors.explanation3) shouldBe
          "The calculation we use for each day is: (Penalty rate of 4.4% × unpaid VAT) ÷ days in a year"
      }

      "have inset text with the correct content" in {
        elementText(Selectors.ttpInset) shouldBe "Your client has asked HMRC if they can set up a payment plan. If a payment plan " +
          "has been agreed, and they keep up with all payments, this penalty will not increase further."
      }

      "have the correct estimates paragraph content" which {

        "has the correct first sentence" in {
          elementText("#estimates") shouldBe "Penalties and interest will show as estimates until:"
        }

        "has the correct first bullet point" in {
          elementText("#bs-ttp-bullet1") shouldBe "your client makes all payments due under the payment plan, and"
        }

        "has the correct second bullet point" in {
          elementText("#bs-bullet2") shouldBe "Breathing Space ends"
        }
      }
    }
  }
}
