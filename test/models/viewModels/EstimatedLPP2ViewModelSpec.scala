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
import models.payments.AACharge
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json
import views.ViewBaseSpec

import java.time.LocalDate

class EstimatedLPP2ViewModelSpec extends ViewBaseSpec with AnyWordSpecLike with Matchers {

  "The EstimatedLPP2ViewModel" should {

    "read from JSON" in {
      estimatedLPP2Json.as[EstimatedLPP2ViewModel] shouldBe estimatedLPP2Model
    }

    "write to JSON" in {
      Json.toJson(estimatedLPP2Model) shouldBe estimatedLPP2Json
    }
  }

  "The buildEstimatedLPP2ViewModel function" should {

    "return a EstimatedLPP2ViewModel" when {

      "penaltyCategory is LPP2" in {
        val charge = payment.copy(chargeType = AACharge)
        val penalty = LPPDetailsModelMaxWithLPP1HRPercentage.copy(penaltyCategory = "LPP2")
        EstimatedLPP2ViewModel
          .buildEstimatedLPP2ViewModel(charge, Some(penalty), false, false) shouldBe Some(EstimatedLPP2ViewModel(
          "31",
          5.5,
          50.55,
          LocalDate.parse("2019-01-01"),
          LocalDate.parse("2019-02-02"),
          "VAT AA 2nd LPP",
          timeToPay = false,
          breathingSpace = false, directDebitMandateFound = false
        ))
      }
    }
  }
}
