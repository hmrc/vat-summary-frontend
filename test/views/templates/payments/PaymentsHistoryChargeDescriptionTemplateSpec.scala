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
import models.viewModels.PaymentsHistoryModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.templates.payments.PaymentsHistoryChargeDescription

class PaymentsHistoryChargeDescriptionTemplateSpec extends ViewBaseSpec {

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

  "The PaymentsHistoryChargeDescription template" when {

    "user is not an agent" when {

      "there is a vat return debit charge" should {

        lazy val template = paymentsHistoryChargeDescription(exampleModel)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "VAT"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a vat return credit charge" should {

        val model = exampleModel.copy(chargeType = ReturnCreditCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Repayment from HMRC"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for the 12 Jan to 23 Mar 2018 return"
        }
      }

      "there is a vat officer assessment debit charge" should {

        val model = exampleModel.copy(chargeType = OADebitCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Officer’s assessment of VAT"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
        }
      }

      "there is a vat officer assessment credit charge" should {

        val model = exampleModel.copy(chargeType = OACreditCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Officer’s assessment of VAT"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
        }
      }

      "there is a vat central assessment charge" should {

        val model = exampleModel.copy(chargeType = CentralAssessmentCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Central assessment of VAT"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT Debit Default Surcharge charge" should {

        val model = exampleModel.copy(chargeType = DebitDefaultSurcharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Surcharge"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for late payment of the 12 Jan to 23 Mar 2018 return"
        }
      }

      "there is a VAT Credit Default Surcharge charge" should {

        val model = exampleModel.copy(chargeType = CreditDefaultSurcharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Surcharge"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for late payment of the 12 Jan to 23 Mar 2018 return"
        }
      }

      "there is an error correction credit charge" should {

        val model = exampleModel.copy(chargeType = ErrorCorrectionCreditCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Error correction repayment from HMRC"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
        }
      }

      "there is an error correction debit charge" should {

        val model = exampleModel.copy(chargeType = ErrorCorrectionDebitCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Error correction of VAT"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
        }
      }

      "there is a vat officer assessment default interest charge" should {

        val model = exampleModel.copy(chargeType = OADefaultInterestCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "VAT officer’s assessment interest"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "charged on the officer’s assessment"
        }
      }

      "there is a VAT Officer Assessment Further Interest charge" should {

        val model = exampleModel.copy(chargeType = OAFurtherInterestCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "VAT officer’s assessment further interest"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "charged on the officer’s assessment"
        }
      }

      "there is a VAT Additional Assessment charge" should {

        val model = exampleModel.copy(chargeType = AACharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Additional assessment of VAT"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT AA Default Interest charge" should {

        val model = exampleModel.copy(chargeType = AAInterestCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Additional assessment interest"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "charged on additional tax assessed" +
            " for the period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT AA Further Interest charge" should {

        val model = exampleModel.copy(chargeType = AAFurtherInterestCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Additional assessment further interest"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "charged on additional tax assessed" +
            " for the period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT Statutory Interest charge" should {

        val model = exampleModel.copy(chargeType = StatutoryInterestCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Statutory interest"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "interest paid because of an error by HMRC"
        }
      }

      "there is a Vat Inaccuracy Assessments Pen charge" should {

        val model = exampleModel.copy(chargeType = InaccuraciesAssessmentsPenCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Inaccuracies penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe
            "because you submitted an inaccurate document for the period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a Vat Mp Pre 2009 Charge" should {

        val model = exampleModel.copy(chargeType = MpPre2009Charge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Misdeclaration penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "because you have made an incorrect declaration"
        }
      }

      "there is a Vat Mp Repeated Pre 2009 Charge" should {

        val model = exampleModel.copy(chargeType = MpRepeatedPre2009Charge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Misdeclaration repeat penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "because you have repeatedly made incorrect declarations"
        }
      }

      "there is a Vat Inaccuracies Return Replaced Charge" should {

        val model = exampleModel.copy(chargeType = InaccuraciesReturnReplacedCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Inaccuracies penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "because you have submitted inaccurate information for" +
            " the period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a Vat Wrong Doing Penalty Charge" should {

        val model = exampleModel.copy(chargeType = WrongDoingPenaltyCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Wrongdoing penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "because you charged VAT when you should not have done"
        }
      }

      "there is a Vat Credit Return Offset Charge Charge" should {

        val model = exampleModel.copy(chargeType = CreditReturnOffsetCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Overpayment partial refund"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "partial repayment for period 12 Jan to 23 Mar 2018"
        }
      }

      "there is an Unallocated Payment Charge Type" should {

        val model = exampleModel.copy(chargeType = UnallocatedPayment)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Unallocated payment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "you made an overpayment which can be refunded to you or left on account"
        }
      }

      "there is an Refund Charge Type" should {

        val model = exampleModel.copy(chargeType = Refund)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Refund payment from HMRC"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "as you requested a refund on an overpayment you made"
        }
      }

      "there is a VAT POA Instalment charge" should {

        val model = exampleModel.copy(chargeType = PaymentOnAccountInstalments)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Payment on account instalment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT POA Return Debit charge" should {

        val model = exampleModel.copy(chargeType = PaymentOnAccountReturnDebitCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Payment on account balance"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT POA Return Credit charge" should {

        val model = exampleModel.copy(chargeType = PaymentOnAccountReturnCreditCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Payment on account repayment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT AA Monthly Instalment charge" should {

        val model = exampleModel.copy(chargeType = AAMonthlyInstalment)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Annual accounting monthly instalment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for the period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT AA Quarterly Instalment charge" should {

        val model = exampleModel.copy(chargeType = AAQuarterlyInstalments)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Annual accounting quarterly instalment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for the period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT AA Return Debit charge" should {

        val model = exampleModel.copy(chargeType = AAReturnDebitCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Annual accounting balance"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT AA Return Credit charge" should {

        val model = exampleModel.copy(chargeType = AAReturnCreditCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Annual accounting repayment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for the period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT Unrepayable Overpayment Charge" should {

        val model = exampleModel.copy(chargeType = VatUnrepayableOverpayment)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Overpayment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "cannot be repaid after 4 years"
        }
      }

      "there is a VAT return 1st LPP Charge" should {

        val model = exampleModel.copy(chargeType = VatReturn1stLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for late payment of VAT for VAT period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT Return LPI charge" should {

        val model = exampleModel.copy(chargeType = VatReturnLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on VAT"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT Return 1st LPP LPI charge" should {

        val model = exampleModel.copy(chargeType = VatReturn1stLPPLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT Return 2nd LPP LPI charge" should {

        val model = exampleModel.copy(chargeType = VatReturn2ndLPPLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on second penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT Central Assessment LPI charge" should {

        val model = exampleModel.copy(chargeType = VatCentralAssessmentLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on central assessment of VAT"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT CA 1st LPP LPI charge" should {

        val model = exampleModel.copy(chargeType = VatCA1stLPPLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on central assessment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT CA 2nd LPP LPI charge" should {

        val model = exampleModel.copy(chargeType = VatCA2ndLPPLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on central assessment second penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT Officer's Assessment LPI charge" should {

        val model = exampleModel.copy(chargeType = VatOfficersAssessmentLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on officer’s assessment of VAT"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT OA 1st LPP LPI charge" should {

        val model = exampleModel.copy(chargeType = VatOA1stLPPLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on officer’s assessment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT OA 2nd LPP LPI charge" should {

        val model = exampleModel.copy(chargeType = VatOA2ndLPPLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on officer’s assessment second penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT PA LPI charge" should {

        val model = exampleModel.copy(chargeType = VatPALPICharge)
        lazy val template = paymentsHistoryChargeDescription(model)(messages, user)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on protective assessment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT PA 1st LPP charge" should {

        val model = exampleModel.copy(chargeType = VatPA1stLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for late payment of protective assessment for 12 Jan to 23 Mar 2018"
        }
      }
      "there is a VAT PA 2nd LPP charge" should {

        val model = exampleModel.copy(chargeType = VatPA2ndLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Second late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for late payment of protective assessment for 12 Jan to 23 Mar 2018"
        }
      }
    }
  }

  "user is an agent" when {

    "there is a vat return debit charge" should {

      lazy val template = paymentsHistoryChargeDescription(exampleModel)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "VAT"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a vat return credit charge" should {

      val model = exampleModel.copy(chargeType = ReturnCreditCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Repayment from HMRC"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for the 12 Jan to 23 Mar 2018 return"
      }
    }

    "there is a vat officer assessment debit charge" should {

      val model = exampleModel.copy(chargeType = OADebitCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Officer’s assessment of VAT"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
      }
    }

    "there is a vat officer assessment credit charge" should {

      val model = exampleModel.copy(chargeType = OACreditCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Officer’s assessment of VAT"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
      }
    }

    "there is a vat central assessment charge" should {

      val model = exampleModel.copy(chargeType = CentralAssessmentCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Central assessment of VAT"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT Debit Default Surcharge charge" should {

      val model = exampleModel.copy(chargeType = DebitDefaultSurcharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Surcharge"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for late payment of the 12 Jan to 23 Mar 2018 return"
      }
    }

    "there is a VAT Credit Default Surcharge charge" should {

      val model = exampleModel.copy(chargeType = CreditDefaultSurcharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Surcharge"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for late payment of the 12 Jan to 23 Mar 2018 return"
      }
    }

    "there is an error correction credit charge" should {

      val model = exampleModel.copy(chargeType = ErrorCorrectionCreditCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Error correction repayment from HMRC"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
      }
    }

    "there is an error correction debit charge" should {

      val model = exampleModel.copy(chargeType = ErrorCorrectionDebitCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Error correction of VAT"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
      }
    }

    "there is a vat officer assessment default interest charge" should {

      val model = exampleModel.copy(chargeType = OADefaultInterestCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "VAT officer’s assessment interest"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "charged on the officer’s assessment"
      }
    }

    "there is a VAT Officer Assessment Further Interest charge" should {

      val model = exampleModel.copy(chargeType = OAFurtherInterestCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "VAT officer’s assessment further interest"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "charged on the officer’s assessment"
      }
    }

    "there is a VAT Additional Assessment charge" should {

      val model = exampleModel.copy(chargeType = AACharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Additional assessment of VAT"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT AA Default Interest charge" should {

      val model = exampleModel.copy(chargeType = AAInterestCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Additional assessment interest"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "charged on additional tax assessed" +
          " for the period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT AA Further Interest charge" should {

      val model = exampleModel.copy(chargeType = AAFurtherInterestCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Additional assessment further interest"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "charged on additional tax assessed" +
          " for the period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT Statutory Interest charge" should {

      val model = exampleModel.copy(chargeType = StatutoryInterestCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Statutory interest"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "interest paid because of an error by HMRC"
      }
    }

    "there is a Vat Inaccuracy Assessments Pen charge" should {

      val model = exampleModel.copy(chargeType = InaccuraciesAssessmentsPenCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Inaccuracies penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe
          "because your client submitted an inaccurate document for the period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a Vat Mp Pre 2009 Charge" should {

      val model = exampleModel.copy(chargeType = MpPre2009Charge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Misdeclaration penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "because your client has made an incorrect declaration"
      }
    }

    "there is a Vat Mp Repeated Pre 2009 Charge" should {

      val model = exampleModel.copy(chargeType = MpRepeatedPre2009Charge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Misdeclaration repeat penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "because your client has repeatedly made incorrect declarations"
      }
    }

    "there is a Vat Inaccuracies Return Replaced Charge" should {

      val model = exampleModel.copy(chargeType = InaccuraciesReturnReplacedCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Inaccuracies penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "because your client submitted inaccurate information for" +
          " the period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a Vat Wrong Doing Penalty Charge" should {

      val model = exampleModel.copy(chargeType = WrongDoingPenaltyCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Wrongdoing penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "because your client charged VAT when they should not have done"
      }
    }

    "there is a Vat Credit Return Offset Charge Charge" should {

      val model = exampleModel.copy(chargeType = CreditReturnOffsetCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Overpayment partial refund"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "partial repayment for period 12 Jan to 23 Mar 2018"
      }
    }

    "there is an Unallocated Payment Charge Type" should {

      val model = exampleModel.copy(chargeType = UnallocatedPayment)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Unallocated payment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe
          "your client made an overpayment which can be refunded to them or left on account"
      }
    }

    "there is an Refund Charge Type" should {

      val model = exampleModel.copy(chargeType = Refund)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Refund payment from HMRC"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "as your client requested a refund on an overpayment they made"
      }
    }

    "there is a VAT POA Instalment charge" should {

      val model = exampleModel.copy(chargeType = PaymentOnAccountInstalments)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Payment on account instalment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT POA Return Debit charge" should {

      val model = exampleModel.copy(chargeType = PaymentOnAccountReturnDebitCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Payment on account balance"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT POA Return Credit charge" should {

      val model = exampleModel.copy(chargeType = PaymentOnAccountReturnCreditCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Payment on account repayment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT AA Monthly Instalment charge" should {

      val model = exampleModel.copy(chargeType = AAMonthlyInstalment)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Annual accounting monthly instalment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for the period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT AA Quarterly Instalment charge" should {

      val model = exampleModel.copy(chargeType = AAQuarterlyInstalments)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Annual accounting quarterly instalment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for the period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT AA Return Debit charge" should {

      val model = exampleModel.copy(chargeType = AAReturnDebitCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Annual accounting balance"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT AA Return Credit charge" should {

      val model = exampleModel.copy(chargeType = AAReturnCreditCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Annual accounting repayment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for the period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT Unrepayable Overpayment Charge" should {

      val model = exampleModel.copy(chargeType = VatUnrepayableOverpayment)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Overpayment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "cannot be repaid after 4 years"
      }
    }

    "there is a VAT return 1st LPP Charge" should {

      val model = exampleModel.copy(chargeType = VatReturn1stLPP)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Late payment penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for late payment of VAT for VAT period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT Return LPI charge" should {

      val model = exampleModel.copy(chargeType = VatReturnLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on VAT"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT PA LPI charge" should {

      val model = exampleModel.copy(chargeType = VatPALPICharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on protective assessment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT Return 1st LPP LPI charge" should {

      val model = exampleModel.copy(chargeType = VatReturn1stLPPLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT Return 2nd LPP LPI charge" should {

      val model = exampleModel.copy(chargeType = VatReturn2ndLPPLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on second penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT Central Assessment LPI charge" should {

      val model = exampleModel.copy(chargeType = VatCentralAssessmentLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on central assessment of VAT"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT CA 1st LPP LPI charge" should {

      val model = exampleModel.copy(chargeType = VatCA1stLPPLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on central assessment penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT CA 2nd LPP LPI charge" should {

      val model = exampleModel.copy(chargeType = VatCA2ndLPPLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on central assessment second penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT Officer's Assessment LPI charge" should {

      val model = exampleModel.copy(chargeType = VatOfficersAssessmentLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on officer’s assessment of VAT"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT OA 1st LPP LPI charge" should {

      val model = exampleModel.copy(chargeType = VatOA1stLPPLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on officer’s assessment penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
      }
    }

    "there is a VAT OA 2nd LPP LPI charge" should {

      val model = exampleModel.copy(chargeType = VatOA2ndLPPLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on officer’s assessment second penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
      }
    }
  }
}
