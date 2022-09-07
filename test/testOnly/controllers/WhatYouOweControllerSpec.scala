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

package testOnly.controllers

import common.TestModels._
import controllers.ControllerBaseSpec
import models.User
import models.errors.PaymentsError
import models.payments.{MiscPenaltyCharge, Payments, VatReturn1stLPPLPI, VatReturnLPI}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.errors.PaymentsError
import views.html.payments.{NoPayments, WhatYouOwe}


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

    "there is a payment with the originalAmount, charge description and accrued interest defined" should {

      "return a view model with 2 charge models including an estimated interest charge and the correct total amount" in {
        val result = {
          mockDateServiceCall()
          controller.constructViewModel(Seq(payment), mandationStatus = "MTDfB")
        }
        result shouldBe Some(whatYouOweViewModelWithEstimatedInterest)
      }

    }

    "there is a payment with a charge type that can have estimated interest but accruedInterest is 0" should {

      "return the correct view model with 1 charge model" in {
        val result = {
          mockDateServiceCall()
          controller.constructViewModel(Seq(paymentNoAccInterest), mandationStatus = "MTDfB")
        }
        result shouldBe Some(whatYouOweViewModel.copy(charges = Seq(whatYouOweChargeModel)))
      }
    }

    "there is a payment that has accrued interest defined but estimated interest is not supported" should {

      "return the correct view model with 1 charge model" in {
        val result = {
          mockDateServiceCall()
          controller.constructViewModel(Seq(unrepayableOverpayment), mandationStatus = "MTDfB")
        }
        result shouldBe Some(whatYouOweViewModel.copy(charges = Seq(wyoChargeUnrepayableOverpayment)))
      }
    }

    "there are multiple payments" should {

      "calculate the correct total amount" in {

        val result = {
          mockDateServiceCall()
          controller.constructViewModel(Seq(payment, payment, payment), mandationStatus = "MTDfB")
        } map (_.totalAmount)
        val expectedTotal = 30000

        result shouldBe Some(expectedTotal)
      }
    }

    "there is a payment with the originalAmount not defined" should {

      "return None" in {
        val result = {
          mockDateServiceCall()
          controller.constructViewModel(Seq(payment.copy(originalAmount = None)), mandationStatus = "MTDfB")
        }
        result shouldBe None
      }

    }

    "there are multiple payments and some do not have these fields defined" should {

      "return None" in {
        val result = {
          mockDateServiceCall()
          controller.constructViewModel(Seq(
            payment, payment, payment.copy(originalAmount = None)
          ),
            mandationStatus = "MTDfB")
        }
        result shouldBe None
      }
    }

    "there are multiple payments and some are interest payments" should {

      "return a view model with some CrystallisedInterestViewModels" in {
        val result = {
          mockDateServiceCall()
          controller.constructViewModel(Seq(
            payment, payment, payment.copy(chargeType = VatReturnLPI), payment.copy(chargeType = VatReturn1stLPPLPI)
          ),
            mandationStatus = "MTDfB")
        }
        result shouldBe Some(whatYouOweViewModelInterestCharges)
      }
    }

    "an interest payment doesn't have the charge reference defined" should {

      "not build a view model" in {
        val result = {
          mockDateServiceCall()
          controller.constructViewModel(Seq(
            payment.copy(chargeReference = None, chargeType = VatReturn1stLPPLPI)
          ),
            mandationStatus = "MTDfB")
        }
        result shouldBe None
      }

    }

    "an interest payment doesn't have the original amount defined" should {

      "not build a view model" in {
        val result = {
          mockDateServiceCall()
          controller.constructViewModel(Seq(
            payment.copy(originalAmount = None, chargeType = VatReturn1stLPPLPI)
          ),
            mandationStatus = "MTDfB")
        }
        result shouldBe None
      }

    }

    "description() cannot retrieve a charge description" should {

      "return a charge model with an empty string as a description" in {
        val result = {
          mockDateServiceCall()
          controller.constructViewModel(Seq(
            payment.copy(chargeType = MiscPenaltyCharge)
          ), mandationStatus = "MTDfB")
        }
        result shouldBe Some(viewModelNoChargeDescription)
      }
    }
  }
}
