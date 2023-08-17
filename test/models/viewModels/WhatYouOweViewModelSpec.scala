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

import common.{SpecBase, TestModels}

import java.time.LocalDate

class WhatYouOweViewModelSpec extends SpecBase {

  val testDueDate: LocalDate = LocalDate.parse("2023-09-07")

  val testCharges: Seq[ChargeDetailsViewModel with Serializable] = Seq(
    TestModels.chargeModel1.copy(dueDate = testDueDate.plusDays(3)),
    TestModels.chargeModel2.copy(dueDate = testDueDate.plusDays(1)),
    TestModels.overdueCrystallisedInterestCharge.copy(dueDate = testDueDate),
    TestModels.whatYouOweChargeModel.copy(dueDate = testDueDate.plusDays(2)),
    TestModels.whatYouOweChargeModelEstimatedInterest,
    TestModels.estimatedLPP1Model,
    TestModels.whatYouOweChargeModelInterestCharge.copy(dueDate = testDueDate.plusDays(1)),
    TestModels.lateSubmissionPenaltyCharge.copy(dueDate = testDueDate.plusYears(1))
  )

  def testModel(charges: Seq[ChargeDetailsViewModel]): WhatYouOweViewModel =
    WhatYouOweViewModel(
      totalAmount = BigDecimal(12345.67),
      charges = charges,
      mandationStatus = "mtdVatMandationStatus",
      containsOverduePayments = true,
      breathingSpace = false
    )

  "earliestDueDate" when {

    "there are multiple charges" when {

      "there are multiple dueDates" must {

        "return the earliest due date" in {

          val expectedResult = Some(testDueDate)
          val actualResult = testModel(testCharges).earliestDueDate

          expectedResult mustBe actualResult
        }
      }

      "there are multiple dueDates and multiple have the earliest due date" must {

        "return the earliest due date" in {

          val testChargesSameDueDate = Seq(
            TestModels.chargeModel1.copy(dueDate = testDueDate),
            TestModels.chargeModel2.copy(dueDate = testDueDate),
          )

          val expectedResult = Some(testDueDate)
          val actualResult = testModel(testCharges ++ testChargesSameDueDate).earliestDueDate

          expectedResult mustBe actualResult
        }
      }

      "there is a single dueDate" must {

        "return the due date" in {

          val testCharges = Seq(
            TestModels.whatYouOweChargeModel.copy(dueDate = testDueDate),
            TestModels.whatYouOweChargeModelEstimatedInterest,
            TestModels.estimatedLPP1Model,
          )

          val expectedResult = Some(testDueDate)
          val actualResult = testModel(testCharges).earliestDueDate

          expectedResult mustBe actualResult
        }
      }

      "there are charges but no due dates" must {

        "return none" in {

          val testCharges = Seq(
            TestModels.whatYouOweChargeModelEstimatedInterest,
            TestModels.estimatedLPP1Model,
          )

          val expectedResult = None
          val actualResult = testModel(testCharges).earliestDueDate

          expectedResult mustBe actualResult
        }
      }
    }

    "there is a single charges" when {

      "there is a dueDate" must {

        "return the due date" in {

          val testCharges = Seq(
            TestModels.chargeModel1.copy(dueDate = testDueDate)
          )

          val expectedResult = Some(testDueDate)
          val actualResult = testModel(testCharges).earliestDueDate

          expectedResult mustBe actualResult
        }
      }

      "there is a charge but no due date" must {

        "return none" in {

          val testCharges = Seq(
            TestModels.whatYouOweChargeModelEstimatedInterest
          )

          val expectedResult = None
          val actualResult = testModel(testCharges).earliestDueDate

          expectedResult mustBe actualResult
        }
      }
    }

    "there are no charges" must {

      "return none" in {

        val expectedResult = None
        val actualResult = testModel(Seq()).earliestDueDate

        expectedResult mustBe actualResult
      }
    }
  }

  "earliestDueDateFormatted" when {

    "there are charges with due dates" must {

      "return the due date as a string in iso format" in {

        val expectedResult =  Some("2023-09-07")
        val actualResult = testModel(testCharges).earliestDueDateFormatted

        expectedResult mustBe actualResult
      }
    }

    "there are no charges with due dates" must {

      "return None" in {

        val expectedResult = None
        val actualResult = testModel(Seq()).earliestDueDateFormatted

        expectedResult mustBe actualResult
      }
    }
  }
}
