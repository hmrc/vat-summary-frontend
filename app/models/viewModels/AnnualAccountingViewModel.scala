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

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

sealed trait AAPaymentStatus {
  def tagClass: String
  def messageKey: String
}

object AAPaymentStatus {
  case object Upcoming extends AAPaymentStatus {
    val tagClass: String = "govuk-tag--grey"
    val messageKey: String = "annualAccounting.status.upcoming"
  }
  case object Paid extends AAPaymentStatus {
    val tagClass: String = "govuk-tag--green"
    val messageKey: String = "annualAccounting.status.paid"
  }
  case object PaidLate extends AAPaymentStatus {
    val tagClass: String = "govuk-tag--yellow"
    val messageKey: String = "annualAccounting.status.paidLate"
  }
  case object Overdue extends AAPaymentStatus {
    val tagClass: String = "govuk-tag--red"
    val messageKey: String = "annualAccounting.status.overdue"
  }
}

sealed trait PaymentFrequency { def instalments: Int }
object PaymentFrequency {
  case object Monthly extends PaymentFrequency { val instalments: Int = 9 }
  case object Quarterly extends PaymentFrequency { val instalments: Int = 3 }
}

case class AAPayment(isBalancing: Boolean, dueDate: LocalDate, amount: Option[BigDecimal], status: AAPaymentStatus)

case class AASchedulePeriod(startDate: LocalDate, endDate: LocalDate, payments: List[AAPayment], isCurrent: Boolean, isPast: Boolean)

case class AnnualAccountingViewModel(
  changedOn: Option[LocalDate],
  currentPeriods: List[AASchedulePeriod],
  pastPeriods: List[AASchedulePeriod],
  nextPayment: Option[AAPayment],
  isAgent: Boolean,
  hasDirectDebit: Option[Boolean],
  frequency: Option[PaymentFrequency] = None
) {
  private val formatter = DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.UK)
  def changedOnFormattedOpt: Option[String] = changedOn.map(_.format(formatter))
}
