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

import common.TestModels.estimatedLPP1Model
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.EstimatedLPP1View


class EstimatedLPP1ViewSpec extends ViewBaseSpec {

  val injectedView: EstimatedLPP1View = injector.instanceOf[EstimatedLPP1View]
  val whatYouOweLink: String = controllers.routes.WhatYouOweController.show.url

  "Rendering the Estimated LPP1 breakdown page for a principal user" when {

    lazy val view = injectedView(estimatedLPP1Model, Html(""))(request, messages, mockConfig, user)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Late payment penalty - Manage your VAT account - GOV.UK"
    }

    "have the correct page heading" in {
      elementWholeText("h1") shouldBe "1\u00a0January\u00a02019 to 2\u00a0February\u00a02019 Late payment penalty"
    }

    "have a period caption" in {
      elementWholeText(".govuk-caption-xl") shouldBe "1\u00a0January\u00a02019 to 2\u00a0February\u00a02019"
    }

    "not render a back link" in {
      elementExtinct(".govuk-back-link")
    }

    "render breadcrumbs which" should {

      "have the text 'Business tax account'" in {
        elementText("body > div > div.govuk-breadcrumbs > ol > li:nth-child(1) > a") shouldBe "Business tax account"
      }

      "link to bta" in {
        element("body > div > div.govuk-breadcrumbs > ol > li:nth-child(1) > a").attr("href") shouldBe "bta-url"
      }

      "have the text 'Your VAT account'" in {
        elementText("body > div > div.govuk-breadcrumbs > ol > li:nth-child(2) > a") shouldBe "Your VAT account"
      }

      "link to VAT overview page" in {
        element("body > div > div.govuk-breadcrumbs > ol > li:nth-child(2) > a").attr("href") shouldBe
          controllers.routes.VatDetailsController.details.url
      }

      "have the text 'What You Owe'" in {
        elementText("body > div > div.govuk-breadcrumbs > ol > li:nth-child(3) > a") shouldBe "What you owe"
      }

      "link to the what you owe page" in {
        element("body > div > div.govuk-breadcrumbs > ol > li:nth-child(3) > a").attr("href") shouldBe
          whatYouOweLink
      }
    }

    "have the correct first explanation paragraph" in {
      elementText("#content > div > div > p:nth-child(2)") shouldBe
        s"This penalty applies if VAT has not been paid for ${estimatedLPP1Model.part1Days} days."
    }

    "have the correct calculation explanation" in {
      elementText("#content > div > div > p:nth-child(3)") shouldBe "The calculation we use is: " +
        s"${estimatedLPP1Model.part1PenaltyRate}% of £${estimatedLPP1Model.part1UnpaidVAT} (the unpaid VAT " +
        s"${estimatedLPP1Model.part1Days} days after the due date)"
    }

    "have the correct heading for the first row" in {
      elementText(".govuk-summary-list__row:nth-child(1) > dt") shouldBe "Penalty amount (estimate)"
    }

    "display the penalty amount" in {
      elementText(".govuk-summary-list__row:nth-child(1) > dd") shouldBe s"£${estimatedLPP1Model.penaltyAmount}"
    }

    "have the correct heading for the second row" in {
      elementText(".govuk-summary-list__row:nth-child(2) > dt") shouldBe "Amount received"
    }

    "display the amount received" in {
      elementText(".govuk-summary-list__row:nth-child(2) > dd") shouldBe "£0.00"
    }

    "have the correct heading for the third row" in {
      elementText(".govuk-summary-list__row:nth-child(3) > dt") shouldBe "Left to pay"
    }

    "display the amount left to pay" in {
      elementText(".govuk-summary-list__row:nth-child(3) > dd") shouldBe s"£${estimatedLPP1Model.penaltyAmount}"
    }

    "have the correct warning text" in {
      elementText(".govuk-warning-text__text") shouldBe "Warning The penalty will increase by a further " +
        s"${estimatedLPP1Model.part2PenaltyRate}% of the unpaid VAT, if VAT remains unpaid " +
        s"${estimatedLPP1Model.part2Days} days after the due date."
    }

    "not render inset text" in {
      elementExtinct(".govuk-inset-text")
    }

    "have an estimates subheading" in {
      elementText("#content h2") shouldBe "Estimates"
    }

    "have the estimate description" which {

      "has the correct introduction text" in {
        elementText("#content > div > div > p:nth-child(7)") shouldBe "Penalties will show as estimates until:"
      }

      "has the correct first bullet sentence" in {
        elementText("#content ul > li:nth-child(1)") shouldBe "you pay the VAT bill, or"
      }

      "has the correct second bullet sentence" in {
        elementText("#content ul > li:nth-child(2)") shouldBe
          s"${estimatedLPP1Model.part2Days} days have passed since the VAT due date"
      }
    }

    "have a link to VAT penalties and appeals" which {

      "has the correct link text" in {
        elementText("#content > div > div > p:nth-child(9) > a") shouldBe "View your VAT penalties and appeals"
      }

      "has the correct href" in {
        element("#content > div > div > p:nth-child(9) > a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
      }
    }

    "have a link to the what you owe page" which {

      "has the correct link text" in {
        elementText("#wyo-link") shouldBe "Return to what you owe"
      }

      "has the correct href" in {
        element("#wyo-link > a").attr("href") shouldBe whatYouOweLink
      }
    }
  }

  "TTP/breathing space content" when {

    "the user has a time to pay arrangement and no breathing space" should {

      lazy val view = injectedView(estimatedLPP1Model.copy(timeToPayPlan = true), Html(""))(request, messages, mockConfig, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "not render warning text" in {
        elementExtinct(".govuk-warning-text__text")
      }

      "have the correct inset text" in {
        elementText("#ttp-inset") shouldBe "You’ve asked HMRC if you can set up a payment plan. " +
          "If a payment plan has been agreed, and you keep up with all payments, this penalty will not increase further."
      }

      "have an estimates subheading" in {
        elementText("#estimates-subheading") shouldBe "Estimates"
      }

      "have a time to pay plan description" in {
        elementText("#ttp-only-p1") shouldBe "Penalties will show as estimates until you make all payments due under the payment plan."
      }
    }

    "the user has time to pay and breathing space arrangements" should {

      lazy val view = injectedView(estimatedLPP1Model.copy(timeToPayPlan = true, breathingSpace = true), Html(""))(request, messages, mockConfig, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "not show the warning text" in {
        elementExtinct(".govuk-warning-text__text")
      }

      "show the TTP inset text" in {
        document.select("#ttp-inset").size shouldBe 1
      }

      "have the correct first sentence" in {
        elementText("#bs-ttp-p1") shouldBe "Penalties will show as estimates until:"
      }

      "have the correct first bullet point" in {
        elementText("#bs-ttp-bullet1") shouldBe "you make all payments due under the payment plan, and"
      }

      "have the correct second bullet point" in {
        elementText("#bs-ttp-bullet2") shouldBe "Breathing Space ends"
      }

    }

    "the user has breathing space and no time to pay arrangement" should {

      lazy val view = injectedView(estimatedLPP1Model.copy(breathingSpace = true), Html(""))(request, messages, mockConfig, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "not show the warning text" in {
        elementExtinct(".govuk-warning-text__text")
      }

      "not show the TTP inset text" in {
        elementExtinct("#ttp-inset")
      }

      "show the correct estimates section content" in {
        elementText("#bs-only-p1") shouldBe "Penalties will show as estimates until your Breathing Space ends."
      }
    }

  }

  "Rendering the Estimated LPP1 breakdown page for an agent" should {

    lazy val view = injectedView(estimatedLPP1Model, Html(""))(request, messages, mockConfig, agentUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Late payment penalty - Your client’s VAT details - GOV.UK"
    }

    "have the correct warning text" in {
      elementText(".govuk-warning-text__text") shouldBe "Warning The penalty will increase by a further " +
        s"${estimatedLPP1Model.part2PenaltyRate}% of the unpaid VAT, if VAT remains unpaid " +
        s"${estimatedLPP1Model.part2Days} days after the due date."
    }

    "not render inset text" in {
      elementExtinct(".govuk-inset-text")
    }

    "have the estimate description" which {

      "has the correct introduction text" in {
        elementText("#estimate-p1") shouldBe "Penalties will show as estimates until:"
      }

      "has the correct first bullet sentence" in {
        elementText("#estimates-bullet1") shouldBe "your client pays the VAT bill, or"
      }

      "has the correct second bullet sentence" in {
        elementText("#estimates-bullet2") shouldBe
          s"${estimatedLPP1Model.part2Days} days have passed since the VAT due date"
      }
    }

    "have a link to VAT penalties and appeals" which {

      "has the correct link text" in {
        elementText("#penalties-appeal-link") shouldBe "View your client’s VAT penalties and appeals"
      }

      "has the correct href" in {
        element("#penalties-appeal-link > a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
      }
    }

    "have a link to the what you owe page" which {

      "has the correct link text" in {
        elementText("#wyo-link") shouldBe "Return to what your client owes"
      }

      "has the correct href" in {
        element("#wyo-link > a").attr("href") shouldBe whatYouOweLink
      }
    }

    "not render breadcrumbs" in {
      elementExtinct(".govuk-breadcrumbs")
    }

    "have a back link" which {

      "has the text Back" in {
        elementText(".govuk-back-link") shouldBe "Back"
      }

      "has the correct href" in {
        element(".govuk-back-link").attr("href") shouldBe whatYouOweLink
      }
    }
  }

  "Rendering the Estimated LPP1 breakdown page for an agent whose client has a time to pay plan set up" should {

    lazy val view = injectedView(estimatedLPP1Model.copy(timeToPayPlan = true), Html(""))(request, messages, mockConfig, agentUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "not render warning text" in {
      elementExtinct(".govuk-warning-text__text")
    }

    "have the correct inset text" in {
      elementText("#ttp-inset") shouldBe "Your client has asked HMRC if they can set up a payment plan. " +
        "If a payment plan has been agreed, and they keep up with all payments, this penalty will not increase further."
    }

    "have an estimates subheading" in {
      elementText("#estimates-subheading") shouldBe "Estimates"
    }

    "have a time to pay plan description" in {
      elementText("#ttp-only-p1") shouldBe
        "Penalties will show as estimates until your client makes all payments due under the payment plan."
    }
  }

}
