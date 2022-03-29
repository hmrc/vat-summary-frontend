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

import models.payments._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.templates.payments.WhatYouOweChargeRow

class WhatYouOweChargeRowTemplateSpec extends ViewBaseSpec {

  val whatYouOweChargeRow: WhatYouOweChargeRow = injector.instanceOf[WhatYouOweChargeRow]

  object Selectors {
    val title = "#payment-1 > dl > div > dt > p"
    val due = "#payment-1 > dl > div > dt > div"

    val amount = "#payment-1 > dl > div > .govuk-summary-list__value > span"
    val amountData = "#payment-1 > dl > div > .govuk-summary-list__value > span[data-amount]"

    val payLink = "#payment-1 > dl > div > dd.govuk-summary-list__actions.what-you-owe-links > a"
    val payText = s"$payLink > span:nth-child(1)"
    val directDebitText = s"tr > td:nth-child(3) > p > a > span:nth-of-type(2)"
    val payHiddenContent = s"$payLink > span:nth-of-type(2)"
    val viewReturnLink = "#payment-1 > dl > div > dd.govuk-summary-list__actions.what-you-owe-links > p > a"
    val viewReturnText = s"$viewReturnLink > span:nth-of-type(2)"
    val viewReturnHiddenContent = s"$viewReturnLink > span:nth-of-type(1)"
  }

  def generateModel: OpenPaymentsModelWithPeriod = {
    OpenPaymentsModelWithPeriod(
      ReturnDebitCharge,
      BigDecimal(100.00),
      LocalDate.parse("2018-03-03"),
      LocalDate.parse("2018-01-01"),
      LocalDate.parse("2018-02-02"),
      "18AA",
      Some("XD002750002155"),
      isOverdue = false
    )
  }

  "Rendering the view" when {

    "the user is a principal entity" when {

      "user has no direct debit" when {

        "payment has a return associated" should {

          val model = generateModel
          lazy val view = whatYouOweChargeRow(model, 0, userIsAgent = false)
          lazy implicit val document: Document = Jsoup.parse(s"<table>${view.body}</table>")

          "display the correct title" in {
            elementText(Selectors.title) shouldBe "Return for the period 1 Jan to 2 Feb 2018"
          }

          "display the correct due text" in {
            elementText(Selectors.due) shouldBe "due by 3 Mar 2018"
          }

          "display the correct owed amount" in {
            elementText(Selectors.amount) shouldBe "£100"
          }

          "display the correct amount data attribute" in {
            elementText(Selectors.amountData) shouldBe "£100"
          }

          "display the correct pay text" in {
            elementText(Selectors.payText) shouldBe "Pay now"
          }

          "not display overdue flag" in {
            elementExtinct(".govuk-tag--red")
          }

          "have the correct pay link destination" in {
            element(Selectors.payLink).attr("href") shouldBe
              "/vat-through-software/make-payment/10000/2/2018/2018-02-02/VAT%20Return%20Debit%20Charge/2018-03-03/XD002750002155"
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
            "18AA",
            Some("XD002750002155"),
            isOverdue = false
          )

          lazy val view = whatYouOweChargeRow(model, 0, userIsAgent = false)
          lazy implicit val document: Document = Jsoup.parse(s"<table>${view.body}</table>")

          "not show a link to View Return" in {
            document.select(Selectors.viewReturnLink) shouldBe empty
          }
        }
      }

      "payment is overdue" should {

        val model: OpenPaymentsModelWithPeriod = {
          OpenPaymentsModelWithPeriod(
            ReturnDebitCharge,
            BigDecimal(100.00),
            LocalDate.parse("2018-03-03"),
            LocalDate.parse("2018-01-01"),
            LocalDate.parse("2018-02-02"),
            "18AA",
            Some("XD002750002155"),
            isOverdue = true
          )
        }
        lazy val view = whatYouOweChargeRow(model, 0, userIsAgent = false)
        lazy implicit val document: Document = Jsoup.parse(s"<table>${view.body}</table>")

        "display overdue flag" in {
          elementText(".govuk-tag--red") shouldBe "overdue"
        }
      }
    }
  }

  "the user is an agent" should {

    val model = generateModel
    lazy val view = whatYouOweChargeRow(model, 0, userIsAgent = true)
    lazy implicit val document: Document = Jsoup.parse(s"<table>${view.body}</table>")

    "not display the 'Pay now' link" in {
      elementExtinct(Selectors.payLink)
    }
  }
}
