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

import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers
import ChargeType._
import common.TestModels

class ChargeTypeSpec extends AnyWordSpecLike with Matchers {

  "Charge type apply method" when {

    "given a valid charge type" should {
      ChargeType.allChargeTypes.foreach { chargeType =>
        s"return the correct chargeType model for ${chargeType.value}" in {
          ChargeType.apply(chargeType.value) shouldBe chargeType
        }
      }
    }

    "given a valid charge type in all lowercase" should {
      "return the correct ChargeType model" in {
        ChargeType.apply("vat carter penalty") shouldBe CarterPenaltyCharge
      }
    }

    s"given an invalid charge type" should {
      "throw IllegalArgumentException(Invalid Charge Type)" in {
        val exception = intercept[IllegalArgumentException] {
          ChargeType.apply("Bad Charge Type")
        }
        exception.getMessage shouldBe "Invalid Charge Type"
      }
    }
  }

  "ChargeType unapply method" should {
    ChargeType.allChargeTypes.foreach { chargeType =>
      s"return the correct value for ${chargeType.value}" in {
        ChargeType.unapply(chargeType) shouldBe chargeType.value
      }
    }
  }

  "ChargeType .isValidChargeType" when {

    "given an invalid charge type" should {

      "return false" in {
        ChargeType.isValidChargeType("VAAAAAAAT") shouldBe false
      }
    }

    "given a valid charge type" should {

      "return true" in {
        ChargeType.isValidChargeType("VAT FTN RCSL") shouldBe true
      }
    }
  }

  "The charge type .toPathElement method" should {

    "correctly hyphenate and lowercase a string" in {
      ChargeType.apply("VAT Further Interest").toPathElement shouldBe "vat-further-interest"
    }

  }

  "notInterest" should {

    interestChargeTypes.foreach { charge =>
      s"return false for $charge" in {
        charge.notInterest shouldBe false
      }
    }

    allChargeTypes.diff(interestChargeTypes).foreach { charge =>
      s"return true for $charge" in {
        charge.notInterest shouldBe true
      }
    }

  }

  "isInterest" should {

    interestChargeTypes.foreach { charge =>
      s"return true for $charge" in {
        charge.isInterest shouldBe true
      }
    }

  }

}
