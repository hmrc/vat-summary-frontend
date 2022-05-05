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

import java.time.LocalDate
import common.MessageLookup.PaymentMessages
import models.User
import models.payments._
import models.viewModels.OpenPaymentsViewModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.OpenPayments
import views.templates.payments.PaymentMessageHelper

class OpenPaymentsViewSpec extends ViewBaseSpec {

  val openPaymentsView: OpenPayments = injector.instanceOf[OpenPayments]
  object Selectors {
    val pageHeading = "h1"
    val caption = ".govuk-caption-xl"
    val backLink = ".govuk-back-link"
    val paymentLink = "#payments a"
    val correctErrorLink = "div > p:nth-child(4) > a"
    val btaBreadcrumb = "li.govuk-breadcrumbs__list-item > a"
    val vatBreadcrumb = "li.govuk-breadcrumbs__list-item:nth-child(2) > a"

    private val columnOne: Int => String = row => s"#payment-$row > dl > div > dt"
    val title: Int => String = row => s"${columnOne(row)} > p"
    val description: Int => String = row => s"${columnOne(row)} > p > span"
    val due: Int => String = row =>s"${columnOne(row)} > div"

    private val columnTwo: Int => String = row => s"#payment-$row > dl > div > dd:nth-child(2)"
    val amount: Int => String = row => s"${columnTwo(row)} > span"

    private val columnThree: Int => String = row => s"#payment-$row > dl > div > dd:nth-child(3)"
    val payLink: Int => String = row => s"${columnThree(row)} > a"
    val payText: Int => String = row => s"${payLink(row)} > span"
    val viewReturn: Int => String = row => s"${columnThree(row)} > p > a"
    val viewReturnText: Int => String = row => s"${columnThree(row)} > p > a > span:nth-of-type(2)"
    val processingTime = "#processing-time"
    val whatOweMissing = "#what-you-owe-missing"
    val helpText = "div > p:nth-child(4)"
    val helpMakePayment = "div > p:nth-child(5)"
    val helpSummaryRevealLink = "summary span:nth-of-type(1)"
    val makePayment = "#vatPaymentsLink"
  }

  override val user: User = User("1111")
  val noPayment: Seq[Nothing] = Seq()
  val payments: Seq[OpenPaymentsModelWithPeriod] = Seq(
    OpenPaymentsModelWithPeriod(
      chargeType = ReturnDebitCharge,
      amount = 2000000000.01,
      due = LocalDate.parse("2001-04-08"),
      periodFrom = LocalDate.parse("2001-01-01"),
      periodTo = LocalDate.parse("2001-03-31"),
      periodKey = "#001",
      chargeReference = Some("XD002750002155"),
      isOverdue = false
    ),
    OpenPaymentsModelWithPeriod(
      AAInterestCharge,
      300.00,
      LocalDate.parse("2003-04-05"),
      LocalDate.parse("2003-01-01"),
      LocalDate.parse("2003-03-31"),
      "#003",
      chargeReference = Some("XD002750002155"),
      isOverdue = false
    )
  )

  "Rendering the open payments page for a principal user" when {

    "the  user has two non-overdue charges" should {

      val viewModel = OpenPaymentsViewModel(payments, mandationStatus = "2")
      lazy val view = {
        openPaymentsView(user, viewModel, Html(""), None)
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "What you owe - Manage your VAT account - GOV.UK"
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe "What you owe"
      }

      "not have a client name caption" in {
        elementExtinct(Selectors.caption)
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

        s"link to https://www.gov.uk/vat-corrections" in {
          element(Selectors.correctErrorLink).attr("href") shouldBe "https://www.gov.uk/vat-corrections"
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
          elementText(Selectors.payText(1)) shouldBe "Pay now"
        }

        "render the correct view return link text" in {
          elementText(Selectors.viewReturnText(1)) shouldBe "View return"
        }

        "render the correct view return link" in {
          element(Selectors.viewReturn(1)).attr("href") shouldBe "/submitted/%23001"
        }
      }

      "for the second payment" should {

        "render the correct title" in {
          elementText(Selectors.title(2)) shouldBe "Additional assessment interest charged on additional " +
            "tax assessed for the period 1 Jan to 31 Mar 2003"
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
            "30000/3/2003/2003-03-31/VAT%20AA%20Default%20Interest/2003-04-05/XD002750002155"
          )
        }
      }

      "render the correct heading for what I owe is incorrect or missing" in {
        elementText(Selectors.whatOweMissing) shouldBe "What I owe is incorrect or missing"
      }

      "render the correct help text" in {
        elementText(Selectors.helpText) shouldBe
          "If what you owe is incorrect, check if you can correct errors on your VAT Return (opens in a new tab)."
      }

      "render the correct make payment help text" in {
        elementText(Selectors.helpMakePayment) shouldBe
          "After you have submitted a return, it can take 24 hours for what you owe to show here. " +
            "You can still make a payment (opens in a new tab)even if a payment is not shown."
      }

      "have the correct destination for the make a payment link" in {
        element(Selectors.makePayment).attr("href") shouldBe "unauthenticated-payments-url"
      }
    }

    "the user has the following charge types" should {

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
              chargeReference = Some("XD002750002155"),
              isOverdue = false
            )),
            mandationStatus = "MTDfB"
          ),
          ChargeType.apply(historyChargeHelper.name).value,
          PaymentMessages.getMessagesForChargeType(historyChargeHelper.name)._1,
          PaymentMessages.getMessagesForChargeType(historyChargeHelper.name)._2
        )
      }.foreach { case (openPaymentsViewModel, chargeTypeTitle, expectedTitle, expectedDescription) =>

        lazy val view = openPaymentsView(user, openPaymentsViewModel, Html(""), None)
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

  "Rendering the open payments page for an agent" should {

    val entityName = "Capgemini"
    val viewModel = OpenPaymentsViewModel(payments, mandationStatus = "2")
    lazy val view = {
      openPaymentsView(agentUser, viewModel, Html(""), Some(entityName))
    }
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "What your client owes - Your client’s VAT details - GOV.UK"
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "What your client owes"
    }

    "have the client name caption" in {
      elementText(Selectors.caption) shouldBe entityName
    }

    "render a Back link" which {

      "has the correct text" in {
        elementText(Selectors.backLink) shouldBe "Back"
      }

      "has the correct href" in {
        element(Selectors.backLink).attr("href") shouldBe mockConfig.agentClientLookupHubUrl
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

      "not render the Pay now link" in {
        elementExtinct(Selectors.payLink(1))
      }

      "render the correct view return link text" in {
        elementText(Selectors.viewReturnText(1)) shouldBe "View return"
      }

      "render the correct view return link" in {
        element(Selectors.viewReturn(1)).attr("href") shouldBe "/submitted/%23001"
      }
    }

    "for the second payment" should {

      "render the correct title" in {
        elementText(Selectors.title(2)) shouldBe "Additional assessment interest charged on additional " +
          "tax assessed for the period 1 Jan to 31 Mar 2003"
      }

      "render the correct amount" in {
        elementText(Selectors.amount(2)) shouldBe "£300"
      }

      "render the correct due period" in {
        elementText(Selectors.due(2)) shouldBe "due by 5 Apr 2003"
      }

      "not render the Pay now link" in {
        elementExtinct(Selectors.payLink(2))
      }
    }

    "render the correct heading for what I owe is incorrect or missing" in {
      elementText(Selectors.whatOweMissing) shouldBe "The amount owed is incorrect or missing"
    }

    "render the correct help text" in {
      elementText(Selectors.helpText) shouldBe
        "If the amount owed is incorrect, check if you can correct errors on your client’s VAT return (opens in a new tab)."
    }

    "render the correct make payment help text" in {
      elementText(Selectors.helpMakePayment) shouldBe
        "If a return has been submitted and your client needs to pay VAT, it can take up to 24 hours to see what they owe."
    }
  }
}
