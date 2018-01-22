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

package models

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.play.test.UnitSpec

class CustomerInformationSpec extends UnitSpec {

  "A CustomerInformation object" should {

    val exampleCustomerInfo: CustomerInformation = CustomerInformation("Cheapo Clothing Ltd")
    val exampleJson: JsValue = Json.parse("""{"tradingName":"Cheapo Clothing Ltd"}""")

    "parse to JSON" in {
      val result = Json.toJson(exampleCustomerInfo)
      result shouldBe exampleJson
    }

    "be parsed from appropriate JSON" in {
      val result = exampleJson.as[CustomerInformation]
      result shouldBe exampleCustomerInfo
    }
  }
}
