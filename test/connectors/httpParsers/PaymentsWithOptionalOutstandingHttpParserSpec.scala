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

import connectors.httpParsers.PaymentsWithOptionalOutstandingHttpParser.PaymentsWithOptionalOutstandingReads
import models.errors._
import models.payments._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse

import java.time.LocalDate

class PaymentsWithOptionalOutstandingHttpParserSpec extends AnyWordSpecLike with Matchers {

  "PaymentsWithOptionalOutstandingReads" when {

    val baseJson = Json.obj(
      "financialTransactions" -> Json.arr(
        Json.obj(
          "chargeType" -> ReturnDebitCharge.value,
          "items" -> Json.arr(
            Json.obj("dueDate" -> "2025-10-25")
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
    )

    "the http response status is 200 OK" should {
      "return a PaymentsWithOptionalOutstanding instance" in {
        val httpResponse = HttpResponse(Status.OK, baseJson.toString())

        val expectedPayment = PaymentWithOptionalOutstanding(
          ReturnDebitCharge,
          due = LocalDate.parse("2025-10-25"),
          outstandingAmount = Some(BigDecimal(1000.50)),
          periodKey = Some("#001"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruingInterestAmount = Some(BigDecimal(2)),
          accruingPenaltyAmount = Some(BigDecimal(3)),
          penaltyType = Some("LPP1"),
          originalAmount = Some(BigDecimal(10000)),
          clearedAmount = None
        )

        val result = PaymentsWithOptionalOutstandingReads.read("", "", httpResponse)
        result shouldBe Right(PaymentsWithOptionalOutstanding(Seq(expectedPayment)))
      }

      "allow missing outstandingAmount and still parse" in {
        val jsonWithoutOutstanding = baseJson.deepMerge(
          Json.obj("financialTransactions" -> Json.arr(
            Json.obj(
              "chargeType" -> ReturnDebitCharge.value,
              "items" -> Json.arr(Json.obj("dueDate" -> "2025-10-25"))
            )
          ))
        )

        val httpResponse = HttpResponse(Status.OK, jsonWithoutOutstanding.toString())
        val result = PaymentsWithOptionalOutstandingReads.read("", "", httpResponse)
        result shouldBe Right(
          PaymentsWithOptionalOutstanding(Seq(
            PaymentWithOptionalOutstanding(
              ReturnDebitCharge,
              due = LocalDate.parse("2025-10-25"),
              outstandingAmount = None,
              periodKey = None,
              chargeReference = None,
              ddCollectionInProgress = false,
              accruingInterestAmount = None,
              accruingPenaltyAmount = None,
              penaltyType = None,
              originalAmount = None,
              clearedAmount = None
            )
          ))
        )
      }
    }

    "the http response status is 404 NOT_FOUND" should {
      "return an empty PaymentsWithOptionalOutstanding object" in {
        val httpResponse = HttpResponse(Status.NOT_FOUND, "")
        val result = PaymentsWithOptionalOutstandingReads.read("", "", httpResponse)
        result shouldBe Right(PaymentsWithOptionalOutstanding(Seq.empty))
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

      val result = PaymentsWithOptionalOutstandingReads.read("", "", httpResponse)

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

      val result = PaymentsWithOptionalOutstandingReads.read("", "", httpResponse)

      "return a MultipleErrors" in {
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
      val result = PaymentsWithOptionalOutstandingReads.read("", "", httpResponse)

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
      val result = PaymentsWithOptionalOutstandingReads.read("", "", httpResponse)

      "return an UnexpectedStatusError" in {
        result shouldBe expected
      }
    }
  }
}
