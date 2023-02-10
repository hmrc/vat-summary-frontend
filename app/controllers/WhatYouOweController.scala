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

package controllers

import com.google.inject.Inject
import common.SessionKeys
import config.AppConfig
import models.payments.{ChargeType, Payment, PaymentWithPeriod, VatLateSubmissionPen}
import models.penalties.LPPDetails
import models.viewModels.StandardChargeViewModel.{periodFrom, periodTo}
import models.viewModels._
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.errors.PaymentsError
import views.html.payments.{NoPayments, WhatYouOwe}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class WhatYouOweController @Inject()(authorisedController: AuthorisedController,
                                     dateService: DateService,
                                     paymentsService: PaymentsService,
                                     serviceInfoService: ServiceInfoService,
                                     mcc: MessagesControllerComponents,
                                     paymentsError: PaymentsError,
                                     view: WhatYouOwe,
                                     noPayments: NoPayments,
                                     accountDetailsService: AccountDetailsService,
                                     penaltyDetailsService: PenaltyDetailsService,
                                     WYOSessionService: WYOSessionService)
                                    (implicit ec: ExecutionContext,
                                     appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  def show: Action[AnyContent] = authorisedController.financialAction { implicit request =>

    implicit user =>
      serviceInfoService.getPartial.flatMap { serviceInfoContent =>
        paymentsService.getOpenPayments(user.vrn).flatMap { payments =>
          accountDetailsService.getAccountDetails(user.vrn).flatMap { accountDetails =>
            val mandationStatus = accountDetails.map(_.mandationStatus).getOrElse("")

            penaltyDetailsService.getPenaltyDetails(user.vrn).flatMap { penaltyDetails =>
              (payments, penaltyDetails) match {
                case (Right(Some(payments)), Right(penalties)) =>
                  constructViewModel(payments.financialTransactions, mandationStatus, penalties.LPPDetails) match {
                    case Some(model) =>
                      WYOSessionService.storeChargeModels(model.charges,user.vrn).map { _ =>
                        Ok(view(model, serviceInfoContent))
                      }
                    case None =>
                      logger.warn("[WhatYouOweController][show] incorrect fields received for payment(s); failed to render view")
                      Future.successful(InternalServerError(paymentsError()))
                  }
                case (Right(_), _) =>
                  val clientName = request.session.get(SessionKeys.mtdVatvcAgentClientName)
                  Future.successful(Ok(noPayments(user, serviceInfoContent, clientName, mandationStatus)))
                case (Left(_), _) | (_, Left(_)) =>
                  logger.warn(s"[WhatYouOweController][show] Error retrieving data from financial or penalty API")
                  Future.successful(InternalServerError(paymentsError()))
              }
            }
          }
        }
      }
  }

  private[controllers] def categoriseCharges(payments: Seq[Payment], penalties: Seq[LPPDetails]): Seq[Option[ChargeDetailsViewModel]] =
    payments collect {
      case p: PaymentWithPeriod if p.chargeType.isPenalty =>
        val matchingPenalty = findPenaltyCharge(p.chargeReference, p.penaltyType, isEstimate = false, penalties)
        buildCrystallisedChargePlusEstimates(p, matchingPenalty)
      case p: PaymentWithPeriod if p.chargeType.isInterest => Seq(buildCrystallisedIntViewModel(p))
      case p: PaymentWithPeriod if p.chargeType.eq(VatLateSubmissionPen) => Seq(buildLateSubmissionPenaltyViewModel(p))
      case p => buildChargePlusEstimates(p, penalties)
    } flatten

  private[controllers] def buildCrystallisedChargePlusEstimates(charge: PaymentWithPeriod, matchingPenalty: Option[LPPDetails])
    : Seq[Option[ChargeDetailsViewModel]] =
      if (charge.showEstimatedInterest) {
        Seq(buildCrystallisedLPPViewModel(charge, matchingPenalty), buildEstimatedIntViewModel(charge))
      } else {
        Seq(buildCrystallisedLPPViewModel(charge, matchingPenalty))
      }

  private[controllers] def buildChargePlusEstimates(charge: Payment,
                                                    penalties: Seq[LPPDetails]): Seq[Option[ChargeDetailsViewModel]] =
    charge match {
      case p: PaymentWithPeriod if p.showEstimatedInterest && p.showEstimatedPenalty =>
        val matchingPenalty = findPenaltyCharge(charge.chargeReference, charge.penaltyType, isEstimate = true, penalties)
        Seq(buildStandardChargeViewModel(p), buildEstimatedIntViewModel(p), buildEstimatedLPPViewModel(p, matchingPenalty))
      case p: PaymentWithPeriod if p.showEstimatedPenalty =>
        val matchingPenalty = findPenaltyCharge(charge.chargeReference, charge.penaltyType, isEstimate = true, penalties)
        Seq(buildStandardChargeViewModel(p), buildEstimatedLPPViewModel(p, matchingPenalty))
      case p: PaymentWithPeriod if p.showEstimatedInterest =>
        Seq(buildStandardChargeViewModel(p), buildEstimatedIntViewModel(p))
      case _ =>
        Seq(buildStandardChargeViewModel(charge))
    }

  private[controllers] def buildStandardChargeViewModel(payment: Payment): Option[StandardChargeViewModel] =
    Some(StandardChargeViewModel(
      chargeType = payment.chargeType.value,
      outstandingAmount = payment.outstandingAmount,
      originalAmount = payment.originalAmount,
      clearedAmount = payment.clearedAmount.getOrElse(0),
      dueDate = payment.due,
      periodKey = payment.periodKey,
      isOverdue = payment.isOverdue(dateService.now()),
      chargeReference = payment.chargeReference,
      periodFrom = periodFrom(payment),
      periodTo = periodTo(payment)
    ))

  private[controllers] def buildEstimatedIntViewModel(payment: PaymentWithPeriod): Option[EstimatedInterestViewModel] =
    payment.accruingInterestAmount match {
      case Some(interestAmnt) =>
        Some(EstimatedInterestViewModel(
          periodFrom = payment.periodFrom,
          periodTo = payment.periodTo,
          chargeType = ChargeType.interestChargeMapping(payment.chargeType).value,
          interestAmount = interestAmnt,
          isPenalty = ChargeType.interestChargeMapping(payment.chargeType).isPenaltyInterest
        ))
      case _ =>
        logger.warn("[WhatYouOweController][buildEstimatedIntViewModel] - Missing accrued interest amount")
        None
    }

  private[controllers] def buildCrystallisedIntViewModel(payment: PaymentWithPeriod): Option[CrystallisedInterestViewModel] =
    payment.chargeReference match {
      case Some(chargeRef) =>
        Some(CrystallisedInterestViewModel(
          periodFrom = payment.periodFrom,
          periodTo = payment.periodTo,
          chargeType = payment.chargeType.value,
          dueDate = payment.due,
          interestAmount = payment.originalAmount,
          amountReceived = payment.clearedAmount.getOrElse(0),
          leftToPay = payment.outstandingAmount,
          isOverdue = payment.isOverdue(dateService.now()),
          chargeReference = chargeRef,
          isPenalty = payment.chargeType.isPenaltyInterest
        ))
      case _ =>
        logger.warn("[WhatYouOweController][buildCrystallisedIntViewModel] - Missing charge reference")
        None
    }

  private[controllers] def buildLateSubmissionPenaltyViewModel(payment: PaymentWithPeriod): Option[LateSubmissionPenaltyViewModel] =
    payment.chargeReference match {
      case Some(chargeRef) =>
        Some(LateSubmissionPenaltyViewModel(
          chargeType = payment.chargeType.value,
          dueDate = payment.due,
          penaltyAmount = payment.originalAmount,
          amountReceived = payment.clearedAmount.getOrElse(0),
          leftToPay = payment.outstandingAmount,
          isOverdue = payment.isOverdue(dateService.now()),
          chargeReference = chargeRef,
          periodFrom = payment.periodFrom,
          periodTo = payment.periodTo
        ))
      case _ =>
        logger.warn("[WhatYouOweController][buildLateSubmissionPenaltyViewModel] - Missing charge reference")
        None
    }

  private[controllers] def buildEstimatedLPPViewModel(payment: PaymentWithPeriod,
                                                      penaltyDetails: Option[LPPDetails]): Option[ChargeDetailsViewModel] =
    (penaltyDetails, payment.accruingPenaltyAmount) match {
      case (Some(LPPDetails(_, "LPP1", Some(calcAmountLR), Some(daysLR), Some(rateLR), _, Some(daysHR), Some(rateHR), _, _, _, timeToPay)), Some(penaltyAmnt)) =>
        Some(EstimatedLPP1ViewModel(
          part1Days = daysLR,
          part2Days = daysHR,
          part1PenaltyRate = rateLR,
          part2PenaltyRate = rateHR,
          part1UnpaidVAT = calcAmountLR,
          penaltyAmount = penaltyAmnt,
          periodFrom = payment.periodFrom,
          periodTo = payment.periodTo,
          chargeType = ChargeType.penaltyChargeMappingLPP1(payment.chargeType).value,
          timeToPayPlan = timeToPay
        ))
      case (Some(LPPDetails(_, "LPP2", _, _, _, _, _, _, Some(daysLPP2), Some(rateLPP2), _, timeToPay)), Some(penaltyAmnt)) =>
        Some(EstimatedLPP2ViewModel(
          day = daysLPP2,
          penaltyRate = rateLPP2,
          penaltyAmount = penaltyAmnt,
          periodFrom = payment.periodFrom,
          periodTo = payment.periodTo,
          chargeType = ChargeType.penaltyChargeMappingLPP2(payment.chargeType).value,
          timeToPay = timeToPay
        ))
      case _ =>
        val loggerMessage = (penaltyDetails, payment.accruingPenaltyAmount) match {
          case (Some(pen), Some(_)) => s"Missing particular LPP details for ${pen.penaltyCategory} penalty type"
          case (None, Some(_)) => "No matching penalty was found"
          case _ => "Accruing penalty amount was not found"
        }
        logger.warn(s"[WhatYouOweController][buildEstimatedLPPViewModel] - $loggerMessage")
        None
    }

  private[controllers] def buildCrystallisedLPPViewModel(payment: PaymentWithPeriod,
                                                         penaltyDetails: Option[LPPDetails]): Option[ChargeDetailsViewModel] =
    (penaltyDetails, payment.chargeReference) match {
      case (Some(LPPDetails(_, "LPP1", Some(calcAmountLR), Some(daysLR), Some(rateLR), calcAmountHR, daysHR, rateHR, _, _, _, _)),
            Some(chargeRef)) =>
        val numOfDays = (calcAmountHR, daysHR) match {
          case (Some(_), Some(days)) => days
          case _ => daysLR
        }
        Some(CrystallisedLPP1ViewModel(
          numberOfDays = numOfDays,
          part1Days = daysLR,
          part2Days = daysHR,
          part1PenaltyRate = rateLR,
          part2PenaltyRate = rateHR,
          part1UnpaidVAT = calcAmountLR,
          part2UnpaidVAT = calcAmountHR,
          dueDate = payment.due,
          penaltyAmount = payment.originalAmount,
          amountReceived = payment.clearedAmount.getOrElse(0),
          leftToPay = payment.outstandingAmount,
          periodFrom = payment.periodFrom,
          periodTo = payment.periodTo,
          chargeType = payment.chargeType.value,
          chargeReference = chargeRef,
          isOverdue = payment.isOverdue(dateService.now())
        ))
      case (Some(LPPDetails(_, "LPP2", _, _, _, _, _, _, Some(daysLPP2), Some(rateLPP2), _, _)),
      Some(chargeRef)) =>
        Some(CrystallisedLPP2ViewModel(
          numberOfDays = daysLPP2,
          penaltyRate = rateLPP2,
          dueDate = payment.due,
          penaltyAmount = payment.originalAmount,
          amountReceived = payment.clearedAmount.getOrElse(0),
          leftToPay = payment.outstandingAmount,
          periodFrom = payment.periodFrom,
          periodTo = payment.periodTo,
          chargeType = payment.chargeType.value,
          chargeReference = chargeRef,
          isOverdue = payment.isOverdue(dateService.now())
        ))
      case (Some(penDetails), _) =>
        logger.warn(s"[WhatYouOweController][buildCrystallisedLPPViewModel] - " +
          s"Missing LPP details for ${penDetails.penaltyCategory} penalty type")
        None
      case _ =>
        val missingData = if(payment.chargeReference.isDefined) "matching penalty" else "charge reference"
        logger.warn(s"[WhatYouOweController][buildCrystallisedLPPViewModel] - Missing $missingData")
        None
    }

  def constructViewModel(payments: Seq[Payment],
                         mandationStatus: String,
                         penalties: Seq[LPPDetails]): Option[WhatYouOweViewModel] = {

    val chargeModels = categoriseCharges(payments, penalties).flatten
    val totalAmount = chargeModels.map(_.outstandingAmount).sum
    val totalPaymentCount = payments.length + payments.count(_.showEstimatedInterest) + payments.count(_.showEstimatedPenalty)

    if(totalPaymentCount == chargeModels.length) {
      Some(WhatYouOweViewModel(totalAmount, chargeModels, mandationStatus, payments.exists(_.isOverdue(dateService.now()))))
    } else {
      None
    }
  }

  def findPenaltyCharge(chargeReference: Option[String],
                        penaltyType: Option[String],
                        isEstimate: Boolean,
                        penalties: Seq[LPPDetails]): Option[LPPDetails] =
    penalties.find(pen => {
      if (isEstimate) {
        pen.principalChargeReference == chargeReference.getOrElse("") && penaltyType.getOrElse("") == pen.penaltyCategory
      } else {
        pen.penaltyChargeReference == chargeReference
      }
    })
}
