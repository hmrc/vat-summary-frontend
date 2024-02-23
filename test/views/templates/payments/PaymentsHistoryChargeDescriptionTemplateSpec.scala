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
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for the 12\u00a0Jan to 23\u00a0Mar\u00a02018 return"
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
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for late payment of the 12\u00a0Jan to 23\u00a0Mar\u00a02018 return"
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
          elementText(Selectors.description) shouldBe "for late payment of the 12\u00a0Jan to 23\u00a0Mar\u00a02018 return"
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
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a Civil Evasion Penalty charge" should {

        val model = exampleModel.copy(chargeType = CivilEvasionPenaltyCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "VAT civil evasion penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "because we have identified irregularities involving dishonesty"
        }
      }

      "there is a VAT Civil Evasion Penalty LPI charge" should {

        val model = exampleModel.copy(chargeType = VatCivilEvasionPenaltyLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on VAT civil evasion penalty"
        }

        "display no additional description" in {
          elementText(Selectors.description) shouldBe ""
        }
      }

      "there is a VAT Failure to Submit EC Sales charge" should {

        val model = exampleModel.copy(chargeType = VatFailureToSubmitECSalesCharge)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "EC sales list penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "because you have not submitted an EC sales list or you have submitted it late"
        }
      }

      "there is a VAT Failure to Submit EC Sales LPI charge" should {

        val model = exampleModel.copy(chargeType = VatFailureToSubmitECSalesChargeLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on EC sales list penalty"
        }

        "display no additional description" in {
          elementText(Selectors.description) shouldBe ""
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
            " for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
            " for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
            "because you submitted an inaccurate document for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a Vat Inaccuracy Assessments Pen LPI charge" should {

        val model = exampleModel.copy(chargeType = VatInaccuracyAssessPenLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on inaccuracies penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe
            "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a Vat OA Inaccur Penalty charge" should {

        val model = exampleModel.copy(chargeType = VatOAInaccuraciesFrom2009LPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on inaccuracies penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe  "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a Vat OA Inaccur Penalty LPI charge" should {

        val model = exampleModel.copy(chargeType = VatOAInaccuraciesFrom2009LPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on inaccuracies penalty"
        }

        "display no additional description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
            " the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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

      "there is a VAT Wrong Doing Penalty LPI" should {

        val model = exampleModel.copy(chargeType = VatWrongDoingPenaltyLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on wrongdoing penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe ""
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
          elementText(Selectors.description) shouldBe "partial repayment for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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

      "there is an VAT Migrated Credit Charge Type" should {

        val model = exampleModel.copy(chargeType = VatMigratedCredit)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "VAT migrated credit"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "miscellaneous VAT credit"
        }
      }

      "there is a VAT Miscellaneous Penalty LPI" should {

        val model = exampleModel.copy(chargeType = VATMiscellaneousPenaltyLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on VAT general penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe ""
        }
      }

      "there is an VAT Migrated Liability Charge Type" should {

        val model = exampleModel.copy(chargeType = VatMigratedLiability)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "VAT migrated liability"
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
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT Overpayment for Tax RPI Charge" should {

        val model = exampleModel.copy(chargeType = VatOverpaymentForTaxRPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Repayment interest on VAT"
        }
        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
        }
      }
      "there is a VAT Overpayment for Tax RPI Charge1" should {

        val model = exampleModel.copy(chargeType = VatOverpayments1stLPPRPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Repayment interest on penalty"
        }
        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
        }
      }
      "there is a VAT Overpayment for Tax RPI Charge2" should {

        val model = exampleModel.copy(chargeType = VatOverpayments2ndLPPRPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Repayment interest on second penalty"
        }
        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
        }
      }

      "there is a VAT Return RPI Charge" should {

        val model = exampleModel.copy(chargeType = VatReturnRPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Repayment interest on payment on account repayment"
        }
        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12 Jan to 23 Mar 2018"
        }
      }

      "this is a VAT Officer's Assessment RPI Charge" should {

        val model = exampleModel.copy(chargeType = VatOfficersAssessmentRPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Repayment interest on officer’s assessment of VAT"
        }
        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12 Jan to 23 Mar 2018"
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

      "there is a Vat Overpayments 2nd LPP Charge" should {

        val model = exampleModel.copy(chargeType = VatOverpayments2ndLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Second late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a Vat Overpayments 1st LPP Charge" should {

        val model = exampleModel.copy(chargeType = VatOverpayments1stLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT Central Assessment 1st LPP charge" should {

        val model = exampleModel.copy(chargeType = VatCentralAssessment1stLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT Central Assessment 2nd LPP charge" should {

        val model = exampleModel.copy(chargeType = VatCentralAssessment2ndLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Second late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT OA 1st LPP charge" should {

        val model = exampleModel.copy(chargeType = VatOfficersAssessment1stLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT OA 2nd LPP charge" should {

        val model = exampleModel.copy(chargeType = VatOfficersAssessment2ndLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Second late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT AA 1st LPP charge" should {

        val model = exampleModel.copy(chargeType = VatAA1stLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT AA 1st LPP LPI charge" should {

        val model = exampleModel.copy(chargeType = VatAA1stLPPLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on additional assessment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT AA 2nd LPP LPI charge" should {

        val model = exampleModel.copy(chargeType = VatAA2ndLPPLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on additional assessment second penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT AA 2nd LPP charge" should {

        val model = exampleModel.copy(chargeType = VatAA2ndLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Second late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT Additional Assessment LPI charge" should {

        val model = exampleModel.copy(chargeType = VatAdditionalAssessmentLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on additional assessment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT Late Submission Penalty charge" should {

        val model = exampleModel.copy(chargeType = VatLateSubmissionPen)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Late submission penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT LSP Interest charge" should {

        val model = exampleModel.copy(chargeType = VatLspInterest)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on late submission penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT Return AA 1st LPP LPI" should {

        val model = exampleModel.copy(chargeType = VatReturnAA1stLPPLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on annual accounting balance penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }
      "there is a VAT Return AA 2nd LPP LPI" should {

        val model = exampleModel.copy(chargeType = VatReturnAA2ndLPPLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on annual accounting balance second penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT POA Instalment LPI" should {
        val model = exampleModel.copy(chargeType = VatPOAInstalmentLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on payment on account instalment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT Return AA LPI" should {
        val model = exampleModel.copy(chargeType = VatAAReturnChargeLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on annual accounting balance"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT Manual LPP" should {

        val model = exampleModel.copy(chargeType = VatManualLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe ""
        }
      }

      "there is a VAT Manual LPP LPI" should {

        val model = exampleModel.copy(chargeType = VatManualLPPLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe ""
        }
      }

      "there is a VAT Manual LPP RPI" should {
        val model = exampleModel.copy(chargeType = VatManualLPPRPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Repayment interest on late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe ""
        }
      }

      "there is a VAT Return LPP1 RPI" should {
        val model = exampleModel.copy(chargeType = VatReturnLPP1RPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Repayment interest on penalty for late payment of VAT"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe ""
        }
      }
      "there is a VAT AA Quarterly Instal LPI" should {

        val model = exampleModel.copy(chargeType = VatAAQuarterlyInstalLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on annual accounting quarterly instalment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }
      "there is a VAT AA Monthly Instal LPI" should {

        val model = exampleModel.copy(chargeType = VatAAMonthlyInstalLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on annual accounting monthly instalment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }
      "there is a VAT AA Return Charge 1st LPP" should {

        val model = exampleModel.copy(chargeType = VatAAReturnCharge1stLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }
      "there is a VAT AA Return Charge 2nd LPP" should {

        val model = exampleModel.copy(chargeType = VatAAReturnCharge2ndLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Second late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }
      "there is a VAT Return 2nd LPP" should {
        val model = exampleModel.copy(chargeType = VatReturn2ndLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Second late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT Error Correction LPI" should {

        val model = exampleModel.copy(chargeType = VatErrorCorrectionLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on error correction"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT Error Correction 1st LPP" should {

        val model = exampleModel.copy(chargeType = VatErrorCorrection1stLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT Error Correction 2nd LPP" should {

        val model = exampleModel.copy(chargeType = VatErrorCorrection2ndLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Second late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT Error Correction 1st LPP LPI" should {

        val model = exampleModel.copy(chargeType = VatErrorCorrection1stLPPLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on error correction penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT Error Correction 2nd LPP LPI" should {

        val model = exampleModel.copy(chargeType = VatErrorCorrection2ndLPPLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on error correction second penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT Return POA LPI" should {
        val model = exampleModel.copy(chargeType = VatReturnPOALPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on payment on account"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT POA Return 1st LPP" should {
        val model = exampleModel.copy(chargeType = VatPOAReturn1stLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT POA Return 2nd LPP" should {
        val model = exampleModel.copy(chargeType = VatPOAReturn2ndLPP)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Second late payment penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT Return POA 1st LPP LPI" should {
        val model = exampleModel.copy(chargeType = VatReturnPOA1stLPPLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on payment on account penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }

      "there is a VAT Return POA 2nd LPP LPI" should {
        val model = exampleModel.copy(chargeType = VatReturnPOA2ndLPPLPI)
        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Interest on payment on account second penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
        }
      }
    }
  }

  "user is an agent" when {

    "there is a VAT Failure to Submit EC Sales charge" should {

      val model = exampleModel.copy(chargeType = VatFailureToSubmitECSalesCharge)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "EC sales list penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "because your client has not submitted an EC sales list or has submitted it late"
      }
    }

    "there is a VAT Failure to Submit EC Sales LPI charge" should {

      val model = exampleModel.copy(chargeType = VatFailureToSubmitECSalesChargeLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on EC sales list penalty"
      }

      "display no additional description" in {
        elementText(Selectors.description) shouldBe ""
      }
    }

    "there is a vat return debit charge" should {

      lazy val template = paymentsHistoryChargeDescription(exampleModel)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "VAT"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for the 12\u00a0Jan to 23\u00a0Mar\u00a02018 return"
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
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for late payment of the 12\u00a0Jan to 23\u00a0Mar\u00a02018 return"
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
        elementText(Selectors.description) shouldBe "for late payment of the 12\u00a0Jan to 23\u00a0Mar\u00a02018 return"
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
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          " for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          " for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          "because your client submitted an inaccurate document for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
          " the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "partial repayment for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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

    "there is an VAT Migrated Credit Charge Type" should {

      val model = exampleModel.copy(chargeType = VatMigratedCredit)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "VAT migrated credit"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "miscellaneous VAT credit"
      }
    }

    "there is an VAT Migrated Liability Charge Type" should {

      val model = exampleModel.copy(chargeType = VatMigratedLiability)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "VAT migrated liability"
      }
    }

    "there is a VAT Deferral Penalty charge" should {

      val model = exampleModel.copy(chargeType = VatDeferralPenalty)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for unpaid deferred VAT"
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
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for the period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
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
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT AA 1st LPP charge" should {

      val model = exampleModel.copy(chargeType = VatAA1stLPP)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Late payment penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT AA 2nd LPP charge" should {

      val model = exampleModel.copy(chargeType = VatAA2ndLPP)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Second late payment penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT Additional Assessment LPI charge" should {

      val model = exampleModel.copy(chargeType = VatAdditionalAssessmentLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on additional assessment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT PA 1st LPP charge" should {

      val model = exampleModel.copy(chargeType = VatPA1stLPP)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Late payment penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT PA 2nd LPP charge" should {

      val model = exampleModel.copy(chargeType = VatPA2ndLPP)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Second late payment penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT AA 1st LPP LPI charge" should {

      val model = exampleModel.copy(chargeType = VatAA1stLPPLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on additional assessment penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT AA 2nd LPP LPI charge" should {

      val model = exampleModel.copy(chargeType = VatAA2ndLPPLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on additional assessment second penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT Late Submission Penalty charge" should {

      val model = exampleModel.copy(chargeType = VatLateSubmissionPen)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Late submission penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT LSP Interest charge" should {

      val model = exampleModel.copy(chargeType = VatLspInterest)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on late submission penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT Return AA 1st LPP LPI" should {

      val model = exampleModel.copy(chargeType = VatReturnAA1stLPPLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on annual accounting balance penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }
    "there is a VAT Return AA 2nd LPP LPI" should {

      val model = exampleModel.copy(chargeType = VatReturnAA2ndLPPLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on annual accounting balance second penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT POA Instalment LPI" should {
      val model = exampleModel.copy(chargeType = VatPOAInstalmentLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on payment on account instalment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT Return AA LPI" should {
      val model = exampleModel.copy(chargeType = VatAAReturnChargeLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on annual accounting balance"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT Manual LPP" should {

      val model = exampleModel.copy(chargeType = VatManualLPP)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Late payment penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe ""
      }
    }
    "there is a VAT Manual LPP LPI" should {

      val model = exampleModel.copy(chargeType = VatManualLPPLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on late payment penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe ""
      }
    }
    "there is a VAT AA Quarterly Instal LPI" should {

      val model = exampleModel.copy(chargeType = VatAAQuarterlyInstalLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on annual accounting quarterly instalment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }
    "there is a VAT AA Monthly Instal LPI" should {

      val model = exampleModel.copy(chargeType = VatAAMonthlyInstalLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on annual accounting monthly instalment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }
    "there is a VAT AA Return Charge 1st LPP" should {

      val model = exampleModel.copy(chargeType = VatAAReturnCharge1stLPP)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Late payment penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT AA Return Charge 2nd LPP" should {

      val model = exampleModel.copy(chargeType = VatAAReturnCharge2ndLPP)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Second late payment penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }


    "there is a VAT Manual LPP RPI for agent to see" should {
      val model = exampleModel.copy(chargeType = VatManualLPPRPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Repayment interest on late payment penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe ""
      }
    }

    "there is a VAT Return LPP1 RPI for agent to see" should {
      val model = exampleModel.copy(chargeType = VatReturnLPP1RPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Repayment interest on penalty for late payment of VAT"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe ""
      }
    }
    "there is a VAT Return 2nd LPP" should {
      val model = exampleModel.copy(chargeType = VatReturn2ndLPP)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Second late payment penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT Error Correction LPI" should {

      val model = exampleModel.copy(chargeType = VatErrorCorrectionLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on error correction"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT Error Correction 1st LPP" should {

      val model = exampleModel.copy(chargeType = VatErrorCorrection1stLPP)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Late payment penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT Error Correction 2nd LPP" should {

      val model = exampleModel.copy(chargeType = VatErrorCorrection2ndLPP)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Second late payment penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT Error Correction 1st LPP LPI" should {

      val model = exampleModel.copy(chargeType = VatErrorCorrection1stLPPLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on error correction penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT Error Correction 2nd LPP LPI" should {

      val model = exampleModel.copy(chargeType = VatErrorCorrection2ndLPPLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on error correction second penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT Return POA LPI" should {
      val model = exampleModel.copy(chargeType = VatReturnPOALPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on payment on account"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT POA Return 1st LPP" should {
      val model = exampleModel.copy(chargeType = VatPOAReturn1stLPP)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Late payment penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT POA Return 2nd LPP" should {
      val model = exampleModel.copy(chargeType = VatPOAReturn2ndLPP)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Second late payment penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT Return POA 1st LPP LPI" should {
      val model = exampleModel.copy(chargeType = VatReturnPOA1stLPPLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on payment on account penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT Return POA 2nd LPP LPI" should {
      val model = exampleModel.copy(chargeType = VatReturnPOA2ndLPPLPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Interest on payment on account second penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT Return AA RPI" should {
      val model = exampleModel.copy(chargeType = VatAAReturnChargeRPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Repayment interest on annual accounting repayment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT Error Correction RPI" should {
      val model = exampleModel.copy(chargeType = VatErrorCorrectionRPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Repayment interest on error correction repayment"
      }
      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }

    "there is a VAT Return  POA RPI" should {
      val model = exampleModel.copy(chargeType = VatReturnPOARPI)
      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Repayment interest on payment on account balance"
      }
      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for period 12\u00a0Jan to 23\u00a0Mar\u00a02018"
      }
    }
  }
}
