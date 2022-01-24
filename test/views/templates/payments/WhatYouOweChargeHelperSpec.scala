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
    "18AA",
    isOverdue = false
  )

  def paymentModelNoPeriod(chargeType: ChargeType): OpenPaymentsModel = OpenPaymentsModelNoPeriod(
    chargeType,
    BigDecimal(100.00),
    LocalDate.parse("2018-03-03"),
    "18AA",
    Some("XD002750002155"),
    isOverdue = false
  )

  "WhatYouOweChargeHelper .description" when {

    "the user is a principal user" when {

      "the charge has a to and from date" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ReturnDebitCharge), messages, user)

        "return the description of the charge" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "the charge should have a to and from date to form the description, but they are not retrieved" should {

        val model = paymentModelNoPeriod(ReturnDebitCharge)
        val helper = new WhatYouOweChargeHelper(model, messages, user)

        "omit the description of the charge" in {
          helper.description shouldBe None
        }
      }

      "the charge does not have to and from date" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OADefaultInterestCharge), messages, user)

        "return the description of the charge" in {
          helper.description shouldBe Some("interest charged on the officer’s assessment")
        }
      }

      "the charge has no description" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(MiscPenaltyCharge), messages, user)

        "return no description" in {
          helper.description shouldBe None
        }
      }

      "there is a vat return debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ReturnDebitCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a vat return credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ReturnCreditCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("for the 1 Jan to 2 Feb 2018 return")
        }
      }

      "there is a vat officer assessment debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OADebitCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("for underpaying by this amount")
        }
      }

      "there is a vat officer assessment credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OACreditCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("for overpaying by this amount")
        }
      }

      "there is a vat central assessment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(CentralAssessmentCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("for the 1 Jan to 2 Feb 2018 return")
        }
      }

      "there is a VAT Debit Default Surcharge charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(DebitDefaultSurcharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("for late payment of the 1 Jan to 2 Feb 2018 return")
        }
      }

      "there is a VAT Credit Default Surcharge charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(CreditDefaultSurcharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("for late payment of the 1 Jan to 2 Feb 2018 return")
        }
      }

      "there is an error correction credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ErrorCorrectionCreditCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("for correcting the 1 Jan to 2 Feb 2018 return")
        }
      }

      "there is an error correction debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ErrorCorrectionDebitCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("for correcting the 1 Jan to 2 Feb 2018 return")
        }
      }

      "there is a vat officer assessment default interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OADefaultInterestCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("interest charged on the officer’s assessment")
        }
      }

      "there is a VAT Officer Assessment Further Interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OAFurtherInterestCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("further interest charged on the officer’s assessment")
        }
      }

      "there is a VAT Additional Assessment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AACharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("additional assessment based on further information for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Default Interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAInterestCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("interest charged on additional tax assessed for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Further Interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAFurtherInterestCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("further interest charged on additional tax assessed for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Statutory Interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(StatutoryInterestCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("interest paid because of an error by HMRC")
        }
      }

      "there is a Vat Inaccuracy Assessments Pen charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(InaccuraciesAssessmentsPenCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("because you submitted an inaccurate document for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a Vat Mp Pre 2009 Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(MpPre2009Charge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("because you have made an incorrect declaration")
        }
      }

      "there is a Vat Mp Repeated Pre 2009 Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(MpRepeatedPre2009Charge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("because you have repeatedly made incorrect declarations")
        }
      }

      "there is a Vat Inaccuracies Return Replaced Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(InaccuraciesReturnReplacedCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("because you have submitted inaccurate information for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a Vat Wrong Doing Penalty Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(WrongDoingPenaltyCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("because you charged VAT when you should not have done")
        }
      }

      "there is a Vat Credit Return Offset Charge Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(CreditReturnOffsetCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("partial repayment for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is an Unallocated Payment Charge Type" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(UnallocatedPayment), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("you made an overpayment which can be refunded to you or left on account")
        }
      }

      "there is a Refund Charge Type" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(Refund), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("as you requested a refund on an overpayment you made")
        }
      }

      "there is a VAT POA Instalment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(PaymentOnAccountInstalments), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT POA Return Debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(PaymentOnAccountReturnDebitCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT POA Return Credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(PaymentOnAccountReturnCreditCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Monthly Instalment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAMonthlyInstalment), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Quarterly Instalment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAQuarterlyInstalments), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Return Debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAReturnDebitCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Return Credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAReturnCreditCharge), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Unrepayable Overpayment Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatUnrepayableOverpayment), messages, user)

        "display the correct description" in {
          helper.description shouldBe Some("cannot be repaid after 4 years")
        }
      }
    }

    "the user is an agent" when {

      "the charge has a to and from date" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ReturnDebitCharge), messages, agentUser)

        "return the description of the charge" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "the charge should have a to and from date to form the description, but they are not retrieved" should {

        val model = paymentModelNoPeriod(ReturnDebitCharge)
        val helper = new WhatYouOweChargeHelper(model, messages, agentUser)

        "omit the description of the charge" in {
          helper.description shouldBe None
        }
      }

      "the charge does not have to and from date" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OADefaultInterestCharge), messages, agentUser)

        "return the description of the charge" in {
          helper.description shouldBe Some("interest charged on the officer’s assessment")
        }
      }

      "the charge has no description" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(MiscPenaltyCharge), messages, agentUser)

        "return no description" in {
          helper.description shouldBe None
        }
      }

      "there is a vat return debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ReturnDebitCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a vat return credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ReturnCreditCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("for the 1 Jan to 2 Feb 2018 return")
        }
      }

      "there is a vat officer assessment debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OADebitCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("for underpaying by this amount")
        }
      }

      "there is a vat officer assessment credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OACreditCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("for overpaying by this amount")
        }
      }

      "there is a vat central assessment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(CentralAssessmentCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("for the 1 Jan to 2 Feb 2018 return")
        }
      }

      "there is a VAT Debit Default Surcharge charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(DebitDefaultSurcharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("for late payment of the 1 Jan to 2 Feb 2018 return")
        }
      }

      "there is a VAT Credit Default Surcharge charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(CreditDefaultSurcharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("for late payment of the 1 Jan to 2 Feb 2018 return")
        }
      }

      "there is an error correction credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ErrorCorrectionCreditCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("for correcting the 1 Jan to 2 Feb 2018 return")
        }
      }

      "there is an error correction debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ErrorCorrectionDebitCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("for correcting the 1 Jan to 2 Feb 2018 return")
        }
      }

      "there is a vat officer assessment default interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OADefaultInterestCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("interest charged on the officer’s assessment")
        }
      }

      "there is a VAT Officer Assessment Further Interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OAFurtherInterestCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("further interest charged on the officer’s assessment")
        }
      }

      "there is a VAT Additional Assessment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AACharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("additional assessment based on further information for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Default Interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAInterestCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("interest charged on additional tax assessed for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Further Interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAFurtherInterestCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("further interest charged on additional tax assessed for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Statutory Interest charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(StatutoryInterestCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("interest paid because of an error by HMRC")
        }
      }

      "there is a Vat Inaccuracy Assessments Pen charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(InaccuraciesAssessmentsPenCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("because your client submitted an inaccurate document for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a Vat Mp Pre 2009 Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(MpPre2009Charge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("because your client has made an incorrect declaration")
        }
      }

      "there is a Vat Mp Repeated Pre 2009 Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(MpRepeatedPre2009Charge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("because your client has repeatedly made incorrect declarations")
        }
      }

      "there is a Vat Inaccuracies Return Replaced Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(InaccuraciesReturnReplacedCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("because your client submitted inaccurate information for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a Vat Wrong Doing Penalty Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(WrongDoingPenaltyCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("because your client charged VAT when they should not have done")
        }
      }

      "there is a Vat Credit Return Offset Charge Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(CreditReturnOffsetCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("partial repayment for period 1 Jan to 2 Feb 2018")
        }
      }

      "there is an Unallocated Payment Charge Type" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(UnallocatedPayment), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("your client made an overpayment which can be refunded to them or left on account")
        }
      }

      "there is a Refund Charge Type" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(Refund), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("as your client requested a refund on an overpayment they made")
        }
      }

      "there is a VAT POA Instalment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(PaymentOnAccountInstalments), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT POA Return Debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(PaymentOnAccountReturnDebitCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT POA Return Credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(PaymentOnAccountReturnCreditCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Monthly Instalment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAMonthlyInstalment), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Quarterly Instalment charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAQuarterlyInstalments), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Return Debit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAReturnDebitCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT AA Return Credit charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAReturnCreditCharge), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("for the period 1 Jan to 2 Feb 2018")
        }
      }

      "there is a VAT Unrepayable Overpayment Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(VatUnrepayableOverpayment), messages, agentUser)

        "display the correct description" in {
          helper.description shouldBe Some("cannot be repaid after 4 years")
        }
      }
    }

    "WhatYouOweChargeHelper .title" should {

      val helper = new WhatYouOweChargeHelper(paymentModel(VatOAInaccuraciesFrom2009), messages, agentUser)

      "return the title" in {
        helper.title shouldBe "Inaccuracies penalty"
      }
    }

    "WhatYouOweChargeHelper .payLinkText" when {

      "charge type is a different type of charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OAFurtherInterestCharge), messages, agentUser)

        "return Pay now" in {
          helper.payLinkText shouldBe "Pay now"
        }
      }
    }

    "WhatYouOweChargeHelper .viewReturnEnabled" when {

      "charge type is Return Debit Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ReturnDebitCharge), messages, agentUser)

        "return true" in {
          helper.viewReturnEnabled shouldBe true
        }
      }

      "charge type is Error Correction Debit Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ErrorCorrectionDebitCharge), messages, agentUser)

        "return true" in {
          helper.viewReturnEnabled shouldBe true
        }
      }

      "charge type is Annual Accounting Return Debit Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAReturnDebitCharge), messages, agentUser)


        "return true" in {
          helper.viewReturnEnabled shouldBe true
        }
      }

      "charge type is POA Return Debit Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(PaymentOnAccountReturnDebitCharge), messages, agentUser)

        "return true" in {
          helper.viewReturnEnabled shouldBe true
        }
      }

      "charge type is a different type of charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OAFurtherInterestCharge), messages, agentUser)

        "return Pay now" in {
          helper.viewReturnEnabled shouldBe false
        }
      }

    }
  }

  "WhatYouOweChargeHelper .viewReturnContext" when {

    "the charge has a to and from period" when {

      "charge type is Return Debit Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ReturnDebitCharge), messages, user)

        "return 'that you corrected for the period 1 January to 2 February 2018'" in {
          helper.viewReturnContext shouldBe "for the period 1 January to 2 February 2018"
        }
      }

      "charge type is Annual Account Return Debit Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAReturnDebitCharge), messages, user)

        "return 'that you corrected for the period 1 January to 2 February 2018'" in {
          helper.viewReturnContext shouldBe "for the period 1 January to 2 February 2018"
        }
      }

      "charge type is a POA Return Debit Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(AAReturnDebitCharge), messages, user)

        "return 'that you corrected for the period 1 January to 2 February 2018'" in {
          helper.viewReturnContext shouldBe "for the period 1 January to 2 February 2018"
        }
      }

      "charge type is Error Correction Debit Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ErrorCorrectionDebitCharge), messages, user)

        "return 'for the period 1 January to 2 February 2018'" in {
          helper.viewReturnContext shouldBe "that you corrected for the period 1 January to 2 February 2018"
        }
      }

      "charge type is different type of charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OAFurtherInterestCharge), messages, user)

        "return empty string" in {
          helper.viewReturnContext shouldBe ""
        }
      }
    }

    "the charge has no to or from period" should {

      val helper = new WhatYouOweChargeHelper(paymentModelNoPeriod(MpRepeatedPre2009Charge), messages, user)

      "return empty string" in {
        helper.viewReturnContext shouldBe ""
      }
    }
  }

}
