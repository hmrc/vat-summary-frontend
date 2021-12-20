/*
 * Copyright 2021 HM Revenue & Customs
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

package views.templates.payments

import java.time.LocalDate

import models.payments._
import play.api.i18n.Messages
import views.templates.formatters.dates.DisplayDateRangeHelper.displayDateRange

sealed case class PaymentMessageHelper(name: String, title: String, principalUserDescription: Option[String], agentDescription: Option[String])

//scalastyle:off
object PaymentMessageHelper {

  object UnrepayableOverpayment extends PaymentMessageHelper(
    VatUnrepayableOverpayment.value,
    "chargeType.vatUnrepayableOverpaymentTitle",
    Some("chargeType.UnrepayableOverpaymentDescription"),
    Some("chargeType.UnrepayableOverpaymentDescription")
  )

  object RepaymentSupplementRecovery extends PaymentMessageHelper(
    VatRepaymentSupplementRecovery.value,
    "chargeType.repaymentSupplementRecTitle",
    Some("chargeType.repaymentSupplementRecDescription"),
    Some("chargeType.repaymentSupplementRecDescription"),
  )

  object IndirectTaxRevenueRecovery extends PaymentMessageHelper(
    VatIndirectTaxRevenueRecovery.value,
    "chargeType.indirectRevRecoveryTitle",
    Some("chargeType.indirectRevRecoveryDescription"),
    Some("chargeType.indirectRevRecoveryDescription")
  )

  object DefaultInterest extends PaymentMessageHelper(
    VatDefaultInterest.value,
    "chargeType.defaultInterestTitle",
    Some("chargeType.defaultAndFurtherInterestDescription"),
    Some("chargeType.defaultAndFurtherInterestDescription"),
  )

  object FurtherInterest extends PaymentMessageHelper(
    VatFurtherInterest.value,
    "chargeType.furtherInterestTitle",
    Some("chargeType.defaultAndFurtherInterestDescription"),
    Some("chargeType.defaultAndFurtherInterestDescription")
  )

  object VatReturnCreditCharge extends PaymentMessageHelper(
    ReturnCreditCharge.value,
    "chargeType.vatReturnCreditChargeTitle",
    Some("chargeType.vatReturnCreditChargeDescription"),
    Some("chargeType.vatReturnCreditChargeDescription")
  )

  object VatReturnDebitCharge extends PaymentMessageHelper(
    ReturnDebitCharge.value,
    "chargeType.vatReturnDebitChargeTitle",
    Some("chargeType.vatReturnDebitChargeDescription"),
    Some("chargeType.vatReturnDebitChargeDescription")
  )


  object VatOfficerAssessmentCreditCharge extends PaymentMessageHelper(
    OACreditCharge.value,
    "chargeType.officerAssessmentChargeTitle",
    Some("chargeType.officerAssessmentCreditChargeDescription"),
    Some("chargeType.officerAssessmentCreditChargeDescription")
  )

  object VatOfficerAssessmentDebitCharge extends PaymentMessageHelper(
    OADebitCharge.value,
    "chargeType.officerAssessmentChargeTitle",
    Some("chargeType.officerAssessmentDebitChargeDescription"),
    Some("chargeType.officerAssessmentDebitChargeDescription")
  )

  object VatCentralAssessment extends PaymentMessageHelper(
    CentralAssessmentCharge.value,
    "chargeType.vatCentralAssessmentTitle",
    Some("chargeType.vatCentralAssessmentDescription"),
    Some("chargeType.vatCentralAssessmentDescription")
  )

  object VatDebitDefaultSurcharge extends PaymentMessageHelper(
    DebitDefaultSurcharge.value,
    "chargeType.vatDefaultSurchargeTitle",
    Some("chargeType.vatDefaultSurchargeDescription"),
    Some("chargeType.vatDefaultSurchargeDescription")
  )

  object VatCreditDefaultSurcharge extends PaymentMessageHelper(
    CreditDefaultSurcharge.value,
    "chargeType.vatDefaultSurchargeTitle",
    Some("chargeType.vatDefaultSurchargeDescription"),
    Some("chargeType.vatDefaultSurchargeDescription")
  )

  object VatErrorCorrectionDebitCharge extends PaymentMessageHelper(
    ErrorCorrectionDebitCharge.value,
    "chargeType.vatErrorCorrectionDebitChargeTitle",
    Some("chargeType.vatErrorCorrectionChargeDescription"),
    Some("chargeType.vatErrorCorrectionChargeDescription")
  )

  object VatErrorCorrectionCreditCharge extends PaymentMessageHelper(
    ErrorCorrectionCreditCharge.value,
    "chargeType.vatErrorCorrectionCreditChargeTitle",
    Some("chargeType.vatErrorCorrectionChargeDescription"),
    Some("chargeType.vatErrorCorrectionChargeDescription")
  )

  object VatRepaymentSupplement extends PaymentMessageHelper(
    RepaymentSupplement.value,
    "chargeType.vatRepaymentSupplementTitle",
    Some("chargeType.vatRepaymentSupplementDescription"),
    Some("chargeType.vatRepaymentSupplementDescription")
  )

  object OADefaultInterest extends PaymentMessageHelper(
    OADefaultInterestCharge.value,
    "chargeType.OADefaultInterestTitle",
    Some("chargeType.OADefaultInterestDescription"),
    Some("chargeType.OADefaultInterestDescription")
  )

  object VatBnpRegPre2010Charge extends PaymentMessageHelper(
    BnpRegPre2010Charge.value,
    "chargeType.bnpRegPre2010ChargeTitle",
    Some("chargeType.bnpRegPre2010ChargeDescription"),
    Some("chargeType.bnpRegPre2010ChargeDescription")
  )

  object VatBnpRegPost2010Charge extends PaymentMessageHelper(
    BnpRegPost2010Charge.value,
    "chargeType.bnpRegPost2010ChargeTitle",
    Some("chargeType.bnpRegPost2010ChargeDescription"),
    Some("chargeType.bnpRegPost2010ChargeDescription")
  )

  object VatFtnMatPre2010Charge extends PaymentMessageHelper(
    FtnMatPre2010Charge.value,
    "chargeType.ftnMatPre2010ChargeTitle",
    Some("chargeType.ftnMatPre2010ChargeDescription"),
    Some("chargeType.ftnMatPre2010ChargeDescription")
  )

  object VatFtnMatPost2010Charge extends PaymentMessageHelper(
    FtnMatPost2010Charge.value,
    "chargeType.ftnMatPost2010ChargeTitle",
    Some("chargeType.ftnMatPost2010ChargeDescription"),
    Some("chargeType.ftnMatPost2010ChargeDescription")
  )

  object VatMiscPenaltyCharge extends PaymentMessageHelper(
    MiscPenaltyCharge.value,
    "chargeType.miscPenaltyCharge",
    None,
    None
  )

  object VatOfficersAssessmentFurtherInterest extends PaymentMessageHelper(
    OAFurtherInterestCharge.value,
    "chargeType.VatOfficersAssessmentFurtherInterestTitle",
    Some("chargeType.VatOfficersAssessmentFurtherInterestDescription"),
    Some("chargeType.VatOfficersAssessmentFurtherInterestDescription")
  )

  object VatAdditionalAssessment extends PaymentMessageHelper(
    AACharge.value,
    "chargeType.VatAdditionalAssessmentTitle",
    Some("chargeType.VatAdditionalAssessmentDescription"),
    Some("chargeType.VatAdditionalAssessmentDescription")
  )

  object VatAADefaultInterest extends PaymentMessageHelper(
    AAInterestCharge.value,
    "chargeType.VatAdditionalAssessmentDefaultInterestTitle",
    Some("chargeType.VatAdditionalAssessmentDefaultInterestDescription"),
    Some("chargeType.VatAdditionalAssessmentDefaultInterestDescription")
  )

  object VatAAFurtherInterest extends PaymentMessageHelper(
    AAFurtherInterestCharge.value,
    "chargeType.VatAdditionalAssessmentFurtherInterestTitle",
    Some("chargeType.VatAdditionalAssessmentFurtherInterestDescription"),
    Some("chargeType.VatAdditionalAssessmentFurtherInterestDescription")
  )

  object VatAAMonthlyInstalment extends PaymentMessageHelper(
    AAMonthlyInstalment.value,
    "chargeType.VatAnnualAccountMonthlyInstalmentTitle",
    Some("chargeType.VatAnnualAccountMonthlyInstalmentDescription"),
    Some("chargeType.VatAnnualAccountMonthlyInstalmentDescription")
  )

  object VatAAQuarterlyInstalments extends PaymentMessageHelper(
    AAQuarterlyInstalments.value,
    "chargeType.VatAnnualAccountQuarterlyInstalmentsTitle",
    Some("chargeType.VatAnnualAccountQuarterlyInstalmentsDescription"),
    Some("chargeType.VatAnnualAccountQuarterlyInstalmentsDescription")
  )

  object VatAAReturnDebitCharge extends PaymentMessageHelper(
    AAReturnDebitCharge.value,
    "chargeType.VatAnnualAccountReturnDebitChargeTitle",
    Some("chargeType.VatAnnualAccountReturnDebitChargeDescription"),
    Some("chargeType.VatAnnualAccountReturnDebitChargeDescription")
  )

  object VatAAReturnCreditCharge extends PaymentMessageHelper(
    AAReturnCreditCharge.value,
    "chargeType.VatAnnualAccountReturnCreditChargeTitle",
    Some("chargeType.VatAnnualAccountReturnCreditChargeDescription"),
    Some("chargeType.VatAnnualAccountReturnCreditChargeDescription")
  )

  object VatStatutoryInterestCharge extends PaymentMessageHelper(
    StatutoryInterestCharge.value,
    "chargeType.VatStatutoryInterestTitle",
    Some("chargeType.VatStatutoryInterestDescription"),
    Some("chargeType.VatStatutoryInterestDescription")
  )

  object VatSecurityDepositRequest extends PaymentMessageHelper(
    VatSecurityDepositRequestCharge.value,
    "chargeType.vatSecurityDepositRequestTitle",
    Some("chargeType.vatSecurityDepositRequestDescription"),
    Some("chargeType.vatSecurityDepositRequestDescription")
  )

  object VatEcNoticeFurtherInterest extends PaymentMessageHelper(
    VatECFurtherInterestCharge.value,
    "chargeType.vatEcNoticeFurtherInterestTitle",
    Some("chargeType.vatEcNoticeFurtherInterestDescription"),
    Some("chargeType.vatEcNoticeFurtherInterestDescription")
  )

  object VatPADefaultInterest extends PaymentMessageHelper(
    VatPADefaultInterestCharge.value,
    "chargeType.vatPADefaultInterestTitle",
    Some("chargeType.vatPADefaultInterestDescription"),
    Some("chargeType.vatPADefaultInterestDescription")
  )

  object CivilEvasionPenalty extends PaymentMessageHelper(
    CivilEvasionPenaltyCharge.value,
    "chargeType.civilEvasionPenaltyTitle",
    Some("chargeType.civilEvasionPenaltyDescription"),
    Some("chargeType.civilEvasionPenaltyDescription")
  )

  object VatInaccuraciesInECSales extends PaymentMessageHelper(
    VatInaccuraciesInECSalesCharge.value,
    "chargeType.vatInaccuraciesInECSalesTitle",
    Some("chargeType.vatInaccuraciesInECSalesDescription"),
    Some("chargeType.vatInaccuraciesInECSalesDescription")
  )

  object VatFailureToSubmitECSales extends PaymentMessageHelper(
    VatFailureToSubmitECSalesCharge.value,
    "chargeType.vatFailureToSubmitECSalesTitle",
    Some("chargeType.vatFailureToSubmitECSalesDescription"),
    Some("chargeType.vatFailureToSubmitECSalesDescription")
  )

  object FtnEachPartner extends PaymentMessageHelper(
    FtnEachPartnerCharge.value,
    "chargeType.ftnEachPartnerTitle",
    Some("chargeType.ftnEachPartnerDescription"),
    Some("chargeType.ftnEachPartnerDescription")
  )

  object VatOAInaccuracies2009 extends PaymentMessageHelper(
    VatOAInaccuraciesFrom2009.value,
    "chargeType.vatOAInaccuraciesFrom2009Title",
    Some("chargeType.vatOAInaccuraciesFrom2009Description"),
    Some("chargeType.vatOAInaccuraciesFrom2009Description")
  )

  object VatInaccuracyAssessmentsPenCharge extends PaymentMessageHelper(
    InaccuraciesAssessmentsPenCharge.value,
    "chargeType.vatInaccuracyAssessmentsPenChargeTitle",
    Some("chargeType.vatInaccuracyAssessmentsPenChargeDescription"),
    Some("chargeType.vatInaccuracyAssessmentsPenChargeDescription")
  )

  object VatMpPre2009Charge extends PaymentMessageHelper(
    MpPre2009Charge.value,
    "chargeType.vatMpPre2009ChargeTitle",
    Some("chargeType.vatMpPre2009ChargeDescription"),
    Some("chargeType.vatMpPre2009ChargeDescription")
  )

  object VatMpRepeatedPre2009Charge extends PaymentMessageHelper(
    MpRepeatedPre2009Charge.value,
    "chargeType.vatMpRepeatedPre2009ChargeTitle",
    Some("chargeType.vatMpRepeatedPre2009ChargeDescription"),
    Some("chargeType.vatMpRepeatedPre2009ChargeDescription")
  )

  object VatInaccuraciesReturnReplacedCharge extends PaymentMessageHelper(
    InaccuraciesReturnReplacedCharge.value,
    "chargeType.vatInaccuraciesReturnReplacedChargeTitle",
    Some("chargeType.vatInaccuraciesReturnReplacedChargeDescription"),
    Some("chargeType.vatInaccuraciesReturnReplacedChargeDescription")
  )

  object VatWrongDoingPenaltyCharge extends PaymentMessageHelper(
    WrongDoingPenaltyCharge.value,
    "chargeType.vatWrongDoingPenaltyChargeTitle",
    Some("chargeType.vatWrongDoingPenaltyChargeDescription"),
    Some("chargeType.vatWrongDoingPenaltyChargeDescription")
  )

  object VatECDefaultInterest extends PaymentMessageHelper(
    VatECDefaultInterestCharge.value,
    "chargeType.vatErrorCorrectionNoticeDefaultInterestTitle",
    Some("chargeType.vatErrorCorrectionNoticeDefaultInterestDescription"),
    Some("chargeType.vatErrorCorrectionNoticeDefaultInterestDescription")
  )

  object VatPaFurtherInterest extends PaymentMessageHelper(
    VatPaFurtherInterestCharge.value,
    "chargeType.vatPaFurtherInterestTitle",
    Some("chargeType.vatPaFurtherInterestDescription"),
    Some("chargeType.vatPaFurtherInterestDescription")
  )

  object VatCarterPenaltyCharge extends PaymentMessageHelper(
    CarterPenaltyCharge.value,
    "chargeType.vatCarterPenaltyChargeTitle",
    Some("chargeType.vatCarterPenaltyChargeDescription"),
    Some("chargeType.vatCarterPenaltyChargeDescription")
  )

  object VatFailureToNotifyRCSL extends PaymentMessageHelper(
    FailureToNotifyRCSLCharge.value,
    "chargeType.vatFailureToNotifyRCSLTitle",
    Some("chargeType.vatFailureToNotifyRCSLDescription"),
    Some("chargeType.vatFailureToNotifyRCSLDescription")
  )

  object VatFailureToSubmitRCSL extends PaymentMessageHelper(
    FailureToSubmitRCSLCharge.value,
    "chargeType.vatFailureToSubmitRCSLTitle",
    Some("chargeType.vatFailureToSubmitRCSLDescription"),
    Some("chargeType.vatFailureToSubmitRCSLDescription")
  )

  object VatCreditReturnOffsetCharge extends PaymentMessageHelper(
    CreditReturnOffsetCharge.value,
    "chargeType.vatCreditReturnOffsetChargeTitle",
    Some("chargeType.vatCreditReturnOffsetChargeDescription"),
    Some("chargeType.vatCreditReturnOffsetChargeDescription")
  )

  object ProtectiveAssessmentCharge extends PaymentMessageHelper(
    VatProtectiveAssessmentCharge.value,
    "chargeType.vatProtectiveAssessmentChargeTitle",
    Some("chargeType.vatProtectiveAssessmentChargeDescription"),
    Some("chargeType.vatProtectiveAssessmentChargeDescription")
  )

  object UnallocatedPaymentCharge extends PaymentMessageHelper(
    UnallocatedPayment.value,
    "chargeType.unallocatedPaymentTitle",
    Some("chargeType.unallocatedPaymentDescription"),
    Some("chargeType.unallocatedPaymentDescription")
  )

  object RefundsCharge extends PaymentMessageHelper(
    Refund.value,
    "chargeType.refundTitle",
    Some("chargeType.refundDescription"),
    Some("chargeType.refundDescription")
  )

  object VATPOAInstalmentCharge extends PaymentMessageHelper(
    PaymentOnAccountInstalments.value,
    "chargeType.POAInstalmentTitle",
    Some("chargeType.POAChargeDescription"),
    Some("chargeType.POAChargeDescription")
  )

  object VATPOAReturnDebitCharge extends PaymentMessageHelper(
    PaymentOnAccountReturnDebitCharge.value,
    "chargeType.POAReturnDebitChargeTitle",
    Some("chargeType.POAChargeDescription"),
    Some("chargeType.POAChargeDescription")
  )

  object VATPOAReturnCreditCharge extends PaymentMessageHelper(
    PaymentOnAccountReturnCreditCharge.value,
    "chargeType.POAReturnCreditChargeTitle",
    Some("chargeType.POAChargeDescription"),
    Some("chargeType.POAChargeDescription")
  )

  val values: Seq[PaymentMessageHelper] = Seq(
    UnrepayableOverpayment,
    RepaymentSupplementRecovery,
    IndirectTaxRevenueRecovery,
    DefaultInterest,
    FurtherInterest,
    VatReturnDebitCharge,
    VatReturnCreditCharge,
    VatOfficerAssessmentCreditCharge,
    VatOfficerAssessmentDebitCharge,
    VatCentralAssessment,
    VatDebitDefaultSurcharge,
    VatCreditDefaultSurcharge,
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
    VatAAMonthlyInstalment,
    VatAAQuarterlyInstalments,
    VatAAReturnCreditCharge,
    VatAAReturnDebitCharge,
    CivilEvasionPenalty,
    VatInaccuraciesInECSales,
    VatFailureToSubmitECSales,
    FtnEachPartner,
    VatOAInaccuracies2009,
    VatInaccuracyAssessmentsPenCharge,
    VatMpPre2009Charge,
    VatMpRepeatedPre2009Charge,
    VatInaccuraciesReturnReplacedCharge,
    VatWrongDoingPenaltyCharge,
    VatPADefaultInterest,
    VatStatutoryInterestCharge,
    VatECDefaultInterest,
    VatPaFurtherInterest,
    VatCarterPenaltyCharge,
    VatFailureToNotifyRCSL,
    VatFailureToSubmitRCSL,
    VatCreditReturnOffsetCharge,
    ProtectiveAssessmentCharge,
    UnallocatedPaymentCharge,
    RefundsCharge,
    VATPOAInstalmentCharge,
    VATPOAReturnDebitCharge,
    VATPOAReturnCreditCharge
  )

  def getFullDescription(descriptionMessageKey: String, from: Option[LocalDate], to: Option[LocalDate])
                        (implicit messages: Messages): String = {
    (from, to) match {
      case (Some(fromDate), Some(toDate)) => messages(descriptionMessageKey, displayDateRange(fromDate, toDate, useShortDayFormat = true))
      case _ => messages(descriptionMessageKey)
    }
  }

  def getChargeType(lookupName: String): PaymentMessageHelper = {
    values.find(_.name == lookupName).getOrElse(throw new IllegalArgumentException(s"Invalid charge type: $lookupName"))
  }
}
