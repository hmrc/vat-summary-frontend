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

import common.TestModels.lateSubmissionPenaltyModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.LateSubmissionPenaltyView


class LateSubmissionPenaltyViewSpec extends ViewBaseSpec {

  val injectedView: LateSubmissionPenaltyView = injector.instanceOf[LateSubmissionPenaltyView]
  val whatYouOweLink: String = controllers.routes.WhatYouOweController.show.url


  "Rendering the Late Submission Penalty page for a principal user" when {


    lazy val view = injectedView(lateSubmissionPenaltyModel, Html(""))(request, messages, mockConfig, user)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Late submission penalty - Manage your VAT account - GOV.UK"
    }

    "have the correct page heading" in {
      elementText("h1") shouldBe "5 May 2020 to 6 June 2020 Late submission penalty"
    }

    "have a period caption" in {
      elementText(".govuk-caption-xl") shouldBe "5 May 2020 to 6 June 2020"
    }

    "not render backlink" in {
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

    "have the correct heading for the first row" in {
      elementText(".govuk-summary-list__row:nth-child(1) > dt") shouldBe "Due date"
    }

    "display when the penalty is due by" in {
      elementText(".govuk-summary-list__row:nth-child(1) > dd") shouldBe "1 October 2020"
    }

    "have the correct heading for the second row" in {
      elementText(".govuk-summary-list__row:nth-child(2) > dt") shouldBe "Penalty amount"
    }

    "display the original amount of the penalty charge" in {
      elementText(".govuk-summary-list__row:nth-child(2) > dd") shouldBe s"£${lateSubmissionPenaltyModel.penaltyAmount}"
    }

    "have the correct heading for the third row" in {
      elementText(".govuk-summary-list__row:nth-child(3) > dt") shouldBe "Amount received"
    }

    "display the amount received" in {
      elementText(".govuk-summary-list__row:nth-child(3) > dd") shouldBe s"£${lateSubmissionPenaltyModel.amountReceived}"
    }

    "have the correct heading for the fourth row" in {
      elementText(".govuk-summary-list__row:nth-child(4) > dt") shouldBe "Left to pay"
    }

    "display the outstanding amount" in {
      elementText(".govuk-summary-list__row:nth-child(4) > dd") shouldBe s"£${lateSubmissionPenaltyModel.leftToPay}"
    }

    "have a pay now button" which {

      "has the correct link text" in {
        elementText("#content > div > div > a") shouldBe "Pay now"
      }


      "has the correct href" in {
        element("#content > div > div > a").attr("href") shouldBe
          "/vat-through-software/make-payment/10055/6/2020/2020-06-06/VAT%20Late%20Submission%20Pen/2020-10-01/CHARGEREF"
      }
    }

    "have a link to VAT penalties and appeals" which {

      "has the correct link text" in {
        elementText("#content > div > div > p:nth-child(4) > a") shouldBe "View your VAT penalties and appeals"
      }

      "has the correct href" in {
        element("#content > div > div > p:nth-child(4) > a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
      }
    }


    "have a link to the what you owe page" which {

      "has the correct link text" in {
        elementText("#content > div > div > p:nth-child(5) > a") shouldBe "Return to what you owe"
      }

      "has the correct href" in {
        element("#content > div > div > p:nth-child(5) > a").attr("href") shouldBe whatYouOweLink
      }
    }
  }


  "Rendering the Late Submission Penalty page for an agent" should {

    lazy val view = injectedView(lateSubmissionPenaltyModel, Html(""))(request, messages, mockConfig, agentUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Late submission penalty - Your client’s VAT details - GOV.UK"
    }

    "have a link to VAT penalties and appeals" which {

      "has the correct link text" in {
        elementText("#content > div > div > p:nth-child(3) > a") shouldBe "View your client’s VAT penalties and appeals"
      }

      "has the correct href" in {
        element("#content > div > div > p:nth-child(3) > a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
      }
    }

    "have a link to the what you owe page" which {

      "has the correct link text" in {
        elementText("#content > div > div > p:nth-child(4) > a") shouldBe "Return to what your client owes"
      }

      "has the correct href" in {
        element("#content > div > div > p:nth-child(4) > a").attr("href") shouldBe whatYouOweLink
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

    "not have a pay now button" in {
      elementExtinct("#content > div > div > a")
    }
  }
}
