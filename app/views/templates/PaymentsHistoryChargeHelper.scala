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

package views.templates

import models.payments._
import play.api.Logger

sealed case class PaymentsHistoryChargeHelper(name: String, title: String, description: Option[String], id: String = "")

object PaymentsHistoryChargeHelper {
  object VatReturnCreditCharge extends PaymentsHistoryChargeHelper(
    ReturnCreditCharge.value,
    "paymentsHistory.vatReturnCreditChargeTitle",
    Some("paymentsHistory.vatReturnCreditChargeDescription"))

  object VatReturnDebitCharge extends PaymentsHistoryChargeHelper(
    ReturnDebitCharge.value,
    "paymentsHistory.vatReturnDebitChargeTitle",
    Some("paymentsHistory.vatReturnDebitChargeDescription"))

  object VatOfficerAssessmentCreditCharge extends PaymentsHistoryChargeHelper(
    OACreditCharge.value,
    "paymentsHistory.officerAssessmentChargeTitle",
    Some("paymentsHistory.officerAssessmentCreditChargeDescription"))

  object VatOfficerAssessmentDebitCharge extends PaymentsHistoryChargeHelper(
    OADebitCharge.value,
    "paymentsHistory.officerAssessmentChargeTitle",
    Some("paymentsHistory.officerAssessmentDebitChargeDescription"))

  object VatCentralAssessment extends PaymentsHistoryChargeHelper(
    CentralAssessmentCharge.value,
    "paymentsHistory.vatCentralAssessmentTitle",
    Some("paymentsHistory.vatCentralAssessmentDescription"))

  object VatDefaultSurcharge extends PaymentsHistoryChargeHelper(
    DefaultSurcharge.value,
    "paymentsHistory.vatDefaultSurchargeTitle",
    Some("paymentsHistory.vatDefaultSurchargeDescription"))

  object VatErrorCorrectionDebitCharge extends PaymentsHistoryChargeHelper(
    ErrorCorrectionDebitCharge.value,
    "paymentsHistory.vatErrorCorrectionDebitChargeTitle",
    Some("paymentsHistory.vatErrorCorrectionChargeDescription")
  )

  object VatErrorCorrectionCreditCharge extends PaymentsHistoryChargeHelper(
    ErrorCorrectionCreditCharge.value,
    "paymentsHistory.vatErrorCorrectionCreditChargeTitle",
    Some("paymentsHistory.vatErrorCorrectionChargeDescription")
  )

  object VatRepaymentSupplement extends PaymentsHistoryChargeHelper(
    RepaymentSupplement.value,
    "paymentsHistory.vatRepaymentSupplementTitle",
    Some("paymentsHistory.vatRepaymentSupplementDescription"),
    "repayment"
  )

  object OADefaultInterest extends PaymentsHistoryChargeHelper(
    OADefaultInterestCharge.value,
    "paymentsHistory.OADefaultInterestTitle",
    Some("paymentsHistory.OADefaultInterestDescription")
  )

  object VatBnpRegPre2010Charge extends PaymentsHistoryChargeHelper(
    BnpRegPre2010Charge.value,
    "paymentsHistory.bnpRegPre2010ChargeTitle",
    Some("paymentsHistory.bnpRegPre2010ChargeDescription")
  )

  object VatBnpRegPost2010Charge extends PaymentsHistoryChargeHelper(
    BnpRegPost2010Charge.value,
    "paymentsHistory.bnpRegPost2010ChargeTitle",
    Some("paymentsHistory.bnpRegPost2010ChargeDescription")
  )

  object VatFtnMatPre2010Charge extends PaymentsHistoryChargeHelper(
    FtnMatPre2010Charge.value,
    "paymentsHistory.ftnMatPre2010ChargeTitle",
    Some("paymentsHistory.ftnMatPre2010ChargeDescription")
  )

  object VatFtnMatPost2010Charge extends PaymentsHistoryChargeHelper(
    FtnMatPost2010Charge.value,
    "paymentsHistory.ftnMatPost2010ChargeTitle",
    Some("paymentsHistory.ftnMatPost2010ChargeDescription")
  )

  object VatMiscPenaltyCharge extends PaymentsHistoryChargeHelper(
    MiscPenaltyCharge.value,
    "paymentsHistory.miscPenaltyCharge",
    None
  )

  object VatOfficersAssessmentFurtherInterest extends PaymentsHistoryChargeHelper(
    VatOfficersAssessmentFurtherInterestCharge.value,
    "paymentsHistory.VatOfficersAssessmentFurtherInterestTitle",
    Some("paymentsHistory.VatOfficersAssessmentFurtherInterestDescription")
  )

  object VatAdditionalAssessment extends PaymentsHistoryChargeHelper(
    AACharge.value,
    "paymentsHistory.VatAdditionalAssessmentTitle",
    Some("paymentsHistory.VatAdditionalAssessmentDescription")
  )

  object VatAADefaultInterest extends PaymentsHistoryChargeHelper(
    AAInterestCharge.value,
    "paymentsHistory.VatAdditionalAssessmentDefaultInterestTitle",
    Some("paymentsHistory.VatAdditionalAssessmentDefaultInterestDescription")
  )

  object VatAAFurtherInterest extends PaymentsHistoryChargeHelper(
    AAFurtherInterestCharge.value,
    "paymentsHistory.VatAdditionalAssessmentFurtherInterestTitle",
    Some("paymentsHistory.VatAdditionalAssessmentFurtherInterestDescription")
  )

  object VatStatutoryInterestCharge extends PaymentsHistoryChargeHelper(
    StatutoryInterestCharge.value,
    "paymentsHistory.VatStatutoryInterestTitle",
    Some("paymentsHistory.VatStatutoryInterestDescription"),
    "repayment"
  )

  object VatSecurityDepositRequest extends PaymentsHistoryChargeHelper(
    VatSecurityDepositRequestCharge.value,
    "paymentsHistory.vatSecurityDepositRequestTitle",
    Some("paymentsHistory.vatSecurityDepositRequestDescription")
  )

  object VatEcNoticeFurtherInterest extends PaymentsHistoryChargeHelper(
    VatECFurtherInterestCharge.value,
    "paymentsHistory.vatEcNoticeFurtherInterestTitle",
    Some("paymentsHistory.vatEcNoticeFurtherInterestDescription")
  )

  object CivilEvasionPenalty extends PaymentsHistoryChargeHelper(
    CivilEvasionPenaltyCharge.value,
    "paymentsHistory.civilEvasionPenaltyTitle",
    Some("paymentsHistory.civilEvasionPenaltyDescription")
  )

  object VatInaccuraciesInECSales extends PaymentsHistoryChargeHelper(
    VatInaccuraciesInECSalesCharge.value,
    "paymentsHistory.vatInaccuraciesInECSalesTitle",
    Some("paymentsHistory.vatInaccuraciesInECSalesDescription")
  )

  object VatFailureToSubmitECSales extends PaymentsHistoryChargeHelper(
    VatFailureToSubmitECSalesCharge.value,
    "paymentsHistory.vatFailureToSubmitECSalesTitle",
    Some("paymentsHistory.vatFailureToSubmitECSalesDescription")
  )

  object FtnEachPartner extends PaymentsHistoryChargeHelper(
    FtnEachPartnerCharge.value,
    "paymentsHistory.ftnEachPartnerTitle",
    Some("paymentsHistory.ftnEachPartnerDescription")
  )

  object VatOAInaccuracies2009 extends PaymentsHistoryChargeHelper(
    VatOAInaccuraciesFrom2009.value,
    "paymentsHistory.vatOAInaccuraciesFrom2009Title",
    Some("paymentsHistory.vatOAInaccuraciesFrom2009Description")
  )

  val values = Seq(
    VatReturnDebitCharge,
    VatReturnCreditCharge,
    VatOfficerAssessmentCreditCharge,
    VatOfficerAssessmentDebitCharge,
    VatCentralAssessment,
    VatDefaultSurcharge,
    VatErrorCorrectionDebitCharge,
    VatErrorCorrectionCreditCharge,
    VatRepaymentSupplement,
    OADefaultInterest,
    VatBnpRegPre2010Charge,
    VatBnpRegPost2010Charge,
    VatFtnMatPre2010Charge,
    VatFtnMatPost2010Charge,
    VatMiscPenaltyCharge,
    VatSecurityDepositRequest,
    VatEcNoticeFurtherInterest,
    VatOfficersAssessmentFurtherInterest,
    VatAdditionalAssessment,
    VatAADefaultInterest,
    VatAAFurtherInterest,
    VatStatutoryInterestCharge,
    CivilEvasionPenalty,
    VatInaccuraciesInECSales,
    VatFailureToSubmitECSales,
    FtnEachPartner,
    VatOAInaccuracies2009
  )

  def getChargeType(lookupName: String): Option[PaymentsHistoryChargeHelper] = {
    val chargeType = values.find(_.name == lookupName)
    if(chargeType.isEmpty) Logger.warn("[PaymentsHistoryChargeHelper][getChargeType] Valid charge type received, but not valid on Payment History page")
    chargeType
  }
}
