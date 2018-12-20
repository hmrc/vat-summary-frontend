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

package models.payments

import java.time.LocalDate

import common.FinancialTransactionsConstants._
import play.api.libs.json.Json
import views.ViewBaseSpec

class OpenPaymentsModelSpec extends ViewBaseSpec {

  "An OpenPaymentsModel with a start and end date" when {

    s"charge type is $officerAssessmentDebitCharge" should {

      val testModel = OpenPaymentsModel(
        paymentType = officerAssessmentDebitCharge,
        amount = 300.00,
        due = LocalDate.parse("2003-04-05"),
        start = LocalDate.parse("2001-01-01"),
        end = LocalDate.parse("2001-03-31"),
        periodKey = "#003",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> officerAssessmentDebitCharge,
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

    s"charge type is $officerAssessmentDefaultInterest" should{

      val testModel = OpenPaymentsModel(
        paymentType = officerAssessmentDefaultInterest,
        amount = 400.00,
        due = LocalDate.parse("2004-04-05"),
        start = LocalDate.parse("2001-01-01"),
        end = LocalDate.parse("2001-03-31"),
        periodKey = "#004",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> officerAssessmentDefaultInterest,
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

    s"charge type is $vatReturnDebitCharge" should {

      val testModel = OpenPaymentsModel(
        paymentType = vatReturnDebitCharge,
        amount = 2000000000.01,
        due = LocalDate.parse("2001-04-08"),
        start = LocalDate.parse("2001-01-01"),
        end = LocalDate.parse("2001-03-31"),
        periodKey = "#001",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatReturnDebitCharge,
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

    s"charge type is $vatCentralAssessment" should {

      val testModel = OpenPaymentsModel(
        paymentType = vatCentralAssessment,
        amount = 1600,
        due = LocalDate.parse("2016-04-08"),
        start = LocalDate.parse("2016-01-01"),
        end = LocalDate.parse("2016-03-31"),
        periodKey = "#016",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatCentralAssessment,
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

    s"charge type is $vatAdditionalAssessmentInterest" should {

      val testModel = OpenPaymentsModel(
        paymentType = vatAdditionalAssessmentInterest,
        amount = 300.00,
        due = LocalDate.parse("2003-04-05"),
        start = LocalDate.parse("2003-01-01"),
        end = LocalDate.parse("2003-03-31"),
        periodKey = "#003",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatAdditionalAssessmentInterest,
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

    s"charge type is $errorCorrectionDebitCharge" should {

      val testModel = OpenPaymentsModel(
        paymentType = errorCorrectionDebitCharge,
        amount = 1700.00,
        due = LocalDate.parse("2017-04-05"),
        start = LocalDate.parse("2017-01-01"),
        end = LocalDate.parse("2017-03-31"),
        periodKey = "#017",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> errorCorrectionDebitCharge,
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

    s"charge type is $vatAdditionalAssessmentFurtherInterest" should {

      val testModel = OpenPaymentsModel(
        paymentType = vatAdditionalAssessmentFurtherInterest,
        amount = 400.00,
        due = LocalDate.parse("2004-04-05"),
        start = LocalDate.parse("2004-01-01"),
        end = LocalDate.parse("2004-03-31"),
        periodKey = "#004",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatAdditionalAssessmentFurtherInterest,
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

    s"charge type is $vatAdditionalAssessment" should {

      val testModel = OpenPaymentsModel(
        paymentType = vatAdditionalAssessment,
        amount = 500.00,
        due = LocalDate.parse("2005-04-05"),
        start = LocalDate.parse("2005-01-01"),
        end = LocalDate.parse("2005-03-31"),
        periodKey = "#005",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatAdditionalAssessment,
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

    s"charge type is $vatOfficersAssessment" should {

      val testModel = OpenPaymentsModel(
        paymentType = vatOfficersAssessment,
        amount = 600.00,
        due = LocalDate.parse("2006-04-05"),
        start = LocalDate.parse("2006-01-01"),
        end = LocalDate.parse("2006-03-31"),
        periodKey = "#006",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatOfficersAssessment,
        "amount" -> 600.00,
        "due" -> "2006-04-05",
        "start" -> "2006-01-01",
        "end" -> "2006-03-31",
        "periodKey" -> "#006",
        "overdue" -> true
      )

      "return the correct message" in {
        testModel.whatYouOweDescription shouldBe "a VAT officer's investigation showed you underpaid by this amount"
      }

      "correctly write to Json" in {
        Json.toJson(testModel) shouldBe testJson
      }
    }

    s"charge type is $vatBNPofRegPre2010" should {

      val testModel = OpenPaymentsModel(
        paymentType = vatBNPofRegPre2010,
        amount = 700.00,
        due = LocalDate.parse("2007-04-05"),
        start = LocalDate.parse("2007-01-01"),
        end = LocalDate.parse("2007-03-31"),
        periodKey = "#007",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatBNPofRegPre2010,
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

    s"charge type is $vatBnpRegPost2010" should {

      val testModel = OpenPaymentsModel(
        paymentType = vatBnpRegPost2010,
        amount = 800.00,
        due = LocalDate.parse("2008-04-05"),
        start = LocalDate.parse("2008-01-01"),
        end = LocalDate.parse("2008-03-31"),
        periodKey = "#008",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatBnpRegPost2010,
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

    s"charge type is $vatFtnMatPre2010" should {

      val testModel = OpenPaymentsModel(
        paymentType = vatFtnMatPre2010,
        amount = 900.00,
        due = LocalDate.parse("2009-04-05"),
        start = LocalDate.parse("2009-01-01"),
        end = LocalDate.parse("2009-03-31"),
        periodKey = "#009",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatFtnMatPre2010,
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

    s"charge type is $vatFtnMatPost2010" should {

      val testModel = OpenPaymentsModel(
        paymentType = vatFtnMatPost2010,
        amount = 1000.00,
        due = LocalDate.parse("2010-04-05"),
        start = LocalDate.parse("2010-01-01"),
        end = LocalDate.parse("2010-03-31"),
        periodKey = "#010",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatFtnMatPost2010,
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

    s"charge type is $vatMiscPenalty" should {

      val testModel = OpenPaymentsModel(
        paymentType = vatMiscPenalty,
        amount = 1100.00,
        due = LocalDate.parse("2011-04-05"),
        start = LocalDate.parse("2011-01-01"),
        end = LocalDate.parse("2011-03-31"),
        periodKey = "#011",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatMiscPenalty,
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

    s"charge type is $vatFtnEachpartner" should {

      val testModel = OpenPaymentsModel(
        paymentType = vatFtnEachpartner,
        amount = 1200.00,
        due = LocalDate.parse("2012-04-05"),
        start = LocalDate.parse("2012-01-01"),
        end = LocalDate.parse("2012-03-31"),
        periodKey = "#012",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatFtnEachpartner,
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

    s"charge type is $vatMpPre2009" should {

      val testModel = OpenPaymentsModel(
        paymentType = vatMpPre2009,
        amount = 1300.00,
        due = LocalDate.parse("2013-04-05"),
        start = LocalDate.parse("2013-01-01"),
        end = LocalDate.parse("2013-03-31"),
        periodKey = "#013",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatMpPre2009,
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

    s"charge type is $vatMpRepeatedPre2009" should {

      val testModel = OpenPaymentsModel(
        paymentType = vatMpRepeatedPre2009,
        amount = 1400.00,
        due = LocalDate.parse("2014-04-05"),
        start = LocalDate.parse("2014-01-01"),
        end = LocalDate.parse("2014-03-31"),
        periodKey = "#014",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatMpRepeatedPre2009,
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

    s"charge type is $vatCivilEvasionPenalty" should {

      val testModel = OpenPaymentsModel(
        paymentType = vatCivilEvasionPenalty,
        amount = 1500.00,
        due = LocalDate.parse("2015-04-05"),
        start = LocalDate.parse("2015-01-01"),
        end = LocalDate.parse("2015-03-31"),
        periodKey = "#015",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatCivilEvasionPenalty,
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

    s"charge type is $officerAssessmentDebitCharge" should {

      val testModel = OpenPaymentsModel(
        paymentType = officerAssessmentDebitCharge,
        amount = 300.00,
        due = LocalDate.parse("2003-04-05"),
        periodKey = "#003",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> officerAssessmentDebitCharge,
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

    s"charge type is $officerAssessmentDefaultInterest" should{

      val testModel = OpenPaymentsModel(
        paymentType = officerAssessmentDefaultInterest,
        amount = 400.00,
        due = LocalDate.parse("2004-04-05"),
        periodKey = "#004",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> officerAssessmentDefaultInterest,
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

    s"charge type is $vatOfficersAssessment" should{

      val testModel = OpenPaymentsModel(
        paymentType = vatOfficersAssessment,
        amount = 500.00,
        due = LocalDate.parse("2005-04-05"),
        periodKey = "#005",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatOfficersAssessment,
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

    s"charge type is $vatBnpRegPost2010" should{

      val testModel = OpenPaymentsModel(
        paymentType = vatBnpRegPost2010,
        amount = 600.00,
        due = LocalDate.parse("2006-04-05"),
        periodKey = "#006",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatBnpRegPost2010,
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

    s"charge type is $vatFtnMatPre2010" should{

      val testModel = OpenPaymentsModel(
        paymentType = vatFtnMatPre2010,
        amount = 700.00,
        due = LocalDate.parse("2007-04-05"),
        periodKey = "#007",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatFtnMatPre2010,
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

    s"charge type is $vatFtnMatPost2010" should{

      val testModel = OpenPaymentsModel(
        paymentType = vatFtnMatPost2010,
        amount = 800.00,
        due = LocalDate.parse("2008-04-05"),
        periodKey = "#008",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatFtnMatPost2010,
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

    s"charge type is $vatMiscPenalty" should{

      val testModel = OpenPaymentsModel(
        paymentType = vatMiscPenalty,
        amount = 900.00,
        due = LocalDate.parse("2009-04-05"),
        periodKey = "#009",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatMiscPenalty,
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

    s"charge type is $vatFtnEachpartner" should{

      val testModel = OpenPaymentsModel(
        paymentType = vatFtnEachpartner,
        amount = 1000.00,
        due = LocalDate.parse("2010-04-05"),
        periodKey = "#010",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatFtnEachpartner,
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

    s"charge type is $vatMpPre2009" should{

      val testModel = OpenPaymentsModel(
        paymentType = vatMpPre2009,
        amount = 1100.00,
        due = LocalDate.parse("2011-04-05"),
        periodKey = "#011",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatMpPre2009,
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

    s"charge type is $vatMpRepeatedPre2009" should{

      val testModel = OpenPaymentsModel(
        paymentType = vatMpRepeatedPre2009,
        amount = 1200.00,
        due = LocalDate.parse("2012-04-05"),
        periodKey = "#012",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatMpRepeatedPre2009,
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

    s"charge type is $vatCivilEvasionPenalty" should{

      val testModel = OpenPaymentsModel(
        paymentType = vatCivilEvasionPenalty,
        amount = 1300.00,
        due = LocalDate.parse("2013-04-05"),
        periodKey = "#013",
        overdue = true
      )

      val testJson = Json.obj(
        "paymentType" -> vatCivilEvasionPenalty,
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
  }
}
