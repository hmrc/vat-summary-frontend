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

import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.util.Locale

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

case class PaymentDetail(paymentType: PaymentType, dueDate: Option[LocalDate], amount: Option[BigDecimal]) {
  private val formatter = DateTimeFormatter.ofPattern("d MMM uuuu", Locale.UK)
  def formattedDueDateOrPending: String = {
    if (paymentType == PaymentType.ThirdPayment) "Pending"
    else dueDate.map(_.format(formatter)).getOrElse("Pending")
  }
}

case class VatPeriod(startDate: LocalDate, endDate: LocalDate, payments: List[PaymentDetail], isCurrent: Boolean, isPast: Boolean) {
  private val formatter = DateTimeFormatter.ofPattern("d MMMM uuuu")
  def title: String = s"${startDate.format(formatter)} to ${endDate.format(formatter)}"
  def isCurrentOrUpcoming: Boolean = !isPast
}

case class PaymentsOnAccountViewModel(
  breathingSpace: Boolean,
  periods: List[VatPeriod],
  changedOn: Option[LocalDate],
  currentPeriods: List[VatPeriod],
  pastPeriods: List[VatPeriod],
  nextPayment: Option[PaymentDetail],
) {
  private val formatter = DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.UK)
  def changedOnFormattedOpt: Option[String] = changedOn.map(_.format(formatter))
}