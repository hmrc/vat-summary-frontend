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

import models.payments.VatOverpayments1stLPP
import models.viewModels.CrystallisedLPP1ViewModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.CrystallisedLPP1View

import java.time.LocalDate

class CrystallisedLPP1ViewSpec extends ViewBaseSpec {

  val injectedView: CrystallisedLPP1View = injector.instanceOf[CrystallisedLPP1View]
  val whatYouOweLink: String = controllers.routes.WhatYouOweController.show.url

  def viewModel(chargeValue: String = "VAT Return 1st LPP"): CrystallisedLPP1ViewModel = CrystallisedLPP1ViewModel(
    "99",
    "10",
    Some("20"),
    2.4,
    Some(2.6),
    111.11,
    Some(222.22),
    LocalDate.parse("2020-01-01"),
    500.55,
    100.11,
    400.44,
    LocalDate.parse("2020-03-03"),
    LocalDate.parse("2020-04-04"),
    chargeValue,
    "CHARGEREF",
    isOverdue = false, false
  )

  "Rendering the Crystallised LPP1 Page for a principal user" when {

    "there are two penalty parts with Charge Ref \"VAT Overpayments 1st LPP\"" should {
      lazy val view = injectedView(viewModel(VatOverpayments1stLPP.value), Html(""))(request, messages, mockConfig, user)
      lazy val viewAsString = view.toString
      lazy implicit val document2: Document = Jsoup.parse(view.body)

      "have the correct first explanation paragraph" in {
        elementText("#content > div > div > p:nth-child(2)") shouldBe
          "This penalty applies if the VAT correction charge for 3 March 2020 to 4 April 2020 has not been paid for 99 days."
      }

      "use a non breaking space for the first explanation paragraph" in {
        viewAsString.contains("This penalty applies if the VAT correction" +
          "charge for 3\u00a0March\u00a02020 to 4\u00a0April\u00a02020 has not been paid for 99 days.")
      }
    }

    "there are two penalty parts with Charge Ref \"VAT Return 1st LPP\"" should {

      lazy val view = injectedView(viewModel(), Html(""))(request, messages, mockConfig, user)
      lazy val viewAsString = view.toString
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "Late payment penalty - Manage your VAT account - GOV.UK"
      }

      "have the correct page heading" in {
        elementText("h1") shouldBe "3 March 2020 to 4 April 2020 Late payment penalty"
      }

      "use a non breaking space for the page heading" in {
        viewAsString.contains("3\u00a0March\u00a02020 to 4\u00a0April\u00a02020 Late payment penalty")
      }

      "have a period caption" in {
        elementText(".govuk-caption-xl") shouldBe "3 March 2020 to 4 April 2020"
      }

      "use a non breaking space for the period caption" in {
        viewAsString.contains("3\u00a0March\u00a02020 to 4\u00a0April\u00a02020")
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
          "This penalty applies if VAT has not been paid for 99 days."
      }

      "have the correct calculation explanation paragraph" in {
        elementText("#two-parts-explanation") shouldBe "It is made up of 2 parts:"
      }

      "not have a paragraph for the 1 part calculation" in {
        elementExtinct("#one-part-explanation")
      }

      "have a bullet list" which {

        "has the first penalty part calculation as the first bullet point" in {
          elementText("#content ul > li:nth-child(1)") shouldBe s"${viewModel().part1PenaltyRate}% of " +
            s"£${viewModel().part1UnpaidVAT} (the unpaid VAT ${viewModel().part1Days} days after the due date)"
        }

        "has the second penalty part calculation as the second bullet point" in {
          elementText("#content ul > li:nth-child(2)") shouldBe s"${viewModel().part2PenaltyRate.get}% of " +
            s"£${viewModel().part2UnpaidVAT.get} (the unpaid VAT ${viewModel().part2Days.get} days after the due date)"
        }
      }

      "have the correct heading for the first row" in {
        elementText(".govuk-summary-list__row:nth-child(1) > dt") shouldBe "Due date"
      }

      "display when the penalty is due by" in {
        elementText(".govuk-summary-list__row:nth-child(1) > dd") shouldBe "1 January 2020"
      }

      "use a non breaking space to display when the penalty is due by" in {
        viewAsString.contains(" Due Date 1\u00a0January\u00a02020")
      }

      "have the correct heading for the second row" in {
        elementText(".govuk-summary-list__row:nth-child(2) > dt") shouldBe "Penalty amount"
      }

      "display the original amount of the penalty charge" in {
        elementText(".govuk-summary-list__row:nth-child(2) > dd") shouldBe s"£${viewModel().penaltyAmount}"
      }

      "have the correct heading for the third row" in {
        elementText(".govuk-summary-list__row:nth-child(3) > dt") shouldBe "Amount received"
      }

      "display the amount received" in {
        elementText(".govuk-summary-list__row:nth-child(3) > dd") shouldBe s"£${viewModel().amountReceived}"
      }

      "have the correct heading for the fourth row" in {
        elementText(".govuk-summary-list__row:nth-child(4) > dt") shouldBe "Left to pay"
      }

      "display the outstanding amount" in {
        elementText(".govuk-summary-list__row:nth-child(4) > dd") shouldBe s"£${viewModel().leftToPay}"
      }

      "have a pay now button" which {

        "has the correct link text" in {
          elementText("#content > div > div > a") shouldBe "Pay now"
        }

        "has the correct href" in {
          element("#content > div > div > a").attr("href") shouldBe controllers.routes.MakePaymentController.makePayment(
            40044, 4, 2020, "2020-04-04", "VAT Return 1st LPP", "2020-01-01", "CHARGEREF"
          ).url
        }
      }

      "have a link to VAT penalties and appeals" which {

        "has the correct link text" in {
          elementText("#content > div > div > p:nth-child(7) > a") shouldBe "View your VAT penalties and appeals"
        }

        "has the correct href" in {
          element("#content > div > div > p:nth-child(7) > a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
        }
      }

      "have a link to the what you owe page" which {

        "has the correct link text" in {
          elementText("#content > div > div > p:nth-child(8) > a") shouldBe "Return to what you owe"
        }

        "has the correct href" in {
          element("#content > div > div > p:nth-child(8) > a").attr("href") shouldBe whatYouOweLink
        }
      }
    }

    "there is one penalty part" should {

      lazy val view =
        injectedView(viewModel().copy(part2Days = None, part2UnpaidVAT = None), Html(""))(request, messages, mockConfig, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct calculation explanation paragraph" in {
        elementText("#one-part-explanation") shouldBe
          s"The calculation we use is: ${viewModel().part1PenaltyRate}% of £${viewModel().part1UnpaidVAT} " +
          s"(the unpaid VAT ${viewModel().part1Days} days after the due date)"
      }

      "not have a paragraph for the 2 parts calculation" in {
        elementExtinct("#two-parts-explanation")
      }

      "not have a bullet list" in {
        elementExtinct("#content ul")
      }
    }
  }

  "Rendering the Crystallised LPP1 Page for an agent" should {

    lazy val view = injectedView(viewModel(), Html(""))(request, messages, mockConfig, agentUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Late payment penalty - Your client’s VAT details - GOV.UK"
    }

    "have a link to the what you owe page" which {

      "has the correct link text" in {
        elementText("#content > div > div > p:nth-child(7) > a") shouldBe "Return to what your client owes"
      }

      "has the correct href" in {
        element("#content > div > div > p:nth-child(7) > a").attr("href") shouldBe whatYouOweLink
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

    "have a link to VAT penalties and appeals" which {

      "has the correct link text" in {
        elementText("#content > div > div > p:nth-child(6) > a") shouldBe "View your client’s VAT penalties and appeals"
      }

      "has the correct href" in {
        element("#content > div > div > p:nth-child(6) > a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
      }
    }
  }
}
