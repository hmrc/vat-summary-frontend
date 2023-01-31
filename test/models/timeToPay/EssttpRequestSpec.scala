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

<<<<<<<< HEAD:test/models/TTPRequestModelSpec.scala
package models
========
package models.timeToPay
>>>>>>>> deb03f18 (Parser tests completed):test/models/timeToPay/EssttpRequestSpec.scala

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

class TTPRequestModelSpec extends AnyWordSpec with Matchers {

<<<<<<<< HEAD:test/models/TTPRequestModelSpec.scala
  "TTPRequestModel" should {

    "write to JSON" in {
      val model = TTPRequestModel("/return-url", "/back-url")
      val expectedJson = Json.obj("returnUrl" -> "/return-url", "backUrl" -> "/back-url")
========
  val essttpRequestModel : TimeToPayRequest = TimeToPayRequest("www.TestWebsite.co.uk","www.TestWebsite.co.uk")

  val essttpRequestJson = Json.obj("returnUrl" -> "www.TestWebsite.co.uk",
                                          "backUrl" -> "www.TestWebsite.co.uk")

  "TimeToPayRequest" should {

    "parse from json" in {

        essttpRequestJson.as[TimeToPayRequest] shouldBe essttpRequestModel
>>>>>>>> deb03f18 (Parser tests completed):test/models/timeToPay/EssttpRequestSpec.scala

      Json.toJson(model) shouldBe expectedJson
    }
  }
}
