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

import models.viewModels.EstimatedInterestViewModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.EstimatedInterestView
import java.time.LocalDate

class EstimatedInterestViewSpec extends ViewBaseSpec {

  val injectedView: EstimatedInterestView = injector.instanceOf[EstimatedInterestView]
  val whatYouOweLink: String = testOnly.controllers.routes.WhatYouOweController.show.url

  val viewModel: EstimatedInterestViewModel = EstimatedInterestViewModel(
    LocalDate.parse("2018-01-01"),
    LocalDate.parse("2018-02-02"),
    "VAT Return Debit Charge",
    2.6,
    3,
    300.33,
    200.22,
    100.11,
    isPenalty = false
  )

  "Rendering the Interest Charge Details page for a principal user" when {

    "the interest is not for a penalty charge" should {

      lazy val view = injectedView(viewModel, Html(""))(request, messages, mockConfig, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "VAT - Manage your VAT account - GOV.UK"
      }

      "have the correct page heading" in {
        elementText("h1") shouldBe "VAT"
      }

      "have a period caption" in {
        elementText(".govuk-caption-xl") shouldBe "1 January 2018 to 2 February 2018"
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
        elementText("p.govuk-body:nth-of-type(1)") shouldBe "We charge interest on any unpaid VAT."
      }

      "have the correct second explanation paragraph" in {
        elementText("p.govuk-body:nth-of-type(2)") shouldBe
          "The total increases daily based on the amount of unpaid VAT for the period."
      }

      "have the correct third explanation paragraph" in {
        elementText("p.govuk-body:nth-of-type(3)") shouldBe "The calculation we use for each day is: " +
          s"(Interest rate of ${viewModel.interestRate}% × VAT amount unpaid) ÷ days in a year"
      }

      "have the correct heading for the first row" in {
        elementText(".govuk-summary-list__row:nth-child(1) > dt") shouldBe "VAT late by"
      }

      "display the number of days the interest is late by" in {
        elementText(".govuk-summary-list__row:nth-child(1) > dd") shouldBe s"${viewModel.numberOfDaysLate} days"
      }

      "have the correct heading for the second row" in {
        elementText(".govuk-summary-list__row:nth-child(2) > dt") shouldBe "Current amount (estimate)"
      }

      "display the current amount of interest accumulated" in {
        elementText(".govuk-summary-list__row:nth-child(2) > dd") shouldBe s"£${viewModel.currentAmount}"
      }

      "have the correct heading for the third row" in {
        elementText(".govuk-summary-list__row:nth-child(3) > dt") shouldBe "Amount received"
      }

      "display the amount received" in {
        elementText(".govuk-summary-list__row:nth-child(3) > dd") shouldBe s"£${viewModel.amountReceived}"
      }

      "have the correct heading for the fourth row" in {
        elementText(".govuk-summary-list__row:nth-child(4) > dt") shouldBe "Left to pay"
      }

      "display the outstanding amount" in {
        elementText(".govuk-summary-list__row:nth-child(4) > dd") shouldBe s"£${viewModel.leftToPay}"
      }

      "have the correct subheading" in {
        elementText("h2") shouldBe "Estimates"
      }

      "have the correct paragraph explaining estimates" in {
        elementText("p.govuk-body:nth-of-type(4)") shouldBe "Penalties and interest will show as estimates " +
          "if HMRC has not been given enough information to calculate the final amounts."
      }

      "have a link to guidance on how interest is calculated" which {

        "has the correct link text" in {
          elementText("p.govuk-body:nth-of-type(5) > a") shouldBe
            "Read the guidance about how interest is calculated (opens in a new tab)"
        }

        "has the correct href" in {
          element("p.govuk-body:nth-of-type(5) > a").attr("href") shouldBe "#"
        }
      }

      "have a link to the what you owe page" which {

        "has the correct link text" in {
          elementText("p.govuk-body:nth-of-type(6) > a") shouldBe "Return to what you owe"
        }

        "has the correct href" in {
          element("p.govuk-body:nth-of-type(6) > a").attr("href") shouldBe whatYouOweLink
        }
      }
    }

    "the interest is for a penalty charge" should {

      lazy val view = injectedView(viewModel.copy(isPenalty = true), Html(""))(request, messages, mockConfig, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct first explanation paragraph" in {
        elementText("p.govuk-body:nth-of-type(1)") shouldBe "We charge interest on any unpaid late payment penalties."
      }

      "have the correct second explanation paragraph" in {
        elementText("p.govuk-body:nth-of-type(2)") shouldBe
          "The total increases daily based on the amount of unpaid penalty for the period."
      }

      "have the correct third explanation paragraph" in {
        elementText("p.govuk-body:nth-of-type(3)") shouldBe "The calculation we use for each day is: " +
          s"(Interest rate of ${viewModel.interestRate}% × penalty amount unpaid) ÷ days in a year"
      }

      "have the correct heading for the first row" in {
        elementText(".govuk-summary-list__key") shouldBe "Late payment penalty late by"
      }
    }

    "the accumulated interest is only 1 day late" should {

      lazy val view = injectedView(viewModel.copy(numberOfDaysLate = 1), Html(""))(request, messages, mockConfig, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the number of days the interest is late by" in {
        elementText(".govuk-summary-list__row:nth-child(1) > dd") shouldBe "1 day"
      }
    }
  }

  "Rendering the Interest Charge Details page for an agent" should {

    lazy val view = injectedView(viewModel, Html(""))(request, messages, mockConfig, agentUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

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

    "have a link to the What your client owes page" which {

      "has the correct link text" in {
        elementText("p.govuk-body:nth-of-type(6) > a") shouldBe "Return to what your client owes"
      }

      "has the correct href" in {
        element("p.govuk-body:nth-of-type(6) > a").attr("href") shouldBe whatYouOweLink
      }
    }
  }
}
