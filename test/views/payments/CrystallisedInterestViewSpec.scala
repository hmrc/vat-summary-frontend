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

import models.viewModels.CrystallisedInterestViewModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.CrystallisedInterestView

import java.time.LocalDate

class CrystallisedInterestViewSpec extends ViewBaseSpec {

  val injectedView: CrystallisedInterestView = injector.instanceOf[CrystallisedInterestView]
  val whatYouOweLink: String = controllers.routes.WhatYouOweController.show.url

  val viewModel: CrystallisedInterestViewModel = CrystallisedInterestViewModel(
    LocalDate.parse("2022-10-01"),
    LocalDate.parse("2022-12-31"),
    "VAT OA Default Interest",
    LocalDate.parse("2023-03-30"),
    7.71,
    0.00,
    7.71,
    isOverdue = true,
    "chargeRef",
    isPenalty = false
  )

  "Rendering the Crystallised Interest Page for a principal user" when {

    "the interest is not for a penalty charge" should {

      lazy val view = injectedView(viewModel, Html(""))(request, messages, mockConfig, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "VAT officer’s assessment interest - Manage your VAT account - GOV.UK"
      }

      "have the correct page heading" in {
        elementText("h1") shouldBe "1\u00a0October\u00a02022 to 31\u00a0December\u00a02022 VAT officer’s assessment interest"
      }

      "have a period caption" in {
        elementText(".govuk-caption-xl") shouldBe "1\u00a0October\u00a02022 to 31\u00a0December\u00a02022"
      }

      "render breadcrumbs which" should {

        "have the text 'Business tax account'" in {
          elementText(".govuk-breadcrumbs li:nth-child(1) > a") shouldBe "Business tax account"
        }
        "link to bta" in {
          element(".govuk-breadcrumbs li:nth-child(1) > a").attr("href") shouldBe "bta-url"
        }
        "have the text 'Your VAT account'" in {
          elementText(".govuk-breadcrumbs li:nth-child(2) > a") shouldBe "Your VAT account"
        }
        "link to VAT overview page" in {
          element(".govuk-breadcrumbs li:nth-child(2) > a").attr("href") shouldBe
            controllers.routes.VatDetailsController.details.url
        }
        "have the text 'What You Owe'" in {
          elementText(".govuk-breadcrumbs li:nth-child(3) > a") shouldBe "What you owe"
        }
        "link to the what you owe page" in {
          element(".govuk-breadcrumbs li:nth-child(3) > a").attr("href") shouldBe
            whatYouOweLink
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

      "have the correct heading for the first row" in {
        elementText(".govuk-summary-list__row:nth-child(1) > dt") shouldBe "Due date"
      }

      "display when the interest is due by" in {
        elementText(".govuk-summary-list__row:nth-child(1) > dd") shouldBe "30\u00a0March\u00a02023 overdue"
      }

      "have the correct heading for the second row" in {
        elementText(".govuk-summary-list__row:nth-child(2) > dt") shouldBe "Interest amount"
      }

      "display the current amount of interest accumulated" in {
        elementText(".govuk-summary-list__row:nth-child(2) > dd") shouldBe s"£${viewModel.interestAmount}"
      }

      "have the correct heading for the third row" in {
        elementText(".govuk-summary-list__row:nth-child(3) > dt") shouldBe "Amount received"
      }

      "display the amount received" in {
        elementText(".govuk-summary-list__row:nth-child(3) > dd") shouldBe s"£0.00"
      }

      "have the correct heading for the fourth row" in {
        elementText(".govuk-summary-list__row:nth-child(4) > dt") shouldBe "Left to pay"
      }

      "display the outstanding amount" in {
        elementText(".govuk-summary-list__row:nth-child(4) > dd") shouldBe s"£${viewModel.leftToPay}"
      }

      "have a pay now button" which {

        "has the correct link text" in {
          elementText("#pay-button") shouldBe "Pay now"
        }

        "has the correct href" in {
          element("#pay-button").attr("href") shouldBe controllers.routes.MakePaymentController.makePayment(
            771, 12, 2022, "2022-12-31", "VAT OA Default Interest", "2023-03-30", "chargeRef"
          ).url
        }
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

      lazy val view = injectedView(viewModel.copy(isPenalty = true), Html(""))(request, messages, mockConfig, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "must contain the HowInterestIsCalculated dropdown" in {

        elementExistsOnce("#how-interest-calculated-dropdown")
      }
    }
  }

  "Rendering the Crystallised Interest Page for an agent" should {

    lazy val view = injectedView(viewModel, Html(""))(request, messages, mockConfig, agentUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

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

    "have a backLink" which {

      "has the text Back" in {
        elementText(".govuk-back-link") shouldBe "Back"
      }

      "has the correct href" in {
        element(".govuk-back-link").attr("href") shouldBe whatYouOweLink
      }
    }

    "not have a pay now button" in {
      elementExtinct("#pay-button")
    }
  }
}
