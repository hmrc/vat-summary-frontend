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

import models.payments.ChargeType.{LPIChargeTypes, nonPenaltyReformPenaltyLPIChargeTypes, penaltyReformPenaltiesChargeTypes, penaltyReformPenaltyLPIChargeTypes}
import play.api.libs.json.Reads._
import play.api.libs.json._
import utils.LoggerUtil

sealed trait ChargeType {
  val value: String

  override def toString: String = value

  def toPathElement: String = {
    value.map(_.toLower).replace(' ', '-')
  }

  def isLPICharge: Boolean = LPIChargeTypes.contains(this)
  def notLPICharge: Boolean = !isLPICharge
  def isPenaltyInterest: Boolean = penaltyReformPenaltyLPIChargeTypes.contains(this)

  def isNonPenaltyReformPenaltyLPI: Boolean = nonPenaltyReformPenaltyLPIChargeTypes.contains(this)
  def isPenalty: Boolean = penaltyReformPenaltiesChargeTypes.contains(this)
}

case object VatOverpayments2ndLPP extends ChargeType {
  override val value: String = "VAT Overpayments 2nd LPP"
}

case object VATOverpaymentforTax extends ChargeType {
  override val value: String = "VAT Overpayment for Tax"
}

case object VatOverpaymentForTaxRPI extends ChargeType {
  override val value: String = "VAT Overpayment for Tax RPI"
}
case object VatOverpayments1stLPPRPI extends ChargeType {
  override val value: String = "VAT Overpayments 1st LPP RPI"
}
case object VatOverpayments2ndLPPRPI extends ChargeType {
  override val value: String = "VAT Overpayments 2nd LPP RPI"
}
case object VATOverpaymentforTaxLPI extends ChargeType {
  override val value: String = "VAT Overpayment for Tax LPI"
}
case object VatOverpayments2ndLPPLPI extends ChargeType {
  override val value: String = "VAT Overpayments 2nd LPP LPI"
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
case object VatDefaultInterestDebit extends ChargeType {
  override val value: String = "VAT Default Interest Debit"
}
case object VatDefaultInterestCredit extends ChargeType {
  override val value: String = "VAT Default Interest Credit"
}
case object VatFurtherInterestDebit extends ChargeType {
  override val value: String = "VAT Further Interest Debit"
}
case object VatFurtherInterestCredit extends ChargeType {
  override val value: String = "VAT Further Interest Credit"
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
case object FtnMatPost2010ChargeLPI extends ChargeType {
  override val value: String = "VAT FTN Mat Chg Post 2010 LPI"
}
case object FtnEachPartnerCharge extends ChargeType {
  override val value: String = "VAT FTN Each Partner"
}
case object FtnEachPartnerChargeLPI extends ChargeType {
  override val value: String = "VAT FTN Each Partner LPI"
}
case object MiscPenaltyCharge extends ChargeType {
  override val value: String = "VAT Miscellaneous Penalty"
}
case object VATMiscellaneousPenaltyLPI  extends ChargeType {
  override val value: String = "VAT Miscellaneous Penalty LPI"
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
case object VatCivilEvasionPenaltyLPI extends ChargeType {
  override val value: String = "VAT Civil Evasion Penalty LPI"
}
case object VatOAInaccuraciesFrom2009 extends ChargeType {
  override val value: String = "VAT OA Inaccuracies from 2009"
}
case object VatOAInaccuraciesFrom2009LPI extends ChargeType {
  override val value: String = "VAT OA Inaccur from 2009 LPI"
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

case object VatWrongDoingPenaltyLPI extends ChargeType {
  override val value: String = "VAT Wrong Doing Penalty LPI"
}

case object CarterPenaltyCharge extends ChargeType {
  override val value: String = "VAT Carter Penalty"
}

case object VatCarterPenaltyLPI extends ChargeType {
  override val value: String = "VAT Carter Penalty LPI"
}

case object FailureToNotifyRCSLCharge extends ChargeType {
  override val value: String = "VAT FTN RCSL"
}
case object FailureToSubmitRCSLCharge extends ChargeType {
  override val value: String = "VAT Failure to submit RCSL"
}

case object VatFailureToSubmitRCSLLPI extends ChargeType {
  override val value: String = "VAT Failure to Submit RCSL LPI"
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
case object VatFailureToSubmitECSalesChargeLPI extends ChargeType {
  override val value: String = "VAT Fail to Sub EC Sales LPI"
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
case object VatReturnPOALPI extends ChargeType {
  override val value: String = "VAT Return POA LPI"
}
case object VatPOAReturn1stLPP extends ChargeType {
  override val value: String = "VAT POA Return 1st LPP"
}
case object VatPOAReturn2ndLPP extends ChargeType {
  override val value: String = "VAT POA Return 2nd LPP"
}
case object VatReturnPOA1stLPPLPI extends ChargeType {
  override val value: String = "VAT Return POA 1st LPP LPI"
}
case object VatReturnPOA2ndLPPLPI extends ChargeType {
  override val value: String = "VAT Return POA 2nd LPP LPI"
}
// Parent charges: Main transaction 4701, Sub transaction 1174 and Main transaction 4701, Sub transaction 1177
case object VatReturnPOARPI extends ChargeType {
  override val value: String = "VAT Return POA RPI"
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
case object VatDeferralPenalty extends ChargeType {
  override val value: String = "VAT Deferral Penalty"
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
case object VatCentralAssessment1stLPP extends ChargeType {
  override val value: String = "VAT Central Assessment 1st LPP"
}
case object VatCentralAssessment2ndLPP extends ChargeType {
  override val value: String = "VAT Central Assessment 2nd LPP"
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

// Parent charges: Main transaction 4730, Sub transaction 1177 and Main transaction 4730, Sub transaction 1174
case object VatOfficersAssessmentRPI extends ChargeType {
  override val value: String = "VAT Officer’s Assessment RPI"
}
case object VatOfficersAssessment1stLPP extends ChargeType {
  override val value: String = "VAT OA 1st LPP"
}
case object VatOfficersAssessment2ndLPP extends ChargeType {
  override val value: String = "VAT OA 2nd LPP"
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
case object VatAAReturnChargeLPI extends ChargeType {
  override val value: String = "VAT Return AA LPI"
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
  override val value: String = "VAT Error Correct 1st LPP LPI"
}
case object VatErrorCorrection2ndLPPLPI extends ChargeType {
  override val value: String = "VAT Error Correct 2nd LPP LPI"
}
case object VatOverpayments1stLPP extends ChargeType {
  override val value: String = "VAT Overpayments 1st LPP"
}
case object VatPOAInstalmentLPI extends ChargeType {
  override val value: String = "VAT POA Instalment LPI"
}

case object VatOverpayments1stLPPLPI extends ChargeType {
  override val value: String = "VAT Overpayments 1st LPP LPI"
}

case object VatOverpaymentForRPI extends ChargeType {
  override val value: String = "VAT Overpayment for RPI"
}

// Parent charges: Main transaction 4702, Sub transaction 1174 and Main transaction 4702, Sub transaction 1177
case object VatAAReturnChargeRPI extends ChargeType {
  override val value: String = "VAT Return AA RPI"
}

// Parent charge: Main transaction 4700, Sub transaction 1177
case object VatReturnRPI extends ChargeType {
  override val value: String = "VAT Return RPI"
}

// Parent charge: Main transaction 4787, Sub transaction 1090
case object VatManualLPPRPI extends ChargeType {
  override val value: String = "VAT Manual LPP RPI"
}

// Parent charge: Main transaction 4703, Sub transaction 1090
case object VatReturnLPP1RPI extends ChargeType {
  override val value: String = "VAT Return 1st LPP RPI"
}

case object VatInaccuracyAssessPenLPI extends ChargeType {
  override val value: String = "VAT Inaccuracy Assessments Pen LPI"
}

// Parent charges: Main transaction 4731, Sub transaction 1174 and Main transaction 4731, Sub transaction 1177
case object VatErrorCorrectionRPI extends ChargeType {
  override val value: String = "VAT Error Correction RPI"
}

object ChargeType extends LoggerUtil {

  val allChargeTypes: Set[ChargeType] = Set(
    VatDefaultInterestDebit,
    VatDefaultInterestCredit,
    VatFurtherInterestDebit,
    VatFurtherInterestCredit,
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
    FtnEachPartnerChargeLPI,
    FtnMatPost2010Charge,
    FtnMatPost2010ChargeLPI,
    FtnMatPre2010Charge,
    MiscPenaltyCharge,
    VATMiscellaneousPenaltyLPI,
    MpPre2009Charge,
    MpRepeatedPre2009Charge,
    CivilEvasionPenaltyCharge,
    VatCivilEvasionPenaltyLPI,
    VatOAInaccuraciesFrom2009,
    VatOAInaccuraciesFrom2009LPI,
    InaccuraciesAssessmentsPenCharge,
    InaccuraciesReturnReplacedCharge,
    WrongDoingPenaltyCharge,
    VatWrongDoingPenaltyLPI,
    CarterPenaltyCharge,
    VatCarterPenaltyLPI,
    FailureToNotifyRCSLCharge,
    FailureToSubmitRCSLCharge,
    VatFailureToSubmitRCSLLPI,
    VatInaccuraciesInECSalesCharge,
    StatutoryInterestCharge,
    VatPADefaultInterestCharge,
    VatPALPICharge,
    VatProtectiveAssessmentCharge,
    VatSecurityDepositRequestCharge,
    VatECFurtherInterestCharge,
    VatECDefaultInterestCharge,
    VatFailureToSubmitECSalesCharge,
    VatFailureToSubmitECSalesChargeLPI,
    VatPaFurtherInterestCharge,
    CreditReturnOffsetCharge,
    PaymentOnAccount,
    PaymentOnAccountReturnDebitCharge,
    PaymentOnAccountReturnCreditCharge,
    PaymentOnAccountInstalments,
    VatReturnPOALPI,
    VatPOAReturn1stLPP,
    VatPOAReturn2ndLPP,
    VatReturnPOA1stLPPLPI,
    VatReturnPOA2ndLPPLPI,
    VatReturnPOARPI,
    UnallocatedPayment,
    Refund,
    VatMigratedLiability,
    VatMigratedCredit,
    VatDeferralPenalty,
    VatReturn1stLPP,
    VatReturnLPI,
    VatReturn1stLPPLPI,
    VatReturn2ndLPPLPI,
    VatCentralAssessment1stLPP,
    VatCentralAssessment2ndLPP,
    VatCentralAssessmentLPI,
    VatCA1stLPPLPI,
    VatCA2ndLPPLPI,
    VatOfficersAssessmentLPI,
    VatOfficersAssessment1stLPP,
    VatOfficersAssessment2ndLPP,
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
    VatAAReturnChargeLPI,
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
    VatErrorCorrection2ndLPPLPI,
    VatPOAInstalmentLPI,
    VATOverpaymentforTax,
    VatOverpayments1stLPP,
    VatOverpayments2ndLPP,
    VatOverpaymentForRPI,
    VatOverpaymentForTaxRPI,
    VatOverpayments1stLPPRPI,
    VatOverpayments2ndLPPRPI,
    VATOverpaymentforTaxLPI,
    VatOverpayments1stLPPLPI,
    VatOverpayments2ndLPPLPI,
    VatAAReturnChargeRPI,
    VatReturnRPI,
    VatInaccuracyAssessPenLPI,
    VatErrorCorrectionRPI,
    VatOfficersAssessmentRPI,
    VatManualLPPRPI,
    VatReturnLPP1RPI

  )

  val LPIChargeTypes: Set[ChargeType] = Set(
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
    VatPALPICharge,
    VatCivilEvasionPenaltyLPI,
    VatAdditionalAssessmentLPI,
    VATMiscellaneousPenaltyLPI,
    VatAA1stLPPLPI,
    VatAA2ndLPPLPI,
    VatAAReturnChargeLPI,
    VatLspInterest,
    VatReturnAA1stLPPLPI,
    VatReturnAA2ndLPPLPI,
    VatManualLPPLPI,
    VatOAInaccuraciesFrom2009LPI,
    VatWrongDoingPenaltyLPI,
    VatCarterPenaltyLPI,
    VatFailureToSubmitRCSLLPI,
    VatAAQuarterlyInstalLPI,
    VatAAMonthlyInstalLPI,
    VatErrorCorrectionLPI,
    VatErrorCorrection1stLPPLPI,
    VatErrorCorrection2ndLPPLPI,
    VatPOAInstalmentLPI,
    VatReturnPOALPI,
    VatReturnPOA1stLPPLPI,
    VatReturnPOA2ndLPPLPI,
    VatOverpayments1stLPPLPI,
    VATOverpaymentforTaxLPI,
    VatOverpayments2ndLPPLPI,
    VatInaccuracyAssessPenLPI,
    VatFailureToSubmitECSalesChargeLPI,
    FtnEachPartnerChargeLPI,
    FtnMatPost2010ChargeLPI
  )

  val penaltyReformPenaltyLPIChargeTypes: Set[ChargeType] = Set(
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
    VatErrorCorrection2ndLPPLPI,
    VatReturnPOA1stLPPLPI,
    VatReturnPOA2ndLPPLPI,
    VatOverpayments1stLPPLPI,
    VatOverpayments2ndLPPLPI
  )

  val nonPenaltyReformPenaltyLPIChargeTypes: Set[ChargeType] = Set(
    VatInaccuracyAssessPenLPI,
    VatCivilEvasionPenaltyLPI,
    VatOAInaccuraciesFrom2009LPI,
    VATMiscellaneousPenaltyLPI,
    VatWrongDoingPenaltyLPI,
    VatFailureToSubmitECSalesChargeLPI,
    VatCarterPenaltyLPI,
    VatFailureToSubmitRCSLLPI,
    VatFailureToSubmitECSalesChargeLPI,
    FtnEachPartnerChargeLPI,
    FtnMatPost2010ChargeLPI
  )

  val LPP1ChargeTypes: Set[ChargeType] = Set(
    VatReturn1stLPP,
    VatPA1stLPP,
    VatAA1stLPP,
    VatAAReturnCharge1stLPP,
    VatErrorCorrection1stLPP,
    VatPOAReturn1stLPP,
    VatCentralAssessment1stLPP,
    VatOfficersAssessment1stLPP,
    VatOverpayments1stLPP
  )

  val LPP2ChargeTypes: Set[ChargeType] = Set(
    VatPA2ndLPP,
    VatAA2ndLPP,
    VatAAReturnCharge2ndLPP,
    VatReturn2ndLPP,
    VatErrorCorrection2ndLPP,
    VatPOAReturn2ndLPP,
    VatCentralAssessment2ndLPP,
    VatOfficersAssessment2ndLPP,
    VatOverpayments2ndLPP
  )

  val penaltyReformPenaltiesChargeTypes: Set[ChargeType] = LPP1ChargeTypes ++ LPP2ChargeTypes

  val LPIChargeMapping: Map[ChargeType, ChargeType] = Map(
    ReturnDebitCharge -> VatReturnLPI,
    VatReturn1stLPP -> VatReturn1stLPPLPI,
    CentralAssessmentCharge -> VatCentralAssessmentLPI,
    OADebitCharge -> VatOfficersAssessmentLPI,
    VatPA1stLPP -> VatPA1stLPPLPI,
    VatPA2ndLPP -> VatPA2ndLPPLPI,
    VatProtectiveAssessmentCharge -> VatPALPICharge,
    CivilEvasionPenaltyCharge -> VatCivilEvasionPenaltyLPI,
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
    VatErrorCorrection2ndLPP -> VatErrorCorrection2ndLPPLPI,
    PaymentOnAccountReturnDebitCharge -> VatReturnPOALPI,
    WrongDoingPenaltyCharge -> VatWrongDoingPenaltyLPI,
    CarterPenaltyCharge -> VatCarterPenaltyLPI,
    FailureToSubmitRCSLCharge -> VatFailureToSubmitRCSLLPI,
    VatOAInaccuraciesFrom2009-> VatOAInaccuraciesFrom2009LPI,
    VatPOAReturn1stLPP -> VatReturnPOA1stLPPLPI,
    VatPOAReturn2ndLPP -> VatReturnPOA2ndLPPLPI,
    AAReturnDebitCharge -> VatAAReturnChargeLPI,
    PaymentOnAccountInstalments -> VatPOAInstalmentLPI,
    VatCentralAssessment1stLPP -> VatCA1stLPPLPI,
    VatCentralAssessment2ndLPP -> VatCA2ndLPPLPI,
    MiscPenaltyCharge -> VATMiscellaneousPenaltyLPI,
    VatOfficersAssessment1stLPP -> VatOA1stLPPLPI,
    VatOfficersAssessment2ndLPP -> VatOA2ndLPPLPI,
    VatOverpayments2ndLPP -> VatOverpayments2ndLPPLPI,
    VatOverpayments1stLPP -> VatOverpayments1stLPPLPI,
    VATOverpaymentforTax -> VATOverpaymentforTaxLPI,
    InaccuraciesAssessmentsPenCharge -> VatInaccuracyAssessPenLPI,
    VatFailureToSubmitECSalesCharge -> VatFailureToSubmitECSalesChargeLPI,
    FtnEachPartnerCharge -> FtnEachPartnerChargeLPI,
    FtnMatPost2010Charge -> FtnMatPost2010ChargeLPI
  )

  val penaltyChargeMappingLPP1: Map[ChargeType, ChargeType] = Map(
    ReturnDebitCharge -> VatReturn1stLPP,
    VatProtectiveAssessmentCharge -> VatPA1stLPP,
    AACharge -> VatAA1stLPP,
    AAReturnDebitCharge -> VatAAReturnCharge1stLPP,
    ErrorCorrectionDebitCharge -> VatErrorCorrection1stLPP,
    PaymentOnAccountReturnDebitCharge -> VatPOAReturn1stLPP,
    CentralAssessmentCharge -> VatCentralAssessment1stLPP,
    OADebitCharge -> VatOfficersAssessment1stLPP,
    VATOverpaymentforTax -> VatOverpayments1stLPP
  )

  val penaltyChargeMappingLPP2: Map[ChargeType, ChargeType] = Map(
    VatProtectiveAssessmentCharge -> VatPA2ndLPP,
    AACharge -> VatAA2ndLPP,
    AAReturnDebitCharge -> VatAAReturnCharge2ndLPP,
    ReturnDebitCharge -> VatReturn2ndLPP,
    ErrorCorrectionDebitCharge -> VatErrorCorrection2ndLPP,
    PaymentOnAccountReturnDebitCharge -> VatPOAReturn2ndLPP,
    CentralAssessmentCharge -> VatCentralAssessment2ndLPP,
    OADebitCharge -> VatOfficersAssessment2ndLPP,
    VATOverpaymentforTax -> VatOverpayments2ndLPP
  )

  def apply: String => ChargeType = input => {
    allChargeTypes.find { chargeType =>
      chargeType.value.toUpperCase.equals(input.trim.toUpperCase)
    }.getOrElse(throw new IllegalArgumentException("Invalid Charge Type: " + input))
  }

  def unapply(arg: ChargeType): String = arg.value

  implicit val reads: Reads[ChargeType] = __.read[String].map(apply)

}
