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

package testOnly.controllers

import com.google.inject.Inject
import common.SessionKeys
import config.AppConfig
import controllers.AuthorisedController
import models.payments.{ChargeType, Payment, PaymentWithPeriod}
import models.penalties.LPPDetails
import models.viewModels.StandardChargeViewModel.{periodFrom, periodTo}
import models.viewModels._
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{AccountDetailsService, DateService, PaymentsService, PenaltyDetailsService, ServiceInfoService, WYOSessionService}
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
        Seq(buildCrystallisedLPPViewModel(p, matchingPenalty))
      case p: PaymentWithPeriod if p.chargeType.isInterest => Seq(buildCrystallisedIntViewModel(p))
      case p => buildChargePlusEstimates(p, penalties)
    } flatten

  private[controllers] def buildChargePlusEstimates(charge: Payment,
                                                    penalties: Seq[LPPDetails]): Seq[Option[ChargeDetailsViewModel]] = {
    val matchingPenalty = findPenaltyCharge(charge.chargeReference, charge.penaltyType, isEstimate = true, penalties)
    (charge, matchingPenalty) match {
      case (p: PaymentWithPeriod, Some(penalty)) if p.showEstimatedInterest && p.showEstimatedPenalty =>
        Seq(buildStandardChargeViewModel(p), buildEstimatedIntViewModel(p), buildEstimatedLPPViewModel(p, penalty))
      case (p: PaymentWithPeriod, Some(penalty)) if p.showEstimatedPenalty =>
        Seq(buildStandardChargeViewModel(p), buildEstimatedLPPViewModel(p, penalty))
      case (p: PaymentWithPeriod, _) if p.showEstimatedInterest =>
        Seq(buildStandardChargeViewModel(p), buildEstimatedIntViewModel(p))
      case _ =>
        Seq(buildStandardChargeViewModel(charge))
    }
  }

  private[controllers] def buildStandardChargeViewModel(payment: Payment): Option[StandardChargeViewModel] =
    payment.originalAmount match {
      case Some(originalAmount) =>
        Some(StandardChargeViewModel(
          chargeType = payment.chargeType.value,
          outstandingAmount = payment.outstandingAmount,
          originalAmount = originalAmount,
          clearedAmount = payment.clearedAmount.getOrElse(0),
          dueDate = payment.due,
          periodKey = payment.periodKey,
          isOverdue = payment.isOverdue(dateService.now()),
          chargeReference = payment.chargeReference,
          periodFrom = periodFrom(payment),
          periodTo = periodTo(payment)
        ))
      case _ => None
    }

  private[controllers] def buildEstimatedIntViewModel(payment: PaymentWithPeriod): Option[EstimatedInterestViewModel] =
    payment.accruedInterestAmount match {
      case Some(interestAmnt) =>
        Some(EstimatedInterestViewModel(
          periodFrom = payment.periodFrom,
          periodTo = payment.periodTo,
          chargeType = ChargeType.interestChargeMapping(payment.chargeType).value,
          interestRate = 5.00, // TODO replace with API field
          interestAmount = interestAmnt,
          isPenalty = payment.chargeType.isPenaltyInterest
        ))
      case _ => None
    }

  private[controllers] def buildCrystallisedIntViewModel(payment: PaymentWithPeriod): Option[CrystallisedInterestViewModel] =
    (payment.chargeReference, payment.originalAmount) match {
      case (Some(chargeRef), Some(originalAmount)) =>
        Some(CrystallisedInterestViewModel(
          periodFrom = payment.periodFrom,
          periodTo = payment.periodTo,
          chargeType = payment.chargeType.value,
          interestRate = 5.00, // TODO replace with API field
          dueDate = payment.due,
          interestAmount = originalAmount,
          amountReceived = payment.clearedAmount.getOrElse(0),
          leftToPay = payment.outstandingAmount,
          isOverdue = payment.isOverdue(dateService.now()),
          chargeReference = chargeRef,
          isPenalty = payment.chargeType.isPenaltyInterest
        ))
      case _ => None
    }

  private[controllers] def buildEstimatedLPPViewModel(payment: PaymentWithPeriod,
                                                      penaltyDetails: LPPDetails): Option[ChargeDetailsViewModel] =
    (penaltyDetails, payment.accruedPenaltyAmount) match {
      case (LPPDetails(_, "LPP1", Some(calcAmountLR), Some(daysLR), Some(rateLR), _, Some(daysHR), Some(rateHR), _, _, _), Some(penaltyAmnt)) =>
        Some(EstimatedLPP1ViewModel(
          part1Days = daysLR,
          part2Days = daysHR,
          part1PenaltyRate = rateLR,
          part2PenaltyRate = rateHR,
          part1UnpaidVAT = calcAmountLR,
          penaltyAmount = penaltyAmnt,
          periodFrom = payment.periodFrom,
          periodTo = payment.periodTo,
          chargeType = ChargeType.penaltyChargeMappingLPP1(payment.chargeType).value
        ))
      case (LPPDetails(_, "LPP2", _, _, _, _, _, _, Some(daysLPP2), Some(rateLPP2), _), Some(penaltyAmnt)) =>
        Some(EstimatedLPP2ViewModel(
          day = daysLPP2,
          penaltyRate = rateLPP2,
          penaltyAmount = penaltyAmnt,
          periodFrom = payment.periodFrom,
          periodTo = payment.periodTo,
          chargeType = ChargeType.penaltyChargeMappingLPP2(payment.chargeType).value
        ))
      case _ => None
    }

  private[controllers] def buildCrystallisedLPPViewModel(payment: PaymentWithPeriod,
                                                         penaltyDetails: Option[LPPDetails]): Option[ChargeDetailsViewModel] =
    (penaltyDetails, payment.originalAmount, payment.chargeReference) match {
      case (Some(LPPDetails(_, "LPP1", Some(calcAmountLR), Some(daysLR), Some(rateLR), calcAmountHR, Some(daysHR), rateHR, _, _, _)),
            Some(originalAmnt), Some(chargeRef)) =>
        val numOfDays = if (calcAmountHR.isDefined) daysHR else daysLR
        Some(CrystallisedLPP1ViewModel(
          numberOfDays = numOfDays,
          part1Days = daysLR,
          part2Days = Some(daysHR),
          part1PenaltyRate = rateLR,
          part2PenaltyRate = rateHR,
          part1UnpaidVAT = calcAmountLR,
          part2UnpaidVAT = calcAmountHR,
          dueDate = payment.due,
          penaltyAmount = originalAmnt,
          amountReceived = payment.clearedAmount.getOrElse(0),
          leftToPay = payment.outstandingAmount,
          periodFrom = payment.periodFrom,
          periodTo = payment.periodTo,
          chargeType = payment.chargeType.value,
          chargeReference = chargeRef,
          isOverdue = payment.isOverdue(dateService.now())
        ))
      case _ => None
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
      val matchingChargeRef = if (isEstimate) {
        pen.principalChargeReference == chargeReference.getOrElse("")
      } else {
        pen.penaltyChargeReference == chargeReference
      }

      matchingChargeRef && penaltyType.getOrElse("") == pen.penaltyCategory
    })
}
