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

package connectors.httpParsers

import common.TestModels._
import connectors.httpParsers.TimeToPayHttpParser.TimeToPayReads
import models.errors.{BadRequestError, UnexpectedStatusError}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse

class TimeToPayHttpParserSpec extends AnyWordSpecLike with Matchers{

  "TimeToPayReads" when {

    "the http response status is 200 OK" should {

      val jsonResponse = timeToPayResponseJson
      val httpResponse = HttpResponse(Status.OK,jsonResponse.toString())
      val expected = Right(timeToPayResponseModel)
      val result = TimeToPayReads.read("","",httpResponse)

      "return a time to pay instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        Json.obj(
          "code" -> "400",
          "message" -> "'taxType' This field is mandatory"
        ).toString()
      )

      val expected = Left(BadRequestError(
        code = "400",
        errorResponse = "'taxType' This field is mandatory"
      ))

      val result = TimeToPayReads.read("","", httpResponse)

      "return a BadRequestError" in {
        result shouldEqual expected
      }

    }

    "the HTTP response status isn't handled" should {

      val body = Json.obj(
        "code" -> "Conflict",
        "message" -> "CONFLCIT"
      )

      val httpResponse = HttpResponse(Status.CONFLICT, body.toString())
      val expected = Left(UnexpectedStatusError(Status.CONFLICT.toString, httpResponse.body))
      val result = TimeToPayReads.read("", "", httpResponse)

      "return an UnexpectedStatusError" in {
        result shouldBe expected
      }
    }

  }


}
