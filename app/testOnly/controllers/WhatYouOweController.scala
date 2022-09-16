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
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import controllers.AuthorisedController
import models.payments.{ChargeType, Payment, PaymentOnAccount, PaymentWithPeriod}
import models.penalties.{LPPDetails, PenaltyDetails}
import models.viewModels._
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{AccountDetailsService, DateService, PaymentsService, PenaltyDetailsService, ServiceInfoService, WYOSessionService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.errors.PaymentsError
import views.html.payments.{NoPayments, WhatYouOwe}

import scala.concurrent.{ExecutionContext, Future}

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
              payments match {
                case Right(Some(payments)) =>
                  constructViewModel(payments.financialTransactions.filterNot(_.chargeType equals PaymentOnAccount), mandationStatus) match {
                    case Some(model) =>
                      WYOSessionService.storeChargeModels(model.charges,user.vrn).map { _ =>
                        Ok(view(model, serviceInfoContent))
                      }
                    case None =>
                      logger.warn("[WhatYouOweController][show] incorrect fields received for payment(s); failed to render view")
                      Future.successful(InternalServerError(paymentsError()))
                  }
                case Right(_) =>
                  val clientName = request.session.get(SessionKeys.mtdVatvcAgentClientName)
                  Future.successful(Ok(noPayments(user, serviceInfoContent, clientName, mandationStatus)))
                case Left(error) =>
                  logger.warn(s"[WhatYouOweController][show] Payments error: $error")
                  Future.successful(InternalServerError(paymentsError()))
              }
            }
          }
        }
      }
  }

  private[controllers] def categoriseCharges(payments: Seq[Payment]): Seq[ChargeDetailsViewModel] = {
    payments collect {
      case p: PaymentWithPeriod if
        p.chargeType.isInterest
        && p.chargeReference.isDefined
        && p.originalAmount.isDefined => Seq(buildCrystallisedIntViewModel(p))

      case p if
        p.originalAmount.isDefined
        && p.chargeType.notInterest => decideIfEstimatedInterest(p, buildStandardChargeViewModel(p))
    } flatten

  }

  private[controllers] def decideIfEstimatedInterest(p: Payment, charge: ChargeDetailsViewModel)
    : Seq[ChargeDetailsViewModel] = { p match {
      case p: PaymentWithPeriod if p.showEstimatedInterest => Seq(charge, buildEstimatedIntViewModel(p))
      case _ => Seq(charge)
  }}

  private[controllers] def buildCrystallisedIntViewModel(payment: PaymentWithPeriod): CrystallisedInterestViewModel = {
    CrystallisedInterestViewModel(
      periodFrom = payment.periodFrom,
      periodTo = payment.periodTo,
      chargeType = payment.chargeType.value,
      interestRate = 5.00, // TODO replace with API field
      dueDate = payment.due,
      interestAmount = payment.originalAmount.get,
      amountReceived = payment.clearedAmount.getOrElse(0),
      leftToPay = payment.outstandingAmount,
      isOverdue = payment.isOverdue(dateService.now()),
      chargeReference = payment.chargeReference.get,
      isPenalty = payment.chargeType.isPenaltyInterest
    )
  }

  private[controllers] def buildStandardChargeViewModel(payment: Payment): StandardChargeViewModel = {
    import StandardChargeViewModel._
    StandardChargeViewModel(
      chargeType = payment.chargeType.value,
      outstandingAmount = payment.outstandingAmount,
      originalAmount = payment.originalAmount.get,
      clearedAmount = payment.clearedAmount.getOrElse(0),
      dueDate = payment.due,
      periodKey = payment.periodKey,
      isOverdue = payment.isOverdue(dateService.now()),
      chargeReference = payment.chargeReference,
      periodFrom = periodFrom(payment),
      periodTo = periodTo(payment)
    )
  }

  private[controllers] def buildEstimatedIntViewModel(payment: PaymentWithPeriod): EstimatedInterestViewModel = {
    EstimatedInterestViewModel(
      periodFrom = payment.periodFrom,
      periodTo = payment.periodTo,
      chargeType = ChargeType.interestChargeMapping(payment.chargeType).value,
      interestRate = 5.00, // TODO replace with API field
      interestAmount = payment.accruedInterestAmount.get,
      isPenalty = payment.chargeType.isPenaltyInterest
    )
  }

  def constructViewModel(payments: Seq[Payment], mandationStatus: String): Option[WhatYouOweViewModel] = {

    val totalAmount = payments.map(_.outstandingAmount).sum
    val chargeModels = categoriseCharges(payments)
    val totalPaymentCount = payments.length + payments.count(_.showEstimatedInterest)

    if(totalPaymentCount == chargeModels.length) {
      Some(WhatYouOweViewModel(totalAmount, chargeModels, mandationStatus, payments.exists(_.isOverdue(dateService.now()))))
    } else {
      None
    }
  }

  def findPenaltyCharge(chargeReference: String, penaltyType: String, penalties: Seq[LPPDetails]): Option[LPPDetails] =
    penalties.find(pen =>
      pen.principalChargeReference == chargeReference && penaltyType == pen.penaltyCategory
    )

}
