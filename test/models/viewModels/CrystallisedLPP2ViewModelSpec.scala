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

import common.TestModels.{crystallisedLPP2Json, crystallisedLPP2Model}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json
import views.ViewBaseSpec

class CrystallisedLPP2ViewModelSpec extends ViewBaseSpec with AnyWordSpecLike with Matchers {

  "The makePaymentRedirect value" should {

    "be a payment handoff URL generated from the model's parameters" in {
      val amountInPence = (crystallisedLPP2Model.leftToPay * 100).toLong
      val chargeTypeEncoded = crystallisedLPP2Model.chargeType.replace(" ", "%20")

      crystallisedLPP2Model.makePaymentRedirect should include(
        s"/make-payment/$amountInPence/${crystallisedLPP2Model.periodTo.getMonthValue}/" +
          s"${crystallisedLPP2Model.periodTo.getYear}/${crystallisedLPP2Model.periodTo}/$chargeTypeEncoded/" +
          s"${crystallisedLPP2Model.dueDate}/${crystallisedLPP2Model.chargeReference}"
      )
    }
  }

  "The CrystallisedLPP2ViewModel" should {

    "read from JSON" when {

      "all fields are populated" in {
        crystallisedLPP2Json.as[CrystallisedLPP2ViewModel] shouldBe crystallisedLPP2Model
      }
    }

    "write to JSON" when {

      "all fields are populated" in {
        Json.toJson(crystallisedLPP2Model) shouldBe crystallisedLPP2Json
      }
    }

    "title()" when {

      "the charge type is valid" should {

        "return the charge type title" in {
          crystallisedLPP2Model.title(messages) shouldBe
            "Second penalty for late payment of additional assessment"
        }
      }
    }
  }
}
