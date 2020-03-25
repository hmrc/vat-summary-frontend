/*
 * Copyright 2020 HM Revenue & Customs
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

import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec

class PaymentSpec extends UnitSpec {

  "paymentReads" when {

    val startDate = "2017-01-01"
    val endDate = "2017-03-01"
    val dueDate = "2017-03-08"

    "given a start and end date" should {

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
          "periodKey" -> "#001"
        )

        val paymentWithPeriodModel = PaymentWithPeriod(
          chargeType = ReturnDebitCharge,
          periodFrom = LocalDate.parse(startDate),
          periodTo = LocalDate.parse(endDate),
          due = LocalDate.parse(dueDate),
          outstandingAmount = 9999,
          periodKey = "#001"
        )
        paymentJson.as[Payment] shouldBe paymentWithPeriodModel
      }
    }

    "not given a start and end date" should {

      "write to Json correctly" in {
        val paymentJson = Json.obj(
          "chargeType" -> ReturnDebitCharge.value,
          "items" -> Json.arr(
            Json.obj(
              "dueDate" -> dueDate
            )
          ),
          "outstandingAmount" -> -9999,
          "periodKey" -> "#001"
        )

        val paymentNoPeriodModel = PaymentNoPeriod(
          chargeType = ReturnDebitCharge,
          due = LocalDate.parse(dueDate),
          outstandingAmount = -9999,
          periodKey = "#001"
        )
        paymentJson.as[Payment] shouldBe paymentNoPeriodModel
      }
    }

    "not given only a start or an end date" should {

      "write to Json correctly" in {
        val paymentJson = Json.obj(
          "chargeType" -> ReturnDebitCharge.value,
          "taxPeriodFrom" -> startDate,
          "items" -> Json.arr(
            Json.obj(
              "dueDate" -> dueDate
            )
          ),
          "outstandingAmount" -> 9999,
          "periodKey" -> "#001"
        )

        val exception = intercept[Exception] {
          paymentJson.as[Payment]
        }
        exception.getMessage shouldBe s"Partial taxPeriod was supplied: periodFrom: 'Some($startDate)', periodTo: 'None'"
      }
    }

  }

}
