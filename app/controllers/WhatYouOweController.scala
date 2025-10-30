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

import audit.AuditingService
import audit.models.WhatYouOweAuditModel
import com.google.inject.Inject
import common.SessionKeys
import config.AppConfig
import models.payments._
import models.penalties.{LPPDetails, PenaltyDetails}
import models.viewModels.StandardChargeViewModel.{periodFrom, periodTo}
import models.viewModels._
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
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
                                     WYOSessionService: WYOSessionService,
                                     auditService: AuditingService,
                                     poaCheckService: POACheckService)
                                    (implicit ec: ExecutionContext,
                                     appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  def show: Action[AnyContent] = authorisedController.financialAction { implicit request =>

    implicit user =>
      serviceInfoService.getPartial.flatMap { serviceInfoContent =>
        paymentsService.getOpenPayments(user.vrn).flatMap { payments =>
          accountDetailsService.getAccountDetails(user.vrn).flatMap { accountDetails =>
            val mandationStatus = accountDetails.map(_.mandationStatus).getOrElse("")
            val today = dateService.now()

            penaltyDetailsService.getPenaltyDetails(user.vrn).flatMap { penaltyDetails =>
              paymentsService.getDirectDebitStatus(user.vrn).flatMap { ddDetails =>
                val ddStatus: Boolean = ddDetails.map(_.directDebitMandateFound).getOrElse(false)
                val isPoaActiveForCustomer = poaCheckService.retrievePoaActiveForCustomer(accountDetails, today)

                (payments, penaltyDetails) match {
                  case (Right(Some(payments)), Right(penalties)) =>

                    constructViewModel(payments.financialTransactions, mandationStatus, penalties, ddStatus) match {
                      case Some(model) =>
                        auditService.extendedAudit(
                          WhatYouOweAuditModel(user.vrn, user.arn, model.charges),
                          routes.WhatYouOweController.show.url
                        )
                        WYOSessionService.storeChargeModels(model.charges, user.vrn).map { _ =>
                          Ok(view(model, serviceInfoContent, isPoaActiveForCustomer))
                        }
                      case None =>
                        logger.warn("[WhatYouOweController][show] incorrect fields received for payment(s); failed to render view")
                        Future.successful(InternalServerError(paymentsError()))
                    }
                  case (Right(None), Right(_)) =>
                    val clientName = request.session.get(SessionKeys.mtdVatvcAgentClientName)

                    Future.successful(Ok(noPayments(user, serviceInfoContent, clientName, mandationStatus, isPoaActiveForCustomer)))
                  case _ =>
                    val loggerMessage = (payments, penaltyDetails) match {
                      case (Left(_), Right(_)) => s"Financial API failed"
                      case (Right(_), Left(_)) => s"Penalty API failed"
                      case (Left(_), Left(_)) => s"Both financial and penalty API failed"
                    }
                    logger.warn(s"[WhatYouOweController][show] $loggerMessage")
                    Future.successful(InternalServerError(paymentsError()))
                }
              }
            }
          }
        }
      }
  }


  private[controllers] def categoriseCharges(payments: Seq[Payment], penalties: PenaltyDetails,
                                             ddStatus: Boolean)
                                            (implicit request: Request[_]): Seq[Option[ChargeDetailsViewModel]] =
    payments collect {
      case p: PaymentWithPeriod if p.chargeType.isPenalty =>
        val matchingPenalty = findPenaltyCharge(p.chargeReference, p.penaltyType, isEstimate = false, penalties.LPPDetails)
        buildCrystallisedChargePlusEstimates(p, matchingPenalty, ddStatus)
      case p: PaymentWithPeriod if p.chargeType.isLPICharge =>
        Seq(buildCrystallisedIntViewModel(p, ddStatus))
      case p: PaymentWithPeriod if p.chargeType.eq(VatLateSubmissionPen) =>
        buildLSPPlusEstimates(p, ddStatus)
      case p: PaymentWithPeriod if p.chargeType.eq(VatOverpaymentForRPI) =>
        Seq(buildVatOverpaymentForRPIViewModel(p, ddStatus))
      case p =>
        buildChargePlusEstimates(p, penalties, ddStatus)
    } flatten

  private[controllers] def buildCrystallisedChargePlusEstimates(charge: PaymentWithPeriod,
                                                                matchingPenalty: Option[LPPDetails],
                                                                ddStatus: Boolean): Seq[Option[ChargeDetailsViewModel]] =
    if (charge.showEstimatedInterest) {
      Seq(buildCrystallisedLPPViewModel(charge, matchingPenalty, ddStatus), buildEstimatedIntViewModel(charge, ddStatus))
    } else {
      Seq(buildCrystallisedLPPViewModel(charge, matchingPenalty, ddStatus))
    }

  private[controllers] def buildChargePlusEstimates(charge: Payment,
                                                    penalties: PenaltyDetails,
                                                    ddStatus: Boolean)(implicit request: Request[_]): Seq[Option[ChargeDetailsViewModel]] =
    charge match {
      case p: PaymentWithPeriod if p.showEstimatedInterest && p.showEstimatedPenalty =>
        val matchingPenalty = findPenaltyCharge(charge.chargeReference, charge.penaltyType, isEstimate = true, penalties.LPPDetails)
        Seq(buildStandardChargeViewModel(p, ddStatus), buildEstimatedIntViewModel(p, ddStatus), buildEstimatedLPPViewModel(p, matchingPenalty, penalties.breathingSpace, ddStatus))
      case p: PaymentWithPeriod if p.showEstimatedPenalty =>
        val matchingPenalty = findPenaltyCharge(charge.chargeReference, charge.penaltyType, isEstimate = true, penalties.LPPDetails)
        Seq(buildStandardChargeViewModel(p, ddStatus), buildEstimatedLPPViewModel(p, matchingPenalty, penalties.breathingSpace, ddStatus))
      case p: PaymentWithPeriod if p.showEstimatedInterest =>
        Seq(buildStandardChargeViewModel(p, ddStatus), buildEstimatedIntViewModel(p, ddStatus))
      case _ =>
        Seq(buildStandardChargeViewModel(charge, ddStatus))
    }

  private[controllers] def buildLSPPlusEstimates(charge: PaymentWithPeriod,
                                                 ddStatus: Boolean): Seq[Option[ChargeDetailsViewModel]] =
    if (charge.showEstimatedInterest) {
      Seq(buildLateSubmissionPenaltyViewModel(charge, ddStatus), buildEstimatedIntViewModel(charge, ddStatus))
    } else {
      Seq(buildLateSubmissionPenaltyViewModel(charge, ddStatus))
    }

  private[controllers] def buildStandardChargeViewModel(payment: Payment, ddStatus: Boolean): Option[StandardChargeViewModel] = {
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
      periodTo = periodTo(payment),
      directDebitMandateFound = ddStatus
    ))
  }

  private[controllers] def buildVatOverpaymentForRPIViewModel(payment: PaymentWithPeriod, ddStatus: Boolean): Option[VatOverpaymentForRPIViewModel] = {
    Some(VatOverpaymentForRPIViewModel(
      periodFrom = payment.periodFrom,
      periodTo = payment.periodTo,
      chargeType = payment.chargeType.value,
      dueDate = payment.due,
      correctionCharge = payment.originalAmount,
      amountReceived = payment.clearedAmount.getOrElse(0),
      leftToPay = payment.outstandingAmount,
      isOverdue = payment.isOverdue(dateService.now()),
      chargeReference = payment.chargeReference,
      directDebitMandateFound = ddStatus
    ))
  }

  private[controllers] def buildEstimatedIntViewModel(payment: PaymentWithPeriod, ddStatus: Boolean): Option[EstimatedInterestViewModel] =
    payment.accruingInterestAmount match {
      case Some(interestAmnt) =>
        Some(EstimatedInterestViewModel(
          periodFrom = payment.periodFrom,
          periodTo = payment.periodTo,
          chargeType = ChargeType.LPIChargeMapping(payment.chargeType).value,
          interestAmount = interestAmnt,
          isPenaltyReformPenaltyLPI = ChargeType.LPIChargeMapping(payment.chargeType).isPenaltyReformPenaltyLPI,
          isNonPenaltyReformPenaltyLPI = ChargeType.LPIChargeMapping(payment.chargeType).isNonPenaltyReformPenaltyLPI,
          directDebitMandateFound = ddStatus
        ))
      case _ =>
        logger.warn("[WhatYouOweController][buildEstimatedIntViewModel] - Missing accrued interest amount")
        None
    }

  private[controllers] def buildCrystallisedIntViewModel(payment: PaymentWithPeriod, ddStatus: Boolean): Option[CrystallisedInterestViewModel] =
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
          isPenaltyReformPenaltyLPI = payment.chargeType.isPenaltyReformPenaltyLPI,
          isNonPenaltyReformPenaltyLPI = payment.chargeType.isNonPenaltyReformPenaltyLPI,
          directDebitMandateFound = ddStatus
        ))
      case _ =>
        logger.warn("[WhatYouOweController][buildCrystallisedIntViewModel] - Missing charge reference")
        None
    }

  private[controllers] def buildLateSubmissionPenaltyViewModel(payment: PaymentWithPeriod, ddStatus: Boolean): Option[LateSubmissionPenaltyViewModel] =
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
          periodTo = payment.periodTo,
          directDebitMandateFound = ddStatus
        ))
      case _ =>
        logger.warn("[WhatYouOweController][buildLateSubmissionPenaltyViewModel] - Missing charge reference")
        None
    }

  private[controllers] def buildEstimatedLPPViewModel(payment: PaymentWithPeriod,
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
      case _ =>
        val loggerMessage = (penaltyDetails, payment.accruingPenaltyAmount) match {
          case (Some(pen), Some(_)) => s"Missing particular LPP details for ${pen.penaltyCategory} penalty type"
          case (None, Some(_)) => s"No matching penalty was found for charge type: ${payment.chargeType}"
          case _ => "Accruing penalty amount was not found"
        }
        logger.warn(s"[WhatYouOweController][buildEstimatedLPPViewModel] - $loggerMessage")
        None
    }

  private[controllers] def buildCrystallisedLPPViewModel(payment: PaymentWithPeriod,
                                                         penaltyDetails: Option[LPPDetails],
                                                         ddStatus: Boolean): Option[ChargeDetailsViewModel] =
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
          isOverdue = payment.isOverdue(dateService.now()),
          directDebitMandateFound = ddStatus
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
          isOverdue = payment.isOverdue(dateService.now()),
          directDebitMandateFound = ddStatus
        ))
      case (Some(penDetails), _) =>
        logger.warn(s"[WhatYouOweController][buildCrystallisedLPPViewModel] - " +
          s"Missing LPP details for ${penDetails.penaltyCategory} penalty type")
        None
      case _ =>
        val missingData = if (payment.chargeReference.isDefined) s"matching penalty for charge type: ${payment.chargeType}" else "charge reference"
        logger.warn(s"[WhatYouOweController][buildCrystallisedLPPViewModel] - Missing $missingData")
        None
    }

  def constructViewModel(payments: Seq[Payment],
                         mandationStatus: String,
                         penalties: PenaltyDetails,
                         ddStatus: Boolean)(implicit request: Request[_]): Option[WhatYouOweViewModel] = {

    val chargeModels = categoriseCharges(payments, penalties,ddStatus).flatten
    val totalAmount = chargeModels.map(_.outstandingAmount).sum
    val totalPaymentCount = payments.length + payments.count(_.showEstimatedInterest) + payments.count(_.showEstimatedPenalty)

    if (totalPaymentCount == chargeModels.length) {
      Some(WhatYouOweViewModel(
        totalAmount,
        chargeModels,
        mandationStatus,
        payments.exists(_.isOverdue(dateService.now())),
        penalties.breathingSpace,
        ddStatus))
    } else {
      warnLog(s"[WhatYouOweController][constructViewModel] " +
        s"totalPaymentCount - $totalPaymentCount does not equal chargeModels.length - ${chargeModels.length}" +
        s"\n no. of payments - ${payments.length}" +
        s"\n DD-Status - ${ddStatus}" +
        s"\n estimated interest count - ${payments.count(_.showEstimatedInterest)}" +
        s"\n estimated penalty count - ${payments.count(_.showEstimatedPenalty)}" +
        s"\n estimated interest and estimated penalty - ${payments.count(p => p.showEstimatedInterest && p.showEstimatedPenalty)}" +
        s"\n no. of charge references - ${payments.count(_.chargeReference.isDefined)}" +
        s"\n no. of penalties - ${penalties.LPPDetails.length}" +
        s"\n no. of penalty charge ref - ${penalties.LPPDetails.count(_.penaltyChargeReference.isDefined)}")
      None
    }
  }

  def findPenaltyCharge(chargeReference: Option[String],
                        penaltyType: Option[String],
                        isEstimate: Boolean,
                        penalties: Seq[LPPDetails])(implicit request: Request[_]): Option[LPPDetails] =
    penalties.find(pen => {
      if (isEstimate) {
        pen.principalChargeReference == chargeReference.getOrElse("") && penaltyType.getOrElse("") == pen.penaltyCategory
      } else {
        if (pen.penaltyChargeReference.isEmpty && chargeReference.isEmpty) {
          infoLog("[WhatYouOweController][findPenaltyCharge] both penaltyChargeReference and chargeReference are None")
        }
        pen.penaltyChargeReference == chargeReference
      }
    })
}
