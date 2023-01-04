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

package pages

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import play.api.http.Status
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.CustomerInfoStub.customerInfoJson
import stubs.{AuthStub, CustomerInfoStub, ServiceInfoStub}
import play.api.test.Helpers._

class VatCertificatePageSpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping

    def request(isAgent: Boolean = false): WSRequest = {
      setupStubs()
      CustomerInfoStub.stubCustomerInfo(customerInfoJson(
        isPartialMigration = false,
        hasVerifiedEmail = true)
      )

      if (isAgent) {
        buildRequest("/vat-certificate", formatSessionVrn(Some("1112221112")))
      } else {
        ServiceInfoStub.stubServiceInfoPartial
        buildRequest("/vat-certificate")
      }
    }
  }

  "Calling the vat certificate route with an authenticated user" when {

    "the user is a non agent" should {

      "return 200 with the agent title" in new Test {
        override def setupStubs(): StubMapping = AuthStub.authorised()
        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
        response.body.contains("Your VAT Certificate") shouldBe true
      }
    }

    "the user is an agent" should {

      "return 200 with the non-agent title" in new Test {
        override def setupStubs(): StubMapping = AuthStub.agentAuthorised()
        val response: WSResponse = await(request(true).get())
        response.status shouldBe Status.OK
        response.body.contains("Your client’s VAT Certificate") shouldBe true
      }
    }
  }
}
