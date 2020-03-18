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

import java.time.LocalDate

import common.MessageLookup.PaymentMessages
import models.User
import models.payments._
import models.viewModels.OpenPaymentsViewModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.templates.payments.PaymentMessageHelper

class OpenPaymentsViewSpec extends ViewBaseSpec {

  object Selectors {
    val pageHeading = "h1"
    val paymentLink = "#payments a"
    val correctErrorLink = "details p:nth-of-type(1) a"
    val btaBreadcrumb = "div.breadcrumbs li:nth-of-type(1)"
    val btaBreadcrumbLink = "div.breadcrumbs li:nth-of-type(1) a"
    val vatBreadcrumb = "div.breadcrumbs li:nth-of-type(2)"
    val vatBreadcrumbLink = "div.breadcrumbs li:nth-of-type(2) a"
    val paymentBreadcrumb = "div.breadcrumbs li:nth-of-type(3)"

    private val columnOne: Int => String = row => s"#payment-$row"
    val title: Int => String = row => s"${columnOne(row)} dt > h2"
    val description: Int => String = row => s"${columnOne(row)} div:nth-of-type(1)"
    val due: Int => String = row =>s"${columnOne(row)} > dl > div > dt > div"

    private val columnTwo: Int => String = row => s"#payment-$row dd:nth-of-type(1)"
    val amount: Int => String = row => s"${columnTwo(row)} > span"

    private val columnThree: Int => String = row => s"#payment-$row dd:nth-of-type(2)"
    val payLink: Int => String = row => s"${columnThree(row)} > div > a:nth-of-type(1)"
    val payText: Int => String = row => s"${payLink(row)} > span:nth-of-type(1)"
    val payByDirectDebit: Int => String = row => s"${columnThree(row)} span"
    val viewReturnLink: Int => String = row => s"${columnThree(row)} > a"
    val viewReturnText: Int => String = row => s"${viewReturnLink(row)} > span:nth-of-type(2)"

    val processingTime = "#processing-time"
    val processingTimeOld = "#processing-time-old"
    val directDebit = "#direct-debits"
    val directDebitText = "#check-direct-debit p:nth-of-type(1)"
    val directDebitLink = "#check-direct-debit a:nth-of-type(1)"
    val helpText = "details p:nth-of-type(1)"
    val helpMakePayment = "details p:nth-of-type(2)"
    val helpSummaryRevealLink = "summary span:nth-of-type(1)"
    val makePayment = "#vatPaymentsLink"
  }

  override val user = User("1111")
  val noPayment: Seq[Nothing] = Seq()
  val payments: Seq[OpenPaymentsModelWithPeriod] = Seq(
    OpenPaymentsModelWithPeriod(
      chargeType = ReturnDebitCharge,
      amount = 2000000000.01,
      due = LocalDate.parse("2001-04-08"),
      periodFrom = LocalDate.parse("2001-01-01"),
      periodTo = LocalDate.parse("2001-03-31"),
      periodKey = "#001",
      isOverdue = false
    ),
    OpenPaymentsModelWithPeriod(
      AAInterestCharge,
      300.00,
      LocalDate.parse("2003-04-05"),
      LocalDate.parse("2003-01-01"),
      LocalDate.parse("2003-03-31"),
      "#003",
      isOverdue = false
    )
  )

  "Rendering the open payments page when a user has a direct debit" should {

    val hasDirectDebit = Some(true)
    val viewModel = OpenPaymentsViewModel(payments, hasDirectDebit)
    lazy val view = views.html.payments.openPayments(user, viewModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "What you owe - Business tax account - GOV.UK"
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
        elementText(Selectors.vatBreadcrumb) shouldBe "Your VAT account"
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

    "for the first payment" should {

      "render the correct title" in {
        elementText(Selectors.title(1)) shouldBe "Return for the period 1 Jan to 31 Mar 2001"
      }

      "render the correct amount" in {
        elementText(Selectors.amount(1)) shouldBe "£2,000,000,000.01"
      }

      "render the correct due period" in {
        elementText(Selectors.due(1)) shouldBe "due by 8 Apr 2001"
      }

      "render the Pay now text" in {
        elementText(Selectors.payByDirectDebit(1)) shouldBe "Pay now"
      }

      "render the correct view return link text" in {
        elementText(Selectors.viewReturnText(1)) shouldBe "View return"
      }

      "render the correct view return link" in {
        element(Selectors.viewReturnLink(1)).attr("href") shouldBe "/submitted/%23001"
      }
    }


    "for the second payment" should {

      "render the correct title" in {
        elementText(Selectors.title(2)) shouldBe "Additional assessment interest interest charged on additional tax assessed for the period 1 Jan to 31 Mar 2003"
      }

      "render the correct amount" in {
        elementText(Selectors.amount(2)) shouldBe "£300"
      }

      "render the correct due period" in {
        elementText(Selectors.due(2)) shouldBe "due by 5 Apr 2003"
      }

      "render the Pay now text" in {
        elementText(Selectors.payText(2)) shouldBe "Pay now"
      }

      "render the correct Pay now link" in {
        element(Selectors.payLink(2)).attr("href") should endWith(
          "30000/3/2003/VAT%20AA%20Default%20Interest/2003-04-05"
        )
      }
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

    "have the correct destination for the make a payment link" in {
      element(Selectors.makePayment).attr("href") shouldBe "unauthenticated-payments-url"
    }
  }

  "Rendering the open payments page when a user does not have a direct debit" should {

    val hasDirectDebit = Some(false)
    val viewModel = OpenPaymentsViewModel(payments, hasDirectDebit)
    lazy val view = views.html.payments.openPayments(user, viewModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the Pay now text" in {
      elementText(Selectors.payText(1)) shouldBe "Pay now"
    }

    "render the correct link to Pay now" in {
      element(Selectors.payLink(1)).attr("href") should endWith(
        "200000000001/3/2001/VAT%20Return%20Debit%20Charge/2001-04-08"
      )
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

  "Supplying with the following charge types" should {

    PaymentMessageHelper.values.map { historyChargeHelper =>
      (
        OpenPaymentsViewModel(Seq(
          OpenPaymentsModelWithPeriod(
            chargeType = ChargeType.apply(historyChargeHelper.name),
            amount = 2000000000.01,
            due = LocalDate.parse("2008-04-08"),
            periodFrom = LocalDate.parse("2018-01-01"),
            periodTo = LocalDate.parse("2018-02-01"),
            periodKey = "#001",
            isOverdue = false
          )),
          hasDirectDebit = Some(false)
        ),
        ChargeType.apply(historyChargeHelper.name).value,
        PaymentMessages.getMessagesForChargeType(historyChargeHelper.name)._1,
        PaymentMessages.getMessagesForChargeType(historyChargeHelper.name)._2
      )
    }.foreach { case (openPaymentsViewModel, chargeTypeTitle, expectedTitle, expectedDescription) =>

      lazy val view = views.html.payments.openPayments(user, openPaymentsViewModel)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"contain a $chargeTypeTitle which" should {

        if (expectedDescription.nonEmpty) {
          "render the correct title" in {
            elementText(Selectors.title(1)) shouldBe expectedTitle + " " + expectedDescription
          }

        } else {
          "render the correct title" in {
            elementText(Selectors.title(1)) shouldBe expectedTitle
          }
        }
      }
    }
  }
}
