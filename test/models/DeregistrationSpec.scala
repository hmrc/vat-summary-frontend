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

package models

import java.time.LocalDate

import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json
import org.scalatest.matchers.should.Matchers

class DeregistrationSpec extends AnyWordSpecLike with Matchers {

  "Deregistration Model" should {

    "deserialize from JSON" when {

      "date of cancellation is supplied" in {
        Json.obj("effectDateOfCancellation" -> "2020-01-01").as[Deregistration] shouldBe Deregistration(Some(LocalDate.parse("2020-01-01")))
      }

      "date of cancellation is not supplied" in {
        Json.obj().as[Deregistration] shouldBe Deregistration(None)
      }
    }

    "serialize to JSON" when {

      "date of cancellation is supplied" in {
        Json.toJson(Deregistration(Some(LocalDate.parse("2020-01-01")))) shouldBe Json.obj("effectDateOfCancellation" -> "2020-01-01")
      }

      "date of cancellation is not supplied" in {
        Json.toJson(Deregistration(None)) shouldBe Json.obj()
      }
    }
  }
}
