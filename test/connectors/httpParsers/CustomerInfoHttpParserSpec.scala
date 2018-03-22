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

import connectors.httpParsers.CustomerInfoHttpParser.CustomerInfoReads
import models.{Address, CustomerInformation}
import models.errors._
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.test.UnitSpec

class CustomerInfoHttpParserSpec extends UnitSpec {

  "CustomerInfoReads" when {

    "the HTTP response status is OK (200)" should {

      val httpResponse = HttpResponse(Status.OK, responseJson = Some(
        Json.obj(
          "approvedInformation" -> Json.obj(
            "customerDetails" -> Json.obj(
              "organisationName" -> "Cheapo Clothing Ltd",
              "individual" -> Json.obj(
                "firstName" -> "Betty",
                "lastName" -> "Jones"
              ),
              "tradingName" -> "Cheapo Clothing"
            ),
            "PPOB" -> Json.obj(
              "address" -> Json.obj(
                "line1" -> "Bedrock Quarry",
                "line2" -> "Bedrock",
                "line3" -> "Graveldon",
                "line4" -> "Graveldon",
                "postCode" -> "GV2 4BB"
              ),
              "contactDetails" -> Json.obj(
                "primaryPhoneNumber" -> "01632 982028",
                "mobileNumber" -> "07700 900018",
                "emailAddress" -> "bettylucknexttime@gmail.com"
              )
            ),
            "correspondenceContactDetails" -> Json.obj(
              "address" -> Json.obj(
                "line1" -> "13 Pebble Lane",
                "line2" -> "Bedrock",
                "line3" -> "Graveldon",
                "line4" -> "Graveldon",
                "postCode" -> "GV13 4BJ"
              ),
              "contactDetails" -> Json.obj(
                "primaryPhoneNumber" -> "01632 960026",
                "mobileNumber" -> "07700 900018",
                "emailAddress" -> "bettylucknexttime@gmail.com"
              )
            )
          )
        )
      ))

      val expected = Right(CustomerInformation(
        Some("Cheapo Clothing Ltd"),
        Some("Betty"),
        Some("Jones"),
        Some("Cheapo Clothing"),
        Address("Bedrock Quarry",
          "Bedrock",
          Some("Graveldon"),
          Some("Graveldon"),
          Some("GV2 4BB")
        ),
        Some("01632 982028"),
        Some("07700 900018"),
        Some("bettylucknexttime@gmail.com"),
        Address("13 Pebble Lane",
          "Bedrock",
          Some("Graveldon"),
          Some("Graveldon"),
          Some("GV13 4BJ")
        ),
        Some("01632 960026"),
        Some("07700 900018"),
        Some("bettylucknexttime@gmail.com")
      ))

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
      val expected = Left(ServerSideError(Status.GATEWAY_TIMEOUT, httpResponse.body))
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
      val expected = Left(UnexpectedStatusError(Status.CONFLICT, httpResponse.body))
      val result = CustomerInfoReads.read("", "", httpResponse)

      "return an UnexpectedStatusError" in {
        result shouldBe expected
      }
    }
  }
}
