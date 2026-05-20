/*
 * Copyright 2026 HM Revenue & Customs
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

package services

import common.TestModels._
import controllers.ControllerBaseSpec
import models.payments.{AACharge, VatLateSubmissionPen, VatPA2ndLPP, VatReturn1stLPP, VatReturnLPI}
import models.viewModels.{CrystallisedInterestViewModel, CrystallisedLPP1ViewModel, CrystallisedLPP2ViewModel, EstimatedInterestViewModel, EstimatedLPP1ViewModel, EstimatedLPP2ViewModel, LateSubmissionPenaltyViewModel, StandardChargeViewModel, VatOverpaymentForRPIViewModel}
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.LocalDate

class WYOViewServiceSpec extends AnyWordSpec with Matchers with MockFactory {

  val mockDateServ: DateService = mock[DateService]
  val service = new WYOViewService(mockDateServ)

  def mockDateServiceCall(): Any =
    (() => mockDateServ.now())
      .stubs()
      .returns(LocalDate.parse("2018-05-01"))

  "The findPenaltyCharge method" when {

    val penaltyType = Some("LPP1")

    "the penalty is an estimate" when {

      val chargeReference = Some("ABCDEFGHIJKLMNOP")

      "there is a penalty with a matching principal charge reference and penalty type" should {

        "return that penalty" in {
          val res = service.findPenaltyCharge(chargeReference, penaltyType, isEstimate = true, Seq(LPPDetailsModelMin))
          res shouldBe Some(LPPDetailsModelMin)
        }
      }

      "the principal charge reference does not match" should {

        "return None" in {
          val res = service.findPenaltyCharge(Some("FLJDHGKDJFH"), penaltyType, isEstimate = true, Seq(LPPDetailsModelMin))
          res shouldBe None
        }
      }

      "the penalty charge reference matches" should {

        "return None" in {
          val res = service.findPenaltyCharge(Some("BCDEFGHIJKLMNOPQ"), penaltyType, isEstimate = true, Seq(LPPDetailsModelMaxWithLPP1HRPercentage))
          res shouldBe None
        }
      }

      "the penalty type does not match" should {

        "return None" in {
          val res = service.findPenaltyCharge(chargeReference, Some("LPP2"), isEstimate = true, Seq(LPPDetailsModelMin))
          res shouldBe None
        }
      }

      "a charge reference is not provided" should {

        "return None" in {
          val res = service.findPenaltyCharge(None, penaltyType, isEstimate = true, Seq(LPPDetailsModelMin))
          res shouldBe None
        }
      }

      "a penalty type is not provided" should {

        "return None" in {
          val res = service.findPenaltyCharge(chargeReference, None, isEstimate = true, Seq(LPPDetailsModelMin))
          res shouldBe None
        }
      }

      "an empty list of penalties is provided" should {

        "return None" in {
          val res = service.findPenaltyCharge(chargeReference, penaltyType, isEstimate = true, Seq())
          res shouldBe None
        }
      }
    }

    "the penalty is crystallised" when {

      val chargeReference = Some("BCDEFGHIJKLMNOPQ")

      "there is a penalty with a matching penalty charge reference and penalty type" should {

        "return that penalty" in {
          val res = service.findPenaltyCharge(chargeReference, None, isEstimate = false, Seq(LPPDetailsModelMaxWithLPP1HRPercentage))
          res shouldBe Some(LPPDetailsModelMaxWithLPP1HRPercentage)
        }
      }

      "the penalty charge reference does not match" should {

        "return None" in {
          val res = service.findPenaltyCharge(Some("FLJDHGKDJFH"), None, isEstimate = false, Seq(LPPDetailsModelMaxWithLPP1HRPercentage))
          res shouldBe None
        }
      }

      "the principal charge reference matches" should {

        "return None" in {
          val res = service.findPenaltyCharge(Some("XD002750002155"), None, isEstimate = false, Seq(LPPDetailsModelMaxWithLPP1HRPercentage))
          res shouldBe None
        }
      }

      "a charge reference is not provided" should {

        "return None" in {
          val res = service.findPenaltyCharge(None, None, isEstimate = false, Seq(LPPDetailsModelMaxWithLPP1HRPercentage))
          res shouldBe None
        }
      }

      "an empty list of penalties is provided" should {

        "return None" in {
          val res = service.findPenaltyCharge(chargeReference, None, isEstimate = false, Seq())
          res shouldBe None
        }
      }
    }
  }

  "The buildPenaltyChargePlusEstimates" when {

    "there is a crystallised penalty charge with accrued interest" should {

      "return a crystallised penalty and estimated interest view model" in {
        val charge = payment.copy(chargeType = VatReturn1stLPP)
        mockDateServiceCall()
        service.buildCrystallisedChargePlusEstimates(charge, Some(LPPDetailsModelMaxWithLPP1HRPercentage), false) shouldBe
          Seq(Some(crystallisedPenaltyModel), Some(estimatedLPIPenalty))
      }

      "return a crystallised penalty view model" in {
        val charge = payment.copy(chargeType = VatReturn1stLPP, accruingInterestAmount = None)
        mockDateServiceCall()
        service.buildCrystallisedChargePlusEstimates(charge, Some(LPPDetailsModelMaxWithLPP1HRPercentage), false) shouldBe
          Seq(Some(crystallisedPenaltyModel))
      }
    }
  }

  "The buildChargePlusEstimates function" when {

    "the charge has accruing interest and accruing LPP" should {

      "return three charges" in {
        mockDateServiceCall()
        service.buildStandardChargePlusEstimates(payment, penaltyDetailsModelMax, false).size shouldBe 3
      }
    }

    "the charge has accruing LPP" should {

      "return two charges" in {
        mockDateServiceCall()
        val charge = payment.copy(accruingInterestAmount = None)
        service.buildStandardChargePlusEstimates(charge, penaltyDetailsModelMax, false).size shouldBe 2
      }
    }

    "the charge has accruing interest" should {

      "return two charges" in {
        mockDateServiceCall()
        val charge = payment.copy(accruingPenaltyAmount = None)
        service.buildStandardChargePlusEstimates(charge, penaltyDetailsModelMin, false).size shouldBe 2
      }
    }

    "the charge has nothing accruing" should {

      "return one charge" in {
        mockDateServiceCall()
        val charge = payment.copy(accruingInterestAmount = None, accruingPenaltyAmount = None)
        service.buildStandardChargePlusEstimates(charge, penaltyDetailsModelMin, false).size shouldBe 1
      }
    }
  }

  "The buildEstimatedLPPViewModel function" should {

    "return a EstimatedLPP1ViewModel" when {

      "accruingPenaltyAmount and all appropriate LPP1 penalty details are present (no LPP1HRPercentage)" in {
        service.buildEstimatedLPPViewModel(
          payment,
          Some(LPPDetailsModelMaxWithoutLPP1HRPercentage),
          breathingSpace = false, false
        ) shouldBe Some(EstimatedLPP1ViewModel(
          "15",
          "30",
          2.4,
          2.4,
          100.11,
          50.55,
          LocalDate.parse("2019-01-01"),
          LocalDate.parse("2019-02-02"),
          "VAT Return 1st LPP",
          timeToPayPlan = false,
          breathingSpace = false, directDebitMandateFound = false
        ))
      }

      "accruingPenaltyAmount and all appropriate LPP1 penalty details are present (including LPP1HRPercentage)" in {
        service.buildEstimatedLPPViewModel(
          payment,
          Some(LPPDetailsModelMaxWithLPP1HRPercentage),
          breathingSpace = false, false
        ) shouldBe Some(EstimatedLPP1ViewModel(
          "15",
          "30",
          2.4,
          4.2,
          100.11,
          50.55,
          LocalDate.parse("2019-01-01"),
          LocalDate.parse("2019-02-02"),
          "VAT Return 1st LPP",
          timeToPayPlan = false,
          breathingSpace = false, directDebitMandateFound = false
        ))
      }
    }

    "return a EstimatedLPP2ViewModel" when {

      "accruingPenaltyAmount and all appropriate LPP2 penalty details are present" in {
        val charge = payment.copy(chargeType = AACharge)
        val penalty = LPPDetailsModelMaxWithLPP1HRPercentage.copy(penaltyCategory = "LPP2")
        service.buildEstimatedLPPViewModel(charge, Some(penalty), breathingSpace = false, false) shouldBe Some(EstimatedLPP2ViewModel(
          "31",
          5.5,
          50.55,
          LocalDate.parse("2019-01-01"),
          LocalDate.parse("2019-02-02"),
          "VAT AA 2nd LPP",
          timeToPay = false,
          breathingSpace = false, directDebitMandateFound = false
        ))
      }
    }

    "return None" when {

      "accruingPenaltyAmount is missing" in {
        val charge = payment.copy(accruingPenaltyAmount = None)
        service.buildEstimatedLPPViewModel(
          charge, Some(LPPDetailsModelMaxWithLPP1HRPercentage), breathingSpace = false, ddStatus = false
        ) shouldBe None
      }

      "penalty type is not recognised" in {
        val penalty = LPPDetailsModelMaxWithLPP1HRPercentage.copy(penaltyCategory = "LPP3")
        service.buildEstimatedLPPViewModel(payment, Some(penalty), breathingSpace = false, ddStatus = false) shouldBe None
      }

      "penalty type is LPP1 but LPP1 details are missing" in {
        val penalty = LPPDetailsModelMaxWithLPP1HRPercentage
          .copy(LPP1LRDays = None, LPP1HRDays = None, LPP1LRPercentage = None,
            LPP1HRPercentage = None, LPP1LRCalculationAmount = None)
        service.buildEstimatedLPPViewModel(payment, Some(penalty), breathingSpace = false, ddStatus = false) shouldBe None
      }

      "penalty type is LPP2 but LPP2 details are missing" in {
        val penalty = LPPDetailsModelMaxWithLPP1HRPercentage
          .copy(penaltyCategory = "LPP2", LPP2Days = None, LPP2Percentage = None)
        service.buildEstimatedLPPViewModel(payment, Some(penalty), breathingSpace = false, ddStatus = false) shouldBe None
      }

      "no matching penalty was found" in {
        service.buildEstimatedLPPViewModel(payment, None, breathingSpace = false, ddStatus = false) shouldBe None
      }
    }
  }

  "The buildCrystallisedLPPViewModel function" should {

    val penaltyCharge = payment.copy(chargeType = VatReturn1stLPP)
    val penaltyLPP2Charge = payment.copy(chargeType = VatPA2ndLPP)

    "return a CrystallisedLPP1ViewModel" when {

      "chargeReference and all appropriate LPP1 penalty details (lower rate) are present" in {
        val lppDetails = LPPDetailsModelMaxWithLPP1HRPercentage.copy(LPP1HRCalculationAmount = None)
        mockDateServiceCall()

        service.buildCrystallisedLPPViewModel(penaltyCharge, Some(lppDetails), false) shouldBe Some(CrystallisedLPP1ViewModel(
          "15",
          "15",
          Some("30"),
          2.4,
          Some(4.2),
          100.11,
          None,
          LocalDate.parse("2019-03-03"),
          10000,
          0,
          10000,
          LocalDate.parse("2019-01-01"),
          LocalDate.parse("2019-02-02"),
          "VAT Return 1st LPP",
          "XD002750002155",
          isOverdue = false, directDebitMandateFound = false
        ))
      }

      "chargeReference and all appropriate LPP1 penalty details (higher rate) are present" in {

        mockDateServiceCall()

        service.buildCrystallisedLPPViewModel(penaltyCharge, Some(LPPDetailsModelMaxWithLPP1HRPercentage), false) shouldBe
          Some(CrystallisedLPP1ViewModel(
            "30",
            "15",
            Some("30"),
            2.4,
            Some(4.2),
            100.11,
            Some(200.22),
            LocalDate.parse("2019-03-03"),
            10000,
            0,
            10000,
            LocalDate.parse("2019-01-01"),
            LocalDate.parse("2019-02-02"),
            "VAT Return 1st LPP",
            "XD002750002155",
            isOverdue = false, directDebitMandateFound = false
          ))
      }
    }

    "return a CrystallisedLPP2ViewModel" when {

      "chargeReference and all appropriate LPP2 penalty details are present" in {

        mockDateServiceCall()

        service.buildCrystallisedLPPViewModel(penaltyLPP2Charge, Some(LPPLPP2DetailsModelMax), false) shouldBe
          Some(CrystallisedLPP2ViewModel(
            "31",
            5.5,
            LocalDate.parse("2019-03-03"),
            10000,
            0,
            10000,
            LocalDate.parse("2019-01-01"),
            LocalDate.parse("2019-02-02"),
            "VAT PA 2nd LPP",
            "XD002750002155",
            isOverdue = false, directDebitMandateFound = false
          ))
      }
    }

    "return None" when {

      "charge reference is missing for LPP1" in {
        mockDateServiceCall()
        val charge = penaltyCharge.copy(chargeReference = None)
        service.buildCrystallisedLPPViewModel(charge, Some(LPPDetailsModelMaxWithLPP1HRPercentage), ddStatus = false) shouldBe None
      }

      "charge reference and penalty details are missing for LPP2" in {
        val charge = penaltyLPP2Charge.copy(chargeReference = None)
        service.buildCrystallisedLPPViewModel(charge, None, ddStatus = false) shouldBe None
      }

      "penalty type is not recognised" in {
        val penalty = LPPDetailsModelMaxWithLPP1HRPercentage.copy(penaltyCategory = "LPP3")
        service.buildCrystallisedLPPViewModel(penaltyCharge, Some(penalty), ddStatus = false) shouldBe None
      }

      "no matching penalty is provided" in {
        service.buildCrystallisedLPPViewModel(penaltyCharge, None, ddStatus = false) shouldBe None
      }

      "penalty type is LPP1 but LPP1 details are missing" in {
        val penalty = LPPDetailsModelMaxWithLPP1HRPercentage
          .copy(LPP1LRDays = None, LPP1HRDays = None, LPP1LRPercentage = None,
            LPP1HRPercentage = None, LPP1LRCalculationAmount = None)

        mockDateServiceCall()

        service.buildCrystallisedLPPViewModel(penaltyCharge, Some(penalty), false) shouldBe None
      }

      "penalty type is LPP2 but LPP2 details are missing" in {
        val penalty = LPPLPP2DetailsModelMax.copy(LPP2Days = None, LPP2Percentage = None)

        mockDateServiceCall()

        service.buildCrystallisedLPPViewModel(penaltyLPP2Charge, Some(penalty), ddStatus = false) shouldBe None
      }
    }
  }

  "The buildLSPPlusEstimates function" when {

    "there is accruing interest on the Late Submission Penalty" should {

      "return a LateSubmissionPenalty and Estimate view model" in {
        val charge = payment.copy(chargeType = VatLateSubmissionPen)

        mockDateServiceCall()

        service.buildLSPPlusEstimates(charge, false) shouldBe
          Seq(Some(LateSubmissionPenaltyViewModel(
            "VAT Late Submission Pen",
            LocalDate.parse("2019-03-03"),
            10000,
            0,
            10000,
            isOverdue = false,
            "XD002750002155",
            LocalDate.parse("2019-01-01"),
            LocalDate.parse("2019-02-02"), false
          )), Some(estimatedLSPInterestPenalty))
      }
    }
  }

  "the buildEstimatedInterest function" when {

    "there is an accruingInterestAmount" should {

      "return an EstimatedInterestViewModel" in {
        service.buildEstimatedInterest(payment, false) shouldBe
          Some(EstimatedInterestViewModel(
            LocalDate.parse("2019-01-01"),
            LocalDate.parse("2019-02-02"),
            "VAT Return LPI",
            2,
            isPenaltyReformPenaltyLPI = false,
            isNonPenaltyReformPenaltyLPI = false, directDebitMandateFound = false
          ))
      }
    }

    "accruingInterestAmount is missing" should {

      "return None" in {
        val charge = payment.copy(accruingInterestAmount = None)
        service.buildEstimatedInterest(charge, false) shouldBe None
      }
    }
  }

  "the buildCrystallisedInterest function" when {

    "there is a chargeReference" should {

      "return a CrystallisedInterestViewModel" in {
        val charge = payment.copy(chargeType = VatReturnLPI, clearedAmount = None)

        service.buildCrystallisedInterest(charge, false, LocalDate.parse("2019-03-02")) shouldBe
          Some(CrystallisedInterestViewModel(
            LocalDate.parse("2019-01-01"),
            LocalDate.parse("2019-02-02"),
            "VAT Return LPI",
            LocalDate.parse("2019-03-03"),
            10000,
            0,
            10000,
            isOverdue = false,
            "XD002750002155",
            isPenaltyReformPenaltyLPI = false,
            isNonPenaltyReformPenaltyLPI = false, directDebitMandateFound = false
          ))
      }
    }

    "chargeReference is missing" should {

      "return None" in {
        val charge = payment.copy(chargeType = VatReturnLPI, chargeReference = None)
        service.buildCrystallisedInterest(charge, false, LocalDate.parse("2019-03-02")) shouldBe None
      }
    }
  }

  "the buildLateSubmissionPenalty function" when {

    "there is a chargeReference" should {

      "return a LateSubmissionPenaltyViewModel" in {
        val charge = payment.copy(chargeType = VatLateSubmissionPen, clearedAmount = None)
        service
          .buildLateSubmissionPenalty(charge, false, LocalDate.parse("2019-03-02")) shouldBe Some(LateSubmissionPenaltyViewModel(
          "VAT Late Submission Pen",
          LocalDate.parse("2019-03-03"),
          10000,
          0,
          10000,
          isOverdue = false,
          "XD002750002155",
          LocalDate.parse("2019-01-01"),
          LocalDate.parse("2019-02-02"), false
        ))
      }
    }

    "chargeReference is missing" should {

      "return None" in {
        val charge = payment.copy(chargeType = VatLateSubmissionPen, chargeReference = None)
        service.buildLateSubmissionPenalty(charge, false, LocalDate.parse("2019-03-02")) shouldBe None
      }
    }
  }
}
