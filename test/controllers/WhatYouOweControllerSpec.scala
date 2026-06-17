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

package controllers

import common.TestModels._
import models.User
import models.errors.{PaymentsError, UnexpectedStatusError}
import models.payments._
import models.viewModels._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.errors.PaymentsError
import views.html.payments.{NoPayments, WhatYouOwe}

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

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
    mockWYOViewService,
    mockWYOSessionService,
    mockAuditService,
    mockPOACheckService,
    mockAnnualAccountingService
  )

  (mockAnnualAccountingService.getStandingRequests(_: String)(_: HeaderCarrier, _: ExecutionContext))
    .stubs(*, *, *)
    .returning(Future.successful(None))

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
          mockPOACheckServiceCall()
          mockCategoriseChargesCall(categoriseChargesViewModels)
          mockGetDirectDebitStatus(Right(directDebitEnrolled))
          mockWYOSessionServiceCall()
          mockAudit()
          controller.show(fakeRequest)
        }

        "return OK" in {
          status(result) shouldBe OK
        }

        "return the correct content" in {
          Jsoup.parse(contentAsString(result)).title() shouldBe "What you owe - Manage your VAT account - GOV.UK"
        }

      }

      "the user has open payments and poa active date is in future" when {

        lazy val result = {
          mockPrincipalAuth()
          mockServiceInfoCall()
          mockOpenPayments(Right(Some(Payments(Seq(payment, payment)))))
          mockCustomerInfo(Right(customerInformationMax))
          mockCustomerInfo(Right(customerInformationMax))
          mockPOACheckServiceCallTrue()
          mockDateServiceCall()
          mockPenaltyDetailsServiceCall()
          mockCategoriseChargesCall(categoriseChargesViewModels)

          mockGetDirectDebitStatus(Right(directDebitNotEnrolled))
          (mockAnnualAccountingService.getStandingRequests(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returning(Future.successful(None))
            .anyNumberOfTimes()
          mockWYOSessionServiceCall()
          mockAudit()
          controller.show(fakeRequest)
        }

        "return OK" in {
          status(result) shouldBe OK
        }

        "return the correct content with poa schedule link and banner" in {
          val document = Jsoup.parse(contentAsString(result))
          document.title() shouldBe "What you owe - Manage your VAT account - GOV.UK"
          document.select(".govuk-inset-text .govuk-link").attr("href") shouldBe "/vat-through-software/payments-on-account"
        }

      }

      "the user has open payments and is an Annual Accounting customer" when {

        lazy val result = {
          mockAppConfig.features.annualAccountingFeatureEnabled(true)
          mockPrincipalAuth()
          mockServiceInfoCall()
          mockOpenPayments(Right(Some(Payments(Seq(payment, payment)))))
          mockCustomerInfo(Right(customerInformationMax))
          mockCustomerInfo(Right(customerInformationMax))
          mockPOACheckServiceCall()
          (mockAnnualAccountingService.getStandingRequests(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returning(Future.successful(Some(standingRequestSampleAnnualAccounting)))
            .anyNumberOfTimes()
          mockCategoriseChargesCall(categoriseChargesViewModels)

          mockDateServiceCall()
          mockPenaltyDetailsServiceCall()
          mockGetDirectDebitStatus(Right(directDebitNotEnrolled))
          mockWYOSessionServiceCall()
          mockAudit()
          val response = controller.show(fakeRequest)
          mockAppConfig.features.annualAccountingFeatureEnabled(false)
          response
        }

        "return OK" in {
          status(result) shouldBe OK
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
          mockPOACheckServiceCall()
          mockPenaltyDetailsServiceCall()
          mockGetDirectDebitStatus(Right(directDebitEnrolled))
          mockAudit()
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
          mockPOACheckServiceCall()
          mockGetDirectDebitStatus(Right(directDebitEnrolled))
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

      "the penalties call is unsuccessful" should {

        lazy val result = {
          mockPrincipalAuth()
          mockServiceInfoCall()
          mockOpenPayments(Right(Some(Payments(Seq(payment, payment)))))
          mockCustomerInfo(Right(customerInformationMax))
          mockCustomerInfo(Right(customerInformationMax))
          mockDateServiceCall()
          mockPOACheckServiceCall()
          mockPenaltyDetailsServiceCall(Left(UnexpectedStatusError("500", "oops")))
          mockGetDirectDebitStatus(Right(directDebitEnrolled))
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
          mockCategoriseChargesCall(categoriseChargesViewModels)
          mockPOACheckServiceCall()
          mockGetDirectDebitStatus(Right(directDebitEnrolled))
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
        mockGetDirectDebitStatus(Right(directDebitEnrolled))
        mockPOACheckServiceCall()
        mockCategoriseChargesCall(categoriseChargesViewModels)
        mockWYOSessionServiceCall()
        mockAudit()
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
        val charge = payment.copy(accruingPenaltyAmount = None)
        val result = {
          mockDateServiceCall()
          mockCategoriseChargesCall(Seq(Some(whatYouOweChargeModel), Some(whatYouOweChargeModelEstimatedLPI)))
          controller.constructViewModel(Seq(charge), mandationStatus = "MTDfB", penaltyDetailsModelMax, ddStatus = false
          )(fakeRequest)
        }
        result shouldBe Some(whatYouOweViewModelWithEstimatedLPI)
      }

    }

    "there is a payment with a charge type that can have estimated interest but accruedInterest is 0" should {

      "return the correct view model with 1 charge model" in {
        val result = {
          mockDateServiceCall()
          mockCategoriseChargesCall(Seq(Some(whatYouOweChargeModel)))
          controller.constructViewModel(Seq(paymentNoAccInterest), mandationStatus = "MTDfB", penaltyDetailsModelMax, ddStatus = false)(fakeRequest)
        }
        result shouldBe Some(whatYouOweViewModel.copy(charges = Seq(whatYouOweChargeModel)))
      }
    }

    "there is a payment that has accrued interest defined but estimated interest is not supported" should {

      "return the correct view model with 1 charge model" in {
        val result = {
          mockDateServiceCall()
          mockCategoriseChargesCall(Seq(Some(wyoChargeUnrepayableOverpayment)))
          controller.constructViewModel(Seq(unrepayableOverpayment), mandationStatus = "MTDfB", penaltyDetailsModelMax, ddStatus = false)(fakeRequest)
        }
        result shouldBe Some(whatYouOweViewModel.copy(charges = Seq(wyoChargeUnrepayableOverpayment)))
      }
    }

    "there is a payment correction charge because HMRC paid more VAT than owed" should {

      "return the correct view model with 1 charge model" in {
        val result = {
          mockDateServiceCall()
          mockCategoriseChargesCall(Seq(Some(vatOverpaymentTax), Some(vatOverpaymentTaxLPIEstimatedModel)))
          controller.constructViewModel(Seq(overpaymentforTax), mandationStatus = "MTDfB", penaltyDetailsModelMax, ddStatus = false)(fakeRequest)
        }
        result shouldBe Some(whatYouOweViewModel.copy(
          totalAmount = BigDecimal(10002),
          charges = Seq(
            vatOverpaymentTax,
            vatOverpaymentTaxLPIEstimatedModel
          )
        ))
      }
    }

    "there is a LPP1 Charge for a late vat overpayment" should {

      "return the correct view model with 1 charge model" in {
        val result = {
          mockDateServiceCall()
          mockCategoriseChargesCall(Seq(Some(crystallisedVatOPLPP1Model)))
          controller.constructViewModel(
            payments = Seq(overpaymentforTaxLPP1),
            mandationStatus = "MTDfB",
            penalties = penaltyDetailsModelMax, false)(fakeRequest)
        }
        result shouldBe Some(whatYouOweViewModel.copy(charges = Seq(crystallisedVatOPLPP1Model)))
      }
    }

    "there is a LPP1 Charge for a late vat overpayment with accruing interest" should {

      "return the correct view model with 1 charge model" in {
        val result = {
          mockDateServiceCall()
          mockCategoriseChargesCall(Seq(Some(crystallisedVatOPLPP1Model), Some(estimatedVatOPLPP1LPI)))
          controller.constructViewModel(
            payments = Seq(overpaymentForTaxLPP1EstLPI),
            mandationStatus = "MTDfB",
            penalties = penaltyDetailsModelMax, false)(fakeRequest)
        }
        result shouldBe Some(whatYouOweViewModel.copy(
          totalAmount = BigDecimal(10002),
          charges = Seq(
            crystallisedVatOPLPP1Model,
            estimatedVatOPLPP1LPI
          )
        ))
      }
    }

    "there is a crystallised LPP1 LPI Charge for a late vat overpayment" should {

      "return the correct view model with 1 charge model" in {
        val result = {
          mockDateServiceCall()
          mockCategoriseChargesCall(Seq(Some(crystallisedVatOPLPP1LPIModel)))
          controller.constructViewModel(
            payments = Seq(overpaymentForTaxLPP1LPI),
            mandationStatus = "MTDfB",
            penalties = penaltyDetailsModelMax, false)(fakeRequest)
        }
        result shouldBe Some(whatYouOweViewModel.copy(charges = Seq(crystallisedVatOPLPP1LPIModel)))
      }
    }

    "there is a crystallised Vat Overpayment LPI Charge for a late vat overpayment" should {

      "return the correct view model with 1 crystalised charge model" in {
        val result = {
          mockDateServiceCall()
          mockCategoriseChargesCall(Seq(Some(crystallisedVATOverpaymentforTaxLPI)))
          controller.constructViewModel(Seq(overpaymentforTaxLPI), mandationStatus = "MTDfB", penaltyDetailsModelMax, ddStatus = false)(fakeRequest)
        }
        result shouldBe Some(whatYouOweViewModel.copy(
          charges = Seq(crystallisedVATOverpaymentforTaxLPI),
          totalAmount = BigDecimal(10000),
        ))
      }
    }

    "there is a LPP2 Charge for a late vat overpayment" should {

      "return the correct view model with 1 charge model" in {
        val result = {
          mockDateServiceCall()
          mockCategoriseChargesCall(Seq(Some(crystallisedVatOPLPP2Model)))
          controller.constructViewModel(
            payments = Seq(overpaymentforTaxLPP2),
            mandationStatus = "MTDfB",
            penalties = penaltyDetailsLPP2ModelMax, ddStatus = false)(fakeRequest)
        }
        result shouldBe Some(whatYouOweViewModel.copy(charges = Seq(crystallisedVatOPLPP2Model)))
      }
    }

    "there is a LPP2 Charge for a late vat overpayment with accruing interest" should {

      "return the correct view model with 1 charge model" in {
        val result = {
          mockDateServiceCall()
          mockCategoriseChargesCall(Seq(Some(crystallisedVatOPLPP2Model), Some(estimatedVatOPLPP2LPI)))
          controller.constructViewModel(
            payments = Seq(overpaymentForTaxLPP2EstLPI),
            mandationStatus = "MTDfB",
            penalties = penaltyDetailsLPP2ModelMax, ddStatus = false)(fakeRequest)
        }
        result shouldBe Some(whatYouOweViewModel.copy(
          totalAmount = BigDecimal(10002),
          charges = Seq(
            crystallisedVatOPLPP2Model,
            estimatedVatOPLPP2LPI
          )
        ))
      }
    }

    "there is a crystallised LPP2 LPI Charge for a late vat overpayment" should {

      "return the correct view model with 1 charge model" in {
        val result = {
          mockDateServiceCall()
          mockCategoriseChargesCall(Seq(Some(crystallisedVatOPLPP2LPIModel)))
          controller.constructViewModel(
            payments = Seq(overpaymentForTaxLPP2LPI),
            mandationStatus = "MTDfB",
            penalties = penaltyDetailsLPP2ModelMax, ddStatus = false)(fakeRequest)
        }
        result shouldBe Some(whatYouOweViewModel.copy(charges = Seq(crystallisedVatOPLPP2LPIModel)))
      }
    }

    "there is a non penalty reform penalty with accruing interest" should {

      "return the correct view model with 1 charge model" in {
        val result = {
          mockDateServiceCall()
          mockCategoriseChargesCall(Seq(Some(vatInaccAssessPenViewModel), Some(estimatedVATInaccAssessPenLPIModel)))
          controller.constructViewModel(
            payments = Seq(vatInaccAssessPen),
            mandationStatus = "MTDfB",
            penalties = penaltyDetailsModelMin, ddStatus = false)(fakeRequest)
        }
        result shouldBe Some(whatYouOweViewModel.copy(
          totalAmount = BigDecimal(10002),
          charges = Seq(
            vatInaccAssessPenViewModel,
            estimatedVATInaccAssessPenLPIModel
          )
        ))
      }
    }

    "there is a crystallised non penalty reform penalty LPI Charge" should {

      "return the correct view model with 1 charge model" in {
        val result = {
          mockDateServiceCall()
          mockCategoriseChargesCall(Seq(Some(crystallisedVATInaccAssessPenLPIModel)))
          controller.constructViewModel(
            payments = Seq(vatInaccAssessPenLPI),
            mandationStatus = "MTDfB",
            penalties = penaltyDetailsModelMin, ddStatus = false)(fakeRequest)
        }
        result shouldBe Some(
          whatYouOweViewModel.copy(totalAmount = BigDecimal(10000),
            charges = Seq(crystallisedVATInaccAssessPenLPIModel)))
      }
    }

    "there are multiple payments with a mix of estimated & crystallised interest, LPP1 and LPP2" should {

      "return the expected view models and total amount" in {

        val crystallisedInterest = {
          payment.copy(chargeType = VatReturnLPI, accruingInterestAmount = None, accruingPenaltyAmount = None)
        }
        val lateSubmissionPenalty = {
          payment.copy(chargeType = VatLateSubmissionPen, accruingInterestAmount = None, accruingPenaltyAmount = None, penaltyType = None)
        }
        lazy val result = {
          mockDateServiceCall()
          mockCategoriseChargesCall(
            Seq(
              Some(whatYouOweChargeModel),
              Some(whatYouOweChargeModelEstimatedLPI),
              Some(estimatedLPP1Model),
              Some(whatYouOweChargeModelLPICharge),
              Some(lateSubmissionPenaltyCharge)
            )
          )
          controller.constructViewModel(
            Seq(payment, crystallisedInterest, lateSubmissionPenalty),
            mandationStatus = "MTDfB",
            penaltyDetailsModelMax, ddStatus = false
          )(fakeRequest)
        }


        result shouldBe Some(whatYouOweViewModelMultipleTypes)
      }
    }

    "an interest payment doesn't have the charge reference defined" should {

      "not build a view model" in {
        val result = {
          mockDateServiceCall()
          mockCategoriseChargesCall(Seq(None))
          controller.constructViewModel(
            Seq(payment.copy(chargeReference = None, chargeType = VatReturn1stLPPLPI)),
            mandationStatus = "MTDfB",
            penaltyDetailsModelMax, ddStatus = false
          )(fakeRequest)
        }
        result shouldBe None
      }
    }

    //fix test
    //    "description() cannot retrieve a charge description" should {
    //
    //      "return a charge model with an empty string as a description" in {
    //        val result = {
    //          mockDateServiceCall()
    //          controller.constructViewModel(
    //            Seq(payment.copy(chargeType = FtnEachPartnerCharge)), mandationStatus = "MTDfB", penaltyDetailsModelMax
    //          )(fakeRequest)
    //        }
    //          result shouldBe Some(viewModelNoChargeDescription)
    //      }
    //    }
  }
}