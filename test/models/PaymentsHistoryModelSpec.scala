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

package models

import models.payments._
import models.viewModels.PaymentsHistoryModel
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json._

import java.time.LocalDate

class PaymentsHistoryModelSpec extends AnyWordSpecLike with Matchers {

  val reads: Reads[Seq[PaymentsHistoryModel]] = PaymentsHistoryModel.reads

  "reads" when {

    "one item per period is returned" should {

      val testJson: JsValue = Json.parse(
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
           |            "amount" : 150,
           |            "dueDate" : "2018-12-07",
           |            "clearingDate" : "2018-01-10"
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
           |            "amount" : -600,
           |            "dueDate" : "2018-09-07",
           |            "clearingDate" : "2018-03-10"
           |          }
           |        ]
           |      }
           |    ]
           |  }""".stripMargin
      )

      val expectedSeq: Seq[PaymentsHistoryModel] = Seq(
        PaymentsHistoryModel(
          clearingSAPDocument = Some("002828853334"),
          chargeType = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
          amount = 150,
          clearedDate = Some(LocalDate.of(2018, 1, 10))
        ),
        PaymentsHistoryModel(
          clearingSAPDocument = Some("002828853335"),
          chargeType = ReturnCreditCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 5, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 7, 31)),
          amount = -600,
          clearedDate = Some(LocalDate.of(2018, 3, 10))
        )
      )

      "return one item per period" in {
        Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
      }
    }

    "multiple items per period is returned" should {

      val testJson: JsValue = Json.parse(
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
           |            "amount" : 150,
           |            "dueDate" : "2018-12-07",
           |            "clearingDate" : "2018-01-10"
           |          }
           |        ]
           |      },
           |      {
           |        "chargeType" : "$DebitDefaultSurcharge",
           |        "mainType" : "VAT Default Surcharge",
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
           |            "clearingSAPDocument" : "002828853335",
           |            "subItem" : "000",
           |            "amount" : 150,
           |            "dueDate" : "2018-12-07",
           |            "clearingDate" : "2018-01-10"
           |          }
           |        ]
           |      },
           |      {
           |        "chargeType" : "$CentralAssessmentCharge",
           |        "mainType" : "$CentralAssessmentCharge",
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
           |            "clearingSAPDocument" : "002828853336",
           |            "subItem" : "000",
           |            "amount" : 150,
           |            "dueDate" : "2018-12-07",
           |            "clearingDate" : "2018-01-10"
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
           |            "clearingSAPDocument" : "002828853337",
           |            "subItem" : "000",
           |            "amount" : -600,
           |            "dueDate" : "2018-09-07",
           |            "clearingDate" : "2018-03-10"
           |          }
           |        ]
           |      },
           |      {
           |        "chargeType" : "$ErrorCorrectionCreditCharge",
           |        "mainType" : "VAT Error Correction",
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
           |            "clearingSAPDocument" : "002828853338",
           |            "subItem" : "000",
           |            "amount" : -600,
           |            "dueDate" : "2018-09-07",
           |            "clearingDate" : "2018-03-10"
           |          }
           |        ]
           |      },
           |      {
           |        "chargeType" : "$ErrorCorrectionDebitCharge",
           |        "mainType" : "VAT Error Correction",
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
           |        "originalAmount" : 600,
           |        "items" : [
           |          {
           |            "clearingSAPDocument" : "002828853339",
           |            "subItem" : "000",
           |            "amount" : 600,
           |            "dueDate" : "2018-09-07",
           |            "clearingDate" : "2018-03-10"
           |          }
           |        ]
           |      }
           |    ]
           |  }""".stripMargin
      )

      val expectedSeq: Seq[PaymentsHistoryModel] = Seq(
        PaymentsHistoryModel(
          clearingSAPDocument = Some("002828853334"),
          chargeType = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
          amount = 150,
          clearedDate = Some(LocalDate.of(2018, 1, 10))
        ),
        PaymentsHistoryModel(
          clearingSAPDocument = Some("002828853335"),
          chargeType = DebitDefaultSurcharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
          amount = 150,
          clearedDate = Some(LocalDate.of(2018, 1, 10))
        ),
        PaymentsHistoryModel(
          clearingSAPDocument = Some("002828853336"),
          chargeType = CentralAssessmentCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
          amount = 150,
          clearedDate = Some(LocalDate.of(2018, 1, 10))
        ),
        PaymentsHistoryModel(
          clearingSAPDocument = Some("002828853337"),
          chargeType = ReturnCreditCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 5, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 7, 31)),
          amount = -600,
          clearedDate = Some(LocalDate.of(2018, 3, 10))
        ),
        PaymentsHistoryModel(
          clearingSAPDocument = Some("002828853338"),
          chargeType = ErrorCorrectionCreditCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 5, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 7, 31)),
          amount = -600,
          clearedDate = Some(LocalDate.of(2018, 3, 10))
        ),
        PaymentsHistoryModel(
          clearingSAPDocument = Some("002828853339"),
          chargeType = ErrorCorrectionDebitCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 5, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 7, 31)),
          amount = 600,
          clearedDate = Some(LocalDate.of(2018, 3, 10))
        )
      )
      "return multiple items per period" in {
        Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
      }
    }

    "one item per period with different charge types is returned" should {

      val testJson: JsValue = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : 555555555,
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
           |            "amount" : 150,
           |            "dueDate" : "2018-12-07",
           |            "clearingDate" : "2018-01-10"
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
           |            "amount" : -600,
           |            "dueDate" : "2018-09-07",
           |            "clearingDate" : "2018-04-10"
           |          }
           |        ]
           |      }
           |    ]
           |  }""".stripMargin
      )

      val expectedSeq = Seq(
        PaymentsHistoryModel(
          clearingSAPDocument = Some("002828853334"),
          chargeType = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
          amount = 150,
          clearedDate = Some(LocalDate.of(2018, 1, 10))
        ),
        PaymentsHistoryModel(
          clearingSAPDocument = Some("002828853335"),
          chargeType = ReturnCreditCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 5, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 7, 31)),
          amount = -600,
          clearedDate = Some(LocalDate.of(2018, 4, 10))
        )
      )

      "return the correct models" in {
        Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
      }
    }

    "there are two partial payments against one charge" should {

      val testJson: JsValue = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : 555555555,
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
           |        "originalAmount" : 250,
           |        "outstandingAmount": 30,
           |        "items" : [
           |          {
           |            "amount": 30,
           |            "dueDate": "2018-12-07"
           |          },
           |          {
           |            "clearingSAPDocument" : "002828853334",
           |            "subItem" : "001",
           |            "amount" : 100,
           |            "dueDate" : "2018-12-07",
           |            "clearingDate" : "2018-03-10"
           |          },
           |          {
           |            "clearingSAPDocument" : "002828853335",
           |            "subItem" : "000",
           |            "amount" : 120,
           |            "dueDate" : "2018-12-07",
           |            "clearingDate" : "2018-01-10"
           |          }
           |        ]
           |      }
           |    ]
           |  }""".stripMargin

      )

      val expectedSeq: Seq[PaymentsHistoryModel] = Seq(
        PaymentsHistoryModel(
          clearingSAPDocument = Some("002828853334"),
          chargeType = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
          amount = 100,
          clearedDate = Some(LocalDate.of(2018, 3, 10))
        ),
        PaymentsHistoryModel(
          clearingSAPDocument = Some("002828853335"),
          chargeType = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
          amount = 120,
          clearedDate = Some(LocalDate.of(2018, 1, 10))
        )
      )

      "return two payment models" in {
        Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
      }
    }

    "financial transactions is empty" should {

      val testJson: JsValue = Json.parse(
        """
          |{
          |   "financialTransactions" : [
          |
          |   ]
          |}
        """.stripMargin
      )

      val expectedSeq: Seq[PaymentsHistoryModel] = Seq.empty

      "return empty sequence" in {
        Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
      }
    }

    "there is no financialTransactions block" should {
      val testJson: JsValue = Json.parse("""{ "abc" : "xyz" }""")

      "throw a JsResultException" in {
        intercept[JsResultException](Json.fromJson(testJson)(reads))
      }
    }

    "there is no items block" should {

      val testJson: JsValue = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : 555555555,
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
           |        "outstandingAmount" : 150
           |      }
           |    ]
           |  }""".stripMargin
      )

      "return an empty sequence of sub items" in {
        Json.fromJson(testJson)(reads) shouldBe JsSuccess(Seq())
      }
    }

    "there is no amount" should {
      val testJson = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : 555555555,
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
           |            "dueDate" : "2018-12-07",
           |            "clearingDate" : "2018-01-10"
           |          }
           |        ]
           |      }
           |    ]
           |  }""".stripMargin
      )

      "return an empty sequence" in {
        Json.fromJson(testJson)(reads) shouldBe JsSuccess(Seq())
      }
    }

    "there is no clearingDate" should {
      val testJson = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : 555555555,
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
           |            "dueDate" : "2018-12-07",
           |            "amount" : 150
           |          }
           |        ]
           |      }
           |    ]
           |  }""".stripMargin
      )

      "return an empty sequence" in {
        Json.fromJson(testJson)(reads) shouldBe JsSuccess(Seq())
      }
    }

    "there is no dateFrom" should {

      val testJson: JsValue = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : 555555555,
           |    "regimeType" : "VATC",
           |    "processingDate" : "2018-03-07T09:30:00.000Z",
           |    "financialTransactions" : [
           |      {
           |        "chargeType" : "$ReturnDebitCharge",
           |        "mainType" : "VAT Return Charge",
           |        "periodKey" : "17AA",
           |        "periodKeyDescription" : "ABCD",
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
           |            "amount" : 150,
           |            "dueDate" : "2018-12-07",
           |            "clearingDate" : "2018-01-10"
           |          }
           |        ]
           |      }
           |    ]
           |  }""".stripMargin
      )

      val expectedSeq: Seq[PaymentsHistoryModel] = Seq(
        PaymentsHistoryModel(
          clearingSAPDocument = Some("002828853334"),
          chargeType = ReturnDebitCharge,
          taxPeriodFrom = None,
          taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
          amount = 150,
          clearedDate = Some(LocalDate.of(2018, 1, 10)))
      )

      "parse successfully" in {
        Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
      }
    }

    "there is no dateTo" should {

      val testJson: JsValue = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : 555555555,
           |    "regimeType" : "VATC",
           |    "processingDate" : "2018-03-07T09:30:00.000Z",
           |    "financialTransactions" : [
           |      {
           |        "chargeType" : "$ReturnDebitCharge",
           |       "mainType" : "VAT Return Charge",
           |        "periodKey" : "17AA",
           |        "periodKeyDescription" : "ABCD",
           |        "taxPeriodFrom" : "2018-08-01",
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
           |            "amount" : 150,
           |            "dueDate" : "2018-12-07",
           |            "clearingDate" : "2018-01-10"
           |          }
           |        ]
           |      }
           |    ]
           |  }""".stripMargin
      )

      val expectedSeq: Seq[PaymentsHistoryModel] = Seq(
        PaymentsHistoryModel(
          clearingSAPDocument = Some("002828853334"),
          chargeType = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
          taxPeriodTo = None,
          amount = 150,
          clearedDate = Some(LocalDate.of(2018, 1, 10)))
      )

      "parse successfully" in {
        Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
      }
    }

    s"charge type is $PaymentOnAccount" when {

      "clearing reason is filled" should {

        val testJson: JsValue = Json.parse(
          s"""{
             |    "idType" : "VRN",
             |    "idNumber" : "555555555",
             |    "regimeType" : "VATC",
             |    "processingDate" : "2018-03-07T09:30:00.000Z",
             |    "financialTransactions" : [
             |      {
             |        "chargeType" : "$PaymentOnAccount",
             |        "mainType" : "Payment on account",
             |        "businessPartner" : "0",
             |        "contractAccountCategory" : "99",
             |        "contractAccount" : "X",
             |        "contractObjectType" : "ABCD",
             |        "contractObject" : "0",
             |        "sapDocumentNumber" : "0",
             |        "sapDocumentNumberItem" : "0",
             |        "mainTransaction" : "1234",
             |        "subTransaction" : "5678",
             |        "originalAmount" : -5050,
             |        "items" : [
             |          {
             |            "clearingSAPDocument" : "002828853334",
             |            "subItem": "000",
             |            "paymentReference": "654378944",
             |            "amount": -5050,
             |            "paymentMethod": "BANK GIRO RECEIPTS",
             |            "paymentLot": "RP11",
             |            "paymentLotItem": "000001",
             |            "dueDate": "2018-12-04",
             |            "clearingDate": "2017-12-04",
             |            "clearingReason": "Some clearing reason"
             |          }
             |        ]
             |      }
             |    ]
             |  }""".stripMargin
        )

        val expectedSeq: Seq[PaymentsHistoryModel] = Seq(
          PaymentsHistoryModel(
            clearingSAPDocument = Some("002828853334"),
            Refund,
            None,
            None,
            -5050,
            Some(LocalDate.of(2017, 12, 4)))
        )

        s"return $Refund as the charge type " in {
          Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
        }
      }
    }
  }

  "generatePaymentModel" when {

    "Charge type is not Payment on account" should {

      val chargeType = ReturnDebitCharge
      val subItem = TransactionSubItem(clearingSAPDocument = Some("002828853334"), Some(9),
        Some(LocalDate.of(2018, 1, 1)), None)
      val transaction: JsValue = Json.parse(
        """ {
          |   "taxPeriodFrom" : "2018-08-01",
          |   "taxPeriodTo" : "2018-11-01"
          | }
        """.stripMargin
      )

      val result = PaymentsHistoryModel.generatePaymentModel(chargeType, subItem, transaction).get

      "return the same charge type" in {
        result.chargeType shouldBe chargeType
      }

      "return a from date" in {
        result.taxPeriodFrom shouldBe Some(LocalDate.of(2018, 8, 1))
      }

      "return a to date" in {
        result.taxPeriodTo shouldBe Some(LocalDate.of(2018, 11, 1))
      }

      "return clearingSAPDocument" in {
        result.clearingSAPDocument shouldBe subItem.clearingSAPDocument
      }

      "return the amount of the sub item" in {
        result.amount shouldBe subItem.amount.get
      }

      "return the cleared date of the sub item" in {
        result.clearedDate shouldBe subItem.clearingDate
      }
    }

    "Charge type is Payment on account" when {

      val chargeType = PaymentOnAccount

      "clearing reason is defined" should {

        val subItem = TransactionSubItem(clearingSAPDocument = Some("002828853334"), Some(9000),
          Some(LocalDate.of(2018, 1, 1)), Some("some clearing reason"))
        val transaction: JsValue = Json.parse(
          """ {
            |   "taxPeriodFrom" : "2018-08-01",
            |   "taxPeriodTo" : "2018-11-01"
            | }
          """.stripMargin
        )
        val result = PaymentsHistoryModel.generatePaymentModel(chargeType, subItem, transaction).get

        "return a Refund" in {
          result.chargeType shouldBe Refund
        }

        "not return a from date" in {
          result.taxPeriodFrom shouldBe None
        }

        "not return a to date" in {
          result.taxPeriodTo shouldBe None
        }

        "return clearingSAPDocument" in {
          result.clearingSAPDocument shouldBe subItem.clearingSAPDocument
        }

        "return the correct amount" in {
          result.amount shouldBe subItem.amount.get
        }

        "return a clearing date" in {
          result.clearedDate shouldBe subItem.clearingDate
        }
      }

      "clearing reason is not defined" should {

        val subItem = TransactionSubItem(clearingSAPDocument = Some("002828853334"), Some(9), None, None,
          Some(LocalDate.of(2018, 1, 1)))
        val transaction: JsValue = Json.parse(
          """ {
            |   "outstandingAmount" : "1000"
            | }
          """.stripMargin
        )

        val result = PaymentsHistoryModel.generatePaymentModel(chargeType, subItem, transaction).get

        "return an UnallocatedPayment" in {
          result.chargeType shouldBe UnallocatedPayment
        }

        "not return a from date" in {
          result.taxPeriodFrom shouldBe None
        }

        "not return a to date" in {
          result.taxPeriodTo shouldBe None
        }

        "return clearingSAPDocument" in {
          result.clearingSAPDocument shouldBe subItem.clearingSAPDocument
        }

        "return the correct amount" in {
          result.amount shouldBe subItem.amount.get
        }

        "use the due date of the sub item as cleared date" in {
          result.clearedDate shouldBe subItem.dueDate
        }
      }
    }
  }
}