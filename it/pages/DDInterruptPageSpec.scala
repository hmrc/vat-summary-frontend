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

package pages

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.{AuthStub, CustomerInfoStub, FinancialDataStub}
import stubs.CustomerInfoStub.customerInfoJsonDD
import play.api.http.Status
import config.AppConfig
import play.api.test.Helpers._

class DDInterruptPageSpec extends IntegrationBaseSpec {
  val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
  val redirectUrl = "/vat-overview"
  private trait Test {
    def setupStubs(): StubMapping
    def request(): WSRequest = {
      setupStubs()
      CustomerInfoStub.stubCustomerInfo(customerInfoJsonDD(
        isPartialMigration = false, hasVerifiedEmail = true)
      )
      buildRequest(s"/direct-debit-interrupt?redirectUrl=$redirectUrl")
    }
  }

  "Calling the direct debit interrupt route with an authenticated user and the feature switch is on" when {

    "the user does not have a direct debit" should {

      "return 200" in new Test {
        appConfig.features.directDebitInterrupt(true)

         override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          FinancialDataStub.stubSuccessfulDirectDebit
        }
        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
      }
    }

  }

  "Calling the direct debit interrupt route with an authenticated user and the feature switch is off" when {

      "return 303" in new Test {
        appConfig.features.directDebitInterrupt(false)

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          FinancialDataStub.stubSuccessfulDirectDebit
        }
        val response: WSResponse = await(request().get())
        response.status shouldBe Status.SEE_OTHER
      }
  }
}
