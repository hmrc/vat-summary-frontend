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

import connectors.httpParsers.PaymentsRedirectUrlHttpParser.PaymentsRedirectUrlReads
import models.errors._
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.test.UnitSpec

class PaymentsRedirectUrlHttpParserSpec extends UnitSpec {

  "PaymentsRedirectUrlReads" when {

    "the HTTP response status is OK (200)" should {

      val redirectUrl = "https://www.google.com"

      val json =
        s"""
           |{
           |  "links": {
           |    "nextUrl": "$redirectUrl"
           |  }
           |}
        """.stripMargin

      val httpResponse = HttpResponse(Status.OK, responseJson = Some(Json.parse(json)))

      val expected = Right(redirectUrl)

      val result = PaymentsRedirectUrlReads.read("", "", httpResponse)

      "return a redirect URL" in {
        result shouldBe expected
      }
    }

    "the HTTP response status is not OK (!200)" should {

      val errorBody =
        """{
          |  "errors": [
          |    {
          |      "code": "INVALID_TAXTYPE",
          |      "message": "The tax type is invalid",
          |      "path": "/taxType"
          |    }
          |  ]
          |}""".stripMargin

      val httpResponse = HttpResponse(
        Status.BAD_REQUEST,
        responseJson = Some(Json.parse(errorBody)))

      val result = PaymentsRedirectUrlReads.read("", "", httpResponse)

      "return a UnexpectedStatusError" in {
        result match {
          case Left(UnexpectedStatusError(code, message)) =>
            code shouldBe "400"
            Json.parse(message) shouldBe Json.parse(errorBody)
          case _ => fail("Should have returned a Left with an error.")
        }
      }
    }
  }
}
