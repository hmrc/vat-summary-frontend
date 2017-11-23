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

import models.{BadRequestError, Obligation, Obligations, ServerSideError}
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

    "the http response status is 400 BAD_REQUEST" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST)

      val expected = Left(BadRequestError)

      val result = ObligationsReads.read("", "", httpResponse)

      "return a BadRequestError" in {
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

  }

}
