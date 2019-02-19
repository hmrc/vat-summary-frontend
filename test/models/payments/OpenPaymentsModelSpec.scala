/*
 * Copyright 2019 HM Revenue & Customs
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

import common.FinancialTransactionsConstants._
import play.api.libs.json.Json
import views.ViewBaseSpec

class OpenPaymentsModelSpec extends ViewBaseSpec {

  "Apply method" when {

    "given a Payment with a start and end date" should {

      "create an OpenPaymentsModelWithPeriod" in {
        val applyWithPaymentModel = OpenPaymentsModel(
          payment = Payment(
            chargeType = OADebitCharge,
            start = LocalDate.parse("2001-01-01"),
            end = LocalDate.parse("2001-03-31"),
            due = LocalDate.parse("2003-04-05"),
            outstandingAmount = 300.00,
            periodKey = Some("#003")
          ),
          overdue = true
        )

        val regularApply = OpenPaymentsModel(
          chargeType = OADebitCharge,
          amount = 300.00,
          due = LocalDate.parse("2003-04-05"),
          start = LocalDate.parse("2001-01-01"),
          end = LocalDate.parse("2001-03-31"),
          periodKey = "#003",
          overdue = true
        )

        applyWithPaymentModel shouldBe regularApply

      }
    }

    "given a Payment without a start and end date" should {

      "create an OpenPaymentsModelNoPeriod" in {
        val applyWithPaymentModel = OpenPaymentsModel(
          payment = Payment(
            chargeType = OADebitCharge,
            due = LocalDate.parse("2003-04-05"),
            outstandingAmount = 300.00,
            periodKey = Some("#003")
          ),
          overdue = true
        )

        val regularApply = OpenPaymentsModel(
          chargeType = OADebitCharge,
          amount = 300.00,
          due = LocalDate.parse("2003-04-05"),
          periodKey = "#003",
          overdue = true
        )

        applyWithPaymentModel shouldBe regularApply
      }
    }
  }
}
