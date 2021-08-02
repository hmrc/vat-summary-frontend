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

package models.payments

import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json._
import org.scalatest.matchers.should.Matchers

class PaymentDetailsModelSpec extends AnyWordSpecLike with Matchers  {

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

          actualJson shouldBe expectedJson
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

          actualJson shouldBe expectedJson
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

        actualJson shouldBe expectedJson
      }
    }
  }
}
