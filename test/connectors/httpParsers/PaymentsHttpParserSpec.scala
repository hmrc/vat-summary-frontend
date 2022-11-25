/*
 * Copyright 2022 HM Revenue & Customs
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
import models.payments._
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse
import org.scalatest.matchers.should.Matchers

class PaymentsHttpParserSpec extends AnyWordSpecLike with Matchers {

  "PaymentsReads" when {

    def json(charge: ChargeType): String = Json.obj(
      "financialTransactions" -> Json.arr(
        Json.obj(
          "mainType" -> charge.value,
          "chargeType" -> charge.value,
          "taxPeriodFrom" -> "2016-12-01",
          "taxPeriodTo" -> "2017-01-01",
          "items" -> Json.arr(
            Json.obj("dueDate" -> "2017-10-25")
          ),
          "outstandingAmount" -> 1000.50,
          "periodKey" -> "#001",
          "chargeReference" -> "XD002750002155",
          "accruingInterestAmount" -> 2,
          "accruingPenaltyAmount" -> 3,
          "penaltyType" -> "LPP1",
          "originalAmount" -> 10000
        )
      )
    ).toString

    def expectedModel(charge: ChargeType): Payment = PaymentWithPeriod(
      charge,
      periodFrom = LocalDate.parse("2016-12-01"),
      periodTo = LocalDate.parse("2017-01-01"),
      due = LocalDate.parse("2017-10-25"),
      outstandingAmount = BigDecimal(1000.50),
      periodKey = Some("#001"),
      chargeReference = Some("XD002750002155"),
      ddCollectionInProgress = false,
      accruingInterestAmount = Some(BigDecimal(2)),
      interestRate = None,
      accruingPenaltyAmount = Some(3),
      penaltyType = Some("LPP1"),
      originalAmount = BigDecimal(10000),
      clearedAmount = None
    )

    ChargeType.allChargeTypes.foreach { charge =>

      s"the http response status is 200 OK and the charge is $charge" should {

        val httpResponse = HttpResponse(Status.OK, json(charge))

        val expected = Right(Payments(Seq(expectedModel(charge))))

        val result = PaymentsReads.read("", "", httpResponse)

        "return a Payments instance" in {
          result shouldBe expected
        }
      }
    }

    "the http response status is 404 NOT_FOUND" should {

      val httpResponse = HttpResponse(Status.NOT_FOUND, "")

      val expected = Right(Payments(Seq.empty))

      val result = PaymentsReads.read("", "", httpResponse)

      "return an empty Payments object" in {
        result shouldBe expected
      }
    }

    "the http response status is 400 BAD_REQUEST (single error)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        Json.obj(
          "code" -> "VRN_INVALID",
          "reason" -> "Fail!"
        ).toString()
      )

      val expected = Left(BadRequestError(
        code = "VRN_INVALID",
        errorResponse = "Fail!"
      ))

      val result = PaymentsReads.read("", "", httpResponse)

      "return a BadRequestError" in {
        result shouldBe expected
      }
    }

    "a http response of 400 BAD_REQUEST (multiple errors)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        Json.obj(
          "failures" -> Json.arr(
            Json.obj(
              "code" -> "INVALID DATE FROM",
              "reason" -> "Bad date from"
            ),
            Json.obj(
              "code" -> "INVALID DATE TO",
              "reason" -> "Bad date to"
            )
          )
        ).toString()
      )

      val errors = Seq(ApiSingleError("INVALID DATE FROM", "Bad date from"), ApiSingleError("INVALID DATE TO", "Bad date to"))

      val expected = Left(MultipleErrors(Status.BAD_REQUEST.toString, Json.toJson(errors).toString()))

      val result = PaymentsReads.read("", "", httpResponse)

      "return a MultipleErrors" in {
        result shouldBe expected
      }
    }

    "the http response status is 400 BAD_REQUEST (unknown API error json)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        Json.obj(
          "foo" -> "INVALID",
          "bar" -> "Fail!"
        ).toString()
      )

      val expected = Left(UnknownError)

      val result = PaymentsReads.read("", "", httpResponse)

      "return a UnknownError" in {
        result shouldBe expected
      }
    }

    "the HTTP response status is 5xx" should {

      val body: JsObject = Json.obj(
        "code" -> "GATEWAY_TIMEOUT",
        "message" -> "GATEWAY_TIMEOUT"
      )

      val httpResponse = HttpResponse(Status.GATEWAY_TIMEOUT, body.toString())
      val expected = Left(ServerSideError(Status.GATEWAY_TIMEOUT.toString, httpResponse.body))
      val result = PaymentsReads.read("", "", httpResponse)

      "return a ServerSideError" in {
        result shouldBe expected
      }
    }

    "the HTTP response status isn't handled" should {

      val body: JsObject = Json.obj(
        "code" -> "Conflict",
        "message" -> "CONFLICT"
      )

      val httpResponse = HttpResponse(Status.CONFLICT, body.toString())
      val expected = Left(UnexpectedStatusError("409", httpResponse.body))
      val result = PaymentsReads.read("", "", httpResponse)

      "return an UnexpectedStatusError" in {
        result shouldBe expected
      }
    }
  }
}
