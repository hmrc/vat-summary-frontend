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
import play.api.http.Status.OK
import play.api.test.Helpers._

class WhatYouOweControllerSpec extends ControllerBaseSpec {

  val controller = new WhatYouOweController(
    authorisedController
  )

  "The WhatYouOweController .show method" when {

    "a principal user is authenticated" should {

      lazy val result = {
        mockPrincipalAuth
        mockCustomerInfo(Right(customerInformationMax))
        mockDateServiceCall
        controller.show(fakeRequest)
      }

      "return OK" in {
        status(result) shouldBe OK
      }

      "return the correct content" in {
        Jsoup.parse(contentAsString(result)).text() shouldBe "view"
      }

    }

    "an agent user is authenticated" should {

      lazy val result = {
        mockAgentAuth
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
  }
}
