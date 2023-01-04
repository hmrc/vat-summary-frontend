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
import play.api.libs.json.{JsObject, Json}
import org.scalatest.matchers.should.Matchers

class EmailSpec extends AnyWordSpecLike with Matchers {

  val correctMaxJson: JsObject = Json.obj(
    "emailAddress" -> "asdf@asdf.com",
    "emailVerified" -> true
  )

  val correctMinJson: JsObject = Json.obj()

  "Email" should {

    "correctly parse from max json" in {
      correctMaxJson.as[Email] shouldBe Email(Some("asdf@asdf.com"), Some(true))
    }

    "correctly parse from min json" in {
      correctMinJson.as[Email] shouldBe Email(None, None)
    }

  }

}
