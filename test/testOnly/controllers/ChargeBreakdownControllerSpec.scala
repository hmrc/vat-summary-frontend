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

import controllers.ControllerBaseSpec
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.errors.PaymentsError
import views.html.payments.{ChargeTypeDetailsView, EstimatedInterestView}

class ChargeBreakdownControllerSpec extends ControllerBaseSpec {

  override def beforeEach(): Unit = {
    super.beforeEach()
    mockAppConfig.features.interestBreakdownEnabled(true)
  }

  val controller = new ChargeBreakdownController(
    authorisedController, ddInterruptPredicate, mcc, mockServiceInfoService,
    injector.instanceOf[ChargeTypeDetailsView], injector.instanceOf[EstimatedInterestView],
    injector.instanceOf[PaymentsError], mockServiceErrorHandler
  )

  "The chargeBreakdown action" when {

    "valid form information is submitted in the request" when {

      "the user is logged in as a principal entity" should {

        val request = fakeRequestWithSession.withFormUrlEncodedBody(
          "chargeDescription" -> "Example Description",
          "chargeTitle" -> "Example Charge",
          "outstandingAmount" -> "1234.56",
          "originalAmount" -> "1234.56",
          "dueDate" -> "2018-01-01",
          "isOverdue" -> "true",
          "makePaymentRedirect" -> "/payment-redirect"
        )

        lazy val result = {
          mockPrincipalAuth()
          mockServiceInfoCall()
          controller.chargeBreakdown(request)
        }

        "return 200" in {
          status(result) shouldBe OK
        }

        "load the page" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.title() shouldBe "Example Charge - Manage your VAT account - GOV.UK"
        }
      }

      "the user is logged in as an agent" should {

        lazy val request = agentFinancialRequest.withFormUrlEncodedBody(
          "chargeDescription" -> "Example Description",
          "chargeTitle" -> "Example Charge",
          "outstandingAmount" -> "1234.56",
          "originalAmount" -> "1234.56",
          "dueDate" -> "2018-01-01",
          "isOverdue" -> "true",
          "makePaymentRedirect" -> "/payment-redirect"
        )

        lazy val result = {
          mockAgentAuth()
          mockServiceInfoCall()
          controller.chargeBreakdown(request)
        }

        "return 200" in {
          status(result) shouldBe OK
        }

        "load the page" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.title() shouldBe "Example Charge - Your client’s VAT details - GOV.UK"
        }
      }
    }

    "invalid form information is submitted in the request" should {

      lazy val result = {
        mockPrincipalAuth()
        mockServiceInfoCall()
        controller.chargeBreakdown(fakeRequestWithSession.withFormUrlEncodedBody("field" -> "value"))
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
        controller.chargeBreakdown(fakeRequestWithSession)
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
        controller.chargeBreakdown(fakeRequest)
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
        val result = controller.chargeBreakdown()(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "the user is insolvent without access" should {

      "return 403" in {
        mockPrincipalAuth()
        val result = controller.chargeBreakdown()(insolventRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "the user has no viewDirectDebitInterrupt in session" should {

      lazy val result = {
        mockPrincipalAuth()
        controller.chargeBreakdown()(DDInterruptRequest)
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
        "chargeTitle" -> "Example interest charge title",
        "interestRate" -> "2.6",
        "numberOfDaysLate" -> "3",
        "currentAmount" -> "300.33",
        "amountReceived" -> "200.22",
        "leftToPay" -> "100.11",
        "isPenalty" -> "false"
      )

      "the user is logged in as a principal entity" should {

        lazy val result = {
          mockPrincipalAuth()
          mockServiceInfoCall()
          controller.estimatedInterestBreakdown(requestWithForm(fakeRequestWithSession))
        }

        "return 200" in {
          status(result) shouldBe OK
        }

        "load the page" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.title() shouldBe "Example interest charge title - Manage your VAT account - GOV.UK"
        }
      }

      "the user is logged in as an agent" should {

        lazy val result = {
          mockAgentAuth()
          mockServiceInfoCall()
          controller.estimatedInterestBreakdown(requestWithForm(agentFinancialRequest))
        }

        "return 200" in {
          status(result) shouldBe OK
        }

        "load the page" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.title() shouldBe "Example interest charge title - Your client’s VAT details - GOV.UK"
        }
      }

      "the interest breakdown feature switch is disabled" should {

        "return 404" in {
          mockAppConfig.features.interestBreakdownEnabled(false)
          mockPrincipalAuth()
          val result = controller.estimatedInterestBreakdown(requestWithForm(fakeRequestWithSession))

          status(result) shouldBe NOT_FOUND
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
}
