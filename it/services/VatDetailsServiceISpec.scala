
package services

import java.time.LocalDate

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import connectors.httpParsers.ObligationsHttpParser._
import helpers.IntegrationBaseSpec
import models.errors.BadRequestError
import models.{Obligation, User}
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

  "Calling getVatDetails" should {

    "return the user's latest obligation" in new Test {
      override def setupStubs(): StubMapping = VatApiStub.stubOutstandingObligations

      val expected = Right(Obligation(
        start = LocalDate.parse("2017-04-01"),
        end = LocalDate.parse("2017-07-30"),
        due = LocalDate.parse("2017-08-30"),
        status = "O",
        received = None,
        periodKey = "#004"
      ))

      val result: HttpGetResult[Obligation] = await(service.getVatDetails(User("1111")))

      result shouldBe expected
    }

    "return a BadRequestError (VRN_INVALID)" in new Test {
      override def setupStubs(): StubMapping = VatApiStub.stubInvalidVrn

      val expected = Left(BadRequestError("VRN_INVALID", ""))

      val result: HttpGetResult[Obligation] = await(service.getVatDetails(User("1111")))

      result shouldBe expected
    }

  }

}
