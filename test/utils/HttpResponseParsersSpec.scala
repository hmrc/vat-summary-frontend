/*
 * Copyright 2017 HM Revenue & Customs
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

package utils

import java.time.LocalDate

import models._
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.test.UnitSpec
import utils.HttpResponseParsers.ObligationsReads

class HttpResponseParsersSpec extends UnitSpec {

  "ObligationsReads" when {

    "the http response status is 200 OK" should {

      val httpResponse = HttpResponse(Status.OK, responseJson = Some(
        Json.obj(
          "obligations" -> Json.arr(
            Json.obj(
              "start" -> "2017-01-01",
              "end" -> "2017-03-30",
              "due" -> "2017-04-30",
              "status" -> "O",
              "periodKey" -> "#001"
            )
          )
        )
      ))

      val expected = Right(Obligations(Seq(
        Obligation(
          start = LocalDate.parse("2017-01-01"),
          end = LocalDate.parse("2017-03-30"),
          due = LocalDate.parse("2017-04-30"),
          status = "O",
          received = None,
          periodKey = "#001"
        )
      )))

      val result = ObligationsReads.read("", "", httpResponse)

      "return an Obligations instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (invalid VRN)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "code" -> "VRN_INVALID",
          "message" -> "fail!"
        ))
      )

      val expected = Left(InvalidVrnError)

      val result = ObligationsReads.read("", "", httpResponse)

      "return a InvalidVrnError" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (invalid 'from' date)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "code" -> "INVALID_DATE_FROM",
          "message" -> "fail!"
        ))
      )

      val expected = Left(InvalidFromDateError)

      val result = ObligationsReads.read("", "", httpResponse)

      "return a InvalidFromDateError" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (invalid 'to' date)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "code" -> "INVALID_DATE_TO",
          "message" -> "fail!"
        ))
      )

      val expected = Left(InvalidToDateError)

      val result = ObligationsReads.read("", "", httpResponse)

      "return a InvalidToDateError" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (invalid date range)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "code" -> "INVALID_DATE_RANGE",
          "message" -> "fail!"
        ))
      )

      val expected = Left(InvalidDateRangeError)

      val result = ObligationsReads.read("", "", httpResponse)

      "return a InvalidDateRangeError" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (invalid obligation status)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "code" -> "INVALID_STATUS",
          "message" -> "fail!"
        ))
      )

      val expected = Left(InvalidStatusError)

      val result = ObligationsReads.read("", "", httpResponse)

      "return a InvalidStatusError" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (unknown API error code)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "code" -> "RED_CAR",
          "message" -> "fail!"
        ))
      )

      val expected = Left(UnknownError)

      val result = ObligationsReads.read("", "", httpResponse)

      "return a UnknownError" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (multiple errors)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "code" -> "BAD_REQUEST",
          "message" -> "fail!",
          "errors" -> Json.arr(
            Json.obj(
              "code" -> "INVALID_DATE_FROM",
              "message" -> "Bad date from",
              "path" -> "/from"
            ),
            Json.obj(
              "code" -> "INVALID_DATE_TO",
              "message" -> "Bad date to",
              "path" -> "/to"
            )
          )
        ))
      )

      val expected = Left(MultipleErrors)

      val result = ObligationsReads.read("", "", httpResponse)

      "return a MultipleErrors" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (unknown API error json)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "foo" -> "RED_CAR",
          "bar" -> "fail!"
        ))
      )

      val expected = Left(UnknownError)

      val result = ObligationsReads.read("", "", httpResponse)

      "return a UnknownError" in {
        result shouldEqual expected
      }

    }

    "the http response status is 5xx" should {

      val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR)

      val expected = Left(ServerSideError)

      val result = ObligationsReads.read("", "", httpResponse)

      "return a ServerSideError" in {
        result shouldEqual expected
      }

    }

    "the http response status is isn't handled" should {

      val httpResponse = HttpResponse(Status.CREATED)

      val expected = Left(UnexpectedStatusError(Status.CREATED))

      val result = ObligationsReads.read("", "", httpResponse)

      "return a UnexpectedStatusError" in {
        result shouldEqual expected
      }

    }

  }

}
