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
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.test.UnitSpec

class PaymentsHttpParserSpec extends UnitSpec {

  "PaymentsReads" when {

    "the http response status is 200 OK" should {

      val httpResponse = HttpResponse(Status.OK, responseJson = Some(
        Json.obj(
          "financialTransactions" -> Json.arr(
            Json.obj(
              "taxPeriodFrom" -> "2016-12-01",
              "taxPeriodTo" -> "2017-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2017-10-25")
              ),
              "outstandingAmount" -> 1000,
              "periodKey" -> "#003"
            )
          )
        )
      ))

      val expected = Right(Payments(Seq(
        Payment(
          start = LocalDate.parse("2016-12-01"),
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
          "reason" -> "Fail!"
        ))
      )

      val expected = Left(BadRequestError(
        code = "VRN_INVALID",
        errorResponse = "Fail!"
      ))

      val result = PaymentsReads.read("", "", httpResponse)

      "return a BadRequestError" in {
        result shouldEqual expected
      }
    }

    "the HTTP response status is BAD_REQUEST (400) (multiple errors)" should {

      val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_REQUEST, responseJson = Some(
        Json.obj(
          "code" -> "BAD_REQUEST",
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

      val expected = Left(MultipleErrors(Status.BAD_REQUEST.toString, Json.toJson(errors).toString()))

      val result = PaymentsReads.read("", "", httpResponse)

      "return a MultipleErrors" in {
        result shouldBe expected
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

      val result = PaymentsReads.read("", "", httpResponse)

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
      val expected = Left(ServerSideError(Status.GATEWAY_TIMEOUT, httpResponse.body))
      val result = PaymentsReads.read("", "", httpResponse)

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
      val expected = Left(UnexpectedStatusError(Status.CONFLICT, httpResponse.body))
      val result = PaymentsReads.read("", "", httpResponse)

      "return an UnexpectedStatusError" in {
        result shouldBe expected
      }
    }

  }
}
