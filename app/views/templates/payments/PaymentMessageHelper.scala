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

package views.templates.payments

import java.time.LocalDate
import models.payments._
import play.api.i18n.Messages
import views.templates.formatters.dates.DisplayDateRangeHelper.displayDateRange

sealed case class PaymentMessageHelper(name: String, title: String, principalUserDescription: Option[String], agentDescription: Option[String])

//scalastyle:off
//noinspection ScalaWeakerAccess
object PaymentMessageHelper {

  object OverpaymentforTax extends PaymentMessageHelper(
    VATOverpaymentforTax.value,
    "chargeType.vATOverpaymentforTaxTitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object OverpaymentforTaxLPI extends PaymentMessageHelper(
    VATOverpaymentforTaxLPI.value,
    "chargeType.overpaymentforTaxLPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object Overpayments1stLPP extends PaymentMessageHelper(
    VatOverpayments1stLPP.value,
    "chargeType.vatOP1stLPP.Title",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object Overpayments2ndLPP extends PaymentMessageHelper(
    VatOverpayments2ndLPP.value,
    "chargeType.vatOverpayments2ndLPPTitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object UnrepayableOverpayment extends PaymentMessageHelper(
    VatUnrepayableOverpayment.value,
    "chargeType.vatUnrepayableOverpaymentTitle",
    Some("chargeType.vatUnrepayableOverpaymentDescription"),
    Some("chargeType.vatUnrepayableOverpaymentDescription")
  )

  object RepaymentSupplementRecovery extends PaymentMessageHelper(
    VatRepaymentSupplementRecovery.value,
    "chargeType.repaymentSupplementRecTitle",
    Some("chargeType.repaymentSupplementRecDescription"),
    Some("chargeType.repaymentSupplementRecDescription")
  )

  object IndirectTaxRevenueRecovery extends PaymentMessageHelper(
    VatIndirectTaxRevenueRecovery.value,
    "chargeType.indirectRevRecoveryTitle",
    Some("chargeType.indirectRevRecoveryDescription"),
    Some("chargeType.indirectRevRecoveryDescription")
  )

  object OverpaymentForTaxRPI extends PaymentMessageHelper(
    VatOverpaymentForTaxRPI.value,
    "chargeType.overpaymentForTaxRPITitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object Overpayments1stLPPRPI extends PaymentMessageHelper(
    VatOverpayments1stLPPRPI.value,
    "chargeType.overpayments1stLPPRPITitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object Overpayments2ndLPPRPI extends PaymentMessageHelper(
    VatOverpayments2ndLPPRPI.value,
    "chargeType.overpayments2ndLPPRPITitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object DefaultInterestDebit extends PaymentMessageHelper(
    VatDefaultInterestDebit.value,
    "chargeType.defaultInterestTitle",
    Some("chargeType.defaultAndFurtherInterestDescription"),
    Some("chargeType.defaultAndFurtherInterestDescription")
  )

  object DefaultInterestCredit extends PaymentMessageHelper(
    VatDefaultInterestCredit.value,
    "chargeType.defaultInterestTitle",
    Some("chargeType.defaultAndFurtherInterestDescription"),
    Some("chargeType.defaultAndFurtherInterestDescription")
  )

  object FurtherInterestDebit extends PaymentMessageHelper(
    VatFurtherInterestDebit.value,
    "chargeType.furtherInterestTitle",
    Some("chargeType.defaultAndFurtherInterestDescription"),
    Some("chargeType.defaultAndFurtherInterestDescription")
  )

  object FurtherInterestCredit extends PaymentMessageHelper(
    VatFurtherInterestCredit.value,
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
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VatOfficerAssessmentCreditCharge extends PaymentMessageHelper(
    OACreditCharge.value,
    "chargeType.officerAssessmentChargeTitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VatOfficerAssessmentDebitCharge extends PaymentMessageHelper(
    OADebitCharge.value,
    "chargeType.officerAssessmentChargeTitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VatCentralAssessment extends PaymentMessageHelper(
    CentralAssessmentCharge.value,
    "chargeType.vatCentralAssessmentTitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
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
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VatErrorCorrectionCreditCharge extends PaymentMessageHelper(
    ErrorCorrectionCreditCharge.value,
    "chargeType.vatErrorCorrectionCreditChargeTitle",
    Some("chargeType.for"),
    Some("chargeType.for")
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
    Some("chargeType.bnpRegPre2010ChargeDescription.agent")
  )

  object VatBnpRegPost2010Charge extends PaymentMessageHelper(
    BnpRegPost2010Charge.value,
    "chargeType.bnpRegPost2010ChargeTitle",
    Some("chargeType.bnpRegPost2010ChargeDescription"),
    Some("chargeType.bnpRegPost2010ChargeDescription.agent")
  )

  object VatFtnMatPre2010Charge extends PaymentMessageHelper(
    FtnMatPre2010Charge.value,
    "chargeType.ftnMatPre2010ChargeTitle",
    Some("chargeType.ftnMatPre2010ChargeDescription"),
    Some("chargeType.ftnMatPre2010ChargeDescription.agent")
  )

  object VatFtnMatPost2010Charge extends PaymentMessageHelper(
    FtnMatPost2010Charge.value,
    "chargeType.ftnMatPost2010ChargeTitle",
    Some("chargeType.ftnMatPost2010ChargeDescription"),
    Some("chargeType.ftnMatPost2010ChargeDescription.agent")
  )

  object VatFtnMatPost2010ChargeLPI extends PaymentMessageHelper(
    FtnMatPost2010ChargeLPI.value,
    "chargeType.ftnMatPost2010ChargeLPITitle",
    None,
    None
  )

  object VatMiscPenaltyCharge extends PaymentMessageHelper(
    MiscPenaltyCharge.value,
    "chargeType.miscPenaltyCharge",
    None,
    None
  )

  object VATMiscellaneousPenLPI extends PaymentMessageHelper(
    VATMiscellaneousPenaltyLPI.value,
    "chargeType.vatMiscellaneousPenaltyLPITitle",
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
    Some("chargeType.for"),
    Some("chargeType.for")
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
    Some("chargeType.forThePeriod"),
    Some("chargeType.forThePeriod")
  )

  object VatAAQuarterlyInstalments extends PaymentMessageHelper(
    AAQuarterlyInstalments.value,
    "chargeType.VatAnnualAccountQuarterlyInstalmentsTitle",
    Some("chargeType.forThePeriod"),
    Some("chargeType.forThePeriod")
  )

  object VatAAReturnDebitCharge extends PaymentMessageHelper(
    AAReturnDebitCharge.value,
    "chargeType.VatAnnualAccountReturnDebitChargeTitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VatAAReturnCreditCharge extends PaymentMessageHelper(
    AAReturnCreditCharge.value,
    "chargeType.VatAnnualAccountReturnCreditChargeTitle",
    Some("chargeType.forThePeriod"),
    Some("chargeType.forThePeriod")
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
    Some("chargeType.vatSecurityDepositRequestDescription.agent")
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

  object VatPALPI extends PaymentMessageHelper(
    VatPALPICharge.value,
    "chargeType.vatPALPITitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object CivilEvasionPenalty extends PaymentMessageHelper(
    CivilEvasionPenaltyCharge.value,
    "chargeType.civilEvasionPenaltyTitle",
    Some("chargeType.civilEvasionPenaltyDescription"),
    Some("chargeType.civilEvasionPenaltyDescription")
  )

  object VATCivilEvasionPenaltyLPI extends PaymentMessageHelper(
    VatCivilEvasionPenaltyLPI.value,
    "chargeType.vatCivilEvasionPenaltyLPITitle",
    None,
    None
  )

  object VatInaccuraciesInECSales extends PaymentMessageHelper(
    VatInaccuraciesInECSalesCharge.value,
    "chargeType.vatInaccuraciesInECSalesTitle",
    Some("chargeType.vatInaccuraciesInECSalesDescription"),
    Some("chargeType.vatInaccuraciesInECSalesDescription.agent")
  )

  object VatFailureToSubmitECSales extends PaymentMessageHelper(
    VatFailureToSubmitECSalesCharge.value,
    "chargeType.vatFailureToSubmitECSalesTitle",
    Some("chargeType.vatFailureToSubmitECSalesDescription"),
    Some("chargeType.vatFailureToSubmitECSalesDescription.agent")
  )

  object VatFailureToSubmitECSalesLPI extends PaymentMessageHelper(
    VatFailureToSubmitECSalesChargeLPI.value,
    "chargeType.vatFailureToSubmitECSalesLPITitle",
    None,
    None
  )

  object FtnEachPartner extends PaymentMessageHelper(
    FtnEachPartnerCharge.value,
    "chargeType.ftnEachPartnerTitle",
    Some("chargeType.ftnEachPartnerDescription"),
    Some("chargeType.ftnEachPartnerDescription.agent")
  )

  object FtnEachPartnerLPI extends PaymentMessageHelper(
    FtnEachPartnerChargeLPI.value,
    "chargeType.ftnEachPartnerLPITitle",
    None,
    None
  )

  object VatOAInaccuracies2009 extends PaymentMessageHelper(
    VatOAInaccuraciesFrom2009.value,
    "chargeType.vatOAInaccuraciesFrom2009Title",
    Some("chargeType.vatOAInaccuraciesFrom2009Description"),
    Some("chargeType.vatOAInaccuraciesFrom2009Description.agent")
  )

  object VatOAInaccuracies2009LPI extends PaymentMessageHelper(
    VatOAInaccuraciesFrom2009LPI.value,
    "chargeType.vatOAInaccuraciesFrom2009LPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VatInaccuracyAssessmentsPenCharge extends PaymentMessageHelper(
    InaccuraciesAssessmentsPenCharge.value,
    "chargeType.vatInaccuracyAssessmentsPenChargeTitle",
    Some("chargeType.vatInaccuracyAssessmentsPenChargeDescription"),
    Some("chargeType.vatInaccuracyAssessmentsPenChargeDescription.agent")
  )

  object VatMpPre2009Charge extends PaymentMessageHelper(
    MpPre2009Charge.value,
    "chargeType.vatMpPre2009ChargeTitle",
    Some("chargeType.vatMpPre2009ChargeDescription"),
    Some("chargeType.vatMpPre2009ChargeDescription.agent")
  )

  object VatMpRepeatedPre2009Charge extends PaymentMessageHelper(
    MpRepeatedPre2009Charge.value,
    "chargeType.vatMpRepeatedPre2009ChargeTitle",
    Some("chargeType.vatMpRepeatedPre2009ChargeDescription"),
    Some("chargeType.vatMpRepeatedPre2009ChargeDescription.agent")
  )

  object VatInaccuraciesReturnReplacedCharge extends PaymentMessageHelper(
    InaccuraciesReturnReplacedCharge.value,
    "chargeType.vatInaccuraciesReturnReplacedChargeTitle",
    Some("chargeType.vatInaccuraciesReturnReplacedChargeDescription"),
    Some("chargeType.vatInaccuraciesReturnReplacedChargeDescription.agent")
  )

  object VatWrongDoingPenaltyCharge extends PaymentMessageHelper(
    WrongDoingPenaltyCharge.value,
    "chargeType.vatWrongDoingPenaltyChargeTitle",
    Some("chargeType.vatWrongDoingPenaltyChargeDescription"),
    Some("chargeType.vatWrongDoingPenaltyChargeDescription.agent")
  )

  object VATWrongDoingPenaltyLPI extends PaymentMessageHelper(
    VatWrongDoingPenaltyLPI.value,
    "chargeType.vatWrongDoingPenaltyLPITitle",
    None,
    None
  )

  object VatECDefaultInterest extends PaymentMessageHelper(
    VatECDefaultInterestCharge.value,
    "chargeType.vatErrorCorrectionNoticeDefaultInterestTitle",
    Some("chargeType.vatErrorCorrectionNoticeDefaultInterestDescription"),
    Some("chargeType.vatErrorCorrectionNoticeDefaultInterestDescription")
  )

  object VATErrorCorrectionLPI extends PaymentMessageHelper(
    VatErrorCorrectionLPI.value,
    "chargeType.vatErrorCorrectionLPITitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATErrorCorrection1stLPP extends PaymentMessageHelper(
    VatErrorCorrection1stLPP.value,
    "chargeType.vatErrorCorrection1stLPPTitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATErrorCorrection2ndLPP extends PaymentMessageHelper(
    VatErrorCorrection2ndLPP.value,
    "chargeType.vatErrorCorrection2ndLPPTitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATErrorCorrection1stLPPLPI extends PaymentMessageHelper(
    VatErrorCorrection1stLPPLPI.value,
    "chargeType.vatErrorCorrection1stLPPLPITitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATErrorCorrection2ndLPPLPI extends PaymentMessageHelper(
    VatErrorCorrection2ndLPPLPI.value,
    "chargeType.vatErrorCorrection2ndLPPLPITitle",
    Some("chargeType.for"),
    Some("chargeType.for")
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
    Some("chargeType.vatCarterPenaltyChargeDescription.agent")
  )

  object VATCarterPenaltyLPI extends PaymentMessageHelper(
    VatCarterPenaltyLPI.value,
    "chargeType.vatCarterPenaltyLPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VatFailureToNotifyRCSL extends PaymentMessageHelper(
    FailureToNotifyRCSLCharge.value,
    "chargeType.vatFailureToNotifyRCSLTitle",
    Some("chargeType.vatFailureToNotifyRCSLDescription"),
    Some("chargeType.vatFailureToNotifyRCSLDescription.agent")
  )

  object VatFailureToSubmitRCSL extends PaymentMessageHelper(
    FailureToSubmitRCSLCharge.value,
    "chargeType.vatFailureToSubmitRCSLTitle",
    Some("chargeType.vatFailureToSubmitRCSLDescription"),
    Some("chargeType.vatFailureToSubmitRCSLDescription.agent")
  )

  object VATFailureToSubmitRCSLLPI extends PaymentMessageHelper(
    VatFailureToSubmitRCSLLPI.value,
    "chargeType.vatFailureToSubmitRCSLLPITitle",
    None,
    None
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
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object UnallocatedPaymentCharge extends PaymentMessageHelper(
    UnallocatedPayment.value,
    "chargeType.unallocatedPaymentTitle",
    Some("chargeType.unallocatedPaymentDescription"),
    Some("chargeType.unallocatedPaymentDescription.agent")
  )

  object RefundsCharge extends PaymentMessageHelper(
    Refund.value,
    "chargeType.refundTitle",
    Some("chargeType.refundDescription"),
    Some("chargeType.refundDescription.agent")
  )

  object VatMigratedLiabilityCharge extends PaymentMessageHelper(
    VatMigratedLiability.value,
    "chargeType.migratedLiabilityTitle",
    None,
    None
  )

  object VatMigratedCreditCharge extends PaymentMessageHelper(
    VatMigratedCredit.value,
    "chargeType.migratedCreditTitle",
    Some("chargeType.migratedCreditDescription"),
    Some("chargeType.migratedCreditDescription")
  )

  object VatDeferralPenaltyCharge extends PaymentMessageHelper(
    VatDeferralPenalty.value,
    "chargeType.vatDeferralPenaltyTitle",
    Some("chargeType.vatDeferralPenaltyDescription"),
    Some("chargeType.vatDeferralPenaltyDescription")
  )

  object VATPOAInstalmentCharge extends PaymentMessageHelper(
    PaymentOnAccountInstalments.value,
    "chargeType.POAInstalmentTitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATPOAReturnDebitCharge extends PaymentMessageHelper(
    PaymentOnAccountReturnDebitCharge.value,
    "chargeType.POAReturnDebitChargeTitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATPOAReturnCreditCharge extends PaymentMessageHelper(
    PaymentOnAccountReturnCreditCharge.value,
    "chargeType.POAReturnCreditChargeTitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATReturnPOALPI extends PaymentMessageHelper(
    VatReturnPOALPI.value,
    "chargeType.VatReturnPOALPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATPOAReturn1stLPP extends PaymentMessageHelper(
    VatPOAReturn1stLPP.value,
    "chargeType.VatPOAReturn1stLPPTitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATPOAReturn2ndLPP extends PaymentMessageHelper(
    VatPOAReturn2ndLPP.value,
    "chargeType.VatPOAReturn2ndLPPTitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATReturnPOA1stLPPLPI extends PaymentMessageHelper(
    VatReturnPOA1stLPPLPI.value,
    "chargeType.VatReturnPOA1stLPPLPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATReturnPOA2ndLPPLPI extends PaymentMessageHelper(
    VatReturnPOA2ndLPPLPI.value,
    "chargeType.VatReturnPOA2ndLPPLPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATReturnPOARPI extends PaymentMessageHelper(
    VatReturnPOARPI.value,
    "chargeType.VatReturnPOARPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATReturn1stLPP extends PaymentMessageHelper(
    VatReturn1stLPP.value,
    "chargeType.VATReturn1stLPPTitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATFtnRCSLChargeLPI extends PaymentMessageHelper(
    FtnRCSLChargeLPI.value,
    "chargeType.ftnRCSLChargeLPITitle",
    None,
    None
  )

  object VATInaccurInECSalesChargeLPI extends PaymentMessageHelper(
    InaccurInECSalesChargeLPI.value,
    "chargeType.inaccurInECSalesChargeLPITitle",
    None,
    None
  )

  object VATReturnLPI extends PaymentMessageHelper(
    VatReturnLPI.value,
    "chargeType.vatReturnLPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATReturn1stLPPLPI extends PaymentMessageHelper(
    VatReturn1stLPPLPI.value,
    "chargeType.vatReturn1stLPPLPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATReturn2ndLPPLPI extends PaymentMessageHelper(
    VatReturn2ndLPPLPI.value,
    "chargeType.vatReturn2ndLPPLPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATCA1stLPP extends PaymentMessageHelper(
    VatCentralAssessment1stLPP.value,
    "chargeType.vatVATCA1stLPPTitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATCA2ndLPP extends PaymentMessageHelper(
    VatCentralAssessment2ndLPP.value,
    "chargeType.vatVATCA2ndLPPTitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATCentralAssessmentLPI extends PaymentMessageHelper(
    VatCentralAssessmentLPI.value,
    "chargeType.vatCentralAssessmentLPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATCA1stLPPLPI extends PaymentMessageHelper(
    VatCA1stLPPLPI.value,
    "chargeType.VATCA1stLPPLPPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATCA2ndLPPLPI extends PaymentMessageHelper(
    VatCA2ndLPPLPI.value,
    "chargeType.VATCA2ndLPPLPPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATOfficersAssessmentLPI extends PaymentMessageHelper(
    VatOfficersAssessmentLPI.value,
    "chargeType.VATOfficersAssessmentLPITitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATOfficersAssessmentRPI extends PaymentMessageHelper(
    VatOfficersAssessmentRPI.value,
    "chargeType.VATOfficersAssessmentRPITitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATOA1stLPP extends PaymentMessageHelper(
    VatOfficersAssessment1stLPP.value,
    "chargeType.VATOA1stLPPTitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATOA2ndLPP extends PaymentMessageHelper(
    VatOfficersAssessment2ndLPP.value,
    "chargeType.VATOA2ndLPPTitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATOA1stLPPLPI extends PaymentMessageHelper(
    VatOA1stLPPLPI.value,
    "chargeType.VATOA1stLPPLPITitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATOA2ndLPPLPI extends PaymentMessageHelper(
    VatOA2ndLPPLPI.value,
    "chargeType.VATOA2ndLPPLPITitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATPA1stLPPLPI extends PaymentMessageHelper(
    VatPA1stLPPLPI.value,
    "chargeType.VATPA1stLPPLPITitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATAA1stLPPLPI extends PaymentMessageHelper(
    VatAA1stLPPLPI.value,
    "chargeType.VATAA1stLPPLPITitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATAA2ndLPPLPI extends PaymentMessageHelper(
    VatAA2ndLPPLPI.value,
    "chargeType.VATAA2ndLPPLPITitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATPA2ndLPPLPI extends PaymentMessageHelper(
    VatPA2ndLPPLPI.value,
    "chargeType.VATPA2ndLPPLPITitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATPA1stLPP extends PaymentMessageHelper(
    VatPA1stLPP.value,
    "chargeType.VATPA1stLPPTitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATPA2ndLPP extends PaymentMessageHelper(
    VatPA2ndLPP.value,
    "chargeType.VATPA2ndLPPTitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATAA1stLPP extends PaymentMessageHelper(
    VatAA1stLPP.value,
    "chargeType.VATAA1stLPPTitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATAA2ndLPP extends PaymentMessageHelper(
    VatAA2ndLPP.value,
    "chargeType.VATAA2ndLPPTitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATAdditionalAssessmentLPI extends PaymentMessageHelper(
    VatAdditionalAssessmentLPI.value,
    "chargeType.VATAdditionalAssessmentLPITitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATLateSubPen extends PaymentMessageHelper(
    VatLateSubmissionPen.value,
    "chargeType.VATLateSubPenTitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATLSPInterest extends PaymentMessageHelper(
    VatLspInterest.value,
    "chargeType.VATLSPIntTitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATReturnAA1stLPPLPI extends PaymentMessageHelper(
    VatReturnAA1stLPPLPI.value,
    "chargeType.VATReturnAA1stLPPLPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATReturnAA2ndLPPLPI extends PaymentMessageHelper(
    VatReturnAA2ndLPPLPI.value,
    "chargeType.VATReturnAA2ndLPPLPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATManualLPP extends PaymentMessageHelper(
    VatManualLPP.value,
    "chargeType.latePaymentPenalty",
    None,
    None
  )

  object VATManualLPPLPI extends PaymentMessageHelper(
    VatManualLPPLPI.value,
    "chargeType.VATManualLPPLPITitle",
    None,
    None
  )

  object VATAAQuarterlyInstalLPI  extends PaymentMessageHelper(
    VatAAQuarterlyInstalLPI.value,
    "chargeType.VATAAQuarterlyInstalLPI",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATAAMonthlyInstalLPI extends PaymentMessageHelper(
    VatAAMonthlyInstalLPI.value,
    "chargeType.VATAAMonthlyInstalLPI",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATAAReturnCharge1stLPP extends PaymentMessageHelper(
    VatAAReturnCharge1stLPP.value,
    "chargeType.VATAAReturnCharge1stLPP",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATAAReturnCharge2ndLPP extends PaymentMessageHelper(
    VatAAReturnCharge2ndLPP.value,
    "chargeType.VATAAReturnCharge2ndLPP",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATReturn2ndLPP extends PaymentMessageHelper(
    VatReturn2ndLPP.value,
    "chargeType.secondLatePaymentPenalty",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATPOAInstalLPI extends PaymentMessageHelper(
    VatPOAInstalmentLPI.value,
    "chargeType.POAInstalLPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATAAReturnLPI extends PaymentMessageHelper(
    VatAAReturnChargeLPI.value,
    "chargeType.VATReturnAALPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )
  object Overpayments1stLPPLPI extends PaymentMessageHelper(
    name = VatOverpayments1stLPPLPI.value,
    title = "chargeType.Overpayments1stLPPLPI",
    principalUserDescription = Some("chargeType.forPeriod"),
    agentDescription = Some("chargeType.forPeriod")
  )
  object Overpayments2ndLPPLPI extends PaymentMessageHelper(
    name = VatOverpayments2ndLPPLPI.value,
    title = "chargeType.Overpayments2ndLPPLPI",
    principalUserDescription = Some("chargeType.forPeriod"),
    agentDescription = Some("chargeType.forPeriod")
  )

  object VATOverpaymentForRPI extends PaymentMessageHelper(
    VatOverpaymentForRPI.value,
    "chargeType.VATOverpaymentForRPITitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VATAAReturnRPI extends PaymentMessageHelper(
    VatAAReturnChargeRPI.value,
    "chargeType.VATReturnAARPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VatReturnForRPI extends PaymentMessageHelper(
    VatReturnRPI.value,
    "chargeType.VATReturnForRPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )
  object VatForManualLPPRPI extends PaymentMessageHelper(
    VatManualLPPRPI.value,
    "chargeType.VatManualLPPRPITitle",
    None,
    None
  )

  object VatReturnForLPP1RPI extends PaymentMessageHelper(
    VatReturnLPP1RPI.value,
    "chargeType.VatReturn1stLPPRPITitle",
    None,
    None
  )

  object VATInaccuracyAssessPenLPI extends PaymentMessageHelper(
    VatInaccuracyAssessPenLPI.value,
    "chargeType.VATInaccuracyAssessPenLPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATErrorCorrectionRPI extends PaymentMessageHelper(
    VatErrorCorrectionRPI.value,
    "chargeType.vatErrorCorrectionRPITitle",
    Some("chargeType.for"),
    Some("chargeType.for")
  )

  object VatReturn2ndLPPRPI extends PaymentMessageHelper (
    models.payments.VatReturn2ndLPPRPI.value,
    "chargeType.vatReturn2ndLPPRPI",
    None,
    None
  )
  object VatReturnPOA1stLPPRPI extends PaymentMessageHelper (
    models.payments.VatReturnPOA1stLPPRPI.value,
    "chargeType.vatReturnPOA1stLPPRPI",
    None,
    None
  )
  object VatReturnPOA2ndLPPRPI extends PaymentMessageHelper (
    models.payments.VatReturnPOA2ndLPPRPI.value,
    "chargeType.vatReturnPOA2ndLPPRPI",
    None,
    None
  )

  object VatReturnAA1stLPPRPI extends PaymentMessageHelper (
    models.payments.VatReturnAA1stLPPRPI.value,
    "chargeType.vatReturnAA1stLPPRPI",
    None,
    None
  )
  object VatReturnAA2ndLPPRPI extends PaymentMessageHelper (
    models.payments.VatReturnAA2ndLPPRPI.value,
    "chargeType.vatReturnAA2ndLPPRPI",
    None,
    None
  )

  object VATInaccRtnReplacedLPI extends PaymentMessageHelper(
    VatInaccRtnReplacedLPI.value,
    "chargeType.vatInaccRtnReplacedLPITitle",
    Some("chargeType.forPeriod"),
    Some("chargeType.forPeriod")
  )

  object VATProtectiveAssessRPI extends PaymentMessageHelper(
    VatProtectiveAssessRPI.value,
    "chargeType.vatProtectiveAssessRPITitle",
    None,
    None
  )

  object VATAdditionalAssessRPI extends PaymentMessageHelper(
    VatAdditionalAssessRPI.value,
    "chargeType.vatAdditionalAssessRPITitle",
    None,
    None
  )

  object VATCentralAssessRPI extends PaymentMessageHelper(
    VatCentralAssessRPI.value,
    "chargeType.vatCentralAssessRPITitle",
    None,
    None
  )

  object VATCa1stLppRPI extends PaymentMessageHelper(
    VatCa1stLppRPI.value,
    "chargeType.vatCa1stLppRPITitle",
    None,
    None
  )

  object VATErrorCorrection2ndLppRPI extends PaymentMessageHelper(
    VatErrorCorrection2ndLppRPI.value,
    "chargeType.vatErrorCorrection2ndLppRPITitle",
    None,
    None
  )

object VatRPIRecovery extends PaymentMessageHelper(
  models.payments.VatRPIRecovery.value,
  "chargeType.vatRPIRecovery",
  None,
  None
)
object VatManualRPI extends PaymentMessageHelper(
  models.payments.VatManualRPI.value,
  "chargeType.vatManualRPI",
  None,
  None
)

  object VATErrorCorrect1stLppRPI extends PaymentMessageHelper(
    VatErrorCorrect1stLppRPI.value,
    "chargeType.vatErrorCorrect1stLppRPITitle",
    None,
    None
  )

  object VATLspRepaymentInterest extends PaymentMessageHelper(
    VatLspRepaymentInterest.value,
    "chargeType.vatLspRepaymentInterestTitle",
    None,
    None
  )

  object VATCa2ndLppRPI extends PaymentMessageHelper(
    VatCa2ndLppRPI.value,
    "chargeType.vatCa2ndLppRPITitle",
    None,
    None
  )

  object VatForOA1stLPPRPI extends PaymentMessageHelper(
    VatOA1stLPPRPI.value,
    "chargeType.vatOA1stLPPRPITitle",
    None,
    None
  )

  object VatForOA2ndLPPRPI extends PaymentMessageHelper(
    VatOA2ndLPPRPI.value,
    "chargeType.vatOA2ndLPPRPITitle",
    None,
    None
  )

  object VatForCarterPenRPI extends PaymentMessageHelper(
    VatCarterPenRPI.value,
    "chargeType.VatCarterPenRPITitle",
    None,
    None
  )

  object VATAA1stLppRPI extends PaymentMessageHelper(
    VatAA1stLppRPI.value,
    "chargeType.VatAA1stLppRPITitle",
    None,
    None
  )

  object VATAA2ndLppRPI extends PaymentMessageHelper(
    VatAA2ndLppRPI.value,
    "chargeType.VatAA2ndLppRPITitle",
    None,
    None
  )

  object VATPA1stLppRPI extends PaymentMessageHelper(
    VatPA1stLppRPI.value,
    "chargeType.VatPA1stLppRPITitle",
    None,
    None
  )

  object VATPA2ndLppRPI extends PaymentMessageHelper(
    VatPA2ndLppRPI.value,
    "chargeType.VatPA2ndLppRPITitle",
    None,
    None
  )

  val values: Seq[PaymentMessageHelper] = Seq(
    OverpaymentforTax,
    UnrepayableOverpayment,
    RepaymentSupplementRecovery,
    IndirectTaxRevenueRecovery,
    OverpaymentForTaxRPI,
    Overpayments1stLPPRPI,
    Overpayments2ndLPPRPI,
    DefaultInterestDebit,
    DefaultInterestCredit,
    FurtherInterestDebit,
    FurtherInterestCredit,
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
    VatFtnMatPost2010ChargeLPI,
    VatMiscPenaltyCharge,
    VATMiscellaneousPenLPI,
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
    VATCivilEvasionPenaltyLPI,
    VatInaccuraciesInECSales,
    VatFailureToSubmitECSales,
    VatFailureToSubmitECSalesLPI,
    VATFailureToSubmitRCSLLPI,
    FtnEachPartner,
    FtnEachPartnerLPI,
    VatOAInaccuracies2009,
    VatOAInaccuracies2009LPI,
    VatInaccuracyAssessmentsPenCharge,
    VatMpPre2009Charge,
    VatMpRepeatedPre2009Charge,
    VatInaccuraciesReturnReplacedCharge,
    VatWrongDoingPenaltyCharge,
    VATWrongDoingPenaltyLPI,
    VatPADefaultInterest,
    VatPALPI,
    VatStatutoryInterestCharge,
    VatECDefaultInterest,
    VATErrorCorrectionLPI,
    VATErrorCorrection1stLPP,
    VATErrorCorrection2ndLPP,
    VATErrorCorrection1stLPPLPI,
    VATErrorCorrection2ndLPPLPI,
    VATErrorCorrectionRPI,
    VatPaFurtherInterest,
    VatCarterPenaltyCharge,
    VATCarterPenaltyLPI,
    VatFailureToNotifyRCSL,
    VatFailureToSubmitRCSL,
    VatCreditReturnOffsetCharge,
    ProtectiveAssessmentCharge,
    UnallocatedPaymentCharge,
    RefundsCharge,
    VatMigratedLiabilityCharge,
    VatMigratedCreditCharge,
    VatDeferralPenaltyCharge,
    VATPOAInstalmentCharge,
    VATPOAReturnDebitCharge,
    VATPOAReturnCreditCharge,
    VATReturn1stLPP,
    VATReturnLPI,
    VATReturn1stLPPLPI,
    VATReturn2ndLPPLPI,
    VATCentralAssessmentLPI,
    VATCA1stLPPLPI,
    VATCA2ndLPPLPI,
    VATOfficersAssessmentLPI,
    VATOA1stLPPLPI,
    VATOA2ndLPPLPI,
    VATPA1stLPPLPI,
    VATPA2ndLPPLPI,
    VATPA1stLPP,
    VATPA2ndLPP,
    VATAA1stLPP,
    VATAA2ndLPP,
    VATAdditionalAssessmentLPI,
    VATAA1stLPPLPI,
    VATAA2ndLPPLPI,
    VATLateSubPen,
    VATLSPInterest,
    VATReturnAA1stLPPLPI,
    VATReturnAA2ndLPPLPI,
    VATManualLPP,
    VATManualLPPLPI,
    VATAAQuarterlyInstalLPI,
    VATAAMonthlyInstalLPI,
    VATAAReturnCharge1stLPP,
    VATAAReturnCharge2ndLPP,
    VATReturn2ndLPP,
    VATAAReturnLPI,
    VATPOAInstalLPI,
    VATReturnPOALPI,
    VATPOAReturn1stLPP,
    VATPOAReturn2ndLPP,
    VATReturnPOA1stLPPLPI,
    VATReturnPOA2ndLPPLPI,
    VATReturnPOARPI,
    VATCA1stLPP,
    VATCA2ndLPP,
    VATOA1stLPP,
    VATOA2ndLPP,
    Overpayments2ndLPP,
    Overpayments1stLPP,
    Overpayments1stLPPLPI,
    VATOverpaymentForRPI,
    OverpaymentforTaxLPI,
    Overpayments2ndLPPLPI,
    VATAAReturnRPI,
    VatReturnForRPI,
    VATInaccuracyAssessPenLPI,
    VATOfficersAssessmentRPI,
    VatForManualLPPRPI,
    VatReturnForLPP1RPI,
    VATInaccurInECSalesChargeLPI,
    VATFtnRCSLChargeLPI,
    VatReturn2ndLPPRPI,
    VatReturnPOA1stLPPRPI,
    VatReturnPOA2ndLPPRPI,
    VatReturnAA2ndLPPRPI,
    VatReturnAA1stLPPRPI,
    VATInaccRtnReplacedLPI,
    VATProtectiveAssessRPI,
    VATAdditionalAssessRPI,
    VATCentralAssessRPI,
    VATCa1stLppRPI,
    VATErrorCorrection2ndLppRPI,
    VatRPIRecovery,
    VatManualRPI,
    VATErrorCorrect1stLppRPI,
    VATLspRepaymentInterest,
    VATCa2ndLppRPI,
    VatForOA1stLPPRPI,
    VatForOA2ndLPPRPI,
    VatForCarterPenRPI,
    VATAA1stLppRPI,
    VATAA2ndLppRPI,
    VATPA1stLppRPI,
    VATPA2ndLppRPI
  )

  private def getFullDescription(descriptionMessageKey: String, from: Option[LocalDate], to: Option[LocalDate])
                                (implicit messages: Messages): String =
    (from, to) match {
      case (Some(fromDate), Some(toDate)) =>
        messages(descriptionMessageKey, displayDateRange(fromDate, toDate, useShortDayFormat = true))
      case _ =>
        messages(descriptionMessageKey)
    }

  def getCorrectDescription(principalMessageKey: String, agentMessageKey: String, from: Option[LocalDate],
                            to: Option[LocalDate], userIsAgent: Boolean)
                           (implicit messages: Messages): String =
    if(userIsAgent) {
      getFullDescription(agentMessageKey, from, to)
    } else {
      getFullDescription(principalMessageKey, from, to)
    }

  def getChargeType(lookupName: String): PaymentMessageHelper =
    values.find(_.name == lookupName).getOrElse(throw new IllegalArgumentException(s"Invalid charge type: $lookupName"))
}
