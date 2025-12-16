/*
 * Copyright 2025 HM Revenue & Customs
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

package connectors.httpParsers

import connectors.httpParsers.ResponseHttpParsers.HttpResult
import connectors.httpParsers.StandingRequestsHttpParser.StandingRequestsResponseReads
import models.StandingRequest
import models.errors._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.http.Status
import play.api.libs.json.{JsError, JsValue, Json}
import uk.gov.hmrc.http.HttpResponse

class StandingRequestsHttpParserSpec extends AnyWordSpecLike with Matchers {

  private val sampleStandingRequestJson: JsValue = Json.parse(
    """
      |{
      |  "processingDate": "2024-07-15T09:30:47Z",
      |  "standingRequests": [
      |    {
      |      "requestNumber": "20000037272",
      |      "requestCategory": "4",
      |      "createdOn": "2023-11-30",
      |      "changedOn": "2024-12-26",
      |      "requestItems": [
      |        {
      |          "period": "1",
      |          "periodKey": "24A1",
      |          "startDate": "2024-02-01",
      |          "endDate": "2024-04-30",
      |          "dueDate": "2024-03-31",
      |          "amount": 22945.23,
      |          "chargeReference": "XD006411191344",
      |          "postingDueDate": "2024-03-31"
      |        }
      |      ]
      |    }
      |  ]
      |}
      |""".stripMargin
  )

  "StandingRequestsResponseReads.read" should {

    "return a StandingRequest when the response is OK with valid JSON" in {
      val httpResponse = HttpResponse(Status.OK, sampleStandingRequestJson.toString())

      val result: HttpResult[StandingRequest] = StandingRequestsResponseReads.read("GET", "/test", httpResponse)

      result shouldBe Right(sampleStandingRequestJson.as[StandingRequest])
    }

    "return an UnexpectedStatusError when the response is OK but JSON cannot be parsed" in {
      val httpResponse = HttpResponse(Status.OK, Json.obj().toString())
      val errors = Json.obj().validate[StandingRequest].asInstanceOf[JsError].errors

      val result = StandingRequestsResponseReads.read("GET", "/test", httpResponse)

      result shouldBe Left(UnexpectedStatusError(Status.OK.toString, s"JSON Parsing Error: $errors"))
    }

    "return an empty StandingRequest when the response is NOT_FOUND" in {
      val httpResponse = HttpResponse(Status.NOT_FOUND, "")

      val result = StandingRequestsResponseReads.read("GET", "/test", httpResponse)

      result shouldBe Right(StandingRequest("", List.empty))
    }

    "map BAD_REQUEST errors using handleBadRequest" in {
      val httpResponse = HttpResponse(
        Status.BAD_REQUEST,
        Json.obj("code" -> "INVALID_VRN", "reason" -> "Bad vrn").toString()
      )

      val result = StandingRequestsResponseReads.read("GET", "/test", httpResponse)

      result shouldBe Left(BadRequestError("INVALID_VRN", "Bad vrn"))
    }

    "map 5xx responses to ServerSideError" in {
      val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, "boom")

      val result = StandingRequestsResponseReads.read("GET", "/test", httpResponse)

      result shouldBe Left(ServerSideError(Status.INTERNAL_SERVER_ERROR.toString, "boom"))
    }

    "map unexpected responses to UnexpectedStatusError" in {
      val httpResponse = HttpResponse(Status.IM_A_TEAPOT, "teapot")

      val result = StandingRequestsResponseReads.read("GET", "/test", httpResponse)

      result shouldBe Left(UnexpectedStatusError(Status.IM_A_TEAPOT.toString, "teapot"))
    }
  }
}
