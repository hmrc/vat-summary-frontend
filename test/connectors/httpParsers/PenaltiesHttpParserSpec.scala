/*
 * Copyright 2021 HM Revenue & Customs
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

import connectors.httpParsers.DirectDebitStatusHttpParser.DirectDebitStatusReads
import connectors.httpParsers.PenaltiesHttpParser.PenaltiesReads
import models.errors.{BadRequestError, ServerSideError, UnexpectedStatusError}
import models.penalties.PenaltiesSummary
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.{JsObject, Json}
import play.api.http.Status
import models.errors._
import uk.gov.hmrc.http.HttpResponse

class PenaltiesHttpParserSpec extends AnyWordSpecLike with Matchers {

  "PenaltiesHttpParser" when {

    "the http response status is 200 OK" should {
      val jsonResponse = Json.obj(
        "noOfPoints" -> 3,
        "noOfEstimatedPenalties" -> 2,
        "noOfCrystalisedPenalties" -> 1,
        "estimatedPenaltyAmount" -> 123.45,
        "crystalisedPenaltyAmountDue" -> 54.32,
        "hasAnyPenaltyData" -> true
      )
      val httpResponse = HttpResponse(Status.OK, jsonResponse.toString())
      val expected = Right(PenaltiesSummary(
        noOfPoints = 3,
        noOfEstimatedPenalties = 2,
        noOfCrystalisedPenalties = 1,
        estimatedPenaltyAmount = 123.45,
        crystalisedPenaltyAmountDue = 54.32,
        hasAnyPenaltyData = true
      ))
      val result = PenaltiesReads.read("", "", httpResponse)

      "return a PenaltiesSummaryInstance" in {
        result shouldBe expected
      }
    }

    "the http response status is 404 NOT_FOUND" should {
      val httpResponse = HttpResponse(Status.NOT_FOUND, "")
      val expected = Right(PenaltiesSummary.empty)
      val result = PenaltiesReads.read("", "", httpResponse)

      "return a 404 error" in {
        result shouldEqual expected
      }
    }

    "the http response status is 400 BAD_REQUEST (single error)" should {
      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        Json.obj(
          "code" -> "VRN_INVALID",
          "reason" -> "Fail"
        ).toString()
      )
      val expected = Left(BadRequestError(
        code = "VRN_INVALID",
        errorResponse = "Fail"
      ))
      val result = PenaltiesReads.read("", "", httpResponse)

      "return a BadRequestError" in {
        result shouldEqual expected
      }
    }

    "the http response status is 400 BAD_REQUEST (unknown API error json)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        Json.obj(
          "foo" -> "VRN_INVALID",
          "bar" -> "Fail"
        ).toString()
      )
      val expected = Left(UnknownError)

      val result = PenaltiesReads.read("", "", httpResponse)

      "return a UnknownError" in {
        result shouldEqual expected
      }
    }

    "the HTTP response status is 5xx" should {
      val body: JsObject = Json.obj(
        "code" -> "GATEWAY_TIMEOUT",
        "message" -> "GATEWAY_TIMEOUT"
      )

      val httpResponse = HttpResponse(Status.GATEWAY_TIMEOUT, body.toString())
      val expected = Left(ServerSideError(Status.GATEWAY_TIMEOUT.toString, httpResponse.body))
      val result = PenaltiesReads.read("", "", httpResponse)

      "return a ServerSideError" in {
        result shouldBe expected
      }
    }

    "the HTTP response status isn't handled" should {
      val body: JsObject = Json.obj(
        "code" -> "Conflict",
        "message" -> "CONFLCIT"
      )

      val httpResponse = HttpResponse(Status.CONFLICT, body.toString())
      val expected = Left(UnexpectedStatusError("409", httpResponse.body))
      val result = PenaltiesReads.read("", "", httpResponse)

      "return an UnexpectedStatusError" in {
        result shouldBe expected
      }
    }
  }

}
