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

  object DefaultInterest extends PaymentMessageHelper(
    VatDefaultInterest.value,
    "chargeType.defaultInterestTitle",
    Some("chargeType.defaultAndFurtherInterestDescription"),
    Some("chargeType.defaultAndFurtherInterestDescription")
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

  object FtnEachPartner extends PaymentMessageHelper(
    FtnEachPartnerCharge.value,
    "chargeType.ftnEachPartnerTitle",
    Some("chargeType.ftnEachPartnerDescription"),
    Some("chargeType.ftnEachPartnerDescription.agent")
  )

  object VatOAInaccuracies2009 extends PaymentMessageHelper(
    VatOAInaccuraciesFrom2009.value,
    "chargeType.vatOAInaccuraciesFrom2009Title",
    Some("chargeType.vatOAInaccuraciesFrom2009Description"),
    Some("chargeType.vatOAInaccuraciesFrom2009Description.agent")
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
    Some("chargeType.vatCarterPenaltyChargeDescription.agent")
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

  object VATReturn1stLPP extends PaymentMessageHelper(
    VatReturn1stLPP.value,
    "chargeType.VATReturn1stLPPTitle",
    Some("chargeType.VATReturn1stLPPDescription"),
    Some("chargeType.VATReturn1stLPPDescription")
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
    Some("chargeType.VATPALPPDescription"),
    Some("chargeType.VATPALPPDescription")
  )

  object VATPA2ndLPP extends PaymentMessageHelper(
    VatPA2ndLPP.value,
    "chargeType.VATPA2ndLPPTitle",
    Some("chargeType.VATPALPPDescription"),
    Some("chargeType.VATPALPPDescription")
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
    VatPALPI,
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
    VATAA2ndLPPLPI
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
