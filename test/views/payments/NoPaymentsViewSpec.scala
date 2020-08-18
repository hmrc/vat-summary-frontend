/*
 * Copyright 2020 HM Revenue & Customs
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
    val directDebitMessage = "#noPaymentsDetail p:nth-of-type(2)"
    val directDebitLink = "#noPaymentsDetail p:nth-of-type(2) a"
    val btaBreadcrumb = "div.breadcrumbs li:nth-of-type(1)"
    val btaBreadcrumbLink = "div.breadcrumbs li:nth-of-type(1) a"
    val vatBreadcrumb = "div.breadcrumbs li:nth-of-type(2)"
    val vatBreadcrumbLink = "div.breadcrumbs li:nth-of-type(2) a"
    val paymentBreadcrumb = "div.breadcrumbs li:nth-of-type(3)"
    val covidPartialLine1 = "div.grid-row.form-group.flex-container > div > div > ul > li:nth-child(1)"
    val covidPartialLine2 = "div.grid-row.form-group.flex-container > div > div > ul > li:nth-child(2)"
  }

  override val user: User = User("123456789")

  "Rendering the no payments page" when {

    "the user has a direct debit" should {

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

      "have the correct message regarding viewing a direct debit" in {
        elementText(Selectors.directDebitMessage) shouldBe "You can also view your Direct Debit details (opens in a new tab)."
      }

      "have the correct link text regarding viewing a direct debit" in {
        elementText(Selectors.directDebitLink) shouldBe "view your Direct Debit details (opens in a new tab)"
      }

      "have the correct link destination regarding viewing a direct debit" in {
        element(Selectors.directDebitLink).attr("href") shouldBe "/vat-through-software/direct-debit?status=true"
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

    "the user does not have a direct debit" should {

      lazy val view = noPaymentsView(user,  hasDirectDebit = Some(false))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct message regarding setting up a direct debit" in {
        elementText(Selectors.directDebitMessage) shouldBe
          "You can also set up a Direct Debit (opens in a new tab) for your VAT Returns."
      }

      "have the correct link text regarding setting up a direct debit" in {
        elementText(Selectors.directDebitLink) shouldBe "set up a Direct Debit (opens in a new tab)"
      }

      "have the correct link destination regarding setting up a direct debit" in {
        element(Selectors.directDebitLink).attr("href") shouldBe "/vat-through-software/direct-debit?status=false"
      }

    }

    "the call to the direct debit service fails" should {

      lazy val view = noPaymentsView(user, hasDirectDebit = None)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "not display a direct debit message" in {
        intercept[org.scalatest.exceptions.TestFailedException](element(Selectors.directDebitMessage))
      }
    }
  }

  "Rendering the no payments page" should {

    "display the covid message" when {

      "the display covid feature switch is on" should  {
        mockConfig.features.displayCovidMessage(true)

        lazy val view = noPaymentsView(user, hasDirectDebit = Some(true))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct first message" in {
          elementText(Selectors.covidPartialLine1) should include(CovidMessages.line1)
        }

        "have the correct second message" in {
          elementText(Selectors.covidPartialLine2) should include(CovidMessages.line2)
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
