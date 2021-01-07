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

import java.time.LocalDate

import models.payments._
import models.viewModels.{PaymentsHistoryModel, PaymentsHistoryViewModel}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.PaymentHistory

class PaymentHistoryViewSpec extends ViewBaseSpec {

  val paymentHistoryView: PaymentHistory = injector.instanceOf[PaymentHistory]

  object Selectors {
    val pageHeading = "h1"
    val btaBreadcrumb = "div.breadcrumbs li:nth-of-type(1)"
    val btaBreadcrumbLink = "div.breadcrumbs li:nth-of-type(1) a"
    val vatBreadcrumb = "div.breadcrumbs li:nth-of-type(2)"
    val vatBreadcrumbLink = "div.breadcrumbs li:nth-of-type(2) a"
    val paymentHistoryBreadcrumb = "div.breadcrumbs li:nth-of-type(3)"
    val tabOne = "#content > article > div.grid-row > div > div > ul > li:nth-child(1) > a"
    val tabTwo = "#content > article > div.grid-row > div > div > ul > li:nth-child(2) > a"
    val tabThree = "#content > article > div.grid-row > div > div > ul > li:nth-child(3) > a"
    val tabFour = "#content > article > div.grid-row > div > div > ul > li:nth-child(4) > a"
    val currentYearSubheading = "#2018 > h2"
    val previousYearSubheading = "#2017 > h2"
    val previousYearNoPayments = "#2017 > p"
    val prevPaymentsSubheading = "#previous-payments > h2"
    val prevPaymentsParagraph = "#previous-payments > p"
    val prevPaymentsLink: String = prevPaymentsParagraph + " > a"
    val paymentDateTableHeading = "tr th:nth-of-type(1) div"
    val paymentDateTableContent = "tr td:nth-of-type(1)"
    val descriptionTableChargeType = "tr td:nth-of-type(2) span.bold"
    val descriptionTableContent = "tr td:nth-of-type(2) span:nth-of-type(2)"
    val amountPaidTableContent = "tr td:nth-of-type(3)"
  }

  val currentYear = 2018
  val exampleAmount = 100

  val model: PaymentsHistoryViewModel = PaymentsHistoryViewModel(
    currentYear,
    Some(currentYear - 1),
    Some(currentYear - 2),
    previousPaymentsTab = true,
    Seq(PaymentsHistoryModel(
      chargeType = ReturnDebitCharge,
      taxPeriodFrom = Some(LocalDate.parse(s"2018-01-01")),
      taxPeriodTo = Some(LocalDate.parse(s"2018-02-01")),
      amount = exampleAmount,
      clearedDate = Some(LocalDate.parse(s"2018-03-01"))
    ))
  )

  "The payments history page" should {

    lazy val view: Html = paymentHistoryView(model)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Payment history - Business tax account - GOV.UK"
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "Payment history"
    }

    "render breadcrumbs" which {

      "have the text 'Business tax account'" in {
        elementText(Selectors.btaBreadcrumb) shouldBe "Business tax account"
      }

      "link to bta" in {
        element(Selectors.btaBreadcrumbLink).attr("href") shouldBe "bta-url"
      }

      "have the text 'Your VAT account'" in {
        elementText(Selectors.vatBreadcrumb) shouldBe "Your VAT account"
      }

      s"link to ${controllers.routes.VatDetailsController.details().url}" in {
        element(Selectors.vatBreadcrumbLink).attr("href") shouldBe controllers.routes.VatDetailsController.details().url
      }

      "have the text 'Payment history'" in {
        elementText(Selectors.paymentHistoryBreadcrumb) shouldBe "Payment history"
      }
    }

    "display a current year tab" in {
      elementText(Selectors.tabOne) shouldBe currentYear.toString
    }

    "display a previous year tab" in {
      elementText(Selectors.tabTwo) shouldBe (currentYear - 1).toString
    }

    "display a tab for 2 years ago" in {
      elementText(Selectors.tabThree) shouldBe (currentYear - 2).toString
    }

    "display a previous payments tab" in {
      elementText(Selectors.tabFour) shouldBe "Previous payments"
    }

    "have the current year subheading" in {
      elementText(Selectors.currentYearSubheading) shouldBe currentYear.toString
    }

    "have the previous year subheading" in {
      elementText(Selectors.previousYearSubheading) shouldBe (currentYear - 1).toString
    }

    "have the previous payments subheading" in {
      elementText(Selectors.prevPaymentsSubheading) shouldBe "Previous payments"
    }

    "have the correct current year tab content" which {

      "has the correct amount in the charge row" in {
        elementText(Selectors.amountPaidTableContent) shouldBe "- £100"
      }

      "has the correct title in the charge row" in {
        elementText(Selectors.descriptionTableChargeType) shouldBe "Return"
      }

      "has the correct description in the charge row" in {
        elementText(Selectors.descriptionTableContent) shouldBe "for the period 1 Jan to 1 Feb 2018"
      }

      "has the correct date in the charge row" in {
        elementText(Selectors.paymentDateTableContent) shouldBe "1 Mar 2018"
      }
    }

    "have the correct previous year tab content" in {
      elementText(Selectors.previousYearNoPayments) shouldBe
        "You have not made or received any payments using the new VAT service this year."
    }

    "have the correct previous payments tab content" which {

      "has the correct message" in {
        elementText(Selectors.prevPaymentsParagraph) shouldBe "You can view your previous payments " +
          "(opens in new tab) if you made payments before joining Making Tax Digital."
      }

      "has a link to the old VAT portal" which {

        "has the correct link text" in {
          elementText(Selectors.prevPaymentsLink) shouldBe "view your previous payments (opens in new tab)"
        }

        "has the correct link href" in {
          element(Selectors.prevPaymentsLink).attr("href") shouldBe
            mockConfig.portalNonHybridPreviousPaymentsUrl(user.vrn)
        }
      }
    }
  }
}
