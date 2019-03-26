/*
 * Copyright 2018 HM Revenue & Customs
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
import config.AppConfig
import helpers.IntegrationBaseSpec
import play.api.http.Status
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs._

class VatDetailsPageSpec extends IntegrationBaseSpec {
  val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  val obligationsStub: VatObligationsStub = new VatObligationsStub(
    app.configuration.underlying.getBoolean("features.useVatObligationsService.enabled")
  )

  private trait Test {
    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest("/vat-overview")
    }
  }

  "Calling the /details route" when {

    "the user is authenticated" should {

      "return 200 and 'View returns deadline'" in new Test {
        appConfig.features.submitReturnFeatures(true)

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          obligationsStub.stubOutstandingObligations
          CustomerInfoStub.stubCustomerInfo()
          CustomerInfoStub.stubCustomerMandationStatus()
          FinancialDataStub.stubAllOutstandingOpenPayments
        }
        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
        response.body.contains("View return deadlines") shouldBe true
      }

      "return 200 and 'Submit return' when Non MTDfB" in {
        appConfig.features.submitReturnFeatures(true)

        new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            obligationsStub.stubOutstandingObligations
            CustomerInfoStub.stubCustomerInfo()
            CustomerInfoStub.stubCustomerMandationStatus(Json.obj("mandationStatus" -> "Non MTDfB"))
            FinancialDataStub.stubAllOutstandingOpenPayments
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.OK
          response.body.contains("Submit return") shouldBe true
        }
      }
    }

    "the user is not authenticated" should {

      def setupStubsForScenario(): StubMapping = AuthStub.unauthorisedNotLoggedIn()

      "return 401 (Unauthorised)" in new Test {
        override def setupStubs(): StubMapping = setupStubsForScenario()
        val response: WSResponse = await(request().get())
        response.status shouldBe Status.UNAUTHORIZED
      }
    }
  }
}
