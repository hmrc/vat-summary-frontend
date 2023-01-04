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

package audit

import java.time.LocalDate
import _root_.models.User
import _root_.models.payments.{Payment, Payments, ReturnDebitCharge}
import audit.models.ViewNextOutstandingVatPaymentAuditModel
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

class ViewNextOutstandingVatPaymentAuditModelSpec extends AnyWordSpecLike with Matchers {

  val paymentModel: Payment = Payment(
    ReturnDebitCharge,
    Some(LocalDate.parse("2017-01-01")),
    Some(LocalDate.parse("2017-03-01")),
    LocalDate.parse("2017-03-08"),
    9999,
    Some("#001"),
    chargeReference = Some("XD002750002155"),
    ddCollectionInProgress = false,
    accruingInterestAmount = Some(BigDecimal(2)),
    interestRate = Some(2.22),
    accruingPenaltyAmount = None,
    penaltyType = None,
    originalAmount = BigDecimal(10000),
    clearedAmount = None
  )

  val onePayment: Payments = Payments(Seq(paymentModel))
  val twoPayments: Payments = Payments(Seq(paymentModel, paymentModel))
  val user: User = User("999999999")

  "ViewNextOutstandingVatPaymentAuditModel" should {

    "be constructed correctly when there is one outstanding payment" in {
      val testData = ViewNextOutstandingVatPaymentAuditModel(
        user,
        payments = Some(onePayment))

      val expected: Map[String, String] = Map(
        "vrn" -> "999999999",
        "paymentOutstanding" -> "yes",
        "paymentPeriodFrom" -> "2017-01-01",
        "paymentPeriodTo" -> "2017-03-01",
        "paymentDueBy" -> "2017-03-08"
      )

      testData.detail shouldBe expected
    }

    "be constructed correctly when there are multiple outstanding payments" in {

      val testData = ViewNextOutstandingVatPaymentAuditModel(
        user,
        Some(twoPayments)
      )

      val expected: Map[String, String] = Map(
        "vrn" -> "999999999",
        "numberOfPayments" -> "2"
      )

      testData.detail shouldBe expected
    }

    "be constructed correctly when there are no outstanding payments" in {
      val testData = ViewNextOutstandingVatPaymentAuditModel(
        user,
        None
      )

      val expected: Map[String, String] = Map(
        "vrn" -> "999999999",
        "paymentOutstanding" -> "no"
      )

      testData.detail shouldBe expected
    }
  }
}
