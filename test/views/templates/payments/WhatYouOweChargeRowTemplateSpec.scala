/*
 * Copyright 2019 HM Revenue & Customs
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

import common.FinancialTransactionsConstants._
import models.payments._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class WhatYouOweChargeRowTemplateSpec extends ViewBaseSpec {

  mockConfig.features.allowDirectDebits(true)

  object Selectors {
    private val columnOne = ""
    val title = s"h2"
    val due = s"$columnOne dt > div"
    val overdueLabel = s"$due .task-overdue"

    private val columnTwo = "dd:nth-of-type(1)"
    val amount = s"$columnTwo > span"
    val amountData = s"$columnTwo > span[data-amount]"

    private val columnThree = "dd:nth-of-type(2)"
    val payLink = s"$columnThree > div > a:nth-of-type(1)"
    val payText = s"$payLink > span:nth-of-type(1)"
    val directDebitText = s"$columnThree span"
    val payHiddenContent = s"$payLink > span:nth-of-type(2)"
    val viewReturnLink = s"$columnThree > a"
    val viewReturnText = s"$viewReturnLink > span:nth-of-type(2)"
    val viewReturnHiddenContent = s"$viewReturnLink span:nth-of-type(1)"
  }

  "Rendering the view" when {

    def generateModel(overdue: Boolean): OpenPaymentsModelWithPeriod = {
      OpenPaymentsModelWithPeriod(
        ReturnDebitCharge,
        BigDecimal(100.00),
        LocalDate.parse("2018-03-03"),
        LocalDate.parse("2018-01-01"),
        LocalDate.parse("2018-02-02"),
        "18AA",
        overdue
      )
    }

    "user has no direct debit" when {

      "payment has a return associated" should {

        val model = generateModel(overdue = false)
        lazy val view = views.html.templates.payments.whatYouOweChargeRow(model, 0)
        lazy implicit val document: Document = Jsoup.parse(
          s"<table>${view.body}</table>"
        )

        "display the correct title" in {
          elementText(Selectors.title) shouldBe "Return for the period 1 Jan to 2 Feb 2018"
        }

        "display the correct due text" in {
          elementText(Selectors.due) shouldBe "due by 3 Mar 2018"
        }

        "display the correct owed amount as a negative" in {
          elementText(Selectors.amount) shouldBe "£100"
        }

        "display the correct amount data attribute as a negative" in {
          elementText(Selectors.amountData) shouldBe "£100"
        }

        "display the correct pay text" in {
          elementText(Selectors.payText) shouldBe "Pay now"
        }

        "have the correct pay link destination" in {
          element(Selectors.payLink).attr("href") shouldBe
            "/vat-through-software/make-payment/10000/2/2018/VAT%20Return%20Debit%20Charge/2018-03-03"
        }

        "have the correct pay link context" in {
          elementText(Selectors.payHiddenContent) shouldBe "£100"
        }

        "display the link to view the return" in {
          elementText(Selectors.viewReturnText) shouldBe "View return"
        }

        "have the correct return location" in {
          element(Selectors.viewReturnLink).attr("href") shouldBe "/submitted/18AA"
        }

        "have the correct return context" in {
          elementText(Selectors.viewReturnHiddenContent) shouldBe "for the period 1 January to 2 February 2018"
        }
      }

      "payment does not have a return associated" should {

        val model = OpenPaymentsModelWithPeriod(
          VatPADefaultInterestCharge,
          BigDecimal(100.00),
          LocalDate.parse("2018-03-03"),
          LocalDate.parse("2018-01-01"),
          LocalDate.parse("2018-02-02"),
          "18AA"
        )

        lazy val view = views.html.templates.payments.whatYouOweChargeRow(model, 0)
        lazy implicit val document: Document = Jsoup.parse(
          s"<table>${view.body}</table>"
        )

        "not show a link to View Return" in {
          document.select(Selectors.viewReturnLink) shouldBe empty
        }
      }
    }

    "the payment is overdue" should {

      val model = generateModel(overdue = true)
      lazy val view = views.html.templates.payments.whatYouOweChargeRow(model, 0)
      lazy implicit val document: Document = Jsoup.parse(
        s"<table>${view.body}</table>"
      )

      "display the overdue label" in {
        elementText(Selectors.overdueLabel) shouldBe "overdue"
      }

      "have the correct pay link context" in {
        elementText(Selectors.payHiddenContent) shouldBe "£100"
      }
    }
  }
}
