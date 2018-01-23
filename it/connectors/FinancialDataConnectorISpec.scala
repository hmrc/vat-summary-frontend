
package connectors

import java.time.LocalDate

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import models.errors.BadRequestError
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

  "calling getVatReturnObligations with an invalid VRN" should {

    "return an BadRequestError" in new Test {
      override def setupStubs(): StubMapping = FinancialDataStub.stubInvalidVrn

      val expected = Left(BadRequestError(
        code = "VRN_INVALID",
        message = ""
      ))

      setupStubs()
      private val result = await(connector.getPaymentsForVatReturns("111",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-12-31"),
        Status.Outstanding))

      result shouldEqual expected
    }

  }

  "calling getVatReturnObligations with an invalid 'from' date" should {

    "return an BadRequestError" in new Test {
      override def setupStubs(): StubMapping = FinancialDataStub.stubInvalidFromDate

      val expected = Left(BadRequestError(
        code = "INVALID_DATE_FROM",
        message = ""
      ))

      setupStubs()
      private val result = await(connector.getPaymentsForVatReturns("111",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-12-31"),
        Status.Outstanding))

      result shouldEqual expected
    }

  }

  "calling getVatReturnObligations with an invalid 'to' date" should {

    "return an BadRequestError" in new Test {
      override def setupStubs(): StubMapping = FinancialDataStub.stubInvalidToDate

      val expected = Left(BadRequestError(
        code = "INVALID_DATE_TO",
        message = ""
      ))

      setupStubs()
      private val result = await(connector.getPaymentsForVatReturns("111",
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-12-31"),
        Status.Outstanding))

      result shouldEqual expected
    }

  }

}
