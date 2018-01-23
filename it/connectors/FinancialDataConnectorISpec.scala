
package connectors

import java.time.LocalDate

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import models.obligations.Obligation.Status
import models.payments.{Payment, Payments}
import stubs.FinancialDataStub
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class FinancialDataConnectorISpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping

    val connector: FinancialDataConnector = app.injector.instanceOf[FinancialDataConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()
  }

  "calling getPaymentsForVatReturns with a status of 'O'" should {

    "return all outstanding payments for a given period" in new Test {
      override def setupStubs(): StubMapping = FinancialDataStub.stubAllOutstandingPayments

      val expected = Right(Payments(Seq(
        Payment(
          LocalDate.parse("2015-03-31"),
          LocalDate.parse("2019-01-15"),
          10000,
          "15AC"
        ),
        Payment(
          LocalDate.parse("2015-03-31"),
          LocalDate.parse("2019-01-16"),
          10000,
          "15AC"
        )
      )))

      setupStubs()
      private val result = await(connector.getPaymentsForVatReturns("123456789",
        LocalDate.now(),
        LocalDate.now(),
        Status.Outstanding))

      result shouldEqual expected
    }

  }

}
