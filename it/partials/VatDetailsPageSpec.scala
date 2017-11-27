
package partials

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import play.api.http.Status
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.AuthStub

class VatDetailsPageSpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest("/details")
    }
  }

  "Calling the /details route" when {

    "the user is authenticated" should {

      "return 200" in new Test {
        override def setupStubs(): StubMapping = AuthStub.authorisedBtaPartial()
        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
      }
    }

    "the user is not authenticated" should {

      def setupStubsForScenario(): StubMapping = AuthStub.unauthorisedNotLoggedIn()

      "return 401" in new Test {
        override def setupStubs(): StubMapping = setupStubsForScenario()
        val response: WSResponse = await(request().get())
        response.status shouldBe Status.UNAUTHORIZED
      }
    }

    "the user has a different enrolment" should {

      def setupStubsForScenario(): StubMapping = AuthStub.unauthorisedOtherEnrolmentBtaPartial()

      "return 403" in new Test {
        override def setupStubs(): StubMapping = setupStubsForScenario()
        val response: WSResponse = await(request().get())
        response.status shouldBe Status.FORBIDDEN
      }
    }

  }

}
