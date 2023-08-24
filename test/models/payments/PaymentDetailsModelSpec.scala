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

package models.payments

import common.GuiceBox
import config.AppConfig
import models.User
import play.api.libs.json._
import services.DateService

class PaymentDetailsModelSpec extends GuiceBox {

  implicit val appConfig: AppConfig = inject[AppConfig]
  implicit val dateService: DateService = inject[DateService]

  val testTaxType = "vat"
  val testVrn = "123456789"
  val testGenericAmountInPence = "0"
  val testReturnUrl: String = appConfig.paymentsReturnUrl
  val testBackUrl: String = appConfig.paymentsBackUrl
  val testGenericChargeType = "Payment on account"
  val testDueDate = "2023-09-01"

  implicit val testUser: User = User(vrn = testVrn)

  val testPaymentDetailsModelGeneric: PaymentDetailsModelGeneric = PaymentDetailsModelGeneric(Some(testDueDate))
  val testPaymentDetailsModelGenericNoDate: PaymentDetailsModelGeneric = PaymentDetailsModelGeneric(None)

  "PaymentDetailsModel.apply" when {
    "not given a chargeReference" when {
      "given taxPeriod details when converted to JSON" should {
        "result in the correct JSON format" in {
          val payment = PaymentDetailsModel(
            taxType = "vat",
            taxReference = "123456789",
            amountInPence = 123456,
            taxPeriodMonth = 3,
            taxPeriodYear = 2018,
            vatPeriodEnding = "2018-08-08",
            returnUrl = "https://www.tax.service.gov.uk/mtdfb-page",
            backUrl = "https://www.tax.service.gov.uk/mtdfb-page2",
            chargeType = ReturnDebitCharge,
            dueDate = "2018-08-08",
            chargeReference = None
          )

          val expectedJson = Json.parse(
            """
              |{
              |  "vrn": "123456789",
              |  "amountInPence": 123456,
              |  "dueDate": "2018-08-08",
              |  "vatPeriodEnding": "2018-08-08",
              |  "returnUrl": "https://www.tax.service.gov.uk/mtdfb-page",
              |  "backUrl": "https://www.tax.service.gov.uk/mtdfb-page2"
              |}
          """.stripMargin
          )

          val actualJson = Json.toJson(payment)(PaymentDetailsModel.writes)

          actualJson mustBe expectedJson
        }
      }

      "not given taxPeriod details when converted to JSON" should {
        "result in the correct JSON format" in {
          val payment = PaymentDetailsModel(
            taxType = "vat",
            taxReference = "123456789",
            amountInPence = 123456,
            returnUrl = "https://www.tax.service.gov.uk/mtdfb-page",
            backUrl = "https://www.tax.service.gov.uk/mtdfb-page2",
            chargeType = ReturnDebitCharge,
            dueDate = "2018-08-08",
            chargeReference = None
          )

          val expectedJson = Json.parse(
            """
              |{
              |  "vrn": "123456789",
              |  "amountInPence": 123456,
              |  "dueDate": "2018-08-08",
              |  "returnUrl": "https://www.tax.service.gov.uk/mtdfb-page",
              |  "backUrl": "https://www.tax.service.gov.uk/mtdfb-page2"
              |}
          """.stripMargin
          )

          val actualJson = Json.toJson(payment)(PaymentDetailsModel.writes)

          actualJson mustBe expectedJson
        }
      }
    }

    "given a chargeReference" should {
      "result in the correct JSON format" in {
        val payment = PaymentDetailsModel(
          taxType = "vat",
          taxReference = "123456789",
          amountInPence = 123456,
          returnUrl = "https://www.tax.service.gov.uk/mtdfb-page",
          backUrl = "https://www.tax.service.gov.uk/mtdfb-page2",
          chargeType = ReturnDebitCharge,
          dueDate = "2018-08-08",
          chargeReference = Some("XD002750002155")
        )

        val expectedJson = Json.parse(
          """
            |{
            |  "vrn": "123456789",
            |  "amountInPence": 123456,
            |  "dueDate": "2018-08-08",
            |  "returnUrl": "https://www.tax.service.gov.uk/mtdfb-page",
            |  "backUrl": "https://www.tax.service.gov.uk/mtdfb-page2",
            |  "chargeReference": "XD002750002155"
            |}
          """.stripMargin
        )

        val actualJson = Json.toJson(payment)(PaymentDetailsModel.writes)

        actualJson mustBe expectedJson
      }
    }
  }

  "PaymentDetailsModelGeneric" when {

    "earliestDueDate is provided" must {

      "result in json including the provided earliestDueDate" in {

        val expectedResult = Json.obj(
          "vrn" -> testVrn,
          "amountInPence" -> testGenericAmountInPence,
          "returnUrl" -> testReturnUrl,
          "backUrl" -> testBackUrl,
          "chargeType" -> testGenericChargeType,
          "dueDate" -> testDueDate
        )
        val actualResult = Json.toJson(testPaymentDetailsModelGeneric)

        expectedResult mustBe actualResult
      }

      "result in audit model including the provided earliestDueDate" in {

        val expectedResult = Map(
          "taxType" -> testTaxType,
          "taxReference" -> testVrn,
          "amountInPence" -> testGenericAmountInPence,
          "returnUrl" -> testReturnUrl,
          "backUrl" -> testBackUrl,
          "chargeType" -> testGenericChargeType,
          "dueDate" -> testDueDate
        )
        val actualResult = testPaymentDetailsModelGeneric.auditDetail

        expectedResult mustBe actualResult
      }
    }

    "earliestDueDate is not provided" must {

      "result in json due date is today's date" in {

        val expectedResult = Json.obj(
          "vrn" -> testVrn,
          "amountInPence" -> testGenericAmountInPence,
          "returnUrl" -> testReturnUrl,
          "backUrl" -> testBackUrl,
          "chargeType" -> testGenericChargeType,
          "dueDate" -> dateService.now().toString
        )
        val actualResult = Json.toJson(testPaymentDetailsModelGenericNoDate)

        expectedResult mustBe actualResult
      }

      "result in audit model due date is today's date" in {

        val expectedResult = Map(
          "taxType" -> testTaxType,
          "taxReference" -> testVrn,
          "amountInPence" -> testGenericAmountInPence,
          "returnUrl" -> testReturnUrl,
          "backUrl" -> testBackUrl,
          "chargeType" -> testGenericChargeType,
          "dueDate" -> dateService.now().toString
        )
        val actualResult = testPaymentDetailsModelGenericNoDate.auditDetail

        expectedResult mustBe actualResult
      }
    }
  }
}
