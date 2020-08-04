/*
 * Copyright 2020 HM Revenue & Customs
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

sealed case class PaymentMessageHelper(name: String, title: String, description: Option[String], id: String = "")

//scalastyle:off
object PaymentMessageHelper {
  object DefaultInterest extends PaymentMessageHelper(
    VatDefaultInterest.value,
    "chargeType.defaultInterestTitle",
    Some("chargeType.defaultAndFurtherInterestDescription")
  )

  object FurtherInterest extends PaymentMessageHelper(
    VatFurtherInterest.value,
    "chargeType.furtherInterestTitle",
    Some("chargeType.defaultAndFurtherInterestDescription")
  )

  object VatReturnCreditCharge extends PaymentMessageHelper(
    ReturnCreditCharge.value,
    "chargeType.vatReturnCreditChargeTitle",
    Some("chargeType.vatReturnCreditChargeDescription"),
    "repayment"
  )

  object VatReturnDebitCharge extends PaymentMessageHelper(
    ReturnDebitCharge.value,
    "chargeType.vatReturnDebitChargeTitle",
    Some("chargeType.vatReturnDebitChargeDescription"))

  object VatOfficerAssessmentCreditCharge extends PaymentMessageHelper(
    OACreditCharge.value,
    "chargeType.officerAssessmentChargeTitle",
    Some("chargeType.officerAssessmentCreditChargeDescription"),
    "repayment"
  )

  object VatOfficerAssessmentDebitCharge extends PaymentMessageHelper(
    OADebitCharge.value,
    "chargeType.officerAssessmentChargeTitle",
    Some("chargeType.officerAssessmentDebitChargeDescription"))

  object VatCentralAssessment extends PaymentMessageHelper(
    CentralAssessmentCharge.value,
    "chargeType.vatCentralAssessmentTitle",
    Some("chargeType.vatCentralAssessmentDescription"))

  object VatDefaultSurcharge extends PaymentMessageHelper(
    DefaultSurcharge.value,
    "chargeType.vatDefaultSurchargeTitle",
    Some("chargeType.vatDefaultSurchargeDescription"))

  object VatErrorCorrectionDebitCharge extends PaymentMessageHelper(
    ErrorCorrectionDebitCharge.value,
    "chargeType.vatErrorCorrectionDebitChargeTitle",
    Some("chargeType.vatErrorCorrectionChargeDescription")
  )

  object VatErrorCorrectionCreditCharge extends PaymentMessageHelper(
    ErrorCorrectionCreditCharge.value,
    "chargeType.vatErrorCorrectionCreditChargeTitle",
    Some("chargeType.vatErrorCorrectionChargeDescription"),
    "repayment"
  )

  object VatRepaymentSupplement extends PaymentMessageHelper(
    RepaymentSupplement.value,
    "chargeType.vatRepaymentSupplementTitle",
    Some("chargeType.vatRepaymentSupplementDescription"),
    "repayment"
  )

  object OADefaultInterest extends PaymentMessageHelper(
    OADefaultInterestCharge.value,
    "chargeType.OADefaultInterestTitle",
    Some("chargeType.OADefaultInterestDescription")
  )

  object VatBnpRegPre2010Charge extends PaymentMessageHelper(
    BnpRegPre2010Charge.value,
    "chargeType.bnpRegPre2010ChargeTitle",
    Some("chargeType.bnpRegPre2010ChargeDescription")
  )

  object VatBnpRegPost2010Charge extends PaymentMessageHelper(
    BnpRegPost2010Charge.value,
    "chargeType.bnpRegPost2010ChargeTitle",
    Some("chargeType.bnpRegPost2010ChargeDescription")
  )

  object VatFtnMatPre2010Charge extends PaymentMessageHelper(
    FtnMatPre2010Charge.value,
    "chargeType.ftnMatPre2010ChargeTitle",
    Some("chargeType.ftnMatPre2010ChargeDescription")
  )

  object VatFtnMatPost2010Charge extends PaymentMessageHelper(
    FtnMatPost2010Charge.value,
    "chargeType.ftnMatPost2010ChargeTitle",
    Some("chargeType.ftnMatPost2010ChargeDescription")
  )

  object VatMiscPenaltyCharge extends PaymentMessageHelper(
    MiscPenaltyCharge.value,
    "chargeType.miscPenaltyCharge",
    None
  )

  object VatOfficersAssessmentFurtherInterest extends PaymentMessageHelper(
    OAFurtherInterestCharge.value,
    "chargeType.VatOfficersAssessmentFurtherInterestTitle",
    Some("chargeType.VatOfficersAssessmentFurtherInterestDescription")
  )

  object VatAdditionalAssessment extends PaymentMessageHelper(
    AACharge.value,
    "chargeType.VatAdditionalAssessmentTitle",
    Some("chargeType.VatAdditionalAssessmentDescription")
  )

  object VatAADefaultInterest extends PaymentMessageHelper(
    AAInterestCharge.value,
    "chargeType.VatAdditionalAssessmentDefaultInterestTitle",
    Some("chargeType.VatAdditionalAssessmentDefaultInterestDescription")
  )

  object VatAAFurtherInterest extends PaymentMessageHelper(
    AAFurtherInterestCharge.value,
    "chargeType.VatAdditionalAssessmentFurtherInterestTitle",
    Some("chargeType.VatAdditionalAssessmentFurtherInterestDescription")
  )

  object VatAAMonthlyInstalment extends PaymentMessageHelper(
    AAMonthlyInstalment.value,
    "chargeType.VatAnnualAccountMonthlyInstalmentTitle",
    Some("chargeType.VatAnnualAccountMonthlyInstalmentDescription")
  )

  object VatAAQuarterlyInstalments extends PaymentMessageHelper(
    AAQuarterlyInstalments.value,
    "chargeType.VatAnnualAccountQuarterlyInstalmentsTitle",
    Some("chargeType.VatAnnualAccountQuarterlyInstalmentsDescription")
  )

  object VatAAReturnDebitCharge extends PaymentMessageHelper(
    AAReturnDebitCharge.value,
    "chargeType.VatAnnualAccountReturnDebitChargeTitle",
    Some("chargeType.VatAnnualAccountReturnDebitChargeDescription")
  )

  object VatAAReturnCreditCharge extends PaymentMessageHelper(
    AAReturnCreditCharge.value,
    "chargeType.VatAnnualAccountReturnCreditChargeTitle",
    Some("chargeType.VatAnnualAccountReturnCreditChargeDescription"),
    "repayment"
  )

  object VatStatutoryInterestCharge extends PaymentMessageHelper(
    StatutoryInterestCharge.value,
    "chargeType.VatStatutoryInterestTitle",
    Some("chargeType.VatStatutoryInterestDescription"),
    "repayment"
  )

  object VatSecurityDepositRequest extends PaymentMessageHelper(
    VatSecurityDepositRequestCharge.value,
    "chargeType.vatSecurityDepositRequestTitle",
    Some("chargeType.vatSecurityDepositRequestDescription")
  )

  object VatEcNoticeFurtherInterest extends PaymentMessageHelper(
    VatECFurtherInterestCharge.value,
    "chargeType.vatEcNoticeFurtherInterestTitle",
    Some("chargeType.vatEcNoticeFurtherInterestDescription")
  )

  object VatPADefaultInterest extends PaymentMessageHelper(
    VatPADefaultInterestCharge.value,
    "chargeType.vatPADefaultInterestTitle",
    Some("chargeType.vatPADefaultInterestDescription")
  )

  object CivilEvasionPenalty extends PaymentMessageHelper(
    CivilEvasionPenaltyCharge.value,
    "chargeType.civilEvasionPenaltyTitle",
    Some("chargeType.civilEvasionPenaltyDescription")
  )

  object VatInaccuraciesInECSales extends PaymentMessageHelper(
    VatInaccuraciesInECSalesCharge.value,
    "chargeType.vatInaccuraciesInECSalesTitle",
    Some("chargeType.vatInaccuraciesInECSalesDescription")
  )

  object VatFailureToSubmitECSales extends PaymentMessageHelper(
    VatFailureToSubmitECSalesCharge.value,
    "chargeType.vatFailureToSubmitECSalesTitle",
    Some("chargeType.vatFailureToSubmitECSalesDescription")
  )

  object FtnEachPartner extends PaymentMessageHelper(
    FtnEachPartnerCharge.value,
    "chargeType.ftnEachPartnerTitle",
    Some("chargeType.ftnEachPartnerDescription")
  )

  object VatOAInaccuracies2009 extends PaymentMessageHelper(
    VatOAInaccuraciesFrom2009.value,
    "chargeType.vatOAInaccuraciesFrom2009Title",
    Some("chargeType.vatOAInaccuraciesFrom2009Description")
  )

  object VatInaccuracyAssessmentsPenCharge extends PaymentMessageHelper(
    InaccuraciesAssessmentsPenCharge.value,
    "chargeType.vatInaccuracyAssessmentsPenChargeTitle",
    Some("chargeType.vatInaccuracyAssessmentsPenChargeDescription")
  )

  object VatMpPre2009Charge extends PaymentMessageHelper(
    MpPre2009Charge.value,
    "chargeType.vatMpPre2009ChargeTitle",
    Some("chargeType.vatMpPre2009ChargeDescription")
  )

  object VatMpRepeatedPre2009Charge extends PaymentMessageHelper(
    MpRepeatedPre2009Charge.value,
    "chargeType.vatMpRepeatedPre2009ChargeTitle",
    Some("chargeType.vatMpRepeatedPre2009ChargeDescription")
  )

  object VatInaccuraciesReturnReplacedCharge extends PaymentMessageHelper(
    InaccuraciesReturnReplacedCharge.value,
    "chargeType.vatInaccuraciesReturnReplacedChargeTitle",
    Some("chargeType.vatInaccuraciesReturnReplacedChargeDescription")
  )

  object VatWrongDoingPenaltyCharge extends PaymentMessageHelper(
    WrongDoingPenaltyCharge.value,
    "chargeType.vatWrongDoingPenaltyChargeTitle",
    Some("chargeType.vatWrongDoingPenaltyChargeDescription")
  )

  object VatECDefaultInterest extends PaymentMessageHelper(
    VatECDefaultInterestCharge.value,
    "chargeType.vatErrorCorrectionNoticeDefaultInterestTitle",
    Some("chargeType.vatErrorCorrectionNoticeDefaultInterestDescription")
  )

  object VatPaFurtherInterest extends PaymentMessageHelper(
    VatPaFurtherInterestCharge.value,
    "chargeType.vatPaFurtherInterestTitle",
    Some("chargeType.vatPaFurtherInterestDescription")
  )

  object VatCarterPenaltyCharge extends PaymentMessageHelper(
    CarterPenaltyCharge.value,
    "chargeType.vatCarterPenaltyChargeTitle",
    Some("chargeType.vatCarterPenaltyChargeDescription")
  )

  object VatFailureToNotifyRCSL extends PaymentMessageHelper(
    FailureToNotifyRCSLCharge.value,
    "chargeType.vatFailureToNotifyRCSLTitle",
    Some("chargeType.vatFailureToNotifyRCSLDescription")
  )

  object VatFailureToSubmitRCSL extends PaymentMessageHelper(
    FailureToSubmitRCSLCharge.value,
    "chargeType.vatFailureToSubmitRCSLTitle",
    Some("chargeType.vatFailureToSubmitRCSLDescription")
  )

  object VatCreditReturnOffsetCharge extends PaymentMessageHelper(
    CreditReturnOffsetCharge.value,
    "chargeType.vatCreditReturnOffsetChargeTitle",
    Some("chargeType.vatCreditReturnOffsetChargeDescription"),
    "repayment"
  )

  object ProtectiveAssessmentCharge extends PaymentMessageHelper(
    VatProtectiveAssessmentCharge.value,
    "chargeType.vatProtectiveAssessmentChargeTitle",
    Some("chargeType.vatProtectiveAssessmentChargeDescription")
  )

  object UnallocatedPaymentCharge extends PaymentMessageHelper(
    UnallocatedPayment.value,
    "chargeType.unallocatedPaymentTitle",
    Some("chargeType.unallocatedPaymentDescription"),
    "unallocated"
  )

  object RefundsCharge extends PaymentMessageHelper(
    Refund.value,
    "chargeType.refundTitle",
    Some("chargeType.refundDescription"),
    "repayment"
  )

  object VATPOAInstalmentCharge extends PaymentMessageHelper(
    PaymentOnAccountInstalments.value,
    "chargeType.POAInstalmentTitle",
    Some("chargeType.POAChargeDescription")
  )

  object VATPOAReturnDebitCharge extends PaymentMessageHelper(
    PaymentOnAccountReturnDebitCharge.value,
    "chargeType.POAReturnDebitChargeTitle",
    Some("chargeType.POAChargeDescription")
  )

  object VATPOAReturnCreditCharge extends PaymentMessageHelper(
    PaymentOnAccountReturnCreditCharge.value,
    "chargeType.POAReturnCreditChargeTitle",
    Some("chargeType.POAChargeDescription"),
    "repayment"
  )

  val values: Seq[PaymentMessageHelper] = Seq(
    DefaultInterest,
    FurtherInterest,
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
