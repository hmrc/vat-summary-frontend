
package stubs

import java.time.LocalDate

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import models.{ApiMultiError, ApiSingleError, Obligation, Obligations}
import play.api.http.Status._
import play.api.libs.json.Json

object VatApiStub extends WireMockMethods {

  private val obligationsUri = "/vat/([0-9]+)/obligations"

  def stubAllObligations: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> "2017-01-01", "to" -> "2017-12-31", "status" -> "A"
    ))
      .thenReturn(status = OK, body = Json.toJson(allObligations))
  }

  def stubOutstandingObligations: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> "2017-01-01", "to" -> "2017-12-31", "status" -> "O"
    ))
      .thenReturn(status = OK, body = Json.toJson(outstandingObligations))
  }

  def stubFulfilledObligations: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> "2017-01-01", "to" -> "2017-12-31", "status" -> "F"
    ))
      .thenReturn(status = OK, body = Json.toJson(fulfilledObligations))
  }

  def stubInvalidVrn: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> "2017-01-01", "to" -> "2017-12-31", "status" -> "F"
    ))
      .thenReturn(BAD_REQUEST, body = Json.toJson(invalidVrn))
  }

  def stubInvalidFromDate: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> "2017-01-01", "to" -> "2017-12-31", "status" -> "F"
    ))
      .thenReturn(BAD_REQUEST, body = Json.toJson(invalidFromDate))
  }

  def stubInvalidToDate: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> "2017-01-01", "to" -> "2017-12-31", "status" -> "F"
    ))
      .thenReturn(BAD_REQUEST, body = Json.toJson(invalidToDate))
  }

  def stubInvalidDateRange: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> "2017-12-31", "to" -> "2017-01-01", "status" -> "F"
    ))
      .thenReturn(BAD_REQUEST, body = Json.toJson(invalidDateRange))
  }

  def stubInvalidStatus: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> "2017-01-01", "to" -> "2017-12-31", "status" -> "F"
    ))
      .thenReturn(BAD_REQUEST, body = Json.toJson(invalidStatus))
  }

  def stubMultipleErrors: StubMapping = {
    when(method = GET, uri = obligationsUri, queryParams = Map(
      "from" -> "2017-01-01", "to" -> "2017-12-31", "status" -> "F"
    ))
      .thenReturn(BAD_REQUEST, body = Json.toJson(multipleErrors))
  }

  private val allObligations = Obligations(
    Seq(
      Obligation(
        start = LocalDate.parse("2017-01-01"),
        end = LocalDate.parse("2017-03-30"),
        due = LocalDate.parse("2017-04-30"),
        status = "F",
        received = Some(LocalDate.parse("2017-04-15")),
        periodKey = "#001"
      ),
      Obligation(
        start = LocalDate.parse("2017-04-01"),
        end = LocalDate.parse("2017-07-30"),
        due = LocalDate.parse("2017-08-30"),
        status = "O",
        received = None,
        periodKey = "#004"
      )
    )
  )

  private val outstandingObligations = Obligations(
    allObligations.obligations.filter(_.status == "O")
  )

  private val fulfilledObligations = Obligations(
    allObligations.obligations.filter(_.status == "F")
  )

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
