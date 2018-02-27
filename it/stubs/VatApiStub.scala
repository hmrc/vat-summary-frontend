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

package stubs

import java.time.LocalDate

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import models.errors.{ApiMultiError, ApiSingleError}
import models.obligations.{VatReturnObligation, VatReturnObligations}
import play.api.http.Status._
import play.api.libs.json.{Json, Writes}

object VatApiStub extends WireMockMethods {

  private val obligationsUri = "/([0-9]+)/obligations"
  private val dateRegex = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))"

  private implicit def obligationsWrites: Writes[VatReturnObligations] = Json.writes[VatReturnObligations]
  private implicit def obligationWrites: Writes[VatReturnObligation] = Json.writes[VatReturnObligation]

  def stubAllObligations: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> dateRegex, "to" -> dateRegex, "status" -> "A"
    ))
      .thenReturn(status = OK, body = Json.toJson(allObligations))
  }

  def stubOutstandingObligations: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> dateRegex, "to" -> dateRegex, "status" -> "O"
    ))
      .thenReturn(status = OK, body = Json.toJson(outstandingObligations))
  }

  def stubFulfilledObligations: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> dateRegex, "to" -> dateRegex, "status" -> "F"
    ))
      .thenReturn(status = OK, body = Json.toJson(fulfilledObligations))
  }

  def stubNoObligations: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> dateRegex, "to" -> dateRegex, "status" -> "O"
    ))
      .thenReturn(status = OK, body = Json.toJson(noObligations))
  }

  def stubInvalidVrn: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> dateRegex, "to" -> dateRegex, "status" -> "O"
    ))
      .thenReturn(BAD_REQUEST, body = Json.toJson(invalidVrn))
  }

  def stubInvalidFromDate: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> dateRegex, "to" -> dateRegex, "status" -> "F"
    ))
      .thenReturn(BAD_REQUEST, body = Json.toJson(invalidFromDate))
  }

  def stubInvalidToDate: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> dateRegex, "to" -> dateRegex, "status" -> "F"
    ))
      .thenReturn(BAD_REQUEST, body = Json.toJson(invalidToDate))
  }

  def stubInvalidDateRange: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> dateRegex, "to" -> dateRegex, "status" -> "F"
    ))
      .thenReturn(BAD_REQUEST, body = Json.toJson(invalidDateRange))
  }

  def stubInvalidStatus: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> dateRegex, "to" -> dateRegex, "status" -> "F"
    ))
      .thenReturn(BAD_REQUEST, body = Json.toJson(invalidStatus))
  }

  def stubMultipleErrors: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> dateRegex, "to" -> dateRegex, "status" -> "F"
    ))
      .thenReturn(BAD_REQUEST, body = Json.toJson(multipleErrors))
  }


  private val pastFulfilledObligation = VatReturnObligation(
    start = LocalDate.parse("2018-01-01"),
    end = LocalDate.parse("2018-03-31"),
    due = LocalDate.parse("2018-05-07"),
    status = "F",
    received = Some(LocalDate.parse("2018-04-15")),
    periodKey = "#001"
  )

  private val pastOutstandingObligation = VatReturnObligation(
    start = LocalDate.parse("2018-01-01"),
    end = LocalDate.parse("2018-03-31"),
    due = LocalDate.parse("2018-05-07"),
    status = "O",
    received = None,
    periodKey = "#004"
  )

  private val allObligations = VatReturnObligations(
    Seq(
      pastFulfilledObligation,
      pastOutstandingObligation
    )
  )

  private val outstandingObligations = VatReturnObligations(
    allObligations.obligations.filter(_.status == "O")
  )

  private val fulfilledObligations = VatReturnObligations(
    allObligations.obligations.filter(_.status == "F")
  )

  private val noObligations = VatReturnObligations(Seq.empty)

  private val invalidVrn = ApiSingleError("VRN_INVALID", "")
  private val invalidFromDate = ApiSingleError("INVALID_DATE_FROM", "")
  private val invalidToDate = ApiSingleError("INVALID_DATE_TO", "")
  private val invalidDateRange = ApiSingleError("INVALID_DATE_RANGE", "")
  private val invalidStatus = ApiSingleError("INVALID_STATUS", "")

  private val multipleErrors = ApiMultiError("BAD_REQUEST", "", Seq(
    ApiSingleError("ERROR_1", ""),
    ApiSingleError("ERROR_2", "")
  ))

}
