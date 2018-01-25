
package connectors

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import models.CustomerInformation
import models.errors.ServerSideError
import stubs.CustomerInfoStub
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class CustomerInformationConnectorISpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping

    val connector: CustomerInformationConnector = app.injector.instanceOf[CustomerInformationConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()
  }

  "calling getCustomerInfo" should {

    "return a user's customer information" in new Test {
      override def setupStubs(): StubMapping = CustomerInfoStub.stubCustomerInfo

      val expected = Right(CustomerInformation("Vincent", "Vatreturn", "Cheapo Clothing"))

      setupStubs()
      private val result = await(connector.getCustomerInfo("1111"))

      result shouldEqual expected
    }

    "return an HttpError if one is received" in new Test {
      override def setupStubs(): StubMapping = CustomerInfoStub.stubErrorFromApi

      val expected = Left(ServerSideError)

      setupStubs()
      private val result = await(connector.getCustomerInfo("1111"))

      result shouldEqual expected
    }

  }

}
