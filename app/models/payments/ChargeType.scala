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

import models.payments.ChargeType.interestChargeTypes
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
    VatAdditionalAssessmentLPI
  ) ++ positiveOrNegativeChargeTypes

  val interestChargeTypes: Set[ChargeType] = Set(
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
    VatAdditionalAssessmentLPI
  )

  val penaltyInterestChargeTypes: Set[ChargeType] = Set(
    VatReturn1stLPPLPI,
    VatReturn2ndLPPLPI,
    VatCA1stLPPLPI,
    VatCA2ndLPPLPI,
    VatOA1stLPPLPI,
    VatOA2ndLPPLPI,
    VatPA1stLPPLPI,
    VatPA2ndLPPLPI
  )

  val penaltyChargeTypes: Set[ChargeType] = Set(
    VatReturn1stLPP,
    VatPA1stLPP,
    VatPA2ndLPP,
    VatAA1stLPP,
    VatAA2ndLPP
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
