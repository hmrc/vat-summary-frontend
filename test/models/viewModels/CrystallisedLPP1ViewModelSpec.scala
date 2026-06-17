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

package models.viewModels

import common.TestModels._
import models.payments.VatReturn1stLPP
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json
import views.ViewBaseSpec

import java.time.LocalDate

class CrystallisedLPP1ViewModelSpec extends ViewBaseSpec with AnyWordSpecLike with Matchers {

  "The makePaymentRedirect value" should {

    "be a payment handoff URL generated from the model's parameters" in {
      val amountInPence = (crystallisedLPP1Model.leftToPay * 100).toLong
      val chargeTypeEncoded = crystallisedLPP1Model.chargeType.replace(" ", "%20")

      crystallisedLPP1Model.makePaymentRedirect should include(
        s"/make-payment/$amountInPence/${crystallisedLPP1Model.periodTo.getMonthValue}/" +
          s"${crystallisedLPP1Model.periodTo.getYear}/${crystallisedLPP1Model.periodTo}/$chargeTypeEncoded/" +
          s"${crystallisedLPP1Model.dueDate}/${crystallisedLPP1Model.chargeReference}"
      )
    }
  }

  "The CrystallisedLPP1ViewModel" should {

    "read from JSON" when {

      "all fields are populated" in {
        crystallisedLPP1JsonMax.as[CrystallisedLPP1ViewModel] shouldBe crystallisedLPP1Model
      }

      "optional fields are missing" in {
        crystallisedLPP1JsonMin.as[CrystallisedLPP1ViewModel] shouldBe crystallisedLPP1ModelMin
      }
    }

    "write to JSON" when {

      "all fields are populated" in {
        Json.toJson(crystallisedLPP1Model) shouldBe crystallisedLPP1JsonMax
      }

      "optional fields are missing" in {
        Json.toJson(crystallisedLPP1ModelMin) shouldBe crystallisedLPP1JsonMin
      }
    }

    "The buildCrystallisedLPP1ViewModel function" should {

      "The buildCrystallisedLPPViewModel function" should {

        val penaltyCharge = payment.copy(chargeType = VatReturn1stLPP)

        "return a CrystallisedLPP1ViewModel" when {

          "chargeReference and all appropriate LPP1 penalty details (lower rate) are present" in {
            val lppDetails = LPPDetailsModelMaxWithLPP1HRPercentage.copy(LPP1HRCalculationAmount = None)

            CrystallisedLPP1ViewModel.buildCrystallisedLPP1ViewModel(penaltyCharge, Some(lppDetails), false, LocalDate.parse("2019-03-03")) shouldBe Some(CrystallisedLPP1ViewModel(
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

            CrystallisedLPP1ViewModel.buildCrystallisedLPP1ViewModel(penaltyCharge, Some(LPPDetailsModelMaxWithLPP1HRPercentage), false, LocalDate.parse("2019-03-03")) shouldBe
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
      }
    }
  }
}
