/*
 * Copyright 2018 HM Revenue & Customs
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

import java.time.LocalDate

import models.viewModels.{PaymentsHistoryModel, PaymentsHistoryViewModel}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class PaymentHistoryViewSpec extends ViewBaseSpec {

  mockConfig.features.allowPaymentHistory(true)

  object Selectors {
    val pageHeading = "h1"
    val paymentLink = "#payments a"
    val btaBreadcrumb = "div.breadcrumbs li:nth-of-type(1)"
    val btaBreadcrumbLink = "div.breadcrumbs li:nth-of-type(1) a"
    val vatBreadcrumb = "div.breadcrumbs li:nth-of-type(2)"
    val vatBreadcrumbLink = "div.breadcrumbs li:nth-of-type(2) a"
    val paymentHistoryBreadcrumb = "div.breadcrumbs li:nth-of-type(3)"
    val tabOne = ".tabs-nav li:nth-of-type(1)"
    val tabTwo = ".tabs-nav li:nth-of-type(2)"
    val tabOneHiddenText = ".tabs-nav li:nth-of-type(1) span"
    val tabTwoHiddenText = ".tabs-nav li:nth-of-type(2) span"
    val tabHeading = "h2"
    val paymentDateTableHeading = "tr th:nth-of-type(1)"
    val descriptionTableHeading = "tr th:nth-of-type(2)"
    val amountPaidTableHeading = "tr th:nth-of-type(3)"
    val paymentDateTableContent = "tr td:nth-of-type(1)"
    val descriptionTableContent = "tr td:nth-of-type(2)"
    val amountPaidTableContent = "tr td:nth-of-type(3)"
    val noHistoryContent = "div.column-two-thirds p"
  }

  "Rendering the open payments page" when {

    val historyYears = Seq(2018, 2017)

    "there are multiple payment histories to display" should {

      val paymentHistoryModel: PaymentsHistoryViewModel = PaymentsHistoryViewModel(
        historyYears,
        historyYears.head,
        Seq(PaymentsHistoryModel(
          taxPeriodFrom = LocalDate.parse(s"2018-01-01"),
          taxPeriodTo = LocalDate.parse(s"2018-02-01"),
          amount = 123456789,
          clearedDate = LocalDate.parse(s"2018-03-01")
        ),
          PaymentsHistoryModel(
            taxPeriodFrom = LocalDate.parse(s"2018-03-01"),
            taxPeriodTo = LocalDate.parse(s"2018-04-01"),
            amount = 987654321,
            clearedDate = LocalDate.parse(s"2018-03-01")
          ))
      )

      lazy val view = views.html.payments.paymentHistory(paymentHistoryModel)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "Payment history"
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe "Payment history"
      }

      "render breadcrumbs which" should {

        "have the text 'Business tax account'" in {
          elementText(Selectors.btaBreadcrumb) shouldBe "Business tax account"
        }

        "link to bta" in {
          element(Selectors.btaBreadcrumbLink).attr("href") shouldBe "bta-url"
        }

        "have the text 'Your VAT details'" in {
          elementText(Selectors.vatBreadcrumb) shouldBe "Your VAT details"
        }

        s"link to ${controllers.routes.VatDetailsController.details().url}" in {
          element(Selectors.vatBreadcrumbLink).attr("href") shouldBe controllers.routes.VatDetailsController.details().url
        }

        "have the text 'Payment history'" in {
          elementText(Selectors.paymentHistoryBreadcrumb) shouldBe "Payment history"
        }
      }

      "have tabs for each return year" should {

        "tab one" should {

          "have the text '2018'" in {
            elementText(Selectors.tabOne) should include("2018")
          }

          "contain visually hidden text" in {
            elementText(Selectors.tabOneHiddenText) shouldBe "Currently viewing payment history from 2018"
          }
        }

        "tab two" should {

          "have the text '2017'" in {
            elementText(Selectors.tabTwo) should include("2017")
          }

          s"contain a link to ${controllers.routes.PaymentHistoryController.paymentHistory(2017).url}" in {
            element(Selectors.tabTwo).select("a").attr("href") shouldBe
              controllers.routes.PaymentHistoryController.paymentHistory(2017).url
          }

          "contain visually hidden text" in {
            elementText(Selectors.tabTwoHiddenText) shouldBe "View payment history from 2017"
          }
        }
      }


      "have the correct tab heading" in {
        elementText(Selectors.tabHeading) shouldBe "2018"
      }

      "have the correct payment date table heading" in {
        elementText(Selectors.paymentDateTableHeading) shouldBe "Payment date"
      }

      "have the correct description table heading" in {
        elementText(Selectors.descriptionTableHeading) shouldBe "Description"
      }

      "have the correct amount paid table heading" in {
        elementText(Selectors.amountPaidTableHeading) shouldBe "Amount paid"
      }

      "have the correct payment date table content" in {
        elementText(Selectors.paymentDateTableContent) shouldBe "1 March 2018"
      }

      "have the correct description table content" in {
        elementText(Selectors.descriptionTableContent) shouldBe "1 Jan to 1 Feb 2018 return"
      }

      "have the correct amount paid table content" in {
        elementText(Selectors.amountPaidTableContent) shouldBe "£123,456,789"
      }
    }

    "there are no payment histories for the selected year" should {

      val paymentHistoryModel: PaymentsHistoryViewModel = PaymentsHistoryViewModel(
        historyYears,
        historyYears.head,
        Seq.empty
      )

      lazy val view = views.html.payments.paymentHistory(paymentHistoryModel)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "Payment history"
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe "Payment history"
      }

      "render breadcrumbs which" should {

        "have the text 'Business tax account'" in {
          elementText(Selectors.btaBreadcrumb) shouldBe "Business tax account"
        }

        "link to bta" in {
          element(Selectors.btaBreadcrumbLink).attr("href") shouldBe "bta-url"
        }

        "have the text 'Your VAT details'" in {
          elementText(Selectors.vatBreadcrumb) shouldBe "Your VAT details"
        }

        s"link to ${controllers.routes.VatDetailsController.details().url}" in {
          element(Selectors.vatBreadcrumbLink).attr("href") shouldBe controllers.routes.VatDetailsController.details().url
        }

        "have the text 'Payment history'" in {
          elementText(Selectors.paymentHistoryBreadcrumb) shouldBe "Payment history"
        }
      }

      "have tabs for each return year" should {

        "tab one" should {

          "have the text '2018'" in {
            elementText(Selectors.tabOne) should include("2018")
          }

          "contain visually hidden text" in {
            elementText(Selectors.tabOneHiddenText) shouldBe "Currently viewing payment history from 2018"
          }
        }

        "tab two" should {

          "have the text '2017'" in {
            elementText(Selectors.tabTwo) should include("2017")
          }

          s"contain a link to ${controllers.routes.PaymentHistoryController.paymentHistory(2017).url}" in {
            element(Selectors.tabTwo).select("a").attr("href") shouldBe
              controllers.routes.PaymentHistoryController.paymentHistory(2017).url
          }

          "contain visually hidden text" in {
            elementText(Selectors.tabTwoHiddenText) shouldBe "View payment history from 2017"
          }
        }
      }


      "have the correct tab heading" in {
        elementText(Selectors.tabHeading) shouldBe "2018"
      }

      "have the correct content" in {
        elementText(Selectors.noHistoryContent) shouldBe "You haven’t made or received any payments for 2018 yet."
      }
    }
  }
}
