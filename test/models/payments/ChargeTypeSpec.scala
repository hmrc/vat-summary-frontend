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

import uk.gov.hmrc.play.test.UnitSpec

class ChargeTypeSpec extends UnitSpec {

  "Charge type apply method" when {

    "given a valid charge type" should {
      "return the correct ChargeType model" in {
        ChargeType.apply(ReturnDebitCharge.value) shouldBe ReturnDebitCharge
        ChargeType.apply(ReturnCreditCharge.value) shouldBe ReturnCreditCharge
        ChargeType.apply(OACharge.value) shouldBe OACharge
        ChargeType.apply(OACreditCharge.value) shouldBe OACreditCharge
        ChargeType.apply(OADebitCharge.value) shouldBe OADebitCharge
        ChargeType.apply(DefaultSurcharge.value) shouldBe DefaultSurcharge
        ChargeType.apply(CentralAssessmentCharge.value) shouldBe CentralAssessmentCharge
        ChargeType.apply(ErrorCorrectionCharge.value) shouldBe ErrorCorrectionCharge
        ChargeType.apply(ErrorCorrectionCreditCharge.value) shouldBe ErrorCorrectionCreditCharge
        ChargeType.apply(ErrorCorrectionDebitCharge.value) shouldBe ErrorCorrectionDebitCharge
        ChargeType.apply(RepaymentSupplement.value) shouldBe RepaymentSupplement
        ChargeType.apply(RepaySupplement.value) shouldBe RepaySupplement
        ChargeType.apply(OADefaultInterestCharge.value) shouldBe OADefaultInterestCharge
        ChargeType.apply(AACharge.value) shouldBe AACharge
        ChargeType.apply(AAInterestCharge.value) shouldBe AAInterestCharge
        ChargeType.apply(AAFurtherInterestCharge.value) shouldBe AAFurtherInterestCharge
        ChargeType.apply(BnpRegPre2010Charge.value) shouldBe BnpRegPre2010Charge
        ChargeType.apply(BnpRegPost2010Charge.value) shouldBe BnpRegPost2010Charge
        ChargeType.apply(FtnEachPartnerCharge.value) shouldBe FtnEachPartnerCharge
        ChargeType.apply(FtnMatPost2010Charge.value) shouldBe FtnMatPost2010Charge
        ChargeType.apply(FtnMatPre2010Charge.value) shouldBe FtnMatPre2010Charge
        ChargeType.apply(MiscPenaltyCharge.value) shouldBe MiscPenaltyCharge
        ChargeType.apply(MpPre2009Charge.value) shouldBe MpPre2009Charge
        ChargeType.apply(MpRepeatedPre2009Charge.value) shouldBe MpRepeatedPre2009Charge
        ChargeType.apply(CivilEvasionPenaltyCharge.value) shouldBe CivilEvasionPenaltyCharge
        ChargeType.apply(VatOAInaccuraciesFrom2009.value) shouldBe VatOAInaccuraciesFrom2009
        ChargeType.apply(InaccuraciesAssessmentsPenCharge.value) shouldBe InaccuraciesAssessmentsPenCharge
        ChargeType.apply(InaccuraciesReturnReplacedCharge.value) shouldBe InaccuraciesReturnReplacedCharge
        ChargeType.apply(WrongDoingPenaltyCharge.value) shouldBe WrongDoingPenaltyCharge
        ChargeType.apply(FailureToNotifyRCSLCharge.value) shouldBe FailureToNotifyRCSLCharge
        ChargeType.apply(FailureToSubmitRCSLCharge.value) shouldBe FailureToSubmitRCSLCharge
        ChargeType.apply(CarterPenaltyCharge.value) shouldBe CarterPenaltyCharge
        ChargeType.apply(VatOfficersAssessmentFurtherInterestCharge.value) shouldBe VatOfficersAssessmentFurtherInterestCharge
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
    "return the correct value for each chargeType" in {
      ChargeType.unapply(ReturnDebitCharge) shouldBe ReturnDebitCharge.value
      ChargeType.unapply(ReturnCreditCharge) shouldBe ReturnCreditCharge.value
      ChargeType.unapply(OACharge) shouldBe OACharge.value
      ChargeType.unapply(OACreditCharge) shouldBe OACreditCharge.value
      ChargeType.unapply(OADebitCharge) shouldBe OADebitCharge.value
      ChargeType.unapply(DefaultSurcharge) shouldBe DefaultSurcharge.value
      ChargeType.unapply(CentralAssessmentCharge) shouldBe CentralAssessmentCharge.value
      ChargeType.unapply(ErrorCorrectionCharge) shouldBe ErrorCorrectionCharge.value
      ChargeType.unapply(ErrorCorrectionCreditCharge) shouldBe ErrorCorrectionCreditCharge.value
      ChargeType.unapply(ErrorCorrectionDebitCharge) shouldBe ErrorCorrectionDebitCharge.value
      ChargeType.unapply(RepaymentSupplement) shouldBe RepaymentSupplement.value
      ChargeType.unapply(RepaySupplement) shouldBe RepaySupplement.value
      ChargeType.unapply(OADefaultInterestCharge) shouldBe OADefaultInterestCharge.value
      ChargeType.unapply(AACharge) shouldBe AACharge.value
      ChargeType.unapply(AAInterestCharge) shouldBe AAInterestCharge.value
      ChargeType.unapply(AAFurtherInterestCharge) shouldBe AAFurtherInterestCharge.value
      ChargeType.unapply(BnpRegPre2010Charge) shouldBe BnpRegPre2010Charge.value
      ChargeType.unapply(BnpRegPost2010Charge) shouldBe BnpRegPost2010Charge.value
      ChargeType.unapply(FtnEachPartnerCharge) shouldBe FtnEachPartnerCharge.value
      ChargeType.unapply(FtnMatPost2010Charge) shouldBe FtnMatPost2010Charge.value
      ChargeType.unapply(FtnMatPre2010Charge) shouldBe FtnMatPre2010Charge.value
      ChargeType.unapply(MiscPenaltyCharge) shouldBe MiscPenaltyCharge.value
      ChargeType.unapply(MpPre2009Charge) shouldBe MpPre2009Charge.value
      ChargeType.unapply(MpRepeatedPre2009Charge) shouldBe MpRepeatedPre2009Charge.value
      ChargeType.unapply(CivilEvasionPenaltyCharge) shouldBe CivilEvasionPenaltyCharge.value
      ChargeType.unapply(VatOAInaccuraciesFrom2009) shouldBe VatOAInaccuraciesFrom2009.value
      ChargeType.unapply(InaccuraciesReturnReplacedCharge) shouldBe InaccuraciesReturnReplacedCharge.value
      ChargeType.unapply(InaccuraciesAssessmentsPenCharge) shouldBe InaccuraciesAssessmentsPenCharge.value
      ChargeType.unapply(WrongDoingPenaltyCharge) shouldBe WrongDoingPenaltyCharge.value
      ChargeType.unapply(CarterPenaltyCharge) shouldBe CarterPenaltyCharge.value
      ChargeType.unapply(FailureToSubmitRCSLCharge) shouldBe FailureToSubmitRCSLCharge.value
      ChargeType.unapply(FailureToNotifyRCSLCharge) shouldBe FailureToNotifyRCSLCharge.value
      ChargeType.unapply(VatOfficersAssessmentFurtherInterestCharge) shouldBe VatOfficersAssessmentFurtherInterestCharge.value
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
}
