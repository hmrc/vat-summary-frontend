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

import common.TestModels.navContent
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.{JsObject, Json}

class NavLinksSpec extends AnyWordSpecLike with Matchers {

  "NavContent" should {

    val navContentJson: JsObject = Json.obj(
      "home" -> Json.obj(
        "en" -> "Home",
        "cy" -> "Hafan",
        "url" -> "http://localhost:9999/home"
      ),
      "account" -> Json.obj(
        "en" -> "Account",
        "cy" -> "Crfrif",
        "url" -> "http://localhost:9999/account"
      ),
      "messages" -> Json.obj(
        "en" -> "Messages",
        "cy" -> "Negeseuon",
        "url" -> "http://localhost:9999/messages",
        "alerts" -> 1
      ),
      "help" -> Json.obj(
        "en" -> "Help",
        "cy" -> "Cymorth",
        "url" -> "http://localhost:9999/help"
      )
    )

    "instantiate itself from reading appropriate JSON" in {
      navContentJson.as[NavContent] shouldBe navContent
    }
  }

  "NavLinks" should {

    val json = Json.obj(
      "en" -> "Messages",
      "cy" -> "Negeseuon",
      "url" -> "http://localhost:9999/messages",
      "alerts" -> 99
    )
    val model = NavLinks("Messages", "Negeseuon", "http://localhost:9999/messages", Some(99))

    "instantiate itself from reading appropriate JSON" in {
      json.as[NavLinks] shouldBe model
    }
  }
}
