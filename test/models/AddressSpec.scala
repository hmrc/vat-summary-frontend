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

package models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.{JsObject, Json}

class AddressSpec extends AnyWordSpecLike with Matchers {

  val correctMaxJson: JsObject = Json.obj(
    "address" -> Json.obj(
      "line1" -> "Bedrock Quarry",
        "line2" -> "Bedrock",
        "line3" -> "Graveldon",
        "postCode" -> "GV2 4BB"
    )
  )

  val correctMinJson: JsObject = Json.obj(
    "address" -> Json.obj(
    "line1" -> "Bedrock Quarry"
    )
  )

  val correctJsonNoLine1: JsObject = Json.obj("address" -> Json.obj())

  "Address" should {

    "correctly parse from max json" in {
      correctMaxJson.as[Address] shouldBe Address("Bedrock Quarry", Some("Bedrock"), Some("Graveldon"), None, Some("GV2 4BB"))
    }

    "correctly parse from min json" in {
      correctMinJson.as[Address] shouldBe Address("Bedrock Quarry", None, None, None, None)
    }

    "correctly parse when address line 1 is missing" in {
      correctJsonNoLine1.as[Address] shouldBe Address("", None, None, None, None)
    }
  }

}
