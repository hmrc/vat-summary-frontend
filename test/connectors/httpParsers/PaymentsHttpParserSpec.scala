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

import connectors.httpParsers.PaymentsHttpParser.PaymentsReads
import models.errors._
import models.payments._
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
              "mainType" -> ReturnCharge,
              "chargeType" -> ReturnDebitCharge,
              "taxPeriodFrom" -> "2016-12-01",
              "taxPeriodTo" -> "2017-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2017-10-25")
              ),
              "outstandingAmount" -> 1000.50,
              "periodKey" -> "#001"
            ),
            Json.obj(
              "mainType" -> ReturnCharge,
              "chargeType" -> ReturnCreditCharge,
              "taxPeriodFrom" -> "2017-12-01",
              "taxPeriodTo" -> "2018-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2018-10-25")
              ),
              "outstandingAmount" -> 1000.51,
              "periodKey" -> "#002"
            ),
            Json.obj(
              "mainType" -> OACharge,
              "chargeType" -> OACreditCharge,
              "taxPeriodFrom" -> "2017-12-01",
              "taxPeriodTo" -> "2018-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2017-10-25")
              ),
              "outstandingAmount" -> 1000.52,
              "periodKey" -> "#003"
            ),
            Json.obj(
              "mainType" -> OACharge,
              "chargeType" -> OADebitCharge,
              "taxPeriodFrom" -> "2017-12-01",
              "taxPeriodTo" -> "2018-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2017-10-25")
              ),
              "outstandingAmount" -> 1000.53,
              "periodKey" -> "#004"
            ),
            Json.obj(
              "mainType" -> CentralAssessmentCharge,
              "chargeType" -> CentralAssessmentCharge,
              "taxPeriodFrom" -> "2016-12-01",
              "taxPeriodTo" -> "2017-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2016-10-25")
              ),
              "outstandingAmount" -> 1000.25,
              "periodKey" -> "#005"
            ),
            Json.obj(
              "mainType" -> DefaultSurcharge,
              "chargeType" -> DefaultSurcharge,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.27,
              "periodKey" -> "#006"
            ),
            Json.obj(
              "mainType" -> ErrorCorrectionCharge,
              "chargeType" -> ErrorCorrectionCreditCharge,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.29,
              "periodKey" -> "#007"
            ),
            Json.obj(
              "mainType" -> ErrorCorrectionCharge,
              "chargeType" -> ErrorCorrectionDebitCharge,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.30,
              "periodKey" -> "#007"
            ),
            Json.obj(
              "mainType" -> AAInterestCharge,
              "chargeType" -> AAInterestCharge,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.30,
              "periodKey" -> "#008"
            ),
            Json.obj(
              "mainType" -> OADefaultInterestCharge,
              "chargeType" -> OADefaultInterestCharge,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.30,
              "periodKey" -> "#018"
            ),
            Json.obj(
              "mainType" -> AACharge,
              "chargeType" -> AACharge,
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
              "mainType" -> OAFurtherInterestCharge,
              "chargeType" -> OAFurtherInterestCharge,
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
              "mainType" -> BnpRegPre2010Charge,
              "chargeType" -> BnpRegPre2010Charge,
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
              "mainType" -> BnpRegPost2010Charge,
              "chargeType" -> BnpRegPost2010Charge,
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
              "mainType" -> FtnMatPre2010Charge,
              "chargeType" -> FtnMatPre2010Charge,
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
              "mainType" -> FtnMatPost2010Charge,
              "chargeType" -> FtnMatPost2010Charge,
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
              "mainType" -> MiscPenaltyCharge,
              "chargeType" -> MiscPenaltyCharge,
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
              "mainType" -> FtnEachPartnerCharge,
              "chargeType" -> FtnEachPartnerCharge,
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
              "mainType" -> MpPre2009Charge,
              "chargeType" -> MpPre2009Charge,
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
              "mainType" -> MpRepeatedPre2009Charge,
              "chargeType" -> MpRepeatedPre2009Charge,
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
              "mainType" -> CivilEvasionPenaltyCharge,
              "chargeType" -> CivilEvasionPenaltyCharge,
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
            ),
            Json.obj(
              "mainType" -> InaccuraciesAssessmentsPenCharge,
              "chargeType" -> InaccuraciesAssessmentsPenCharge,
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
            ),
            Json.obj(
              "mainType" -> InaccuraciesReturnReplacedCharge,
              "chargeType" -> InaccuraciesReturnReplacedCharge,
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
            ),
            Json.obj(
              "mainType" -> CarterPenaltyCharge,
              "chargeType" -> CarterPenaltyCharge,
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
            ),
            Json.obj(
              "mainType" -> WrongDoingPenaltyCharge,
              "chargeType" -> WrongDoingPenaltyCharge,
              "periodKey" -> "#018",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2008-09-27",
                  "amount" -> 50.00
                )
              )
            ),
            Json.obj(
              "mainType" -> FailureToSubmitRCSLCharge,
              "chargeType" -> FailureToSubmitRCSLCharge,
              "periodKey" -> "#018",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2008-09-27",
                  "amount" -> 50.00
                )
              )
            ),
            Json.obj(
              "mainType" -> FailureToNotifyRCSLCharge,
              "chargeType" -> FailureToNotifyRCSLCharge,
              "periodKey" -> "#018",
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
          ReturnDebitCharge,
          start = LocalDate.parse("2016-12-01"),
          end = LocalDate.parse("2017-01-01"),
          due = LocalDate.parse("2017-10-25"),
          outstandingAmount = BigDecimal(1000.50),
          periodKey = "#001"
        ),
        PaymentWithPeriod(
          OADebitCharge,
          start = LocalDate.parse("2017-12-01"),
          end = LocalDate.parse("2018-01-01"),
          due = LocalDate.parse("2017-10-25"),
          outstandingAmount = BigDecimal(1000.53),
          periodKey = "#004"
        ),
        PaymentWithPeriod(
          CentralAssessmentCharge,
          start = LocalDate.parse("2016-12-01"),
          end = LocalDate.parse("2017-01-01"),
          due = LocalDate.parse("2016-10-25"),
          outstandingAmount = BigDecimal(1000.25),
          periodKey = "#005"
        ),
        PaymentWithPeriod(
          DefaultSurcharge,
          start = LocalDate.parse("2015-12-01"),
          end = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.27),
          periodKey = "#006"
        ),
        PaymentWithPeriod(
          ErrorCorrectionDebitCharge,
          start = LocalDate.parse("2015-12-01"),
          end = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = "#007"
        ),
        PaymentWithPeriod(
          AAInterestCharge,
          start = LocalDate.parse("2015-12-01"),
          end = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = "#008"
        ),
        PaymentWithPeriod(
          OADefaultInterestCharge,
          start = LocalDate.parse("2015-12-01"),
          end = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = "#018"
        ),
        PaymentWithPeriod(
          AACharge,
          start = LocalDate.parse("2016-03-20"),
          end = LocalDate.parse("2016-06-21"),
          due = LocalDate.parse("2016-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#009"
        ),
        PaymentWithPeriod(
          BnpRegPre2010Charge,
          start = LocalDate.parse("2015-03-20"),
          end = LocalDate.parse("2015-06-21"),
          due = LocalDate.parse("2015-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#019"
        ),
        PaymentWithPeriod(
          BnpRegPost2010Charge,
          start = LocalDate.parse("2015-03-20"),
          end = LocalDate.parse("2015-06-21"),
          due = LocalDate.parse("2015-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#011"
        ),
        PaymentWithPeriod(
          FtnMatPre2010Charge,
          start = LocalDate.parse("2014-03-20"),
          end = LocalDate.parse("2014-06-21"),
          due = LocalDate.parse("2014-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#012"
        ),
        PaymentWithPeriod(
          FtnMatPost2010Charge,
          start = LocalDate.parse("2013-03-20"),
          end = LocalDate.parse("2013-06-21"),
          due = LocalDate.parse("2013-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#013"
        ),
        PaymentWithPeriod(
          MiscPenaltyCharge,
          start = LocalDate.parse("2012-03-20"),
          end = LocalDate.parse("2012-06-21"),
          due = LocalDate.parse("2012-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#014"
        ),
        PaymentWithPeriod(
          FtnEachPartnerCharge,
          start = LocalDate.parse("2011-03-20"),
          end = LocalDate.parse("2011-06-21"),
          due = LocalDate.parse("2011-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#015"
        ),
        PaymentWithPeriod(
          MpPre2009Charge,
          start = LocalDate.parse("2010-03-20"),
          end = LocalDate.parse("2010-06-21"),
          due = LocalDate.parse("2010-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#016"
        ),
        PaymentWithPeriod(
          MpRepeatedPre2009Charge,
          start = LocalDate.parse("2009-03-20"),
          end = LocalDate.parse("2009-06-21"),
          due = LocalDate.parse("2009-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#017"
        ),
        PaymentWithPeriod(
          CivilEvasionPenaltyCharge,
          start = LocalDate.parse("2008-03-20"),
          end = LocalDate.parse("2008-06-21"),
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#018"
        ),
        Payment(
          InaccuraciesAssessmentsPenCharge,
          start = LocalDate.parse("2008-03-20"),
          end = LocalDate.parse("2008-06-21"),
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018")
        ),
        Payment(
          InaccuraciesReturnReplacedCharge,
          start = LocalDate.parse("2008-03-20"),
          end = LocalDate.parse("2008-06-21"),
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018")
        ),
        Payment(
          CarterPenaltyCharge,
          start = LocalDate.parse("2008-03-20"),
          end = LocalDate.parse("2008-06-21"),
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018")
        ),
        Payment(
          WrongDoingPenaltyCharge,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018")
        ),
        Payment(
          FailureToSubmitRCSLCharge,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018")
        ),
        Payment(
          FailureToNotifyRCSLCharge,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018")
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
              "mainType" -> ReturnCharge,
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