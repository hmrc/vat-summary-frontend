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

import play.api.i18n.Messages
import views.templates.payments.PaymentMessageHelper

import java.time.LocalDate
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.mapping
import play.api.libs.json.{Json, OFormat}

case class CrystallisedLPP1ViewModel(numberOfDays: String,
                                     part1Days: String,
                                     part2Days: Option[String],
                                     part1PenaltyRate: BigDecimal,
                                     part2PenaltyRate: Option[BigDecimal],
                                     part1UnpaidVAT: BigDecimal,
                                     part2UnpaidVAT: Option[BigDecimal],
                                     dueDate: LocalDate,
                                     penaltyAmount: BigDecimal,
                                     amountReceived: BigDecimal,
                                     leftToPay: BigDecimal,
                                     periodFrom: LocalDate,
                                     periodTo: LocalDate,
                                     chargeType: String,
                                     chargeReference: String,
                                     isOverdue: Boolean) extends ChargeDetailsViewModel {

  override val outstandingAmount: BigDecimal = leftToPay

  val makePaymentRedirect: String = controllers.routes.MakePaymentController.makePayment(
    amountInPence = (leftToPay * 100).toLong,
    taxPeriodMonth = periodTo.getMonthValue,
    taxPeriodYear = periodTo.getYear,
    vatPeriodEnding = periodTo.toString,
    chargeType = chargeType,
    dueDate = dueDate.toString,
    chargeReference = chargeReference
  ).url

  def title(implicit messages: Messages): String = messages(PaymentMessageHelper.getChargeType(chargeType).title)
}

object CrystallisedLPP1ViewModel {

  implicit val format: OFormat[CrystallisedLPP1ViewModel] = Json.format[CrystallisedLPP1ViewModel]

  val form: Form[CrystallisedLPP1ViewModel] = Form(mapping(
    "numberOfDays" -> text,
    "part1Days" -> text,
    "part2Days" -> optional(text),
    "part1PenaltyRate" -> bigDecimal,
    "part2PenaltyRate" -> optional(bigDecimal),
    "part1UnpaidVAT" -> bigDecimal,
    "part2UnpaidVAT" -> optional(bigDecimal),
    "dueDate" -> localDate,
    "penaltyAmount" -> bigDecimal,
    "amountReceived" -> bigDecimal,
    "leftToPay" -> bigDecimal,
    "periodFrom" -> localDate,
    "periodTo" -> localDate,
    "chargeType" -> text,
    "chargeReference" -> text,
    "isOverdue" -> boolean
  )(CrystallisedLPP1ViewModel.apply)(CrystallisedLPP1ViewModel.unapply))

}