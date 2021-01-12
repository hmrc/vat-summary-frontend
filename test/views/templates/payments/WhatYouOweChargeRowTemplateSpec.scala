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
    private val columnOne = ""
    val title = s"h2"
    val due = s"$columnOne dt > div"

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

    "user has no direct debit" when {

      "payment has a return associated" should {

        val model = generateModel
        lazy val view = whatYouOweChargeRow(model, 0)
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

        "not display overdue flag" in {
          elementExtinct(".task-overdue")
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

        lazy val view = whatYouOweChargeRow(model, 0)
        lazy implicit val document: Document = Jsoup.parse(
          s"<table>${view.body}</table>"
        )

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
      lazy val view = whatYouOweChargeRow(model, 0)
      lazy implicit val document: Document = Jsoup.parse(
        s"<table>${view.body}</table>"
      )

      "display overdue flag" in {
        elementText(".task-overdue")shouldBe "overdue"
      }
    }
  }
}
