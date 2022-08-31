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

    "given a start and end date and no direct debit collection flag" should {

      "write to Json correctly" in {
        val paymentJson = Json.obj(
          "chargeType" -> ReturnDebitCharge.value,
          "taxPeriodFrom" -> startDate,
          "taxPeriodTo" -> endDate,
          "items" -> Json.arr(
            Json.obj(
              "dueDate" -> dueDate
            )
          ),
          "outstandingAmount" -> 9999,
          "periodKey" -> "#001",
          "chargeReference" -> "XD002750002155",
          "accruedInterestAmount" -> 2
        )

        val paymentWithPeriodModel = PaymentWithPeriod(
          chargeType = ReturnDebitCharge,
          periodFrom = LocalDate.parse(startDate),
          periodTo = LocalDate.parse(endDate),
          due = LocalDate.parse(dueDate),
          outstandingAmount = 9999,
          periodKey = Some("#001"),
          Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2))
        )
        paymentJson.as[Payment] shouldBe paymentWithPeriodModel
      }
    }

    "not given a start and end date and no direct debit collection flag" should {

      "read from Json correctly" in {
        val paymentJson = Json.obj(
          "chargeType" -> ReturnDebitCharge.value,
          "items" -> Json.arr(
            Json.obj(
              "dueDate" -> dueDate
            )
          ),
          "outstandingAmount" -> -9999,
          "periodKey" -> "#001",
          "chargeReference" -> "XD002750002155",
          "accruedInterestAmount" -> 2
        )

        val paymentNoPeriodModel = PaymentNoPeriod(
          chargeType = ReturnDebitCharge,
          due = LocalDate.parse(dueDate),
          outstandingAmount = -9999,
          periodKey = Some("#001"),
          Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2))
        )
        paymentJson.as[Payment] shouldBe paymentNoPeriodModel
      }
    }

    "not given only a start or an end date" should {

      "read from Json correctly" in {
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
          "chargeReference" -> "XD002750002155"
        )

        val exception = intercept[Exception] {
          paymentJson.as[Payment]
        }
        exception.getMessage shouldBe s"Partial taxPeriod was supplied: periodFrom: 'Some($startDate)', periodTo: 'None'"
      }
    }

    "given a direct debit collection flag" should {

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
        "accruedInterestAmount" -> 2,
        "outstandingAmount" -> 0,
        "periodKey" -> "#001",
        "chargeReference" -> "XD002750002155"
      )

      val model = PaymentWithPeriod(
        chargeType = ReturnDebitCharge,
        periodFrom = LocalDate.parse(startDate),
        periodTo = LocalDate.parse(endDate),
        due = LocalDate.parse(dueDate),
        outstandingAmount = 0,
        periodKey = Some("#001"),
        Some("XD002750002155"),
        ddCollectionInProgress = true,
        accruedInterestAmount = Some(BigDecimal(2))
      )

      "read from json correctly" in {
        paymentJson.as[Payment] shouldBe model
      }
    }

    "given JSON with no original amount or cleared amount fields" should {

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
        "outstandingAmount" -> 0,
        "periodKey" -> "#001",
        "chargeReference" -> "XD002750002155"
      )

      val result = paymentJson.as[Payment]

      "return a model" that {

        "has None in the originalAmount field" in {
          result.originalAmount shouldBe None
        }

        "has None in the clearedAmount field" in {
          result.clearedAmount shouldBe None
        }

      }

    }

    "given JSON with period data and original amount and cleared amount fields" should {

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
          "accruedInterestAmount" -> 2,
          "outstandingAmount" -> 0,
          "periodKey" -> "#001",
          "chargeReference" -> "XD002750002155",
          "originalAmount" -> "10000",
          "clearedAmount" -> "100"
        )

        val model = PaymentWithPeriod(
          chargeType = ReturnDebitCharge,
          periodFrom = LocalDate.parse(startDate),
          periodTo = LocalDate.parse(endDate),
          due = LocalDate.parse(dueDate),
          outstandingAmount = 0,
          periodKey = Some("#001"),
          Some("XD002750002155"),
          ddCollectionInProgress = true,
          accruedInterestAmount = Some(BigDecimal(2)),
          originalAmount = Some(10000),
          clearedAmount = Some(100)
        )

        paymentJson.as[Payment] shouldBe model
      }

    }

    "given JSON without period data and with original amount and cleared amount fields" should {

      "return a paymentNoPeriod model with the correct original amount and cleared amount fields" in {

        val paymentJson = Json.obj(
          "chargeType" -> ReturnDebitCharge.value,
          "items" -> Json.arr(
            Json.obj(
              "dueDate" -> dueDate,
              "DDcollectionInProgress" -> true
            )
          ),
          "outstandingAmount" -> 0,
          "periodKey" -> "#001",
          "chargeReference" -> "XD002750002155",
          "accruedInterestAmount" -> 2,
          "originalAmount" -> "10000",
          "clearedAmount" -> "100"
        )

        val model = PaymentNoPeriod(
          chargeType = ReturnDebitCharge,
          due = LocalDate.parse(dueDate),
          outstandingAmount = 0,
          periodKey = Some("#001"),
          Some("XD002750002155"),
          ddCollectionInProgress = true,
          accruedInterestAmount = Some(BigDecimal(2)),
          originalAmount = Some(10000),
          clearedAmount = Some(100)
        )

        paymentJson.as[Payment] shouldBe model
      }
    }
  }
}
