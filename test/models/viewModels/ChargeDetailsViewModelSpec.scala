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
import views.ViewBaseSpec

class ChargeDetailsViewModelSpec extends ViewBaseSpec with AnyWordSpecLike with Matchers {

  "The generateHash function" should {

    "generate a consistent MD5 hash from a given view model and VRN" in {
      whatYouOweCharge.generateHash("999999999") shouldBe "203f0fcfa5ab35870e6bf6b00cd556ef"
    }

    "generate unique MD5 hashes" when {

      "the view model types differ" in {
        val hash1 = whatYouOweCharge.generateHash("999999999")
        val hash2 = crystallisedLPICharge.generateHash("999999999")
        val hash3 = estimatedLPIModel.generateHash("999999999")

        Seq(hash1, hash2, hash3).distinct.length shouldBe 3
      }

      "a parameter value differs" in {
        val oneHundredHashes = (1 to 100).map { num =>
          whatYouOweCharge.copy(originalAmount = num).generateHash("999999999")
        }

        oneHundredHashes.distinct.length shouldBe 100
      }

      "the VRNs differ" in {
        val oneHundredHashes = (999999001 to 999999100).map { vrn =>
          whatYouOweCharge.generateHash(vrn.toString)
        }

        oneHundredHashes.distinct.length shouldBe 100
      }
    }
  }

  "the title method" should {

    "return the correct title" in {

      whatYouOweCharge.title shouldBe "VAT"
      crystallisedLPICharge.title shouldBe "Interest on central assessment of VAT"
      crystallisedLPP1Model.title shouldBe "Penalty for late payment of VAT"
      crystallisedLPP2Model.title shouldBe "Second penalty for late payment of additional assessment"
      estimatedLPIModel.title shouldBe "Interest on central assessment of VAT"
      estimatedLPP1Model.title shouldBe "Penalty for late payment of VAT"
      estimatedLPP2Model.title shouldBe "Second penalty for late payment of additional assessment"
      lateSubmissionPenaltyModel.title shouldBe "Late submission penalty"
    }
  }
}
