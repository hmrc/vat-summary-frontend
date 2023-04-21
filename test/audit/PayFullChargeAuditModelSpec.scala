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

import _root_.audit.models.PayFullChargeAuditModel
import _root_.models.User
import mocks.MockAppConfig
import org.scalamock.scalatest.MockFactory
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

class PayFullChargeAuditModelSpec extends AnyWordSpecLike with Matchers with MockFactory with GuiceOneAppPerSuite {

  val user = User("999999999")
  lazy implicit val mockConfig: MockAppConfig = new MockAppConfig(app.configuration)
  val auditModel = PayFullChargeAuditModel(user)(mockConfig)

  "PayFullChargeAuditModel" should {

    "have the correct audit type" in {
        auditModel.auditType shouldBe "PaymentsHandOffFullPayment"
    }

    "have the correct transaction name" in {
      auditModel.transactionName shouldBe "pay-full-vat-return-charge"
    }

    "be constructed correctly" in {

      val expected: Map[String, String] = Map(
        "vrn" -> "999999999",
        "taxType" -> "vat",
        "taxReference" -> "999999999",
        "backUrl" -> "payments-back-url",
        "returnUrl" -> "payments-return-url",
      )

      auditModel.detail shouldBe expected
    }


  }
}
