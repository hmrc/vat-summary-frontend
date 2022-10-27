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

  object InsolventError {
    val title: String = "Sorry, you cannot access this service - Manage your VAT account - GOV.UK"
    val heading: String = "Sorry, you cannot access this service"
    val message: String = "Your business has been declared insolvent."
    val buttonText: String = "Go to your business tax account"
    val signOutLink: String = "Sign out"
  }

  object PaymentMessages {

    private val datePeriodShort: String = "1 Jan to 1 Feb 2018"
    private val datePeriodLong: String = "1 January to 1 February 2018"

    //scalastyle:off
    def getMessagesForChargeType(chargeType: String, useLongDateFormat: Boolean = false): (String, String) = {

      val datePeriod = if(useLongDateFormat) datePeriodLong else datePeriodShort

      chargeType match {
        case UnrepayableOverpayment.name => ("Overpayment", "cannot be repaid after 4 years")
        case RepaymentSupplementRecovery.name => ("Repayment supplement recovery", "to recover the amount overpaid by HMRC")
        case IndirectTaxRevenueRecovery.name => ("Payment recovery", "to recover a payment made in error by HMRC")
        case DefaultInterest.name => ("Default interest", s"based on our assessment of tax for the period $datePeriod")
        case FurtherInterest.name => ("Further interest", s"based on our assessment of tax for the period $datePeriod")
        case VatReturnCreditCharge.name => ("Repayment from HMRC", s"for the $datePeriod return")
        case VatReturnDebitCharge.name => ("VAT", s"for period $datePeriod")
        case VatOfficerAssessmentCreditCharge.name => ("Officer’s assessment of VAT", s"for $datePeriod")
        case VatOfficerAssessmentDebitCharge.name => ("Officer’s assessment of VAT", s"for $datePeriod")
        case VatCentralAssessment.name => ("Central assessment", s"of VAT for period $datePeriod")
        case VatDebitDefaultSurcharge.name | VatCreditDefaultSurcharge.name => ("Surcharge", s"for late payment of the $datePeriod return")
        case VatErrorCorrectionDebitCharge.name => ("Error correction", s"of VAT for $datePeriod")
        case VatErrorCorrectionCreditCharge.name => ("Error correction", s"repayment from HMRC for $datePeriod")
        case VatRepaymentSupplement.name => ("Late repayment compensation from HMRC", s"we took too long to repay the $datePeriod return")
        case OADefaultInterest.name => ("VAT officer’s assessment interest", s"charged on the officer’s assessment")
        case VatBnpRegPre2010Charge.name => ("Penalty for late registration", "because you should have been registered for VAT earlier")
        case VatBnpRegPost2010Charge.name => ("Penalty for late registration", "because you should have been registered for VAT earlier")
        case VatFtnMatPre2010Charge.name => ("Failure to notify penalty", "because you did not tell us you are no longer exempt from VAT registration")
        case VatFtnMatPost2010Charge.name => ("Failure to notify penalty", "because you did not tell us you are no longer exempt from VAT registration")
        case VatMiscPenaltyCharge.name => ("VAT general penalty", "")
        case VatOfficersAssessmentFurtherInterest.name => ("VAT officer’s assessment further interest", "charged on the officer’s assessment")
        case VatAdditionalAssessment.name => ("Additional assessment", s"of VAT for $datePeriod")
        case VatAADefaultInterest.name => ("Additional assessment interest", s"charged on additional tax assessed for the period $datePeriod")
        case VatAAFurtherInterest.name => ("Additional assessment further interest", s"charged on additional tax assessed for the period $datePeriod")
        case VatAAReturnDebitCharge.name => ("Annual accounting balance", s"for period $datePeriod")
        case VatAAReturnCreditCharge.name => ("Annual accounting repayment", s"for the period $datePeriod")
        case VatAAMonthlyInstalment.name => ("Annual accounting monthly instalment", s"for the period $datePeriod")
        case VatAAQuarterlyInstalments.name => ("Annual accounting quarterly instalment", s"for the period $datePeriod")
        case VatStatutoryInterestCharge.name => ("Statutory interest", "interest paid because of an error by HMRC")
        case VatSecurityDepositRequest.name => ("Security deposit requirement", "because you have not paid VAT in your current business or a previous business")
        case VatEcNoticeFurtherInterest.name => ("Error correction further interest", "charged on assessed amount")
        case CivilEvasionPenalty.name => ("VAT civil evasion penalty", "because we have identified irregularities involving dishonesty")
        case VatInaccuraciesInECSales.name => ("Inaccuracies penalty", "because you have provided inaccurate information in your EC sales list")
        case VatFailureToSubmitECSales.name => ("EC sales list penalty", "because you have not submitted an EC sales list or you have submitted it late")
        case FtnEachPartner.name => ("Failure to notify penalty", "because you did not tell us about all the partners and changes in your partnership")
        case VatOAInaccuracies2009.name => ("Inaccuracies penalty", s"because you submitted an inaccurate document for the period $datePeriod")
        case VatInaccuracyAssessmentsPenCharge.name => ("Inaccuracies penalty", s"because you submitted an inaccurate document for the period $datePeriod")
        case VatMpPre2009Charge.name => ("Misdeclaration penalty", "because you have made an incorrect declaration")
        case VatMpRepeatedPre2009Charge.name => ("Misdeclaration repeat penalty", "because you have repeatedly made incorrect declarations")
        case VatInaccuraciesReturnReplacedCharge.name => ("Inaccuracies penalty", s"because you have submitted inaccurate information for the period $datePeriod")
        case VatWrongDoingPenaltyCharge.name => ("Wrongdoing penalty", "because you charged VAT when you should not have done")
        case VatPADefaultInterest.name => ("Protective assessment default interest", "charged on the protective assessment")
        case VatPALPI.name => ("Interest on protective assessment", s"for $datePeriod")
        case VatECDefaultInterest.name => ("Error correction default interest", "charged on assessed amount")
        case VatPaFurtherInterest.name => ("Protective assessment further interest", "due on the protective assessment as this was not paid on time")
        case VatCarterPenaltyCharge.name => ("Penalty for not filing correctly", s"because you did not use the correct digital channel for the period $datePeriod")
        case VatFailureToNotifyRCSL.name => ("Failure to notify penalty", "because you failed to notify us of the date you made a reverse charge sale or stopped making supplies")
        case VatFailureToSubmitRCSL.name => ("Reverse Charge sales list penalty", "because you have failed to submit a Reverse Charge sales list")
        case VatCreditReturnOffsetCharge.name => ("Overpayment partial refund", s"partial repayment for period $datePeriod")
        case ProtectiveAssessmentCharge.name => ("Protective assessment", s"of VAT for period $datePeriod")
        case UnallocatedPaymentCharge.name => ("Unallocated payment", "you made an overpayment which can be refunded to you or left on account")
        case RefundsCharge.name => ("Refund payment from HMRC", "as you requested a refund on an overpayment you made")
        case VatMigratedLiabilityCharge.name => ("VAT migrated liability", "")
        case VatMigratedCreditCharge.name => ("VAT migrated credit", "miscellaneous VAT credit")
        case VATPOAInstalmentCharge.name => ("Payment on account instalment", s"for period $datePeriod")
        case VATPOAReturnDebitCharge.name => ("Payment on account balance", s"for period $datePeriod")
        case VATPOAReturnCreditCharge.name => ("Payment on account repayment", s"for period $datePeriod")
        case VATReturn1stLPP.name => ("Penalty for late payment of VAT", s"for period $datePeriod")
        case VATReturnLPI.name => ("Interest on VAT", s"for period $datePeriod")
        case VATReturn1stLPPLPI.name => ("Interest on penalty", s"for period $datePeriod")
        case VATReturn2ndLPPLPI.name => ("Interest on second penalty", s"for period $datePeriod")
        case VATCentralAssessmentLPI.name => ("Interest on central assessment of VAT", s"for period $datePeriod")
        case VATCA1stLPPLPI.name =>("Interest on central assessment penalty", s"for period $datePeriod")
        case VATCA2ndLPPLPI.name =>("Interest on central assessment second penalty", s"for period $datePeriod")
        case VATOfficersAssessmentLPI.name =>("Interest on officer’s assessment of VAT", s"for $datePeriod")
        case VATOA1stLPPLPI.name =>("Interest on officer’s assessment penalty", s"for $datePeriod")
        case VATOA2ndLPPLPI.name =>("Interest on officer’s assessment second penalty", s"for $datePeriod")
        case VATPA1stLPPLPI.name =>("Interest on protective assessment penalty" ,s"for $datePeriod")
        case VATPA2ndLPPLPI.name =>("Interest on second penalty for protective assessment" ,s"for $datePeriod")
        case VATPA1stLPP.name => ("Penalty for late payment of protective assessment", s"for $datePeriod")
        case VATPA2ndLPP.name => ("Second penalty for late payment of protective assessment", s"for $datePeriod")
        case VATAA1stLPPLPI.name =>("Interest on additional assessment penalty" ,s"for $datePeriod")
        case VATAA2ndLPPLPI.name =>("Interest on additional assessment second penalty" ,s"for $datePeriod")
        case VATAA1stLPP.name => ("Penalty for late payment of additional assessment", s"for $datePeriod")
        case VATAA2ndLPP.name => ("Second penalty for late payment of additional assessment", s"for $datePeriod")
        case VATAdditionalAssessmentLPI.name => ("Interest on additional assessment", s"for $datePeriod")
        case VATLateSubPen.name => ("Late submission penalty", s"for period $datePeriod")
        case VATLSPInterest.name => ("Interest on late submission penalty", s"for period $datePeriod")
        case VATReturnAA1stLPPLPI.name =>("Interest on annual accounting balance penalty", s"for period $datePeriod")
        case VATReturnAA2ndLPPLPI.name => ("Interest on annual accounting balance second penalty", s"for period $datePeriod")
        case VATManualLPP.name => ("Late payment penalty", "")
        case VATManualLPPLPI.name => ("Interest on late payment penalty", "")
        case VATAAQuarterlyInstalLPI.name =>("Interest on annual accounting quarterly instalment", s"for period $datePeriod")
        case VATAAMonthlyInstalLPI.name =>("Interest on annual accounting monthly instalment", s"for period $datePeriod")
        case VATAAReturnCharge1stLPP.name =>("Penalty for late payment – annual accounting balance", s"for period $datePeriod")
        case VATAAReturnCharge2ndLPP.name =>("Second penalty for late payment – annual accounting balance", s"for period $datePeriod")
        case VATReturn2ndLPP.name =>("Second late payment penalty", s"for period $datePeriod")
        case _ => throw new IllegalArgumentException(s"[MessageLookup][PaymentMessages][getMessagesForChargeType] Charge type not found in message lookup: $chargeType")
      }
    }
    //scalastyle:on
  }

  object paymentHistoryMessages {
    val insetText: String = "If you cannot see your all of your client’s history, " +
      "you may be able to access more through your HMRC online services for agents account. You’ll need to sign in separately."
  }
}
