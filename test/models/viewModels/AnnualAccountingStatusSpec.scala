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

package models.viewModels

import controllers.AnnualAccountingController
import models.payments._
import models.viewModels.AAPaymentStatus.{Overdue, Paid, PaidLate, Upcoming}
import models.{RequestItem, StandingRequest, StandingRequestDetail}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import java.time.LocalDate

class AnnualAccountingStatusSpec extends AnyWordSpecLike with Matchers {

  private val dueDate = LocalDate.parse("2025-03-31")
  private val today   = LocalDate.parse("2025-08-01")
  private def standingRequestWithItemsDueDate(dueDate: LocalDate) = {
    val payments = Seq(dueDate.toString -> BigDecimal(100), dueDate.toString -> BigDecimal(200), dueDate.toString -> BigDecimal(300))
    val items = payments.zipWithIndex.map { case ((due, amt), idx) =>
      RequestItem((idx + 1).toString, "25A1", "2025-02-01", due, due, amt, None, None)
    }.toList
    StandingRequest(
      processingDate = "2025-01-01",
      standingRequests = List(
        StandingRequestDetail("REQ1", "4", "2025-01-01", Some("2025-02-01"), items)
      )
    )
  }

  private def buildPayments(outstandingAmount: Option[BigDecimal],
                            clearingDate: Option[LocalDate] = None,
                            dueDateOverride: Option[LocalDate] = None) =
    PaymentWithOptionalOutstanding(
      chargeType = AAQuarterlyInstalments,
      due = dueDateOverride.getOrElse(dueDate),
      outstandingAmount = outstandingAmount,
      periodKey = Some("25A1"),
      chargeReference = Some("REF1"),
      ddCollectionInProgress = false,
      accruingInterestAmount = None,
      accruingPenaltyAmount = None,
      penaltyType = None,
      originalAmount = Some(BigDecimal(100)),
      clearedAmount = Some(100),
      clearingDate = clearingDate
    )

  private def buildViewModel(payment: PaymentWithOptionalOutstanding,
                             clearingDate: Option[LocalDate] = None,
                             dueDateOverride: Option[LocalDate] = None): AnnualAccountingViewModel = {
    val altDueDate = dueDateOverride.getOrElse(dueDate)
    AnnualAccountingController.buildViewModel(
      standingRequestResponse = standingRequestWithItemsDueDate(altDueDate),
      today = today,
      returnObligations = None,
      chargesWithDueDates = Seq(
        PaymentHistoryWithDueDate(chargeType = AAMonthlyInstalment, dueDate = altDueDate, clearedDate = clearingDate),
        PaymentHistoryWithDueDate(chargeType = AAMonthlyInstalment, dueDate = altDueDate, clearedDate = clearingDate)
      ),
      paymentsDetails = Some(PaymentsWithOptionalOutstanding(Seq(payment))),
      isAgent = false,
      hasDirectDebit = Some(false),
      displayName = None
    )
  }

  private def getStatusFromViewModel(viewModel: AnnualAccountingViewModel) = viewModel.currentPeriods.head.payments.map(_.status)

  "buildViewModel" should {
    "give payment a 'Paid' status" when {
      "payment has a clearingDate which is before the dueDate" when {
        "outstandingAmount is Some(0)" in {
          val payments = buildPayments(outstandingAmount = Some(0), clearingDate = Some(dueDate.minusWeeks(1)))
          val result   = buildViewModel(payments, clearingDate = Some(dueDate.minusWeeks(1)))

          getStatusFromViewModel(result).head shouldBe Paid
        }

        "outstandingAmount is None" in {
          val payments = buildPayments(outstandingAmount = None, clearingDate = Some(dueDate.minusWeeks(1)))
          val result   = buildViewModel(payments)

          getStatusFromViewModel(result).head shouldBe Paid
        }
      }

      "payment has a clearingDate which is equal to the dueDate" when {
        "outstandingAmount is Some(0)" in {
          val payments = buildPayments(outstandingAmount = Some(0), clearingDate = Some(dueDate))
          val result   = buildViewModel(payments)

          getStatusFromViewModel(result).head shouldBe Paid
        }

        "outstandingAmount is None" in {
          val payments = buildPayments(outstandingAmount = None, clearingDate = Some(dueDate))
          val result   = buildViewModel(payments)

          getStatusFromViewModel(result).head shouldBe Paid
        }
      }
    }

    "give payment a 'PaidLate' status" when {
      "payment has a clearingDate which is after the dueDate" when {
        "outstandingAmount is Some(0)" in {
          val payments = buildPayments(outstandingAmount = Some(0), clearingDate = Some(dueDate.plusWeeks(1)))
          val result   = buildViewModel(payments, clearingDate = Some(dueDate.plusWeeks(1)))

          getStatusFromViewModel(result).head shouldBe PaidLate
        }

        "outstandingAmount is None" in {
          val payments = buildPayments(outstandingAmount = None, clearingDate = Some(dueDate.plusWeeks(1)))
          val result   = buildViewModel(payments, clearingDate = Some(dueDate.plusWeeks(1)))

          getStatusFromViewModel(result).head shouldBe PaidLate
        }
      }
    }

    "give payment an 'Upcoming' status" when {
      "the charge has an outstandingAmount" when {
        "the dueDate is today" in {
          val payment = buildPayments(
            outstandingAmount = Some(42),
            clearingDate = None,
            dueDateOverride = Some(today)
          )
          val result = buildViewModel(
            payment = payment,
            clearingDate = None,
            dueDateOverride = Some(today)
          )

          getStatusFromViewModel(result).head shouldBe Upcoming
        }

        "the dueDate is after today" in {
          val payment = buildPayments(
            outstandingAmount = Some(42),
            clearingDate = None,
            dueDateOverride = Some(today.plusWeeks(1))
          )
          val result = buildViewModel(
            payment = payment,
            clearingDate = None,
            dueDateOverride = Some(today.plusWeeks(1))
          )

          getStatusFromViewModel(result).head shouldBe Upcoming
        }
      }
    }

    "give payment an 'Overdue' status" when {
      "the charge has an outstandingAmount" when {
        "the dueDate is before today" in {
          val payments = buildPayments(outstandingAmount = Some(42), dueDateOverride = Some(today.minusWeeks(1)))
          val result   = buildViewModel(payments, dueDateOverride = Some(today.minusWeeks(1)))

          getStatusFromViewModel(result).head shouldBe Overdue
        }
      }
    }

    "treat the schedule that overlaps today as current, even if it started last year" in {
      val today = LocalDate.parse("2020-03-15")

      val pastItems = List(
        RequestItem("1", "18A1", "2018-07-01", "2019-06-30", "2019-06-30", 100, None, None)
      )

      val currentItems = List(
        RequestItem("1", "19A1", "2019-07-01", "2020-06-30", "2020-03-31", 200, None, None)
      )

      val futureItems = List(
        RequestItem("1", "20A1", "2020-07-01", "2021-06-30", "2021-03-31", 300, None, None)
      )

      val standingRequest = StandingRequest(
        processingDate = "2020-01-01",
        standingRequests = List(
          StandingRequestDetail("REQ-PAST", "4", "2018-07-01", Some("2018-07-15"), pastItems),
          StandingRequestDetail("REQ-CURR", "4", "2019-07-01", Some("2019-07-15"), currentItems),
          StandingRequestDetail("REQ-NEXT", "4", "2020-07-01", Some("2020-07-15"), futureItems)
        )
      )

      val vm = AnnualAccountingController.buildViewModel(
        standingRequestResponse = standingRequest,
        today = today,
        returnObligations = None,
        chargesWithDueDates = Seq.empty,
        paymentsDetails = None,
        isAgent = false,
        hasDirectDebit = Some(false),
        displayName = None
      )

      vm.currentPeriods.map(_.startDate.getYear) shouldBe List(2019)
      vm.pastPeriods.map(_.startDate.getYear) shouldBe List(2018)
    }

    "pick the previous accounting period (by date range) as past" in {
      val today = LocalDate.parse("2025-06-15")

      val currentItems = List(
        RequestItem("1", "25A1", "2025-02-01", "2026-01-31", "2025-11-30", 250, None, None)
      )

      val previousItems = List(
        RequestItem("1", "24A1", "2024-02-01", "2025-01-31", "2024-11-30", 225, None, None)
      )

      val standingRequest = StandingRequest(
        processingDate = "2025-01-01",
        standingRequests = List(
          StandingRequestDetail("REQ-PREV", "4", "2024-01-01", Some("2024-01-15"), previousItems),
          StandingRequestDetail("REQ-CURR", "4", "2025-01-01", Some("2025-01-15"), currentItems)
        )
      )

      val vm = AnnualAccountingController.buildViewModel(
        standingRequestResponse = standingRequest,
        today = today,
        returnObligations = None,
        chargesWithDueDates = Seq.empty,
        paymentsDetails = None,
        isAgent = false,
        hasDirectDebit = Some(false),
        displayName = None
      )

      vm.currentPeriods.head.startDate shouldBe LocalDate.parse("2025-02-01")
      vm.pastPeriods.head.startDate shouldBe LocalDate.parse("2024-02-01")
      vm.currentPeriods.head.isCurrent shouldBe true
      vm.pastPeriods.head.isPast shouldBe true
    }
  }

}
