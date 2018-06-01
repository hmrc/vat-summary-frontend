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

import models.{AuditModel, PaymentHistoryAuditModel}
import _root_.models.User
import uk.gov.hmrc.play.test.UnitSpec

class PaymentHistoryAuditModelSpec extends UnitSpec {

  "PaymentHistoryAuditModel" should {
    "be fully constructed as a map" when {
      "all data items are provided" in {
        val testData: AuditModel = PaymentHistoryAuditModel(
          user            = User("000000000"),
          paymentReceived = LocalDate.of(2018, 1, 1),
          periodFrom      = LocalDate.of(2018, 1, 1),
          periodTo        = LocalDate.of(2018, 1, 3),
          paymentAmount   = 123456789,
          description     = "1 Jan to 3 Jan 2018"
        )

        val expected: Map[String, String] = Map(
          "vrn"             -> "000000000",
          "paymentReceived" -> "2018-01-01",
          "periodFrom"      -> "2018-01-01",
          "periodTo"        -> "2018-01-03",
          "paymentAmount"   -> "123456789",
          "description"     -> "1 Jan to 3 Jan 2018"
        )

        testData.detail shouldBe expected
      }
    }

    "be partially constructed as a map" when {
      "the description contains Officer Assessment and the to and from are defined" in {
        val testData: AuditModel = PaymentHistoryAuditModel(
          user            = User("000000000"),
          paymentReceived = LocalDate.of(2018, 1, 1),
          periodFrom      = LocalDate.of(2018, 1, 1),
          periodTo        = LocalDate.of(2018, 1, 3),
          paymentAmount   = 123456789,
          description     = "Officer Assessment"
        )

        val expected: Map[String, String] = Map(
          "vrn"             -> "000000000",
          "paymentReceived" -> "2018-01-01",
          "paymentAmount"   -> "123456789",
          "description"     -> "Officer Assessment"
        )

        testData.detail shouldBe expected
      }
    }
  }
}
