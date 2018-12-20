/*
 * Copyright 2018 HM Revenue & Customs
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

import play.api.libs.json.Reads._
import play.api.libs.json._

sealed trait ChargeType {
  val value: String
}
case object ReturnCharge extends ChargeType {
  override val value = "VAT Return Charge"
}
case object ReturnDebitCharge extends ChargeType {
  override val value: String = "VAT Return Debit Charge"
}
case object ReturnCreditCharge extends ChargeType {
  override val value: String = "VAT Return Credit Charge"
}
case object OACharge extends ChargeType {
  override val value: String = "VAT Officer's Assessment"
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
case object DefaultSurcharge extends ChargeType {
  override val value: String = "VAT Default Surcharge"
}
case object CentralAssessmentCharge extends ChargeType {
  override val value: String = "VAT Central Assessment"
}
case object ErrorCorrectionCharge extends ChargeType {
  override val value: String = "VAT Error Correction"
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
case object RepaySupplement extends ChargeType {
  override val value: String = "VAT Repay Supplement"
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

object ChargeType {

  def apply: String => ChargeType = {
    case ReturnCharge.value => ReturnCharge
    case ReturnDebitCharge.value => ReturnDebitCharge
    case ReturnCreditCharge.value => ReturnCreditCharge
    case OACharge.value => OACharge
    case OACreditCharge.value => OACreditCharge
    case OADebitCharge.value => OADebitCharge
    case DefaultSurcharge.value => DefaultSurcharge
    case CentralAssessmentCharge.value => CentralAssessmentCharge
    case ErrorCorrectionCharge.value => ErrorCorrectionCharge
    case ErrorCorrectionCreditCharge.value => ErrorCorrectionCreditCharge
    case ErrorCorrectionDebitCharge.value => ErrorCorrectionDebitCharge
    case RepaymentSupplement.value => RepaymentSupplement
    case RepaySupplement.value => RepaySupplement
    case OADefaultInterestCharge.value => OADefaultInterestCharge
    case AACharge.value => AACharge
    case AAInterestCharge.value => AAInterestCharge
    case AAFurtherInterestCharge.value => AAFurtherInterestCharge
    case BnpRegPre2010Charge.value => BnpRegPre2010Charge
    case BnpRegPost2010Charge.value => BnpRegPost2010Charge
    case FtnEachPartnerCharge.value => FtnEachPartnerCharge
    case FtnMatPost2010Charge.value => FtnMatPost2010Charge
    case FtnMatPre2010Charge.value => FtnMatPre2010Charge
    case MiscPenaltyCharge.value => MiscPenaltyCharge
    case MpPre2009Charge.value => MpPre2009Charge
    case MpRepeatedPre2009Charge.value => MpRepeatedPre2009Charge
    case CivilEvasionPenaltyCharge.value => CivilEvasionPenaltyCharge
    case _ => throw new RuntimeException("Invalid Charge Type")
  }

  def unapply(arg: ChargeType): String = arg.value

  implicit val reads: Reads[ChargeType] = __.read[String].map(apply)

  implicit val writes: Writes[ChargeType] = Writes { charge => JsString(charge.value) }

}
