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

import connectors.httpParsers.PenaltyDetailsHttpParser.PenaltyDetailsReads
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import common.TestModels._
import models.errors._
import models.penalties.PenaltyDetails
import uk.gov.hmrc.http.HttpResponse
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}

class PenaltyDetailsHttpParserSpec extends AnyWordSpecLike with Matchers{

  "PenaltyDetailsReads" when {

    "the http response status is 200 OK" should {

      val jsonResponse = penaltyDetailsJsonMax
      val httpResponse = HttpResponse(Status.OK, jsonResponse.toString())
      val expected = Right(penaltyDetailsModelMax)
      val result = PenaltyDetailsReads.read("","",httpResponse)

      "return a PenaltyDetails instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 200 OK, with no penalties" should {

      val jsonResponse = penaltyDetailsJsonMin
      val httpResponse = HttpResponse(Status.OK, jsonResponse.toString())
      val expected = Right(penaltyDetailsModelMin)
      val result = PenaltyDetailsReads.read("", "", httpResponse)

      "return a PenaltyDetails instance" in {
        result shouldEqual expected
      }
    }

    "the http response status is 404 NOT_FOUND" should {

      val httpResponse = HttpResponse(Status.NOT_FOUND, "")
      val expected = Right(PenaltyDetails(Seq(), breathingSpace = false))
      val result = PenaltyDetailsReads.read("", "", httpResponse)

      "return an empty Penalty Details object with an empty sequence and breathing space defaulted to false" in {
        result shouldEqual expected
      }
    }

    "the HTTP response status isn't handled" should {

      val body: JsObject = Json.obj(
        "code" -> "Conflict",
        "message" -> "CONFLICT"
      )

      val httpResponse = HttpResponse(Status.CONFLICT, body.toString())
      val expected = Left(UnexpectedStatusError("409", httpResponse.body))
      val result = PenaltyDetailsReads.read("", "", httpResponse)

      "return an UnexpectedStatusError" in {
        result shouldBe expected
      }
    }
  }
}
