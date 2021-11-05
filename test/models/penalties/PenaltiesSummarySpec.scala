/*
 * Copyright 2021 HM Revenue & Customs
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

class PenaltiesSummarySpec extends AnyWordSpec with Matchers{
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

}
