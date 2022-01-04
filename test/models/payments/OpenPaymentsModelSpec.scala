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

package models.payments

import java.time.LocalDate

import play.api.libs.json.Json
import views.ViewBaseSpec

class OpenPaymentsModelSpec extends ViewBaseSpec {

  val openPaymentsModelWithPeriod: OpenPaymentsModel = OpenPaymentsModel(
    payment = Payment(
      chargeType = OADebitCharge,
      periodFrom = LocalDate.parse("2001-01-01"),
      periodTo = LocalDate.parse("2001-03-31"),
      due = LocalDate.parse("2003-04-05"),
      outstandingAmount = 300.00,
      periodKey = Some("#003"),
      chargeReference = None,
      ddCollectionInProgress = false
    ),
    isOverdue = false
  )

  val openPaymentsModelNoPeriod: OpenPaymentsModel = OpenPaymentsModel(
    payment = Payment(
      chargeType = OADebitCharge,
      due = LocalDate.parse("2003-04-05"),
      outstandingAmount = 300.00,
      periodKey = Some("#003"),
      chargeReference = None,
      ddCollectionInProgress = false
    ),
    isOverdue = false
  )

  "OpenPaymentsModel" when {

    "calling .apply method" when {

      "given a Payment with a start and end date" should {

        "create an OpenPaymentsModelWithPeriod" in {

          val regularApply = OpenPaymentsModel(
            chargeType = OADebitCharge,
            amount = 300.00,
            due = LocalDate.parse("2003-04-05"),
            periodFrom = LocalDate.parse("2001-01-01"),
            periodTo = LocalDate.parse("2001-03-31"),
            periodKey = "#003",
            isOverdue = false
          )

          openPaymentsModelWithPeriod shouldBe regularApply
        }
      }

      "given a Payment without a start and end date" should {

        "create an OpenPaymentsModelNoPeriod" in {

          val regularApply = OpenPaymentsModel(
            chargeType = OADebitCharge,
            amount = 300.00,
            due = LocalDate.parse("2003-04-05"),
            periodKey = "#003",
            isOverdue = false
          )

          openPaymentsModelNoPeriod shouldBe regularApply
        }
      }
    }

    "calling .writes method" when {

      "supplied model creates an OpenPaymentsModelWithPeriod" should {

        val expectedJson = Json.parse(
          """ {
            |   "paymentType" : "VAT OA Debit Charge",
            |   "amount" : 300,
            |   "due" : "2003-04-05",
            |   "periodFrom" : "2001-01-01",
            |   "periodTo" : "2001-03-31",
            |   "periodKey" : "#003"
            | }
          """.stripMargin
        )

        "parse to JSON correctly" in {
          OpenPaymentsModel.writes.writes(openPaymentsModelWithPeriod) shouldBe expectedJson
        }
      }

      "supplied model is OpenPaymentsModelNoPeriod" should {

        val expectedJson = Json.parse(
          """ {
            |   "paymentType" : "VAT OA Debit Charge",
            |   "amount" : 300,
            |   "due" : "2003-04-05",
            |   "periodKey" : "#003"
            | }
          """.stripMargin
        )

        "parse to JSON correctly" in {
          OpenPaymentsModel.writes.writes(openPaymentsModelNoPeriod) shouldBe expectedJson
        }
      }
    }
  }

  "OpenPaymentsModelNoPeriod" when {

    "calling .makePaymentRedirect" should {

      "return a correctly formatted payment redirect URL" in {
        openPaymentsModelNoPeriod.makePaymentRedirect shouldBe controllers.routes.MakePaymentController.makePaymentNoPeriod(
          30000,
          "VAT OA Debit Charge",
          "2003-04-05",
          "noCR"
        ).url
      }
    }

    "calling .writes" should {

      val model = OpenPaymentsModelNoPeriod(
        chargeType = OADebitCharge,
        due = LocalDate.parse("2003-04-05"),
        amount = 300.00,
        periodKey = "#003",
        chargeReference = Some("XD002750002155"),
        isOverdue = false
      )

      val expectedJson = Json.parse(
        """ {
          |   "paymentType" : "VAT OA Debit Charge",
          |   "amount" : 300,
          |   "due" : "2003-04-05",
          |   "periodKey" : "#003"
          | }
        """.stripMargin
      )

      "parse to JSON correctly" in {
        OpenPaymentsModelNoPeriod.writes.writes(model) shouldBe expectedJson
      }
    }
  }

  "OpenPaymentsModelWithPeriod" when {

    "calling .makePaymentRedirect" should {

      "return a correctly formatted payment redirect URL" in {
        openPaymentsModelWithPeriod.makePaymentRedirect shouldBe controllers.routes.MakePaymentController.makePayment(
          30000,
          3,
          2001,
          "2001-03-31",
          "VAT OA Debit Charge",
          "2003-04-05",
          "noCR"
        ).url
      }
    }

    "calling .writes" should {

      val model = OpenPaymentsModelWithPeriod(
        chargeType = OADebitCharge,
        due = LocalDate.parse("2003-04-05"),
        periodFrom = LocalDate.parse("2001-01-01"),
        periodTo = LocalDate.parse("2001-03-31"),
        amount = 300.00,
        periodKey = "#003",
        chargeReference = Some("XD002750002155"),
        isOverdue = false
      )

      val expectedJson = Json.parse(
        """ {
          |   "paymentType" : "VAT OA Debit Charge",
          |   "amount" : 300,
          |   "due" : "2003-04-05",
          |   "periodFrom" : "2001-01-01",
          |   "periodTo" : "2001-03-31",
          |   "periodKey" : "#003"
          | }
        """.stripMargin
      )

      "parse to JSON correctly" in {
        OpenPaymentsModelWithPeriod.writes.writes(model) shouldBe expectedJson
      }
    }
  }
}
