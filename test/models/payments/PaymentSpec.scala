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

package models.payments

import java.time.LocalDate
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json
import org.scalatest.matchers.should.Matchers

class PaymentSpec extends AnyWordSpecLike with Matchers {

  "paymentReads" when {

    val startDate = "2017-01-01"
    val endDate = "2017-03-01"
    val dueDate = "2017-03-08"

    val model = PaymentWithPeriod(
      chargeType = ReturnDebitCharge,
      periodFrom = LocalDate.parse(startDate),
      periodTo = LocalDate.parse(endDate),
      due = LocalDate.parse(dueDate),
      outstandingAmount = 0,
      periodKey = Some("#001"),
      Some("XD002750002155"),
      ddCollectionInProgress = true,
      accruingInterestAmount = Some(BigDecimal(2)),
      interestRate = Some(2.22),
      accruingPenaltyAmount = Some(555.55),
      penaltyType = Some("LPP1"),
      originalAmount = BigDecimal(10000),
      clearedAmount = Some(100)
    )

    "given JSON with maximum expected fields" should {

      "return a model with those fields populated" in {

        val paymentJson = Json.obj(
          "chargeType" -> ReturnDebitCharge.value,
          "taxPeriodFrom" -> startDate,
          "taxPeriodTo" -> endDate,
          "items" -> Json.arr(
            Json.obj(
              "dueDate" -> dueDate,
              "DDcollectionInProgress" -> true
            )
          ),
          "accruingInterestAmount" -> 2,
          "interestRate" -> 2.22,
          "accruingPenaltyAmount" -> "555.55",
          "penaltyType" -> "LPP1",
          "outstandingAmount" -> 0,
          "periodKey" -> "#001",
          "chargeReference" -> "XD002750002155",
          "originalAmount" -> "10000",
          "clearedAmount" -> "100"
        )

        paymentJson.as[Payment] shouldBe model
      }

    }

    "given no direct debit collection flag" should {

      "read from Json correctly, setting the DD collection flag to false" in {
        val paymentJson = Json.obj(
          "chargeType" -> ReturnDebitCharge.value,
          "taxPeriodFrom" -> startDate,
          "taxPeriodTo" -> endDate,
          "items" -> Json.arr(
            Json.obj(
              "dueDate" -> dueDate
            )
          ),
          "accruingInterestAmount" -> 2,
          "interestRate" -> 2.22,
          "accruingPenaltyAmount" -> "555.55",
          "penaltyType" -> "LPP1",
          "outstandingAmount" -> 0,
          "periodKey" -> "#001",
          "chargeReference" -> "XD002750002155",
          "originalAmount" -> "10000",
          "clearedAmount" -> "100"
        )

        paymentJson.as[Payment] shouldBe model.copy(ddCollectionInProgress = false)
      }
    }

    "not given a start and end date and no direct debit collection flag" should {

      "read from Json correctly, into a PaymentNoPeriod model" in {
        val paymentJson = Json.obj(
          "chargeType" -> ReturnDebitCharge.value,
          "items" -> Json.arr(
            Json.obj(
              "dueDate" -> dueDate,
              "DDcollectionInProgress" -> true
            )
          ),
          "accruingInterestAmount" -> 2,
          "interestRate" -> 2.22,
          "accruingPenaltyAmount" -> "555.55",
          "penaltyType" -> "LPP1",
          "outstandingAmount" -> 0,
          "periodKey" -> "#001",
          "chargeReference" -> "XD002750002155",
          "originalAmount" -> "10000",
          "clearedAmount" -> "100"
        )

        val paymentNoPeriodModel = PaymentNoPeriod(
          chargeType = ReturnDebitCharge,
          due = LocalDate.parse(dueDate),
          outstandingAmount = 0,
          periodKey = Some("#001"),
          Some("XD002750002155"),
          ddCollectionInProgress = true,
          accruingInterestAmount = Some(2),
          interestRate = Some(2.22),
          accruingPenaltyAmount = Some(555.55),
          penaltyType = Some("LPP1"),
          originalAmount = BigDecimal(10000),
          clearedAmount = Some(100)
        )

        paymentJson.as[Payment] shouldBe paymentNoPeriodModel
      }
    }

    "given only a start date or an end date" should {

      "throw an IllegalArgumentException" in {
        val paymentJson = Json.obj(
          "chargeType" -> ReturnDebitCharge.value,
          "taxPeriodFrom" -> startDate,
          "items" -> Json.arr(
            Json.obj(
              "dueDate" -> dueDate
            )
          ),
          "outstandingAmount" -> 9999,
          "periodKey" -> "#001",
          "chargeReference" -> "XD002750002155",
          "originalAmount" -> 10000
        )

        val exception = intercept[IllegalArgumentException](paymentJson.as[Payment])
        exception.getMessage shouldBe s"Partial taxPeriod was supplied: periodFrom: 'Some($startDate)', periodTo: 'None'"
      }
    }

    "given JSON with minimum expected fields" should {

      "read from Json correctly, setting empty fields to None" in {
        val paymentJson = Json.obj(
          "chargeType" -> ReturnDebitCharge.value,
          "items" -> Json.arr(
            Json.obj(
              "dueDate" -> dueDate
            )
          ),
          "outstandingAmount" -> 0,
          "originalAmount" -> 100
        )

        val expectedModel = PaymentNoPeriod(
          chargeType = ReturnDebitCharge,
          due = LocalDate.parse(dueDate),
          outstandingAmount = 0,
          periodKey = None,
          chargeReference = None,
          ddCollectionInProgress = false,
          accruingInterestAmount = None,
          interestRate = None,
          accruingPenaltyAmount = None,
          penaltyType = None,
          originalAmount = 100,
          clearedAmount = None
        )

        paymentJson.as[Payment] shouldBe expectedModel
      }
    }
  }
}
