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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import models.payments.{ReturnCreditCharge, ReturnDebitCharge, VatProtectiveAssessmentCharge, VatReturn1stLPP, VatPA2ndLPP}
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}

object FinancialDataStub extends WireMockMethods {

  private val financialDataUri = "/financial-transactions/vat/([0-9]+)"
  private val financialDataDirectDebitUri = "/financial-transactions/has-direct-debit/([0-9]+)"

  private def stubOutstandingApiCall(status: Int, body: JsValue) =
    when(method = GET, uri = financialDataUri, queryParams = Map("onlyOpenItems" -> "true"))
      .thenReturn(status, body)

  def stubOutstandingTransactions: StubMapping = stubOutstandingApiCall(OK, outstandingTransactions)

  def stubSingleCharge: StubMapping = stubOutstandingApiCall(OK, oneCharge)

  def stubNoPayments: StubMapping = stubOutstandingApiCall(OK, noPayments)

  def stubInvalidVrn: StubMapping = stubOutstandingApiCall(BAD_REQUEST, invalidVrn)

  def stubApiError: StubMapping = stubOutstandingApiCall(INTERNAL_SERVER_ERROR, Json.obj())

  def stubPaidTransactions(dateFrom: String, dateTo: String): StubMapping = {
    when(method = GET, uri = financialDataUri, queryParams = Map("dateFrom" -> dateFrom, "dateTo" -> dateTo))
      .thenReturn(status = OK, body = paidTransactions)
  }

  def stubSuccessfulDirectDebit: StubMapping = {
    when(method = GET, uri = financialDataDirectDebitUri)
      .thenReturn(status = OK, body = DDStatusJson)
  }

  def stubInvalidVrnDirectDebit: StubMapping = {
    when(method = GET, uri = financialDataDirectDebitUri)
      .thenReturn(BAD_REQUEST, body = invalidVrn)
  }

  private val DDStatusJson: JsValue = Json.obj(
    "directDebitMandateFound" -> true,
    "directDebitDetails" -> Json.arr(
      Json.obj("dateCreated" -> "2018-01-01")
    )
  )

  private val paidTransactions: JsValue = Json.parse(
    s"""{
       |    "idType" : "VRN",
       |    "idNumber" : "555555555",
       |    "regimeType" : "VATC",
       |    "processingDate" : "2018-03-07T09:30:00.000Z",
       |    "financialTransactions" : [
       |      {
       |        "chargeType" : "$ReturnDebitCharge",
       |        "mainType" : "VAT Return Charge",
       |        "periodKey" : "17AA",
       |        "periodKeyDescription" : "ABCD",
       |        "taxPeriodFrom" : "2018-08-01",
       |        "taxPeriodTo" : "2018-10-31",
       |        "businessPartner" : "0",
       |        "contractAccountCategory" : "99",
       |        "contractAccount" : "X",
       |        "contractObjectType" : "ABCD",
       |        "contractObject" : "0",
       |        "sapDocumentNumber" : "0",
       |        "sapDocumentNumberItem" : "0",
       |        "chargeReference" : "XD002750002155",
       |        "mainTransaction" : "1234",
       |        "subTransaction" : "5678",
       |        "originalAmount" : 150,
       |        "items" : [
       |          {
       |            "clearingSAPDocument" : "002828853334",
       |            "subItem" : "000",
       |            "clearingDate" : "2018-01-10",
       |            "dueDate" : "2018-12-07",
       |            "amount" : 150
       |          }
       |        ]
       |      },
       |      {
       |        "chargeType" : "$ReturnCreditCharge",
       |        "mainType" : "VAT Return Charge",
       |        "periodKey" : "17BB",
       |        "periodKeyDescription" : "ABCD",
       |        "taxPeriodFrom" : "2018-05-01",
       |        "taxPeriodTo" : "2018-07-31",
       |        "businessPartner" : "0",
       |        "contractAccountCategory" : "99",
       |        "contractAccount" : "X",
       |        "contractObjectType" : "ABCD",
       |        "contractObject" : "0",
       |        "sapDocumentNumber" : "0",
       |        "sapDocumentNumberItem" : "0",
       |        "chargeReference" : "XD002750002155",
       |        "mainTransaction" : "1234",
       |        "subTransaction" : "5678",
       |        "originalAmount" : -600,
       |        "items" : [
       |          {
       |            "clearingSAPDocument" : "002828853335",
       |            "subItem" : "000",
       |            "clearingDate" : "2018-03-10",
       |            "dueDate" : "2018-09-07",
       |            "amount" : -600
       |          }
       |        ]
       |      }
       |    ]
       |  }""".stripMargin
  )

  private val outstandingTransactions = Json.parse(
    s"""{
      |    "idType" : "VRN",
      |    "idNumber" : 555555555,
      |    "regimeType" : "VATC",
      |    "processingDate" : "2017-03-07T09:30:00.000Z",
      |    "financialTransactions" : [
      |      {
      |        "chargeType" : "$ReturnDebitCharge",
      |        "mainType" : "VAT Return Charge",
      |        "periodKey" : "15AC",
      |        "taxPeriodFrom" : "2015-03-01",
      |        "taxPeriodTo" : "2015-03-31",
      |        "chargeReference" : "XD002750002155",
      |        "accruingInterestAmount" : 2,
      |        "originalAmount" : 10000,
      |        "outstandingAmount" : 10000,
      |        "items" : [
      |          {
      |            "dueDate" : "2019-01-15",
      |            "amount" : 10000
      |          }
      |        ]
      |      },
      |      {
      |        "chargeType" : "$ReturnDebitCharge",
      |        "mainType" : "VAT Return Charge",
      |        "periodKey" : "16AC",
      |        "taxPeriodFrom" : "2016-03-01",
      |        "taxPeriodTo" : "2016-03-31",
      |        "chargeReference" : "XD002750002156",
      |        "accruingPenaltyAmount" : 3,
      |        "penaltyType" : "LPP1",
      |        "originalAmount" : 10000,
      |        "outstandingAmount" : 10000,
      |        "items" : [
      |          {
      |            "dueDate" : "2019-01-16",
      |            "amount" : 10000
      |          }
      |        ]
      |      },
      |      {
      |        "chargeType" : "$VatProtectiveAssessmentCharge",
      |        "periodKey" : "17AC",
      |        "taxPeriodFrom" : "2017-03-01",
      |        "taxPeriodTo" : "2017-03-31",
      |        "chargeReference" : "XD002750002157",
      |        "accruingPenaltyAmount" : 5,
      |        "penaltyType" : "LPP2",
      |        "originalAmount" : 10000,
      |        "outstandingAmount" : 10000,
      |        "items" : [
      |          {
      |            "dueDate" : "2019-01-17",
      |            "amount" : 10000
      |          }
      |        ]
      |      },
      |      {
      |        "chargeType" : "$VatReturn1stLPP",
      |        "taxPeriodFrom" : "2019-01-01",
      |        "taxPeriodTo" : "2019-02-02",
      |        "chargeReference" : "XD002750002158",
      |        "originalAmount" : 55.55,
      |        "outstandingAmount" : 55.55,
      |        "items" : [
      |          {
      |            "dueDate" : "2019-03-03",
      |            "amount" : 55.55
      |          }
      |        ]
      |      },
      |      {
      |        "chargeType" : "$VatReturn1stLPP",
      |        "taxPeriodFrom" : "2019-01-01",
      |        "taxPeriodTo" : "2019-02-02",
      |        "chargeReference" : "X-PART-1-ONLY-X",
      |        "originalAmount" : 555.55,
      |        "outstandingAmount" : 555.55,
      |        "items" : [
      |          {
      |            "dueDate" : "2019-03-03",
      |            "amount" : 555.55
      |          }
      |        ]
      |      },
      |      {
      |        "chargeType" : "$VatPA2ndLPP",
      |        "taxPeriodFrom" : "2019-01-01",
      |        "taxPeriodTo" : "2019-02-02",
      |        "chargeReference" : "XD002750002159",
      |        "originalAmount" : 99.99,
      |        "outstandingAmount" : 99.99,
      |        "items" : [
      |          {
      |            "dueDate" : "2019-03-03",
      |            "amount" : 99.99
      |          }
      |        ]
      |      }
      |    ]
      |  }""".stripMargin
  )

  private val oneCharge = Json.parse(
    s"""{
      |    "idType" : "VRN",
      |    "idNumber" : 555555555,
      |    "regimeType" : "VATC",
      |    "processingDate" : "2017-03-07T09:30:00.000Z",
      |    "financialTransactions" : [
      |      {
      |        "chargeType" : "$ReturnDebitCharge",
      |        "mainType" : "VAT Return Charge",
      |        "periodKey" : "15AC",
      |        "taxPeriodFrom" : "2015-03-01",
      |        "taxPeriodTo" : "2015-03-31",
      |        "chargeReference" : "XD002750002155",
      |        "originalAmount" : 10000,
      |        "outstandingAmount" : 10000,
      |        "items" : [
      |          {
      |            "dueDate" : "2019-01-15",
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

  private val invalidVrn = Json.obj(
    "code" -> "INVALID_VRN",
    "reason" -> "VRN was invalid!"
  )
}
