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

package models

import play.api.libs.json.Json
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class DirectDebitStatusSpec extends AnyWordSpecLike with Matchers {

  "Direct debit JSON should parse to a DirectDebitStatus model correctly" when {

    "all expected JSON fields are present and there is one item in the DDI details array" in {
      val json = Json.obj(
        "directDebitMandateFound" -> true,
        "directDebitDetails" -> Json.arr(
          Json.obj("dateCreated" -> "2018-01-01")
        )
      )
      json.as[DirectDebitStatus] shouldBe
        DirectDebitStatus(directDebitMandateFound = true, Some(Seq(DDIDetails("2018-01-01"))))
    }

    "all expected JSON fields are present and there are multiple items in the DDI details array" in {
      val json = Json.obj(
        "directDebitMandateFound" -> true,
        "directDebitDetails" -> Json.arr(
          Json.obj("dateCreated" -> "2018-01-01"),
          Json.obj("dateCreated" -> "2018-02-02"),
          Json.obj("dateCreated" -> "2018-03-03")
        )
      )
      json.as[DirectDebitStatus] shouldBe
        DirectDebitStatus(
          directDebitMandateFound = true,
          Some(Seq(DDIDetails("2018-01-01"), DDIDetails("2018-02-02"), DDIDetails("2018-03-03")))
        )
    }

    "the optional DDI details field is not present" in {
      val json = Json.obj("directDebitMandateFound" -> false)
      json.as[DirectDebitStatus] shouldBe DirectDebitStatus(directDebitMandateFound = false, None)
    }
  }
}
