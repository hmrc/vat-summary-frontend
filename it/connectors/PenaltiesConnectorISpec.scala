/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors

import connectors.httpParsers.ResponseHttpParsers.HttpResult
import helpers.IntegrationBaseSpec
import models.penalties.PenaltiesSummary
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import stubs.PenaltiesStub
import uk.gov.hmrc.http.HeaderCarrier

class PenaltiesConnectorISpec extends IntegrationBaseSpec {

  private trait Test{
    val connector: PenaltiesConnector = app.injector.instanceOf[PenaltiesConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()
  }

  "calling getPenaltiesDataForVRN" should {

    "return a successful response and PenaltySummary model from the penalties API" in new Test {

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

      val result: HttpResult[PenaltiesSummary] = await(connector.getPenaltiesDataForVRN("123"))
      result shouldBe Right(expectedContent)
    }

    "return an Empty PenaltiesSummary model when given an invalid vrn" in new Test {
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

      val result: HttpResult[PenaltiesSummary] = await(connector.getPenaltiesDataForVRN("1FOO2"))
      result shouldBe Right(expectedContent)
    }
  }
}
