/*
 * Copyright 2018 HM Revenue & Customs
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
import models.errors._
import play.api.http.Status
import play.api.libs.json.{JsObject, Json, JsValue}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.test.UnitSpec

class DirectDebitStatusHttpParserSpec extends UnitSpec {

  "DirectDebitStatusReads" when {

    "the http response status is 200 OK" should {

      val httpResponse = HttpResponse(Status.OK, responseJson = Some(Json.parse("true")))

      val expected = Right(true)

      val result = DirectDebitStatusReads.read("", "", httpResponse)

      "return a DirectDebitStatus instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 404 NOT_FOUND" should {

      val httpResponse = HttpResponse(Status.NOT_FOUND, None)

      val expected = Left(UnexpectedStatusError("404", null))

      val result = DirectDebitStatusReads.read("", "", httpResponse)

      "return a 404 error" in {
        result shouldEqual expected
      }
    }

    "the http response status is 400 BAD_REQUEST (single error)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "code" -> "VRN_INVALID",
          "reason" -> "Fail!"
        ))
      )

      val expected = Left(BadRequestError(
        code = "VRN_INVALID",
        errorResponse = "Fail!"
      ))

      val result = DirectDebitStatusReads.read("", "", httpResponse)

      "return a BadRequestError" in {
        result shouldEqual expected
      }
    }

    "the http response status is 400 BAD_REQUEST (unknown API error json)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "foo" -> "INVALID",
          "bar" -> "Fail!"
        ))
      )

      val expected = Left(UnknownError)

      val result = DirectDebitStatusReads.read("", "", httpResponse)

      "return a UnknownError" in {
        result shouldEqual expected
      }
    }


    "the HTTP response status is 5xx" should {

      val body: JsObject = Json.obj(
        "code" -> "GATEWAY_TIMEOUT",
        "message" -> "GATEWAY_TIMEOUT"
      )

      val httpResponse = HttpResponse(Status.GATEWAY_TIMEOUT, Some(body))
      val expected = Left(ServerSideError(Status.GATEWAY_TIMEOUT.toString, httpResponse.body))
      val result = DirectDebitStatusReads.read("", "", httpResponse)

      "return a ServerSideError" in {
        result shouldBe expected
      }
    }

    "the HTTP response status isn't handled" should {

      val body: JsObject = Json.obj(
        "code" -> "Conflict",
        "message" -> "CONFLCIT"
      )

      val httpResponse = HttpResponse(Status.CONFLICT, Some(body))
      val expected = Left(UnexpectedStatusError("409", httpResponse.body))
      val result = DirectDebitStatusReads.read("", "", httpResponse)

      "return an UnexpectedStatusError" in {
        result shouldBe expected
      }
    }
  }
}