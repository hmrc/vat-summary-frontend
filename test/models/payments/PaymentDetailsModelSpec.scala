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

package models.payments

import play.api.libs.json._
import uk.gov.hmrc.play.test.UnitSpec

class PaymentDetailsModelSpec extends UnitSpec {

  "PaymentDetailsModel.apply" when {
    "given taxPeriod details when converted to JSON" should {
      "result in the correct JSON format" in {
        val payment = PaymentDetailsModel(
          taxType = "vat",
          taxReference = "123456789",
          amountInPence = 123456,
          taxPeriodMonth = 3,
          taxPeriodYear = 2018,
          returnUrl = "https://www.tax.service.gov.uk/mtdfb-page",
          backUrl = "https://www.tax.service.gov.uk/mtdfb-page2",
          chargeType = ReturnDebitCharge,
          dueDate = "2018-08-08"
        )

        val expectedJson = Json.parse(
          """
            |{
            |  "taxType": "vat",
            |  "reference": "123456789",
            |  "amountInPence": 123456,
            |  "extras" : {
            |    "vatPeriod" : {
            |      "month" : 3,
            |      "year" : 2018
            |    },
            |    "chargeType": "VAT Return Debit Charge",
            |    "dueDate": "2018-08-08"
            |  },
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
          dueDate = "2018-08-08"
        )

        val expectedJson = Json.parse(
          """
            |{
            |  "taxType": "vat",
            |  "reference": "123456789",
            |  "amountInPence": 123456,
            |  "extras" : {
            |    "chargeType": "VAT Return Debit Charge",
            |    "dueDate": "2018-08-08"
            |  },
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
}
