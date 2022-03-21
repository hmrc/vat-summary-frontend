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
import play.api.test.Helpers._
import views.html.errors.PaymentsError
import views.html.payments.ChargeTypeDetailsView

class ChargeBreakdownControllerSpec extends ControllerBaseSpec {

  val controller = new ChargeBreakdownController(
    authorisedController, ddInterruptPredicate, mcc, mockServiceInfoService,
    injector.instanceOf[ChargeTypeDetailsView], injector.instanceOf[PaymentsError]
  )

  "The showBreakdown action" when {

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
          controller.showBreakdown(request)
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
          controller.showBreakdown(request)
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
        controller.showBreakdown(fakeRequestWithSession.withFormUrlEncodedBody("field" -> "value"))
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
        controller.showBreakdown(fakeRequestWithSession)
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
        controller.showBreakdown(fakeRequest)
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
        val result = controller.showBreakdown()(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "the user is insolvent without access" should {

      "return 403" in {
        mockPrincipalAuth()
        val result = controller.showBreakdown()(insolventRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "the user has no viewDirectDebitInterrupt in session" should {

      lazy val result = {
        mockPrincipalAuth()
        controller.showBreakdown()(DDInterruptRequest)
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
