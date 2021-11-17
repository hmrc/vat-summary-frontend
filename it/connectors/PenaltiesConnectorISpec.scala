
package connectors

import config.AppConfig
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import helpers.IntegrationBaseSpec
import models.errors.PenaltiesFeatureSwitchError
import models.penalties.PenaltiesSummary
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import stubs.PenaltiesStub
import uk.gov.hmrc.http.HeaderCarrier

class PenaltiesConnectorISpec extends IntegrationBaseSpec {

  private trait Test{
    val appConfig: AppConfig = app.injector.instanceOf[AppConfig]
    val connector: PenaltiesConnector = app.injector.instanceOf[PenaltiesConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()
  }

  "calling getPenaltiesDataForVRN" when {
    "when the feature switch is enabled" should {
      "return a successful response and PenaltySummary model from the penalties API" in new Test {
        appConfig.features.penaltiesServiceEnabled(true)

        val responseBody: JsValue = Json.parse(
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
        PenaltiesStub.stubPenaltiesSummary(OK, responseBody, "123")
        val expectedContent: PenaltiesSummary = PenaltiesSummary(
          noOfPoints = 3,
          noOfEstimatedPenalties = 2,
          noOfCrystalisedPenalties = 1,
          estimatedPenaltyAmount = 123.45,
          crystalisedPenaltyAmountDue = 54.32,
          hasAnyPenaltyData = true
        )

        val result: HttpGetResult[PenaltiesSummary] = await(connector.getPenaltiesDataForVRN("123"))
        result shouldBe Right(expectedContent)
      }

      "return an Empty PenaltiesSummary model when given an invalid vrn" in new Test {
        appConfig.features.penaltiesServiceEnabled(true)
        val responseBody: JsValue = Json.parse(
          """
            |{
            | "code": "foo",
            | "message": "bar"
            |}
            |""".stripMargin)
        PenaltiesStub.stubPenaltiesSummary(NOT_FOUND, responseBody, "1FOO2")
        val expectedContent: PenaltiesSummary = PenaltiesSummary(
          noOfPoints = 0,
          noOfEstimatedPenalties = 0,
          noOfCrystalisedPenalties = 0,
          estimatedPenaltyAmount = 0,
          crystalisedPenaltyAmountDue = 0,
          hasAnyPenaltyData = false
        )

        val result: HttpGetResult[PenaltiesSummary] = await(connector.getPenaltiesDataForVRN("1FOO2"))
        result shouldBe Right(expectedContent)
      }
    }

    "when the feature switch is disabled" should {
      "return the custom penalties feature switch error" in new Test {
        appConfig.features.penaltiesServiceEnabled(false)
        val result: HttpGetResult[PenaltiesSummary] = await(connector.getPenaltiesDataForVRN("123"))
        result shouldBe Left(PenaltiesFeatureSwitchError)
      }
    }
  }

}
