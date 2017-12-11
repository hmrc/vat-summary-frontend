
package services

import java.time.LocalDate
import java.time.temporal.ChronoUnit

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import connectors.httpParsers.ObligationsHttpParser._
import helpers.IntegrationBaseSpec
import models.errors.BadRequestError
import models.{Obligation, Payment, Payments, User}
import stubs.VatApiStub
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class VatDetailsServiceISpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping

    implicit val hc: HeaderCarrier = HeaderCarrier()
    lazy val service: VatDetailsService = app.injector.instanceOf[VatDetailsService]
    setupStubs()
  }

  "Calling getVatDetails" when {

    "the user has outstanding obligations" should {

      "return the user's latest obligation" in new Test {
        override def setupStubs(): StubMapping = VatApiStub.stubOutstandingObligations

        val expected = Right(Some(Obligation(
          start = LocalDate.now().minus(70L, ChronoUnit.DAYS),
          end = LocalDate.now().minus(40L, ChronoUnit.DAYS),
          due = LocalDate.now().minus(30L, ChronoUnit.DAYS),
          status = "O",
          received = None,
          periodKey = "#004"
        )))

        val result: HttpGetResult[Option[Obligation]] = await(service.getVatDetails(User("1111")))

        result shouldBe expected
      }

    }

    "the user has no outstanding obligations" should {

      "return nothing" in new Test {
        override def setupStubs(): StubMapping = VatApiStub.stubNoObligations

        val expected = Right(None)

        val result: HttpGetResult[Option[Obligation]] = await(service.getVatDetails(User("1111")))

        result shouldBe expected
      }

    }

    "the user has an invalid VRN" should {

      "return a BadRequestError" in new Test {
        override def setupStubs(): StubMapping = VatApiStub.stubInvalidVrn

        val expected = Left(BadRequestError("VRN_INVALID", ""))

        val result: HttpGetResult[Option[Obligation]] = await(service.getVatDetails(User("1111")))

        result shouldBe expected
      }

    }

  }

  "Calling getPaymentData" should {

    "return a Payments m" in new Test {

      override def setupStubs(): StubMapping = VatApiStub.stubInvalidVrn
      val expectedPayments: Payments = Payments(
        Seq(
          Payment(
            endDate = LocalDate.parse("2017-01-01"),
            dueDate = LocalDate.parse("2017-10-25"),
            outstandingAmount = BigDecimal(1000.00),
            status = "O",
            periodKey = "#003"
          ),
          Payment
          (
            endDate = LocalDate.parse("2017-10-19"),
            dueDate = LocalDate.parse("2017-12-25"),
            outstandingAmount = BigDecimal(10.00),
            status = "O",
            periodKey = "#001"
          )
        )
      )

      val result: Payments = await(service.getPaymentData(User("11111111")))

      result shouldBe expectedPayments

    }
  }
}
