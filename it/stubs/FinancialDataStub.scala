
package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import models.errors.ApiSingleError
import play.api.http.Status._
import play.api.libs.json.Json

object FinancialDataStub extends WireMockMethods {

  private val financialDataUri = "/financial-transactions/vat/([0-9]+)"
  private val dateRegex = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))"

  def stubAllOutstandingPayments: StubMapping = {
    when(method = GET, uri = financialDataUri, queryParams = Map(
      "from" -> dateRegex, "to" -> dateRegex, "status" -> "O"
    ))
      .thenReturn(status = OK, body = allOutStandingPayments)
  }

  def stubNoPayments: StubMapping = {
    when(method = GET, uri = financialDataUri, queryParams = Map(
      "from" -> dateRegex, "to" -> dateRegex, "status" -> "O"
    ))
      .thenReturn(status = OK, body = Json.toJson(noPayments))
  }

  def stubInvalidVrn: StubMapping = {
    when(method = GET, uri = financialDataUri, queryParams = Map(
      "from" -> dateRegex, "to" -> dateRegex, "status" -> "O"
    ))
      .thenReturn(BAD_REQUEST, body = Json.toJson(invalidVrn))
  }

  def stubInvalidFromDate: StubMapping = {
    when(method = GET, uri = financialDataUri, queryParams = Map(
      "from" -> dateRegex, "to" -> dateRegex, "status" -> "O"
    ))
      .thenReturn(BAD_REQUEST, body = Json.toJson(invalidFromDate))
  }

  def stubInvalidToDate: StubMapping = {
    when(method = GET, uri = financialDataUri, queryParams = Map(
      "from" -> dateRegex, "to" -> dateRegex, "status" -> "O"
    ))
      .thenReturn(BAD_REQUEST, body = Json.toJson(invalidToDate))
  }

  private val allOutStandingPayments = Json.parse(
    """{
      |    "idType" : "VRN",
      |    "idNumber" : 555555555,
      |    "regimeType" : "VATC",
      |    "processingDate" : "2017-03-07T09:30:00.000Z",
      |    "financialTransactions" : [
      |      {
      |        "chargeType" : "VAT 0A Debit Charge",
      |        "mainType" : "VAT Officer's Assessment",
      |        "periodKey" : "15AC",
      |        "periodKeyDescription" : "March 2015",
      |        "taxPeriodFrom" : "2015-03-01",
      |        "taxPeriodTo" : "2015-03-31",
      |        "businessPartner" : "0",
      |        "contractAccountCategory" : "33",
      |        "contractAccount" : "X",
      |        "contractObjectType" : "ABCD",
      |        "contractObject" : "0",
      |        "sapDocumentNumber" : "0",
      |        "sapDocumentNumberItem" : "0",
      |        "chargeReference" : "XD002750002155",
      |        "mainTransaction" : "1234",
      |        "subTransaction" : "1174",
      |        "originalAmount" : 10000,
      |        "outstandingAmount" : 10000,
      |        "clearedAmount" : 10000,
      |        "items" : [
      |          {
      |            "subItem" : "000",
      |            "dueDate" : "2019-01-15",
      |            "amount" : 10000
      |          }
      |        ]
      |      },
      |      {
      |        "chargeType" : "VAT 0A Debit Charge",
      |        "mainType" : "VAT Officer's Assessment",
      |        "periodKey" : "15AC",
      |        "periodKeyDescription" : "March 2015",
      |        "taxPeriodFrom" : "2015-03-01",
      |        "taxPeriodTo" : "2015-03-31",
      |        "businessPartner" : "0",
      |        "contractAccountCategory" : "33",
      |        "contractAccount" : "X",
      |        "contractObjectType" : "ABCD",
      |        "contractObject" : "0",
      |        "sapDocumentNumber" : "0",
      |        "sapDocumentNumberItem" : "0",
      |        "chargeReference" : "XD002750002155",
      |        "mainTransaction" : "1234",
      |        "subTransaction" : "1174",
      |        "originalAmount" : 10000,
      |        "outstandingAmount" : 10000,
      |        "clearedAmount" : 10000,
      |        "items" : [
      |          {
      |            "subItem" : "000",
      |            "dueDate" : "2019-01-16",
      |            "amount" : 10000
      |          }
      |        ]
      |      }
      |    ]
      |  }""".stripMargin
  )
  private val noPayments = Json.parse(
    """{
      |    "idType" : "VRN",
      |    "idNumber" : 111111111,
      |    "regimeType" : "VATC",
      |    "processingDate" : "2017-03-07T09:30:00.000Z",
      |    "financialTransactions" : []
      |  }""".stripMargin
  )

  private val invalidVrn = ApiSingleError("VRN_INVALID", "", None)
  private val invalidFromDate = ApiSingleError("INVALID_DATE_FROM", "", None)
  private val invalidToDate = ApiSingleError("INVALID_DATE_TO", "", None)

}
