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

import common.TestModels.estimatedInterestModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.EstimatedInterestView

class EstimatedInterestViewSpec extends ViewBaseSpec {

  val injectedView: EstimatedInterestView = injector.instanceOf[EstimatedInterestView]
  val whatYouOweLink: String = controllers.routes.WhatYouOweController.show.url

  "Rendering the Interest Charge Details page for a principal user" when {

    "the interest is not for a penalty charge" should {

      lazy val view = injectedView(estimatedInterestModel, Html(""))(request, messages, mockConfig, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "Interest on central assessment of VAT - Manage your VAT account - GOV.UK"
      }

      "have the correct page heading" in {
        elementText("h1") shouldBe "1\u00a0January\u00a02018 to 2\u00a0February\u00a02018 Interest on central assessment of VAT"
      }

      "have a period caption" in {
        elementText(".govuk-caption-xl") shouldBe "1\u00a0January\u00a02018 to 2\u00a0February\u00a02018"
      }

      "render breadcrumbs which" should {

        "have the text 'Business tax account'" in {
          elementText("li.govuk-breadcrumbs__list-item > a") shouldBe "Business tax account"
        }

        "link to bta" in {
          element("li.govuk-breadcrumbs__list-item > a").attr("href") shouldBe "bta-url"
        }

        "have the text 'Your VAT account'" in {
          elementText("li.govuk-breadcrumbs__list-item:nth-child(2) > a") shouldBe "Your VAT account"
        }

        "link to the VAT overview page" in {
          element("li.govuk-breadcrumbs__list-item:nth-child(2) > a").attr("href") shouldBe
            controllers.routes.VatDetailsController.details.url
        }

        "have the text 'What you owe'" in {
          elementText("li.govuk-breadcrumbs__list-item:nth-child(3) > a") shouldBe "What you owe"
        }

        "link to the what you owe page" in {
          element("li.govuk-breadcrumbs__list-item:nth-child(3) > a").attr("href") shouldBe whatYouOweLink
        }
      }

      "have the correct first explanation paragraph" in {
        elementText("#charge-interest") shouldBe "We charge late payment interest on any unpaid VAT."
      }

      "have the correct second explanation paragraph" in {
        elementText("#increase-daily") shouldBe
          "The total increases daily based on the amount of unpaid VAT for the period."
      }

      "must contain the HowInterestIsCalculated dropdown" in {

        elementExistsOnce("#how-interest-calculated-dropdown")
      }

      "have the correct third explanation paragraph" in {
        elementText("#calculation") shouldBe "The calculation we use for each day is: " +
          "(Interest rate × VAT amount unpaid) ÷ days in a year"
      }

      "have the correct fourth explanation paragraph" in {
        elementText("#rates") shouldBe "You can find interest rates on " +
          "GOV.UK (opens in a new tab). If the interest rate changes during the time interest is building up, we use the " +
          "old interest rate up to the change date, then the new one after that."
      }

      "have a link to guidance for interest rates" which {

        "has the correct link text" in {
          elementText("#prevIntRateLink") shouldBe
            "find interest rates on GOV.UK (opens in a new tab)"
        }

        "has the correct href" in {
          element("#prevIntRateLink").attr("href") shouldBe mockConfig.govUkPrevIntRateUrl
        }
      }

      "have the correct heading for the first row" in {
        elementText(".govuk-summary-list__row:nth-child(1) > dt") shouldBe "Current amount (estimate)"
      }

      "display the current amount of interest accumulated" in {
        elementText(".govuk-summary-list__row:nth-child(1) > dd") shouldBe s"£${estimatedInterestModel.interestAmount}"
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

      "display the outstanding amount" in {
        elementText(".govuk-summary-list__row:nth-child(3) > dd") shouldBe s"£${estimatedInterestModel.interestAmount}"
      }

      "have the correct subheading" in {
        elementText("#estimates-subheading") shouldBe "Estimates"
      }

      "have the correct paragraph explaining estimates" in {
        elementText("#estimates") shouldBe "Penalties and interest will show as estimates " +
          "until you pay the charge they relate to."
      }

      "have a link to guidance on how interest is calculated" which {

        "has the correct link text" in {
          elementText("#guidance-link") shouldBe
            "Read the guidance about how interest is calculated (opens in a new tab)"
        }

        "has the correct href" in {
          element("#guidance-link > a").attr("href") shouldBe mockConfig.latePaymentGuidanceUrl
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

    "the interest is for a penalty charge" should {

      lazy val view = injectedView(estimatedInterestModel.copy(isPenalty = true), Html(""))(request, messages, mockConfig, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct first explanation paragraph" in {
        elementText("#charge-interest") shouldBe "We charge late payment interest on any unpaid penalties."
      }

      "have the correct second explanation paragraph" in {
        elementText("#increase-daily") shouldBe
          "The total increases daily based on the unpaid amount."
      }

      "have the correct third explanation paragraph" in {
        elementText("#calculation") shouldBe "The calculation we use for each day is: " +
          "(Interest rate × penalty amount unpaid) ÷ days in a year"
      }

      "have the correct fourth explanation paragraph" in {
        elementText("#rates") shouldBe "You can find interest rates on GOV.UK (opens in a new tab). " +
          "If the interest rate changes during the time interest is building up, we use the old interest rate up to " +
          "the change date, then the new one after that."
      }

      "have a link to guidance for interest rates" which {

        "has the correct link text" in {
          elementText("#prevIntRateLink") shouldBe
            "find interest rates on GOV.UK (opens in a new tab)"
        }

        "has the correct href" in {
          element("#prevIntRateLink").attr("href") shouldBe mockConfig.govUkPrevIntRateUrl
        }
      }
    }
  }

  "Rendering the Interest Charge Details page for an agent" should {

    lazy val view = injectedView(estimatedInterestModel, Html(""))(request, messages, mockConfig, agentUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

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

    "have the estimate description" in {
      elementText("#estimates") shouldBe
        "Penalties and interest will show as estimates until your client pays the charge they relate to."
    }

    "have a link to the What your client owes page" which {

      "has the correct link text" in {
        elementText("#wyo-link > a") shouldBe "Return to what your client owes"
      }

      "has the correct href" in {
        element("#wyo-link > a").attr("href") shouldBe whatYouOweLink
      }
    }
  }
}
