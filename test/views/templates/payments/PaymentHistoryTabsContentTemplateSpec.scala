/*
 * Copyright 2023 HM Revenue & Customs
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
import models.payments.{ReturnCreditCharge, ReturnDebitCharge}
import models.viewModels.{PaymentsHistoryModel, PaymentsHistoryViewModel}
import org.jsoup.Jsoup
import play.twirl.api.Html
import views.html.templates.payments.{PaymentsHistoryChargeDescription, PaymentsHistoryTabsContent}
import views.templates.TemplateBaseSpec

class PaymentHistoryTabsContentTemplateSpec extends TemplateBaseSpec {

  val paymentsHistoryChargeDescription: PaymentsHistoryChargeDescription =
    injector.instanceOf[PaymentsHistoryChargeDescription]
  val paymentsHistoryTabsContent: PaymentsHistoryTabsContent = injector.instanceOf[PaymentsHistoryTabsContent]

  val currentYear: Int = 2018
  val examplePaymentAmount: BigDecimal = 100.00
  val exampleRepaymentAmount: BigDecimal = -200.00

  val paymentTransaction: PaymentsHistoryModel = PaymentsHistoryModel(
    clearingSAPDocument = Some("002828853334"),
    chargeType = ReturnDebitCharge,
    taxPeriodFrom = Some(LocalDate.parse(s"2018-01-01")),
    taxPeriodTo = Some(LocalDate.parse(s"2018-02-01")),
    amount = examplePaymentAmount.setScale(2),
    clearedDate = Some(LocalDate.parse(s"2018-03-01"))
  )

  val repaymentTransaction: PaymentsHistoryModel =
    paymentTransaction.copy(chargeType = ReturnCreditCharge, amount = exampleRepaymentAmount.setScale(2))

  val paymentHistoryViewModel: PaymentsHistoryViewModel = PaymentsHistoryViewModel(
    currentYear,
    None,
    None,
    previousPaymentsTab = false,
    Seq(paymentTransaction),
    showInsolvencyContent = false,
    None
  )

  val noTransactionsPaymentHistoryViewModel: PaymentsHistoryViewModel = paymentHistoryViewModel.copy(transactions = Seq())

  val repaymentTransactionPaymentHistoryViewModel: PaymentsHistoryViewModel =
    paymentHistoryViewModel.copy(transactions = Seq(repaymentTransaction))

  def sectionExampleWithPayment(repayment: Boolean = false): String =
    s"""
      |<table class="govuk-table">
      |  <thead class="govuk-table__head">
      |    <tr class="govuk-table__row">
      |      <th scope="col" class="govuk-table__header">Date</th>
      |      <th scope="col" class="govuk-table__header">Payment description</th>
      |      <th scope="col" class="govuk-table__header govuk-table__header--numeric">You paid HMRC</th>
      |      <th scope="col" class="govuk-table__header govuk-table__header--numeric">HMRC paid you</th>
      |    </tr>
      |  </thead>
      |  <tbody class="govuk-table__body">
      |    <tr class="govuk-table__row">
      |      <td class="govuk-table__cell">1\u00a0Mar</td>
      |      <td class="govuk-table__cell">
      |        ${paymentsHistoryChargeDescription(if(repayment) repaymentTransaction else paymentTransaction)}
      |      </td>
      |      <td class="govuk-table__cell govuk-table__cell--numeric">£${if(repayment) "0.00" else examplePaymentAmount.setScale(2)}</td>
      |      <td class="govuk-table__cell govuk-table__cell--numeric">£${if(repayment) exampleRepaymentAmount.abs.setScale(2) else "0.00"}</td>
      |    </tr>
      |  </tbody>
      |</table>
    """.stripMargin

  val sectionExampleWithoutPayment: String = {
    s"""
      |  <p class="govuk-body">You have not made or received any payments using the new VAT service this year.</p>
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

    "there are no transactions" should {

      "render the correct HTML" in {

        val expectedMarkup = Html(sectionExampleWithoutPayment + noScriptSection)

        val result = paymentsHistoryTabsContent(
          noTransactionsPaymentHistoryViewModel, currentYear
        )

        formatHtml(result) shouldBe formatHtml(expectedMarkup)
      }
    }

    "there is a transaction where the user paid HMRC" should {

        "render the correct HTML" in {

          val expectedMarkup = Html(sectionExampleWithPayment() + noScriptSection)

          val result = paymentsHistoryTabsContent(
            paymentHistoryViewModel, currentYear
          )

          formatHtml(result) shouldBe formatHtml(expectedMarkup)
        }
      }

    "there is a transaction where HMRC paid the user" should {

        "render the correct HTML" in {

          val expectedMarkup = Html(
            sectionExampleWithPayment(repayment = true) + noScriptSection
          )

          val result = paymentsHistoryTabsContent(
            repaymentTransactionPaymentHistoryViewModel, currentYear
          )

          formatHtml(result) shouldBe formatHtml(expectedMarkup)
        }
      }

    "there are multiple transactions not in the expected order" should {

        "sort the transactions by cleared date (descending order)" in {

          val exampleTransactions = Seq(
            paymentTransaction.copy(clearedDate = Some(LocalDate.parse("2018-04-01"))),
            paymentTransaction.copy(clearedDate = Some(LocalDate.parse("2018-02-01"))),
            paymentTransaction.copy(clearedDate = Some(LocalDate.parse("2018-11-01"))),
            paymentTransaction.copy(clearedDate = Some(LocalDate.parse("2018-07-01"))),
          )

          val result = paymentsHistoryTabsContent(
            paymentHistoryViewModel.copy(transactions = exampleTransactions), currentYear
          )

          val document = Jsoup.parse(result.body)

          document.select("tr.govuk-table__row:nth-child(1) > td:nth-child(1)").text() shouldBe "1\u00a0Nov"
          document.select("tr.govuk-table__row:nth-child(2) > td:nth-child(1)").text() shouldBe "1\u00a0Jul"
          document.select("tr.govuk-table__row:nth-child(3) > td:nth-child(1)").text() shouldBe "1\u00a0Apr"
          document.select("tr.govuk-table__row:nth-child(4) > td:nth-child(1)").text() shouldBe "1\u00a0Feb"
        }
      }
  }
}
