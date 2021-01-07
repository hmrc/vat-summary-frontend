/*
 * Copyright 2021 HM Revenue & Customs
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

  val paymentOnAccountReturnChargeMainType: String = "VAT POA Return Charge"
  val paymentOnAccountInstalmentsMainType: String = "VAT POA Instalments"

  "PaymentsReads" when {

    "the http response status is 200 OK and there are valid charge types" should {

      val httpResponse = HttpResponse(Status.OK,
        Json.obj(
          "financialTransactions" -> Json.arr(
            Json.obj(
              "mainType" -> "VAT Return Charge",
              "chargeType" -> ReturnDebitCharge,
              "taxPeriodFrom" -> "2016-12-01",
              "taxPeriodTo" -> "2017-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2017-10-25")
              ),
              "outstandingAmount" -> 1000.50,
              "periodKey" -> "#001",
              "chargeReference" -> "XD002750002155"
            ),
            Json.obj(
              "mainType" -> "VAT Return Charge",
              "chargeType" -> ReturnCreditCharge,
              "taxPeriodFrom" -> "2017-12-01",
              "taxPeriodTo" -> "2018-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2018-10-25")
              ),
              "outstandingAmount" -> 1000.51,
              "periodKey" -> "#002",
              "chargeReference" -> "XD002750002155"
            ),
            Json.obj(
              "mainType" -> "VAT Officer's Assessment",
              "chargeType" -> OACreditCharge,
              "taxPeriodFrom" -> "2017-12-01",
              "taxPeriodTo" -> "2018-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2017-10-25")
              ),
              "outstandingAmount" -> 1000.52,
              "periodKey" -> "#003",
              "chargeReference" -> "XD002750002155"
            ),
            Json.obj(
              "mainType" -> "VAT Officer's Assessment",
              "chargeType" -> OADebitCharge,
              "taxPeriodFrom" -> "2017-12-01",
              "taxPeriodTo" -> "2018-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2017-10-25")
              ),
              "outstandingAmount" -> 1000.53,
              "periodKey" -> "#004",
              "chargeReference" -> "XD002750002155"
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
              "periodKey" -> "#005",
              "chargeReference" -> "XD002750002155"
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
              "periodKey" -> "#006",
              "chargeReference" -> "XD002750002155"
            ),
            Json.obj(
              "mainType" -> "VAT Error Correction",
              "chargeType" -> ErrorCorrectionCreditCharge,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.29,
              "periodKey" -> "#007",
              "chargeReference" -> "XD002750002155"
            ),
            Json.obj(
              "mainType" -> "VAT Error Correction",
              "chargeType" -> ErrorCorrectionDebitCharge,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.30,
              "periodKey" -> "#007",
              "chargeReference" -> "XD002750002155"
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
              "periodKey" -> "#008",
              "chargeReference" -> "XD002750002155"
            ),
            Json.obj(
              "mainType" -> "VAT AA Return Charge",
              "chargeType" -> AAReturnDebitCharge,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.30,
              "periodKey" -> "#009",
              "chargeReference" -> "XD002750002155"
            ),
            Json.obj(
              "mainType" -> "VAT AA Return Charge",
              "chargeType" -> AAReturnCreditCharge,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.30,
              "periodKey" -> "#010",
              "chargeReference" -> "XD002750002155"
            ),
            Json.obj(
              "mainType" -> "VAT Annual Accounting",
              "chargeType" ->  AAMonthlyInstalment,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.30,
              "periodKey" -> "#011",
              "chargeReference" -> "XD002750002155"
            ),
            Json.obj(
              "mainType" -> "VAT Annual Accounting",
              "chargeType" ->  AAQuarterlyInstalments,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.30,
              "periodKey" -> "#012",
              "chargeReference" -> "XD002750002155"
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
              "periodKey" -> "#018",
              "chargeReference" -> "XD002750002155"
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
              ),
              "chargeReference" -> "XD002750002155"
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
              ),
              "chargeReference" -> "XD002750002155"
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
              ),
              "chargeReference" -> "XD002750002155"
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
              ),
              "chargeReference" -> "XD002750002155"
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
              ),
              "chargeReference" -> "XD002750002155"
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
              ),
              "chargeReference" -> "XD002750002155"
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
              ),
              "chargeReference" -> "XD002750002155"
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
              ),
              "chargeReference" -> "XD002750002155"
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
              ),
              "chargeReference" -> "XD002750002155"
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
              ),
              "chargeReference" -> "XD002750002155"
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
              ),
              "chargeReference" -> "XD002750002155"
            ),
            Json.obj(
              "mainType" -> VatOAInaccuraciesFrom2009,
              "chargeType" -> VatOAInaccuraciesFrom2009,
              "periodKey" -> "#020",
              "taxPeriodFrom" -> "2017-03-20",
              "taxPeriodTo" -> "2017-06-21",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2017-09-27",
                  "amount" -> 50.00
                )
              ),
              "chargeReference" -> "XD002750002155"
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
              ),
              "chargeReference" -> "XD002750002155"
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
              ),
              "chargeReference" -> "XD002750002155"
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
              ),
              "chargeReference" -> "XD002750002155"
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
              ),
              "chargeReference" -> "XD002750002155"
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
              ),
              "chargeReference" -> "XD002750002155"
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
              ),
              "chargeReference" -> "XD002750002155"
            ),
            Json.obj(
              "mainType" -> paymentOnAccountInstalmentsMainType,
              "chargeType" -> PaymentOnAccountInstalments,
              "periodKey" -> "#018",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2008-09-27",
                  "amount" -> 50.00
                )
              ),
              "chargeReference" -> "XD002750002155"
            ),
            Json.obj(
              "mainType" -> paymentOnAccountReturnChargeMainType,
              "chargeType" -> PaymentOnAccountReturnDebitCharge,
              "periodKey" -> "#018",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2008-09-27",
                  "amount" -> 50.00
                )
              ),
              "chargeReference" -> "XD002750002155"
            ),
            Json.obj(
              "mainType" -> paymentOnAccountReturnChargeMainType,
              "chargeType" -> PaymentOnAccountReturnCreditCharge,
              "periodKey" -> "#018",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2008-09-27",
                  "amount" -> 50.00
                )
              ),
              "chargeReference" -> "XD002750002155"
            )
          )
        ).toString()
      )

      val expected = Right(Payments(Seq(
        PaymentWithPeriod(
          ReturnDebitCharge,
          periodFrom = LocalDate.parse("2016-12-01"),
          periodTo = LocalDate.parse("2017-01-01"),
          due = LocalDate.parse("2017-10-25"),
          outstandingAmount = BigDecimal(1000.50),
          periodKey = "#001",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          ReturnCreditCharge,
          periodFrom = LocalDate.parse("2017-12-01"),
          periodTo = LocalDate.parse("2018-01-01"),
          due = LocalDate.parse("2018-10-25"),
          outstandingAmount = BigDecimal(1000.51),
          periodKey = "#002",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          OACreditCharge,
          periodFrom = LocalDate.parse("2017-12-01"),
          periodTo = LocalDate.parse("2018-01-01"),
          due = LocalDate.parse("2017-10-25"),
          outstandingAmount = BigDecimal(1000.52),
          periodKey = "#003",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          OADebitCharge,
          periodFrom = LocalDate.parse("2017-12-01"),
          periodTo = LocalDate.parse("2018-01-01"),
          due = LocalDate.parse("2017-10-25"),
          outstandingAmount = BigDecimal(1000.53),
          periodKey = "#004",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          CentralAssessmentCharge,
          periodFrom = LocalDate.parse("2016-12-01"),
          periodTo = LocalDate.parse("2017-01-01"),
          due = LocalDate.parse("2016-10-25"),
          outstandingAmount = BigDecimal(1000.25),
          periodKey = "#005",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          DefaultSurcharge,
          periodFrom = LocalDate.parse("2015-12-01"),
          periodTo = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.27),
          periodKey = "#006",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          ErrorCorrectionCreditCharge,
          periodFrom = LocalDate.parse("2015-12-01"),
          periodTo = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.29),
          periodKey = "#007",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          ErrorCorrectionDebitCharge,
          periodFrom = LocalDate.parse("2015-12-01"),
          periodTo = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = "#007",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          AAInterestCharge,
          periodFrom = LocalDate.parse("2015-12-01"),
          periodTo = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = "#008",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          AAReturnDebitCharge,
          periodFrom = LocalDate.parse("2015-12-01"),
          periodTo = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = "#009",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          AAReturnCreditCharge,
          periodFrom = LocalDate.parse("2015-12-01"),
          periodTo = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = "#010",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          AAMonthlyInstalment,
          periodFrom = LocalDate.parse("2015-12-01"),
          periodTo = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = "#011",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          AAQuarterlyInstalments,
          periodFrom = LocalDate.parse("2015-12-01"),
          periodTo = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = "#012",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          OADefaultInterestCharge,
          periodFrom = LocalDate.parse("2015-12-01"),
          periodTo = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = "#018",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          AACharge,
          periodFrom = LocalDate.parse("2016-03-20"),
          periodTo = LocalDate.parse("2016-06-21"),
          due = LocalDate.parse("2016-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#009",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          OAFurtherInterestCharge,
          periodFrom = LocalDate.parse("2016-03-20"),
          periodTo = LocalDate.parse("2016-06-21"),
          due = LocalDate.parse("2016-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#010",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          BnpRegPre2010Charge,
          periodFrom = LocalDate.parse("2015-03-20"),
          periodTo = LocalDate.parse("2015-06-21"),
          due = LocalDate.parse("2015-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#019",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          BnpRegPost2010Charge,
          periodFrom = LocalDate.parse("2015-03-20"),
          periodTo = LocalDate.parse("2015-06-21"),
          due = LocalDate.parse("2015-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#011",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          FtnMatPre2010Charge,
          periodFrom = LocalDate.parse("2014-03-20"),
          periodTo = LocalDate.parse("2014-06-21"),
          due = LocalDate.parse("2014-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#012",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          FtnMatPost2010Charge,
          periodFrom = LocalDate.parse("2013-03-20"),
          periodTo = LocalDate.parse("2013-06-21"),
          due = LocalDate.parse("2013-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#013",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          MiscPenaltyCharge,
          periodFrom = LocalDate.parse("2012-03-20"),
          periodTo = LocalDate.parse("2012-06-21"),
          due = LocalDate.parse("2012-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#014",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          FtnEachPartnerCharge,
          periodFrom = LocalDate.parse("2011-03-20"),
          periodTo = LocalDate.parse("2011-06-21"),
          due = LocalDate.parse("2011-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#015",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          MpPre2009Charge,
          periodFrom = LocalDate.parse("2010-03-20"),
          periodTo = LocalDate.parse("2010-06-21"),
          due = LocalDate.parse("2010-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#016",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          MpRepeatedPre2009Charge,
          periodFrom = LocalDate.parse("2009-03-20"),
          periodTo = LocalDate.parse("2009-06-21"),
          due = LocalDate.parse("2009-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#017",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        PaymentWithPeriod(
          CivilEvasionPenaltyCharge,
          periodFrom = LocalDate.parse("2008-03-20"),
          periodTo = LocalDate.parse("2008-06-21"),
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = "#018",
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        Payment(
          VatOAInaccuraciesFrom2009,
          periodFrom = LocalDate.parse("2017-03-20"),
          periodTo = LocalDate.parse("2017-06-21"),
          due = LocalDate.parse("2017-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#020"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        Payment(
          InaccuraciesAssessmentsPenCharge,
          periodFrom = LocalDate.parse("2008-03-20"),
          periodTo = LocalDate.parse("2008-06-21"),
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        Payment(
          InaccuraciesReturnReplacedCharge,
          periodFrom = LocalDate.parse("2008-03-20"),
          periodTo = LocalDate.parse("2008-06-21"),
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        Payment(
          CarterPenaltyCharge,
          periodFrom = LocalDate.parse("2008-03-20"),
          periodTo = LocalDate.parse("2008-06-21"),
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        Payment(
          WrongDoingPenaltyCharge,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        Payment(
          FailureToSubmitRCSLCharge,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        Payment(
          FailureToNotifyRCSLCharge,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        Payment(
          PaymentOnAccountInstalments,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        Payment(
          PaymentOnAccountReturnDebitCharge,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        ),
        Payment(
          PaymentOnAccountReturnCreditCharge,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false
        )
      )))

      val result = PaymentsReads.read("", "", httpResponse)

      "return a Payments instance" in {
        result shouldBe expected
      }
    }

    "the http response is 200 OK and there are no valid charge types" should {
      val httpResponse = HttpResponse(Status.OK,
        Json.obj(
          "financialTransactions" -> Json.arr(
            Json.obj(
              "mainType" -> "VAT Return Charge",
              "chargeType" -> "Other Charge Type",
              "outstandingAmount" -> 99
            )
          )
        ).toString()
      )

      val expected = Right(Payments(Seq.empty))

      val result = PaymentsReads.read("", "", httpResponse)

      "return an empty Payments instance" in {
        result shouldBe expected
      }
    }

    "the http response status is 404 NOT_FOUND" should {

      val httpResponse = HttpResponse(Status.NOT_FOUND, "")

      val expected = Right(Payments(Seq.empty))

      val result = PaymentsReads.read("", "", httpResponse)

      "return an empty Payments object" in {
        result shouldBe expected
      }
    }

    "the http response status is 400 BAD_REQUEST (single error)" should {

      val httpResponse = HttpResponse(Status.BAD_REQUEST,
        Json.obj(
          "code" -> "VRN_INVALID",
          "reason" -> "Fail!"
        ).toString()
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
        Json.obj(
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
        ).toString()
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
        Json.obj(
          "foo" -> "INVALID",
          "bar" -> "Fail!"
        ).toString()
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

      val httpResponse = HttpResponse(Status.GATEWAY_TIMEOUT, body.toString())
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

      val httpResponse = HttpResponse(Status.CONFLICT, body.toString())
      val expected = Left(UnexpectedStatusError("409", httpResponse.body))
      val result = PaymentsReads.read("", "", httpResponse)

      "return an UnexpectedStatusError" in {
        result shouldBe expected
      }
    }
  }
}