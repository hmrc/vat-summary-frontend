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
        exception.getMessage shouldBe "Invalid Charge Type: Bad Charge Type"
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
      ChargeType.apply("VAT Further Interest Debit").toPathElement shouldBe "vat-further-interest-debit"
    }
  }

  "notInterest" should {

    LPIChargeTypes.foreach { charge =>
      s"return false for $charge" in {
        charge.notLPICharge shouldBe false
      }
    }

    allChargeTypes.diff(LPIChargeTypes).foreach { charge =>
      s"return true for $charge" in {
        charge.notLPICharge shouldBe true
      }
    }
  }

  "isInterest" should {

    LPIChargeTypes.foreach { charge =>
      s"return true for $charge" in {
        charge.isLPICharge shouldBe true
      }
    }

    allChargeTypes.diff(LPIChargeTypes).foreach { charge =>
      s"return false for $charge" in {
        charge.isLPICharge shouldBe false
      }
    }
  }

  "isPenalty" should {

    penaltyReformPenaltiesChargeTypes.foreach { charge =>
      s"return true for $charge" in {
        charge.isPenalty shouldBe true
      }
    }

    allChargeTypes.diff(penaltyReformPenaltiesChargeTypes).foreach { charge =>
      s"return false for $charge" in {
        charge.isPenalty shouldBe false
      }
    }
  }

  "isNonPenaltyReformPenaltyLPI" should {

    nonPenaltyReformPenaltyLPIChargeTypes.foreach { charge =>
      s"return true for $charge" in {
        charge.isNonPenaltyReformPenaltyLPI shouldBe true
      }
    }

    allChargeTypes.diff(nonPenaltyReformPenaltyLPIChargeTypes).foreach { charge =>
      s"return false for $charge" in {
        charge.isNonPenaltyReformPenaltyLPI shouldBe false
      }
    }
  }

  "The interestChargeMapping collection" when {

    "provided with a supported charge type" should {

      "return the corresponding interest charge" in {
        ChargeType.LPIChargeMapping(ReturnDebitCharge) shouldBe VatReturnLPI
        ChargeType.LPIChargeMapping(VatReturn1stLPP) shouldBe VatReturn1stLPPLPI
        ChargeType.LPIChargeMapping(CentralAssessmentCharge) shouldBe VatCentralAssessmentLPI
        ChargeType.LPIChargeMapping(OADebitCharge) shouldBe VatOfficersAssessmentLPI
        ChargeType.LPIChargeMapping(VatPA1stLPP) shouldBe VatPA1stLPPLPI
        ChargeType.LPIChargeMapping(VatPA2ndLPP) shouldBe VatPA2ndLPPLPI
        ChargeType.LPIChargeMapping(VatProtectiveAssessmentCharge) shouldBe VatPALPICharge
        ChargeType.LPIChargeMapping(CivilEvasionPenaltyCharge) shouldBe VatCivilEvasionPenaltyLPI
        ChargeType.LPIChargeMapping(AACharge) shouldBe VatAdditionalAssessmentLPI
        ChargeType.LPIChargeMapping(VatAA1stLPP) shouldBe VatAA1stLPPLPI
        ChargeType.LPIChargeMapping(VatAA2ndLPP) shouldBe VatAA2ndLPPLPI
        ChargeType.LPIChargeMapping(VatLateSubmissionPen) shouldBe VatLspInterest
        ChargeType.LPIChargeMapping(VatAAReturnCharge1stLPP) shouldBe VatReturnAA1stLPPLPI
        ChargeType.LPIChargeMapping(VatAAReturnCharge2ndLPP) shouldBe VatReturnAA2ndLPPLPI
        ChargeType.LPIChargeMapping(VatManualLPP) shouldBe VatManualLPPLPI
        ChargeType.LPIChargeMapping(AAQuarterlyInstalments) shouldBe VatAAQuarterlyInstalLPI
        ChargeType.LPIChargeMapping(AAMonthlyInstalment) shouldBe VatAAMonthlyInstalLPI
        ChargeType.LPIChargeMapping(VatReturn2ndLPP) shouldBe VatReturn2ndLPPLPI
        ChargeType.LPIChargeMapping(VatErrorCorrection1stLPP) shouldBe VatErrorCorrection1stLPPLPI
        ChargeType.LPIChargeMapping(VatErrorCorrection2ndLPP) shouldBe VatErrorCorrection2ndLPPLPI
        ChargeType.LPIChargeMapping(AAReturnDebitCharge) shouldBe VatAAReturnChargeLPI
        ChargeType.LPIChargeMapping(PaymentOnAccountInstalments) shouldBe VatPOAInstalmentLPI
        ChargeType.LPIChargeMapping(MiscPenaltyCharge) shouldBe VATMiscellaneousPenaltyLPI
        ChargeType.LPIChargeMapping(PaymentOnAccountReturnDebitCharge) shouldBe VatReturnPOALPI
        ChargeType.LPIChargeMapping(VatPOAReturn1stLPP) shouldBe VatReturnPOA1stLPPLPI
        ChargeType.LPIChargeMapping(VatPOAReturn2ndLPP) shouldBe VatReturnPOA2ndLPPLPI
        ChargeType.LPIChargeMapping(VatCentralAssessment1stLPP) shouldBe VatCA1stLPPLPI
        ChargeType.LPIChargeMapping(VatCentralAssessment2ndLPP) shouldBe VatCA2ndLPPLPI
        ChargeType.LPIChargeMapping(WrongDoingPenaltyCharge) shouldBe VatWrongDoingPenaltyLPI
        ChargeType.LPIChargeMapping(CarterPenaltyCharge) shouldBe VatCarterPenaltyLPI
        ChargeType.LPIChargeMapping(FailureToSubmitRCSLCharge) shouldBe VatFailureToSubmitRCSLLPI
        ChargeType.LPIChargeMapping(VatOAInaccuraciesFrom2009) shouldBe VatOAInaccuraciesFrom2009LPI
        ChargeType.LPIChargeMapping(VatOfficersAssessment1stLPP) shouldBe VatOA1stLPPLPI
        ChargeType.LPIChargeMapping(VatOfficersAssessment2ndLPP) shouldBe VatOA2ndLPPLPI
        ChargeType.LPIChargeMapping(VATOverpaymentforTax) shouldBe VATOverpaymentforTaxLPI
        ChargeType.LPIChargeMapping(VatOverpayments1stLPP) shouldBe VatOverpayments1stLPPLPI
        ChargeType.LPIChargeMapping(VatOverpayments2ndLPP) shouldBe VatOverpayments2ndLPPLPI
        ChargeType.LPIChargeMapping(InaccuraciesAssessmentsPenCharge) shouldBe VatInaccuracyAssessPenLPI
        ChargeType.LPIChargeMapping(VatFailureToSubmitECSalesCharge) shouldBe VatFailureToSubmitECSalesChargeLPI
        ChargeType.LPIChargeMapping(FtnEachPartnerCharge) shouldBe FtnEachPartnerChargeLPI
        ChargeType.LPIChargeMapping(FtnMatPost2010Charge) shouldBe FtnMatPost2010ChargeLPI
      }
    }

    "provided with an unsupported charge type" should {

      "throw an exception" in {
        intercept[NoSuchElementException](ChargeType.LPIChargeMapping(ReturnCreditCharge))
      }
    }
  }

  "The penaltyChargeMappingLPP1" when {

    "provided with a supported charge type" should {

      "return the corresponding LPP1 charge" in {
        ChargeType.penaltyChargeMappingLPP1(ReturnDebitCharge) shouldBe VatReturn1stLPP
        ChargeType.penaltyChargeMappingLPP1(VatProtectiveAssessmentCharge) shouldBe VatPA1stLPP
        ChargeType.penaltyChargeMappingLPP1(AACharge) shouldBe VatAA1stLPP
        ChargeType.penaltyChargeMappingLPP1(AAReturnDebitCharge) shouldBe VatAAReturnCharge1stLPP
        ChargeType.penaltyChargeMappingLPP1(ErrorCorrectionDebitCharge) shouldBe VatErrorCorrection1stLPP
        ChargeType.penaltyChargeMappingLPP1(PaymentOnAccountReturnDebitCharge) shouldBe VatPOAReturn1stLPP
        ChargeType.penaltyChargeMappingLPP1(CentralAssessmentCharge) shouldBe VatCentralAssessment1stLPP
        ChargeType.penaltyChargeMappingLPP1(OADebitCharge) shouldBe VatOfficersAssessment1stLPP
        ChargeType.penaltyChargeMappingLPP1(VATOverpaymentforTax) shouldBe VatOverpayments1stLPP
      }
    }

    "provided with an unsupported charge type" should {

      "throw an exception" in {
        intercept[NoSuchElementException](ChargeType.penaltyChargeMappingLPP1(ReturnCreditCharge))
      }
    }
  }

  "The penaltyChargeMappingLPP2" when {

    "provided with a supported charge type" should {

      "return the corresponding LPP2 charge" in {
        ChargeType.penaltyChargeMappingLPP2(VatProtectiveAssessmentCharge) shouldBe VatPA2ndLPP
        ChargeType.penaltyChargeMappingLPP2(AACharge) shouldBe VatAA2ndLPP
        ChargeType.penaltyChargeMappingLPP2(AAReturnDebitCharge) shouldBe VatAAReturnCharge2ndLPP
        ChargeType.penaltyChargeMappingLPP2(ReturnDebitCharge) shouldBe VatReturn2ndLPP
        ChargeType.penaltyChargeMappingLPP2(ErrorCorrectionDebitCharge) shouldBe VatErrorCorrection2ndLPP
        ChargeType.penaltyChargeMappingLPP2(PaymentOnAccountReturnDebitCharge) shouldBe VatPOAReturn2ndLPP
        ChargeType.penaltyChargeMappingLPP2(CentralAssessmentCharge) shouldBe VatCentralAssessment2ndLPP
        ChargeType.penaltyChargeMappingLPP2(OADebitCharge) shouldBe VatOfficersAssessment2ndLPP
        ChargeType.penaltyChargeMappingLPP2(VATOverpaymentforTax) shouldBe VatOverpayments2ndLPP
      }
    }

    "provided with an unsupported charge type" should {

      "throw an exception" in {
        intercept[NoSuchElementException](ChargeType.penaltyChargeMappingLPP1(ReturnCreditCharge))
      }
    }
  }
}
