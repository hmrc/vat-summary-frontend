/*
 * Copyright 2022 HM Revenue & Customs
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
import models.payments.{ReturnCreditCharge, ReturnDebitCharge, VatReturn1stLPP}
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}

object FinancialDataStub extends WireMockMethods {

  private val financialDataUri = "/financial-transactions/vat/([0-9]+)"
  private val financialDataDirectDebitUri = "/financial-transactions/has-direct-debit/([0-9]+)"

  def stubOutstandingTransactions: StubMapping = {
    when(method = GET, uri = financialDataUri, queryParams = Map("onlyOpenItems" -> "true"))
      .thenReturn(status = OK, body = outstandingTransactions)
  }

  def stubPaidTransactions: StubMapping = {
    when(method = GET, uri = financialDataUri, queryParams = Map(
      "dateFrom" -> "2018-01-01",
      "dateTo" -> "2018-12-31"))
      .thenReturn(status = OK, body = paidTransactions)
  }

  def stubNoPayments: StubMapping = {
    when(method = GET, uri = financialDataUri, queryParams = Map("onlyOpenItems" -> "true"))
      .thenReturn(status = OK, body = Json.toJson(noPayments))
  }

  def stubInvalidVrn: StubMapping = {
    when(method = GET, uri = financialDataUri, queryParams = Map("onlyOpenItems" -> "true"))
      .thenReturn(BAD_REQUEST, body = invalidVrn)
  }

  def stubApiError: StubMapping = {
    when(method = GET, uri = financialDataUri, queryParams = Map("onlyOpenItems" -> "true"))
      .thenReturn(INTERNAL_SERVER_ERROR, body = Json.toJson(""))
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
      |        "accruedInterestAmount" : 2,
      |        "mainTransaction" : "1234",
      |        "subTransaction" : "1174",
      |        "originalAmount" : 10000,
      |        "outstandingAmount" : 10000,
      |        "items" : [
      |          {
      |            "clearingSAPDocument" : "002828853334",
      |            "subItem" : "000",
      |            "dueDate" : "2019-01-15",
      |            "amount" : 10000
      |          }
      |        ]
      |      },
      |      {
      |        "chargeType" : "$ReturnDebitCharge",
      |        "mainType" : "VAT Return Charge",
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
      |        "chargeReference" : "XD002750002156",
      |        "accruedPenaltyAmount" : 3,
      |        "penaltyType" : "LPP1",
      |        "mainTransaction" : "1234",
      |        "subTransaction" : "1174",
      |        "originalAmount" : 10000,
      |        "outstandingAmount" : 10000,
      |        "items" : [
      |          {
      |            "clearingSAPDocument" : "002828853335",
      |            "subItem" : "000",
      |            "dueDate" : "2019-01-16",
      |            "amount" : 10000
      |          }
      |        ]
      |      },
      |            {
      |        "chargeType" : "$VatReturn1stLPP",
      |        "taxPeriodFrom" : "2019-01-01",
      |        "taxPeriodTo" : "2019-02-02",
      |        "chargeReference" : "XD002750002157",
      |        "penaltyType" : "LPP1",
      |        "originalAmount" : 55.55,
      |        "outstandingAmount" : 55.55,
      |        "items" : [
      |          {
      |            "dueDate" : "2019-03-03",
      |            "amount" : 55.55
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
