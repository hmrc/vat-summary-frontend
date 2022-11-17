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

package connectors.httpParsers

import java.time.LocalDate

import connectors.httpParsers.PaymentsHttpParser.PaymentsReads
import models.errors._
import models.payments._
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse
import org.scalatest.matchers.should.Matchers

class PaymentsHttpParserSpec extends AnyWordSpecLike with Matchers {

  val paymentOnAccountReturnChargeMainType: String = "VAT POA Return Charge"
  val paymentOnAccountInstalmentsMainType: String = "VAT POA Instalments"

  "PaymentsReads" when {

    "the http response status is 200 OK and there are valid charge types" should {

      val httpResponse = HttpResponse(Status.OK,
        Json.obj(
          "financialTransactions" -> Json.arr(
            Json.obj(
              "mainType" -> "VAT Return Charge",
              "chargeType" -> ReturnDebitCharge.value,
              "taxPeriodFrom" -> "2016-12-01",
              "taxPeriodTo" -> "2017-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2017-10-25")
              ),
              "outstandingAmount" -> 1000.50,
              "periodKey" -> "#001",
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 3,
              "penaltyType" -> "LPP1",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> "VAT Return Charge",
              "chargeType" -> ReturnCreditCharge.value,
              "taxPeriodFrom" -> "2017-12-01",
              "taxPeriodTo" -> "2018-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2018-10-25")
              ),
              "outstandingAmount" -> 1000.51,
              "periodKey" -> "#002",
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> "VAT Officer's Assessment",
              "chargeType" -> OACreditCharge.value,
              "taxPeriodFrom" -> "2017-12-01",
              "taxPeriodTo" -> "2018-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2017-10-25")
              ),
              "outstandingAmount" -> 1000.52,
              "periodKey" -> "#003",
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> "VAT Officer's Assessment",
              "chargeType" -> OADebitCharge.value,
              "taxPeriodFrom" -> "2017-12-01",
              "taxPeriodTo" -> "2018-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2017-10-25")
              ),
              "outstandingAmount" -> 1000.53,
              "periodKey" -> "#004",
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> CentralAssessmentCharge.value,
              "chargeType" -> CentralAssessmentCharge.value,
              "taxPeriodFrom" -> "2016-12-01",
              "taxPeriodTo" -> "2017-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2016-10-25")
              ),
              "outstandingAmount" -> 1000.25,
              "periodKey" -> "#005",
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> DebitDefaultSurcharge.value,
              "chargeType" -> DebitDefaultSurcharge.value,
              "taxPeriodFrom" -> "2014-12-01",
              "taxPeriodTo" -> "2015-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.27,
              "periodKey" -> "#006",
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> CreditDefaultSurcharge.value,
              "chargeType" -> CreditDefaultSurcharge.value,
              "taxPeriodFrom" -> "2014-12-01",
              "taxPeriodTo" -> "2015-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> -1000.27,
              "periodKey" -> "#006",
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> "VAT Error Correction",
              "chargeType" -> ErrorCorrectionCreditCharge.value,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.29,
              "periodKey" -> "#007",
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> "VAT Error Correction",
              "chargeType" -> ErrorCorrectionDebitCharge.value,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.30,
              "periodKey" -> "#007",
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> AAInterestCharge.value,
              "chargeType" -> AAInterestCharge.value,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.30,
              "periodKey" -> "#008",
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> "VAT AA Return Charge",
              "chargeType" -> AAReturnDebitCharge.value,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.30,
              "periodKey" -> "#009",
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> "VAT AA Return Charge",
              "chargeType" -> AAReturnCreditCharge.value,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.30,
              "periodKey" -> "#010",
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> "VAT Annual Accounting",
              "chargeType" ->  AAMonthlyInstalment.value,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.30,
              "periodKey" -> "#011",
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> "VAT Annual Accounting",
              "chargeType" ->  AAQuarterlyInstalments.value,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.30,
              "periodKey" -> "#012",
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> OADefaultInterestCharge.value,
              "chargeType" -> OADefaultInterestCharge.value,
              "taxPeriodFrom" -> "2015-12-01",
              "taxPeriodTo" -> "2014-01-01",
              "items" -> Json.arr(
                Json.obj("dueDate" -> "2015-10-25")
              ),
              "outstandingAmount" -> 1000.30,
              "periodKey" -> "#018",
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> AACharge.value,
              "chargeType" -> AACharge.value,
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
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> OAFurtherInterestCharge.value,
              "chargeType" -> OAFurtherInterestCharge.value,
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
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> BnpRegPre2010Charge.value,
              "chargeType" -> BnpRegPre2010Charge.value,
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
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> BnpRegPost2010Charge.value,
              "chargeType" -> BnpRegPost2010Charge.value,
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
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> "VAT FTN PRE 2010",
              "chargeType" -> FtnMatPre2010Charge.value,
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
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> "VAT FTN POST 2010",
              "chargeType" -> FtnMatPost2010Charge.value,
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
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> MiscPenaltyCharge.value,
              "chargeType" -> MiscPenaltyCharge.value,
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
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> FtnEachPartnerCharge.value,
              "chargeType" -> FtnEachPartnerCharge.value,
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
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> MpPre2009Charge.value,
              "chargeType" -> MpPre2009Charge.value,
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
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> MpRepeatedPre2009Charge.value,
              "chargeType" -> MpRepeatedPre2009Charge.value,
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
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> CivilEvasionPenaltyCharge.value,
              "chargeType" -> CivilEvasionPenaltyCharge.value,
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
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> VatOAInaccuraciesFrom2009.value,
              "chargeType" -> VatOAInaccuraciesFrom2009.value,
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
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> InaccuraciesAssessmentsPenCharge.value,
              "chargeType" -> InaccuraciesAssessmentsPenCharge.value,
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
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> InaccuraciesReturnReplacedCharge.value,
              "chargeType" -> InaccuraciesReturnReplacedCharge.value,
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
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> CarterPenaltyCharge.value,
              "chargeType" -> CarterPenaltyCharge.value,
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
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> WrongDoingPenaltyCharge.value,
              "chargeType" -> WrongDoingPenaltyCharge.value,
              "periodKey" -> "#018",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2008-09-27",
                  "amount" -> 50.00
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> FailureToSubmitRCSLCharge.value,
              "chargeType" -> FailureToSubmitRCSLCharge.value,
              "periodKey" -> "#018",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2008-09-27",
                  "amount" -> 50.00
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> FailureToNotifyRCSLCharge.value,
              "chargeType" -> FailureToNotifyRCSLCharge.value,
              "periodKey" -> "#018",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2008-09-27",
                  "amount" -> 50.00
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> paymentOnAccountInstalmentsMainType,
              "chargeType" -> PaymentOnAccountInstalments.value,
              "periodKey" -> "#018",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2008-09-27",
                  "amount" -> 50.00
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatReturnPOALPI.value,
              "periodKey" -> "#018",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2008-09-27",
                  "amount" -> 50.00
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatPOAReturn1stLPP.value,
              "periodKey" -> "#018",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2008-09-27",
                  "amount" -> 50.00
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatPOAReturn2ndLPP.value,
              "periodKey" -> "#018",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2008-09-27",
                  "amount" -> 50.00
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatReturnPOA1stLPPLPI.value,
              "periodKey" -> "#018",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2008-09-27",
                  "amount" -> 50.00
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatReturnPOA2ndLPPLPI.value,
              "periodKey" -> "#018",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2008-09-27",
                  "amount" -> 50.00
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> paymentOnAccountReturnChargeMainType,
              "chargeType" -> PaymentOnAccountReturnDebitCharge.value,
              "periodKey" -> "#018",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2008-09-27",
                  "amount" -> 50.00
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> paymentOnAccountReturnChargeMainType,
              "chargeType" -> PaymentOnAccountReturnCreditCharge.value,
              "periodKey" -> "#018",
              "outstandingAmount" -> 50.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2008-09-27",
                  "amount" -> 50.00
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> VatUnrepayableOverpayment.value,
              "chargeType" -> VatUnrepayableOverpayment.value,
              "periodKey" -> "18AA",
              "outstandingAmount" -> 200.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2018-02-01",
                  "amount" -> 200.00
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "mainType" -> VatReturn1stLPP.value,
              "chargeType" -> VatReturn1stLPP.value,
              "periodKey" -> "18AA",
              "outstandingAmount" -> 200.00,
              "items" -> Json.arr(
                Json.obj(
                  "subItem" -> "000",
                  "dueDate" -> "2018-02-01",
                  "amount" -> 200.00
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatReturnLPI.value,
              "periodKey" -> "18AA",
              "outstandingAmount" -> 202.10,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.10
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatPALPICharge.value,
              "periodKey" -> "18AA",
              "outstandingAmount" -> 202.10,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.10
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatReturn1stLPPLPI.value,
              "periodKey" -> "18AB",
              "outstandingAmount" -> 202.20,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.20
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000

            ),
            Json.obj(
              "chargeType" -> VatReturn2ndLPPLPI.value,
              "periodKey" -> "18AC",
              "outstandingAmount" -> 202.30,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.30
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatCentralAssessmentLPI.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatCA1stLPPLPI.value,
              "periodKey" -> "18AA",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatCA2ndLPPLPI.value,
              "periodKey" -> "18AA",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatOfficersAssessmentLPI.value,
              "periodKey" -> "18AB",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatOA1stLPPLPI.value,
              "periodKey" -> "18AC",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatOA2ndLPPLPI.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatPA1stLPPLPI.value,
              "periodKey" -> "18AC",
              "outstandingAmount" -> 202.50,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.50
                )
              ),
              "chargeReference" -> "XD002750002156",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatPA2ndLPPLPI.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.50,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.50
                )
              ),
              "chargeReference" -> "XD002750002157",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatPA1stLPP.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatPA2ndLPP.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatAA1stLPP.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatAA2ndLPP.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatAA1stLPPLPI.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatAA2ndLPPLPI.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatAdditionalAssessmentLPI.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatLateSubmissionPen.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatLspInterest.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatReturnAA1stLPPLPI.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 208.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 208.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatReturnAA2ndLPPLPI.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 208.41,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 208.41
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatManualLPP.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 208.42,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 208.42
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatManualLPPLPI.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 208.43,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 208.43
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatPOAInstalmentLPI.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002156",
              "accruedInterest" -> 0,
              "originalAmount" -> 202.40
            ),
            Json.obj(
              "chargeType" -> VatAAReturnChargeLPI.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002157",
              "accruedInterest" -> 0,
              "originalAmount" -> 202.40
            ),
            Json.obj(
              "chargeType" -> VatAAQuarterlyInstalLPI.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatAAMonthlyInstalLPI.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatAAReturnCharge1stLPP.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatAAReturnCharge2ndLPP.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterestAmount" -> 2,
              "accruedPenaltyAmount" -> 100.00,
              "penaltyType" -> "LPP",
              "originalAmount" -> 10000
            ),
            Json.obj(
              "chargeType" -> VatMigratedLiability.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterest" -> 0,
              "originalAmount" -> 202.40
            ),
            Json.obj(
              "chargeType" -> VatMigratedCredit.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> -202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> -202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterest" -> 0,
              "originalAmount" -> -202.40
            ),
            Json.obj(
              "chargeType" -> VatReturn2ndLPP.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> -202.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> -202.40
                )
              ),
              "chargeReference" -> "XD002750002155",
              "accruedInterest" -> 0,
              "originalAmount" -> -202.40
            ),
            Json.obj(
              "chargeType" -> VatErrorCorrectionLPI.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 502.40,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 502.40
                )
              ),
              "chargeReference" -> "XD002750002140",
              "accruedInterest" -> 0,
              "originalAmount" -> 502.40
            ),
            Json.obj(
              "chargeType" -> VatErrorCorrection1stLPP.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 502.41,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 502.41
                )
              ),
              "chargeReference" -> "XD002750002141",
              "accruedInterest" -> 0,
            "penaltyType" -> "LPP",
              "originalAmount" -> 502.41
            ),
            Json.obj(
              "chargeType" -> VatErrorCorrection2ndLPP.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 502.42,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 502.42
                )
              ),
              "chargeReference" -> "XD002750002142",
              "accruedInterest" -> 0,
            "penaltyType" -> "LPP",
              "originalAmount" -> 502.42
            ),
            Json.obj(
              "chargeType" -> VatErrorCorrection1stLPPLPI.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 502.43,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 502.43
                )
              ),
              "chargeReference" -> "XD002750002143",
              "accruedInterest" -> 0,
            "penaltyType" -> "LPP",
              "originalAmount" -> 502.43
            ),
            Json.obj(
              "chargeType" -> VatErrorCorrection2ndLPPLPI.value,
              "periodKey" -> "18AD",
              "outstandingAmount" -> 502.44,
              "items" -> Json.arr(
                Json.obj(
                  "dueDate" -> "2018-02-01",
                  "amount" -> 502.44
                )
              ),
              "chargeReference" -> "XD002750002144",
              "accruedInterest" -> 0,
              "penaltyType" -> "LPP",
              "originalAmount" -> 502.44
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
          periodKey = Some("#001"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(3),
          penaltyType = Some("LPP1"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          ReturnCreditCharge,
          periodFrom = LocalDate.parse("2017-12-01"),
          periodTo = LocalDate.parse("2018-01-01"),
          due = LocalDate.parse("2018-10-25"),
          outstandingAmount = BigDecimal(1000.51),
          periodKey = Some("#002"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          OACreditCharge,
          periodFrom = LocalDate.parse("2017-12-01"),
          periodTo = LocalDate.parse("2018-01-01"),
          due = LocalDate.parse("2017-10-25"),
          outstandingAmount = BigDecimal(1000.52),
          periodKey = Some("#003"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          OADebitCharge,
          periodFrom = LocalDate.parse("2017-12-01"),
          periodTo = LocalDate.parse("2018-01-01"),
          due = LocalDate.parse("2017-10-25"),
          outstandingAmount = BigDecimal(1000.53),
          periodKey = Some("#004"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          CentralAssessmentCharge,
          periodFrom = LocalDate.parse("2016-12-01"),
          periodTo = LocalDate.parse("2017-01-01"),
          due = LocalDate.parse("2016-10-25"),
          outstandingAmount = BigDecimal(1000.25),
          periodKey = Some("#005"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          DebitDefaultSurcharge,
          periodFrom = LocalDate.parse("2014-12-01"),
          periodTo = LocalDate.parse("2015-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.27),
          periodKey = Some("#006"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          CreditDefaultSurcharge,
          periodFrom = LocalDate.parse("2014-12-01"),
          periodTo = LocalDate.parse("2015-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(-1000.27),
          periodKey = Some("#006"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          ErrorCorrectionCreditCharge,
          periodFrom = LocalDate.parse("2015-12-01"),
          periodTo = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.29),
          periodKey = Some("#007"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          ErrorCorrectionDebitCharge,
          periodFrom = LocalDate.parse("2015-12-01"),
          periodTo = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = Some("#007"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          AAInterestCharge,
          periodFrom = LocalDate.parse("2015-12-01"),
          periodTo = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = Some("#008"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          AAReturnDebitCharge,
          periodFrom = LocalDate.parse("2015-12-01"),
          periodTo = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = Some("#009"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          AAReturnCreditCharge,
          periodFrom = LocalDate.parse("2015-12-01"),
          periodTo = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = Some("#010"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          AAMonthlyInstalment,
          periodFrom = LocalDate.parse("2015-12-01"),
          periodTo = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = Some("#011"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          AAQuarterlyInstalments,
          periodFrom = LocalDate.parse("2015-12-01"),
          periodTo = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = Some("#012"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          OADefaultInterestCharge,
          periodFrom = LocalDate.parse("2015-12-01"),
          periodTo = LocalDate.parse("2014-01-01"),
          due = LocalDate.parse("2015-10-25"),
          outstandingAmount = BigDecimal(1000.30),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          AACharge,
          periodFrom = LocalDate.parse("2016-03-20"),
          periodTo = LocalDate.parse("2016-06-21"),
          due = LocalDate.parse("2016-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#009"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          OAFurtherInterestCharge,
          periodFrom = LocalDate.parse("2016-03-20"),
          periodTo = LocalDate.parse("2016-06-21"),
          due = LocalDate.parse("2016-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#010"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          BnpRegPre2010Charge,
          periodFrom = LocalDate.parse("2015-03-20"),
          periodTo = LocalDate.parse("2015-06-21"),
          due = LocalDate.parse("2015-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#019"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          BnpRegPost2010Charge,
          periodFrom = LocalDate.parse("2015-03-20"),
          periodTo = LocalDate.parse("2015-06-21"),
          due = LocalDate.parse("2015-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#011"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          FtnMatPre2010Charge,
          periodFrom = LocalDate.parse("2014-03-20"),
          periodTo = LocalDate.parse("2014-06-21"),
          due = LocalDate.parse("2014-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#012"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          FtnMatPost2010Charge,
          periodFrom = LocalDate.parse("2013-03-20"),
          periodTo = LocalDate.parse("2013-06-21"),
          due = LocalDate.parse("2013-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#013"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          MiscPenaltyCharge,
          periodFrom = LocalDate.parse("2012-03-20"),
          periodTo = LocalDate.parse("2012-06-21"),
          due = LocalDate.parse("2012-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#014"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          FtnEachPartnerCharge,
          periodFrom = LocalDate.parse("2011-03-20"),
          periodTo = LocalDate.parse("2011-06-21"),
          due = LocalDate.parse("2011-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#015"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          MpPre2009Charge,
          periodFrom = LocalDate.parse("2010-03-20"),
          periodTo = LocalDate.parse("2010-06-21"),
          due = LocalDate.parse("2010-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#016"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          MpRepeatedPre2009Charge,
          periodFrom = LocalDate.parse("2009-03-20"),
          periodTo = LocalDate.parse("2009-06-21"),
          due = LocalDate.parse("2009-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#017"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentWithPeriod(
          CivilEvasionPenaltyCharge,
          periodFrom = LocalDate.parse("2008-03-20"),
          periodTo = LocalDate.parse("2008-06-21"),
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        Payment(
          VatOAInaccuraciesFrom2009,
          periodFrom = Some(LocalDate.parse("2017-03-20")),
          periodTo = Some(LocalDate.parse("2017-06-21")),
          due = LocalDate.parse("2017-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#020"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        Payment(
          InaccuraciesAssessmentsPenCharge,
          periodFrom = Some(LocalDate.parse("2008-03-20")),
          periodTo = Some(LocalDate.parse("2008-06-21")),
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        Payment(
          InaccuraciesReturnReplacedCharge,
          periodFrom = Some(LocalDate.parse("2008-03-20")),
          periodTo = Some(LocalDate.parse("2008-06-21")),
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        Payment(
          CarterPenaltyCharge,
          periodFrom = Some(LocalDate.parse("2008-03-20")),
          periodTo = Some(LocalDate.parse("2008-06-21")),
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          WrongDoingPenaltyCharge,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          FailureToSubmitRCSLCharge,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          FailureToNotifyRCSLCharge,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          PaymentOnAccountInstalments,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatReturnPOALPI,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatPOAReturn1stLPP,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatPOAReturn2ndLPP,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatReturnPOA1stLPPLPI,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatReturnPOA2ndLPPLPI,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          PaymentOnAccountReturnDebitCharge,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          PaymentOnAccountReturnCreditCharge,
          due = LocalDate.parse("2008-09-27"),
          outstandingAmount = BigDecimal(50.00),
          periodKey = Some("#018"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatUnrepayableOverpayment,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(200.00),
          periodKey = Some("18AA"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatReturn1stLPP,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(200.00),
          periodKey = Some("18AA"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatReturnLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.10),
          periodKey = Some("18AA"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatPALPICharge,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.10),
          periodKey = Some("18AA"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatReturn1stLPPLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.20),
          periodKey = Some("18AB"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatReturn2ndLPPLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.30),
          periodKey = Some("18AC"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatCentralAssessmentLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatCA1stLPPLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AA"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.0)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatCA2ndLPPLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AA"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.0)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatOfficersAssessmentLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AB"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatOA1stLPPLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AC"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatOA2ndLPPLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatPA1stLPPLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.50),
          periodKey = Some("18AC"),
          chargeReference = Some("XD002750002156"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatPA2ndLPPLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.50),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002157"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatPA1stLPP,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatPA2ndLPP,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatAA1stLPP,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatAA2ndLPP,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatAA1stLPPLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatAA2ndLPPLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatAdditionalAssessmentLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatLateSubmissionPen,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatLspInterest,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatReturnAA1stLPPLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(208.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatReturnAA2ndLPPLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(208.41),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatManualLPP,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(208.42),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatManualLPPLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(208.43),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatPOAInstalmentLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002156"),
          ddCollectionInProgress = false,
          accruedInterestAmount = None,
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(202.40),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatAAReturnChargeLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002157"),
          ddCollectionInProgress = false,
          accruedInterestAmount = None,
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(202.40),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatAAQuarterlyInstalLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatAAMonthlyInstalLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatAAReturnCharge1stLPP,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatAAReturnCharge2ndLPP,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = Some(BigDecimal(2)),
          interestRate = None,
          accruedPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(10000),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatMigratedLiability,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = None,
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(202.40),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatMigratedCredit,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(-202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = None,
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(-202.40),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatReturn2ndLPP,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(-202.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruedInterestAmount = None,
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(-202.40),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatErrorCorrectionLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(502.40),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002140"),
          ddCollectionInProgress = false,
          accruedInterestAmount = None,
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = None,
          originalAmount = BigDecimal(502.40),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatErrorCorrection1stLPP,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(502.41),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002141"),
          ddCollectionInProgress = false,
          accruedInterestAmount = None,
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(502.41),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatErrorCorrection2ndLPP,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(502.42),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002142"),
          ddCollectionInProgress = false,
          accruedInterestAmount = None,
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(502.42),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatErrorCorrection1stLPPLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(502.43),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002143"),
          ddCollectionInProgress = false,
          accruedInterestAmount = None,
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(502.43),
          clearedAmount = None
        ),
        PaymentNoPeriod(
          VatErrorCorrection2ndLPPLPI,
          due = LocalDate.parse("2018-02-01"),
          outstandingAmount = BigDecimal(502.44),
          periodKey = Some("18AD"),
          chargeReference = Some("XD002750002144"),
          ddCollectionInProgress = false,
          accruedInterestAmount = None,
          interestRate = None,
          accruedPenaltyAmount = None,
          penaltyType = Some("LPP"),
          originalAmount = BigDecimal(502.44),
          clearedAmount = None
        )
      )))

      val result = PaymentsReads.read("", "", httpResponse)

      "return a Payments instance" in {
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