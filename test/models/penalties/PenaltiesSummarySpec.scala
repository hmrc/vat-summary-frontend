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

package models.penalties

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsValue, Json}

class PenaltiesSummarySpec extends AnyWordSpec with Matchers {

  val penaltiesSummaryJson: JsValue = Json.parse(
    """
      |{
      |  "noOfPoints": 3,
      |  "noOfEstimatedPenalties": 2,
      |  "noOfCrystalisedPenalties": 1,
      |  "estimatedPenaltyAmount": 123.45,
      |  "crystalisedPenaltyAmountDue": 54.32,
      |  "hasAnyPenaltyData": true
      |}
      |""".stripMargin
  )

  "Penalties summary JSON should parse to PenaltiesSummary model" when {

    "all fields are present" in {
      val penaltiesDetailsModel = PenaltiesSummary(
        noOfPoints = 3,
        noOfEstimatedPenalties = 2,
        noOfCrystalisedPenalties = 1,
        estimatedPenaltyAmount = 123.45,
        crystalisedPenaltyAmountDue = 54.32,
        hasAnyPenaltyData = true
      )
      penaltiesSummaryJson.as[PenaltiesSummary] shouldBe penaltiesDetailsModel
    }
  }

  val model: PenaltiesSummary = PenaltiesSummary.empty

  ".hasActivePenalties" should {

    "return true" when {

      "the number of penalty points is greater than 0" in {
        model.copy(noOfPoints = 1).hasActivePenalties shouldBe true
        model.copy(noOfPoints = 2).hasActivePenalties shouldBe true
      }

      "the number of Crystalised penalties is greater than 0" in {
        model.copy(noOfCrystalisedPenalties = 1).hasActivePenalties shouldBe true
        model.copy(noOfCrystalisedPenalties = 2).hasActivePenalties shouldBe true
      }

      "the number of estimated penalties is greater than 0" in {
        model.copy(noOfEstimatedPenalties = 1).hasActivePenalties shouldBe true
        model.copy(noOfEstimatedPenalties = 2).hasActivePenalties shouldBe true
      }
    }

    "return false" when {

      "the total number of penalties is 0" in {
        model.hasActivePenalties shouldBe false
      }
    }
  }

  ".hasMultiplePenalties" should {

    "return true" when {

      "the number of penalty points is greater than 1" in {
        model.copy(noOfPoints = 2).hasMultiplePenalties shouldBe true
      }

      "the number of Crystalised penalties is greater than 1" in {
        model.copy(noOfCrystalisedPenalties = 2).hasMultiplePenalties shouldBe true
      }

      "the number of estimated penalties is greater than 1" in {
        model.copy(noOfEstimatedPenalties = 2).hasMultiplePenalties shouldBe true
      }

      "the number of penalty points and Crystalised penalties is greater than 1" in {
        model.copy(noOfPoints = 1, noOfCrystalisedPenalties = 1).hasMultiplePenalties shouldBe true
      }

      "the number of estimated penalties and Crystalised penalties is greater than 1" in {
        model.copy(noOfEstimatedPenalties = 1, noOfCrystalisedPenalties = 1).hasMultiplePenalties shouldBe true
      }

      "the number of penalty points and estimated penalties is greater than 1" in {
        model.copy(noOfPoints = 1, noOfEstimatedPenalties = 1).hasMultiplePenalties shouldBe true
      }
    }

    "return false" when {

      "the sum of number of penalty points, number of estimated penalties and number of Crystalised penalties is 1 or less" in {
        model.hasMultiplePenalties shouldBe false
        model.copy(noOfPoints = 1).hasMultiplePenalties shouldBe false
        model.copy(noOfCrystalisedPenalties = 1).hasMultiplePenalties shouldBe false
        model.copy(noOfEstimatedPenalties = 1).hasMultiplePenalties shouldBe false
      }
    }
  }
}
