
package testOnly.connectors

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import play.api.http.Status._
import testOnly.models.DataModel
import testOnly.stubs.DynamicStub
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class DynamicStubConnectorISpec extends IntegrationBaseSpec {

  private trait Test {

    def setupStubs(): StubMapping

    val connector: DynamicStubConnector = app.injector.instanceOf[DynamicStubConnector]

    implicit val hc: HeaderCarrier = HeaderCarrier()
  }


  "Calling clearStub" when {

    "the stub returns 200 OK" should {

      "return a 200 OK HttpResponse" in new Test {
        override def setupStubs(): StubMapping = DynamicStub.clearStubOkResponse()

        setupStubs()
        val result = connector.clearStub()

        result.status shouldBe OK
      }
    }

  }

  "Calling populateStub" when {

    "the stub returns 200 OK" should {

      "return a 200 OK HttpResponse" in new Test {
        override def setupStubs(): StubMapping = DynamicStub.populateStubOkResponse()

        setupStubs()
        val result = connector.populateStub(DataModel("/test", "GET", OK, None))

        result.status shouldBe OK
      }
    }
  }

}
