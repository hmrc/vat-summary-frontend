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
    val paymentDateTableHeading = "tr th:nth-of-type(1) div"
    val descriptionTableHeading = "tr th:nth-of-type(2) div"
    val amountPaidTableHeading = "tr th:nth-of-type(3) div"
    val paymentDateTableContent = "tr td:nth-of-type(1)"
    val descriptionTableChargeType = "tr td:nth-of-type(2) span.bold"
    val descriptionTableContent = "tr td:nth-of-type(2) span:nth-of-type(2)"
    val amountPaidTableContent = "tr td:nth-of-type(3)"
    val noHistoryContent = "div.column-two-thirds p:nth-of-type(1)"
    val noHistoryWillShowContent = "div.column-two-thirds p:nth-of-type(2)"
    val noHistoryBullet1 = "div.column-two-thirds li:nth-of-type(1)"
    val noHistoryBullet2 = "div.column-two-thirds li:nth-of-type(2)"
  }

  "Rendering the payments history page" when {

    val currentYear = 2018
    val previousYear = 2017
    val historyYears = Seq(currentYear, previousYear)

    "there are multiple payment histories to display" should {

      val paymentHistoryModel: PaymentsHistoryViewModel = PaymentsHistoryViewModel(
        historyYears,
        historyYears.head,
        Seq(PaymentsHistoryModel(
          chargeType = "VAT Return Debit Charge",
          taxPeriodFrom = Some(LocalDate.parse(s"2018-01-01")),
          taxPeriodTo = Some(LocalDate.parse(s"2018-02-01")),
          amount = 123456789,
          clearedDate = Some(LocalDate.parse(s"2018-03-01"))
        ),
          PaymentsHistoryModel(
            chargeType = "VAT Return Debit Charge",
            taxPeriodFrom = Some(LocalDate.parse(s"2018-03-01")),
            taxPeriodTo = Some(LocalDate.parse(s"2018-04-01")),
            amount = 987654321,
            clearedDate = Some(LocalDate.parse(s"2018-03-01"))
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

          s"contain a link to ${controllers.routes.PaymentHistoryController.paymentHistory(previousYear).url}" in {
            element(Selectors.tabTwo).select("a").attr("href") shouldBe
              controllers.routes.PaymentHistoryController.paymentHistory(previousYear).url
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
        elementText(Selectors.paymentDateTableHeading) shouldBe "Payment received"
      }

      "have the correct description table heading" in {
        elementText(Selectors.descriptionTableHeading) shouldBe "Description"
      }

      "have the correct amount paid table heading" in {
        elementText(Selectors.amountPaidTableHeading) shouldBe "Amount"
      }

      "have the visuallyhidden attribute on the payment date table heading" in {
        element(Selectors.paymentDateTableHeading).attr("class") shouldBe "visuallyhidden"
      }

      "have the visuallyhidden attribute on the description table heading" in {
        element(Selectors.descriptionTableHeading).attr("class") shouldBe "visuallyhidden"
      }

      "have the visuallyhidden attribute on the amount paid table heading" in {
        element(Selectors.amountPaidTableHeading).attr("class") shouldBe "visuallyhidden"
      }

      "have the correct payment date table content" in {
        elementText(Selectors.paymentDateTableContent) shouldBe "1 Mar 2018"
      }

      "have the correct description table charge type" in {
        elementText(Selectors.descriptionTableChargeType) shouldBe "Return"
      }
      
      "have the correct description table content" in {
        elementText(Selectors.descriptionTableContent) shouldBe "for the period 1 Jan to 1 Feb 2018"
      }

      "have the correct amount paid table content" in {
        elementText(Selectors.amountPaidTableContent) shouldBe "- £123,456,789"
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
    }

    "there is no payment history for any year" should {

      val paymentHistoryModel: PaymentsHistoryViewModel = PaymentsHistoryViewModel(
        Seq.empty,
        currentYear,
        Seq.empty
      )

      lazy val view = views.html.payments.paymentHistory(paymentHistoryModel)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display no first year tab" in {

        val thrown = intercept[Exception] {
          elementText(Selectors.tabOne)
        }
        thrown.getMessage should startWith("No element exists with the selector")
      }

      "display no second year tab" in {

        val thrown = intercept[Exception] {
          elementText(Selectors.tabTwo)
        }
        thrown.getMessage should startWith("No element exists with the selector")
      }

      "show the no history lead content" in {

        elementText(Selectors.noHistoryContent) shouldBe "You have not made or received any payments yet."
      }

      "show the your history will show content" in {

        elementText(Selectors.noHistoryWillShowContent) shouldBe "Your payment history will show here once you have:"
      }

      "show the your history will show content first bullet" in {

        elementText(Selectors.noHistoryBullet1) shouldBe "paid a VAT Return or made any other VAT payments"
      }

      "show the your history will show content second bullet" in {

        elementText(Selectors.noHistoryBullet2) shouldBe "received any VAT repayments"
      }
    }
  }
}
