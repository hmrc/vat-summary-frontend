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

import models.User
import models.viewModels.OpenPaymentsModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class OpenPaymentsViewSpec extends ViewBaseSpec {

  object Selectors {
    val pageHeading = "h1"
    val paymentLink = "#payments a"
    val btaBreadcrumb = "div.breadcrumbs li:nth-of-type(1)"
    val btaBreadcrumbLink = "div.breadcrumbs li:nth-of-type(1) a"
    val vatBreadcrumb = "div.breadcrumbs li:nth-of-type(2)"
    val vatBreadcrumbLink = "div.breadcrumbs li:nth-of-type(2) a"
    val paymentBreadcrumb = "div.breadcrumbs li:nth-of-type(3)"

    val paymentSectionFirst = "#payment-section-1"
    val paymentSectionSecond = "#payment-section-2"
    val firstPaymentAmount = "#payment-section-1 span:nth-of-type(1)"
    val firstPaymentAmountData = "#payment-section-1 span[data-amount]"
    val secondPaymentAmount = "#payment-section-2 span:nth-of-type(1)"
    val secondPaymentAmountData = "#payment-section-2 span[data-amount]"
    val firstPaymentDue = "#payment-row-1 span:nth-of-type(1)"
    val firstPaymentDueData = "#payment-row-1 span[data-due]"
    val secondPaymentDue = "#payment-row-2 span:nth-of-type(1)"
    val secondPaymentDueData = "#payment-row-2 span[data-due]"
    val firstPaymentPay = "#payment-row-1 input:nth-of-type(1)"
    val secondPaymentPay = "#payment-row-2 input:nth-of-type(1)"
    val firstPaymentPeriod = "#period-row-1 span:nth-of-type(1)"
    val secondPaymentPeriod = "#period-row-2 span:nth-of-type(1)"
    lazy val firstPaymentViewReturn = "#period-row-1 a:nth-of-type(1)"
    lazy val secondPaymentViewReturn = "#period-row-2 a:nth-of-type(1)"
    val processingTime = "#payments-information p:nth-of-type(1)"
    val directDebit = "#direct-debits"
    val directDebitCheckFullText = "#check-direct-debit p:nth-of-type(1)"
    val directDebitCheckLink = "#check-direct-debit a:nth-of-type(1)"
    val helpTwentyFourHours = "details p:nth-of-type(1)"
    val helpMakePayment = "details p:nth-of-type(2)"
    val helpSummaryRevealLink = "summary span:nth-of-type(1)"
    val overdueLabel = ".task-overdue"
  }

  private val user = User("1111")
  val noPayment = Seq()
  val payment = Seq(
    OpenPaymentsModel(
      "Return",
      543.21,
      LocalDate.parse("2000-04-08"),
      LocalDate.parse("2000-01-01"),
      LocalDate.parse("2000-03-31"),
      "#001",
      overdue = true
    ),
    OpenPaymentsModel(
      "Return",
      100.00,
      LocalDate.parse("2000-05-10"),
      LocalDate.parse("2000-02-01"),
      LocalDate.parse("2000-03-28"),
      "#002"
    )
  )

  "Rendering the open payments page" should {

    lazy val view = views.html.payments.openPayments(user, payment)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "What you owe"
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "What you owe"
    }

    "render breadcrumbs which" should {

      "have the text 'Business tax account'" in {
        elementText(Selectors.btaBreadcrumb) shouldBe "Business tax account"
      }

      "link to bta" in {
        element(Selectors.btaBreadcrumbLink).attr("href") shouldBe "bta-url"
      }

      "have the text 'VAT'" in {
        elementText(Selectors.vatBreadcrumb) shouldBe "VAT"
      }

      s"link to ${controllers.routes.VatDetailsController.details().url}" in {
        element(Selectors.vatBreadcrumbLink).attr("href") shouldBe controllers.routes.VatDetailsController.details().url
      }

      "have the text 'What you owe'" in {
        elementText(Selectors.paymentBreadcrumb) shouldBe "What you owe"
      }
    }

    "render the correct amount for the first payment" in {
      elementText(Selectors.firstPaymentAmount) shouldBe "£543.21"
    }

    "render the correct amount for the first payment amount data attribute" in {
      elementText(Selectors.firstPaymentAmountData) shouldBe "£543.21"
    }

    "render the correct due period for the first payment" in {
      elementText(Selectors.firstPaymentDue) shouldBe "due by 8 April 2000"
    }

    "render the correct due period for the first payment period data attribute" in {
      elementText(Selectors.firstPaymentDueData) shouldBe "due by 8 April 2000"
    }

    "render the correct button text for the first payment" in {
      element(Selectors.firstPaymentPay).attr("value") shouldBe "Pay now"
    }

    "render the correct due period for the first payment period" in {
      elementText(Selectors.firstPaymentPeriod) shouldBe "for the period 1 January to 31 March 2000"
    }

    "render the correct view return link text for the first payment" in {
      elementText(Selectors.firstPaymentViewReturn) shouldBe "View return"
    }

    "render the correct view return href for the first payment" in {
      element(Selectors.firstPaymentViewReturn).attr("href") shouldBe "/submitted/%23001"
    }

    "render the correct amount for the second payment" in {
      elementText(Selectors.secondPaymentAmount) shouldBe "£100"
    }

    "render the correct amount for the second payment amount data attribute" in {
      elementText(Selectors.secondPaymentAmountData) shouldBe "£100"
    }

    "render the correct due period for the second payment" in {
      elementText(Selectors.secondPaymentDue) shouldBe "due by 10 May 2000"
    }

    "render the correct due period for the second payment period data attribute" in {
      elementText(Selectors.secondPaymentDueData) shouldBe "due by 10 May 2000"
    }

    "render the correct button text for the second payment" in {
      element(Selectors.secondPaymentPay).attr("value") shouldBe "Pay now"
    }

    "render the correct due period for the second payment period" in {
      elementText(Selectors.secondPaymentPeriod) shouldBe "for the period 1 February to 28 March 2000"
    }

    "render the correct view return link text for the second payment" in {
      elementText(Selectors.secondPaymentViewReturn) shouldBe "View return"
    }

    "render the correct view return href for the second payment" in {
      element(Selectors.secondPaymentViewReturn).attr("href") shouldBe "/submitted/%23002"
    }

    "render the correct text for the processing time" in {
      elementText(Selectors.processingTime) shouldBe "Your payment could take up to 5 days to process. You'll be fined if it's late."
    }

    "render the correct text for the direct debits" in {
      elementText(Selectors.directDebit) shouldBe "Direct debits"
    }

    "render the correct text for the direct debit paragraph" in {
      elementText(Selectors.directDebitCheckFullText) shouldBe
        "If you've already set up a direct debit, you don't need to pay now. You can check if you've set up a direct debit if you're not sure."
    }

    "render the correct check direct debit link text" in {
      elementText(Selectors.directDebitCheckLink) shouldBe "check if you've set up a direct debit"
    }

    "render the correct check direct debit href" in {
      element(Selectors.directDebitCheckLink).attr("href") shouldBe "#"
    }

    "render the correct help revealing link text" in {
      elementText(Selectors.helpSummaryRevealLink) shouldBe "What I owe isn't shown"
    }

    "render the correct 24 hour help text" in {
      elementText(Selectors.helpTwentyFourHours) shouldBe "It can take up to 24 hours to appear after you've submitted your return."
    }

    "render the correct make payment help text" in {
      elementText(Selectors.helpMakePayment) shouldBe "You can still make a payment (opens in a new tab)."
    }

    "render the overdue label" in {
      elementText(Selectors.overdueLabel) shouldBe "overdue"
    }
  }
}
