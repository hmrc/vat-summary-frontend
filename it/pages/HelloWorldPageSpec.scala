/*
 * Copyright 2017 HM Revenue & Customs
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
import play.api.http.{HeaderNames, Status}
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.AuthStub

class HelloWorldPageSpec extends IntegrationBaseSpec {

  private trait Test {

    def setupStubs(): StubMapping
    def request(): WSRequest = {
      setupStubs()
      buildRequest("/hello-world")
    }
  }

  "Calling the hello-world route" when {

    "the user is authenticated" should {

      "return 200" in new Test {
        override def setupStubs(): StubMapping = AuthStub.authorised()
        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
      }
    }

    "the user is not authenticated" should {

      def setupStubsForScenario(): StubMapping = AuthStub.unauthorisedNotLoggedIn()

      "return 303" in new Test {
        override def setupStubs(): StubMapping = setupStubsForScenario()
        val response: WSResponse = await(request().get())
        response.status shouldBe Status.SEE_OTHER
      }

      "redirect to the session timeout page" in new Test {
        override def setupStubs(): StubMapping = setupStubsForScenario()
        val response: WSResponse = await(request().get())
        response.header(HeaderNames.LOCATION) shouldBe Some(s"$appRouteContext/session-timeout")
      }
    }

    "the user has a different enrolment" should {

      def setupStubsForScenario(): StubMapping = AuthStub.unauthorisedOtherEnrolment()

      "return 303" in new Test {
        override def setupStubs(): StubMapping = setupStubsForScenario()
        val response: WSResponse = await(request().get())
        response.status shouldBe Status.SEE_OTHER
      }

      "redirect to the unauthorised page" in new Test {
        override def setupStubs(): StubMapping = setupStubsForScenario()
        val response: WSResponse = await(request().get())
        response.header(HeaderNames.LOCATION) shouldBe Some(s"$appRouteContext/unauthorised")
      }
    }
  }
}
