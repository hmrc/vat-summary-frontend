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

import common.TestModels.{lateSubmissionPenaltyJson, lateSubmissionPenaltyModel}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json
import views.ViewBaseSpec

class LateSubmissionPenaltyViewModelSpec extends ViewBaseSpec with AnyWordSpecLike with Matchers {

  "The description function" should {

    "render the description message" when {

      "the user is an agent" in {
        lateSubmissionPenaltyModel.description(isAgent = true) shouldBe "for period 5 May to 6 Jun 2020"
      }

      "the user is not an agent" in {
        lateSubmissionPenaltyModel.description(isAgent = false) shouldBe "for period 5 May to 6 Jun 2020"
      }
    }
  }

  "The makePaymentRedirect value" should {

    "be a payment handoff URL generated from the model's parameters" in {
      val amountInPence = (lateSubmissionPenaltyModel.leftToPay * 100).toLong
      val chargeTypeEncoded = lateSubmissionPenaltyModel.chargeType.replace(" ", "%20")

      lateSubmissionPenaltyModel.makePaymentRedirect should include(
        s"/make-payment/$amountInPence/${lateSubmissionPenaltyModel.periodTo.getMonthValue}/" +
          s"${lateSubmissionPenaltyModel.periodTo.getYear}/${lateSubmissionPenaltyModel.periodTo}/$chargeTypeEncoded/" +
          s"${lateSubmissionPenaltyModel.dueDate}/${lateSubmissionPenaltyModel.chargeReference}"
      )
    }
  }

  "The LateSubmissionPenaltyViewModel" should {

    "read from JSON" when {

      "all fields are populated" in {
        lateSubmissionPenaltyJson.as[LateSubmissionPenaltyViewModel] shouldBe lateSubmissionPenaltyModel
      }
    }

    "write to JSON" when {

      "all fields are populated" in {
        Json.toJson(lateSubmissionPenaltyModel) shouldBe lateSubmissionPenaltyJson
      }
    }
  }
}
