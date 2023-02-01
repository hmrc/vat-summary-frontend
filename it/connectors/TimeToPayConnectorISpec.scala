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

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import models.ESSTTP.{TTPRequestModel, TTPResponseModel}
import models.errors.UnexpectedStatusError
import play.api.http.Status.{CREATED, INTERNAL_SERVER_ERROR}
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers._
import stubs.TimeToPayStub
import uk.gov.hmrc.http.HeaderCarrier

class TimeToPayConnectorISpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping
    val connector: TimeToPayConnector = app.injector.instanceOf[TimeToPayConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()
  }

  val requestModel: TTPRequestModel = TTPRequestModel("/return-url", "/back-url")

  "Calling setupJourney" should {

    "return a TTPResponseModel when a successful response and expected JSON is returned by the API" in new Test {

      val responseJson: JsObject = Json.obj(
        "journeyId" -> "592d4a09cdc8e04b00021459",
        "nextUrl" -> "http://localhost:1111/test"
      )
      val responseModel: TTPResponseModel = TTPResponseModel("592d4a09cdc8e04b00021459", "http://localhost:1111/test")

      override def setupStubs(): StubMapping = TimeToPayStub.stubESSTTPBackend(CREATED, responseJson)

      val expected: Right[Nothing, TTPResponseModel] = Right(responseModel)

      setupStubs()
      private val result = await(connector.setupJourney(requestModel))

      result shouldBe expected
    }

    "return an error model when an unexpected HTTP status is returned by the API" in new Test {

      val responseJson: JsObject = Json.obj("statusCode" -> 500, "message" -> "Something went wrong")

      override def setupStubs(): StubMapping = TimeToPayStub.stubESSTTPBackend(INTERNAL_SERVER_ERROR, responseJson)

      val expected: Left[UnexpectedStatusError, Nothing] = Left(UnexpectedStatusError("500", responseJson.toString()))

      setupStubs()
      private val result = await(connector.setupJourney(requestModel))

      result shouldBe expected
    }
  }
}
