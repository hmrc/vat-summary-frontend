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

package views.templates.payments

import java.time.LocalDate

import common.FinancialTransactionsConstants._
import models.payments.OpenPaymentsModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class WhatYouOweChargeTemplateSpec extends ViewBaseSpec {

  mockConfig.features.allowDirectDebits(true)
  mockConfig.features.allowNineBox(true)
  mockConfig.features.allowPayments(true)

  object Selectors {
    val amount = "#payment-section-1 span:nth-of-type(1)"
    val amountData = "#payment-section-1 span[data-amount]"
    val due = "#payment-row-1 span:nth-of-type(1)"
    val dueData = "#payment-row-1 span[data-due]"
    val description = ".form-hint"
    val payText = "#links-section-1 span"
    val payLink = "#links-section-1 a"
    val payContext = "#links-section-1 span:nth-of-type(2)"
    val viewReturnText = "#links-section-1 div:nth-of-type(2) span"
    lazy val viewReturnLink = "#links-section-1 div:nth-of-type(2) a"
    val overdueLabel = ".task-overdue"
  }

  "Rendering a VAT Return Debit Charge" when {

    def generateModel(overdue: Boolean): OpenPaymentsModel = {
      OpenPaymentsModel(
        vatReturnDebitCharge,
        BigDecimal(100.00),
        LocalDate.parse("2018-03-03"),
        LocalDate.parse("2018-01-01"),
        LocalDate.parse("2018-02-02"),
        "18AA",
        overdue
      )
    }

    "the user does not have a direct debit" should {

      val model = generateModel(overdue = false)
      lazy val view = views.html.templates.payments.whatYouOweCharge(model, 0, Some(false))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the correct owed amount" in {
        elementText(Selectors.amount) shouldBe "£100"
      }

      "display the correct amount data attribute" in {
        elementText(Selectors.amountData) shouldBe "£100"
      }

      "display the correct due text" in {
        elementText(Selectors.due) shouldBe "due by 3 March 2018"
      }

      "display the correct description text" in {
        elementText(Selectors.description) shouldBe "for the period 1 January to 2 February 2018"
      }

      "display the correct pay text" in {
        elementText(Selectors.payText) shouldBe "Pay now"
      }

      "have the correct pay link destination" in {
        element(Selectors.payLink).attr("href") shouldBe "/vat-through-software/make-payment/10000/2/2018"
      }

      "have the correct pay link context" in {
        elementText(Selectors.payContext) shouldBe "£100 for the period 1 January to 2 February 2018"
      }

      "display the link to view the return" in {
        elementText(Selectors.viewReturnText) shouldBe "View return"
      }

      "have the correct return location" in {
        element(Selectors.viewReturnLink).attr("href") shouldBe "/submitted/18AA"
      }

      "have the correct return context" in {
        elementText(Selectors.viewReturnLink) shouldBe "View return for the period 1 January to 2 February 2018"
      }
    }

    "the user has a direct debit" should {

      val model = generateModel(overdue = false)
      lazy val view = views.html.templates.payments.whatYouOweCharge(model, 0, Some(true))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct pay text" in {
        elementText(Selectors.payText) shouldBe "You pay by direct debit"
      }
    }

    "the payment is overdue" should {

      val model = generateModel(overdue = true)
      lazy val view = views.html.templates.payments.whatYouOweCharge(model, 0, None)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the overdue label" in {
        elementText(Selectors.overdueLabel) shouldBe "overdue"
      }

      "have the correct pay link context" in {
        elementText(Selectors.payContext) shouldBe "£100 overdue for the period 1 January to 2 February 2018"
      }
    }
  }

  "Rendering an Officer's Assessment charge" when {

    def generateModel(overdue: Boolean): OpenPaymentsModel = {
      OpenPaymentsModel(
        officerAssessmentDebitCharge,
        BigDecimal(100.00),
        LocalDate.parse("2018-03-03"),
        LocalDate.parse("2018-01-01"),
        LocalDate.parse("2018-02-02"),
        "18AA",
        overdue
      )
    }

    "the payment is not overdue" should {

      val model = generateModel(overdue = false)
      lazy val view = views.html.templates.payments.whatYouOweCharge(model, 0, Some(true))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the correct description text" in {
        elementText(Selectors.description) shouldBe "a VAT officer's investigation showed you underpaid by this amount"
      }

      "display the correct pay text" in {
        elementText(Selectors.payText) shouldBe "Pay now"
      }

      "have the correct pay link context" in {
        elementText(Selectors.payContext) shouldBe
          "£100 , a VAT officer's investigation showed you underpaid by this amount"
      }

      "not display the view return link" in {
        intercept[org.scalatest.exceptions.TestFailedException](element(Selectors.viewReturnLink))
      }
    }

    "the payment is overdue" should {

      val model = generateModel(overdue = true)
      lazy val view = views.html.templates.payments.whatYouOweCharge(model, 0, Some(true))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct pay link context" in {
        elementText(Selectors.payContext) shouldBe
          "£100 is overdue, a VAT officer's investigation showed you underpaid by this amount"
      }
    }
  }

  "Rendering a default surcharge charge" when {

    def generateModel(overdue: Boolean): OpenPaymentsModel = {
      OpenPaymentsModel(
        vatDefaultSurcharge,
        BigDecimal(300.00),
        LocalDate.parse("2000-05-10"),
        LocalDate.parse("2000-02-01"),
        LocalDate.parse("2000-03-28"),
        "#004",
        overdue
      )
    }

    "the payment is not overdue" should {

      val model = generateModel(overdue = false)
      lazy val view = views.html.templates.payments.whatYouOweCharge(model, 0, Some(true))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the correct description text" in {
        elementText(Selectors.description) shouldBe
          "this is a surcharge for late payment of your 1 February to 28 March 2000 return"
      }

      "display the correct pay text" in {
        elementText(Selectors.payText) shouldBe "Pay now"
      }

      "have the correct pay link context" in {
        elementText(Selectors.payContext) shouldBe
          "£300 , this is a surcharge for late payment of your 1 February to 28 March 2000 return"
      }

      "not display the view return link" in {
        intercept[org.scalatest.exceptions.TestFailedException](element(Selectors.viewReturnLink))
      }
    }

    "the payment is overdue" should {

      val model = generateModel(overdue = true)
      lazy val view = views.html.templates.payments.whatYouOweCharge(model, 0, Some(true))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct pay link context" in {
        elementText(Selectors.payContext) shouldBe
          "£300 is overdue, this is a surcharge for late payment of your 1 February to 28 March 2000 return"
      }
    }
  }

  "Rendering a central assessment charge" when {

    def generateModel(overdue: Boolean): OpenPaymentsModel = {
      OpenPaymentsModel(
        vatCentralAssessment,
        BigDecimal(200.00),
        LocalDate.parse("2001-05-10"),
        LocalDate.parse("2001-02-01"),
        LocalDate.parse("2001-03-28"),
        "#005",
        overdue
      )
    }

    "the payment is not overdue" should {

      val model = generateModel(overdue = false)
      lazy val view = views.html.templates.payments.whatYouOweCharge(model, 0, Some(true))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the correct description text" in {
        elementText(Selectors.description) shouldBe
          "this is our estimate for 1 February to 28 March 2001, " +
        "submit your overdue return to update this amount"
      }

      "display the correct pay text" in {
        elementText(Selectors.payText) shouldBe "Pay estimate"
      }

      "have the correct pay link context" in {
        elementText(Selectors.payContext) shouldBe
          "£200 , this is our estimate for 1 February to 28 March 2001, " +
        "submit your overdue return to update this amount"
      }

      "not display the view return link" in {
        intercept[org.scalatest.exceptions.TestFailedException](element(Selectors.viewReturnLink))
      }
    }

    "the payment is overdue" should {

      val model = generateModel(overdue = true)
      lazy val view = views.html.templates.payments.whatYouOweCharge(model, 0, Some(true))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct pay link context" in {
        elementText(Selectors.payContext) shouldBe
          "£200 is overdue, this is our estimate for 1 February to 28 March 2001, " +
            "submit your overdue return to update this amount"
      }
    }
  }

}
