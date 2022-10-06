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

package controllers

import common.TestModels._
import models.User
import models.errors.PaymentsError
import models.payments._
import models.viewModels._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.errors.PaymentsError
import views.html.payments.{NoPayments, WhatYouOwe}
import java.time.LocalDate

class WhatYouOweControllerSpec extends ControllerBaseSpec {

  val whatYouOwe: WhatYouOwe = injector.instanceOf[WhatYouOwe]
  val mockPaymentsError: PaymentsError = injector.instanceOf[PaymentsError]
  val noPayments: NoPayments = injector.instanceOf[NoPayments]
  implicit val user: User = User("111111111")
  implicit val hc: HeaderCarrier = HeaderCarrier()

  val controller = new WhatYouOweController(
    authorisedController,
    mockDateService,
    mockPaymentsService,
    mockServiceInfoService,
    mcc,
    mockPaymentsError,
    whatYouOwe,
    noPayments,
    mockAccountDetailsService,
    mockPenaltyDetailsService,
    mockWYOSessionService
  )

  "The WhatYouOweController .show method" when {

    "a principal user is authenticated" when {

      "the user has open payments" when {

        lazy val result = {
          mockPrincipalAuth()
          mockServiceInfoCall()
          mockOpenPayments(Right(Some(Payments(Seq(payment, payment)))))
          mockCustomerInfo(Right(customerInformationMax))
          mockCustomerInfo(Right(customerInformationMax))
          mockDateServiceCall()
          mockPenaltyDetailsServiceCall()
          mockWYOSessionServiceCall()
          controller.show(fakeRequest)
        }

        "return OK" in {
          status(result) shouldBe OK
        }

        "return the correct content" in {
          Jsoup.parse(contentAsString(result)).title() shouldBe "What you owe - Manage your VAT account - GOV.UK"
        }

      }

      "the user has no open payments" should {

        lazy val result = {
          mockPrincipalAuth()
          mockServiceInfoCall()
          mockOpenPayments(Right(None))
          mockCustomerInfo(Right(customerInformationMax))
          mockCustomerInfo(Right(customerInformationMax))
          mockDateServiceCall()
          mockPenaltyDetailsServiceCall()
          controller.show(fakeRequest)
        }

        "return OK (200)" in {
          status(result) shouldBe OK
        }

        "return the no payments view" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.select("h1").first().text() shouldBe "What you owe"
        }

      }

      "the payments call is unsuccessful" should {

        lazy val result = {
          mockPrincipalAuth()
          mockServiceInfoCall()
          mockOpenPayments(Left(PaymentsError))
          mockCustomerInfo(Right(customerInformationMax))
          mockCustomerInfo(Right(customerInformationMax))
          mockDateServiceCall()
          mockPenaltyDetailsServiceCall()
          controller.show(fakeRequest)
        }

        "return ISE (500)" in {
          status(result) shouldBe INTERNAL_SERVER_ERROR
        }

        "return the payments error view" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.select("h1").first().text() shouldBe "Sorry, there is a problem with the service"
        }
      }

      "the view model cannot be built due to invalid or missing financial data" should {

        val invalidCharge = payment.copy(penaltyType = Some("LPP9"))

        lazy val result = {
          mockPrincipalAuth()
          mockServiceInfoCall()
          mockOpenPayments(Right(Some(Payments(Seq(invalidCharge)))))
          mockCustomerInfo(Right(customerInformationMax))
          mockCustomerInfo(Right(customerInformationMax))
          mockDateServiceCall()
          mockPenaltyDetailsServiceCall()
          controller.show(fakeRequest)
        }

        "return ISE (500)" in {
          status(result) shouldBe INTERNAL_SERVER_ERROR
        }

        "return the payments error view" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.select("h1").first().text() shouldBe "Sorry, there is a problem with the service"
        }
      }
    }

    "an agent user is authenticated" should {

      lazy val result = {
        mockAgentAuth()
        mockServiceInfoCall()
        mockOpenPayments(Right(Some(Payments(Seq(payment, payment)))))
        mockCustomerInfo(Right(customerInformationMax))
        mockDateServiceCall()
        mockPenaltyDetailsServiceCall()
        mockWYOSessionServiceCall()
        controller.show(agentFinancialRequest)
      }

      "return OK" in {
        status(result) shouldBe OK
      }

      "return the payments view" in {
        val document: Document = Jsoup.parse(contentAsString(result))
        document.select("h1").first().text() shouldBe "What your client owes"
      }
    }

    "the user is not authenticated" should {

      lazy val result = controller.show(agentFinancialRequest)

      "return 403" in {
        mockInsufficientEnrolments()
        status(result) shouldBe FORBIDDEN
      }
    }

    "the user is not signed in" should {

      lazy val result = {
        mockMissingBearerToken()
        controller.show(fakeRequest)
      }

      "return 303" in {
        status(result) shouldBe SEE_OTHER
      }

      "redirect to sign in" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
      }
    }

    "the user is insolvent and not continuing to trade" should {

      "return 403" in {
        val result = {
          mockPrincipalAuth()
          controller.show(insolventRequest)
        }
        status(result) shouldBe FORBIDDEN
      }
    }

  }

  "The constructViewModel method" when {

    "there is a payment with the charge description and accrued interest defined" should {

      "return a view model with 2 charge models including an estimated interest charge and the correct total amount" in {
        val charge = payment.copy(accruedPenaltyAmount = None)
        val result = {
          mockDateServiceCall()
          controller.constructViewModel(Seq(charge), mandationStatus = "MTDfB", Seq(LPPDetailsModelMax))
        }
        result shouldBe Some(whatYouOweViewModelWithEstimatedInterest)
      }

    }

    "there is a payment with a charge type that can have estimated interest but accruedInterest is 0" should {

      "return the correct view model with 1 charge model" in {
        val result = {
          mockDateServiceCall()
          controller.constructViewModel(Seq(paymentNoAccInterest), mandationStatus = "MTDfB", Seq(LPPDetailsModelMax))
        }
        result shouldBe Some(whatYouOweViewModel.copy(charges = Seq(whatYouOweChargeModel)))
      }
    }

    "there is a payment that has accrued interest defined but estimated interest is not supported" should {

      "return the correct view model with 1 charge model" in {
        val result = {
          mockDateServiceCall()
          controller.constructViewModel(Seq(unrepayableOverpayment), mandationStatus = "MTDfB", Seq(LPPDetailsModelMax))
        }
        result shouldBe Some(whatYouOweViewModel.copy(charges = Seq(wyoChargeUnrepayableOverpayment)))
      }
    }

    "there are multiple payments with a mix of estimated & crystallised interest, LSP and penalties" should {

      val crystallisedInterest = {
        payment.copy(chargeType = VatReturnLPI, accruedInterestAmount = None, accruedPenaltyAmount = None)
      }
      val lateSubmissionPenalty = {
        payment.copy(chargeType = VatLateSubmissionPen, accruedInterestAmount = None, accruedPenaltyAmount = None, penaltyType = None)
      }
      lazy val result = {
        mockDateServiceCall()
        controller.constructViewModel(
          Seq(payment, crystallisedInterest, lateSubmissionPenalty),
          mandationStatus = "MTDfB",
          Seq(LPPDetailsModelMax)
        )
      }

      "return the expected view models and total amount" in {
        result shouldBe Some(whatYouOweViewModelMultipleTypes)
      }
    }

    "an interest payment doesn't have the charge reference defined" should {

      "not build a view model" in {
        val result = {
          mockDateServiceCall()
          controller.constructViewModel(
            Seq(payment.copy(chargeReference = None, chargeType = VatReturn1stLPPLPI)),
            mandationStatus = "MTDfB",
            Seq(LPPDetailsModelMax)
          )
        }
        result shouldBe None
      }
    }

    "description() cannot retrieve a charge description" should {

      "return a charge model with an empty string as a description" in {
        val result = {
          mockDateServiceCall()
          controller.constructViewModel(
            Seq(payment.copy(chargeType = MiscPenaltyCharge)), mandationStatus = "MTDfB", Seq(LPPDetailsModelMax)
          )
        }
        result shouldBe Some(viewModelNoChargeDescription)
      }
    }
  }

  "The findPenaltyCharge method" when {

    val penaltyType = Some("LPP1")

    "the penalty is an estimate" when {

      val chargeReference = Some("ABCDEFGHIJKLMNOP")

      "there is a penalty with a matching principal charge reference and penalty type" should {

        "return that penalty" in {
          val res = controller.findPenaltyCharge(chargeReference, penaltyType, isEstimate = true, Seq(LPPDetailsModelMin))
          res shouldBe Some(LPPDetailsModelMin)
        }
      }

      "the principal charge reference does not match" should {

        "return None" in {
          val res = controller.findPenaltyCharge(Some("FLJDHGKDJFH"), penaltyType, isEstimate = true, Seq(LPPDetailsModelMin))
          res shouldBe None
        }
      }

      "the penalty charge reference matches" should {

        "return None" in {
          val res = controller.findPenaltyCharge(Some("BCDEFGHIJKLMNOPQ"), penaltyType, isEstimate = true, Seq(LPPDetailsModelMax))
          res shouldBe None
        }
      }

      "the penalty type does not match" should {

        "return None" in {
          val res = controller.findPenaltyCharge(chargeReference, Some("LPP2"), isEstimate = true, Seq(LPPDetailsModelMin))
          res shouldBe None
        }
      }

      "a charge reference is not provided" should {

        "return None" in {
          val res = controller.findPenaltyCharge(None, penaltyType, isEstimate = true, Seq(LPPDetailsModelMin))
          res shouldBe None
        }
      }

      "a penalty type is not provided" should {

        "return None" in {
          val res = controller.findPenaltyCharge(chargeReference, None, isEstimate = true, Seq(LPPDetailsModelMin))
          res shouldBe None
        }
      }

      "an empty list of penalties is provided" should {

        "return None" in {
          val res = controller.findPenaltyCharge(chargeReference, penaltyType, isEstimate = true, Seq())
          res shouldBe None
        }
      }
    }

    "the penalty is crystallised" when {

      val chargeReference = Some("BCDEFGHIJKLMNOPQ")

      "there is a penalty with a matching penalty charge reference and penalty type" should {

        "return that penalty" in {
          val res = controller.findPenaltyCharge(chargeReference, penaltyType, isEstimate = false, Seq(LPPDetailsModelMax))
          res shouldBe Some(LPPDetailsModelMax)
        }
      }

      "the penalty charge reference does not match" should {

        "return None" in {
          val res = controller.findPenaltyCharge(Some("FLJDHGKDJFH"), penaltyType, isEstimate = false, Seq(LPPDetailsModelMax))
          res shouldBe None
        }
      }

      "the principal charge reference matches" should {

        "return None" in {
          val res = controller.findPenaltyCharge(Some("XD002750002155"), penaltyType, isEstimate = false, Seq(LPPDetailsModelMax))
          res shouldBe None
        }
      }

      "the penalty type does not match" should {

        "return None" in {
          val res = controller.findPenaltyCharge(chargeReference, Some("LPP2"), isEstimate = false, Seq(LPPDetailsModelMax))
          res shouldBe None
        }
      }

      "a charge reference is not provided" should {

        "return None" in {
          val res = controller.findPenaltyCharge(None, penaltyType, isEstimate = false, Seq(LPPDetailsModelMax))
          res shouldBe None
        }
      }

      "a penalty type is not provided" should {

        "return None" in {
          val res = controller.findPenaltyCharge(chargeReference, None, isEstimate = false, Seq(LPPDetailsModelMax))
          res shouldBe None
        }
      }

      "an empty list of penalties is provided" should {

        "return None" in {
          val res = controller.findPenaltyCharge(chargeReference, penaltyType, isEstimate = false, Seq())
          res shouldBe None
        }
      }
    }
  }

  "The buildChargePlusEstimates function" when {

    "the charge has accruing interest, accruing penalty and matching penalty details" should {

      "return three charges" in {
        mockDateServiceCall()
        controller.buildChargePlusEstimates(payment, Seq(LPPDetailsModelMax)).size shouldBe 3
      }
    }

    "the charge has accruing penalty and matching penalty details" should {

      "return two charges" in {
        mockDateServiceCall()
        val charge = payment.copy(accruedInterestAmount = None)
        controller.buildChargePlusEstimates(charge, Seq(LPPDetailsModelMax)).size shouldBe 2
      }
    }

    "the charge has accruing penalty but no matching penalty details" should {

      "return one charge" in {
        mockDateServiceCall()
        val charge = payment.copy(accruedInterestAmount = None)
        controller.buildChargePlusEstimates(charge, Seq()).size shouldBe 1
      }
    }

    "the charge has accruing interest" should {

      "return two charges" in {
        mockDateServiceCall()
        val charge = payment.copy(accruedPenaltyAmount = None)
        controller.buildChargePlusEstimates(charge, Seq()).size shouldBe 2
      }
    }

    "the charge has nothing accruing" should {

      "return one charge" in {
        mockDateServiceCall()
        val charge = payment.copy(accruedInterestAmount = None, accruedPenaltyAmount = None)
        controller.buildChargePlusEstimates(charge, Seq()).size shouldBe 1
      }
    }
  }

  "The buildStandardChargeViewModel function" should {

    "return a StandardChargeViewModel" in {
        mockDateServiceCall()
        controller.buildStandardChargeViewModel(payment) shouldBe Some(StandardChargeViewModel(
          "VAT Return Debit Charge",
          10000,
          10000,
          0,
          LocalDate.parse("2019-03-03"),
          Some("ABCD"),
          isOverdue = false,
          Some("XD002750002155"),
          Some(LocalDate.parse("2019-01-01")),
          Some(LocalDate.parse("2019-02-02"))
        ))
      }
    }

  "The buildCrystallisedIntViewModel function" should {

    "return a CrystallisedInterestViewModel" when {

      "chargeReference is present" in {
        mockDateServiceCall()
        val charge = payment.copy(chargeType = VatReturnLPI)
        controller.buildCrystallisedIntViewModel(charge) shouldBe Some(CrystallisedInterestViewModel(
          LocalDate.parse("2019-01-01"),
          LocalDate.parse("2019-02-02"),
          "VAT Return LPI",
          5.00,
          LocalDate.parse("2019-03-03"),
          10000,
          0,
          10000,
          isOverdue = false,
          "XD002750002155",
          isPenalty = false
        ))
      }
    }

    "return None" when {

      "chargeReference is missing" in {
        val charge = payment.copy(chargeType = VatReturnLPI, chargeReference = None)
        controller.buildCrystallisedIntViewModel(charge) shouldBe None
      }
    }
  }

  "The buildEstimatedIntViewModel function" should {

    "return a EstimatedInterestViewModel" when {

      "accruedInterestAmount is present" in {
        controller.buildEstimatedIntViewModel(payment) shouldBe Some(EstimatedInterestViewModel(
          LocalDate.parse("2019-01-01"),
          LocalDate.parse("2019-02-02"),
          "VAT Return LPI",
          5.00,
          2,
          isPenalty = false
        ))
      }
    }

    "return None" when {

      "accruedInterestAmount is missing" in {
        val charge = payment.copy(accruedInterestAmount = None)
        controller.buildEstimatedIntViewModel(charge) shouldBe None
      }
    }
  }

  "The buildEstimatedLPPViewModel function" should {

    "return a EstimatedLPP1ViewModel" when {

      "accruedPenaltyAmount and all appropriate LPP1 penalty details are present" in {
        controller.buildEstimatedLPPViewModel(payment, LPPDetailsModelMax) shouldBe Some(EstimatedLPP1ViewModel(
          "15",
          "30",
          2.4,
          4.2,
          100.11,
          50.55,
          LocalDate.parse("2019-01-01"),
          LocalDate.parse("2019-02-02"),
          "VAT Return 1st LPP"
        ))
      }
    }

    "return a EstimatedLPP2ViewModel" when {

      "accruedPenaltyAmount and all appropriate LPP2 penalty details are present" in {
        val charge = payment.copy(chargeType = AACharge)
        val penalty = LPPDetailsModelMax.copy(penaltyCategory = "LPP2")
        controller.buildEstimatedLPPViewModel(charge, penalty) shouldBe Some(EstimatedLPP2ViewModel(
          "31",
          5.5,
          50.55,
          LocalDate.parse("2019-01-01"),
          LocalDate.parse("2019-02-02"),
          "VAT AA 2nd LPP"
        ))
      }
    }

    "return None" when {

      "accruedPenaltyAmount is missing" in {
        val charge = payment.copy(accruedPenaltyAmount = None)
        controller.buildEstimatedLPPViewModel(charge, LPPDetailsModelMax) shouldBe None
      }

      "penalty type is not recognised" in {
        val penalty = LPPDetailsModelMax.copy(penaltyCategory = "LPP3")
        controller.buildEstimatedLPPViewModel(payment, penalty) shouldBe None
      }

      "penalty type is LPP1 but LPP1 details are missing" in {
        val penalty = LPPDetailsModelMax.copy(LPP1LRDays = None, LPP1HRDays = None, LPP1LRPercentage = None,
                                              LPP1HRPercentage = None, LPP1LRCalculationAmount = None)
        controller.buildEstimatedLPPViewModel(payment, penalty) shouldBe None
      }

      "penalty type is LPP2 but LPP2 details are missing" in {
        val penalty = LPPDetailsModelMax.copy(penaltyCategory = "LPP2", LPP2Days = None, LPP2Percentage = None)
        controller.buildEstimatedLPPViewModel(payment, penalty) shouldBe None
      }
    }
  }

  "The buildCrystallisedLPPViewModel function" should {

    val penaltyCharge = payment.copy(chargeType = VatReturn1stLPP)
    val penaltyLPP2Charge = payment.copy(chargeType = VatPA2ndLPP)

    "return a CrystallisedLPP1ViewModel" when {

      "chargeReference and all appropriate LPP1 penalty details (lower rate) are present" in {
        val lppDetails = LPPDetailsModelMax.copy(LPP1HRCalculationAmount = None)
        mockDateServiceCall()
        controller.buildCrystallisedLPPViewModel(penaltyCharge, Some(lppDetails)) shouldBe Some(CrystallisedLPP1ViewModel(
          "15",
          "15",
          Some("30"),
          2.4,
          Some(4.2),
          100.11,
          None,
          LocalDate.parse("2019-03-03"),
          10000,
          0,
          10000,
          LocalDate.parse("2019-01-01"),
          LocalDate.parse("2019-02-02"),
          "VAT Return 1st LPP",
          "XD002750002155",
          isOverdue = false
        ))
      }

      "chargeReference and all appropriate LPP1 penalty details (higher rate) are present" in {
        mockDateServiceCall()
        controller.buildCrystallisedLPPViewModel(penaltyCharge, Some(LPPDetailsModelMax)) shouldBe Some(CrystallisedLPP1ViewModel(
          "30",
          "15",
          Some("30"),
          2.4,
          Some(4.2),
          100.11,
          Some(200.22),
          LocalDate.parse("2019-03-03"),
          10000,
          0,
          10000,
          LocalDate.parse("2019-01-01"),
          LocalDate.parse("2019-02-02"),
          "VAT Return 1st LPP",
          "XD002750002155",
          isOverdue = false
        ))
      }
    }

    "return a CrystallisedLPP2ViewModel" when {

      "chargeReference and all appropriate LPP2 penalty details are present" in {
        mockDateServiceCall()
        controller.buildCrystallisedLPPViewModel(penaltyLPP2Charge, Some(LPPLPP2DetailsModelMax)) shouldBe Some(CrystallisedLPP2ViewModel(
          "31",
          5.5,
          LocalDate.parse("2019-03-03"),
          10000,
          0,
          10000,
          LocalDate.parse("2019-01-01"),
          LocalDate.parse("2019-02-02"),
          "VAT PA 2nd LPP",
          "XD002750002155",
          isOverdue = false
        ))
      }
    }

    "return None" when {

      "chargeType is missing" in {
        val charge = penaltyCharge.copy(chargeReference = None)
        controller.buildCrystallisedLPPViewModel(charge, Some(LPPDetailsModelMax)) shouldBe None
      }

      "penalty type is not recognised" in {
        val penalty = LPPDetailsModelMax.copy(penaltyCategory = "LPP3")
        controller.buildCrystallisedLPPViewModel(penaltyCharge, Some(penalty)) shouldBe None
      }

      "no matching penalty is provided" in {
        controller.buildCrystallisedLPPViewModel(penaltyCharge, None) shouldBe None
      }

      "penalty type is LPP1 but LPP1 details are missing" in {
        val penalty = LPPDetailsModelMax.copy(LPP1LRDays = None, LPP1HRDays = None, LPP1LRPercentage = None,
          LPP1HRPercentage = None, LPP1LRCalculationAmount = None)
        controller.buildCrystallisedLPPViewModel(penaltyCharge, Some(penalty)) shouldBe None
      }

      "penalty type is LPP2 but LPP2 details are missing" in {
        val penalty = LPPLPP2DetailsModelMax.copy(LPP2Days = None, LPP2Percentage = None)
        controller.buildCrystallisedLPPViewModel(penaltyLPP2Charge, Some(penalty)) shouldBe None
      }
    }
  }

  "The buildLateSubmissionPenaltyViewModel function" should {

    "return a LateSubmissionPenaltyViewModel" when {

      "chargeReference is present" in {
        mockDateServiceCall()
        val charge = payment.copy(chargeType = VatLateSubmissionPen)
        controller.buildLateSubmissionPenaltyViewModel(charge) shouldBe Some(LateSubmissionPenaltyViewModel(
          "VAT Late Submission Pen",
          LocalDate.parse("2019-03-03"),
          10000,
          0,
          10000,
          isOverdue = false,
          "XD002750002155",
          LocalDate.parse("2019-01-01"),
          LocalDate.parse("2019-02-02")
        ))
      }
    }

    "return None" when {

      "chargeReference is missing" in {
        val charge = payment.copy(chargeType = VatLateSubmissionPen, chargeReference = None)
        controller.buildLateSubmissionPenaltyViewModel(charge) shouldBe None
      }
    }
  }
}
