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

package views.templates.payments

import java.time.LocalDate

import models.payments._
import views.ViewBaseSpec

class WhatYouOweChargeHelperSpec extends ViewBaseSpec {

  def paymentModel(chargeType: ChargeType, overdue: Boolean = false): OpenPaymentsModel = OpenPaymentsModel(
    chargeType,
    BigDecimal(100.00),
    LocalDate.parse("2018-03-03"),
    LocalDate.parse("2018-01-01"),
    LocalDate.parse("2018-02-02"),
    "18AA",
    overdue
  )

  def paymentModelNoPeriod(chargeType: ChargeType): OpenPaymentsModel = OpenPaymentsModelNoPeriod(
    chargeType,
    BigDecimal(100.00),
    LocalDate.parse("2018-03-03"),
    "18AA"
  )

  "WhatYouOweChargeHelper .description" when {

    "the charge has a to and from date" should {

      val helper = new WhatYouOweChargeHelper(paymentModel(ReturnDebitCharge), Some(false), messages)

      "return the description of the charge" in {
        helper.description shouldBe Some("for the period 1 January to 2 February 2018")
      }
    }

    "the charge should have a to and from date to form the description, but they are not retrieved" should {

      val model = paymentModelNoPeriod(ReturnDebitCharge)
      val helper = new WhatYouOweChargeHelper(model, Some(false), messages)

      "omit the description of the charge" in {
        helper.description shouldBe None
      }
    }

    "the charge does not have to and from date" should {

      val helper = new WhatYouOweChargeHelper(paymentModel(OADefaultInterestCharge), Some(false), messages)

      "return the description of the charge" in {
        helper.description shouldBe Some("interest charged on the officer's assessment")
      }
    }

    "the charge has no description" should {

      val helper = new WhatYouOweChargeHelper(paymentModel(MiscPenaltyCharge), Some(false), messages)

      "return no description" in {
        helper.description shouldBe None
      }
    }
  }

  "WhatYouOweChargeHelper .title" should {

    val helper = new WhatYouOweChargeHelper(paymentModel(VatOAInaccuraciesFrom2009), Some(false), messages)

    "return the title" in {
      helper.title shouldBe "Inaccuracies penalty"
    }
  }

  "WhatYouOweChargeHelper .payLinkText" when {


    "charge type is Return Debit charge and has a direct debit" should {

      val helper = new WhatYouOweChargeHelper(paymentModel(ReturnDebitCharge), Some(true), messages)

      "return no Pay text" in {
        helper.payLinkText shouldBe None
      }
    }

    "charge type is Central Assessment charge" should {

      val helper = new WhatYouOweChargeHelper(paymentModel(CentralAssessmentCharge), Some(true), messages)

      "return 'Pay estimate'" in {
        helper.payLinkText shouldBe Some("Pay estimate")
      }
    }

    "charge type is a different type of charge" should {

      val helper = new WhatYouOweChargeHelper(paymentModel(OAFurtherInterestCharge), Some(true), messages)

      "return Pay now" in {
        helper.payLinkText shouldBe Some("Pay now")
      }
    }
  }

  "WhatYouOweChargeHelper .viewReturnEnabled" when {

    "charge type is Return Debit Charge" should {

      val helper = new WhatYouOweChargeHelper(paymentModel(ReturnDebitCharge), Some(true), messages)

      "return true" in {
        helper.viewReturnEnabled shouldBe true
      }
    }

    "charge type is Error Correction Debit Charge" should {

      val helper = new WhatYouOweChargeHelper(paymentModel(ErrorCorrectionDebitCharge), Some(true), messages)

      "return true" in {
        helper.viewReturnEnabled shouldBe true
      }
    }

    "charge type is a different type of charge" should {

      val helper = new WhatYouOweChargeHelper(paymentModel(OAFurtherInterestCharge), Some(true), messages)

      "return Pay now" in {
        helper.viewReturnEnabled shouldBe false
      }
    }
  }

  "WhatYouOweChargeHelper .overdueContext" when {

    "charge type is Return Debit Charge and overdue" should {

      val helper = new WhatYouOweChargeHelper(paymentModel(ReturnDebitCharge, overdue = true), Some(true), messages)

      "return overdue" in {
        helper.overdueContext shouldBe "overdue"
      }
    }

    "charge type is Return Debit Charge and not overdue" should {

      val helper = new WhatYouOweChargeHelper(paymentModel(ReturnDebitCharge), Some(true), messages)

      "return empty string" in {
        helper.overdueContext shouldBe ""
      }
    }

    "charge type is a different type of charge and overdue" should {

      val helper = new WhatYouOweChargeHelper(paymentModel(OAFurtherInterestCharge, overdue = true), Some(true), messages)

      "return 'is overdue'" in {
        helper.overdueContext shouldBe "is overdue,"
      }
    }

    "charge type is a different type of charge and not overdue" should {

      val helper = new WhatYouOweChargeHelper(paymentModel(OAFurtherInterestCharge), Some(true), messages)

      "return ','" in {
        helper.overdueContext shouldBe ","
      }
    }
  }

  "WhatYouOweChargeHelper .viewReturnContext" when {

    "the charge has a to and from period" when {

      "charge type is Return Debit Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ReturnDebitCharge), Some(true), messages)

        "return 'that you corrected for the period 1 January to 2 February 2018'" in {
          helper.viewReturnContext shouldBe "for the period 1 January to 2 February 2018"
        }
      }

      "charge type is Error Correction Debit Charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(ErrorCorrectionDebitCharge), Some(true), messages)

        "return 'for the period 1 January to 2 February 2018'" in {
          helper.viewReturnContext shouldBe "that you corrected for the period 1 January to 2 February 2018"
        }
      }

      "charge type is different type of charge" should {

        val helper = new WhatYouOweChargeHelper(paymentModel(OAFurtherInterestCharge), Some(true), messages)

        "return empty string" in {
          helper.viewReturnContext shouldBe ""
        }
      }
    }

    "the charge has no to or from period" should {

      val helper = new WhatYouOweChargeHelper(paymentModelNoPeriod(MpRepeatedPre2009Charge), Some(true), messages)

      "return empty string" in {
        helper.viewReturnContext shouldBe ""
      }
    }
  }

  "WhatYouOweChargeHelper .viewReturnGAEvent" when {

    "the charge has a to and from period" should {

      val helper = new WhatYouOweChargeHelper(paymentModel(OAFurtherInterestCharge), Some(true), messages)

      "return 'returns:view-return 2018-01-01-to-2018-02-02:open-payments'" in {
        helper.viewReturnGAEvent shouldBe "returns:view-return 2018-01-01-to-2018-02-02:open-payments"
      }
    }

    "the charge has no to and from period" should {

      val helper = new WhatYouOweChargeHelper(paymentModelNoPeriod(MpRepeatedPre2009Charge), Some(true), messages)

      "return 'returns:view-return:open-payments'" in {
        helper.viewReturnGAEvent shouldBe "returns:view-return:open-payments"
      }
    }
  }
}
