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

  "Payment details when converted to JSON" should {
    "result in the correct JSON format" in {
      val payment = PaymentDetailsModel(
        "vat",
        "123456789",
        123456,
        3,
        2018,
        "https://www.tax.service.gov.uk/mtdfb-page"
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
          |    }
          |  },
          |  "returnUrl": "https://www.tax.service.gov.uk/mtdfb-page"
          |}
        """.stripMargin
      ).toString()

      val actualJson = PaymentDetailsModel.writes.writes(payment).toString()

      actualJson shouldBe expectedJson
    }
  }
}
