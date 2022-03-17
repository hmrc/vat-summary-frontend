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

import common.TestModels.customerInformationMax
import org.jsoup.Jsoup
import play.api.test.Helpers._
import testOnly.controllers.WhatYouOweController
import views.html.payments.WhatYouOwe

class WhatYouOweControllerSpec extends ControllerBaseSpec {

  val whatYouOwe: WhatYouOwe = injector.instanceOf[WhatYouOwe]

  val controller = new WhatYouOweController(
    mockServiceInfoService,
    authorisedController,
    ddInterruptPredicate,
    mcc,
    whatYouOwe
  )

  "The WhatYouOweController .show method" when {

    "a principal user is authenticated" should {

      lazy val result = {
        mockPrincipalAuth
        mockServiceInfoCall()
        mockCustomerInfo(Right(customerInformationMax))
        mockDateServiceCall
        controller.show(fakeRequest)
      }

      "return OK" in {
        status(result) shouldBe OK
      }

      "return the correct content" in {
        Jsoup.parse(contentAsString(result)).title() shouldBe "What you owe - Manage your VAT account - GOV.UK"
      }

    }

    "the user has no viewDDInterrupt in session" should {

      lazy val result = {
        mockPrincipalAuth()
        mockServiceInfoCall()
        mockDateServiceCall()
        controller.show(DDInterruptRequest)
      }

      "return 303" in {
        status(result) shouldBe SEE_OTHER
      }

      "redirect to the DD interrupt controller" in {
        redirectLocation(result) shouldBe
          Some(controllers.routes.DDInterruptController.directDebitInterruptCall(DDInterruptRequest.uri).url)
      }
    }

    "an agent user is authenticated" should {

      lazy val result = {
        mockAgentAuth
        mockServiceInfoCall()
        controller.show(agentFinancialRequest)
      }

      "return OK" in {
        status(result) shouldBe OK
      }
    }

    "the user is not authenticated" should {

      lazy val result = controller.show(agentFinancialRequest)

      "return 403" in {
        mockInsufficientEnrolments
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
}
