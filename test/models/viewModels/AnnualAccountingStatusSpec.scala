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

package models.viewModels

import controllers.AnnualAccountingController
import models.{StandingRequest, StandingRequestDetail, RequestItem}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers
import java.time.LocalDate
import models.payments._

class AnnualAccountingStatusSpec extends AnyWordSpecLike with Matchers {

  private def standingRequest(periodStart: String, periodEnd: String, payments: Seq[(String, BigDecimal)]) = {
    val items = payments.zipWithIndex.map { case ((due, amt), idx) =>
      // For AA SR items, use the specific due date as the period end for that instalment
      RequestItem((idx + 1).toString, "25A1", periodStart, due, due, amt, None, None)
    }.toList
    StandingRequest(
      processingDate = "2025-01-01",
      standingRequests = List(
        StandingRequestDetail("REQ1", "4", "2025-01-01", Some("2025-02-01"), items)
      )
    )
  }

  "buildViewModel" should {
    "mark payments as Paid, Paid Late, Upcoming based on clearedDate and Payment outstanding" in {
      val today = LocalDate.parse("2025-08-01")
      val sr = standingRequest("2025-02-01", "2025-11-30",
        Seq("2025-03-31" -> BigDecimal(100), "2025-04-30" -> BigDecimal(200), "2025-11-30" -> BigDecimal(300)))

      val history = Seq(
        PaymentsHistoryModel(None, chargeType = null, taxPeriodFrom = Some(LocalDate.parse("2025-02-01")), taxPeriodTo = Some(LocalDate.parse("2025-04-30")), amount = BigDecimal(100), clearedDate = Some(LocalDate.parse("2025-03-30"))), // Paid
        PaymentsHistoryModel(None, chargeType = null, taxPeriodFrom = Some(LocalDate.parse("2025-02-01")), taxPeriodTo = Some(LocalDate.parse("2025-04-30")), amount = BigDecimal(200), clearedDate = Some(LocalDate.parse("2025-05-02")))  // Paid Late
      )

      val payments = Payments(Seq(
        PaymentNoPeriod(AAQuarterlyInstalments, LocalDate.parse("2025-03-31"), outstandingAmount = 0, periodKey = Some("25A1"), chargeReference = Some("REF1"), ddCollectionInProgress = false, accruingInterestAmount = None, accruingPenaltyAmount = None, penaltyType = None, originalAmount = BigDecimal(100), clearedAmount = Some(100)),
        PaymentNoPeriod(AAQuarterlyInstalments, LocalDate.parse("2025-04-30"), outstandingAmount = 0, periodKey = Some("25A1"), chargeReference = Some("REF2"), ddCollectionInProgress = false, accruingInterestAmount = None, accruingPenaltyAmount = None, penaltyType = None, originalAmount = BigDecimal(200), clearedAmount = Some(200)),
        PaymentNoPeriod(AAQuarterlyInstalments, LocalDate.parse("2025-11-30"), outstandingAmount = 300, periodKey = Some("25A1"), chargeReference = Some("REF3"), ddCollectionInProgress = false, accruingInterestAmount = None, accruingPenaltyAmount = None, penaltyType = None, originalAmount = BigDecimal(300), clearedAmount = None)
      ))

      val vm = AnnualAccountingController.buildViewModel(
        standingRequestResponse = sr,
        today = today,
        returnObligations = None,
        paymentsHistory = history,
        paymentsOpt = Some(payments),
        isAgent = false,
        hasDirectDebit = Some(false)
      )

      val statuses = vm.currentPeriods.head.payments.map(_.status)
      statuses(0) shouldBe AAPaymentStatus.Paid
      statuses(1) shouldBe AAPaymentStatus.PaidLate
      statuses(2) shouldBe AAPaymentStatus.Upcoming
    }

    "mark a past-due unpaid payment as Overdue when outstanding > 0 and no clearedDate" in {
      val today = LocalDate.parse("2025-05-10")
      val sr = standingRequest("2025-02-01", "2025-06-30",
        Seq("2025-03-31" -> BigDecimal(100)))

      val payments = Payments(Seq(
        PaymentNoPeriod(AAQuarterlyInstalments, LocalDate.parse("2025-03-31"), outstandingAmount = 100, periodKey = Some("25A1"), chargeReference = Some("REF1"), ddCollectionInProgress = false, accruingInterestAmount = None, accruingPenaltyAmount = None, penaltyType = None, originalAmount = BigDecimal(100), clearedAmount = None)
      ))

      val vm = AnnualAccountingController.buildViewModel(
        standingRequestResponse = sr,
        today = today,
        returnObligations = None,
        paymentsHistory = Seq.empty,
        paymentsOpt = Some(payments),
        isAgent = false,
        hasDirectDebit = Some(false)
      )

      val period = vm.currentPeriods.headOption.getOrElse(vm.pastPeriods.head)
      period.payments.head.status shouldBe AAPaymentStatus.Overdue
    }
  }
}


