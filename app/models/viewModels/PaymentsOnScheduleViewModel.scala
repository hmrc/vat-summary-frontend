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

import utils.LocalDateHelper
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import models._
import java.util.Locale
import config.AppConfig

sealed trait PaymentType {
  def displayName: String
}

object PaymentType {
  case object FirstPayment extends PaymentType {
    val displayName = "1st payment due"
  }
  case object SecondPayment extends PaymentType {
    val displayName = "2nd payment due"
  }
  case object ThirdPayment extends PaymentType {
    val displayName = "3rd payment due"
  }
}

case class PaymentDetail(paymentType: PaymentType, dueDate: Option[LocalDate], amount: String){
  private val formatter = DateTimeFormatter.ofPattern("d MMM uuuu", Locale.UK)
  def formattedDueDateOrPending: String = dueDate.map(_.format(formatter)).getOrElse("Pending")
}

case class VatPeriod(title: String, startDate: LocalDate, endDate: LocalDate, payments: List[PaymentDetail]) {
  def year: Int = startDate.getYear
  def isPast: Boolean = endDate.isBefore(LocalDate.now().minusDays(35))
  def isCurrentOrUpcoming: Boolean = !isPast
  def isCurrent: Boolean = {
    val today = LocalDate.now()
    val visibleStart = startDate.plusDays(35)
    val visibleEnd = endDate.plusDays(35)

    today.isAfter(visibleStart.minusDays(1)) && today.isBefore(visibleEnd.plusDays(1))
  }
}

case class PaymentsOnScheduleViewModel(
  breathingSpace: Boolean,
  periods: List[VatPeriod],
  changedOn: Option[LocalDate]
) {
  private val formatter = DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.UK)
  def currentPeriods: List[VatPeriod] = periods.filter(_.isCurrentOrUpcoming)
  def pastPeriods: List[VatPeriod] = periods.filter(_.isPast)

  def changedOnFormattedOpt: Option[String] = changedOn.map(_.format(formatter))
    
  def nextPayment: Option[PaymentDetail] = {
    val today = LocalDate.now()

    periods
      .flatMap(_.payments)
      .filter { payment =>
        payment.dueDate.exists(_.isAfter(today)) || 
        (payment.paymentType == PaymentType.ThirdPayment && isBalancingPayment) 
      }
      .sortBy(_.dueDate.getOrElse(LocalDate.MAX))
      .headOption
  }

  def isBalancingPayment: Boolean = {
    val today = LocalDate.now()

    periods.exists { period =>
      val secondPaymentHasPassed = period.payments.exists(p =>
        p.paymentType == PaymentType.SecondPayment && p.dueDate.exists(_.isBefore(today))
      )

      val thirdPaymentIsNext = period.payments.exists(p =>
        p.paymentType == PaymentType.ThirdPayment && p.dueDate.exists(_.isAfter(today))
      )

      secondPaymentHasPassed && thirdPaymentIsNext
    }
  }
}

object PaymentsOnScheduleViewModel {

  def fromViewModelFromVATStandingRequest(apiResponse: VATStandingRequest): PaymentsOnScheduleViewModel = {
    
    val formatter = DateTimeFormatter.ofPattern("d MMM uuuu", Locale.UK)

    val standingRequests = apiResponse.response.standingRequests

    val mostRecentChangedOn: Option[LocalDate] = apiResponse.response.standingRequests
      .flatMap(req => Some(LocalDate.parse(req.changedOn.trim))) 
      .sorted(Ordering[LocalDate].reverse) 
      .headOption

    val periods = standingRequests
      .flatMap(_.requestItems)
      .groupBy(_.periodKey)
      .toSeq.sortBy(_._1)
      .map { case (periodKey, items) =>

        val sortedPayments = items.sortBy(_.period) 

        val paymentsWithPlaceholder = {
          val actualPayments = sortedPayments.take(2).zipWithIndex.map { case (item, index) =>
              PaymentDetail(
                paymentType = if (index == 0) PaymentType.FirstPayment else PaymentType.SecondPayment,
                dueDate = Some(LocalDate.parse(item.dueDate)),
                amount = f"£${item.amount}%.2f"
              )
          }

          val thirdPayment = PaymentDetail(
            paymentType = PaymentType.ThirdPayment,
            dueDate = None,
            amount = "Balance"
          )

          actualPayments :+ thirdPayment
        }

        val startDate = LocalDate.parse(items.head.startDate)
        val endDate = LocalDate.parse(items.head.endDate)

        VatPeriod(
          title = s"${startDate.format(formatter)} to ${endDate.format(DateTimeFormatter.ofPattern("MMMM uuuu"))}",
          startDate = startDate,
          endDate = endDate,
          payments = paymentsWithPlaceholder
        )
      }

    PaymentsOnScheduleViewModel(
      breathingSpace = false, 
      periods = periods.toList,
      changedOn = mostRecentChangedOn
    )
  }
}