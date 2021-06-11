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

package common

import views.templates.payments.PaymentMessageHelper._

object MessageLookup {

  object SessionTimeout {
    val title: String = "Your session has timed out"
    val instructions: String = "To view your VAT summary, you'll have to sign in using your Government Gateway ID."
  }

  object Unauthorised {
    val title: String = "Unauthorised access"
    val instructions: String = "Here are some instructions about what you should do next."
  }

  object PaymentMessages {

    private val datePeriodShort: String = "1 Jan to 1 Feb 2018"
    private val datePeriodLong: String = "1 January to 1 February 2018"

    //scalastyle:off
    def getMessagesForChargeType(chargeType: String, useLongDateFormat: Boolean = false): (String, String) = {

      val datePeriod = if(useLongDateFormat) datePeriodLong else datePeriodShort

      chargeType match {
        case RepaymentSupplementRecovery.name => ("Repayment supplement recovery", "to recover the amount you were overpaid")
        case IndirectTaxRevenueRecovery.name => ("Payment recovery", "to recover a payment made to you in error")
        case DefaultInterest.name => ("Default interest", s"based on our assessment of your tax for the period $datePeriod")
        case FurtherInterest.name => ("Further interest", s"based on our assessment of your tax for the period $datePeriod")
        case VatReturnCreditCharge.name => ("Repayment from HMRC", s"for your $datePeriod return")
        case VatReturnDebitCharge.name => ("Return", s"for the period $datePeriod")
        case VatOfficerAssessmentCreditCharge.name => ("VAT officer’s assessment", "for overpaying by this amount")
        case VatOfficerAssessmentDebitCharge.name => ("VAT officer’s assessment", "for underpaying by this amount")
        case VatCentralAssessment.name => ("Estimate", s"for your $datePeriod return")
        case VatDebitDefaultSurcharge.name | VatCreditDefaultSurcharge.name => ("Surcharge", s"for late payment of your $datePeriod return")
        case VatErrorCorrectionDebitCharge.name => ("Error correction payment", s"for correcting your $datePeriod return")
        case VatErrorCorrectionCreditCharge.name => ("Error correction repayment from HMRC", s"for correcting your $datePeriod return")
        case VatRepaymentSupplement.name => ("Late repayment compensation from HMRC", s"we took too long to repay your $datePeriod return")
        case OADefaultInterest.name => ("VAT officer’s assessment interest", s"interest charged on the officer’s assessment")
        case VatBnpRegPre2010Charge.name => ("Penalty for late registration", "because you should have been registered for VAT earlier")
        case VatBnpRegPost2010Charge.name => ("Penalty for late registration", "because you should have been registered for VAT earlier")
        case VatFtnMatPre2010Charge.name => ("Failure to notify penalty", "you did not tell us you are no longer exempt from VAT registration")
        case VatFtnMatPost2010Charge.name => ("Failure to notify penalty", "you did not tell us you are no longer exempt from VAT registration")
        case VatMiscPenaltyCharge.name => ("VAT general penalty", "")
        case VatOfficersAssessmentFurtherInterest.name => ("VAT officer’s assessment further interest", "further interest charged on the officer’s assessment")
        case VatAdditionalAssessment.name => ("Additional assessment", s"additional assessment based on further information for the period $datePeriod")
        case VatAADefaultInterest.name => ("Additional assessment interest", s"interest charged on additional tax assessed for the period $datePeriod")
        case VatAAFurtherInterest.name => ("Additional assessment further interest", s"further interest charged on additional tax assessed for the period $datePeriod")
        case VatAAReturnDebitCharge.name => ("Annual accounting balance", s"for the period $datePeriod")
        case VatAAReturnCreditCharge.name => ("Annual accounting repayment", s"for the period $datePeriod")
        case VatAAMonthlyInstalment.name => ("Annual accounting monthly instalment", s"for the period $datePeriod")
        case VatAAQuarterlyInstalments.name => ("Annual accounting quarterly instalment", s"for the period $datePeriod")
        case VatStatutoryInterestCharge.name => ("Statutory interest", "interest paid because of an error by HMRC")
        case VatSecurityDepositRequest.name => ("Security deposit requirement", "because you have not paid VAT in your current or previous business(es)")
        case VatEcNoticeFurtherInterest.name => ("Error correction further interest", "further interest charged on assessed amount")
        case CivilEvasionPenalty.name => ("VAT civil evasion penalty", "because we have identified irregularities involving dishonesty")
        case VatInaccuraciesInECSales.name => ("Inaccuracies penalty", "because you have provided inaccurate information in your EC sales list")
        case VatFailureToSubmitECSales.name => ("EC sales list penalty", "because you have not submitted an EC sales list or you have submitted it late")
        case FtnEachPartner.name => ("Failure to notify penalty", "because you did not tell us about all the partners and changes in your partnership")
        case VatOAInaccuracies2009.name => ("Inaccuracies penalty", s"because you submitted an inaccurate document for the period $datePeriod")
        case VatInaccuracyAssessmentsPenCharge.name => ("Inaccuracies penalty", s"because you submitted an inaccurate document for the period $datePeriod")
        case VatMpPre2009Charge.name => ("Misdeclaration penalty", "because you have made an incorrect declaration")
        case VatMpRepeatedPre2009Charge.name => ("Misdeclaration repeat penalty", "because you have repeatedly made incorrect declarations")
        case VatInaccuraciesReturnReplacedCharge.name => ("Inaccuracies penalty", s"this is because you have submitted inaccurate information for the period $datePeriod")
        case VatWrongDoingPenaltyCharge.name => ("Wrongdoing penalty", "because you charged VAT when you should not have done")
        case VatPADefaultInterest.name => ("Protective assessment default interest", "interest charged on the protective assessment")
        case VatECDefaultInterest.name => ("Error correction default interest", "interest charged on assessed amount")
        case VatPaFurtherInterest.name => ("Protective assessment further interest", "further interest due on the protective assessment as this was not paid on time")
        case VatCarterPenaltyCharge.name => ("Penalty for not filing correctly", s"because you did not use the correct digital channel for the period $datePeriod")
        case VatFailureToNotifyRCSL.name => ("Failure to notify penalty", "because you failed to notify us of the date you made a reverse charge sale or stopped making supplies")
        case VatFailureToSubmitRCSL.name => ("Reverse Charge sales list penalty", "because you have failed to submit a Reverse Charge sales list")
        case VatCreditReturnOffsetCharge.name => ("Overpayment partial refund", s"partial repayment for period $datePeriod")
        case ProtectiveAssessmentCharge.name => ("Protective assessment", "assessment raised to protect HMRC’s position during an appeal")
        case UnallocatedPaymentCharge.name => ("Unallocated payment", "you made an overpayment, you can have this refunded or leave it on account")
        case RefundsCharge.name => ("Refund payment from HMRC", "as you requested a refund on an overpayment you made")
        case VATPOAInstalmentCharge.name => ("Payment on account instalment", s"for the period $datePeriod")
        case VATPOAReturnDebitCharge.name => ("Payment on account balance", s"for the period $datePeriod")
        case VATPOAReturnCreditCharge.name => ("Payment on account repayment", s"for the period $datePeriod")
        case _ => throw new IllegalArgumentException(s"[MessageLookup][PaymentMessages][getMessagesForChargeType] Charge type not found in message lookup: $chargeType")
      }
    }
    //scalastyle:on
  }

  object CovidMessages {
    val heading: String = "Paying deferred VAT: the new payment scheme"
    val line1LinkText: String = "VAT deferral new payment scheme (opens in new tab)"
    val line1: String = "If you deferred paying VAT that was due between 20 March 2020 and 30 June 2020, you may be " +
      s"able to join the $line1LinkText."
    val line2: String = "Instead of paying the full amount of your deferred VAT immediately, you can pay in " +
      "smaller, interest free instalments. The number of instalments depends on when you join the scheme."
  }

  object noDDInterruptMessages {
    val backLinkText: String = "Back"
    val pageTitle: String = "You need to check your payment method - VAT - GOV.UK"
    val title: String = "You need to check your payment method"
    val detailsSummary: String = "Why am I seeing this message?"
    val detailsText: String = "You have been transferred to HMRC’s new online system for submitting your VAT returns. " +
      "If you previously paid your VAT bill by Direct Debit, your Direct Debit has been cancelled."
    val para1: String = "To continue to pay your VAT by Direct Debit, " +
      "you must set up a new Direct Debit at least 3 working days before your VAT Return payment is due. " +
      "If you do not pay your VAT Return on time, you may incur a penalty."
    val para2: String = "If you do not have time to set up a new Direct Debit before your next VAT payment is due, " +
      "or you prefer to pay using an alternative payment method, you will be able to do this in the " +
      "’payments and repayments’ section of your VAT account."
    val para3: String = "If you have already set up a payment method you can continue to your VAT account."
    val setupButtonText: String = "Set up a new Direct Debit"
    val continueButtonText: String = "Continue to your VAT account"
  }

  object existingDDInterruptMessages {
    val backLinkText: String = "Back"
    val pageTitle: String = "You need to validate your details for Direct Debit - VAT - GOV.UK"
    val title: String = "You need to validate your details for Direct Debit"
    val insetText: String = "You have been transferred to HMRC’s new online system for submitting your VAT returns. " +
      "Your Service user name and number will change. The updated details will be sent to you as a secure message."
    val para1: String = "You currently pay via Direct Debit and your Acknowledgement Screens tell you how much you need to pay and when."
    val para2: String = "To continue receiving prompts to log into your Acknowledgement Screens and review " +
      "the information you need to validate your email address."
    val validateButtonText: String = "Validate your email"
    val continueButtonText: String = "Continue to your VAT account"
  }
}
