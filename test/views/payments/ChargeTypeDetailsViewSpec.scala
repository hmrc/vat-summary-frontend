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

import models.viewModels.WhatYouOweChargeModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.ChargeTypeDetailsView

import java.time.LocalDate

class ChargeTypeDetailsViewSpec extends ViewBaseSpec {

  val chargeTypeDetailsView: ChargeTypeDetailsView = injector.instanceOf[ChargeTypeDetailsView]
  object Selectors {
    val pageHeading = "h1"
    val caption = ".govuk-caption-xl"
    val breadcrumbs = "govuk-breadcrumbs"
    val btaBreadcrumb = "li.govuk-breadcrumbs__list-item > a"
    val vatBreadcrumb = "li.govuk-breadcrumbs__list-item:nth-child(2) > a"
    val openPaymentsBreadcrumb = "li.govuk-breadcrumbs__list-item:nth-child(3) > a"
    val backLink = ".govuk-back-link"
    val dueDateKey = ".govuk-summary-list__key"
    val dueDateValue = ".govuk-summary-list__value"
    val chargeDueKey = ".govuk-summary-list__row:nth-of-type(2) > .govuk-summary-list__key"
    val chargeDueValue = ".govuk-summary-list__row:nth-of-type(2) > .govuk-summary-list__value"
    val clearedAmountKey = ".govuk-summary-list__row:nth-of-type(3) > .govuk-summary-list__key"
    val clearedAmountValue = ".govuk-summary-list__row:nth-of-type(3) > .govuk-summary-list__value"
    val outstandingAmountKey = ".govuk-summary-list__row:nth-of-type(4) > .govuk-summary-list__key"
    val outstandingAmountValue = ".govuk-summary-list__row:nth-of-type(4) > .govuk-summary-list__value"
    val button = ".govuk-button"
    val whatYouOweLink = "#whatYouOweLink"
  }

  val whatYouOweCharge: WhatYouOweChargeModel = WhatYouOweChargeModel(
    chargeDescription = "Some Charge",
    chargeTitle = "Charge Title",
    outstandingAmount = BigDecimal(1111.11),
    originalAmount = BigDecimal(3333.33),
    clearedAmount = Some(BigDecimal(2222.22)),
    dueDate = LocalDate.parse("2021-04-08"),
    periodKey = None,
    isOverdue = false,
    chargeReference = None,
    makePaymentRedirect = "/paymentPageRedirect",
    periodFrom = Some(LocalDate.parse("2021-01-01")),
    periodTo = Some(LocalDate.parse("2021-03-31"))
  )

  val whatYouOweChargeOverdue: WhatYouOweChargeModel = whatYouOweCharge.copy(isOverdue = true)

  val whatYouOweChargeNoPeriod: WhatYouOweChargeModel = whatYouOweCharge.copy(periodFrom = None, periodTo = None)

  val whatYouOweChargeNoPeriodFrom: WhatYouOweChargeModel = whatYouOweCharge.copy(periodFrom = None)

  val whatYouOweChargeNoPeriodTo: WhatYouOweChargeModel = whatYouOweCharge.copy(periodTo = None)

  val whatYouOweChargeNoClearedAmount: WhatYouOweChargeModel = whatYouOweCharge.copy(clearedAmount = None)

  val whatYouOweUrl: String = testOnly.controllers.routes.WhatYouOweController.show.url

  val vatDetailsUrl: String = controllers.routes.VatDetailsController.details.url

  "Rendering the Charge Type Details page for a principal user" when {

    "the user has a cleared amount and a period for the charge" when {

      "the charge isn't overdue" should {

        lazy val view = {
          chargeTypeDetailsView(whatYouOweCharge, Html(""))(request, messages, mockConfig, user)
        }
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct document title" in {
          document.title shouldBe "Charge Title - Manage your VAT account - GOV.UK"
        }

        "have the correct page heading" in {
          elementText(Selectors.pageHeading) shouldBe "Charge Title"
        }

        "have a period caption" in {
          elementText(Selectors.caption) shouldBe "1 January 2021 to 31 March 2021"
        }

        "render breadcrumbs which" should {

          "have the text 'Business tax account'" in {
            elementText(Selectors.btaBreadcrumb) shouldBe "Business tax account"
          }

          "link to bta" in {
            element(Selectors.btaBreadcrumb).attr("href") shouldBe "bta-url"
          }

          "have the text 'Your VAT account'" in {
            elementText(Selectors.vatBreadcrumb) shouldBe "Your VAT account"
          }

          "link to the VAT overview page" in {
            element(Selectors.vatBreadcrumb).attr("href") shouldBe vatDetailsUrl
          }

          "have the text 'What you owe'" in {
            elementText(Selectors.openPaymentsBreadcrumb) shouldBe "What you owe"
          }

          "link to the what you owe page" in {
            element(Selectors.openPaymentsBreadcrumb).attr("href") shouldBe whatYouOweUrl
          }
        }

        "have the correct first column in the first line" in {
          elementText(Selectors.dueDateKey) shouldBe "Due date"
        }

        "display the correct due date for the charge" in {
          elementText(Selectors.dueDateValue) shouldBe "8 April 2021"
        }

        "have the correct first column in the second line" in {
          elementText(Selectors.chargeDueKey) shouldBe "Charge due"
        }

        "display the correct total amount due" in {
          elementText(Selectors.chargeDueValue) shouldBe "£3,333.33"
        }

        "have the correct first column in the third line" in {
          elementText(Selectors.clearedAmountKey) shouldBe "Amount received"
        }

        "display the correct cleared amount" in {
          elementText(Selectors.clearedAmountValue) shouldBe "£2,222.22"
        }

        "have the correct first column in the fourth line" in {
          elementText(Selectors.outstandingAmountKey) shouldBe "Amount left to pay"
        }

        "display the correct outstanding amount" in {
          elementText(Selectors.outstandingAmountValue) shouldBe "£1,111.11"
        }

        "have a button" which {

          "has the correct button text" in {
            elementText(Selectors.button) shouldBe "Pay now"
          }

          "has the correct href location" in {
            element(Selectors.button).attr("href") shouldBe "/paymentPageRedirect"
          }
        }

        "have a link to the What you owe page" which {

          "has the correct link text" in {
            elementText(Selectors.whatYouOweLink) shouldBe "Return to what you owe"
          }

          "has the correct href" in {
            element(Selectors.whatYouOweLink).attr("href") shouldBe whatYouOweUrl
          }
        }
      }

      "the charge is overdue" should {

        lazy val view = {
          chargeTypeDetailsView(whatYouOweChargeOverdue, Html(""))(request, messages, mockConfig, user)
        }
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "display the overdue label" in {
          elementText(Selectors.dueDateValue) shouldBe "8 April 2021 overdue"
        }
      }
    }

    "the user has a cleared amount and no period for the charge" should {

      lazy val view = {
        chargeTypeDetailsView(whatYouOweChargeNoPeriod, Html(""))(request, messages, mockConfig, user)
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "not have a period caption" in {
        elementExtinct(Selectors.caption)
      }
    }

    "the user only has the periodFrom field but not the periodTo field" should {

      lazy val view = {
        chargeTypeDetailsView(whatYouOweChargeNoPeriodTo, Html(""))(request, messages, mockConfig, user)
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "not have a period caption" in {
        elementExtinct(Selectors.caption)
      }
    }

    "the user only has the periodTo field but not the periodFrom field" should {

      lazy val view = {
        chargeTypeDetailsView(whatYouOweChargeNoPeriodFrom, Html(""))(request, messages, mockConfig, user)
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "not have a period caption" in {
        elementExtinct(Selectors.caption)
      }
    }

    "the user has no cleared amount" should {

      lazy val view = {
        chargeTypeDetailsView(whatYouOweChargeNoClearedAmount, Html(""))(request, messages, mockConfig, user)
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct first column in the third line" in {
        elementText(Selectors.clearedAmountKey) shouldBe "Amount received"
      }

      "display 0 as the cleared amount" in {
        elementText(Selectors.clearedAmountValue) shouldBe "£0"
      }
    }
  }

  "Rendering the Charge Type Details page for an agent" should {

    lazy val view = {
      chargeTypeDetailsView(whatYouOweCharge, Html(""))(request, messages, mockConfig, agentUser)
    }
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "not render breadcrumbs" in {
      elementExtinct(Selectors.breadcrumbs)
    }

    "have a backLink" which {

      "has the text Back" in {
        elementText(Selectors.backLink) shouldBe "Back"
      }

      "has the correct href" in {
        element(Selectors.backLink).attr("href") shouldBe whatYouOweUrl
      }
    }

    "not have the make payment button" in {
      elementExtinct(Selectors.button)
    }

    "have a link to the What your client owes page" which {

      "has the correct link text" in {
        elementText(Selectors.whatYouOweLink) shouldBe "Return to what your client owes"
      }

      "has the correct href" in {
        element(Selectors.whatYouOweLink).attr("href") shouldBe whatYouOweUrl
      }
    }
  }
}
