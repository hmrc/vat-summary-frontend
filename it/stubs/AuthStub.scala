/*
 * Copyright 2021 HM Revenue & Customs
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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import play.api.http.Status._
import play.api.libs.json.{JsObject, Json}

object AuthStub extends WireMockMethods {

  private val authoriseUri: String = "/auth/authorise"
  private val AGENT_ENROLMENT_KEY = "HMRC-AS-AGENT"

  val mtdVatEnrolment: JsObject = Json.obj(
    "key" -> "HMRC-MTD-VAT",
    "identifiers" -> Json.arr(
      Json.obj(
        "key" -> "VRN",
        "value" -> "555555555"
      )
    )
  )

  val vatDecEnrolment: JsObject = Json.obj(
    "key" -> "HMCE-VATDEC-ORG",
    "identifiers" -> Json.arr(
      Json.obj(
        "key" -> "VATRegNo",
        "value" -> "555555555"
      )
    )
  )

  private val agentEnrolment = Json.obj(
    "key" -> AGENT_ENROLMENT_KEY,
    "identifiers" -> Json.arr(
      Json.obj(
        "key" -> "AgentReferenceNumber",
        "value" -> "1234567890"
      )
    )
  )

  val otherEnrolment: JsObject = Json.obj(
    "key" -> "HMRC-XXX-XXX",
    "identifiers" -> Json.arr(
      Json.obj(
        "key" -> "XXX",
        "value" -> "XXX"
      )
    )
  )

  def authorised(response: JsObject = successfulAuthResponse("Individual", mtdVatEnrolment)): StubMapping = {
    when(method = POST, uri = authoriseUri)
      .thenReturn(status = OK, body = response)
  }

  def agentAuthorised(): StubMapping = {
    when(method = POST, uri = authoriseUri)
      .thenReturn(status = OK, body = successfulAuthResponse("Agent", agentEnrolment))
  }
  def unauthorisedOtherEnrolment(): StubMapping = {
    when(method = POST, uri = authoriseUri)
      .thenReturn(status = OK, body = successfulAuthResponse("Individual", otherEnrolment))
  }

  def insufficientEnrolments(): StubMapping = {
    when(method = POST, uri = authoriseUri)
      .thenReturn(status = UNAUTHORIZED, headers = Map("WWW-Authenticate" -> """MDTP detail="InsufficientEnrolments""""))
  }

  def unauthorisedNotLoggedIn(): StubMapping = {
    when(method = POST, uri = authoriseUri)
      .thenReturn(status = UNAUTHORIZED, headers = Map("WWW-Authenticate" -> """MDTP detail="MissingBearerToken""""))
  }

  private def successfulAuthResponse(affinityGroup: String, enrolments: JsObject*): JsObject = {
    Json.obj("allEnrolments" -> enrolments, "affinityGroup" -> affinityGroup)
  }
}
