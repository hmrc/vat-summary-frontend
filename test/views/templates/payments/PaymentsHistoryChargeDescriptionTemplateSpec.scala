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

import models.payments._
import models.viewModels.PaymentsHistoryModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.prop.TableDrivenPropertyChecks
import views.ViewBaseSpec
import views.html.templates.payments.PaymentsHistoryChargeDescription

import java.time.LocalDate

class PaymentsHistoryChargeDescriptionTemplateSpec extends ViewBaseSpec with TableDrivenPropertyChecks {

  val paymentsHistoryChargeDescription: PaymentsHistoryChargeDescription =
    injector.instanceOf[PaymentsHistoryChargeDescription]

  object Selectors {
    val chargeTitle = "span"
    val description = "p"
  }

  val exampleModel: PaymentsHistoryModel = PaymentsHistoryModel(
    clearingSAPDocument = Some("002828853334"),
    ReturnDebitCharge,
    Some(LocalDate.parse("2018-01-12")),
    Some(LocalDate.parse("2018-03-23")),
    1,
    Some(LocalDate.parse("2018-02-14"))
  )

  private val nonAgentChargeTable = Table(
    ("Charge Type", "Title", "Description"),
    (ReturnDebitCharge, "VAT", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (ReturnCreditCharge, "Repayment from HMRC", "for the 12\u00a0Jan to 23\u00a0Mar\u00a02018 return"),
    (OADebitCharge, "Officer’s assessment of VAT", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (OACreditCharge, "Officer’s assessment of VAT", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (CentralAssessmentCharge, "Central assessment of VAT", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (DebitDefaultSurcharge, "Surcharge", "for late payment of the 12\u00a0Jan to 23\u00a0Mar\u00a02018 return"),
    (CreditDefaultSurcharge, "Surcharge", "for late payment of the 12\u00a0Jan to 23\u00a0Mar\u00a02018 return"),
    (ErrorCorrectionCreditCharge, "Error correction repayment from HMRC", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (ErrorCorrectionDebitCharge, "Error correction of VAT", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (OADefaultInterestCharge, "VAT officer’s assessment interest", "charged on the officer’s assessment"),
    (OAFurtherInterestCharge, "VAT officer’s assessment further interest", "charged on the officer’s assessment"),
    (AACharge, "Additional assessment of VAT", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (CivilEvasionPenaltyCharge, "VAT civil evasion penalty", "because we have identified irregularities involving dishonesty"),
    (VatCivilEvasionPenaltyLPI, "Interest on VAT civil evasion penalty", ""),
    (VatFailureToSubmitECSalesCharge, "EC sales list penalty", "because you have not submitted an EC sales list or you have submitted it late"),
    (VatFailureToSubmitECSalesChargeLPI, "Interest on EC sales list penalty", ""),
    (AAInterestCharge, "Additional assessment interest", "charged on additional tax assessed for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (AAFurtherInterestCharge, "Additional assessment further interest", "charged on additional tax assessed for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (StatutoryInterestCharge, "Statutory interest", "interest paid because of an error by HMRC"),
    (InaccuraciesAssessmentsPenCharge, "Inaccuracies penalty", "because you submitted an inaccurate document for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatInaccuracyAssessPenLPI, "Interest on inaccuracies penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatOAInaccuraciesFrom2009LPI, "Interest on inaccuracies penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (MpPre2009Charge, "Misdeclaration penalty", "because you have made an incorrect declaration"),
    (MpRepeatedPre2009Charge, "Misdeclaration repeat penalty", "because you have repeatedly made incorrect declarations"),
    (InaccuraciesReturnReplacedCharge, "Inaccuracies penalty", "because you have submitted inaccurate information for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (WrongDoingPenaltyCharge, "Wrongdoing penalty", "because you charged VAT when you should not have done"),
    (VatWrongDoingPenaltyLPI, "Interest on wrongdoing penalty", ""),
    (CarterPenaltyCharge, "Penalty for not filing correctly", "because you did not use the correct digital channel for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (FailureToSubmitRCSLCharge, "Reverse Charge sales list penalty", "because you have failed to submit a Reverse Charge sales list"),
    (VatFailureToSubmitRCSLLPI, "Interest on Reverse Charge sales list penalty", ""),
    (CreditReturnOffsetCharge, "Overpayment partial refund", "partial repayment for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (UnallocatedPayment, "Unallocated payment", "you made an overpayment which can be refunded to you or left on account"),
    (Refund, "Refund payment from HMRC", "as you requested a refund on an overpayment you made"),
    (VatMigratedCredit, "VAT migrated credit", "miscellaneous VAT credit"),
    (VATMiscellaneousPenaltyLPI, "Interest on VAT general penalty", ""),
    (VatMigratedLiability, "VAT migrated liability", ""),
    (PaymentOnAccountInstalments, "Payment on account instalment", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (PaymentOnAccountReturnDebitCharge, "Payment on account balance", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (PaymentOnAccountReturnCreditCharge, "Payment on account repayment", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (AAMonthlyInstalment, "Annual accounting monthly instalment", "for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (AAQuarterlyInstalments, "Annual accounting quarterly instalment", "for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (AAReturnDebitCharge, "Annual accounting balance", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (AAReturnCreditCharge, "Annual accounting repayment", "for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatOverpaymentForTaxRPI, "Repayment interest on VAT", "for 12 Jan to 23 Mar 2018"),
    (VatOverpayments1stLPPRPI, "Repayment interest on penalty", "for 12 Jan to 23 Mar 2018"),
    (VatOverpayments2ndLPPRPI, "Repayment interest on second penalty", "for 12 Jan to 23 Mar 2018"),
    (VatReturnRPI, "Repayment interest on payment on account repayment", "for period 12 Jan to 23 Mar 2018"),
    (VatOfficersAssessmentRPI, "Repayment interest on officer’s assessment of VAT", "for 12 Jan to 23 Mar 2018"),
    (VatUnrepayableOverpayment, "Overpayment", "cannot be repaid after 4 years"),
    (VatOverpayments2ndLPP, "Second late payment penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatOverpayments1stLPP, "Late payment penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturn1stLPP, "Late payment penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturnLPI, "Interest on VAT", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturn1stLPPLPI, "Interest on penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturn2ndLPPLPI, "Interest on second penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatCentralAssessmentLPI, "Interest on central assessment of VAT", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatCentralAssessment1stLPP, "Late payment penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatCentralAssessment2ndLPP, "Second late payment penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatCA1stLPPLPI, "Interest on central assessment penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatCA2ndLPPLPI, "Interest on central assessment second penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatOfficersAssessmentLPI, "Interest on officer’s assessment of VAT", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatOfficersAssessment1stLPP, "Late payment penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatOfficersAssessment2ndLPP, "Second late payment penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatOA1stLPPLPI, "Interest on officer’s assessment penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatOA2ndLPPLPI, "Interest on officer’s assessment second penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAA1stLPP, "Late payment penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAA1stLPPLPI, "Interest on additional assessment penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAA2ndLPPLPI, "Interest on additional assessment second penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAA2ndLPP, "Second late payment penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAdditionalAssessmentLPI, "Interest on additional assessment", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatPALPICharge, "Interest on protective assessment", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatPA1stLPP, "Late payment penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatPA2ndLPP, "Second late payment penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatLateSubmissionPen, "Late submission penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatLspInterest, "Interest on late submission penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturnAA1stLPPLPI, "Interest on annual accounting balance penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturnAA2ndLPPLPI, "Interest on annual accounting balance second penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatPOAInstalmentLPI, "Interest on payment on account instalment", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAAReturnChargeLPI, "Interest on annual accounting balance", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatManualLPP, "Late payment penalty", ""),
    (VatManualLPPLPI, "Interest on late payment penalty", ""),
    (VatManualLPPRPI, "Repayment interest on late payment penalty", ""),
    (VatReturnLPP1RPI, "Repayment interest on penalty for late payment of VAT", ""),
    (VatAAQuarterlyInstalLPI, "Interest on annual accounting quarterly instalment", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAAMonthlyInstalLPI, "Interest on annual accounting monthly instalment", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAAReturnCharge1stLPP, "Late payment penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAAReturnCharge2ndLPP, "Second late payment penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturn2ndLPP, "Second late payment penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatErrorCorrectionLPI, "Interest on error correction", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatErrorCorrection1stLPP, "Late payment penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatErrorCorrection2ndLPP, "Second late payment penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatErrorCorrection1stLPPLPI, "Interest on error correction penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatErrorCorrection2ndLPPLPI, "Interest on error correction second penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturnPOALPI, "Interest on payment on account", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatPOAReturn1stLPP, "Late payment penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatPOAReturn2ndLPP, "Second late payment penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturnPOA1stLPPLPI, "Interest on payment on account penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturnPOA2ndLPPLPI, "Interest on payment on account second penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (FtnMatPost2010Charge, "Failure to notify penalty", "because you did not tell us you are no longer exempt from VAT registration"),
    (FtnMatPost2010ChargeLPI, "Interest on failure to notify penalty – exempt VAT reg", ""),
    (FtnEachPartnerCharge, "Failure to notify penalty", "because you did not tell us about all the partners and changes in your partnership"),
    (FtnEachPartnerChargeLPI, "Interest on failure to notify penalty – partners", ""),
    (VatReturn2ndLPPRPI, "Repayment interest on second late payment penalty", ""),
    (VatReturnPOA1stLPPRPI, "Repayment interest on penalty for late payment — payment on account", ""),
    (VatReturnPOA2ndLPPRPI, "Repayment interest on second penalty for late payment — payment on account", ""),
    (VatReturnAA1stLPPRPI, "Repayment interest on penalty for late payment — annual accounting balance", ""),
    (VatReturnAA2ndLPPRPI, "Repayment interest on second penalty for late payment — annual accounting balance", ""), (VatInaccRtnReplacedLPI, "Interest on inaccuracies penalty — information", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatProtectiveAssessRPI, "Repayment interest on protective assessment of VAT", ""),
    (VatAdditionalAssessRPI, "Repayment interest on additional assessment of VAT", ""),
    (InaccurInECSalesChargeLPI, "Interest on inaccuracies penalty – EC sales", ""),
    (FtnRCSLChargeLPI, "Interest on failure to notify penalty – reverse charge sale", "")
  )

  private val agentChargeTable = Table(
    ("Charge Type", "Title", "Description"),
    (FtnMatPost2010Charge, "Failure to notify penalty", "because your client did not tell us they were no longer exempt from VAT registration"),
    (FtnMatPost2010ChargeLPI, "Interest on failure to notify penalty – exempt VAT reg", ""),
    (FtnEachPartnerCharge, "Failure to notify penalty", "because your client did not tell us about all the partners and changes in their partnership"),
    (FtnEachPartnerChargeLPI, "Interest on failure to notify penalty – partners", ""),
    (VatFailureToSubmitECSalesCharge, "EC sales list penalty", "because your client has not submitted an EC sales list or has submitted it late"),
    (VatFailureToSubmitECSalesChargeLPI, "Interest on EC sales list penalty", ""),
    (ReturnDebitCharge, "VAT", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (ReturnCreditCharge, "Repayment from HMRC", "for the 12\u00a0Jan to 23\u00a0Mar\u00a02018 return"),
    (OADebitCharge, "Officer’s assessment of VAT", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (OACreditCharge, "Officer’s assessment of VAT", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (CentralAssessmentCharge, "Central assessment of VAT", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (DebitDefaultSurcharge, "Surcharge", "for late payment of the 12\u00a0Jan to 23\u00a0Mar\u00a02018 return"),
    (CreditDefaultSurcharge, "Surcharge", "for late payment of the 12\u00a0Jan to 23\u00a0Mar\u00a02018 return"),
    (ErrorCorrectionCreditCharge, "Error correction repayment from HMRC", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (ErrorCorrectionDebitCharge, "Error correction of VAT", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (OADefaultInterestCharge, "VAT officer’s assessment interest", "charged on the officer’s assessment"),
    (OAFurtherInterestCharge, "VAT officer’s assessment further interest", "charged on the officer’s assessment"),
    (AACharge, "Additional assessment of VAT", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (AAInterestCharge, "Additional assessment interest", "charged on additional tax assessed for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (AAFurtherInterestCharge, "Additional assessment further interest", "charged on additional tax assessed for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (StatutoryInterestCharge, "Statutory interest", "interest paid because of an error by HMRC"),
    (InaccuraciesAssessmentsPenCharge, "Inaccuracies penalty", "because your client submitted an inaccurate document for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (MpPre2009Charge, "Misdeclaration penalty", "because your client has made an incorrect declaration"),
    (MpRepeatedPre2009Charge, "Misdeclaration repeat penalty", "because your client has repeatedly made incorrect declarations"),
    (InaccuraciesReturnReplacedCharge, "Inaccuracies penalty", "because your client submitted inaccurate information for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (WrongDoingPenaltyCharge, "Wrongdoing penalty", "because your client charged VAT when they should not have done"),
    (VatWrongDoingPenaltyLPI, "Interest on wrongdoing penalty", ""),
    (CarterPenaltyCharge, "Penalty for not filing correctly", "because your client did not use the correct digital channel for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatCarterPenaltyLPI, "Interest on penalty for not filing correctly", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (FailureToSubmitRCSLCharge, "Reverse Charge sales list penalty", "because your client failed to submit a reverse charge sales list"),
    (VatFailureToSubmitRCSLLPI, "Interest on Reverse Charge sales list penalty", ""),
    (CreditReturnOffsetCharge, "Overpayment partial refund", "partial repayment for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (UnallocatedPayment, "Unallocated payment", "your client made an overpayment which can be refunded to them or left on account"),
    (Refund, "Refund payment from HMRC", "as your client requested a refund on an overpayment they made"),
    (VatMigratedCredit, "VAT migrated credit", "miscellaneous VAT credit"),
    (VatMigratedLiability, "VAT migrated liability", ""),
    (VatDeferralPenalty, "Penalty", "for unpaid deferred VAT"),
    (PaymentOnAccountInstalments, "Payment on account instalment", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (PaymentOnAccountReturnDebitCharge, "Payment on account balance", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (PaymentOnAccountReturnCreditCharge, "Payment on account repayment", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (AAMonthlyInstalment, "Annual accounting monthly instalment", "for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (AAQuarterlyInstalments, "Annual accounting quarterly instalment", "for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (AAReturnDebitCharge, "Annual accounting balance", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (AAReturnCreditCharge, "Annual accounting repayment", "for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatUnrepayableOverpayment, "Overpayment", "cannot be repaid after 4 years"),
    (VatReturn1stLPP, "Late payment penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturnLPI, "Interest on VAT", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatPALPICharge, "Interest on protective assessment", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturn1stLPPLPI, "Interest on penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturn2ndLPPLPI, "Interest on second penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatCentralAssessmentLPI, "Interest on central assessment of VAT", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatCA1stLPPLPI, "Interest on central assessment penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatCA2ndLPPLPI, "Interest on central assessment second penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatOfficersAssessmentLPI, "Interest on officer’s assessment of VAT", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatOA1stLPPLPI, "Interest on officer’s assessment penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatOA2ndLPPLPI, "Interest on officer’s assessment second penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAA1stLPP, "Late payment penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAA2ndLPP, "Second late payment penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAdditionalAssessmentLPI, "Interest on additional assessment", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatPA1stLPP, "Late payment penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatPA2ndLPP, "Second late payment penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAA1stLPPLPI, "Interest on additional assessment penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAA2ndLPPLPI, "Interest on additional assessment second penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatLateSubmissionPen, "Late submission penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatLspInterest, "Interest on late submission penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturnAA1stLPPLPI, "Interest on annual accounting balance penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturnAA2ndLPPLPI, "Interest on annual accounting balance second penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatPOAInstalmentLPI, "Interest on payment on account instalment", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAAReturnChargeLPI, "Interest on annual accounting balance", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatManualLPP, "Late payment penalty", ""),
    (VatManualLPPLPI, "Interest on late payment penalty", ""),
    (VatAAQuarterlyInstalLPI, "Interest on annual accounting quarterly instalment", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAAMonthlyInstalLPI, "Interest on annual accounting monthly instalment", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAAReturnCharge1stLPP, "Late payment penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAAReturnCharge2ndLPP, "Second late payment penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatManualLPPRPI, "Repayment interest on late payment penalty", ""),
    (VatReturnLPP1RPI, "Repayment interest on penalty for late payment of VAT", ""),
    (VatReturn2ndLPP, "Second late payment penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatErrorCorrectionLPI, "Interest on error correction", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatErrorCorrection1stLPP, "Late payment penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatErrorCorrection2ndLPP, "Second late payment penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatErrorCorrection1stLPPLPI, "Interest on error correction penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatErrorCorrection2ndLPPLPI, "Interest on error correction second penalty", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturnPOALPI, "Interest on payment on account", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatPOAReturn1stLPP, "Late payment penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatPOAReturn2ndLPP, "Second late payment penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturnPOA1stLPPLPI, "Interest on payment on account penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturnPOA2ndLPPLPI, "Interest on payment on account second penalty", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatAAReturnChargeRPI, "Repayment interest on annual accounting repayment", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatErrorCorrectionRPI, "Repayment interest on error correction repayment", "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturnPOARPI, "Repayment interest on payment on account balance", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatReturn2ndLPPRPI, "Repayment interest on second late payment penalty", ""),
    (VatReturnPOA1stLPPRPI, "Repayment interest on penalty for late payment — payment on account", ""),
    (VatReturnPOA2ndLPPRPI, "Repayment interest on second penalty for late payment — payment on account", ""),
    (VatReturnAA1stLPPRPI, "Repayment interest on penalty for late payment — annual accounting balance", ""),
    (VatReturnAA2ndLPPRPI, "Repayment interest on second penalty for late payment — annual accounting balance", ""),
    (VatInaccRtnReplacedLPI, "Interest on inaccuracies penalty — information", "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"),
    (VatProtectiveAssessRPI, "Repayment interest on protective assessment of VAT", ""),
    (VatAdditionalAssessRPI, "Repayment interest on additional assessment of VAT", ""),
    (InaccurInECSalesChargeLPI, "Interest on inaccuracies penalty – EC sales", ""),
    (FtnRCSLChargeLPI, "Interest on failure to notify penalty – reverse charge sale", "")
  )

  "When user is not an agent, PaymentsHistoryChargeDescription template" should
    forEvery(nonAgentChargeTable) { (chargeType, expectedTitle, expectedDescription) =>
      s"display the correct title and description for charge type $chargeType" in {
        val model = exampleModel.copy(chargeType = chargeType)
        val template = paymentsHistoryChargeDescription(model)
        implicit val document: Document = Jsoup.parse(template.body)

        elementText(Selectors.chargeTitle) shouldBe expectedTitle
        elementText(Selectors.description) shouldBe expectedDescription
      }
    }

  "When user is an agent, PaymentsHistoryChargeDescription template" should
    forEvery(agentChargeTable) { (chargeType, expectedTitle, expectedDescription) =>
      s"display the correct title and description for charge type $chargeType" in {
        val model = exampleModel.copy(chargeType = chargeType)
        val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
        implicit val document: Document = Jsoup.parse(template.body)

        elementText(Selectors.chargeTitle) shouldBe expectedTitle
        elementText(Selectors.description) shouldBe expectedDescription

      }
    }
}