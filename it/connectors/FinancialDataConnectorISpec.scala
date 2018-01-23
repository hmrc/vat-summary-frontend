
package connectors

import java.time.LocalDate

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import models.obligations.Obligation.Status
import models.payments.Payments
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class FinancialDataConnectorISpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping

    val connector: FinancialDataConnector = app.injector.instanceOf[FinancialDataConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()
  }

  "calling getPaymentsForVatReturns with a status of 'A'" should {

    "return all payments for a given period" in new Test {
      override def setupStubs(): StubMapping = ???

      val expected = Right(Payments(Seq.empty))

      setupStubs()
      private val result = await(connector.getPaymentsForVatReturns("123456789",
        LocalDate.now(),
        LocalDate.now(),
        Status.All))

      result shouldEqual expected
    }

  }

}
