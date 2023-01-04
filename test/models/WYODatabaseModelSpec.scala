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

import common.TestModels.{wyoDBjsonModel, wyoStandardDBModel}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json

class WYODatabaseModelSpec extends AnyWordSpecLike with Matchers {


  "WYODatabaseModel" should {

    "correctly parse to Json" in {
      Json.toJson(wyoStandardDBModel) shouldBe wyoDBjsonModel
    }

    "correctly parse from Json" in {
      wyoDBjsonModel.as[WYODatabaseModel] shouldBe wyoStandardDBModel
    }
  }
}
