/*
 * Copyright 2026 HM Revenue & Customs
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

package models.payments

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json._

import java.time.LocalDate

class PaymentWithOptionalOutstandingSpec extends AnyWordSpec with Matchers {

  private val fullJson =
    Json.parse(
      """
        |{
        |  "chargeType": "VAT Overpayment for Tax",
        |  "outstandingAmount": 0,
        |  "periodKey": "23AB",
        |  "chargeReference": "XYZ123",
        |  "accruingInterestAmount": 10.50,
        |  "accruingPenaltyAmount": 5.25,
        |  "penaltyType": "LATE",
        |  "originalAmount": 100.00,
        |  "clearedAmount": 100.00,
        |  "items": [
        |    {
        |      "dueDate": "2024-01-31",
        |      "DDcollectionInProgress": true,
        |      "clearingDate": "2024-02-05"
        |    }
        |  ]
        |}
        |""".stripMargin
    )
  private val minimalJson =
    Json.parse(
      """
        |{
        |  "chargeType": "VAT Overpayment for Tax",
        |  "items": [
        |    {
        |      "dueDate": "2024-01-31"
        |    }
        |  ]
        |}
        |""".stripMargin
    )

  private val fullModel = PaymentWithOptionalOutstanding(
    chargeType = VATOverpaymentforTax,
    due = LocalDate.of(2024, 1, 31),
    outstandingAmount = Some(BigDecimal(0)),
    periodKey = Some("23AB"),
    chargeReference = Some("XYZ123"),
    ddCollectionInProgress = true,
    accruingInterestAmount = Some(BigDecimal(10.50)),
    accruingPenaltyAmount = Some(BigDecimal(5.25)),
    penaltyType = Some("LATE"),
    originalAmount = Some(BigDecimal(100.00)),
    clearedAmount = Some(BigDecimal(100.00)),
    clearingDate = Some(LocalDate.of(2024, 2, 5))
  )
  private val minimalModel = PaymentWithOptionalOutstanding(
    chargeType = VATOverpaymentforTax,
    due = LocalDate.of(2024, 1, 31),
    outstandingAmount = None,
    periodKey = None,
    chargeReference = None,
    ddCollectionInProgress = false,
    accruingInterestAmount = None,
    accruingPenaltyAmount = None,
    penaltyType = None,
    originalAmount = None,
    clearedAmount = None,
    clearingDate = None
  )

  "PaymentWithOptionalOutstanding" should {
    "successfully read valid JSON" when {
      "all fields are filled" in {
        fullJson.as[PaymentWithOptionalOutstanding] shouldBe fullModel
      }

      "minimal fields are filled, and DDcollectionInProgress is defaulted to false if missing" in {
        minimalJson.as[PaymentWithOptionalOutstanding] shouldBe minimalModel
      }
    }
  }

  "chargeHasBeenPaidWithNoOutstanding" should {
    "return true" when {
      "outstandingAmount is zero and clearingDate is defined" in {
        val model = minimalModel.copy(
          clearingDate = Some(LocalDate.now()),
          outstandingAmount = Some(0)
        )

        model.chargeHasBeenPaidWithNoOutstanding shouldBe true
      }

      "outstandingAmount is null and clearingDate is defined" in {
        val model = minimalModel.copy(
          clearingDate = Some(LocalDate.now()),
          outstandingAmount = None
        )

        model.chargeHasBeenPaidWithNoOutstanding shouldBe true
      }
    }

    "return false" when {
      "clearingDate is not defined (when outstandingAmount is valid)" in {
        val model = minimalModel.copy(
          clearingDate = None,
          outstandingAmount = Some(0)
        )

        model.chargeHasBeenPaidWithNoOutstanding shouldBe false
      }

      "outstandingAmount is not zero (when clearingDate is defined)" in {
        val model = minimalModel.copy(
          clearingDate = Some(LocalDate.now()),
          outstandingAmount = Some(42)
        )

        model.chargeHasBeenPaidWithNoOutstanding shouldBe false
      }
    }
  }

}
