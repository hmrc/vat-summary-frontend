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

  "isPenalty" should {

    penaltyChargeTypes.foreach { charge =>
      s"return true for $charge" in {
        charge.isPenalty shouldBe true
      }
    }

    allChargeTypes.diff(penaltyChargeTypes).foreach { charge =>
      s"return false for $charge" in {
        charge.isPenalty shouldBe false
      }
    }
  }

  "The interestChargeMapping collection" when {

    "provided with a supported charge type" should {

      "return the corresponding interest charge" in {
        ChargeType.interestChargeMapping(ReturnDebitCharge) shouldBe VatReturnLPI
        ChargeType.interestChargeMapping(VatReturn1stLPP) shouldBe VatReturn1stLPPLPI
        ChargeType.interestChargeMapping(CentralAssessmentCharge) shouldBe VatCentralAssessmentLPI
        ChargeType.interestChargeMapping(OADebitCharge) shouldBe VatOfficersAssessmentLPI
        ChargeType.interestChargeMapping(VatPA1stLPP) shouldBe VatPA1stLPPLPI
        ChargeType.interestChargeMapping(VatPA2ndLPP) shouldBe VatPA2ndLPPLPI
        ChargeType.interestChargeMapping(VatProtectiveAssessmentCharge) shouldBe VatPALPICharge
        ChargeType.interestChargeMapping(AACharge) shouldBe VatAdditionalAssessmentLPI
        ChargeType.interestChargeMapping(VatAA1stLPP) shouldBe VatAA1stLPPLPI
        ChargeType.interestChargeMapping(VatAA2ndLPP) shouldBe VatAA2ndLPPLPI
        ChargeType.interestChargeMapping(VatLateSubmissionPen) shouldBe VatLspInterest
        ChargeType.interestChargeMapping(VatAAReturnCharge1stLPP) shouldBe VatReturnAA1stLPPLPI
        ChargeType.interestChargeMapping(VatAAReturnCharge2ndLPP) shouldBe VatReturnAA2ndLPPLPI
        ChargeType.interestChargeMapping(VatManualLPP) shouldBe VatManualLPPLPI
        ChargeType.interestChargeMapping(AAQuarterlyInstalments) shouldBe VatAAQuarterlyInstalLPI
        ChargeType.interestChargeMapping(AAMonthlyInstalment) shouldBe VatAAMonthlyInstalLPI
        ChargeType.interestChargeMapping(VatReturn2ndLPP) shouldBe VatReturn2ndLPPLPI
        ChargeType.interestChargeMapping(VatErrorCorrection1stLPP) shouldBe VatErrorCorrection1stLPPLPI
        ChargeType.interestChargeMapping(VatErrorCorrection2ndLPP) shouldBe VatErrorCorrection2ndLPPLPI
        ChargeType.interestChargeMapping(AAReturnDebitCharge) shouldBe VatAAReturnChargeLPI
        ChargeType.interestChargeMapping(PaymentOnAccountInstalments) shouldBe VatPOAInstalmentLPI
        ChargeType.interestChargeMapping(PaymentOnAccountReturnDebitCharge) shouldBe VatReturnPOALPI
        ChargeType.interestChargeMapping(VatPOAReturn1stLPP) shouldBe VatReturnPOA1stLPPLPI
        ChargeType.interestChargeMapping(VatPOAReturn2ndLPP) shouldBe VatReturnPOA2ndLPPLPI
        ChargeType.interestChargeMapping(VatCentralAssessment1stLPP) shouldBe VatCA1stLPPLPI
        ChargeType.interestChargeMapping(VatCentralAssessment2ndLPP) shouldBe VatCA2ndLPPLPI
        ChargeType.interestChargeMapping(VatOfficersAssessment1stLPP) shouldBe VatOA1stLPPLPI
        ChargeType.interestChargeMapping(VatOfficersAssessment2ndLPP) shouldBe VatOA2ndLPPLPI
      }
    }

    "provided with an unsupported charge type" should {

      "throw an exception" in {
        intercept[NoSuchElementException](ChargeType.interestChargeMapping(ReturnCreditCharge))
      }
    }
  }
}
