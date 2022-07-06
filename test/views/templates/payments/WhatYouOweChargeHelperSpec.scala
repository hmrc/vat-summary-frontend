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
import views.ViewBaseSpec

class WhatYouOweChargeHelperSpec extends ViewBaseSpec {

  def paymentModel(chargeType: ChargeType): OpenPaymentsModel = OpenPaymentsModel(
    chargeType,
    BigDecimal(100.00),
    LocalDate.parse("2018-03-03"),
    LocalDate.parse("2018-01-01"),
    LocalDate.parse("2018-02-02"),
    Some("18AA"),
    isOverdue = false
  )

  def paymentModelNoPeriod(chargeType: ChargeType): OpenPaymentsModel = OpenPaymentsModelNoPeriod(
    chargeType,
    BigDecimal(100.00),
    LocalDate.parse("2018-03-03"),
    Some("18AA"),
    Some("XD002750002155"),
    isOverdue = false
  )

  "WhatYouOweChargeHelper .description" when {

    "the user is a principal user" when {

      "the charge has a to and from date" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ReturnDebitCharge),false, messages)

        "return the description of the charge" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "the charge should have a to and from date to form the description, but they are not retrieved" should {

        val model = paymentModelNoPeriod(ReturnDebitCharge)
        val helper = new WhatYouOweChargeHelper(model, false, messages)

        "omit the description of the charge" in {
          helper.description shouldBe None
        }
      }

      "the charge does not have to and from date" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OADefaultInterestCharge), false, messages)

        "return the description of the charge" in {
          helper.description shouldBe Some("charged on the officer’s assessment")
        }
      }

      "the charge has no description" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(MiscPenaltyCharge), false, messages)

        "return no description" in {
          helper.description shouldBe None
        }
      }

      "there is a vat return debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ReturnDebitCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a vat return credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ReturnCreditCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for the 1 Jan to 2 Feb 2018 return")
        }
      }

      "there is a vat officer assessment debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OADebitCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for 1 Jan to 2 Feb 2018")
        }
      }

      "there is a vat officer assessment credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OACreditCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for 1 Jan to 2 Feb 2018")
        }
      }

      "there is a vat central assessment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(CentralAssessmentCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Debit Default Surcharge charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(DebitDefaultSurcharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for late payment of the 1 Jan to 2 Feb 2018 return")
        }
      }

      "there is a VAT Credit Default Surcharge charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(CreditDefaultSurcharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for late payment of the 1 Jan to 2 Feb 2018 return")
        }
      }

      "there is an error correction credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ErrorCorrectionCreditCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for 1 Jan to 2 Feb 2018")
        }
      }

      "there is an error correction debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ErrorCorrectionDebitCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for 1 Jan to 2 Feb 2018")
        }
      }

      "there is a vat officer assessment default interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OADefaultInterestCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("charged on the officer’s assessment")
        }
      }

      "there is a VAT Officer Assessment Further Interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OAFurtherInterestCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("charged on the officer’s assessment")
        }
      }

      "there is a VAT Additional Assessment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AACharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Default Interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAInterestCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("charged on additional tax assessed for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Further Interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAFurtherInterestCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("charged on additional tax assessed for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Statutory Interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(StatutoryInterestCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("interest paid because of an error by HMRC")
        }
      }

      "there is a Vat Inaccuracy Assessments Pen charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(InaccuraciesAssessmentsPenCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("because you submitted an inaccurate document for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a Vat Mp Pre 2009 Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(MpPre2009Charge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("because you have made an incorrect declaration")
        }
      }

      "there is a Vat Mp Repeated Pre 2009 Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(MpRepeatedPre2009Charge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("because you have repeatedly made incorrect declarations")
        }
      }

      "there is a Vat Inaccuracies Return Replaced Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(InaccuraciesReturnReplacedCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("because you have submitted inaccurate information for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a Vat Wrong Doing Penalty Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(WrongDoingPenaltyCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("because you charged VAT when you should not have done")
        }
      }

      "there is a Vat Credit Return Offset Charge Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(CreditReturnOffsetCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("partial repayment for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is an Unallocated Payment Charge Type" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(UnallocatedPayment), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("you made an overpayment which can be refunded to you or left on account")
        }
      }

      "there is a Refund Charge Type" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(Refund), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("as you requested a refund on an overpayment you made")
        }
      }

      "there is a VAT POA Instalment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(PaymentOnAccountInstalments), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT POA Return Debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(PaymentOnAccountReturnDebitCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT POA Return Credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(PaymentOnAccountReturnCreditCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Monthly Instalment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAMonthlyInstalment), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Quarterly Instalment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAQuarterlyInstalments), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Return Debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAReturnDebitCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Return Credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAReturnCreditCharge), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Unrepayable Overpayment Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatUnrepayableOverpayment), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("cannot be repaid after 4 years")
        }
      }

      "there is a VAT Return 1st LPP Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatReturn1stLPP), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for late payment of VAT for VAT period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Return LPI charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatReturnLPI), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Return 1st LPP LPI charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatReturn1stLPPLPI), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Return 2nd LPP LPI charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatReturn2ndLPPLPI), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Central Assessment LPI charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatCentralAssessmentLPI), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT CA 1st LPP LPI charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatCA1stLPPLPI), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT CA 2nd LPP LPI charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatCA2ndLPPLPI), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Officer's Assessment LPI charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatOfficersAssessmentLPI), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT OA 1st LPP LPI charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatOA1stLPPLPI), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT OA 2nd LPP LPI charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatOA2ndLPPLPI), false, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for 1 Jan to 2 Feb 2018")
        }
      }
    }

    "the user is an agent" when {

      "the charge has a to and from date" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ReturnDebitCharge), true, messages)

        "return the description of the charge" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "the charge should have a to and from date to form the description, but they are not retrieved" should {

        val model = paymentModelNoPeriod(ReturnDebitCharge)
        val helper = new WhatYouOweChargeHelper(model, true, messages)

        "omit the description of the charge" in {
          helper.description shouldBe None
        }
      }

      "the charge does not have to and from date" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OADefaultInterestCharge), true, messages)

        "return the description of the charge" in {
          helper.description shouldBe Some("charged on the officer’s assessment")
        }
      }

      "the charge has no description" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(MiscPenaltyCharge), true, messages)

        "return no description" in {
          helper.description shouldBe None
        }
      }

      "there is a vat return debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ReturnDebitCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a vat return credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ReturnCreditCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for the 1 Jan to 2 Feb 2018 return")
        }
      }

      "there is a vat officer assessment debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OADebitCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for 1 Jan to 2 Feb 2018")
        }
      }

      "there is a vat officer assessment credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OACreditCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for 1 Jan to 2 Feb 2018")
        }
      }

      "there is a vat central assessment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(CentralAssessmentCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Debit Default Surcharge charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(DebitDefaultSurcharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for late payment of the 1 Jan to 2 Feb 2018 return")
        }
      }

      "there is a VAT Credit Default Surcharge charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(CreditDefaultSurcharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for late payment of the 1 Jan to 2 Feb 2018 return")
        }
      }

      "there is an error correction credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ErrorCorrectionCreditCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for 1 Jan to 2 Feb 2018")
        }
      }

      "there is an error correction debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ErrorCorrectionDebitCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for 1 Jan to 2 Feb 2018")
        }
      }

      "there is a vat officer assessment default interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OADefaultInterestCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("charged on the officer’s assessment")
        }
      }

      "there is a VAT Officer Assessment Further Interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OAFurtherInterestCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("charged on the officer’s assessment")
        }
      }

      "there is a VAT Additional Assessment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AACharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Default Interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAInterestCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("charged on additional tax assessed for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Further Interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAFurtherInterestCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("charged on additional tax assessed for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Statutory Interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(StatutoryInterestCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("interest paid because of an error by HMRC")
        }
      }

      "there is a Vat Inaccuracy Assessments Pen charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(InaccuraciesAssessmentsPenCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("because your client submitted an inaccurate document for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a Vat Mp Pre 2009 Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(MpPre2009Charge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("because your client has made an incorrect declaration")
        }
      }

      "there is a Vat Mp Repeated Pre 2009 Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(MpRepeatedPre2009Charge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("because your client has repeatedly made incorrect declarations")
        }
      }

      "there is a Vat Inaccuracies Return Replaced Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(InaccuraciesReturnReplacedCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("because your client submitted inaccurate information for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a Vat Wrong Doing Penalty Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(WrongDoingPenaltyCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("because your client charged VAT when they should not have done")
        }
      }

      "there is a Vat Credit Return Offset Charge Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(CreditReturnOffsetCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("partial repayment for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is an Unallocated Payment Charge Type" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(UnallocatedPayment), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("your client made an overpayment which can be refunded to them or left on account")
        }
      }

      "there is a Refund Charge Type" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(Refund), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("as your client requested a refund on an overpayment they made")
        }
      }

      "there is a VAT POA Instalment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(PaymentOnAccountInstalments), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT POA Return Debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(PaymentOnAccountReturnDebitCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT POA Return Credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(PaymentOnAccountReturnCreditCharge),true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Monthly Instalment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAMonthlyInstalment), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Quarterly Instalment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAQuarterlyInstalments), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Return Debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAReturnDebitCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Return Credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAReturnCreditCharge), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Unrepayable Overpayment Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatUnrepayableOverpayment), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("cannot be repaid after 4 years")
        }
      }

      "there is a VAT Return 1st LPP Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatReturn1stLPP), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for late payment of VAT for VAT period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Return LPI charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatReturnLPI), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Return 1st LPP LPI charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatReturn1stLPPLPI), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Return 2nd LPP LPI charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatReturn2ndLPPLPI), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Central Assessment LPI charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatCentralAssessmentLPI), true, messages)

        "display the correct description" in {
          helper.description shouldBe Some("for period 1 Jan to 2 Feb 2018")
        }
      }
    }

    "WhatYouOweChargeHelper .title" should {

      val helper = new WhatYouOweChargeHelper(paymentModel(VatOAInaccuraciesFrom2009), true, messages)

      "return the title" in {
        helper.title shouldBe "Inaccuracies penalty"
      }
    }

    "WhatYouOweChargeHelper .payLinkText" when {

      "charge type is a different type of charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OAFurtherInterestCharge), true, messages)

        "return Pay now" in {
          helper.payLinkText shouldBe "Pay now"
        }
      }
    }

    "WhatYouOweChargeHelper .viewReturnEnabled" when {

      "charge type is Return Debit Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ReturnDebitCharge), true, messages)

        "return true" in {
          helper.viewReturnEnabled shouldBe true
        }
      }

      "charge type is Error Correction Debit Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ErrorCorrectionDebitCharge), true, messages)

        "return true" in {
          helper.viewReturnEnabled shouldBe true
        }
      }

      "charge type is Annual Accounting Return Debit Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAReturnDebitCharge), true, messages)


        "return true" in {
          helper.viewReturnEnabled shouldBe true
        }
      }

      "charge type is POA Return Debit Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(PaymentOnAccountReturnDebitCharge), true, messages)

        "return true" in {
          helper.viewReturnEnabled shouldBe true
        }
      }

      "charge type is a different type of charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OAFurtherInterestCharge), true, messages)

        "return Pay now" in {
          helper.viewReturnEnabled shouldBe false
        }
      }
    }
  }

  "WhatYouOweChargeHelper .viewReturnContext" when {

    "the charge has a to and from period" when {

      "charge type is Return Debit Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ReturnDebitCharge), false, messages)

        "return 'that you corrected for the period 1 January to 2 February 2018'" in {
          helper.viewReturnContext shouldBe "for the period 1 January to 2 February 2018"
        }
      }

      "charge type is Annual Account Return Debit Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAReturnDebitCharge), false, messages)

        "return 'that you corrected for the period 1 January to 2 February 2018'" in {
          helper.viewReturnContext shouldBe "for the period 1 January to 2 February 2018"
        }
      }

      "charge type is a POA Return Debit Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAReturnDebitCharge), false, messages)

        "return 'that you corrected for the period 1 January to 2 February 2018'" in {
          helper.viewReturnContext shouldBe "for the period 1 January to 2 February 2018"
        }
      }

      "charge type is Error Correction Debit Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ErrorCorrectionDebitCharge), false, messages)

        "return 'for the period 1 January to 2 February 2018'" in {
          helper.viewReturnContext shouldBe "that you corrected for the period 1 January to 2 February 2018"
        }
      }

      "charge type is different type of charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OAFurtherInterestCharge), false, messages)

        "return empty string" in {
          helper.viewReturnContext shouldBe ""
        }
      }
    }

    "the charge has no to or from period" should {

      val helper = new WhatYouOweChargeHelper(paymentModelNoPeriod(MpRepeatedPre2009Charge), false, messages)

      "return empty string" in {
        helper.viewReturnContext shouldBe ""
      }
    }
  }

}
