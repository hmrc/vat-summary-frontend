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

package views.payments

import common.TestModels.estimatedLPP2Model
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.EstimatedLPP2View


class EstimatedLPP2ViewSpec extends ViewBaseSpec {

  val injectedView: EstimatedLPP2View = injector.instanceOf[EstimatedLPP2View]
  val whatYouOweLink: String = testOnly.controllers.routes.WhatYouOweController.show.url

  "Rendering the Estimated LPP2 Page for a principal user" when {


    lazy val view = injectedView(estimatedLPP2Model, Html(""))(request, messages, mockConfig, user)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Late payment penalty - Manage your VAT account - GOV.UK"
    }

    "have the correct page heading" in {
      elementText("h1") shouldBe "1 January 2020 to 2 February 2020 Late payment penalty"
    }

    "have a period caption" in {
      elementText(".govuk-caption-xl") shouldBe "1 January 2020 to 2 February 2020"
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

    "not render a back link" in {
      elementExtinct(".govuk-back-link")
    }

    "have the correct first explanation paragraph" in {
      elementText("#content > div > div > p:nth-child(2)") shouldBe
        "This penalty applies from day 31, if any VAT remains unpaid."
    }

    "have the correct calculation explanation statement" in {
      elementText("#content > div > div > p:nth-child(3)") shouldBe "The total increases daily based on the amount of unpaid VAT for the period."
    }

    "have the correct calculation explanation" in {
      elementText("#content > div > div > p:nth-child(4)") shouldBe
        "The calculation we use for each day is: (Penalty rate of 4.4% × unpaid VAT) ÷ days in a year"
    }


    "have the correct heading for the first row" in {
      elementText(".govuk-summary-list__row:nth-child(1) > dt") shouldBe "Penalty amount (estimate)"
    }

    "display the penalty amount" in {
      elementText(".govuk-summary-list__row:nth-child(1) > dd") shouldBe s"£${estimatedLPP2Model.penaltyAmount}"
    }

    "have the correct heading for the second row" in {
      elementText(".govuk-summary-list__row:nth-child(2) > dt") shouldBe "Amount received"
    }

    "display the amount received" in {
      elementText(".govuk-summary-list__row:nth-child(2) > dd") shouldBe "£0"
    }

    "have the correct heading for the third row" in {
      elementText(".govuk-summary-list__row:nth-child(3) > dt") shouldBe "Left to pay"
    }

    "display the amount left to pay" in {
      elementText(".govuk-summary-list__row:nth-child(3) > dd") shouldBe s"£${estimatedLPP2Model.penaltyAmount}"
    }

    "have the correct subheading" in {
      elementText("#estimates-subheading") shouldBe "Estimates"
    }

    "have the estimate description" in {
      elementText("#content > div > div > p:nth-child(7)") shouldBe
        "Penalties and interest will show as estimates until you pay the charge they relate to."
    }

    "have a link to VAT penalties and appeals" which {

      "has the correct link text" in {
        elementText("#content > div > div > p:nth-child(8) > a") shouldBe "View your VAT penalties and appeals"
      }

      "has the correct href" in {
        element("#content > div > div > p:nth-child(8) > a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
      }
    }


    "have a link to the what you owe page" which {

      "has the correct link text" in {
        elementText("#content > div > div > p:nth-child(9) > a") shouldBe "Return to what you owe"
      }

      "has the correct href" in {
        element("#content > div > div > p:nth-child(9) > a").attr("href") shouldBe whatYouOweLink
      }
    }
  }


  "Rendering the Estimated LPP2 Page for an agent" should {

    lazy val view = injectedView(estimatedLPP2Model, Html(""))(request, messages, mockConfig, agentUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Late payment penalty - Your client’s VAT details - GOV.UK"
    }

    "have the estimate description" in {
      elementText("#content > div > div > p:nth-child(7)") shouldBe
        "Penalties and interest will show as estimates until your client pays the charge they relate to."
    }

    "have a link to VAT penalties and appeals" which {

      "has the correct link text" in {
        elementText("#content > div > div > p:nth-child(8) > a") shouldBe "View your client’s VAT penalties and appeals page"
      }

      "has the correct href" in {
        element("#content > div > div > p:nth-child(8) > a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
      }
    }

    "have a link to the what you owe page" which {

      "has the correct link text" in {
        elementText("#content > div > div > p:nth-child(9) > a") shouldBe "Return to what your client owes"
      }

      "has the correct href" in {
        element("#content > div > div > p:nth-child(9) > a").attr("href") shouldBe whatYouOweLink
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
}
