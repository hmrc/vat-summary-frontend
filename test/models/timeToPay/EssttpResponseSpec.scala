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

<<<<<<<< HEAD:test/models/TTPResponseModelSpec.scala
package models
========
package models.timeToPay
>>>>>>>> deb03f18 (Parser tests completed):test/models/timeToPay/EssttpResponseSpec.scala

import models.essttp.TTPResponseModel
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json

class TTPResponseModelSpec extends AnyWordSpecLike with Matchers{

<<<<<<<< HEAD:test/models/TTPResponseModelSpec.scala
  val timeToPayResponseModel : TTPResponseModel = TTPResponseModel("592d4a09cdc8e04b00021459","www.TestWebsite.co.uk")
========
  val essttpResponseModel : TimeToPayResponse = TimeToPayResponse("592d4a09cdc8e04b00021459","www.TestWebsite.co.uk")
>>>>>>>> deb03f18 (Parser tests completed):test/models/timeToPay/EssttpResponseSpec.scala

  val timeToPayResponseJson = Json.obj("journeyId" -> "592d4a09cdc8e04b00021459",
                                           "nextUrl" -> "www.TestWebsite.co.uk")

  "TimeToPayResponse" should {

    "parse from json" in {

<<<<<<<< HEAD:test/models/TTPResponseModelSpec.scala
      timeToPayResponseJson.as[TTPResponseModel] shouldBe timeToPayResponseModel
========
        essttpResponseJson.as[TimeToPayResponse] shouldBe essttpResponseModel
>>>>>>>> deb03f18 (Parser tests completed):test/models/timeToPay/EssttpResponseSpec.scala

    }

    "parse to json" in {

      Json.toJson(timeToPayResponseModel) shouldBe timeToPayResponseJson

    }

  }


}
