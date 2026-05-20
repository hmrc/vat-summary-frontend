/*
 * Copyright 2026 HM Revenue & Customs
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

package services

import models.payments.{Payment, PaymentWithPeriod, VatLateSubmissionPen, VatOverpaymentForRPI}
import models.penalties.{LPPDetails, PenaltyDetails}
import models.viewModels.ChargeDetailsViewModel
import models.viewModels.CrystallisedInterestViewModel.buildCrystallisedIntViewModel
import models.viewModels.CrystallisedLPP1ViewModel.buildCrystallisedLPP1ViewModel
import models.viewModels.CrystallisedLPP2ViewModel.buildCrystallisedLPP2ViewModel
import models.viewModels.EstimatedInterestViewModel.buildEstimatedIntViewModel
import models.viewModels.EstimatedLPP1ViewModel.buildEstimatedLPP1ViewModel
import models.viewModels.EstimatedLPP2ViewModel.buildEstimatedLPP2ViewModel
import models.viewModels.LateSubmissionPenaltyViewModel.buildLateSubmissionPenaltyViewModel
import models.viewModels.StandardChargeViewModel.buildStandardChargeViewModel
import models.viewModels.VatOverpaymentForRPIViewModel.buildVatOverpaymentForRPIViewModel
import play.api.mvc.Request
import utils.LoggerUtil

import java.time.LocalDate
import javax.inject.Inject

class WYOViewService @Inject()(dateService: DateService) extends LoggerUtil{

  def categoriseCharges(payments: Seq[Payment], penalties: PenaltyDetails,
                        ddStatus: Boolean): Seq[Option[ChargeDetailsViewModel]] =
    payments.collect {
      case p: PaymentWithPeriod if p.chargeType.isPenalty =>
        val matchingPenalty = findPenaltyCharge(p.chargeReference, p.penaltyType, isEstimate = false, penalties.LPPDetails)
        buildCrystallisedChargePlusEstimates(p, matchingPenalty, ddStatus)
      case p: PaymentWithPeriod if p.chargeType.isLPICharge =>
        Seq(buildCrystallisedInterest(p, ddStatus, dateService.now()))
      case p: PaymentWithPeriod if p.chargeType.eq(VatLateSubmissionPen) =>
        buildLSPPlusEstimates(p, ddStatus)
      case p: PaymentWithPeriod if p.chargeType.eq(VatOverpaymentForRPI) =>
        Seq(buildVatOverpaymentForRPIViewModel(p, ddStatus, dateService.now()))
      case p =>
        buildStandardChargePlusEstimates(p, penalties, ddStatus)
    }.flatten


  def findPenaltyCharge(chargeReference: Option[String],
                        penaltyType: Option[String],
                        isEstimate: Boolean,
                        penalties: Seq[LPPDetails]): Option[LPPDetails] =
    penalties.find(pen => {
      if (isEstimate) {
        pen.principalChargeReference == chargeReference.getOrElse("") && penaltyType.getOrElse("") == pen.penaltyCategory
      } else {
        if (pen.penaltyChargeReference.isEmpty && chargeReference.isEmpty) {
          logger.info("[WhatYouOweController][findPenaltyCharge] both penaltyChargeReference and chargeReference are None")
        }
        pen.penaltyChargeReference == chargeReference
      }
    })

  def buildCrystallisedChargePlusEstimates(charge: PaymentWithPeriod,
                                           matchingPenalty: Option[LPPDetails],
                                           ddStatus: Boolean): Seq[Option[ChargeDetailsViewModel]] =
    if (charge.showEstimatedInterest) {
      Seq(buildCrystallisedLPPViewModel(charge, matchingPenalty, ddStatus), buildEstimatedInterest(charge, ddStatus))
    } else {
      Seq(buildCrystallisedLPPViewModel(charge, matchingPenalty, ddStatus))
    }

  def buildStandardChargePlusEstimates(charge: Payment,
                               penalties: PenaltyDetails,
                               ddStatus: Boolean): Seq[Option[ChargeDetailsViewModel]] =
    charge match {
      case p: PaymentWithPeriod if p.showEstimatedInterest && p.showEstimatedPenalty =>
        val matchingPenalty = findPenaltyCharge(charge.chargeReference, charge.penaltyType, isEstimate = true, penalties.LPPDetails)
        Seq(buildStandardChargeViewModel(p, ddStatus, dateService.now()), buildEstimatedInterest(p, ddStatus), buildEstimatedLPPViewModel(p, matchingPenalty, penalties.breathingSpace, ddStatus))
      case p: PaymentWithPeriod if p.showEstimatedPenalty =>
        val matchingPenalty = findPenaltyCharge(charge.chargeReference, charge.penaltyType, isEstimate = true, penalties.LPPDetails)
        Seq(buildStandardChargeViewModel(p, ddStatus, dateService.now()), buildEstimatedLPPViewModel(p, matchingPenalty, penalties.breathingSpace, ddStatus))
      case p: PaymentWithPeriod if p.showEstimatedInterest =>
        Seq(buildStandardChargeViewModel(p, ddStatus, dateService.now()), buildEstimatedInterest(p, ddStatus))
      case _ =>
        Seq(buildStandardChargeViewModel(charge, ddStatus, dateService.now()))
    }

  def buildLSPPlusEstimates(charge: PaymentWithPeriod,
                            ddStatus: Boolean): Seq[Option[ChargeDetailsViewModel]] =
    if (charge.showEstimatedInterest) {
      Seq(buildLateSubmissionPenalty(charge, ddStatus, dateService.now()), buildEstimatedInterest(charge, ddStatus))
    } else {
      Seq(buildLateSubmissionPenalty(charge, ddStatus, dateService.now()))
    }

  def buildCrystallisedLPPViewModel(payment: PaymentWithPeriod,
                                    penaltyDetails: Option[LPPDetails],
                                    ddStatus: Boolean): Option[ChargeDetailsViewModel] =
    penaltyDetails.flatMap { p =>
      p.penaltyCategory match {
        case "LPP1" => buildCrystallisedLPP1ViewModel(payment, penaltyDetails, ddStatus, dateService.now())
        case "LPP2" => buildCrystallisedLPP2ViewModel(payment, penaltyDetails, ddStatus, dateService.now())
        case _ =>
          val loggerMessage = (penaltyDetails, payment.chargeReference) match {
            case (Some(penDetails), _) => s"[WhatYouOweController][buildCrystallisedLPPViewModel] - " +
              s"Missing LPP details for ${penDetails.penaltyCategory} penalty type"
            case _ =>
              val missingData = if (payment.chargeReference.isDefined) s"matching penalty for charge type: ${payment.chargeType}" else "charge reference"
              s"[WhatYouOweController][buildCrystallisedLPPViewModel] - Missing $missingData"
          }
          logger.warn(loggerMessage)
          None
      }
    }

  def buildEstimatedLPPViewModel(payment: PaymentWithPeriod,
                                 penaltyDetails: Option[LPPDetails],
                                 breathingSpace:Boolean,
                                 ddStatus: Boolean): Option[ChargeDetailsViewModel] =
    penaltyDetails.flatMap { p =>
      p.penaltyCategory match {
        case "LPP1" => buildEstimatedLPP1ViewModel(payment, penaltyDetails, breathingSpace, ddStatus)
        case "LPP2" => buildEstimatedLPP2ViewModel(payment, penaltyDetails, breathingSpace, ddStatus)
        case _ =>
          val loggerMessage = (penaltyDetails, payment.accruingPenaltyAmount) match {
          case (Some(pen), Some(_)) => s"Missing particular LPP details for ${pen.penaltyCategory} penalty type"
          case (None, Some(_)) => s"No matching penalty was found for charge type: ${payment.chargeType}"
          case _ => "Accruing penalty amount was not found"
        }
          logger.warn(s"[WhatYouOweController][buildEstimatedLPPViewModel] - $loggerMessage")
          None
      }
    }

  def buildEstimatedInterest(payment: PaymentWithPeriod, ddStatus: Boolean): Option[ChargeDetailsViewModel] = {
    payment.accruingInterestAmount match {
      case Some(interestAmnt) => buildEstimatedIntViewModel(payment, ddStatus, interestAmnt)
      case _ =>
        logger.warn("[WhatYouOweController][buildEstimatedIntViewModel] - Missing accrued interest amount")
        None
    }
  }

  def buildCrystallisedInterest(payment: PaymentWithPeriod, ddStatus: Boolean, today: LocalDate): Option[ChargeDetailsViewModel] = {
    payment.chargeReference match {
      case Some(chargeRef) => buildCrystallisedIntViewModel(payment, ddStatus, today, chargeRef)
      case _ =>
        logger.warn("[WhatYouOweController][buildCrystallisedIntViewModel] - Missing charge reference")
        None
    }
  }

  def buildLateSubmissionPenalty(payment: PaymentWithPeriod, ddStatus: Boolean, today: LocalDate): Option[ChargeDetailsViewModel] = {
    payment.chargeReference match {
      case Some(chargeRef) => buildLateSubmissionPenaltyViewModel(payment, ddStatus, today, chargeRef)
      case _ =>
        logger.warn("[WhatYouOweController][buildLateSubmissionPenaltyViewModel] - Missing charge reference")
        None
    }
  }
}
