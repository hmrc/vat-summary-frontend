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
    val chargeTitle = "span:nth-of-type(1)"
    val description = "span:nth-of-type(2)"
  }

  "The PaymentsHistoryChargeDescription template" when {

    "user is not an agent" when {

      "there is a vat return debit charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          ReturnDebitCharge,
          Some(LocalDate.parse("2018-01-12")),
          Some(LocalDate.parse("2018-03-23")),
          123456,
          Some(LocalDate.parse("2018-02-14"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Return"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for the period 12 Jan to 23 Mar 2018"
        }
      }

      "there is a vat return credit charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          ReturnCreditCharge,
          Some(LocalDate.parse("2018-01-12")),
          Some(LocalDate.parse("2018-03-23")),
          -123456,
          Some(LocalDate.parse("2018-02-14"))
        )

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

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          OADebitCharge,
          Some(LocalDate.parse("2018-01-12")),
          Some(LocalDate.parse("2018-03-23")),
          123456,
          Some(LocalDate.parse("2018-02-14"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "VAT officer’s assessment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for underpaying by this amount"
        }
      }

      "there is a vat officer assessment credit charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          OACreditCharge,
          Some(LocalDate.parse("2018-01-12")),
          Some(LocalDate.parse("2018-03-23")),
          123456,
          Some(LocalDate.parse("2018-02-14"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "VAT officer’s assessment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for overpaying by this amount"
        }
      }

      "there is a vat central assessment charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          CentralAssessmentCharge,
          Some(LocalDate.parse("2018-01-12")),
          Some(LocalDate.parse("2018-03-23")),
          -123456,
          Some(LocalDate.parse("2018-02-14"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Estimate"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for the 12 Jan to 23 Mar 2018 return"
        }
      }

      "there is a VAT Debit Default Surcharge charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          DebitDefaultSurcharge,
          Some(LocalDate.parse("2018-01-12")),
          Some(LocalDate.parse("2018-03-23")),
          123456,
          Some(LocalDate.parse("2018-02-14"))
        )

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

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          CreditDefaultSurcharge,
          Some(LocalDate.parse("2018-01-12")),
          Some(LocalDate.parse("2018-03-23")),
          -123456,
          Some(LocalDate.parse("2018-02-14"))
        )

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

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          ErrorCorrectionCreditCharge,
          Some(LocalDate.parse("2018-01-12")),
          Some(LocalDate.parse("2018-03-23")),
          1000,
          Some(LocalDate.parse("2018-02-14"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Error correction repayment from HMRC"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for correcting the 12 Jan to 23 Mar 2018 return"
        }
      }

      "there is an error correction debit charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          ErrorCorrectionDebitCharge,
          Some(LocalDate.parse("2018-01-12")),
          Some(LocalDate.parse("2018-03-23")),
          2000,
          Some(LocalDate.parse("2018-02-14"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Error correction payment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for correcting the 12 Jan to 23 Mar 2018 return"
        }
      }

      "there is a vat officer assessment default interest charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          OADefaultInterestCharge,
          Some(LocalDate.parse("2018-01-12")),
          Some(LocalDate.parse("2018-03-23")),
          123456,
          Some(LocalDate.parse("2018-02-14"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "VAT officer’s assessment interest"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "interest charged on the officer’s assessment"
        }
      }

      "there is a VAT Officer Assessment Further Interest charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          OAFurtherInterestCharge,
          Some(LocalDate.parse("2018-02-12")),
          Some(LocalDate.parse("2018-03-24")),
          1500.00,
          Some(LocalDate.parse("2018-04-18"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "VAT officer’s assessment further interest"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "further interest charged on the officer’s assessment"
        }
      }

      "there is a VAT Additional Assessment charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          AACharge,
          Some(LocalDate.parse("2018-01-01")),
          Some(LocalDate.parse("2018-04-01")),
          2000.00,
          Some(LocalDate.parse("2018-05-01"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Additional assessment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "additional assessment based on further information" +
            " for the period 1 Jan to 1 Apr 2018"
        }
      }

      "there is a VAT AA Default Interest charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          AAInterestCharge,
          Some(LocalDate.parse("2018-01-01")),
          Some(LocalDate.parse("2018-04-01")),
          2000.00,
          Some(LocalDate.parse("2018-05-01"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Additional assessment interest"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "interest charged on additional tax assessed" +
            " for the period 1 Jan to 1 Apr 2018"
        }
      }

      "there is a VAT AA Further Interest charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          AAFurtherInterestCharge,
          Some(LocalDate.parse("2018-01-01")),
          Some(LocalDate.parse("2018-04-01")),
          2000.00,
          Some(LocalDate.parse("2018-05-01"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Additional assessment further interest"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "further interest charged on additional tax assessed" +
            " for the period 1 Jan to 1 Apr 2018"
        }
      }

      "there is a VAT Statutory Interest charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          StatutoryInterestCharge,
          Some(LocalDate.parse("2018-01-01")),
          Some(LocalDate.parse("2018-04-01")),
          -1500.00,
          Some(LocalDate.parse("2018-05-01"))
        )

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

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          InaccuraciesAssessmentsPenCharge,
          Some(LocalDate.parse("2018-09-10")),
          Some(LocalDate.parse("2018-10-11")),
          1000.00,
          Some(LocalDate.parse("2018-10-15"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Inaccuracies penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "because you submitted an inaccurate document for the period 10 Sep to 11 Oct 2018"
        }
      }

      "there is a Vat Mp Pre 2009 Charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          MpPre2009Charge,
          Some(LocalDate.parse("2018-09-10")),
          Some(LocalDate.parse("2018-10-11")),
          1100.00,
          Some(LocalDate.parse("2018-10-15"))
        )

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

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          MpRepeatedPre2009Charge,
          Some(LocalDate.parse("2018-09-10")),
          Some(LocalDate.parse("2018-10-11")),
          1100.00,
          Some(LocalDate.parse("2018-10-15"))
        )

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

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          InaccuraciesReturnReplacedCharge,
          Some(LocalDate.parse("2018-09-12")),
          Some(LocalDate.parse("2018-10-13")),
          390.00,
          Some(LocalDate.parse("2018-10-16"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Inaccuracies penalty"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "because you have submitted inaccurate information for" +
            " the period 12 Sep to 13 Oct 2018"
        }
      }

      "there is a Vat Wrong Doing Penalty Charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          WrongDoingPenaltyCharge,
          Some(LocalDate.parse("2018-09-12")),
          Some(LocalDate.parse("2018-10-13")),
          390.00,
          Some(LocalDate.parse("2018-10-16"))
        )

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

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          CreditReturnOffsetCharge,
          Some(LocalDate.parse("2018-09-12")),
          Some(LocalDate.parse("2018-10-13")),
          390.00,
          Some(LocalDate.parse("2018-10-16"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Overpayment partial refund"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "partial repayment for period 12 Sep to 13 Oct 2018"
        }
      }

      "there is an Unallocated Payment Charge Type" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          UnallocatedPayment,
          None,
          None,
          -500,
          Some(LocalDate.parse("2018-10-16"))
        )

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

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          Refund,
          None,
          None,
          500,
          Some(LocalDate.parse("2018-10-16"))
        )

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

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          PaymentOnAccountInstalments,
          Some(LocalDate.parse("2018-01-01")),
          Some(LocalDate.parse("2018-02-02")),
          500,
          Some(LocalDate.parse("2018-10-16"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Payment on account instalment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for the period 1 Jan to 2 Feb 2018"
        }
      }

      "there is a VAT POA Return Debit charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          PaymentOnAccountReturnDebitCharge,
          Some(LocalDate.parse("2018-01-01")),
          Some(LocalDate.parse("2018-02-02")),
          500,
          Some(LocalDate.parse("2018-10-16"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Payment on account balance"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for the period 1 Jan to 2 Feb 2018"
        }
      }

      "there is a VAT POA Return Credit charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          PaymentOnAccountReturnCreditCharge,
          Some(LocalDate.parse("2018-01-01")),
          Some(LocalDate.parse("2018-02-02")),
          500,
          Some(LocalDate.parse("2018-10-16"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Payment on account repayment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for the period 1 Jan to 2 Feb 2018"
        }
      }

      "there is a VAT AA Monthly Instalment charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          AAMonthlyInstalment,
          Some(LocalDate.parse("2018-01-01")),
          Some(LocalDate.parse("2018-02-02")),
          500,
          Some(LocalDate.parse("2018-10-16"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Annual accounting monthly instalment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for the period 1 Jan to 2 Feb 2018"
        }
      }

      "there is a VAT AA Quarterly Instalment charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          AAQuarterlyInstalments,
          Some(LocalDate.parse("2018-01-01")),
          Some(LocalDate.parse("2018-02-02")),
          500,
          Some(LocalDate.parse("2018-10-16"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Annual accounting quarterly instalment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for the period 1 Jan to 2 Feb 2018"
        }
      }

      "there is a VAT AA Return Debit charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          AAReturnDebitCharge,
          Some(LocalDate.parse("2018-01-01")),
          Some(LocalDate.parse("2018-02-02")),
          500,
          Some(LocalDate.parse("2018-10-16"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Annual accounting balance"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for the period 1 Jan to 2 Feb 2018"
        }
      }

      "there is a VAT AA Return Credit charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          AAReturnCreditCharge,
          Some(LocalDate.parse("2018-01-01")),
          Some(LocalDate.parse("2018-02-02")),
          500,
          Some(LocalDate.parse("2018-10-16"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Annual accounting repayment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "for the period 1 Jan to 2 Feb 2018"
        }
      }

      "there is a VAT Unrepayable Overpayment Charge" should {

        val model: PaymentsHistoryModel = PaymentsHistoryModel(
          VatUnrepayableOverpayment,
          Some(LocalDate.parse("2018-03-02")),
          Some(LocalDate.parse("2018-04-02")),
          300,
          Some(LocalDate.parse("2018-10-16"))
        )

        lazy val template = paymentsHistoryChargeDescription(model)
        lazy implicit val document: Document = Jsoup.parse(template.body)

        "display the correct charge title" in {
          elementText(Selectors.chargeTitle) shouldBe "Overpayment"
        }

        "display the correct description" in {
          elementText(Selectors.description) shouldBe "cannot be repaid after 4 years"
        }
      }
    }
  }

  "user is an agent" when {

    "there is a vat return debit charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        ReturnDebitCharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        123456,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Return"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for the period 12 Jan to 23 Mar 2018"
      }
    }

    "there is a vat return credit charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        ReturnCreditCharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        -123456,
        Some(LocalDate.parse("2018-02-14"))
      )

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

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        OADebitCharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        123456,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "VAT officer’s assessment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for underpaying by this amount"
      }
    }

    "there is a vat officer assessment credit charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        OACreditCharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        123456,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "VAT officer’s assessment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for overpaying by this amount"
      }
    }

    "there is a vat central assessment charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        CentralAssessmentCharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        -123456,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Estimate"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for the 12 Jan to 23 Mar 2018 return"
      }
    }

    "there is a VAT Debit Default Surcharge charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        DebitDefaultSurcharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        123456,
        Some(LocalDate.parse("2018-02-14"))
      )

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

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        CreditDefaultSurcharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        -123456,
        Some(LocalDate.parse("2018-02-14"))
      )

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

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        ErrorCorrectionCreditCharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        1000,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Error correction repayment from HMRC"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for correcting the 12 Jan to 23 Mar 2018 return"
      }
    }

    "there is an error correction debit charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        ErrorCorrectionDebitCharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        2000,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Error correction payment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for correcting the 12 Jan to 23 Mar 2018 return"
      }
    }

    "there is a vat officer assessment default interest charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        OADefaultInterestCharge,
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        123456,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "VAT officer’s assessment interest"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "interest charged on the officer’s assessment"
      }
    }

    "there is a VAT Officer Assessment Further Interest charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        OAFurtherInterestCharge,
        Some(LocalDate.parse("2018-02-12")),
        Some(LocalDate.parse("2018-03-24")),
        1500.00,
        Some(LocalDate.parse("2018-04-18"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "VAT officer’s assessment further interest"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "further interest charged on the officer’s assessment"
      }
    }

    "there is a VAT Additional Assessment charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        AACharge,
        Some(LocalDate.parse("2018-01-01")),
        Some(LocalDate.parse("2018-04-01")),
        2000.00,
        Some(LocalDate.parse("2018-05-01"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Additional assessment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "additional assessment based on further information" +
          " for the period 1 Jan to 1 Apr 2018"
      }
    }

    "there is a VAT AA Default Interest charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        AAInterestCharge,
        Some(LocalDate.parse("2018-01-01")),
        Some(LocalDate.parse("2018-04-01")),
        2000.00,
        Some(LocalDate.parse("2018-05-01"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Additional assessment interest"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "interest charged on additional tax assessed" +
          " for the period 1 Jan to 1 Apr 2018"
      }
    }

    "there is a VAT AA Further Interest charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        AAFurtherInterestCharge,
        Some(LocalDate.parse("2018-01-01")),
        Some(LocalDate.parse("2018-04-01")),
        2000.00,
        Some(LocalDate.parse("2018-05-01"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Additional assessment further interest"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "further interest charged on additional tax assessed" +
          " for the period 1 Jan to 1 Apr 2018"
      }
    }

    "there is a VAT Statutory Interest charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        StatutoryInterestCharge,
        Some(LocalDate.parse("2018-01-01")),
        Some(LocalDate.parse("2018-04-01")),
        -1500.00,
        Some(LocalDate.parse("2018-05-01"))
      )

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

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        InaccuraciesAssessmentsPenCharge,
        Some(LocalDate.parse("2018-09-10")),
        Some(LocalDate.parse("2018-10-11")),
        1000.00,
        Some(LocalDate.parse("2018-10-15"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Inaccuracies penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "because your client submitted an inaccurate document for the period 10 Sep to 11 Oct 2018"
      }
    }

    "there is a Vat Mp Pre 2009 Charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        MpPre2009Charge,
        Some(LocalDate.parse("2018-09-10")),
        Some(LocalDate.parse("2018-10-11")),
        1100.00,
        Some(LocalDate.parse("2018-10-15"))
      )

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

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        MpRepeatedPre2009Charge,
        Some(LocalDate.parse("2018-09-10")),
        Some(LocalDate.parse("2018-10-11")),
        1100.00,
        Some(LocalDate.parse("2018-10-15"))
      )

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

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        InaccuraciesReturnReplacedCharge,
        Some(LocalDate.parse("2018-09-12")),
        Some(LocalDate.parse("2018-10-13")),
        390.00,
        Some(LocalDate.parse("2018-10-16"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Inaccuracies penalty"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "because your client submitted inaccurate information for" +
          " the period 12 Sep to 13 Oct 2018"
      }
    }

    "there is a Vat Wrong Doing Penalty Charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        WrongDoingPenaltyCharge,
        Some(LocalDate.parse("2018-09-12")),
        Some(LocalDate.parse("2018-10-13")),
        390.00,
        Some(LocalDate.parse("2018-10-16"))
      )

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

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        CreditReturnOffsetCharge,
        Some(LocalDate.parse("2018-09-12")),
        Some(LocalDate.parse("2018-10-13")),
        390.00,
        Some(LocalDate.parse("2018-10-16"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Overpayment partial refund"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "partial repayment for period 12 Sep to 13 Oct 2018"
      }
    }

    "there is an Unallocated Payment Charge Type" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        UnallocatedPayment,
        None,
        None,
        -500,
        Some(LocalDate.parse("2018-10-16"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Unallocated payment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "your client made an overpayment which can be refunded to them or left on account"
      }
    }

    "there is an Refund Charge Type" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        Refund,
        None,
        None,
        500,
        Some(LocalDate.parse("2018-10-16"))
      )

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

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        PaymentOnAccountInstalments,
        Some(LocalDate.parse("2018-01-01")),
        Some(LocalDate.parse("2018-02-02")),
        500,
        Some(LocalDate.parse("2018-10-16"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Payment on account instalment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for the period 1 Jan to 2 Feb 2018"
      }
    }

    "there is a VAT POA Return Debit charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        PaymentOnAccountReturnDebitCharge,
        Some(LocalDate.parse("2018-01-01")),
        Some(LocalDate.parse("2018-02-02")),
        500,
        Some(LocalDate.parse("2018-10-16"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Payment on account balance"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for the period 1 Jan to 2 Feb 2018"
      }
    }

    "there is a VAT POA Return Credit charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        PaymentOnAccountReturnCreditCharge,
        Some(LocalDate.parse("2018-01-01")),
        Some(LocalDate.parse("2018-02-02")),
        500,
        Some(LocalDate.parse("2018-10-16"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Payment on account repayment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for the period 1 Jan to 2 Feb 2018"
      }
    }

    "there is a VAT AA Monthly Instalment charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        AAMonthlyInstalment,
        Some(LocalDate.parse("2018-01-01")),
        Some(LocalDate.parse("2018-02-02")),
        500,
        Some(LocalDate.parse("2018-10-16"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Annual accounting monthly instalment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for the period 1 Jan to 2 Feb 2018"
      }
    }

    "there is a VAT AA Quarterly Instalment charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        AAQuarterlyInstalments,
        Some(LocalDate.parse("2018-01-01")),
        Some(LocalDate.parse("2018-02-02")),
        500,
        Some(LocalDate.parse("2018-10-16"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Annual accounting quarterly instalment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for the period 1 Jan to 2 Feb 2018"
      }
    }

    "there is a VAT AA Return Debit charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        AAReturnDebitCharge,
        Some(LocalDate.parse("2018-01-01")),
        Some(LocalDate.parse("2018-02-02")),
        500,
        Some(LocalDate.parse("2018-10-16"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Annual accounting balance"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for the period 1 Jan to 2 Feb 2018"
      }
    }

    "there is a VAT AA Return Credit charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        AAReturnCreditCharge,
        Some(LocalDate.parse("2018-01-01")),
        Some(LocalDate.parse("2018-02-02")),
        500,
        Some(LocalDate.parse("2018-10-16"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Annual accounting repayment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "for the period 1 Jan to 2 Feb 2018"
      }
    }

    "there is a VAT Unrepayable Overpayment Charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        VatUnrepayableOverpayment,
        Some(LocalDate.parse("2018-03-02")),
        Some(LocalDate.parse("2018-04-02")),
        300,
        Some(LocalDate.parse("2018-10-16"))
      )

      lazy val template = paymentsHistoryChargeDescription(model)(messages, agentUser)
      lazy implicit val document: Document = Jsoup.parse(template.body)

      "display the correct charge title" in {
        elementText(Selectors.chargeTitle) shouldBe "Overpayment"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "cannot be repaid after 4 years"
      }
    }
  }
}
