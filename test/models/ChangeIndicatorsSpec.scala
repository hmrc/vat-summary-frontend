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

import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json
import org.scalatest.matchers.should.Matchers

class ChangeIndicatorsSpec extends AnyWordSpecLike with Matchers {

  "ChangeindicatorsModel" should {

    "deserialize from JSON" when {
      "deregister is true" in {
        Json.obj("deregister" -> true).as[ChangeIndicators] shouldBe ChangeIndicators(true)
      }
      "deregister is false" in {
        Json.obj("deregister" -> false).as[ChangeIndicators] shouldBe ChangeIndicators(false)
      }
    }

    "serialize to JSON" when {
      "ChangeIndicator is true" in {
        Json.toJson(ChangeIndicators(true)) shouldBe Json.obj("deregister" -> true)
      }

      "ChangeIndicator is false" in {
        Json.toJson(ChangeIndicators(false)) shouldBe Json.obj("deregister" -> false)
      }
    }
  }
}
