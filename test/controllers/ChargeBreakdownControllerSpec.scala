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

import play.api.test.Helpers._

class ChargeBreakdownControllerSpec extends ControllerBaseSpec {

  val controller = new ChargeBreakdownController(authorisedController, ddInterruptPredicate, mcc)

  "The showBreakdown action" when {

    "the user is logged in as a principal entity" should {

      lazy val result = {
        mockPrincipalAuth()
        controller.showBreakdown(fakeRequestWithSession)
      }

      "return 200" in {
        status(result) shouldBe OK
      }

      "load the page" in {
        contentAsString(result) shouldBe "Example Charge"
      }
    }

    "the user is logged in as an agent" should {

      lazy val result = {
        mockAgentAuth()
        controller.showBreakdown(agentFinancialRequest)
      }

      "return 200" in {
        status(result) shouldBe OK
      }

      "load the page" in {
        contentAsString(result) shouldBe "Example Charge"
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
