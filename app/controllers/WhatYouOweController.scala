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
import connectors.httpParsers.ResponseHttpParsers
import models.payments._
import models.penalties.{LPPDetails, PenaltyDetails}
import models.viewModels.StandardChargeViewModel.{buildStandardChargeViewModel, periodFrom, periodTo}
import models.viewModels._
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request, Result}
import services._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.errors.PaymentsError
import views.html.payments.{NoPayments, WhatYouOwe}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import models.ChangedOnVatPeriod.RequestCategoryType4
import models.viewModels.VatOverpaymentForRPIViewModel.buildVatOverpaymentForRPIViewModel
import models.{CustomerInformation, DirectDebitStatus, StandingRequest, User}
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier

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
                                     WYOViewService: WYOViewService,
                                     WYOSessionService: WYOSessionService,
                                     auditService: AuditingService,
                                     poaCheckService: POACheckService,
                                     annualAccountingService: AnnualAccountingService)
                                    (implicit ec: ExecutionContext,
                                     appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  def show: Action[AnyContent] = authorisedController.financialAction { implicit request =>

    implicit user =>

      val servinceInfoF = serviceInfoService.getPartial
      val paymentsF = paymentsService.getOpenPayments(user.vrn)
      val accountDetailsF = accountDetailsService.getAccountDetails(user.vrn)
      val penaltiesF = penaltyDetailsService.getPenaltyDetails(user.vrn)
      val ddDetailsF = paymentsService.getDirectDebitStatus(user.vrn)
      val aaStandingF =
        if (appConfig.features.annualAccountingFeatureEnabled()) {
          annualAccountingService.getStandingRequests(user.vrn)
        } else {
          Future.successful(None)
        }

      for {
        serviceInfo <- servinceInfoF
        payments <- paymentsF
        accountDetails <- accountDetailsF
        penalties <- penaltiesF
        ddDetails <- ddDetailsF
        aaStanding <- aaStandingF
        mandationStatus = accountDetails.map(_.mandationStatus).getOrElse("")
        today = dateService.now()
        ddStatus = ddDetails.map(_.directDebitMandateFound).getOrElse(false)
        isPoaActiveForCustomer = poaCheckService.retrievePoaActiveForCustomer(accountDetails, today)
        isAACustomer = appConfig.features.annualAccountingFeatureEnabled() &&
          aaStanding.exists(_.standingRequests.exists(_.requestCategory == RequestCategoryType4))
        showPoaContent = isPoaActiveForCustomer && !isAACustomer
        result <- handleWYOResult(
          serviceInfo, payments, penalties, mandationStatus, ddStatus, isAACustomer, showPoaContent
        )
      } yield result
  }

  def handleWYOResult(serviceInfo: Html,
                     payments: models.ServiceResponse[Option[Payments]],
                     penalties: ResponseHttpParsers.HttpResult[PenaltyDetails],
                     mandationStatus: String,
                     ddStatus: Boolean,
                     isAACustomer: Boolean,
                     showPoaContent: Boolean
                    )(implicit hc: HeaderCarrier, user: User, ec: ExecutionContext, request: Request[_]): Future[Result] = {
    (payments, penalties) match {
      case (Right(Some(payments)), Right(penalties)) =>

        constructViewModel(payments.financialTransactions, mandationStatus, penalties, ddStatus) match {
          case Some(model) =>
            auditService.extendedAudit(
              WhatYouOweAuditModel(user.vrn, user.arn, model.charges),
              routes.WhatYouOweController.show.url
            )
            WYOSessionService.storeChargeModels(model.charges, user.vrn).map { _ =>
              Ok(view(model, serviceInfo, showPoaContent, isAACustomer))
            }
          case None =>
            logger.warn("[WhatYouOweController][show] incorrect fields received for payment(s); failed to render view")
            Future.successful(InternalServerError(paymentsError()))
        }
      case (Right(None), Right(_)) =>
        val clientName = request.session.get(SessionKeys.mtdVatvcAgentClientName)

        Future.successful(Ok(noPayments(user, serviceInfo, clientName, mandationStatus, showPoaContent, isAACustomer)))
      case _ =>
        val loggerMessage = (payments, penalties) match {
          case (Left(_), Right(_)) => s"Financial API failed"
          case (Right(_), Left(_)) => s"Penalty API failed"
          case (Left(_), Left(_)) => s"Both financial and penalty API failed"
        }
        logger.warn(s"[WhatYouOweController][show] $loggerMessage")
        Future.successful(InternalServerError(paymentsError()))
    }
  }

  def constructViewModel(payments: Seq[Payment],
                         mandationStatus: String,
                         penalties: PenaltyDetails,
                         ddStatus: Boolean)(implicit request: Request[_]): Option[WhatYouOweViewModel] = {

    val chargeModels = WYOViewService.categoriseCharges(payments, penalties,ddStatus).flatten
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
}
