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

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import play.api.http.Status._
import play.api.libs.json.Json

object FinancialDataStub extends WireMockMethods {

  private val financialDataUri = "/financial-transactions/vat/([0-9]+)"
  private val financialDataDirectDebitUri = "/financial-transactions/has-direct-debit/([0-9]+)"

  def stubAllOutstandingPayments: StubMapping = {
    when(method = GET, uri = financialDataUri, queryParams = Map("onlyOpenItems" -> "true"))
      .thenReturn(status = OK, body = allOutStandingPayments)
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
      .thenReturn(status = OK, body = true)
  }

  def stubInvalidVrnDirectDebit: StubMapping = {
    when(method = GET, uri = financialDataDirectDebitUri)
      .thenReturn(BAD_REQUEST, body = invalidVrn)
  }

  private val allOutStandingPayments = Json.parse(
    """{
      |    "idType" : "VRN",
      |    "idNumber" : 555555555,
      |    "regimeType" : "VATC",
      |    "processingDate" : "2017-03-07T09:30:00.000Z",
      |    "financialTransactions" : [
      |      {
      |        "chargeType" : "VAT Return Debit Charge",
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
      |        "chargeType" : "VAT Return Debit Charge",
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

  private val invalidVrn = Json.obj(
    "code" -> "INVALID_VRN",
    "reason" -> "VRN was invalid!"
  )
}
