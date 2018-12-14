/*
 * Copyright 2018 HM Revenue & Customs
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
import _root_.models.payments.{Payment, Payments}
import audit.models.ViewNextOutstandingVatPaymentAuditModel
import uk.gov.hmrc.play.test.UnitSpec

class ViewNextOutstandingVatPaymentAuditModelSpec extends UnitSpec {

  val onePayment = Payments(
    Seq(
      Payment(
        "VAT Return Debit Charge",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-03-01"),
        LocalDate.parse("2017-03-08"),
        9999,
        Some("#001")
      )
    )
  )

  val twoPayments = Payments(
    Seq(
      Payment(
        "VAT Return Debit Charge",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-03-01"),
        LocalDate.parse("2017-03-08"),
        9999,
        Some("#001")
      ),
      Payment(
        "VAT Return Debit Charge",
        LocalDate.parse("2017-02-01"),
        LocalDate.parse("2017-04-01"),
        LocalDate.parse("2017-05-08"),
        7777,
        Some("#002")
      )
    )
  )

  val user = User("999999999")

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
