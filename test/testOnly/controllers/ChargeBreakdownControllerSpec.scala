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

import common.TestModels.chargeModel1
import controllers.ControllerBaseSpec
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.mvc.Http.HeaderNames
import views.html.errors.PaymentsError
import views.html.payments.{ChargeTypeDetailsView, CrystallisedInterestView, CrystallisedLPP1View, EstimatedInterestView}

class ChargeBreakdownControllerSpec extends ControllerBaseSpec {

  val controller = new ChargeBreakdownController(
    authorisedController, ddInterruptPredicate, mcc, mockServiceInfoService,
    injector.instanceOf[ChargeTypeDetailsView], injector.instanceOf[EstimatedInterestView],
    injector.instanceOf[PaymentsError], injector.instanceOf[CrystallisedInterestView],
    injector.instanceOf[CrystallisedLPP1View]
  )

  lazy val requestWithReferrer: FakeRequest[AnyContentAsEmpty.type] =
    fakeRequestWithSession.withHeaders(HeaderNames.REFERER -> "/home")
  lazy val agentRequestWithReferrer: FakeRequest[AnyContentAsEmpty.type] =
    agentFinancialRequest.withHeaders(HeaderNames.REFERER -> "/home")

  "The chargeBreakdown action" when {

    "there is a HTTP referrer" when {

      "the user is logged in as a principal entity" should {

        lazy val result = {
          mockPrincipalAuth()
          mockServiceInfoCall()
          controller.chargeBreakdown(chargeModel1)(requestWithReferrer)
        }

        "return 200" in {
          status(result) shouldBe OK
        }

        "load the page" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.title() shouldBe "VAT - Manage your VAT account - GOV.UK"
        }
      }

      "the user is logged in as an agent" should {

        lazy val result = {
          mockAgentAuth()
          mockServiceInfoCall()
          controller.chargeBreakdown(chargeModel1)(agentRequestWithReferrer)
        }

        "return 200" in {
          status(result) shouldBe OK
        }

        "load the page" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.title() shouldBe "VAT - Your client’s VAT details - GOV.UK"
        }
      }
    }

    "there is no HTTP referrer" should {

      lazy val result = {
        mockPrincipalAuth()
        mockServiceInfoCall()
        controller.chargeBreakdown(chargeModel1)(fakeRequestWithSession)
      }

      "return 303" in {
        status(result) shouldBe SEE_OTHER
      }

      "redirect to the What You Owe page" in {
        redirectLocation(result) shouldBe Some(routes.WhatYouOweController.show.url)
      }
    }

    "the user is not logged in" should {

      lazy val result = {
        mockMissingBearerToken()
        controller.chargeBreakdown(chargeModel1)(requestWithReferrer)
      }

      "return status 303" in {
        status(result) shouldBe SEE_OTHER
      }
      "return the correct redirect location which should be sign in" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
      }
    }

    "the user has invalid credentials" should {

      "return 403" in {
        mockInsufficientEnrolments()
        val result = controller.chargeBreakdown(chargeModel1)(requestWithReferrer)
        status(result) shouldBe FORBIDDEN
      }
    }

    "the user is insolvent without access" should {

      "return 403" in {
        mockPrincipalAuth()
        val result = controller.chargeBreakdown(chargeModel1)(insolventRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "the user has no viewDirectDebitInterrupt in session" should {

      lazy val result = {
        mockPrincipalAuth()
        controller.chargeBreakdown(chargeModel1)(DDInterruptRequest)
      }

      "return 303" in {
        status(result) shouldBe SEE_OTHER
      }

      "redirect to the DD interrupt controller" in {
        redirectLocation(result) shouldBe
          Some(controllers.routes.DDInterruptController.directDebitInterruptCall("/homepage").url)
      }
    }
  }

  "The interestBreakdown action" when {

    "valid form information is submitted in the request" when {

      def requestWithForm(request: FakeRequest[_]): FakeRequest[AnyContentAsFormUrlEncoded] = request.withFormUrlEncodedBody(
        "periodFrom" -> "2018-01-01",
        "periodTo" -> "2018-02-02",
        "chargeType" -> "VAT Return Debit Charge",
        "interestRate" -> "2.6",
        "currentAmount" -> "300.33",
        "amountReceived" -> "200.22",
        "leftToPay" -> "100.11",
        "isPenalty" -> "false"
      )

      "the user is logged in as a principal entity" should {

        lazy val result = {
          mockPrincipalAuth()
          mockServiceInfoCall()
          controller.estimatedInterestBreakdown(requestWithForm(fakePostWithSession))
        }

        "return 200" in {
          status(result) shouldBe OK
        }

        "load the page" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.title() shouldBe "VAT - Manage your VAT account - GOV.UK"
        }
      }

      "the user is logged in as an agent" should {

        lazy val result = {
          mockAgentAuth()
          mockServiceInfoCall()
          controller.estimatedInterestBreakdown(requestWithForm(agentPostFinancialRequest))
        }

        "return 200" in {
          status(result) shouldBe OK
        }

        "load the page" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.title() shouldBe "VAT - Your client’s VAT details - GOV.UK"
        }
      }

    }

    "invalid form information is submitted in the request" should {

      lazy val result = {
        mockPrincipalAuth()
        mockServiceInfoCall()
        controller.estimatedInterestBreakdown(fakeRequestWithSession.withFormUrlEncodedBody("field" -> "value"))
      }

      "return 500" in {
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }

      "load the payments error view" in {
        val document: Document = Jsoup.parse(contentAsString(result))
        document.select(".govuk-body").text() shouldBe "If you know how much you owe, you can still pay now."
      }
    }

    "no form information is submitted in the request" should {

      lazy val result = {
        mockPrincipalAuth()
        mockServiceInfoCall()
        controller.estimatedInterestBreakdown(fakeRequestWithSession)
      }

      "return 500" in {
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }

      "load the payments error view" in {
        val document: Document = Jsoup.parse(contentAsString(result))
        document.select(".govuk-body").text() shouldBe "If you know how much you owe, you can still pay now."
      }
    }

    "the user is not logged in" should {

      lazy val result = {
        mockMissingBearerToken()
        controller.estimatedInterestBreakdown(fakeRequest)
      }

      "return 303" in {
        status(result) shouldBe SEE_OTHER
      }

      "redirect to the sign-in URL" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
      }
    }

    "the user has invalid credentials" should {

      "return 403" in {
        mockInsufficientEnrolments()
        val result = controller.estimatedInterestBreakdown()(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "the user is insolvent without access" should {

      "return 403" in {
        mockPrincipalAuth()
        val result = controller.estimatedInterestBreakdown()(insolventRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "the user has no viewDirectDebitInterrupt in session" should {

      lazy val result = {
        mockPrincipalAuth()
        controller.estimatedInterestBreakdown()(DDInterruptRequest)
      }

      "return 303" in {
        status(result) shouldBe SEE_OTHER
      }

      "redirect to the DD interrupt controller" in {
        redirectLocation(result) shouldBe
          Some(controllers.routes.DDInterruptController.directDebitInterruptCall("/homepage").url)
      }
    }
  }

  "The crystallisedInterestBreakdown action" when {

    "valid form information is submitted in the request" when {

      def requestWithForm(request: FakeRequest[_]): FakeRequest[AnyContentAsFormUrlEncoded] = request.withFormUrlEncodedBody(
        "periodFrom" -> "2018-01-01",
        "periodTo" -> "2018-02-02",
        "chargeType" -> "VAT Default Interest",
        "interestRate" -> "2.6",
        "dueDate" -> "2018-03-03",
        "interestAmount" -> "300.33",
        "amountReceived" -> "200.22",
        "leftToPay" -> "100.11",
        "isOverdue" -> "false",
        "chargeReference" -> "XXXXXX0123456789"
      )

      "the user is logged in as a principal entity" should {

        lazy val result = {
          mockPrincipalAuth()
          mockServiceInfoCall()
          controller.crystallisedInterestBreakdown(requestWithForm(fakePostWithSession))
        }

        "return 200" in {
          status(result) shouldBe OK
        }

        "load the page" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.title() shouldBe "Default interest - Manage your VAT account - GOV.UK"
        }
      }

      "the user is logged in as an agent" should {

        lazy val result = {
          mockAgentAuth()
          mockServiceInfoCall()
          controller.crystallisedInterestBreakdown(requestWithForm(agentPostFinancialRequest))
        }

        "return 200" in {
          status(result) shouldBe OK
        }

        "load the page" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.title() shouldBe "Default interest - Your client’s VAT details - GOV.UK"
        }
      }

    }

    "invalid form information is submitted in the request" should {

      lazy val result = {
        mockPrincipalAuth()
        mockServiceInfoCall()
        controller.crystallisedInterestBreakdown(fakeRequestWithSession.withFormUrlEncodedBody("field" -> "value"))
      }

      "return 500" in {
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }

      "load the payments error view" in {
        val document: Document = Jsoup.parse(contentAsString(result))
        document.select(".govuk-body").text() shouldBe "If you know how much you owe, you can still pay now."
      }
    }

    "no form information is submitted in the request" should {

      lazy val result = {
        mockPrincipalAuth()
        mockServiceInfoCall()
        controller.crystallisedInterestBreakdown(fakeRequestWithSession)
      }

      "return 500" in {
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }

      "load the payments error view" in {
        val document: Document = Jsoup.parse(contentAsString(result))
        document.select(".govuk-body").text() shouldBe "If you know how much you owe, you can still pay now."
      }
    }

    "the user is not logged in" should {

      lazy val result = {
        mockMissingBearerToken()
        controller.crystallisedInterestBreakdown(fakeRequest)
      }

      "return 303" in {
        status(result) shouldBe SEE_OTHER
      }

      "redirect to the sign-in URL" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
      }
    }

    "the user has invalid credentials" should {

      "return 403" in {
        mockInsufficientEnrolments()
        val result = controller.crystallisedInterestBreakdown()(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "the user is insolvent without access" should {

      "return 403" in {
        mockPrincipalAuth()
        val result = controller.crystallisedInterestBreakdown()(insolventRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "the user has no viewDirectDebitInterrupt in session" should {

      lazy val result = {
        mockPrincipalAuth()
        controller.crystallisedInterestBreakdown()(DDInterruptRequest)
      }

      "return 303" in {
        status(result) shouldBe SEE_OTHER
      }

      "redirect to the DD interrupt controller" in {
        redirectLocation(result) shouldBe
          Some(controllers.routes.DDInterruptController.directDebitInterruptCall("/homepage").url)
      }
    }
  }

  "The crystallisedLPP1Breakdown action" when {

    "valid form information is submitted in the request" when {

      def requestWithForm(request: FakeRequest[_]): FakeRequest[AnyContentAsFormUrlEncoded] = request.withFormUrlEncodedBody(
        "numberOfDays" -> "30",
        "part1Days" -> "15",
        "part2Days" -> "30",
        "part1PenaltyRate" -> "15.0",
        "part1PenaltyRate" -> "2.0",
        "part1UnpaidVAT" -> "77.00",
        "part2UnpaidVAT" -> "77.00",
        "dueDate" -> "2018-01-01",
        "penaltyAmount" -> "154.00",
        "amountReceived" -> "0.00",
        "leftToPay" -> "154.00",
        "periodFrom" -> "2018-01-01",
        "periodTo" -> "2018-02-02",
        "chargeType" -> "VAT Default Interest",
        "chargeReference" -> "XXXXXX1234567890",
        "isOverdue" -> "false"
      )

      "the user is logged in as a principal entity" should {

        lazy val result = {
          mockPrincipalAuth()
          mockServiceInfoCall()
          controller.crystallisedLPP1Breakdown(requestWithForm(fakePostWithSession))
        }

        "return 200" in {
          status(result) shouldBe OK
        }

        "load the page" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.title() shouldBe "Default interest - Manage your VAT account - GOV.UK"
        }
      }

      "the user is logged in as an agent" should {

        lazy val result = {
          mockAgentAuth()
          mockServiceInfoCall()
          controller.crystallisedLPP1Breakdown(requestWithForm(agentPostFinancialRequest))
        }

        "return 200" in {
          status(result) shouldBe OK
        }

        "load the page" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.title() shouldBe "Default interest - Your client’s VAT details - GOV.UK"
        }
      }

    }

    "invalid form information is submitted in the request" should {

      lazy val result = {
        mockPrincipalAuth()
        mockServiceInfoCall()
        controller.crystallisedLPP1Breakdown(fakeRequestWithSession.withFormUrlEncodedBody("field" -> "value"))
      }

      "return 500" in {
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }

      "load the payments error view" in {
        val document: Document = Jsoup.parse(contentAsString(result))
        document.select(".govuk-body").text() shouldBe "If you know how much you owe, you can still pay now."
      }
    }

    "no form information is submitted in the request" should {

      lazy val result = {
        mockPrincipalAuth()
        mockServiceInfoCall()
        controller.crystallisedLPP1Breakdown(fakeRequestWithSession)
      }

      "return 500" in {
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }

      "load the payments error view" in {
        val document: Document = Jsoup.parse(contentAsString(result))
        document.select(".govuk-body").text() shouldBe "If you know how much you owe, you can still pay now."
      }
    }

    "the user is not logged in" should {

      lazy val result = {
        mockMissingBearerToken()
        controller.crystallisedLPP1Breakdown(fakeRequest)
      }

      "return 303" in {
        status(result) shouldBe SEE_OTHER
      }

      "redirect to the sign-in URL" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
      }
    }

    "the user has invalid credentials" should {

      "return 403" in {
        mockInsufficientEnrolments()
        val result = controller.crystallisedLPP1Breakdown()(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "the user is insolvent without access" should {

      "return 403" in {
        mockPrincipalAuth()
        val result = controller.crystallisedLPP1Breakdown()(insolventRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "the user has no viewDirectDebitInterrupt in session" should {

      lazy val result = {
        mockPrincipalAuth()
        controller.crystallisedLPP1Breakdown()(DDInterruptRequest)
      }

      "return 303" in {
        status(result) shouldBe SEE_OTHER
      }

      "redirect to the DD interrupt controller" in {
        redirectLocation(result) shouldBe
          Some(controllers.routes.DDInterruptController.directDebitInterruptCall("/homepage").url)
      }
    }
  }
}
