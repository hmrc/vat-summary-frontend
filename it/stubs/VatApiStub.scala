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

package stubs

import java.time.LocalDate
import java.time.temporal.ChronoUnit

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import models.errors.{ApiMultiError, ApiSingleError}
import models.{Obligation, Obligations}
import play.api.http.Status._
import play.api.libs.json.Json

object VatApiStub extends WireMockMethods {

  private val obligationsUri = "/vat/([0-9]+)/obligations"
  private val dateRegex = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))"

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


  private val pastFulfilledObligation = Obligation(
    start = LocalDate.now().minus(80L, ChronoUnit.DAYS),
    end = LocalDate.now().minus(50L, ChronoUnit.DAYS),
    due = LocalDate.now().minus(40L, ChronoUnit.DAYS),
    status = "F",
    received = Some(LocalDate.now().minus(45L, ChronoUnit.DAYS)),
    periodKey = "#001"
  )

  private val pastOutstandingObligation = Obligation(
    start = LocalDate.now().minus(70L, ChronoUnit.DAYS),
    end = LocalDate.now().minus(40L, ChronoUnit.DAYS),
    due = LocalDate.now().minus(30L, ChronoUnit.DAYS),
    status = "O",
    received = None,
    periodKey = "#004"
  )

  private val allObligations = Obligations(
    Seq(
      pastFulfilledObligation,
      pastOutstandingObligation
    )
  )

  private val outstandingObligations = Obligations(
    allObligations.obligations.filter(_.status == "O")
  )

  private val fulfilledObligations = Obligations(
    allObligations.obligations.filter(_.status == "F")
  )

  private val noObligations = Obligations(Seq.empty)

  private val invalidVrn = ApiSingleError("VRN_INVALID", "", None)
  private val invalidFromDate = ApiSingleError("INVALID_DATE_FROM", "", None)
  private val invalidToDate = ApiSingleError("INVALID_DATE_TO", "", None)
  private val invalidDateRange = ApiSingleError("INVALID_DATE_RANGE", "", None)
  private val invalidStatus = ApiSingleError("INVALID_STATUS", "", None)

  private val multipleErrors = ApiMultiError("BAD_REQUEST", "", Seq(
    ApiSingleError("ERROR_1", "", None),
    ApiSingleError("ERROR_2", "", None)
  ))

}
