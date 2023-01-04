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

package models.viewModels

import common.TestModels._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json
import views.ViewBaseSpec

class EstimatedLPP1ViewModelSpec extends ViewBaseSpec with AnyWordSpecLike with Matchers {

  "The EstimatedLPP1ViewModel" should {

    "read from JSON" in {
      estimatedLPP1Json.as[EstimatedLPP1ViewModel] shouldBe estimatedLPP1Model
    }

    "write to JSON" in {
      Json.toJson(estimatedLPP1Model) shouldBe estimatedLPP1Json
    }
  }
}
