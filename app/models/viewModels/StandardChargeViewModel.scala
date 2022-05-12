/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.data.Form
import play.api.data.Forms._
import models.payments._
import play.api.i18n.Messages
import utils.LoggerUtil
import views.templates.payments.PaymentMessageHelper

import java.time.LocalDate

case class StandardChargeViewModel(chargeType: String,
                                   outstandingAmount: BigDecimal,
                                   originalAmount: BigDecimal,
                                   clearedAmount: Option[BigDecimal],
                                   dueDate: LocalDate,
                                   periodKey: Option[String],
                                   isOverdue: Boolean,
                                   chargeReference: Option[String],
                                   periodFrom: Option[LocalDate],
                                   periodTo: Option[LocalDate]) extends ChargeDetailsViewModel with LoggerUtil {

  val makePaymentRedirect: String = periodTo match {
    case Some(date) =>
      controllers.routes.MakePaymentController.makePayment(
        amountInPence = (outstandingAmount * 100).toLong,
        taxPeriodMonth = date.getMonthValue,
        taxPeriodYear = date.getYear,
        vatPeriodEnding = date.toString,
        chargeType = chargeType,
        dueDate = dueDate.toString,
        chargeReference = chargeReference.getOrElse("noCR")
      ).url
    case None =>
      controllers.routes.MakePaymentController.makePaymentNoPeriod(
        amountInPence = (outstandingAmount * 100).toLong,
        chargeType = chargeType,
        dueDate = dueDate.toString,
        chargeReference = chargeReference.getOrElse("noCR")
      ).url
  }

  def title(implicit messages: Messages): String = messages(PaymentMessageHelper.getChargeType(chargeType).title)

  def description(isAgent: Boolean)(implicit messages: Messages): String = {
    val message = PaymentMessageHelper.getCorrectDescription(
      PaymentMessageHelper.getChargeType(chargeType).principalUserDescription.getOrElse(""),
      PaymentMessageHelper.getChargeType(chargeType).agentDescription.getOrElse(""),
      periodFrom,
      periodTo,
      isAgent
    )
    if (message.contains("{0}")) {
      logger.warn(s"[WhatYouOweChargeHelper][description] - No date period was found for $chargeType. Omitting description.")
      ""
    } else {
      message
    }
  }
}

object StandardChargeViewModel extends LoggerUtil {

  val form: Form[StandardChargeViewModel] = Form(mapping(
    "chargeType" -> text,
    "outstandingAmount" -> bigDecimal,
    "originalAmount" -> bigDecimal,
    "clearedAmount" -> optional(bigDecimal),
    "dueDate" -> localDate,
    "periodKey" -> optional(text),
    "isOverdue" -> boolean,
    "chargeReference" -> optional(text),
    "periodFrom" -> optional(localDate),
    "periodTo" -> optional(localDate)
  )(StandardChargeViewModel.apply)(StandardChargeViewModel.unapply))

  def periodFrom(payment: Payment): Option[LocalDate] = payment match {
    case p: PaymentWithPeriod => Some(p.periodFrom)
    case _ => None
  }

  def periodTo(payment: Payment): Option[LocalDate] = payment match {
    case p: PaymentWithPeriod => Some(p.periodTo)
    case _ => None
  }

  def viewReturnEnabled(chargeValue: String): Boolean = ChargeType.apply(chargeValue) match {
    case ReturnDebitCharge |
         ErrorCorrectionDebitCharge |
         PaymentOnAccountReturnDebitCharge |
         AAReturnDebitCharge => true
    case _ => false
  }
}
