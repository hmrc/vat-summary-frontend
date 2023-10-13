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

import common.TestModels.{vatOverpaymentForRPI, vatOverpaymentForRPIJson}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json
import views.ViewBaseSpec

class VatOverpaymentForRPIViewModelSpec extends ViewBaseSpec with AnyWordSpecLike with Matchers {

  "The description function" should {

    "render the description message" when {

      "the user is an agent" in {
        vatOverpaymentForRPI.description(isAgent = true) shouldBe "from 7\u00a0Apr to 10\u00a0Apr\u00a02018"
      }

      "the user is not an agent" in {
        vatOverpaymentForRPI.description(isAgent = false) shouldBe "from 7\u00a0Apr to 10\u00a0Apr\u00a02018"
      }
    }
  }

  "The makePaymentRedirect value" should {

    "be a payment handoff URL generated from the model's parameters" in {
      val amountInPence = (vatOverpaymentForRPI.leftToPay * 100).toLong
      val chargeTypeEncoded = vatOverpaymentForRPI.chargeType.replace(" ", "%20")

      vatOverpaymentForRPI.makePaymentRedirect should include(
        s"/make-payment/$amountInPence/${vatOverpaymentForRPI.periodTo.getMonthValue}/" +
          s"${vatOverpaymentForRPI.periodTo.getYear}/${vatOverpaymentForRPI.periodTo}/$chargeTypeEncoded/" +
          s"${vatOverpaymentForRPI.dueDate}/${vatOverpaymentForRPI.chargeReference.getOrElse("noCR")}"
      )
    }
  }

  "The LateSubmissionPenaltyViewModel" should {

    "read from JSON" when {

      "all fields are populated" in {
        vatOverpaymentForRPIJson.as[VatOverpaymentForRPIViewModel] shouldBe vatOverpaymentForRPI
      }
    }

    "write to JSON" when {

      "all fields are populated" in {
        Json.toJson(vatOverpaymentForRPI) shouldBe vatOverpaymentForRPIJson
      }
    }
  }

}
