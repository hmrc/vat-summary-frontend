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

package audit

import _root_.models.DirectDebitDetailsModel
import audit.models.DirectDebitAuditModel
import uk.gov.hmrc.play.test.UnitSpec

class DirectDebitAuditModelSpec extends UnitSpec {
        
  val directDebitDetails = DirectDebitDetailsModel(
    userId = "123456789",
    userIdType = "VRN",
    returnUrl = "return-url",
    backUrl = "back-url"
  )

  "DirectDebitAuditModel" should {

    "be constructed correctly when the user has an active direct debit" in {
      val testData = DirectDebitAuditModel(
        directDebitDetails,
        Some(true),
        "url"
      )

      val expected: Map[String, String] = Map(
        "taxType" -> "vat",
        "returnUrl" -> "return-url",
        "backUrl" -> "back-url",
        "vrn" -> "123456789",
        "hasActiveDirectDebit" -> "true"
      )

      testData.detail shouldBe expected
    }

    "be constructed correctly when the user has no active direct debit" in {
      val testData = DirectDebitAuditModel(
        directDebitDetails,
        Some(false),
        "url"
      )

      val expected: Map[String, String] = Map(
        "taxType" -> "vat",
        "returnUrl" -> "return-url",
        "backUrl" -> "back-url",
        "vrn" -> "123456789",
        "hasActiveDirectDebit" -> "false"
      )

      testData.detail shouldBe expected
    }

    "be constructed correctly when the financialTransactions api returns an error" in {
      val testData = DirectDebitAuditModel(
        directDebitDetails,
        None,
        "url"
      )

      val expected: Map[String, String] = Map(
        "taxType" -> "vat",
        "returnUrl" -> "return-url",
        "backUrl" -> "back-url",
        "vrn" -> "123456789",
        "hasActiveDirectDebit" -> "API Error"
      )

      testData.detail shouldBe expected
    }
  }
}
