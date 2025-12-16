/*
 * Copyright 2025 HM Revenue & Customs
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

package models.viewModels

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class ExistingDirectDebitFormModelSpec extends AnyWordSpecLike with Matchers {

  "formApply" should {
    "create a model from individual fields" in {
      val model = ExistingDirectDebitFormModel.formApply(
        Some("2025-01-31"),
        "link-id",
        directDebitMandateFound = true,
        ExistingDDContinuePayment.Yes
      )

      model shouldBe ExistingDirectDebitFormModel(
        Some("2025-01-31"),
        "link-id",
        directDebitMandateFound = true,
        ExistingDDContinuePayment.Yes
      )
    }
  }

  "formUnapply" should {
    "break a model into a tuple for form binding" in {
      val model = ExistingDirectDebitFormModel(
        Some("2025-01-31"),
        "link-id",
        directDebitMandateFound = false,
        ExistingDDContinuePayment.No
      )

      ExistingDirectDebitFormModel.formUnapply(model) shouldBe Some(
        (Some("2025-01-31"), "link-id", false, ExistingDDContinuePayment.No)
      )
    }
  }
}
