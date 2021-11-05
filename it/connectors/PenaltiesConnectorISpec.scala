
package connectors

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import config.AppConfig
import helpers.IntegrationBaseSpec
import models.penalties.PenaltiesSummary
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import stubs.PenaltiesStub
import uk.gov.hmrc.http.HeaderCarrier

class PenaltiesConnectorISpec extends IntegrationBaseSpec {

  private trait Test{

    def setupStubs(): StubMapping
    val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
    val connector: PenaltiesConnector = app.injector.instanceOf[PenaltiesConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()
  }

  "calling getPenaltiesDataForVRN" when {
    "when the feature switch is enabled" should {
      "return a successful response and PenaltySummary model from the penalties API" in new Test {
        appConfig.features.directDebitInterrupt(true)

        val responseBody = Json.parse(
          """
            |{
            |  "noOfPoints": 3,
            |  "noOfEstimatedPenalties": 2,
            |  "noOfCrystalisedPenalties": 1,
            |  "estimatedPenaltyAmount": 123.45,
            |  "crystalisedPenaltyAmountDue": 54.32,
            |  "hasAnyPenaltyData": true
            |}
            |""".stripMargin)
        override def setupStubs(): StubMapping = PenaltiesStub.stubPenaltiesSummary(OK, responseBody)
        val expectedContent: PenaltiesSummary = PenaltiesSummary(
          noOfPoints = 3,
          noOfEstimatedPenalties = 2,
          noOfCrystalisedPenalties = 1,
          estimatedPenaltyAmount = 123.45,
          crystalisedPenaltyAmountDue = 54.32,
          hasAnyPenaltyData = true
        )
        setupStubs()

        val result = await(connector.getPenaltiesDataForVRN("123"))
        result shouldBe Right(expectedContent)
      }

      "return an Empty PenaltiesSummary model when given an invalid vrn" in new Test {
        appConfig.features.directDebitInterrupt(true)
        val responseBody = Json.parse(
          """
            |{
            | "code": "foo",
            | "message": "bar"
            |}
            |""".stripMargin)
        override def setupStubs(): StubMapping = PenaltiesStub.stubPenaltiesSummary(NOT_FOUND, responseBody)
        val expectedContent: PenaltiesSummary = PenaltiesSummary(
          noOfPoints = 0,
          noOfEstimatedPenalties = 0,
          noOfCrystalisedPenalties = 0,
          estimatedPenaltyAmount = 0,
          crystalisedPenaltyAmountDue = 0,
          hasAnyPenaltyData = false
        )

        val result = await(connector.getPenaltiesDataForVRN("1FOO2"))
        result shouldBe Right(expectedContent)
      }


    }

    "when the feature switch is disabled" should {
      "return None" in new Test {
        appConfig.features.directDebitInterrupt(false)
        val responseBody = Json.parse(
          """
            |{
            |}
            |""".stripMargin)
        override def setupStubs(): StubMapping = PenaltiesStub.stubPenaltiesSummary(OK, responseBody)
        val result = await(connector.getPenaltiesDataForVRN("123"))
        result shouldBe None

      }
    }
  }

}
