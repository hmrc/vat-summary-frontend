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

package connectors.httpParsers

import java.time.LocalDate

import common.FinancialTransactionsConstants
import connectors.httpParsers.PaymentsHttpParser.PaymentsReads
import models.errors._
import models.payments.{PaymentWithPeriod, Payments}
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.test.UnitSpec

class PaymentsHttpParserSpec extends UnitSpec {

  "PaymentsReads" when {

    "the http response status is 200 OK and there are valid charge types" should {

      val httpResponse = HttpResponse(Status.OK, responseJson = Some(
        Json.obj(
          "financialTransactions" -> Json.arr(
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.vatReturnCharge,
              "chargeType" -> FinancialTransactionsConstants.vatReturnDebitCharge,
              "taxPeriodFrom" -> "2016-12-01",
              "taxPeriodTo" -> "2017-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2017-10-25")
              ),
              "outstandingAmount" -> 1000.50,
              "periodKey" -> "#001"
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.vatReturnCharge,
              "chargeType" -> FinancialTransactionsConstants.vatReturnCreditCharge,
              "taxPeriodFrom" -> "2017-12-01",
              "taxPeriodTo" -> "2018-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2018-10-25")
              ),
              "outstandingAmount" -> 1000.51,
              "periodKey" -> "#002"
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.officerAssessmentCharge,
              "chargeType" -> FinancialTransactionsConstants.officerAssessmentCreditCharge,
              "taxPeriodFrom" -> "2017-12-01",
              "taxPeriodTo" -> "2018-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2017-10-25")
              ),
              "outstandingAmount" -> 1000.52,
              "periodKey" -> "#003"
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.officerAssessmentCharge,
              "chargeType" -> FinancialTransactionsConstants.officerAssessmentDebitCharge,
              "taxPeriodFrom" -> "2017-12-01",
              "taxPeriodTo" -> "2018-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2017-10-25")
              ),
              "outstandingAmount" -> 1000.53,
              "periodKey" -> "#004"
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.vatCentralAssessment,
              "chargeType" -> FinancialTransactionsConstants.vatCentralAssessment,
              "taxPeriodFrom" -> "2016-12-01",
              "taxPeriodTo" -> "2017-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2016-10-25")
              ),
              "outstandingAmount" -> 1000.25,
              "periodKey" -> "#005"
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.vatDefaultSurcharge,
              "chargeType" -> FinancialTransactionsConstants.vatDefaultSurcharge,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.27,
              "periodKey" -> "#006"
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.errorCorrectionChargeType,
              "chargeType" -> FinancialTransactionsConstants.errorCorrectionCreditCharge,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.29,
              "periodKey" -> "#007"
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.errorCorrectionChargeType,
              "chargeType" -> FinancialTransactionsConstants.errorCorrectionDebitCharge,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.30,
              "periodKey" -> "#007"
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.vatAdditionalAssessmentInterest,
              "chargeType" -> FinancialTransactionsConstants.vatAdditionalAssessmentInterest,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.30,
              "periodKey" -> "#008"
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.officerAssessmentDefaultInterest,
              "chargeType" -> FinancialTransactionsConstants.officerAssessmentDefaultInterest,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.30,
              "periodKey" -> "#018"
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.vatAdditionalAssessment,
              "chargeType" -> FinancialTransactionsConstants.vatAdditionalAssessment,
              "periodKey" -> "#009",
              "taxPeriodFrom" -> "2016-03-20",
              "taxPeriodTo" -> "2016-06-21",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2016-09-27",
                  "amount" -> 50.00
                )
              )
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.vatOfficersAssessment,
              "chargeType" -> FinancialTransactionsConstants.vatOfficersAssessment,
              "periodKey" -> "#010",
              "taxPeriodFrom" -> "2016-03-20",
              "taxPeriodTo" -> "2016-06-21",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2016-09-27",
                  "amount" -> 50.00
                )
              )
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.vatBNPofRegPre2010,
              "chargeType" -> FinancialTransactionsConstants.vatBNPofRegPre2010,
              "periodKey" -> "#019",
              "taxPeriodFrom" -> "2015-03-20",
              "taxPeriodTo" -> "2015-06-21",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2015-09-27",
                  "amount" -> 50.00
                )
              )
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.vatBnpRegPost2010,
              "chargeType" -> FinancialTransactionsConstants.vatBnpRegPost2010,
              "periodKey" -> "#011",
              "taxPeriodFrom" -> "2015-03-20",
              "taxPeriodTo" -> "2015-06-21",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2015-09-27",
                  "amount" -> 50.00
                )
              )
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.vatFtnMatPre2010,
              "chargeType" -> FinancialTransactionsConstants.vatFtnMatPre2010,
              "periodKey" -> "#012",
              "taxPeriodFrom" -> "2014-03-20",
              "taxPeriodTo" -> "2014-06-21",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2014-09-27",
                  "amount" -> 50.00
                )
              )
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.vatFtnMatPost2010,
              "chargeType" -> FinancialTransactionsConstants.vatFtnMatPost2010,
              "periodKey" -> "#013",
              "taxPeriodFrom" -> "2013-03-20",
              "taxPeriodTo" -> "2013-06-21",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2013-09-27",
                  "amount" -> 50.00
                )
              )
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.vatMiscPenalty,
              "chargeType" -> FinancialTransactionsConstants.vatMiscPenalty,
              "periodKey" -> "#014",
              "taxPeriodFrom" -> "2012-03-20",
              "taxPeriodTo" -> "2012-06-21",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2012-09-27",
                  "amount" -> 50.00
                )
              )
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.vatFtnEachpartner,
              "chargeType" -> FinancialTransactionsConstants.vatFtnEachpartner,
              "periodKey" -> "#015",
              "taxPeriodFrom" -> "2011-03-20",
              "taxPeriodTo" -> "2011-06-21",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2011-09-27",
                  "amount" -> 50.00
                )
              )
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.vatMpPre2009,
              "chargeType" -> FinancialTransactionsConstants.vatMpPre2009,
              "periodKey" -> "#016",
              "taxPeriodFrom" -> "2010-03-20",
              "taxPeriodTo" -> "2010-06-21",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2010-09-27",
                  "amount" -> 50.00
                )
              )
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.vatMpRepeatedPre2009,
              "chargeType" -> FinancialTransactionsConstants.vatMpRepeatedPre2009,
              "periodKey" -> "#017",
              "taxPeriodFrom" -> "2009-03-20",
              "taxPeriodTo" -> "2009-06-21",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2009-09-27",
                  "amount" -> 50.00
                )
              )
            ),
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.vatCivilEvasionPenalty,
              "chargeType" -> FinancialTransactionsConstants.vatCivilEvasionPenalty,
              "periodKey" -> "#018",
              "taxPeriodFrom" -> "2008-03-20",
              "taxPeriodTo" -> "2008-06-21",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2008-09-27",
                  "amount" -> 50.00
                )
              )
            )
          )
        )
      ))

      val expected = Right(Payments(Seq(
        PaymentWithPeriod(
          FinancialTransactionsConstants.vatReturnDebitCharge,
          start = LocalDate.parse("2016-12-01"),
          end = LocalDate.parse("2017-01-01"),
          due = LocalDate.parse("2017-10-25"),
          outstandingAmount = BigDecimal(1000.50),
          periodKey = "#001"
        ),
        PaymentWithPeriod(
          FinancialTransactionsConstants.officerAssessmentDebitCharge,
          start = LocalDate.parse("2017-12-01"),
          end = LocalDate.parse("2018-01-01"),
          due = LocalDate.parse("2017-10-25"),
          outstandingAmount = BigDecimal(1000.53),
          periodKey = "#004"
        ),
        PaymentWithPeriod(
          FinancialTransactionsConstants.vatCentralAssessment,
          start = LocalDate.parse("2016-12-01"),
          end = LocalDate.parse("2017-01-01"),
          due = LocalDate.parse("2016-10-25"),
          outstandingAmount = BigDecimal(1000.25),
          periodKey = "#005"
        ),
        PaymentWithPeriod(
          FinancialTransactionsConstants.vatDefaultSurcharge,
          start = LocalDate.parse("2015-12-01"),
          end = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.27),
          periodKey = "#006"
        ),
        PaymentWithPeriod(
          FinancialTransactionsConstants.errorCorrectionDebitCharge,
          start = LocalDate.parse("2015-12-01"),
          end = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = "#007"
        ),
        PaymentWithPeriod(
          FinancialTransactionsConstants.vatAdditionalAssessmentInterest,
          start = LocalDate.parse("2015-12-01"),
          end = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = "#008"
        ),
        PaymentWithPeriod(
          FinancialTransactionsConstants.officerAssessmentDefaultInterest,
          start = LocalDate.parse("2015-12-01"),
          end = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = "#018"
        ),
        PaymentWithPeriod(
          FinancialTransactionsConstants.vatAdditionalAssessment,
          start = LocalDate.parse("2016-03-20"),
          end = LocalDate.parse("2016-06-21"),
          due = LocalDate.parse("2016-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#009"
        ),
        PaymentWithPeriod(
          FinancialTransactionsConstants.vatOfficersAssessment,
          start = LocalDate.parse("2016-03-20"),
          end = LocalDate.parse("2016-06-21"),
          due = LocalDate.parse("2016-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#010"
        ),
        PaymentWithPeriod(
          FinancialTransactionsConstants.vatBNPofRegPre2010,
          start = LocalDate.parse("2015-03-20"),
          end = LocalDate.parse("2015-06-21"),
          due = LocalDate.parse("2015-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#019"
        ),
        PaymentWithPeriod(
          FinancialTransactionsConstants.vatBnpRegPost2010,
          start = LocalDate.parse("2015-03-20"),
          end = LocalDate.parse("2015-06-21"),
          due = LocalDate.parse("2015-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#011"
        ),
        PaymentWithPeriod(
          FinancialTransactionsConstants.vatFtnMatPre2010,
          start = LocalDate.parse("2014-03-20"),
          end = LocalDate.parse("2014-06-21"),
          due = LocalDate.parse("2014-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#012"
        ),
        PaymentWithPeriod(
          FinancialTransactionsConstants.vatFtnMatPost2010,
          start = LocalDate.parse("2013-03-20"),
          end = LocalDate.parse("2013-06-21"),
          due = LocalDate.parse("2013-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#013"
        ),
        PaymentWithPeriod(
          FinancialTransactionsConstants.vatMiscPenalty,
          start = LocalDate.parse("2012-03-20"),
          end = LocalDate.parse("2012-06-21"),
          due = LocalDate.parse("2012-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#014"
        ),
        PaymentWithPeriod(
          FinancialTransactionsConstants.vatFtnEachpartner,
          start = LocalDate.parse("2011-03-20"),
          end = LocalDate.parse("2011-06-21"),
          due = LocalDate.parse("2011-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#015"
        ),
        PaymentWithPeriod(
          FinancialTransactionsConstants.vatMpPre2009,
          start = LocalDate.parse("2010-03-20"),
          end = LocalDate.parse("2010-06-21"),
          due = LocalDate.parse("2010-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#016"
        ),
        PaymentWithPeriod(
          FinancialTransactionsConstants.vatMpRepeatedPre2009,
          start = LocalDate.parse("2009-03-20"),
          end = LocalDate.parse("2009-06-21"),
          due = LocalDate.parse("2009-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#017"
        ),
        PaymentWithPeriod(
          FinancialTransactionsConstants.vatCivilEvasionPenalty,
          start = LocalDate.parse("2008-03-20"),
          end = LocalDate.parse("2008-06-21"),
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#018"
        )
      )))


      val result = PaymentsReads.read("", "", httpResponse)

      "return a Payments instance" in {
        result shouldBe expected
      }
    }

    "the http response is 200 OK and there are no valid charge types" should {
      val httpResponse = HttpResponse(Status.OK, responseJson = Some(
        Json.obj(
          "financialTransactions" -> Json.arr(
            Json.obj(
              "mainType" -> FinancialTransactionsConstants.vatReturnCharge,
              "chargeType" -> "Other Charge Type",
              "outstandingAmount" -> 99
            )
          )
        )
      ))

      val expected = Right(Payments(Seq.empty))

      val result = PaymentsReads.read("", "", httpResponse)

      "return an empty Payments instance" in {
        result shouldBe expected
      }
    }

    "the http response status is 404 NOT_FOUND" should {

      val httpResponse = HttpResponse(Status.NOT_FOUND, None)

      val expected = Right(Payments(Seq.empty))

      val result = PaymentsReads.read("", "", httpResponse)

      "return an empty Payments object" in {
        result shouldBe expected
      }
    }

    "the http response status is 400 BAD_REQUEST (single error)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "code" -> "VRN_INVALID",
          "reason" -> "Fail!"
        ))
      )

      val expected = Left(BadRequestError(
        code = "VRN_INVALID",
        errorResponse = "Fail!"
      ))

      val result = PaymentsReads.read("", "", httpResponse)

      "return a BadRequestError" in {
        result shouldBe expected
      }
    }

    "a http response of 400 BAD_REQUEST (multiple errors)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
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

      val errors = Seq(ApiSingleError("INVALID DATE FROM", "Bad date from"), ApiSingleError("INVALID DATE TO", "Bad date to"))

      val expected = Left(MultipleErrors(Status.BAD_REQUEST.toString, Json.toJson(errors).toString()))

      val result = PaymentsReads.read("", "", httpResponse)

      "return a MultipleErrors" in {
        result shouldBe expected
      }
    }

    "the http response status is 400 BAD_REQUEST (unknown API error json)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        responseJson = Some(Json.obj(
          "foo" -> "INVALID",
          "bar" -> "Fail!"
        ))
      )

      val expected = Left(UnknownError)

      val result = PaymentsReads.read("", "", httpResponse)

      "return a UnknownError" in {
        result shouldBe expected
      }
    }


    "the HTTP response status is 5xx" should {

      val body: JsObject = Json.obj(
        "code" -> "GATEWAY_TIMEOUT",
        "message" -> "GATEWAY_TIMEOUT"
      )

      val httpResponse = HttpResponse(Status.GATEWAY_TIMEOUT, Some(body))
      val expected = Left(ServerSideError(Status.GATEWAY_TIMEOUT.toString, httpResponse.body))
      val result = PaymentsReads.read("", "", httpResponse)

      "return a ServerSideError" in {
        result shouldBe expected
      }
    }

    "the HTTP response status isn't handled" should {

      val body: JsObject = Json.obj(
        "code" -> "Conflict",
        "message" -> "CONFLICT"
      )

      val httpResponse = HttpResponse(Status.CONFLICT, Some(body))
      val expected = Left(UnexpectedStatusError("409", httpResponse.body))
      val result = PaymentsReads.read("", "", httpResponse)

      "return an UnexpectedStatusError" in {
        result shouldBe expected
      }
    }
  }
}