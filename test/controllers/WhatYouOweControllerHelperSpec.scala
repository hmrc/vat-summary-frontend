/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers

import common.TestModels.penaltyDetailsModelMin
import models.penalties.LPPDetails
import models.payments._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import views.html.errors.PaymentsError
import views.html.payments.{NoPayments, WhatYouOwe}

class WhatYouOweControllerHelperSpec extends ControllerBaseSpec with AnyWordSpecLike with Matchers {

  private val paymentsErrorView: PaymentsError = injector.instanceOf[PaymentsError]
  private val whatYouOweView: WhatYouOwe = injector.instanceOf[WhatYouOwe]
  private val noPaymentsView: NoPayments = injector.instanceOf[NoPayments]

  private val controller = new WhatYouOweController(
    authorisedController,
    mockDateService,
    mockPaymentsService,
    mockServiceInfoService,
    mcc,
    paymentsErrorView,
    whatYouOweView,
    noPaymentsView,
    mockAccountDetailsService,
    mockPenaltyDetailsService,
    mockWYOSessionService,
    mockAuditService,
    mockPOACheckService,
    mockAnnualAccountingService
  )(ec, mockAppConfig)

  "findPenaltyCharge" should {
    "match by principal charge reference and penalty type for estimated penalties" in {
      val penalties = Seq(LPPDetails(
        principalChargeReference = "PRINC",
        penaltyCategory = "LPP1",
        LPP1LRCalculationAmount = None,
        LPP1LRDays = None,
        LPP1LRPercentage = None,
        LPP1HRCalculationAmount = None,
        LPP1HRDays = None,
        LPP1HRPercentage = None,
        LPP2Days = None,
        LPP2Percentage = None,
        penaltyChargeReference = Some("PEN"),
        timeToPay = false
      ))

      val result = controller.findPenaltyCharge(Some("PRINC"), Some("LPP1"), isEstimate = true, penalties)(fakeRequest)

      result shouldBe penalties.headOption
    }

    "match by penalty charge reference when not estimating, even when references are missing" in {
      val penalties = Seq(LPPDetails(
        principalChargeReference = "PRINC",
        penaltyCategory = "LPP1",
        LPP1LRCalculationAmount = None,
        LPP1LRDays = None,
        LPP1LRPercentage = None,
        LPP1HRCalculationAmount = None,
        LPP1HRDays = None,
        LPP1HRPercentage = None,
        LPP2Days = None,
        LPP2Percentage = None,
        penaltyChargeReference = None,
        timeToPay = false
      ))

      val result = controller.findPenaltyCharge(None, None, isEstimate = false, penalties)(fakeRequest)

      result shouldBe penalties.headOption
    }
  }

  "categoriseCharges" should {
    "build an interest view model for LPI charges" in {
      val payment = PaymentWithPeriod(
        chargeType = VatReturnLPI,
        periodFrom = mockTodayDate,
        periodTo = mockTodayDate.plusMonths(1),
        due = mockTodayDate.plusMonths(2),
        outstandingAmount = BigDecimal(100),
        periodKey = Some("18AA"),
        chargeReference = Some("CR1"),
        ddCollectionInProgress = false,
        accruingInterestAmount = Some(BigDecimal(1)),
        accruingPenaltyAmount = None,
        penaltyType = None,
        originalAmount = BigDecimal(100),
        clearedAmount = None
      )

      mockDateServiceCall()
      val result = controller.categoriseCharges(Seq(payment), penaltyDetailsModelMin, ddStatus = false)(fakeRequest)

      result.head shouldBe defined
    }

    "build an overpayment view model for VAT overpayment charges" in {
      val payment = PaymentWithPeriod(
        chargeType = VatOverpaymentForRPI,
        periodFrom = mockTodayDate,
        periodTo = mockTodayDate.plusMonths(1),
        due = mockTodayDate.plusMonths(2),
        outstandingAmount = BigDecimal(50),
        periodKey = Some("18AA"),
        chargeReference = Some("CR2"),
        ddCollectionInProgress = false,
        accruingInterestAmount = None,
        accruingPenaltyAmount = None,
        penaltyType = None,
        originalAmount = BigDecimal(50),
        clearedAmount = None
      )

      mockDateServiceCall()
      val result = controller.categoriseCharges(Seq(payment), penaltyDetailsModelMin, ddStatus = false)(fakeRequest)

      result.head shouldBe defined
    }
  }
}
