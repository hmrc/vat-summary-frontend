/*
 * Copyright 2020 HM Revenue & Customs
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

import common.TestJson.customerInfoJsonMax
import common.TestModels.customerInformationMax
import connectors.httpParsers.CustomerInfoHttpParser.CustomerInfoReads
import models.errors._
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.test.UnitSpec

class CustomerInfoHttpParserSpec extends UnitSpec {

  "CustomerInfoReads" when {

    "the HTTP response status is OK (200)" should {

      val httpResponse = HttpResponse(
        Status.OK,
        responseJson = Some(customerInfoJsonMax)
      )

      val expected = Right(customerInformationMax)

      val result = CustomerInfoReads.read("", "", httpResponse)

      "return a Trading Name" in {
        result shouldBe expected
      }
    }

    "the HTTP response status is BAD_REQUEST (400) (single error)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST, responseJson = Some(
        Json.obj(
          "code" -> "INVALID",
          "message" -> "Fail!"
        )
      ))

      val expected = Left(BadRequestError(
        code = "INVALID",
        errorResponse = "Fail!"
      ))

      val result = CustomerInfoReads.read("", "", httpResponse)

      "return a BadRequestError" in {
        result shouldBe expected
      }
    }


    "the HTTP response status is BAD_REQUEST (400) (multiple errors)" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_REQUEST, responseJson = Some(
        Json.obj(
          "code" -> "400",
          "message" -> "Fail!",
          "errors" -> Json.arr(
            Json.obj(
              "code" -> "INVALID",
              "message" -> "Fail!"
            ),
            Json.obj(
              "code" -> "INVALID_2",
              "message" -> "Fail!"
            )
          )
        )
      ))

      val errors = Seq(ApiSingleError("INVALID", "Fail!"), ApiSingleError("INVALID_2", "Fail!"))

      val expected = Left(MultipleErrors("400", Json.toJson(errors).toString()))

      val result = CustomerInfoReads.read("", "", httpResponse)

      "return a MultipleErrors" in {
        result shouldBe expected
      }
    }

    "the HTTP response status is BAD_REQUEST (400) (unknown error)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST, responseJson = Some(
        Json.obj(
          "foo" -> "INVALID",
          "bar" -> "Fail!"
        )
      ))

      val expected = Left(UnknownError)

      val result = CustomerInfoReads.read("", "", httpResponse)

      "return an UnknownError" in {
        result shouldBe expected
      }
    }

    "the HTTP response status is 5xx" should {

      val body: JsObject = Json.obj(
        "code" -> "GATEWAY_TIMEOUT",
        "message" -> "GATEWAY_TIMEOUT"
      )

      val httpResponse = HttpResponse(Status.GATEWAY_TIMEOUT, Some(body))
      val expected = Left(ServerSideError("504", httpResponse.body))
      val result = CustomerInfoReads.read("", "", httpResponse)

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
      val expected = Left(UnexpectedStatusError(Status.CONFLICT.toString, httpResponse.body))
      val result = CustomerInfoReads.read("", "", httpResponse)

      "return an UnexpectedStatusError" in {
        result shouldBe expected
      }
    }
  }
}
