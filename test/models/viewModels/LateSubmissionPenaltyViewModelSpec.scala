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

import common.TestModels.{lateSubmissionPenaltyJson, lateSubmissionPenaltyModel, payment}
import models.payments.VatLateSubmissionPen
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json
import views.ViewBaseSpec

import java.time.LocalDate

class LateSubmissionPenaltyViewModelSpec extends ViewBaseSpec with AnyWordSpecLike with Matchers {

  "The description function" should {

    "render the description message" when {

      "the user is an agent" in {
        lateSubmissionPenaltyModel.description(isAgent = true) shouldBe "for period 5\u00a0May to 6\u00a0Jun\u00a02020"
      }

      "the user is not an agent" in {
        lateSubmissionPenaltyModel.description(isAgent = false) shouldBe "for period 5\u00a0May to 6\u00a0Jun\u00a02020"
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

  "The buildLateSubmissionPenaltyViewModel function" should {

    "return a LateSubmissionPenaltyViewModel" when {

      "chargeReference is present" in {

        val charge = payment.copy(chargeType = VatLateSubmissionPen, clearedAmount = None)
        LateSubmissionPenaltyViewModel
          .buildLateSubmissionPenaltyViewModel(charge, false, LocalDate.parse("2019-03-02"), "XD002750002155") shouldBe Some(LateSubmissionPenaltyViewModel(
          "VAT Late Submission Pen",
          LocalDate.parse("2019-03-03"),
          10000,
          0,
          10000,
          isOverdue = false,
          "XD002750002155",
          LocalDate.parse("2019-01-01"),
          LocalDate.parse("2019-02-02"), false
        ))
      }
    }
  }
}
