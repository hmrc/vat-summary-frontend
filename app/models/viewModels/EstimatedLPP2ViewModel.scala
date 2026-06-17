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

import models.payments.{ChargeType, PaymentWithPeriod}
import models.penalties.LPPDetails
import play.api.i18n.Messages
import play.api.libs.json.{Json, OFormat}
import views.templates.payments.PaymentMessageHelper

import java.time.LocalDate

case class EstimatedLPP2ViewModel(day: String,
                                  penaltyRate: BigDecimal,
                                  penaltyAmount: BigDecimal,
                                  periodFrom: LocalDate,
                                  periodTo: LocalDate,
                                  chargeType: String,
                                  timeToPay: Boolean,
                                  breathingSpace: Boolean,
                                  directDebitMandateFound: Boolean) extends EstimatedViewModel {

  override val outstandingAmount: BigDecimal = penaltyAmount
  override def description(isAgent: Boolean)(implicit messages: Messages): String = PaymentMessageHelper.getCorrectDescription(
    PaymentMessageHelper.getChargeType(chargeType).principalUserDescription.getOrElse(""),
    PaymentMessageHelper.getChargeType(chargeType).agentDescription.getOrElse(""),
    Some(periodFrom),
    Some(periodTo),
    isAgent
  )
}

object EstimatedLPP2ViewModel {

  def buildEstimatedLPP2ViewModel(payment: PaymentWithPeriod,
                                 penaltyDetails: Option[LPPDetails],
                                 breathingSpace: Boolean,
                                 ddStatus: Boolean): Option[ChargeDetailsViewModel] =
    (penaltyDetails, payment.accruingPenaltyAmount) match {
      case (Some(LPPDetails(_, "LPP2", _, _, _, _, _, _, Some(daysLPP2), Some(rateLPP2), _, timeToPay)), Some(penaltyAmnt)) =>
        Some(EstimatedLPP2ViewModel(
          day = daysLPP2,
          penaltyRate = rateLPP2,
          penaltyAmount = penaltyAmnt,
          periodFrom = payment.periodFrom,
          periodTo = payment.periodTo,
          chargeType = ChargeType.penaltyChargeMappingLPP2(payment.chargeType).value,
          timeToPay = timeToPay,
          breathingSpace = breathingSpace,
          directDebitMandateFound = ddStatus
        ))
      case _ => None
    }

  implicit val format: OFormat[EstimatedLPP2ViewModel] = Json.format[EstimatedLPP2ViewModel]

}
