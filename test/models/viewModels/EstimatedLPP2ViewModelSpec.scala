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

package models.viewModels

import common.TestModels.{estimatedLPP2Json, estimatedLPP2Model}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json
import views.ViewBaseSpec

class EstimatedLPP2ViewModelSpec extends ViewBaseSpec with AnyWordSpecLike with Matchers {

  "The EstimatedLPP2ViewModel" should {

    "read from JSON" in {
      estimatedLPP2Json.as[EstimatedLPP2ViewModel] shouldBe estimatedLPP2Model
    }

    "write to JSON" in {
      Json.toJson(estimatedLPP2Model) shouldBe estimatedLPP2Json
    }

    "title()" when {

      "the charge type is valid" should {

        "return the charge type title" in {
          estimatedLPP2Model.title(messages) shouldBe
            "Penalty for late payment of VAT"
        }
      }
    }
  }
}
