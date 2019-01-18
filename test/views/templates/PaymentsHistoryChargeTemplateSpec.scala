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

package views.templates

import org.jsoup.Jsoup
import java.time.LocalDate

import models.payments._
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import models.viewModels.PaymentsHistoryModel
import views.templates.PaymentsHistoryChargeHelper.VatAADefaultInterest

class PaymentsHistoryChargeTemplateSpec extends ViewBaseSpec {

  object Selectors {
    val tableRow = "tr"
    val clearedDate = "tr td:nth-of-type(1)"
    val chargeTitle = "tr td:nth-of-type(2) span.bold"
    val description = "tr td:nth-of-type(2) span:nth-of-type(2)"
    val amount = "tr td:nth-of-type(3)"
    val errorText = "tr td:nth-of-type(2)"
  }

  "The chargeTypes template" when {

    "there is a valid charge type" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        ReturnDebitCharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        123456,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = views.html.templates.paymentsHistoryCharge(model)
      lazy implicit val document: Document = Jsoup.parse(
        s"<table>${template.body}</table>"
      )

      "display the correct clearing date" in {
        elementText(Selectors.clearedDate) shouldBe "14 Feb 2018"
      }

      "display the correct amount" in {
        elementText(Selectors.amount) shouldBe "- £123,456"
      }
    }

    "there is a vat return debit charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        ReturnDebitCharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        123456,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = views.html.templates.paymentsHistoryCharge(model)
      lazy implicit val document: Document = Jsoup.parse(
        s"<table>${template.body}</table>"
      )

      "display the correct table row class" in {
        element(Selectors.tableRow).attr("class") shouldBe ""
      }

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Return"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for the period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a vat return credit charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        ReturnCreditCharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        -123456,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = views.html.templates.paymentsHistoryCharge(model)
      lazy implicit val document: Document = Jsoup.parse(
        s"<table>${template.body}</table>"
      )

      "display the correct table row class" in {
        element(Selectors.tableRow).attr("class") shouldBe ""
      }

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Repayment from HMRC"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for your 12 Jan to 23 Mar 2018 return"
      }
    }

    "there is a vat officer assessment debit charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        OADebitCharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        123456,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = views.html.templates.paymentsHistoryCharge(model)
      lazy implicit val document: Document = Jsoup.parse(
        s"<table>${template.body}</table>"
      )

      "display the correct table row class" in {
        element(Selectors.tableRow).attr("class") shouldBe ""
      }

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "VAT officer's investigation"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for underpaying by this amount"
      }
    }

    "there is a vat officer assessment credit charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        OACreditCharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        123456,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = views.html.templates.paymentsHistoryCharge(model)
      lazy implicit val document: Document = Jsoup.parse(
        s"<table>${template.body}</table>"
      )

      "display the correct table row class" in {
        element(Selectors.tableRow).attr("class") shouldBe ""
      }

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "VAT officer's investigation"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for overpaying by this amount"
      }
    }

    "there is a vat central assessment charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        CentralAssessmentCharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        -123456,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = views.html.templates.paymentsHistoryCharge(model)
      lazy implicit val document: Document = Jsoup.parse(
        s"<table>${template.body}</table>"
      )

      "display the correct table row class" in {
        element(Selectors.tableRow).attr("class") shouldBe ""
      }

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Estimate"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for your 12 Jan to 23 Mar 2018 return"
      }
    }

    "there is a vat default surcharge charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        DefaultSurcharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        -123456,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = views.html.templates.paymentsHistoryCharge(model)
      lazy implicit val document: Document = Jsoup.parse(
        s"<table>${template.body}</table>"
      )

      "display the correct table row class" in {
        element(Selectors.tableRow).attr("class") shouldBe ""
      }

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Surcharge"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for late payment of your 12 Jan to 23 Mar 2018 return"
      }
    }

    "there is an error correction credit charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        ErrorCorrectionCreditCharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        1000,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = views.html.templates.paymentsHistoryCharge(model)
      lazy implicit val document: Document = Jsoup.parse(
        s"<table>${template.body}</table>"
      )

      "display the correct table row class" in {
        element(Selectors.tableRow).attr("class") shouldBe ""
      }

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Error correction repayment from HMRC"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for correcting your 12 Jan to 23 Mar 2018 return"
      }
    }

    "there is an error correction debit charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        ErrorCorrectionDebitCharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        2000,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = views.html.templates.paymentsHistoryCharge(model)
      lazy implicit val document: Document = Jsoup.parse(
        s"<table>${template.body}</table>"
      )

      "display the correct table row class" in {
        element(Selectors.tableRow).attr("class") shouldBe ""
      }

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Error correction payment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for correcting your 12 Jan to 23 Mar 2018 return"
      }
    }

    "there is a vat officer assessment default interest charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        OADefaultInterestCharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        123456,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = views.html.templates.paymentsHistoryCharge(model)
      lazy implicit val document: Document = Jsoup.parse(
        s"<table>${template.body}</table>"
      )

      "display the correct table row class" in {
        element(Selectors.tableRow).attr("class") shouldBe ""
      }

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "VAT officer's assessment interest"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "interest charged on the officer's assessment"
      }
    }

    "there is a VAT Officer Assessment Further Interest charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        VatOfficersAssessmentFurtherInterestCharge,
        Some(LocalDate.parse("2018-02-12")),
        Some(LocalDate.parse("2018-03-24")),
        1500.00,
        Some(LocalDate.parse("2018-04-18"))
      )

      lazy val template = views.html.templates.paymentsHistoryCharge(model)
      lazy implicit val document: Document = Jsoup.parse(
        s"<table>${template.body}</table>"
      )

      "display the correct table row class" in {
        element(Selectors.tableRow).attr("class") shouldBe ""
      }

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "VAT officer's assessment further interest"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "further interest charged on the officer's assessment"
      }
    }

    "there is a VAT Additional Assessment charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        AACharge,
        Some(LocalDate.parse("2018-01-01")),
        Some(LocalDate.parse("2018-04-01")),
        2000.00,
        Some(LocalDate.parse("2018-05-01"))
      )

      lazy val template = views.html.templates.paymentsHistoryCharge(model)
      lazy implicit val document: Document = Jsoup.parse(
        s"<table>${template.body}</table>"
      )

      "display the correct table row class" in {
        element(Selectors.tableRow).attr("class") shouldBe ""
      }

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Additional assessment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "additional assessment based on further information" +
          " for the period 1 Jan to 1 Apr 2018"
      }
    }


    "there is a VAT AA Default Interest charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        AAInterestCharge,
        Some(LocalDate.parse("2018-01-01")),
        Some(LocalDate.parse("2018-04-01")),
        2000.00,
        Some(LocalDate.parse("2018-05-01"))
      )

      lazy val template = views.html.templates.paymentsHistoryCharge(model)
      lazy implicit val document: Document = Jsoup.parse(
        s"<table>${template.body}</table>"
      )

      "display the correct table row class" in {
        element(Selectors.tableRow).attr("class") shouldBe ""
      }

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Additional assessment interest"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "interest charged on additional tax assessed" +
          " for the period 1 Jan to 1 Apr 2018"
      }
    }
  }
}
