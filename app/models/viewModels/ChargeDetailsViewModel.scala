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

import play.api.i18n.Messages
import views.templates.payments.PaymentMessageHelper

import java.security.MessageDigest
import java.time.LocalDate

trait ChargeDetailsViewModel {
  val chargeType: String
  val outstandingAmount: BigDecimal
  val directDebitMandateFound: Boolean

  def title(implicit messages: Messages): String = messages(PaymentMessageHelper.getChargeType(chargeType).title)
  def description(isAgent: Boolean)(implicit messages: Messages): String

  def generateHash(vrn: String): String =
    MessageDigest.getInstance("MD5").digest((this.toString + vrn).getBytes("UTF-8")).map("%02x".format(_)).mkString
}

trait ChargeDetailsViewModelWithDueDate extends ChargeDetailsViewModel {
  val dueDate: LocalDate
}

trait CrystallisedViewModel extends ChargeDetailsViewModelWithDueDate {
  val isOverdue: Boolean
  val periodFrom: LocalDate
  val periodTo: LocalDate
}

trait EstimatedViewModel extends ChargeDetailsViewModel {
  val periodFrom: LocalDate
  val periodTo: LocalDate
}
