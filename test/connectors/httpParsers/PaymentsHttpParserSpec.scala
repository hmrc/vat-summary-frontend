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

import java.time.LocalDate

import connectors.httpParsers.PaymentsHttpParser.PaymentsReads
import models.errors._
import models.payments.{Payment, Payments}
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.test.UnitSpec

class PaymentsHttpParserSpec extends UnitSpec {

  "PaymentsReads" when {

    "the http response status is 200 OK" should {

      val httpResponse = HttpResponse(Status.OK, responseJson = Some(
        Json.obj(
          "payments" -> Json.arr(
            Json.obj(
              "end" -> "2017-01-01",
              "due" -> "2017-10-25",
              "outstandingAmount" -> 1000,
              "status" -> "O",
              "periodKey" -> "#003"
            )
          )
        )
      ))

      val expected = Right(Payments(Seq(
        Payment(
          end = LocalDate.parse("2017-01-01"),
          due = LocalDate.parse("2017-10-25"),
          outstandingAmount = BigDecimal(1000.00),
          periodKey = "#003"
        )
      )))

      val result = PaymentsReads.read("", "", httpResponse)

      "return a Payments instance" in {
        result shouldEqual expected
      }

    }

    "the http response status is 400 BAD_REQUEST (single error)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "code" -> "VRN_INVALID",
          "message" -> "fail!"
        ))
      )

      val expected = Left(BadRequestError(
        code = "VRN_INVALID",
        message = "fail!"
      ))

      val result = PaymentsReads.read("", "", httpResponse)

      "return a BadRequestError" in {
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

      val result = PaymentsReads.read("", "", httpResponse)

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

      val result = PaymentsReads.read("", "", httpResponse)

      "return a UnknownError" in {
        result shouldEqual expected
      }

    }

    "the http response status is 5xx" should {

      val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR)

      val expected = Left(ServerSideError)

      val result = PaymentsReads.read("", "", httpResponse)

      "return a ServerSideError" in {
        result shouldEqual expected
      }

    }

    "the http response status is isn't handled" should {

      val httpResponse = HttpResponse(Status.CREATED)

      val expected = Left(UnexpectedStatusError(Status.CREATED))

      val result = PaymentsReads.read("", "", httpResponse)

      "return a UnexpectedStatusError" in {
        result shouldEqual expected
      }

    }

  }

}
