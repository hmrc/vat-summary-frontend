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

import models.essttp.TTPResponseModel
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json

class TTPResponseModelSpec extends AnyWordSpecLike with Matchers{

  val timeToPayResponseModel : TTPResponseModel = TTPResponseModel("592d4a09cdc8e04b00021459","www.TestWebsite.co.uk")

  val timeToPayResponseJson = Json.obj("journeyId" -> "592d4a09cdc8e04b00021459",
                                           "nextUrl" -> "www.TestWebsite.co.uk")

  "EssttpResponse" should {

    "parse from json" in {

      timeToPayResponseJson.as[TTPResponseModel] shouldBe timeToPayResponseModel

    }

    "parse to json" in {

      Json.toJson(timeToPayResponseModel) shouldBe timeToPayResponseJson

    }

  }


}
