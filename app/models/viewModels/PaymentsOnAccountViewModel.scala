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

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

sealed trait PaymentType {
  def messageKey: String
}

object PaymentType {
  case object FirstPayment extends PaymentType {
    val messageKey = "paymentsOnAccount.paymentType.firstPayment"
  }
  case object SecondPayment extends PaymentType {
    val messageKey = "paymentsOnAccount.paymentType.secondPayment"
  }
  case object ThirdPayment extends PaymentType {
    val messageKey = "paymentsOnAccount.paymentType.thirdPayment"
  }
}

case class DueDate(
  dueDate: Option[LocalDate],
  obligationsDueDate: Option[LocalDate] = None
)

case class PaymentDetail(paymentType: PaymentType, dueDate: DueDate, amount: Option[BigDecimal])

case class VatPeriod(startDate: LocalDate, endDate: LocalDate, payments: List[PaymentDetail], isCurrent: Boolean, isPast: Boolean) {
  def isCurrentOrUpcoming: Boolean = !isPast
}

case class PaymentsOnAccountViewModel(
  breathingSpace: Boolean,
  periods: List[VatPeriod],
  changedOn: Option[LocalDate],
  currentPeriods: List[VatPeriod],
  pastPeriods: List[VatPeriod],
  nextPayment: Option[PaymentDetail],
  displayName: Option[String]
) {
  private val formatter = DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.UK)
  def changedOnFormattedOpt: Option[String] = changedOn.map(_.format(formatter))
}