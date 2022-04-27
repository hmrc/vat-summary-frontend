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

package views.templates.payments

import java.time.LocalDate
import models.User
import models.payments.{ReturnCreditCharge, ReturnDebitCharge}
import models.viewModels.PaymentsHistoryModel
import org.jsoup.Jsoup
import play.twirl.api.Html
import views.html.templates.payments.{PaymentsHistoryChargeDescription, PaymentsHistoryTabsContent}
import views.templates.TemplateBaseSpec

class PaymentHistoryTabsContentTemplateSpec extends TemplateBaseSpec {

  val paymentsHistoryChargeDescription: PaymentsHistoryChargeDescription =
    injector.instanceOf[PaymentsHistoryChargeDescription]
  val paymentsHistoryTabsContent: PaymentsHistoryTabsContent = injector.instanceOf[PaymentsHistoryTabsContent]

  val currentYear: Int = 2018
  val previousYear: Int = 2017
  val examplePaymentAmount: Int = 100
  val exampleRepaymentAmount: Int = -200
  val singleYear: Seq[Int] = Seq(currentYear)
  val multipleYears: Seq[Int] = Seq(currentYear, previousYear)
  implicit val user: User = User("999999999")

  val paymentTransaction: PaymentsHistoryModel = PaymentsHistoryModel(
    clearingSAPDocument = Some("002828853334"),
    chargeType = ReturnDebitCharge,
    taxPeriodFrom = Some(LocalDate.parse(s"2018-01-01")),
    taxPeriodTo = Some(LocalDate.parse(s"2018-02-01")),
    amount = examplePaymentAmount,
    clearedDate = Some(LocalDate.parse(s"2018-03-01"))
  )

  val repaymentTransaction: PaymentsHistoryModel =
    paymentTransaction.copy(chargeType = ReturnCreditCharge, amount = exampleRepaymentAmount)

  def sectionExampleWithPayment(repayment: Boolean = false): String =
    s"""
      |<div id="year-$currentYear" class="govuk-tabs__panel">
      |  <table class="govuk-table">
      |    <thead class="govuk-table__head">
      |      <tr class="govuk-table__row">
      |        <th scope="col" class="govuk-table__header">Date</th>
      |        <th scope="col" class="govuk-table__header">Payment description</th>
      |        <th scope="col" class="govuk-table__header govuk-table__header--numeric">You paid HMRC</th>
      |        <th scope="col" class="govuk-table__header govuk-table__header--numeric">HMRC paid you</th>
      |      </tr>
      |    </thead>
      |    <tbody class="govuk-table__body">
      |      <tr class="govuk-table__row">
      |        <td class="govuk-table__cell">1 Mar</td>
      |        <td class="govuk-table__cell">
      |          ${paymentsHistoryChargeDescription(if(repayment) repaymentTransaction else paymentTransaction)}
      |        </td>
      |        <td class="govuk-table__cell govuk-table__cell--numeric">
      |          £${if(repayment) "0" else examplePaymentAmount}
      |        </td>
      |        <td class="govuk-table__cell govuk-table__cell--numeric">
      |          £${if(repayment) exampleRepaymentAmount.abs else "0"}
      |        </td>
      |      </tr>
      |    </tbody>
      |  </table>
      |</div>
    """.stripMargin

  def sectionExampleWithoutPayment(year: Int, divClassHidden: Boolean = false): String = {
    val hiddenDivClass: String =
      if(divClassHidden) {" govuk-tabs__panel--hidden"}
      else {""}
    val sectionAttributes: String =
        s"""id="year-$year" class="govuk-tabs__panel$hiddenDivClass""""
    s"""
      |<div $sectionAttributes>
      |  <p class="govuk-body">You have not made or received any payments using the new VAT service this year.</p>
      |</div>
      """.stripMargin
  }

  val noScriptSection: String = {
    """
    |<noscript>
    |  <div>
    |    <a href="#top" class="govuk-link">
    |      <svg class="arrow input--radio-inline" width="13" height="15" viewBox="0 -5 13 15">
    |        <path fill="currentColor" d="M6.5 0L0 6.5 1.4 8l4-4v12.7h2V4l4.3 4L13 6.4z"></path>
    |      </svg><span>Back to top</span>
    |    </a>
    |  </div>
    |</noscript>
    """.stripMargin
  }

  "The payment history tabs content template" when {

    "the showPreviousPaymentsTab boolean is set to false" when {

      "there is one year" when {

        "there are no transactions" should {

          "render the correct HTML" in {

            val expectedMarkup = Html(sectionExampleWithoutPayment(currentYear) + noScriptSection)

            val result = paymentsHistoryTabsContent(
              singleYear, Seq.empty, showPreviousPaymentsTab = false
            )

            formatHtml(result) shouldBe formatHtml(expectedMarkup)
          }
        }

        "there is a transaction where the user paid HMRC" should {

          "render the correct HTML" in {

            val expectedMarkup = Html(sectionExampleWithPayment() + noScriptSection)

            val result = paymentsHistoryTabsContent(
              singleYear, Seq(paymentTransaction), showPreviousPaymentsTab = false
            )

            formatHtml(result) shouldBe formatHtml(expectedMarkup)
          }
        }

        "there is a transaction where HMRC paid the user" should {

          "render the correct HTML" in {

            val expectedMarkup = Html(
              sectionExampleWithPayment(repayment = true) +
              sectionExampleWithoutPayment(previousYear, divClassHidden = true) +
              noScriptSection
            )

            val result = paymentsHistoryTabsContent(
              multipleYears, Seq(repaymentTransaction), showPreviousPaymentsTab = false
            )

            formatHtml(result) shouldBe formatHtml(expectedMarkup)
          }
        }

        "there are multiple transactions not in the expected order" should {

          "sort the transactions by cleared date (descending order)" in {

            val transactions = Seq(
              paymentTransaction.copy(clearedDate = Some(LocalDate.parse("2018-04-01"))),
              paymentTransaction.copy(clearedDate = Some(LocalDate.parse("2018-02-01"))),
              paymentTransaction.copy(clearedDate = Some(LocalDate.parse("2018-11-01"))),
              paymentTransaction.copy(clearedDate = Some(LocalDate.parse("2018-07-01"))),
            )

            val result = paymentsHistoryTabsContent(
              multipleYears, transactions, showPreviousPaymentsTab = false
            )

            val document = Jsoup.parse(result.body)

            document.select("tr.govuk-table__row:nth-child(1) > td:nth-child(1)").text() shouldBe "1 Nov"
            document.select("tr.govuk-table__row:nth-child(2) > td:nth-child(1)").text() shouldBe "1 Jul"
            document.select("tr.govuk-table__row:nth-child(3) > td:nth-child(1)").text() shouldBe "1 Apr"
            document.select("tr.govuk-table__row:nth-child(4) > td:nth-child(1)").text() shouldBe "1 Feb"
          }
        }
      }

      "there are two years" when {

        "there are no transactions" should {

          "render the correct HTML" in {
            val expectedMarkup = Html(
              sectionExampleWithoutPayment(currentYear) + sectionExampleWithoutPayment(previousYear, divClassHidden = true) + noScriptSection
            )

            val result = paymentsHistoryTabsContent(
              multipleYears, Seq.empty, showPreviousPaymentsTab = false
            )

            formatHtml(result) shouldBe formatHtml(expectedMarkup)
          }
        }

        "there are some transactions" should {

          "render the correct HTML" in {

            val expectedMarkup = Html(
              sectionExampleWithPayment() +
              sectionExampleWithoutPayment(previousYear, divClassHidden = true) +
              noScriptSection
            )

            val result = paymentsHistoryTabsContent(
              multipleYears, Seq(paymentTransaction), showPreviousPaymentsTab = false
            )

            formatHtml(result) shouldBe formatHtml(expectedMarkup)
          }
        }
      }
    }

    "the showPreviousPaymentsTab boolean is set to true" should {

      "render the correct HTML" in {

        val expectedMarkup = Html(
          sectionExampleWithoutPayment(currentYear) +
          s"""
            |<div id="previous-payments" class="govuk-tabs__panel">
            |  <p class="govuk-body">
            |    You can
            |    <a class="govuk-link"
            |       rel="noreferrer noopener"
            |       href="${mockAppConfig.portalNonHybridPreviousPaymentsUrl(user.vrn)}"
            |       target="_blank">view your previous payments (opens in a new tab)</a>
            |    if you made payments before joining Making Tax Digital.
            |  </p>
            |</div>
          """.stripMargin
          + noScriptSection
        )

        val result = paymentsHistoryTabsContent(
          singleYear, Seq.empty, showPreviousPaymentsTab = true
        )

        formatHtml(result) shouldBe formatHtml(expectedMarkup)
      }
    }
  }
}
