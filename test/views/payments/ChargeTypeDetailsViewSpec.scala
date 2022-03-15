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
    val btaBreadcrumb = "li.govuk-breadcrumbs__list-item > a"
    val vatBreadcrumb = "li.govuk-breadcrumbs__list-item:nth-child(2) > a"
    val openPaymentsBreadcrumb = "li.govuk-breadcrumbs__list-item:nth-child(3) > a"
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

  "Rendering the Charge Type Details page" when {

    "the user has a cleared amount and a period for the charge" should {

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

        "have the text 'VAT'" in {
          elementText(Selectors.vatBreadcrumb) shouldBe "Your VAT account"
        }

        s"link to ${controllers.routes.VatDetailsController.details.url}" in {
          element(Selectors.vatBreadcrumb).attr("href") shouldBe controllers.routes.VatDetailsController.details.url
        }
      }

      "have the correct first column in the first line" in {
        elementText(Selectors.dueDateKey) shouldBe "Due date"
      }

      "display the correct due date for the charge" in {
        elementText(Selectors.dueDateValue) shouldBe "2021-04-08"
      }

      "have the correct first column in the second line" in {
        elementText(Selectors.chargeDueKey) shouldBe "Charge due"
      }

      "display the correct total amount due" in {
        elementText(Selectors.chargeDueValue) shouldBe "3333.33"
      }

      "have the correct first column in the third line" in {
        elementText(Selectors.clearedAmountKey) shouldBe "Amount received"
      }

      "display the correct cleared amount" in {
        elementText(Selectors.clearedAmountValue) shouldBe "2222.22"
      }

      "have the correct first column in the fourth line" in {
        elementText(Selectors.outstandingAmountKey) shouldBe "Amount left to pay"
      }

      "display the correct outstanding amount" in {
        elementText(Selectors.outstandingAmountValue) shouldBe "1111.11"
      }

      "have a button" which {

        "has the correct button text" in {
          elementText(Selectors.button) shouldBe "Pay now"
        }

        "has the correct href location" in {
          element(Selectors.button).attr("href") shouldBe "unauthenticated-payments-url"
        }
      }

      "have a link to the What you owe page" which {

        "has the correct link text" in {
          elementText(Selectors.whatYouOweLink) shouldBe "Return to what you owe"
        }

        "has the correct href" in {
          element(Selectors.whatYouOweLink).attr("href") shouldBe "#"
          //TODO: add correct link location once this page and the What you owe page have been wired up
        }
      }
    }
  }
}
