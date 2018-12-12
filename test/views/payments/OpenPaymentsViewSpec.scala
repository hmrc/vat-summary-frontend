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

import common.FinancialTransactionsConstants._
import models.User
import models.payments.OpenPaymentsModel
import models.viewModels.OpenPaymentsViewModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class OpenPaymentsViewSpec extends ViewBaseSpec {

  mockConfig.features.allowDirectDebits(true)

  object Selectors {
    val pageHeading = "h1"
    val paymentLink = "#payments a"
    val correctErrorLink = "details p:nth-of-type(1) a"
    val btaBreadcrumb = "div.breadcrumbs li:nth-of-type(1)"
    val btaBreadcrumbLink = "div.breadcrumbs li:nth-of-type(1) a"
    val vatBreadcrumb = "div.breadcrumbs li:nth-of-type(2)"
    val vatBreadcrumbLink = "div.breadcrumbs li:nth-of-type(2) a"
    val paymentBreadcrumb = "div.breadcrumbs li:nth-of-type(3)"
    val paymentSectionFirst = "#payment-section-1"
    val firstPaymentAmount = "#payment-section-1 span:nth-of-type(1)"
    val firstPaymentAmountData = "#payment-section-1 span[data-amount]"
    val firstPaymentDue = "#payment-row-1 span:nth-of-type(1)"
    val firstPaymentDueData = "#payment-row-1 span[data-due]"
    val firstPaymentPayLink = "#payment-row-1 a"
    val firstPaymentPayContext = "#payment-row-1 span.float--right"
    val firstPaymentPayNowLinkText = "#payment-row-1 div:nth-of-type(2) span:nth-of-type(1)"
    val firstPaymentPayNowContext = "#payment-row-1 div:nth-of-type(2) span:nth-of-type(2)"
    val paymentSectionSecond = "#payment-section-2"
    val secondPaymentAmount = "#payment-section-2 span:nth-of-type(1)"
    val secondPaymentAmountData = "#payment-section-2 span[data-amount]"
    val secondPaymentDue = "#payment-row-2 span:nth-of-type(1)"
    val secondPaymentDueData = "#payment-row-2 span[data-due]"
    val secondPaymentPayLink = "#payment-row-2 a"
    val secondPaymentPayNowLinkText = "#payment-row-2 div:nth-of-type(2) span:nth-of-type(1)"
    val secondPaymentPayContext = "#payment-row-2 div:nth-of-type(2) span:nth-of-type(2)"
    val thirdPaymentAmount = "#payment-section-3 span:nth-of-type(1)"
    val thirdPaymentAmountData = "#payment-section-3 span[data-amount]"
    val thirdPaymentDue = "#payment-row-3 span:nth-of-type(1)"
    val thirdPaymentDueData = "#payment-row-3 span[data-due]"
    val thirdPaymentPayLink = "#payment-row-3 a"
    val thirdPaymentPayContext = "#payment-row-3 span.float--right"
    val thirdPaymentPayNowLinkText = "#payment-row-3 div:nth-of-type(2) span:nth-of-type(1)"
    val thirdPaymentPayNowContext = "#payment-row-3 div:nth-of-type(2) span:nth-of-type(2)"
    lazy val firstPaymentViewReturnLink = "#links-section-1 div:nth-of-type(2) a"
    val firstPaymentPeriod = "#payment-row-1 span:nth-of-type(3)"
    val firstPaymentViewReturnText = "#links-section-1 div:nth-of-type(2) a span:nth-of-type(1)"
    val firstPaymentViewReturnContext = "#links-section-1 div:nth-of-type(2) a"
    lazy val secondPaymentViewReturnLink = "#links-section-2 div:nth-of-type(2) a"
    val secondPaymentViewReturnText = "#links-section-2 div:nth-of-type(2) a span:nth-of-type(1)"
    val secondPaymentViewReturnContext = "#links-section-2 div:nth-of-type(2) a"
    val secondPaymentPeriod = "#payment-row-2 span:nth-of-type(2)"
    lazy val thirdPaymentViewReturnLink = "#links-section-3 div:nth-of-type(2) a"
    val thirdPaymentPeriod = "#payment-row-3 span:nth-of-type(3)"
    val thirdPaymentViewReturnText = "#links-section-3 div:nth-of-type(2) a span:nth-of-type(1)"
    val thirdPaymentViewReturnContext = "#links-section-3 div:nth-of-type(2) a"
    val processingTime = "#processing-time"
    val processingTimeOld = "#processing-time-old"
    val directDebit = "#direct-debits"
    val directDebitText = "#check-direct-debit p:nth-of-type(1)"
    val directDebitLink = "#check-direct-debit a:nth-of-type(1)"
    val helpText = "details p:nth-of-type(1)"
    val helpMakePayment = "details p:nth-of-type(2)"
    val helpSummaryRevealLink = "summary span:nth-of-type(1)"
    val overdueLabel = ".task-overdue"
    val makePayment = "#vatPaymentsLink"
  }

  private val user = User("1111")
  val noPayment = Seq()
  val payments = Seq(
    OpenPaymentsModel(
      vatReturnDebitCharge,
      2000000000.01,
      LocalDate.parse("2001-04-08"),
      LocalDate.parse("2001-01-01"),
      LocalDate.parse("2001-03-31"),
      "#001",
      overdue = true
    ),
    OpenPaymentsModel(
      vatReturnDebitCharge,
      100.00,
      LocalDate.parse("2002-05-10"),
      LocalDate.parse("2002-02-01"),
      LocalDate.parse("2002-03-28"),
      "#002"
    ),
    OpenPaymentsModel(
      vatBNPofRegPre2010,
      250.00,
      LocalDate.parse("2017-04-05"),
      LocalDate.parse("2017-01-01"),
      LocalDate.parse("2017-03-31"),
      "#003"
    )
  )

  "Rendering the open payments page when a user has a direct debit" should {

    val hasDirectDebit = Some(true)
    val viewModel = OpenPaymentsViewModel(payments, hasDirectDebit)
    lazy val view = views.html.payments.openPayments(user, viewModel)
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
        elementText(Selectors.vatBreadcrumb) shouldBe "Your VAT details"
      }

      s"link to ${controllers.routes.VatDetailsController.details().url}" in {
        element(Selectors.vatBreadcrumbLink).attr("href") shouldBe controllers.routes.VatDetailsController.details().url
      }

      s"link to https://www.gov.uk/vat-corrections" in {
        element(Selectors.correctErrorLink).attr("href") shouldBe "https://www.gov.uk/vat-corrections"
      }

      "have the text 'What you owe'" in {
        elementText(Selectors.paymentBreadcrumb) shouldBe "What you owe"
      }
    }

    "render the correct amount for the first payment" in {
      elementText(Selectors.firstPaymentAmount) shouldBe "£2,000,000,000.01"
    }

    "render the correct amount for the first payment amount data attribute" in {
      elementText(Selectors.firstPaymentAmountData) shouldBe "£2,000,000,000.01"
    }

    "render the correct due period for the first payment" in {
      elementText(Selectors.firstPaymentDue) shouldBe "due by 8 April 2001"
    }

    "render the correct due period for the first payment period data attribute" in {
      elementText(Selectors.firstPaymentDueData) shouldBe "due by 8 April 2001"
    }

    "render the Direct Debit text for the first payment" in {
      elementText(Selectors.firstPaymentPayContext) shouldBe "You pay by direct debit"
    }

    "render the correct due period for the first payment period" in {
      elementText(Selectors.firstPaymentPeriod) shouldBe "for the period 1 January to 31 March 2001"
    }

    "render the correct view return link text for the first payment" in {
      elementText(Selectors.firstPaymentViewReturnText) shouldBe "View return"
    }

    "render the correct view return link text for the first payment with hidden text for context" in {
      elementText(Selectors.firstPaymentViewReturnContext) shouldBe "View return for the period 1 January to 31 March 2001"
    }

    "render the correct view return href for the first payment" in {
      element(Selectors.firstPaymentViewReturnLink).attr("href") shouldBe "/submitted/%23001"
    }

    "render the correct amount for the second payment" in {
      elementText(Selectors.secondPaymentAmount) shouldBe "£100"
    }

    "render the correct amount for the second payment amount data attribute" in {
      elementText(Selectors.secondPaymentAmountData) shouldBe "£100"
    }

    "render the correct due period for the second payment" in {
      elementText(Selectors.secondPaymentDue) shouldBe "due by 10 May 2002"
    }

    "render the correct due period for the second payment period data attribute" in {
      elementText(Selectors.secondPaymentDueData) shouldBe "due by 10 May 2002"
    }

    "render the correct due period for the second payment period" in {
      elementText(Selectors.secondPaymentPeriod) shouldBe "for the period 1 February to 28 March 2002"
    }

    "render the correct view return link text for the second payment" in {
      elementText(Selectors.secondPaymentViewReturnText) shouldBe "View return"
    }

    "render the correct view return link text for the second payment with hidden text for context" in {
      elementText(Selectors.secondPaymentViewReturnContext) shouldBe "View return for the period 1 February to 28 March 2002"
    }

    "render the correct view return href for the second payment" in {
      element(Selectors.secondPaymentViewReturnLink).attr("href") shouldBe "/submitted/%23002"
    }

    "render the correct amount for the third payment" in {
      elementText(Selectors.thirdPaymentAmount) shouldBe "£250"
    }

    "render the correct amount for the third payment amount data attribute" in {
      elementText(Selectors.thirdPaymentAmountData) shouldBe "£250"
    }

    "render the correct due period for the third payment" in {
      elementText(Selectors.thirdPaymentDue) shouldBe "due by 5 April 2017"
    }

    "render the correct due period for the third payment period data attribute" in {
      elementText(Selectors.thirdPaymentDueData) shouldBe "due by 5 April 2017"
    }

    "not display a view return link text for the third payment" in {
      document.select(Selectors.thirdPaymentViewReturnText).size shouldBe 0
    }

    "render the correct heading for the direct debits" in {
      elementText(Selectors.directDebit) shouldBe "Direct debits"
    }

    "render the correct text for the direct debits" in {
      elementText(Selectors.directDebitText) shouldBe "You can view your direct debit details."
    }

    "render the correct link text for the direct debits" in {
      elementText(Selectors.directDebitLink) shouldBe "view your direct debit details"
    }

    "have the correct link destination to the direct debits service" in {
      element(Selectors.directDebitLink).attr("href") shouldBe "/vat-through-software/direct-debit?status=true"
    }

    "render the correct help revealing link text" in {
      elementText(Selectors.helpSummaryRevealLink) shouldBe "What I owe is incorrect or missing"
    }

    "render the correct help text" in {
      elementText(Selectors.helpText) shouldBe
        "If what you owe is incorrect, check if you can correct errors on your VAT Return (opens in a new tab)."
    }

    "render the correct make payment help text" in {
      elementText(Selectors.helpMakePayment) shouldBe
        "After you have submitted a return, it can take 24 hours for what you owe to show here. " +
          "You can still make a payment (opens in a new tab) even if a payment is not shown."
    }

    "render the overdue label" in {
      elementText(Selectors.overdueLabel) shouldBe "overdue"
    }

    "have the correct destination for the make a payment link" in {
      element(Selectors.makePayment).attr("href") shouldBe "unauthenticated-payments-url"
    }
  }

  "Rendering the open payments page when a user does not have a direct debit" should {

    val hasDirectDebit = Some(false)
    val viewModel = OpenPaymentsViewModel(payments, hasDirectDebit)
    lazy val view = views.html.payments.openPayments(user, viewModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the correct link text for the first payment" in {
      elementText(Selectors.firstPaymentPayNowLinkText) shouldBe "Pay now"
    }

    "render the correct pay now href for the first payment" in {
      element(Selectors.firstPaymentPayLink).attr("href") should endWith(
        "200000000001/3/2001/VAT%20Return%20Debit%20Charge/2001-04-08"
      )
    }

    "render a hidden label for the button for the first payment" in {
      elementText(Selectors.firstPaymentPayNowContext) shouldBe
        "£2,000,000,000.01 overdue for the period 1 January to 31 March 2001"
    }

    "render the correct text for the processing time" in {
      elementText(Selectors.processingTime) shouldBe "Payments can take up to 5 days to process."
    }

    "render the correct text for the direct debit paragraph" in {
      elementText(Selectors.directDebitText) shouldBe
        "You can set up a direct debit to pay your VAT Returns."
    }

    "render the correct check direct debit link text" in {
      elementText(Selectors.directDebitLink) shouldBe "set up a direct debit"
    }

    "have the correct link destination to the direct debits service" in {
      element(Selectors.directDebitLink).attr("href") shouldBe "/vat-through-software/direct-debit?status=false"
    }
  }

  "Rendering the open payments page when the direct debit service can not be reached" should {

    val hasDirectDebit = None
    val viewModel = OpenPaymentsViewModel(payments, hasDirectDebit)
    lazy val view = views.html.payments.openPayments(user, viewModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the correct link text for the first payment" in {
      elementText(Selectors.firstPaymentPayNowLinkText) shouldBe "Pay now"
    }

    "render the correct pay now href for the first payment" in {
      element(Selectors.firstPaymentPayLink).attr("href") should endWith(
        "200000000001/3/2001/VAT%20Return%20Debit%20Charge/2001-04-08"
      )
    }

    "render a hidden label for the button for the first payment" in {
      elementText(Selectors.firstPaymentPayNowContext) shouldBe
        "£2,000,000,000.01 overdue for the period 1 January to 31 March 2001"
    }

    "render the correct text for the processing time" in {
      elementText(Selectors.processingTimeOld) shouldBe
        "Your payment could take up to 5 days to process. You may be fined if it is late."
    }

    "render the correct text for the direct debit paragraph" in {
      elementText(Selectors.directDebitText) shouldBe
        "If you have already set up a direct debit, you do not need to pay now. You can view your direct debits if you are not sure."
    }

    "render the correct check direct debit link text" in {
      elementText(Selectors.directDebitLink) shouldBe "view your direct debits"
    }

    "have the correct link destination to the direct debits service" in {
      element(Selectors.directDebitLink).attr("href") shouldBe "/vat-through-software/direct-debit"
    }
  }
}
