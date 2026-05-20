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
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDate
import play.api.i18n.Messages
import views.templates.payments.PaymentMessageHelper

case class EstimatedLPP1ViewModel(part1Days: String,
                                  part2Days: String,
                                  part1PenaltyRate: BigDecimal,
                                  part2PenaltyRate: BigDecimal,
                                  part1UnpaidVAT: BigDecimal,
                                  penaltyAmount: BigDecimal,
                                  periodFrom: LocalDate,
                                  periodTo: LocalDate,
                                  chargeType: String,
                                  timeToPayPlan: Boolean,
                                  breathingSpace: Boolean,
                                  directDebitMandateFound: Boolean) extends   EstimatedViewModel {

  override val outstandingAmount: BigDecimal = penaltyAmount

  override def description(isAgent: Boolean)(implicit messages: Messages): String = PaymentMessageHelper.getCorrectDescription(
    PaymentMessageHelper.getChargeType(chargeType).principalUserDescription.getOrElse(""),
    PaymentMessageHelper.getChargeType(chargeType).agentDescription.getOrElse(""),
    Some(periodFrom),
    Some(periodTo),
    isAgent
  )

}

object EstimatedLPP1ViewModel {

  def buildEstimatedLPP1ViewModel(payment: PaymentWithPeriod,
                                 penaltyDetails: Option[LPPDetails],
                                 breathingSpace: Boolean,
                                 ddStatus: Boolean): Option[ChargeDetailsViewModel] =
    (penaltyDetails, payment.accruingPenaltyAmount) match {
      case (Some(LPPDetails(_, "LPP1", Some(calcAmountLR), Some(daysLR), Some(rateLR), _, Some(daysHR), _, _, _, _, timeToPay)), Some(penaltyAmnt)) =>
        Some(EstimatedLPP1ViewModel(
          part1Days = daysLR,
          part2Days = daysHR,
          part1PenaltyRate = rateLR,
          part2PenaltyRate = penaltyDetails match {
            case Some(LPPDetails(_, "LPP1", _, _, _, _, _, Some(rateHR), _, _, _, _)) => rateHR
            case _ => rateLR
          },
          part1UnpaidVAT = calcAmountLR,
          penaltyAmount = penaltyAmnt,
          periodFrom = payment.periodFrom,
          periodTo = payment.periodTo,
          chargeType = ChargeType.penaltyChargeMappingLPP1(payment.chargeType).value,
          timeToPayPlan = timeToPay,
          breathingSpace = breathingSpace,
          directDebitMandateFound = ddStatus
        ))
      case _ => None
    }

  implicit val format: OFormat[EstimatedLPP1ViewModel] = Json.format[EstimatedLPP1ViewModel]
}
