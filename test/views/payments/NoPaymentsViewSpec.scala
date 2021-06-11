/*
 * Copyright 2021 HM Revenue & Customs
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


import common.MessageLookup.CovidMessages
import models.User
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.payments.NoPayments

class NoPaymentsViewSpec extends ViewBaseSpec {

  val noPaymentsView: NoPayments = injector.instanceOf[NoPayments]
  object Selectors {
    val pageHeading = "h1"
    val secondaryHeading = "h2"
    val noPaymentsDetail = "#noPaymentsDetail p:nth-of-type(1)"
    val paymentLink = "#noPaymentsDetail p:nth-of-type(1) a"
    val btaBreadcrumb = "div.govuk-breadcrumbs li:nth-of-type(1)"
    val btaBreadcrumbLink = "div.govuk-breadcrumbs li:nth-of-type(1) a"
    val vatBreadcrumb = "div.govuk-breadcrumbs li:nth-of-type(2)"
    val vatBreadcrumbLink = "div.govuk-breadcrumbs li:nth-of-type(2) a"
    val paymentBreadcrumb = "div.govuk-breadcrumbs li:nth-of-type(3)"
    val covidHeading = ".govuk-warning-text__text"
    val covidPartialLine1 = "div.govuk-inset-text > p:nth-of-type(1)"
    val covidPartialLine1Link = "div.govuk-inset-text > p > a"
    val covidPartialLine2 = "div.govuk-inset-text > p:nth-of-type(2)"
  }

  override val user: User = User("123456789")

  "Rendering the no payments page" when {

      lazy val view = noPaymentsView(user, hasDirectDebit = Some(true))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "What you owe - Business tax account - GOV.UK"
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe "What you owe"
      }

      "have the correct secondary heading" in {
        elementText(Selectors.secondaryHeading) shouldBe "You do not owe anything right now."
      }

      "have the correct context information" in {
        elementText(Selectors.noPaymentsDetail) shouldBe
          "If you have submitted a return and need to pay VAT, it can take up to 24 hours to see what you owe." +
          " You can still make a payment (opens in a new tab)."
      }

      "have the correct make a payment link text" in {
        elementText(Selectors.paymentLink) shouldBe "make a payment (opens in a new tab)"
      }

      "have the correct href" in {
        element(Selectors.paymentLink).attr("href") shouldBe "unauthenticated-payments-url"
      }

      "render breadcrumbs which" should {

        "have the text 'Business tax account'" in {
          elementText(Selectors.btaBreadcrumb) shouldBe "Business tax account"
        }

        "link to bta" in {
          element(Selectors.btaBreadcrumbLink).attr("href") shouldBe "bta-url"
        }

        "have the text 'VAT'" in {
          elementText(Selectors.vatBreadcrumb) shouldBe "Your VAT account"
        }

        s"link to ${controllers.routes.VatDetailsController.details().url}" in {
          element(Selectors.vatBreadcrumbLink).attr("href") shouldBe controllers.routes.VatDetailsController.details().url
        }

        "have the text 'What you owe'" in {
          elementText(Selectors.paymentBreadcrumb) shouldBe "What you owe"
        }
      }
    }

  "Rendering the no payments page" should {

    "display the covid message" when {

      "the display covid feature switch is on" should  {
        mockConfig.features.displayCovidMessage(true)

        lazy val view = noPaymentsView(user, hasDirectDebit = Some(true))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct warning heading" in {
          elementText(Selectors.covidHeading) shouldBe CovidMessages.heading
        }

        "have the correct first message" which {

          "has the correct text" in {
            elementText(Selectors.covidPartialLine1) shouldBe CovidMessages.line1
          }

          "has the correct link text" in {
            elementText(Selectors.covidPartialLine1Link) shouldBe CovidMessages.line1LinkText
          }

          "has the correct link destination" in {
            element(Selectors.covidPartialLine1Link).attr("href") shouldBe mockConfig.govUkVatDeferralUrl
          }
        }

        "have the correct second message" in {
          elementText(Selectors.covidPartialLine2) shouldBe CovidMessages.line2
        }
      }
    }

    "not display the covid message" when {

      "the display covid feature switch is off" in {
        mockConfig.features.displayCovidMessage(false)

        lazy val view = noPaymentsView(user, hasDirectDebit = Some(true))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        elementExtinct(Selectors.covidPartialLine1)
        elementExtinct(Selectors.covidPartialLine2)
      }

    }

  }
}
