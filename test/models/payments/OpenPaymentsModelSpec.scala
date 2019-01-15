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

package models.payments

import java.time.LocalDate

import common.FinancialTransactionsConstants._
import play.api.libs.json.Json
import views.ViewBaseSpec

class OpenPaymentsModelSpec extends ViewBaseSpec {

  "An OpenPaymentsModel with a start and end date" when {

    s"charge type is $OADebitCharge" should {

      val testModel = OpenPaymentsModel(
        chargeType = OADebitCharge,
        amount = 300.00,
        due = LocalDate.parse("2003-04-05"),
        start = LocalDate.parse("2001-01-01"),
        end = LocalDate.parse("2001-03-31"),
        periodKey = "#003",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> OADebitCharge.value,
        "amount" -> 300.00,
        "due" -> "2003-04-05",
        "start" -> "2001-01-01",
        "end" -> "2001-03-31",
        "periodKey" -> "#003",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "a VAT officer's investigation showed you underpaid by this amount"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $OADefaultInterestCharge" should{

      val testModel = OpenPaymentsModel(
        chargeType = OADefaultInterestCharge,
        amount = 400.00,
        due = LocalDate.parse("2004-04-05"),
        start = LocalDate.parse("2001-01-01"),
        end = LocalDate.parse("2001-03-31"),
        periodKey = "#004",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> OADefaultInterestCharge.value,
        "amount" -> 400.00,
        "due" -> "2004-04-05",
        "start" -> "2001-01-01",
        "end" -> "2001-03-31",
        "periodKey" -> "#004",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "interest charged on the officer's assessment"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $ReturnDebitCharge" should {

      val testModel = OpenPaymentsModel(
        chargeType = ReturnDebitCharge,
        amount = 2000000000.01,
        due = LocalDate.parse("2001-04-08"),
        start = LocalDate.parse("2001-01-01"),
        end = LocalDate.parse("2001-03-31"),
        periodKey = "#001",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> ReturnDebitCharge.value,
        "amount" -> 2000000000.01,
        "due" -> "2001-04-08",
        "start" -> "2001-01-01",
        "end" -> "2001-03-31",
        "periodKey" -> "#001",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "for the period 1 January to 31 March 2001"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $CentralAssessmentCharge" should {

      val testModel = OpenPaymentsModel(
        chargeType = CentralAssessmentCharge,
        amount = 1600,
        due = LocalDate.parse("2016-04-08"),
        start = LocalDate.parse("2016-01-01"),
        end = LocalDate.parse("2016-03-31"),
        periodKey = "#016",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> CentralAssessmentCharge.value,
        "amount" -> 1600,
        "due" -> "2016-04-08",
        "start" -> "2016-01-01",
        "end" -> "2016-03-31",
        "periodKey" -> "#016",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "this is our estimate for 1 January to 31 March 2016, submit your overdue return to update this amount"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }


    }

    s"charge type is $AAInterestCharge" should {

      val testModel = OpenPaymentsModel(
        chargeType = AAInterestCharge,
        amount = 300.00,
        due = LocalDate.parse("2003-04-05"),
        start = LocalDate.parse("2003-01-01"),
        end = LocalDate.parse("2003-03-31"),
        periodKey = "#003",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> AAInterestCharge.value,
        "amount" -> 300.00,
        "due" -> "2003-04-05",
        "start" -> "2003-01-01",
        "end" -> "2003-03-31",
        "periodKey" -> "#003",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "interest charged on additional tax assessed for the period 1 January to 31 March 2003"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $ErrorCorrectionDebitCharge" should {

      val testModel = OpenPaymentsModel(
        chargeType = ErrorCorrectionDebitCharge,
        amount = 1700.00,
        due = LocalDate.parse("2017-04-05"),
        start = LocalDate.parse("2017-01-01"),
        end = LocalDate.parse("2017-03-31"),
        periodKey = "#017",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> ErrorCorrectionDebitCharge.value,
        "amount" -> 1700.00,
        "due" -> "2017-04-05",
        "start" -> "2017-01-01",
        "end" -> "2017-03-31",
        "periodKey" -> "#017",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "this is the correction you made to your 1 January to 31 March 2017 return"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $AAFurtherInterestCharge" should {

      val testModel = OpenPaymentsModel(
        chargeType = AAFurtherInterestCharge,
        amount = 400.00,
        due = LocalDate.parse("2004-04-05"),
        start = LocalDate.parse("2004-01-01"),
        end = LocalDate.parse("2004-03-31"),
        periodKey = "#004",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> AAFurtherInterestCharge.value,
        "amount" -> 400.00,
        "due" -> "2004-04-05",
        "start" -> "2004-01-01",
        "end" -> "2004-03-31",
        "periodKey" -> "#004",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "further interest charged on additional tax assessed for the period 1 January to 31 March 2004"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $AACharge" should {

      val testModel = OpenPaymentsModel(
        chargeType = AACharge,
        amount = 500.00,
        due = LocalDate.parse("2005-04-05"),
        start = LocalDate.parse("2005-01-01"),
        end = LocalDate.parse("2005-03-31"),
        periodKey = "#005",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> AACharge.value,
        "amount" -> 500.00,
        "due" -> "2005-04-05",
        "start" -> "2005-01-01",
        "end" -> "2005-03-31",
        "periodKey" -> "#005",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "additional assessment based on further information for the period 1 January to 31 March 2005"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $OAFurtherInterestCharge" should {

      val testModel = OpenPaymentsModel(
        chargeType = OAFurtherInterestCharge,
        amount = 600.00,
        due = LocalDate.parse("2006-04-05"),
        start = LocalDate.parse("2006-01-01"),
        end = LocalDate.parse("2006-03-31"),
        periodKey = "#006",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> OAFurtherInterestCharge.value,
        "amount" -> 600.00,
        "due" -> "2006-04-05",
        "start" -> "2006-01-01",
        "end" -> "2006-03-31",
        "periodKey" -> "#006",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "further interest charged on the officer's assessment"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $BnpRegPre2010Charge" should {

      val testModel = OpenPaymentsModel(
        chargeType = BnpRegPre2010Charge,
        amount = 700.00,
        due = LocalDate.parse("2007-04-05"),
        start = LocalDate.parse("2007-01-01"),
        end = LocalDate.parse("2007-03-31"),
        periodKey = "#007",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> BnpRegPre2010Charge.value,
        "amount" -> 700.00,
        "due" -> "2007-04-05",
        "start" -> "2007-01-01",
        "end" -> "2007-03-31",
        "periodKey" -> "#007",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "because you should have been registered for VAT earlier"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $BnpRegPost2010Charge" should {

      val testModel = OpenPaymentsModel(
        chargeType = BnpRegPost2010Charge,
        amount = 800.00,
        due = LocalDate.parse("2008-04-05"),
        start = LocalDate.parse("2008-01-01"),
        end = LocalDate.parse("2008-03-31"),
        periodKey = "#008",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> BnpRegPost2010Charge.value,
        "amount" -> 800.00,
        "due" -> "2008-04-05",
        "start" -> "2008-01-01",
        "end" -> "2008-03-31",
        "periodKey" -> "#008",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "you should have been registered for VAT earlier"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $FtnMatPre2010Charge" should {

      val testModel = OpenPaymentsModel(
        chargeType = FtnMatPre2010Charge,
        amount = 900.00,
        due = LocalDate.parse("2009-04-05"),
        start = LocalDate.parse("2009-01-01"),
        end = LocalDate.parse("2009-03-31"),
        periodKey = "#009",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> FtnMatPre2010Charge.value,
        "amount" -> 900.00,
        "due" -> "2009-04-05",
        "start" -> "2009-01-01",
        "end" -> "2009-03-31",
        "periodKey" -> "#009",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "you did not tell us you are no longer exempt from VAT registration"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $FtnMatPost2010Charge" should {

      val testModel = OpenPaymentsModel(
        chargeType = FtnMatPost2010Charge,
        amount = 1000.00,
        due = LocalDate.parse("2010-04-05"),
        start = LocalDate.parse("2010-01-01"),
        end = LocalDate.parse("2010-03-31"),
        periodKey = "#010",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> FtnMatPost2010Charge.value,
        "amount" -> 1000.00,
        "due" -> "2010-04-05",
        "start" -> "2010-01-01",
        "end" -> "2010-03-31",
        "periodKey" -> "#010",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "you did not tell us you are no longer exempt from VAT registration"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $MiscPenaltyCharge" should {

      val testModel = OpenPaymentsModel(
        chargeType = MiscPenaltyCharge,
        amount = 1100.00,
        due = LocalDate.parse("2011-04-05"),
        start = LocalDate.parse("2011-01-01"),
        end = LocalDate.parse("2011-03-31"),
        periodKey = "#011",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> MiscPenaltyCharge.value,
        "amount" -> 1100.00,
        "due" -> "2011-04-05",
        "start" -> "2011-01-01",
        "end" -> "2011-03-31",
        "periodKey" -> "#011",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "VAT general penalty"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $FtnEachPartnerCharge" should {

      val testModel = OpenPaymentsModel(
        chargeType = FtnEachPartnerCharge,
        amount = 1200.00,
        due = LocalDate.parse("2012-04-05"),
        start = LocalDate.parse("2012-01-01"),
        end = LocalDate.parse("2012-03-31"),
        periodKey = "#012",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> FtnEachPartnerCharge.value,
        "amount" -> 1200.00,
        "due" -> "2012-04-05",
        "start" -> "2012-01-01",
        "end" -> "2012-03-31",
        "periodKey" -> "#012",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "because you did not tell us about all the partners and changes in your partnership"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $MpPre2009Charge" should {

      val testModel = OpenPaymentsModel(
        chargeType = MpPre2009Charge,
        amount = 1300.00,
        due = LocalDate.parse("2013-04-05"),
        start = LocalDate.parse("2013-01-01"),
        end = LocalDate.parse("2013-03-31"),
        periodKey = "#013",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> MpPre2009Charge.value,
        "amount" -> 1300.00,
        "due" -> "2013-04-05",
        "start" -> "2013-01-01",
        "end" -> "2013-03-31",
        "periodKey" -> "#013",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "because you have made an incorrect declaration"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $MpRepeatedPre2009Charge" should {

      val testModel = OpenPaymentsModel(
        chargeType = MpRepeatedPre2009Charge,
        amount = 1400.00,
        due = LocalDate.parse("2014-04-05"),
        start = LocalDate.parse("2014-01-01"),
        end = LocalDate.parse("2014-03-31"),
        periodKey = "#014",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> MpRepeatedPre2009Charge.value,
        "amount" -> 1400.00,
        "due" -> "2014-04-05",
        "start" -> "2014-01-01",
        "end" -> "2014-03-31",
        "periodKey" -> "#014",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "this is because you have repeatedly made incorrect declarations"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $CivilEvasionPenaltyCharge" should {

      val testModel = OpenPaymentsModel(
        chargeType = CivilEvasionPenaltyCharge,
        amount = 1500.00,
        due = LocalDate.parse("2015-04-05"),
        start = LocalDate.parse("2015-01-01"),
        end = LocalDate.parse("2015-03-31"),
        periodKey = "#015",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> CivilEvasionPenaltyCharge.value,
        "amount" -> 1500.00,
        "due" -> "2015-04-05",
        "start" -> "2015-01-01",
        "end" -> "2015-03-31",
        "periodKey" -> "#015",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "because we have identified irregularities involving dishonesty"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }
  }

  "An OpenPaymentsModel without a start and end date" when {

    s"charge type is $OADebitCharge" should {

      val testModel = OpenPaymentsModel(
        chargeType = OADebitCharge,
        amount = 300.00,
        due = LocalDate.parse("2003-04-05"),
        periodKey = "#003",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> OADebitCharge.value,
        "amount" -> 300.00,
        "due" -> "2003-04-05",
        "periodKey" -> "#003",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "a VAT officer's investigation showed you underpaid by this amount"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $OADefaultInterestCharge" should{

      val testModel = OpenPaymentsModel(
        chargeType = OADefaultInterestCharge,
        amount = 400.00,
        due = LocalDate.parse("2004-04-05"),
        periodKey = "#004",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> OADefaultInterestCharge.value,
        "amount" -> 400.00,
        "due" -> "2004-04-05",
        "periodKey" -> "#004",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "interest charged on the officer's assessment"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $OACharge" should{

      val testModel = OpenPaymentsModel(
        chargeType = OACharge,
        amount = 500.00,
        due = LocalDate.parse("2005-04-05"),
        periodKey = "#005",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> OACharge.value,
        "amount" -> 500.00,
        "due" -> "2005-04-05",
        "periodKey" -> "#005",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "a VAT officer's investigation showed you underpaid by this amount"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $BnpRegPost2010Charge" should{

      val testModel = OpenPaymentsModel(
        chargeType = BnpRegPost2010Charge,
        amount = 600.00,
        due = LocalDate.parse("2006-04-05"),
        periodKey = "#006",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> BnpRegPost2010Charge.value,
        "amount" -> 600.00,
        "due" -> "2006-04-05",
        "periodKey" -> "#006",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "you should have been registered for VAT earlier"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $FtnMatPre2010Charge" should{

      val testModel = OpenPaymentsModel(
        chargeType = FtnMatPre2010Charge,
        amount = 700.00,
        due = LocalDate.parse("2007-04-05"),
        periodKey = "#007",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> FtnMatPre2010Charge.value,
        "amount" -> 700.00,
        "due" -> "2007-04-05",
        "periodKey" -> "#007",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "you did not tell us you are no longer exempt from VAT registration"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $FtnMatPost2010Charge" should{

      val testModel = OpenPaymentsModel(
        chargeType = FtnMatPost2010Charge,
        amount = 800.00,
        due = LocalDate.parse("2008-04-05"),
        periodKey = "#008",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> FtnMatPost2010Charge.value,
        "amount" -> 800.00,
        "due" -> "2008-04-05",
        "periodKey" -> "#008",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "you did not tell us you are no longer exempt from VAT registration"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $MiscPenaltyCharge" should{

      val testModel = OpenPaymentsModel(
        chargeType = MiscPenaltyCharge,
        amount = 900.00,
        due = LocalDate.parse("2009-04-05"),
        periodKey = "#009",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> MiscPenaltyCharge.value,
        "amount" -> 900.00,
        "due" -> "2009-04-05",
        "periodKey" -> "#009",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "VAT general penalty"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $FtnEachPartnerCharge" should{

      val testModel = OpenPaymentsModel(
        chargeType = FtnEachPartnerCharge,
        amount = 1000.00,
        due = LocalDate.parse("2010-04-05"),
        periodKey = "#010",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> FtnEachPartnerCharge.value,
        "amount" -> 1000.00,
        "due" -> "2010-04-05",
        "periodKey" -> "#010",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "because you did not tell us about all the partners and changes in your partnership"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $MpPre2009Charge" should{

      val testModel = OpenPaymentsModel(
        chargeType = MpPre2009Charge,
        amount = 1100.00,
        due = LocalDate.parse("2011-04-05"),
        periodKey = "#011",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> MpPre2009Charge.value,
        "amount" -> 1100.00,
        "due" -> "2011-04-05",
        "periodKey" -> "#011",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "because you have made an incorrect declaration"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $MpRepeatedPre2009Charge." should{

      val testModel = OpenPaymentsModel(
        chargeType = MpRepeatedPre2009Charge,
        amount = 1200.00,
        due = LocalDate.parse("2012-04-05"),
        periodKey = "#012",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> MpRepeatedPre2009Charge.value,
        "amount" -> 1200.00,
        "due" -> "2012-04-05",
        "periodKey" -> "#012",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "this is because you have repeatedly made incorrect declarations"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $CivilEvasionPenaltyCharge" should{

      val testModel = OpenPaymentsModel(
        chargeType = CivilEvasionPenaltyCharge,
        amount = 1300.00,
        due = LocalDate.parse("2013-04-05"),
        periodKey = "#013",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> CivilEvasionPenaltyCharge.value,
        "amount" -> 1300.00,
        "due" -> "2013-04-05",
        "periodKey" -> "#013",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "because we have identified irregularities involving dishonesty"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $VatOAInaccuraciesFrom2009" should{

      val testModel = OpenPaymentsModel(
        chargeType = VatOAInaccuraciesFrom2009,
        amount = 50.00,
        due = LocalDate.parse("2017-09-27"),
        start = LocalDate.parse("2017-03-20"),
        end = LocalDate.parse("2017-06-21"),
        periodKey = "#020",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> VatOAInaccuraciesFrom2009.value,
        "amount" -> 50.00,
        "due" -> "2017-09-27",
        "start" -> "2017-03-20",
        "end" -> "2017-06-21",
        "periodKey" -> "#020",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "because you submitted an inaccurate document for the period 20 March to 21 June 2017"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }
  }

  "Apply method" when {

    "given a Payment with a start and end date" should {

      "create an OpenPaymentsModelWithPeriod" in {
        val applyWithPaymentModel = OpenPaymentsModel(
          payment = Payment(
            chargeType = OADebitCharge,
            start = LocalDate.parse("2001-01-01"),
            end = LocalDate.parse("2001-03-31"),
            due = LocalDate.parse("2003-04-05"),
            outstandingAmount = 300.00,
            periodKey = Some("#003")
          ),
          overdue = true
        )

        val regularApply = OpenPaymentsModel(
          chargeType = OADebitCharge,
          amount = 300.00,
          due = LocalDate.parse("2003-04-05"),
          start = LocalDate.parse("2001-01-01"),
          end = LocalDate.parse("2001-03-31"),
          periodKey = "#003",
          overdue = true
        )

        applyWithPaymentModel shouldBe regularApply

      }
    }

    "given a Payment without a start and end date" should {

      "create an OpenPaymentsModelNoPeriod" in {
        val applyWithPaymentModel = OpenPaymentsModel(
          payment = Payment(
            chargeType = OADebitCharge,
            due = LocalDate.parse("2003-04-05"),
            outstandingAmount = 300.00,
            periodKey = Some("#003")
          ),
          overdue = true
        )

        val regularApply = OpenPaymentsModel(
          chargeType = OADebitCharge,
          amount = 300.00,
          due = LocalDate.parse("2003-04-05"),
          periodKey = "#003",
          overdue = true
        )

        applyWithPaymentModel shouldBe regularApply
      }
    }
  }
}
