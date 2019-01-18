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

package connectors.httpParsers

import java.time.LocalDate

import connectors.httpParsers.PaymentsHistoryHttpParser.PaymentsHistoryReads
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.errors._
import models.payments._
import models.viewModels.PaymentsHistoryModel
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.test.UnitSpec

class PaymentsHistoryHttpParserSpec extends UnitSpec {

  val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.OK, Some(Json.parse(
    s"""{
       |    "idType" : "VRN",
       |    "idNumber" : "555555555",
       |    "regimeType" : "VATC",
       |    "processingDate" : "2018-03-07T09:30:00.000Z",
       |    "financialTransactions" : [
       |      {
       |        "chargeType" : "${ReturnDebitCharge}",
       |        "mainType" : "${ReturnCharge}",
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
       |        "mainType" : "${ReturnCharge}",
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
       |        "chargeType" : "${OADebitCharge}",
       |        "mainType" : "${OACharge}",
       |        "periodKey" : "17AA",
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
       |        "originalAmount" : 150,
       |        "outstandingAmount" : 150,
       |        "items" : [
       |          {
       |            "subItem" : "000",
       |            "clearingDate" : "2018-04-14",
       |            "dueDate" : "2018-09-07",
       |            "amount" : 200
       |          }
       |        ]
       |      },
       |      {
       |        "chargeType" : "${OACreditCharge}",
       |        "mainType" : "${OACharge}",
       |        "periodKey" : "17AA",
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
       |        "originalAmount" : 150,
       |        "outstandingAmount" : 150,
       |        "items" : [
       |          {
       |            "subItem" : "000",
       |            "clearingDate" : "2018-06-28",
       |            "dueDate" : "2018-09-07",
       |            "amount" : 550
       |          }
       |        ]
       |      },
       |      {
       |        "chargeType" : "${DefaultSurcharge}",
       |        "mainType" : "${DefaultSurcharge}",
       |        "periodKey" : "17AA",
       |        "periodKeyDescription" : "ABCD",
       |        "taxPeriodFrom" : "2018-06-10",
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
       |            "clearingDate" : "2018-11-10",
       |            "dueDate" : "2018-09-07",
       |            "amount" : 150
       |          }
       |        ]
       |      },
       |      {
       |        "chargeType" : "${CentralAssessmentCharge}",
       |        "mainType" : "${CentralAssessmentCharge}",
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
       |            "clearingDate" : "2018-11-10",
       |            "dueDate" : "2018-11-10",
       |            "amount" : 150
       |          }
       |        ]
       |      },
       |      {
       |        "chargeType" : "${ErrorCorrectionCreditCharge}",
       |        "mainType" : "${ErrorCorrectionCharge}",
       |        "periodKey" : "17AA",
       |        "periodKeyDescription" : "ABCD",
       |        "taxPeriodFrom" : "2018-02-01",
       |        "taxPeriodTo" : "2018-05-20",
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
       |            "clearingDate" : "2018-06-10",
       |            "dueDate" : "2018-06-10",
       |            "amount" : 150
       |          }
       |        ]
       |      },
       |       {
       |        "chargeType" : "${ErrorCorrectionDebitCharge}",
       |        "mainType" : "${ErrorCorrectionCharge}",
       |        "periodKey" : "17AA",
       |        "periodKeyDescription" : "ABCD",
       |        "taxPeriodFrom" : "2018-10-12",
       |        "taxPeriodTo" : "2018-12-12",
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
       |            "clearingDate" : "2018-12-15",
       |            "dueDate" : "2018-09-07",
       |            "amount" : 1000
       |          }
       |        ]
       |      },
       |      {
       |        "chargeType" : "${OADefaultInterestCharge}",
       |        "mainType" : "${OADefaultInterestCharge}",
       |        "periodKey" : "17AA",
       |        "periodKeyDescription" : "ABCD",
       |        "taxPeriodFrom" : "2018-10-12",
       |        "taxPeriodTo" : "2018-12-12",
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
       |            "clearingDate" : "2018-12-15",
       |            "dueDate" : "2018-09-07",
       |            "amount" : 1000
       |          }
       |        ]
       |      },
       |      {
       |        "chargeType" : "$VatOfficersAssessmentFurtherInterestCharge",
       |        "mainType" : "$VatOfficersAssessmentFurtherInterestCharge",
       |        "periodKey" : "17AA",
       |        "periodKeyDescription" : "ABCD",
       |        "taxPeriodFrom" : "2018-10-12",
       |        "taxPeriodTo" : "2018-12-12",
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
       |        "outstandingAmount" : 0,
       |        "items" : [
       |          {
       |            "subItem" : "000",
       |            "clearingDate" : "2018-12-15",
       |            "dueDate" : "2018-09-07",
       |            "amount" : 150
       |          }
       |        ]
       |      },
       |      {
       |        "chargeType" : "$AACharge",
       |        "mainType" : "$AACharge",
       |        "periodKey" : "17AA",
       |        "periodKeyDescription" : "ABCD",
       |        "taxPeriodFrom" : "2018-10-12",
       |        "taxPeriodTo" : "2018-12-12",
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
       |        "originalAmount" : 400,
       |        "outstandingAmount" : 0,
       |        "items" : [
       |          {
       |            "subItem" : "000",
       |            "clearingDate" : "2018-12-15",
       |            "dueDate" : "2018-09-07",
       |            "amount" : 400
       |          }
       |        ]
       |      },
       |      {
       |        "chargeType" : "$AAInterestCharge",
       |        "mainType" : "$AAInterestCharge",
       |        "periodKey" : "17AA",
       |        "periodKeyDescription" : "ABCD",
       |        "taxPeriodFrom" : "2018-06-12",
       |        "taxPeriodTo" : "2018-09-12",
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
       |        "originalAmount" : 555,
       |        "outstandingAmount" : 0,
       |        "items" : [
       |          {
       |            "subItem" : "000",
       |            "clearingDate" : "2018-10-07",
       |            "dueDate" : "2018-10-08",
       |            "amount" : 555
       |          }
       |        ]
       |      },
       |      {
       |        "chargeType" : "$StatutoryInterestCharge",
       |        "mainType" : "$StatutoryInterestCharge",
       |        "periodKey" : "17AA",
       |        "periodKeyDescription" : "ABCD",
       |        "taxPeriodFrom" : "2018-06-12",
       |        "taxPeriodTo" : "2018-09-12",
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
       |        "originalAmount" : -555,
       |        "outstandingAmount" : 0,
       |        "items" : [
       |          {
       |            "subItem" : "000",
       |            "clearingDate" : "2018-10-07",
       |            "dueDate" : "2018-10-08",
       |            "amount" : -555
       |          }
       |        ]
       |      }
       |    ]
       |  }""".stripMargin
  )))

  val expected: Either[Nothing, Seq[PaymentsHistoryModel]] = Right(Seq(
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
    ),
    PaymentsHistoryModel(
      chargeType = OADebitCharge,
      taxPeriodFrom = Some(LocalDate.of(2018, 5, 1)),
      taxPeriodTo = Some(LocalDate.of(2018, 7, 31)),
      amount = 200,
      clearedDate = Some(LocalDate.of(2018, 4, 14))
    ),
    PaymentsHistoryModel(
      chargeType = OACreditCharge,
      taxPeriodFrom = Some(LocalDate.of(2018, 5, 1)),
      taxPeriodTo = Some(LocalDate.of(2018, 7, 31)),
      amount = 550,
      clearedDate = Some(LocalDate.of(2018, 6, 28))
    ),
    PaymentsHistoryModel(
      chargeType = DefaultSurcharge,
      taxPeriodFrom = Some(LocalDate.of(2018, 6, 10)),
      taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
      amount = 150,
      clearedDate = Some(LocalDate.of(2018, 11, 10))
    ),
    PaymentsHistoryModel(
      chargeType = CentralAssessmentCharge,
      taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
      taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
      amount = 150,
      clearedDate = Some(LocalDate.of(2018, 11, 10))
    ),
    PaymentsHistoryModel(
      chargeType = ErrorCorrectionCreditCharge,
      taxPeriodFrom = Some(LocalDate.of(2018, 2, 1)),
      taxPeriodTo = Some(LocalDate.of(2018, 5, 20)),
      amount = 150,
      clearedDate = Some(LocalDate.of(2018, 6, 10))
    ),
    PaymentsHistoryModel(
      chargeType= ErrorCorrectionDebitCharge,
      taxPeriodFrom = Some(LocalDate.of(2018, 10, 12)),
      taxPeriodTo = Some(LocalDate.of(2018, 12, 12)),
      amount = 1000,
      clearedDate = Some(LocalDate.of(2018, 12, 15))
    ),
    PaymentsHistoryModel(
      chargeType= OADefaultInterestCharge,
      taxPeriodFrom = Some(LocalDate.of(2018, 10, 12)),
      taxPeriodTo = Some(LocalDate.of(2018, 12, 12)),
      amount = 1000,
      clearedDate = Some(LocalDate.of(2018, 12, 15))
    ),
    PaymentsHistoryModel(
      chargeType = VatOfficersAssessmentFurtherInterestCharge,
      taxPeriodFrom = Some(LocalDate.of(2018, 10, 12)),
      taxPeriodTo = Some(LocalDate.of(2018, 12, 12)),
      amount = 150,
      clearedDate = Some(LocalDate.of(2018, 12, 15))
    ),
    PaymentsHistoryModel(
      chargeType = AACharge,
      taxPeriodFrom = Some(LocalDate.of(2018, 10, 12)),
      taxPeriodTo = Some(LocalDate.of(2018, 12, 12)),
      amount = 400,
      clearedDate = Some(LocalDate.of(2018, 12, 15))
    ),
    PaymentsHistoryModel(
      chargeType = AAInterestCharge,
      taxPeriodFrom = Some(LocalDate.of(2018, 6, 12)),
      taxPeriodTo = Some(LocalDate.of(2018, 9, 12)),
      amount = 555,
      clearedDate = Some(LocalDate.of(2018, 10, 7))
    ),
    PaymentsHistoryModel(
      chargeType = StatutoryInterestCharge,
      taxPeriodFrom = Some(LocalDate.of(2018, 6, 12)),
      taxPeriodTo = Some(LocalDate.of(2018, 9, 12)),
      amount = -555,
      clearedDate = Some(LocalDate.of(2018, 10, 7))
    )
  ))

  val result: HttpGetResult[Seq[PaymentsHistoryModel]] = PaymentsHistoryReads.read("", "", httpResponse)

  "return a Payments instance" in {
    result shouldBe expected
  }

  "the http response is 200 OK and there are no valid charge types" should {
    val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.OK, Some(Json.parse(
      s"""{
         |    "idType" : "VRN",
         |    "idNumber" : 555555555,
         |    "regimeType" : "VATC",
         |    "processingDate" : "2018-03-07T09:30:00.000Z",
         |    "financialTransactions" : [
         |      {
         |        "chargeType" : "Some Other Charge Type",
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
         |      },
         |      {"chargeType" : "Some Other Charge Type",
         |        "mainType" : "${OACharge}",
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

    )))

    val expected: Either[Nothing, Seq[Nothing]] = Right(Seq.empty)

    val result: HttpGetResult[Seq[PaymentsHistoryModel]] = PaymentsHistoryReads.read("", "", httpResponse)

    "return an empty Payments instance" in {
      result shouldBe expected
    }
  }

  "the http response status is 404 NOT_FOUND" should {

    val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.NOT_FOUND, None)

    val expected: Either[Nothing, Seq[Nothing]] = Right(Seq.empty)

    val result: HttpGetResult[Seq[PaymentsHistoryModel]] = PaymentsHistoryReads.read("", "", httpResponse)

    "return an empty Payments object" in {
      result shouldBe expected
    }
  }

  "the http response status is 400 BAD_REQUEST (single error)" should {

    val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_REQUEST,
      responseJson = Some(Json.obj(
        "code" -> "VRN_INVALID",
        "reason" -> "Fail!"
      ))
    )

    val expected: Either[BadRequestError, Nothing] = Left(BadRequestError(
      code = "VRN_INVALID",
      errorResponse = "Fail!"
    ))

    val result: HttpGetResult[Seq[PaymentsHistoryModel]] = PaymentsHistoryReads.read("", "", httpResponse)

    "return a BadRequestError" in {
      result shouldBe expected
    }
  }

  "a http response of 400 BAD_REQUEST (multiple errors)" should {

    val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_REQUEST,
      responseJson = Some(Json.obj(
        "failures" -> Json.arr(
          Json.obj(
            "code" -> "INVALID DATE FROM",
            "reason" -> "Bad date from"
          ),
          Json.obj(
            "code" -> "INVALID DATE TO",
            "reason" -> "Bad date to"
          )
        )
      ))
    )

    val errors: Seq[ApiSingleError] = Seq(ApiSingleError("INVALID DATE FROM", "Bad date from"), ApiSingleError("INVALID DATE TO", "Bad date to"))

    val expected: Either[MultipleErrors, Nothing] = Left(MultipleErrors(Status.BAD_REQUEST.toString, Json.toJson(errors).toString()))

    val result = PaymentsHistoryReads.read("", "", httpResponse)

    "return a MultipleErrors" in {
      result shouldBe expected
    }
  }

  "the http response status is 400 BAD_REQUEST (unknown API error json)" should {

    val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.BAD_REQUEST,
      responseJson = Some(Json.obj(
        "foo" -> "INVALID",
        "bar" -> "Fail!"
      ))
    )

    val expected: Either[UnknownError.type, Nothing] = Left(UnknownError)

    val result = PaymentsHistoryReads.read("", "", httpResponse)

    "return a UnknownError" in {
      result shouldBe expected
    }
  }

  "the HTTP response status is 5xx" should {

    val body: JsObject = Json.obj(
      "code" -> "GATEWAY_TIMEOUT",
      "message" -> "GATEWAY_TIMEOUT"
    )

    val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.GATEWAY_TIMEOUT, Some(body))
    val expected: Either[ServerSideError, Nothing] = Left(ServerSideError(Status.GATEWAY_TIMEOUT.toString, httpResponse.body))
    val result: HttpGetResult[Seq[PaymentsHistoryModel]] = PaymentsHistoryReads.read("", "", httpResponse)

    "return a ServerSideError" in {
      result shouldBe expected
    }
  }

  "the HTTP response status isn't handled" should {

    val body: JsObject = Json.obj(
      "code" -> "Conflict",
      "message" -> "CONFLICT"
    )

    val httpResponse: AnyRef with HttpResponse = HttpResponse(Status.CONFLICT, Some(body))
    val expected: Either[UnexpectedStatusError, Nothing] = Left(UnexpectedStatusError("409", httpResponse.body))
    val result: HttpGetResult[Seq[PaymentsHistoryModel]] = PaymentsHistoryReads.read("", "", httpResponse)

    "return an UnexpectedStatusError" in {
      result shouldBe expected
    }
  }
}
