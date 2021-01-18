/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.libs.json.JsValue
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.CustomerInfoStub.{customerInfoJson, customerInfoJsonNonMtdfb}
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
        appConfig.features.r17Content(true)

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          obligationsStub.stubOutstandingObligations
          CustomerInfoStub.stubCustomerInfo(customerInfoJson(
            isPartialMigration = false,
            hasVerifiedEmail = true))
          FinancialDataStub.stubOutstandingTransactions
          ServiceInfoStub.stubServiceInfoPartial
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
        response.body.contains("View return deadlines") shouldBe true
        response.body.contains("You need to confirm your email address") shouldBe false
      }

      "return 200 and 'Submit VAT Return' when Non MTDfB" in {
        appConfig.features.submitReturnFeatures(true)

        new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            obligationsStub.stubOutstandingObligations
            CustomerInfoStub.stubCustomerInfo(customerInfoJsonNonMtdfb(
              isPartialMigration = false,
              hasVerifiedEmail = true)
            )
            FinancialDataStub.stubOutstandingTransactions
            ServiceInfoStub.stubServiceInfoPartial
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.OK
          response.body.contains("Submit VAT Return") shouldBe true
        }
      }

      "return 200 when user's email is not verified" in new Test {
        appConfig.features.submitReturnFeatures(true)
        appConfig.features.r17Content(true)

        val customerDataToUse: JsValue = CustomerInfoStub.customerInfoJson(
          isPartialMigration = false,
          hasVerifiedEmail = false)

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          obligationsStub.stubOutstandingObligations
          CustomerInfoStub.stubCustomerInfo(customerDataToUse)
          FinancialDataStub.stubOutstandingTransactions
          ServiceInfoStub.stubServiceInfoPartial
        }

        val response: WSResponse = await(request().get())

        response.status shouldBe Status.OK
        response.body.contains("You need to confirm your email address") shouldBe true
      }
    }

    "the user is not signed in" should {

      def setupStubsForScenario(): StubMapping = AuthStub.unauthorisedNotLoggedIn()

      "return 303 (SEE_OTHER)" in new Test {
        override def setupStubs(): StubMapping = setupStubsForScenario()
        val response: WSResponse = await(request().get())

        response.status shouldBe Status.SEE_OTHER
      }
    }
  }
}
