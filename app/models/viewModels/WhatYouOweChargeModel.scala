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
import java.time.LocalDate

import models.payments._
import play.api.i18n.Messages
import utils.LoggerUtil
import views.templates.payments.PaymentMessageHelper

case class WhatYouOweChargeModel(chargeValue: String,
                                 chargeDescription: String,
                                 chargeTitle: String,
                                 outstandingAmount: BigDecimal,
                                 originalAmount: BigDecimal,
                                 clearedAmount: Option[BigDecimal],
                                 dueDate: LocalDate,
                                 periodKey: Option[String],
                                 isOverdue: Boolean,
                                 chargeReference: Option[String],
                                 makePaymentRedirect: String,
                                 periodFrom: Option[LocalDate],
                                 periodTo: Option[LocalDate])

object WhatYouOweChargeModel extends LoggerUtil {

  val form: Form[WhatYouOweChargeModel] = Form(mapping(
    "chargeValue" -> text,
    "chargeDescription" -> text,
    "chargeTitle" -> text,
    "outstandingAmount" -> bigDecimal,
    "originalAmount" -> bigDecimal,
    "clearedAmount" -> optional(bigDecimal),
    "dueDate" -> localDate,
    "periodKey" -> optional(text),
    "isOverdue" -> boolean,
    "chargeReference" -> optional(text),
    "makePaymentRedirect" -> text,
    "periodFrom" -> optional(localDate),
    "periodTo" -> optional(localDate)
  )(WhatYouOweChargeModel.apply)(WhatYouOweChargeModel.unapply))

  def makePaymentRedirect(payment: Payment): String = payment match {
    case p: PaymentWithPeriod =>
      controllers.routes.MakePaymentController.makePayment(
        amountInPence = (p.outstandingAmount * 100).toLong,
        taxPeriodMonth = p.periodTo.getMonthValue,
        taxPeriodYear = p.periodTo.getYear,
        vatPeriodEnding = p.periodTo.toString,
        chargeType = p.chargeType.value,
        dueDate = p.due.toString,
        chargeReference = p.chargeReference.getOrElse("noCR")
      ).url
    case p: PaymentNoPeriod =>
      controllers.routes.MakePaymentController.makePaymentNoPeriod(
        amountInPence = (p.outstandingAmount * 100).toLong,
        chargeType = p.chargeType.value,
        dueDate = p.due.toString,
        chargeReference = p.chargeReference.getOrElse("noCR")
      ).url
  }

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

  def description(payment: Payment, userIsAgent: Boolean)(implicit messages: Messages): Option[String] = {

    val helper: PaymentMessageHelper = PaymentMessageHelper.getChargeType(payment.chargeType.value)

    (payment, helper.principalUserDescription, helper.agentDescription) match {
      case (payment: PaymentWithPeriod, Some(principalDesc), Some(agentDesc)) =>
        Some(PaymentMessageHelper.getCorrectDescription(principalDesc, agentDesc, Some(payment.periodFrom), Some(payment.periodTo), userIsAgent))
      case (_: PaymentNoPeriod, Some(principalDesc), Some(agentDesc)) =>
        val descriptionText = PaymentMessageHelper.getCorrectDescription(principalDesc, agentDesc, None, None, userIsAgent)
        if (descriptionText.contains("{0}")) {
          logger.warn("[WhatYouOweChargeHelper][description] - " +
            s"No date period was found for ${payment.chargeType}. Omitting description.")
          None
        } else {
          Some(descriptionText)
        }
      case (_, _, _) => None
    }
  }

  def title(chargeValue: String)(implicit messages: Messages): String = {
    messages(PaymentMessageHelper.getChargeType(chargeValue).title)
  }

}
