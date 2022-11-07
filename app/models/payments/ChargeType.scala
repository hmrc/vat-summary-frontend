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

import models.payments.ChargeType.{interestChargeTypes, penaltyChargeTypes, penaltyInterestChargeTypes}
import play.api.libs.json.Reads._
import play.api.libs.json._
import utils.LoggerUtil

sealed trait ChargeType {
  val value: String

  override def toString: String = value

  def toPathElement: String = {
    value.map(_.toLower).replace(' ', '-')
  }

  def isInterest: Boolean = interestChargeTypes.contains(this)
  def notInterest: Boolean = !isInterest
  def isPenaltyInterest: Boolean = penaltyInterestChargeTypes.contains(this)
  def isPenalty: Boolean = penaltyChargeTypes.contains(this)
}

case object VatUnrepayableOverpayment extends ChargeType {
  override val value: String = "VAT Unrepayable Overpayment"
}
case object VatRepaymentSupplementRecovery extends ChargeType {
  override val value: String = "VAT Repayment Supplement Rec"
}
case object VatIndirectTaxRevenueRecovery extends ChargeType {
  override val value: String = "VAT Indirect Tax Revenue Rec"
}
case object VatDefaultInterest extends ChargeType {
  override val value: String = "VAT Default Interest"
}
case object VatFurtherInterest extends ChargeType {
  override val value: String = "VAT Further Interest"
}
case object ReturnDebitCharge extends ChargeType {
  override val value: String = "VAT Return Debit Charge"
}
case object ReturnCreditCharge extends ChargeType {
  override val value: String = "VAT Return Credit Charge"
}
case object OACreditCharge extends ChargeType {
  override val value: String = "VAT OA Credit Charge"
}
case object OADebitCharge extends ChargeType {
  override val value: String = "VAT OA Debit Charge"
}
case object OADefaultInterestCharge extends ChargeType {
  override val value: String = "VAT OA Default Interest"
}
case object OAFurtherInterestCharge extends ChargeType {
  override val value: String = "VAT OA Further Interest"
}
case object DebitDefaultSurcharge extends ChargeType {
  override val value: String = "VAT Debit Default Surcharge"
}
case object CreditDefaultSurcharge extends ChargeType {
  override val value: String = "VAT Credit Default Surcharge"
}
case object CentralAssessmentCharge extends ChargeType {
  override val value: String = "VAT Central Assessment"
}
case object ErrorCorrectionCreditCharge extends ChargeType {
  override val value: String = "VAT EC Credit Charge"
}
case object ErrorCorrectionDebitCharge extends ChargeType {
  override val value: String = "VAT EC Debit Charge"
}
case object RepaymentSupplement extends ChargeType {
  override val value: String = "VAT Repayment Supplement"
}
case object AAInterestCharge extends ChargeType {
  override val value: String = "VAT AA Default Interest"
}
case object AAFurtherInterestCharge extends ChargeType {
  override val value: String = "VAT AA Further Interest"
}
case object AACharge extends ChargeType {
  override val value: String = "VAT Additional Assessment"
}
case object AAQuarterlyInstalments extends ChargeType {
  override val value: String = "VAT AA Quarterly Instalments"
}
case object AAMonthlyInstalment extends ChargeType {
  override val value: String = "VAT AA Monthly Instalment"
}
case object AAReturnDebitCharge extends ChargeType {
  override val value: String = "VAT AA Return Debit Charge"
}
case object AAReturnCreditCharge extends ChargeType {
  override val value: String = "VAT AA Return Credit Charge"
}
case object BnpRegPre2010Charge extends ChargeType {
  override val value: String = "VAT BNP of Reg Pre 2010"
}
case object BnpRegPost2010Charge extends ChargeType {
  override val value: String = "VAT BNP of Reg Post 2010"
}
case object FtnMatPre2010Charge extends ChargeType {
  override val value: String = "VAT FTN Mat Change Pre 2010"
}
case object FtnMatPost2010Charge extends ChargeType {
  override val value: String = "VAT FTN Mat Change Post 2010"
}
case object FtnEachPartnerCharge extends ChargeType {
  override val value: String = "VAT FTN Each Partner"
}
case object MiscPenaltyCharge extends ChargeType {
  override val value: String = "VAT Miscellaneous Penalty"
}
case object MpPre2009Charge extends ChargeType {
  override val value: String = "VAT MP pre 2009"
}
case object MpRepeatedPre2009Charge extends ChargeType {
  override val value: String = "VAT MP (R) pre 2009"
}
case object CivilEvasionPenaltyCharge extends ChargeType {
  override val value: String = "VAT Civil Evasion Penalty"
}
case object VatOAInaccuraciesFrom2009 extends ChargeType {
  override val value: String = "VAT OA Inaccuracies from 2009"
}
case object InaccuraciesAssessmentsPenCharge extends ChargeType {
  override val value: String = "VAT Inaccuracy Assessments pen"
}
case object InaccuraciesReturnReplacedCharge extends ChargeType {
  override val value: String = "VAT Inaccuracy return replaced"
}
case object WrongDoingPenaltyCharge extends ChargeType {
  override val value: String = "VAT Wrong Doing Penalty"
}
case object CarterPenaltyCharge extends ChargeType {
  override val value: String = "VAT Carter Penalty"
}
case object FailureToNotifyRCSLCharge extends ChargeType {
  override val value: String = "VAT FTN RCSL"
}
case object FailureToSubmitRCSLCharge extends ChargeType {
  override val value: String = "VAT Failure to submit RCSL"
}
case object VatInaccuraciesInECSalesCharge extends ChargeType {
  override val value: String = "VAT Inaccuracies in EC Sales"
}
case object VatECDefaultInterestCharge extends ChargeType {
  override val value: String = "VAT EC Default Interest"
}
case object VatECFurtherInterestCharge extends ChargeType {
  override val value: String = "VAT EC Further Interest"
}
case object VatSecurityDepositRequestCharge extends ChargeType {
  override val value: String = "VAT Security Deposit Request"
}
case object VatProtectiveAssessmentCharge extends ChargeType {
  override val value: String = "VAT Protective Assessment"
}
case object VatPADefaultInterestCharge extends ChargeType {
  override val value: String = "VAT PA Default Interest"
}
case object VatFailureToSubmitECSalesCharge extends ChargeType {
  override val value: String = "VAT Failure to Submit EC Sales"
}
case object StatutoryInterestCharge extends ChargeType {
  override val value: String = "VAT Statutory Interest"
}
case object VatPaFurtherInterestCharge extends ChargeType {
  override val value: String = "VAT PA Further Interest"
}
case object CreditReturnOffsetCharge extends ChargeType {
  override val value: String = "Credit Return Offset"
}
case object PaymentOnAccount extends ChargeType {
  override val value: String = "Payment on account"
}
case object PaymentOnAccountReturnDebitCharge extends ChargeType {
  override val value: String = "VAT POA Return Debit Charge"
}
case object PaymentOnAccountReturnCreditCharge extends ChargeType {
  override val value: String = "VAT POA Return Credit Charge"
}
case object PaymentOnAccountInstalments extends ChargeType {
  override val value: String = "VAT POA Instalments"
}
case object UnallocatedPayment extends ChargeType {
  override val value: String = "Unallocated payment"
}
case object Refund extends ChargeType {
  override val value: String = "Refund"
}
case object VatMigratedLiability extends ChargeType {
  override val value: String = "VAT Migrated Liabilities debit"
}
case object VatMigratedCredit extends ChargeType {
  override val value: String = "VAT Migrated Credit"
}
case object VatReturn1stLPP extends ChargeType {
  override val value: String = "VAT Return 1st LPP"
}
case object VatReturnLPI extends ChargeType {
  override val value: String = "VAT Return LPI"
}
case object VatReturn1stLPPLPI extends ChargeType {
  override val value: String = "VAT Return 1st LPP LPI"
}
case object VatReturn2ndLPPLPI extends ChargeType {
  override val value: String = "VAT Return 2nd LPP LPI"
}
case object VatCentralAssessmentLPI extends ChargeType {
  override val value: String = "VAT Central Assessment LPI"
}
case object VatCA1stLPPLPI extends ChargeType {
  override val value: String = "VAT CA 1st LPP LPI"
}
case object VatCA2ndLPPLPI extends ChargeType {
  override val value: String = "VAT CA 2nd LPP LPI"
}
case object VatOfficersAssessmentLPI extends ChargeType {
  override val value: String = "VAT Officer's Assessment LPI"
}
case object VatOA1stLPPLPI extends ChargeType {
  override val value: String = "VAT OA 1st LPP LPI"
}
case object VatOA2ndLPPLPI extends ChargeType {
  override val value: String = "VAT OA 2nd LPP LPI"
}
case object VatAA1stLPPLPI extends ChargeType {
  override val value: String = "VAT AA 1st LPP LPI"
}
case object VatAA2ndLPPLPI extends ChargeType {
  override val value: String = "VAT AA 2nd LPP LPI"
}
case object VatPALPICharge extends ChargeType {
  override val value: String = "VAT Protective Assessment LPI"
}
case object VatPA1stLPPLPI extends ChargeType {
  override val value: String = "VAT PA 1st LPP LPI"
}
case object VatPA2ndLPPLPI extends ChargeType {
  override val value: String = "VAT PA 2nd LPP LPI"
}
case object VatPA1stLPP extends ChargeType {
  override val value: String = "VAT PA 1st LPP"
}
case object VatPA2ndLPP extends ChargeType {
  override val value: String = "VAT PA 2nd LPP"
}
case object VatAA1stLPP extends ChargeType {
  override val value: String = "VAT AA 1st LPP"
}
case object VatAA2ndLPP extends ChargeType {
  override val value: String = "VAT AA 2nd LPP"
}
case object VatAdditionalAssessmentLPI extends ChargeType {
  override val value: String = "VAT Additional Assessment LPI"
}
case object VatLateSubmissionPen extends ChargeType {
  override val value: String = "VAT Late Submission Pen"
}
case object VatLspInterest extends ChargeType {
  override val value: String = "VAT LSP Interest"
}
case object VatReturnAA1stLPPLPI extends ChargeType {
  override val value: String = "VAT Return AA 1st LPP LPI"
}
case object VatReturnAA2ndLPPLPI extends ChargeType {
  override val value: String = "VAT Return AA 2nd LPP LPI"
}
case object VatManualLPP extends ChargeType {
  override val value: String = "VAT Manual LPP"
}
case object VatManualLPPLPI extends ChargeType {
  override val value: String = "VAT Manual LPP LPI"
}
case object VatAAQuarterlyInstalLPI extends ChargeType {
  override val value: String = "VAT AA Quarterly Instal LPI"
}
case object VatAAMonthlyInstalLPI extends ChargeType {
  override val value: String = "VAT AA Monthly Instal LPI"
}
case object VatAAReturnCharge1stLPP extends ChargeType {
  override val value: String = "VAT AA Return Charge 1st LPP"
}
case object VatAAReturnCharge2ndLPP extends ChargeType {
  override val value: String = "VAT AA Return Charge 2nd LPP"
}

case object VatReturn2ndLPP extends ChargeType {
  override val value: String = "VAT Return 2nd LPP"
}

case object VatErrorCorrectionLPI extends ChargeType {
  override val value: String = "VAT Error Correction LPI"
}

case object VatErrorCorrection1stLPP extends ChargeType {
  override val value: String = "VAT Error Correction 1st LPP"
}

case object VatErrorCorrection2ndLPP extends ChargeType {
  override val value: String = "VAT Error Correction 2nd LPP"
}

case object VatErrorCorrection1stLPPLPI extends ChargeType {
  override val value: String = "VAT Error Correction 1st LPP LPI"
}

case object VatErrorCorrection2ndLPPLPI extends ChargeType {
  override val value: String = "VAT Error Correction 2nd LPP LPI"
}

object ChargeType extends LoggerUtil {

  val positiveOrNegativeChargeTypes: Set[ChargeType] = Set(
    VatDefaultInterest,
    VatFurtherInterest
  )

  val allChargeTypes: Set[ChargeType] = Set(
    VatUnrepayableOverpayment,
    VatRepaymentSupplementRecovery,
    VatIndirectTaxRevenueRecovery,
    ReturnDebitCharge,
    ReturnCreditCharge,
    OACreditCharge,
    OADebitCharge,
    DebitDefaultSurcharge,
    CreditDefaultSurcharge,
    CentralAssessmentCharge,
    ErrorCorrectionCreditCharge,
    ErrorCorrectionDebitCharge,
    RepaymentSupplement,
    OADefaultInterestCharge,
    OAFurtherInterestCharge,
    AACharge,
    AAInterestCharge,
    AAFurtherInterestCharge,
    AAMonthlyInstalment,
    AAQuarterlyInstalments,
    AAReturnDebitCharge,
    AAReturnCreditCharge,
    BnpRegPre2010Charge,
    BnpRegPost2010Charge,
    FtnEachPartnerCharge,
    FtnMatPost2010Charge,
    FtnMatPre2010Charge,
    MiscPenaltyCharge,
    MpPre2009Charge,
    MpRepeatedPre2009Charge,
    CivilEvasionPenaltyCharge,
    VatOAInaccuraciesFrom2009,
    InaccuraciesAssessmentsPenCharge,
    InaccuraciesReturnReplacedCharge,
    WrongDoingPenaltyCharge,
    CarterPenaltyCharge,
    FailureToNotifyRCSLCharge,
    FailureToSubmitRCSLCharge,
    VatInaccuraciesInECSalesCharge,
    StatutoryInterestCharge,
    VatPADefaultInterestCharge,
    VatPALPICharge,
    VatProtectiveAssessmentCharge,
    VatSecurityDepositRequestCharge,
    VatECFurtherInterestCharge,
    VatECDefaultInterestCharge,
    VatFailureToSubmitECSalesCharge,
    VatPaFurtherInterestCharge,
    CreditReturnOffsetCharge,
    PaymentOnAccount,
    PaymentOnAccountReturnDebitCharge,
    PaymentOnAccountReturnCreditCharge,
    PaymentOnAccountInstalments,
    UnallocatedPayment,
    Refund,
    VatMigratedLiability,
    VatMigratedCredit,
    VatReturn1stLPP,
    VatReturnLPI,
    VatReturn1stLPPLPI,
    VatReturn2ndLPPLPI,
    VatCentralAssessmentLPI,
    VatCA1stLPPLPI,
    VatCA2ndLPPLPI,
    VatOfficersAssessmentLPI,
    VatOA1stLPPLPI,
    VatOA2ndLPPLPI,
    VatPA1stLPPLPI,
    VatPA2ndLPPLPI,
    VatPA1stLPP,
    VatPA2ndLPP,
    VatAA1stLPP,
    VatAA2ndLPP,
    VatAdditionalAssessmentLPI,
    VatAA1stLPPLPI,
    VatAA2ndLPPLPI,
    VatLateSubmissionPen,
    VatLspInterest,
    VatReturnAA1stLPPLPI,
    VatReturnAA2ndLPPLPI,
    VatManualLPP,
    VatManualLPPLPI,
    VatAAQuarterlyInstalLPI,
    VatAAMonthlyInstalLPI,
    VatAAReturnCharge1stLPP,
    VatAAReturnCharge2ndLPP,
    VatReturn2ndLPP,
    VatErrorCorrectionLPI,
    VatErrorCorrection1stLPP,
    VatErrorCorrection2ndLPP,
    VatErrorCorrection1stLPPLPI,
    VatErrorCorrection2ndLPPLPI
  ) ++ positiveOrNegativeChargeTypes

  val interestChargeTypes: Set[ChargeType] = Set(
    VatReturnLPI,
    VatReturn1stLPPLPI,
    VatReturn2ndLPPLPI, // TODO add interest mapping when parent charge available
    VatCentralAssessmentLPI,
    VatCA1stLPPLPI, // TODO add interest mapping when parent charge available
    VatCA2ndLPPLPI, // TODO add interest mapping when parent charge available
    VatOfficersAssessmentLPI,
    VatOA1stLPPLPI, // TODO add interest mapping when parent charge available
    VatOA2ndLPPLPI, // TODO add interest mapping when parent charge available
    VatPA1stLPPLPI,
    VatPA2ndLPPLPI,
    VatPALPICharge,
    VatAdditionalAssessmentLPI,
    VatAA1stLPPLPI,
    VatAA2ndLPPLPI,
    VatLspInterest,
    VatReturnAA1stLPPLPI,
    VatReturnAA2ndLPPLPI,
    VatManualLPPLPI,
    VatAAQuarterlyInstalLPI,
    VatAAMonthlyInstalLPI,
    VatErrorCorrectionLPI,
    VatErrorCorrection1stLPPLPI,
    VatErrorCorrection2ndLPPLPI
  )

  val penaltyInterestChargeTypes: Set[ChargeType] = Set(
    VatReturn1stLPPLPI,
    VatReturn2ndLPPLPI,
    VatCA1stLPPLPI,
    VatCA2ndLPPLPI,
    VatOA1stLPPLPI,
    VatOA2ndLPPLPI,
    VatPA1stLPPLPI,
    VatPA2ndLPPLPI,
    VatAA1stLPPLPI,
    VatAA2ndLPPLPI,
    VatLspInterest,
    VatReturnAA1stLPPLPI,
    VatReturnAA2ndLPPLPI,
    VatManualLPPLPI,
    VatErrorCorrection1stLPPLPI,
    VatErrorCorrection2ndLPPLPI
  )

  val LPP1ChargeTypes: Set[ChargeType] = Set(
    VatReturn1stLPP,
    VatPA1stLPP,
    VatAA1stLPP,
    VatAAReturnCharge1stLPP,
    VatErrorCorrection1stLPP
  )

  val LPP2ChargeTypes: Set[ChargeType] = Set(
    VatPA2ndLPP,
    VatAA2ndLPP,
    VatAAReturnCharge2ndLPP,
    VatReturn2ndLPP,
    VatErrorCorrection2ndLPP
  )

  val penaltyChargeTypes: Set[ChargeType] = LPP1ChargeTypes ++ LPP2ChargeTypes

  val interestChargeMapping: Map[ChargeType, ChargeType] = Map(
    ReturnDebitCharge -> VatReturnLPI,
    VatReturn1stLPP -> VatReturn1stLPPLPI,
    CentralAssessmentCharge -> VatCentralAssessmentLPI,
    OADebitCharge -> VatOfficersAssessmentLPI,
    VatPA1stLPP -> VatPA1stLPPLPI,
    VatPA2ndLPP -> VatPA2ndLPPLPI,
    VatProtectiveAssessmentCharge -> VatPALPICharge,
    AACharge -> VatAdditionalAssessmentLPI,
    VatAA1stLPP -> VatAA1stLPPLPI,
    VatAA2ndLPP -> VatAA2ndLPPLPI,
    VatLateSubmissionPen -> VatLspInterest,
    VatAAReturnCharge1stLPP -> VatReturnAA1stLPPLPI,
    VatAAReturnCharge2ndLPP -> VatReturnAA2ndLPPLPI,
    VatManualLPP -> VatManualLPPLPI,
    AAQuarterlyInstalments -> VatAAQuarterlyInstalLPI,
    AAMonthlyInstalment -> VatAAMonthlyInstalLPI,
    VatReturn2ndLPP -> VatReturn2ndLPPLPI,
    VatErrorCorrection1stLPP -> VatErrorCorrection1stLPPLPI,
    VatErrorCorrection2ndLPP -> VatErrorCorrection2ndLPPLPI
  )

  val penaltyChargeMappingLPP1: Map[ChargeType, ChargeType] = Map(
    ReturnDebitCharge -> VatReturn1stLPP,
    VatProtectiveAssessmentCharge -> VatPA1stLPP,
    AACharge -> VatAA1stLPP,
    AAReturnDebitCharge -> VatAAReturnCharge1stLPP,
    VatECDefaultInterestCharge -> VatErrorCorrection1stLPP
  )

  val penaltyChargeMappingLPP2: Map[ChargeType, ChargeType] = Map(
    VatProtectiveAssessmentCharge -> VatPA2ndLPP,
    AACharge -> VatAA2ndLPP,
    AAReturnDebitCharge -> VatAAReturnCharge2ndLPP,
    ReturnDebitCharge -> VatReturn2ndLPP,
    VatECDefaultInterestCharge -> VatErrorCorrection2ndLPP
  )

  def apply: String => ChargeType = input => {
    allChargeTypes.find { chargeType =>
      chargeType.value.toUpperCase.equals(input.trim.toUpperCase)
    }.getOrElse(throw new IllegalArgumentException("Invalid Charge Type"))
  }

  def unapply(arg: ChargeType): String = arg.value

  implicit val reads: Reads[ChargeType] = __.read[String].map(apply)

  implicit val writes: Writes[ChargeType] = Writes { charge => JsString(charge.value) }

}
