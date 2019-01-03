/*
 * Copyright 2019 HM Revenue & Customs
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

import java.time.LocalDate

import common.FinancialTransactionsConstants
import models.payments._
import models.viewModels.PaymentsHistoryModel
import play.api.libs.json._
import uk.gov.hmrc.play.test.UnitSpec

class PaymentsHistoryModelSpec extends UnitSpec {

  val reads: Reads[Seq[PaymentsHistoryModel]] = PaymentsHistoryModel.reads

  implicit val writes: Writes[PaymentsHistoryModel] = Json.writes[PaymentsHistoryModel]

  "reads" should {
    "read json containing one item per period into a sequence" in {
      val testJson: JsValue = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : "555555555",
           |    "regimeType" : "VATC",
           |    "processingDate" : "2018-03-07T09:30:00.000Z",
           |    "financialTransactions" : [
           |      {
           |        "chargeType" : "$ReturnDebitCharge",
           |       "mainType" : "$ReturnCharge",
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
           |        "outstandingAmount" : 150,
           |        "items" : [
           |          {
           |            "subItem" : "000",
           |            "clearingDate" : "2018-01-10",
           |            "dueDate" : "2018-12-07",
           |            "amount" : 150
           |          }
           |        ]
           |      },
           |      {
           |        "chargeType" : "$ReturnCreditCharge",
           |        "mainType" : "$ReturnCharge",
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
           |        "outstandingAmount" : 600,
           |        "items" : [
           |          {
           |            "subItem" : "000",
           |            "clearingDate" : "2018-03-10",
           |            "dueDate" : "2018-09-07",
           |            "amount" : 600
           |          }
           |        ]
           |      }
           |    ]
           |  }""".stripMargin
      )

      val expectedSeq: Seq[PaymentsHistoryModel] = Seq(
        PaymentsHistoryModel(
          chargeType = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
          amount = 150,
          clearedDate = Some(LocalDate.of(2018, 1, 10))
        ),
        PaymentsHistoryModel(
          chargeType = ReturnCreditCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 5, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 7, 31)),
          amount = 600,
          clearedDate = Some(LocalDate.of(2018, 3, 10))
        )
      )

      Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
    }

    "read json containing multiple items per period into a sequence" in {
      val testJson: JsValue = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : "555555555",
           |    "regimeType" : "VATC",
           |    "processingDate" : "2018-03-07T09:30:00.000Z",
           |    "financialTransactions" : [
           |      {
           |        "chargeType" : "$ReturnDebitCharge",
           |       "mainType" : "$ReturnCharge",
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
           |        "outstandingAmount" : 150,
           |        "items" : [
           |          {
           |            "subItem" : "000",
           |            "clearingDate" : "2018-01-10",
           |            "dueDate" : "2018-12-07",
           |            "amount" : 150
           |          }
           |        ]
           |      },
           |      {
           |        "chargeType" : "$DefaultSurcharge",
           |       "mainType" : "$DefaultSurcharge",
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
           |        "outstandingAmount" : 150,
           |        "items" : [
           |          {
           |            "subItem" : "000",
           |            "clearingDate" : "2018-01-10",
           |            "dueDate" : "2018-12-07",
           |            "amount" : 150
           |          }
           |        ]
           |      },
           |      {
           |        "chargeType" : "$CentralAssessmentCharge",
           |       "mainType" : "$CentralAssessmentCharge",
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
           |        "outstandingAmount" : 150,
           |        "items" : [
           |          {
           |            "subItem" : "000",
           |            "clearingDate" : "2018-01-10",
           |            "dueDate" : "2018-12-07",
           |            "amount" : 150
           |          }
           |        ]
           |      },
           |      {
           |        "chargeType" : "${ReturnCreditCharge}",
           |        "mainType" : "$ReturnCharge",
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
           |        "outstandingAmount" : 600,
           |        "items" : [
           |          {
           |            "subItem" : "000",
           |            "clearingDate" : "2018-03-10",
           |            "dueDate" : "2018-09-07",
           |            "amount" : 600
           |          }
           |        ]
           |      },
           |      {
           |        "chargeType" : "$ErrorCorrectionCreditCharge",
           |        "mainType" : "$ErrorCorrectionCharge",
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
           |        "outstandingAmount" : 600,
           |        "items" : [
           |          {
           |            "subItem" : "000",
           |            "clearingDate" : "2018-03-10",
           |            "dueDate" : "2018-09-07",
           |            "amount" : 600
           |          }
           |        ]
           |      },
           |      {
           |        "chargeType" : "$ErrorCorrectionDebitCharge",
           |        "mainType" : "$ErrorCorrectionCharge",
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
           |        "outstandingAmount" : 600,
           |        "items" : [
           |          {
           |            "subItem" : "000",
           |            "clearingDate" : "2018-03-10",
           |            "dueDate" : "2018-09-07",
           |            "amount" : 600
           |          }
           |        ]
           |      }
           |    ]
           |  }""".stripMargin
      )

      val expectedSeq: Seq[PaymentsHistoryModel] = Seq(
        PaymentsHistoryModel(
          chargeType = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
          amount = 150,
          clearedDate = Some(LocalDate.of(2018, 1, 10))
        ),
        PaymentsHistoryModel(
          chargeType = DefaultSurcharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
          amount = 150,
          clearedDate = Some(LocalDate.of(2018, 1, 10))
        ),
        PaymentsHistoryModel(
          chargeType = CentralAssessmentCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
          amount = 150,
          clearedDate = Some(LocalDate.of(2018, 1, 10))
        ),
        PaymentsHistoryModel(
          chargeType = ReturnCreditCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 5, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 7, 31)),
          amount = 600,
          clearedDate = Some(LocalDate.of(2018, 3, 10))
        ),
        PaymentsHistoryModel(
          chargeType = ErrorCorrectionCreditCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 5, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 7, 31)),
          amount = 600,
          clearedDate = Some(LocalDate.of(2018, 3, 10))
        ),
        PaymentsHistoryModel(
          chargeType = ErrorCorrectionDebitCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 5, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 7, 31)),
          amount = 600,
          clearedDate = Some(LocalDate.of(2018, 3, 10))
        )
      )

      Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
    }

    "read json containing one item per period into a sequence with different charge types" in {
      val testJson: JsValue = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : 555555555,
           |    "regimeType" : "VATC",
           |    "processingDate" : "2018-03-07T09:30:00.000Z",
           |    "financialTransactions" : [
           |      {
           |        "chargeType" : "$ReturnDebitCharge",
           |        "mainType" : "$ReturnCharge",
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
           |        "outstandingAmount" : 150,
           |        "items" : [
           |          {
           |            "subItem" : "000",
           |            "clearingDate" : "2018-01-10",
           |            "dueDate" : "2018-12-07",
           |            "amount" : 150
           |          }
           |        ]
           |      },
           |      {
           |        "chargeType" : "$ReturnCreditCharge",
           |        "mainType" : "$ReturnCharge",
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
           |        "outstandingAmount" : 600,
           |        "items" : [
           |          {
           |            "subItem" : "000",
           |            "clearingDate" : "2018-04-10",
           |            "dueDate" : "2018-09-07",
           |            "amount" : 600
           |          }
           |        ]
           |      }
           |    ]
           |  }""".stripMargin

      )

      val expectedSeq = Seq(
        PaymentsHistoryModel(
          chargeType = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
          amount = 150,
          clearedDate = Some(LocalDate.of(2018, 1, 10))
        ),
        PaymentsHistoryModel(
          chargeType = ReturnCreditCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 5, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 7, 31)),
          amount = 600,
          clearedDate = Some(LocalDate.of(2018, 4, 10))
        )
      )

      Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
    }

    "read json containing one item per period into a sequence with different main types" in {
      val testJson: JsValue = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : 555555555,
           |    "regimeType" : "VATC",
           |    "processingDate" : "2018-03-07T09:30:00.000Z",
           |    "financialTransactions" : [
           |      {
           |        "chargeType" : "$ReturnDebitCharge",
           |        "mainType" : "$ReturnCharge",
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
           |        "outstandingAmount" : 150,
           |        "items" : [
           |          {
           |            "subItem" : "000",
           |            "clearingDate" : "2018-01-10",
           |            "dueDate" : "2018-12-07",
           |            "amount" : 150
           |          }
           |        ]
           |      },
           |      {
           |        "chargeType" : "$OACreditCharge",
           |        "mainType" : "$OACharge",
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
           |        "outstandingAmount" : 600,
           |        "items" : [
           |          {
           |            "subItem" : "000",
           |            "clearingDate" : "2018-04-10",
           |            "dueDate" : "2018-09-07",
           |            "amount" : 600
           |          }
           |        ]
           |      }
           |    ]
           |  }""".stripMargin

      )

      val expectedSeq = Seq(
        PaymentsHistoryModel(
          chargeType = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
          amount = 150,
          clearedDate = Some(LocalDate.of(2018, 1, 10))
        ),
        PaymentsHistoryModel(
          chargeType = OACreditCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 5, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 7, 31)),
          amount = 600,
          clearedDate = Some(LocalDate.of(2018, 4, 10))
        )
      )

      Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
    }

    "read json containing multiple items in only one period" in {
      val testJson: JsValue = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : 555555555,
           |    "regimeType" : "VATC",
           |    "processingDate" : "2018-03-07T09:30:00.000Z",
           |    "financialTransactions" : [
           |      {
           |        "chargeType" : "$ReturnDebitCharge",
           |        "mainType" : "$ReturnCharge",
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
           |        "outstandingAmount" : 150,
           |        "items" : [
           |          {
           |            "subItem" : "000",
           |            "clearingDate" : "2018-01-10",
           |            "dueDate" : "2018-12-07",
           |            "amount" : 150
           |          },
           |          {
           |            "subItem" : "000",
           |            "clearingDate" : "2018-03-10",
           |            "dueDate" : "2018-12-07",
           |            "amount" : 100
           |          }
           |        ]
           |      }
           |    ]
           |  }""".stripMargin

      )

      val expectedSeq: Seq[PaymentsHistoryModel] = Seq(
        PaymentsHistoryModel(
          chargeType = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
          amount = 150,
          clearedDate = Some(LocalDate.of(2018, 1, 10))
        ),
        PaymentsHistoryModel(
          chargeType = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
          amount = 100,
          clearedDate = Some(LocalDate.of(2018, 3, 10))
        )
      )

      Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
    }

    "read json containing one item in only one period" in {
      val testJson: JsValue = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : 555555555,
           |    "regimeType" : "VATC",
           |    "processingDate" : "2018-03-07T09:30:00.000Z",
           |    "financialTransactions" : [
           |      {
           |        "chargeType" : "$ReturnDebitCharge",
           |        "mainType" : "$ReturnCharge",
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
           |        "outstandingAmount" : 150,
           |        "items" : [
           |          {
           |            "subItem" : "000",
           |            "clearingDate" : "2018-01-10",
           |            "dueDate" : "2018-12-07",
           |            "amount" : 150
           |          }
           |        ]
           |      }
           |    ]
           |  }""".stripMargin

      )

      val expectedSeq: Seq[PaymentsHistoryModel] = Seq(
        PaymentsHistoryModel(
          chargeType = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
          taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
          amount = 150,
          clearedDate = Some(LocalDate.of(2018, 1, 10))
        )
      )

      Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
    }

    "read json into an empty sequence" in {
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

      Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
    }

    "throw an IllegalStateException" when {
      "there is no financialTransactions block" in {
        val testJson: JsValue = Json.parse("""{ "abc" : "xyz" }""")

        val result: IllegalStateException = intercept[IllegalStateException](Json.fromJson(testJson)(reads))
        result.getMessage shouldBe """The data for key financialTransactions could not be found in the Json: {"abc":"xyz"}"""
      }
    }

    "there is no items block" in {

      val testJson: JsValue = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : 555555555,
           |    "regimeType" : "VATC",
           |    "processingDate" : "2018-03-07T09:30:00.000Z",
           |    "financialTransactions" : [
           |      {
           |        "chargeType" : "$ReturnDebitCharge",
           |       "mainType" : "$ReturnCharge",
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

      val result: IllegalStateException = intercept[IllegalStateException](Json.fromJson(testJson)(reads))
      result.getMessage shouldBe s"The data for key items could not be found in the Json"
    }

    "the items list is empty" in {
      val testJson: JsValue = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : 555555555,
           |    "regimeType" : "VATC",
           |    "processingDate" : "2018-03-07T09:30:00.000Z",
           |    "financialTransactions" : [
           |      {
           |        "chargeType" : "$ReturnDebitCharge",
           |       "mainType" : "$ReturnCharge",
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
           |        "outstandingAmount" : 150,
           |        "items" : [
           |        ]
           |      }
           |    ]
           |  }""".stripMargin
      )

      val result = intercept[IllegalStateException](Json.fromJson(testJson)(reads))
      result.getMessage shouldBe "The items list was found but the list was empty"
    }

    "there is no amount" in {
      val testJson = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : 555555555,
           |    "regimeType" : "VATC",
           |    "processingDate" : "2018-03-07T09:30:00.000Z",
           |    "financialTransactions" : [
           |      {
           |        "chargeType" : "$ReturnDebitCharge",
           |       "mainType" : "$ReturnCharge",
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
           |        "outstandingAmount" : 150,
           |        "items" : [
           |          {
           |            "subItem" : "000",
           |            "clearingDate" : "2018-01-10",
           |            "dueDate" : "2018-12-07"
           |          }
           |        ]
           |      }
           |    ]
           |  }""".stripMargin
      )

      val expectedMessageJson = """{"subItem":"000","clearingDate":"2018-01-10","dueDate":"2018-12-07"}"""
      val result = intercept[IllegalStateException](Json.fromJson(testJson)(reads))
      result.getMessage shouldEqual s"The data for key amount could not be found in the Json: $expectedMessageJson"
    }

    "there is no clearingDate should parse successfully" in {
      val testJson: JsValue = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : 555555555,
           |    "regimeType" : "VATC",
           |    "processingDate" : "2018-03-07T09:30:00.000Z",
           |    "financialTransactions" : [
           |      {
           |        "chargeType" : "$ReturnDebitCharge",
           |       "mainType" : "$ReturnCharge",
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
           |        "outstandingAmount" : 150,
           |        "items" : [
           |          {
           |            "subItem" : "000",
           |            "dueDate" : "2018-12-07",
           |            "amount" : 150
           |          }
           |        ]
           |      }
           |    ]
           |  }""".stripMargin
      )

      val expectedSeq: Seq[PaymentsHistoryModel] = Seq.empty

      Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
    }

    "there is no dateFrom should parse successfully" in {
      val testJson: JsValue = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : 555555555,
           |    "regimeType" : "VATC",
           |    "processingDate" : "2018-03-07T09:30:00.000Z",
           |    "financialTransactions" : [
           |      {
           |        "chargeType" : "$ReturnDebitCharge",
           |        "mainType" : "$ReturnCharge",
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
           |        "outstandingAmount" : 150,
           |        "items" : [
           |          {
           |            "subItem" : "000",
           |            "clearingDate" : "2018-01-10",
           |            "dueDate" : "2018-12-07",
           |            "amount" : 150
           |          }
           |        ]
           |      }
           |    ]
           |  }""".stripMargin
      )

      val expectedSeq: Seq[PaymentsHistoryModel] = Seq(
        PaymentsHistoryModel(
          chargeType = ReturnDebitCharge,
          taxPeriodFrom = None,
          taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
          amount = 150,
          clearedDate = Some(LocalDate.of(2018, 1, 10)))
      )

      Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
    }

    "there is no dateTo should parse successfully" in {
      val testJson: JsValue = Json.parse(
        s"""{
           |    "idType" : "VRN",
           |    "idNumber" : 555555555,
           |    "regimeType" : "VATC",
           |    "processingDate" : "2018-03-07T09:30:00.000Z",
           |    "financialTransactions" : [
           |      {
           |        "chargeType" : "$ReturnDebitCharge",
           |       "mainType" : "$ReturnCharge",
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
           |        "outstandingAmount" : 150,
           |        "items" : [
           |          {
           |            "subItem" : "000",
           |            "clearingDate" : "2018-01-10",
           |            "dueDate" : "2018-12-07",
           |            "amount" : 150
           |          }
           |        ]
           |      }
           |    ]
           |  }""".stripMargin
      )

      val expectedSeq: Seq[PaymentsHistoryModel] = Seq(
        PaymentsHistoryModel(
          chargeType = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
          taxPeriodTo = None,
          amount = 150,
          clearedDate = Some(LocalDate.of(2018, 1, 10)))
      )

      Json.fromJson(testJson)(reads) shouldBe JsSuccess(expectedSeq)
    }
  }
}