/*
 * Copyright 2025 HM Revenue & Customs
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
import com.google.inject.Inject
import config.{AppConfig, ServiceErrorHandler}
import models.{ServiceResponse, StandingRequest, User}
import models.obligations.VatReturnObligations
import models.viewModels._
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request, Result}
import play.twirl.api.Html
import services._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.annual.AnnualAccountingView

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class AnnualAccountingController @Inject() (
    authorisedController: AuthorisedController,
    dateService: DateService,
    paymentsOnAccountService: PaymentsOnAccountService,
    paymentsService: PaymentsService,
    serviceInfoService: ServiceInfoService,
    serviceErrorHandler: ServiceErrorHandler,
    vatDetailService: VatDetailsService,
    annualAccountingError: views.html.errors.AnnualAccountingError,
    mcc: MessagesControllerComponents,
    view: AnnualAccountingView,
    auditService: AuditingService
)(implicit ec: ExecutionContext, appConfig: AppConfig)
    extends FrontendController(mcc)
    with I18nSupport
    with LoggerUtil {

  import AnnualAccountingController._

  def show: Action[AnyContent] = authorisedController.authorisedActionAllowAgents { implicit request => implicit user =>
    if (appConfig.features.annualAccountingFeatureEnabled()) {
      (for {
        serviceInfoContent <- serviceInfoService.getPartial
        today = dateService.now()
        standingRequestOpt <- paymentsOnAccountService.getPaymentsOnAccounts(user.vrn)
        obligationsResult <- vatDetailService.getReturnObligations(user.vrn)
        ddStatusResult <- paymentsService.getDirectDebitStatus(user.vrn)
        paymentsHistory <- paymentsService.getPaymentsHistory(user.vrn, today, None)
        paymentsForPeriod <- {
          val aaRequests = standingRequestOpt.toSeq.flatMap(_.standingRequests).filter(_.requestCategory == models.ChangedOnVatPeriod.RequestCategoryType4)
          val schedulesRanges = aaRequests.map { d =>
            val starts = d.requestItems.map(ri => LocalDate.parse(ri.startDate))
            val ends = d.requestItems.map(ri => LocalDate.parse(ri.endDate))
            (starts.min, ends.max)
          }
          val (from, to) = schedulesRanges.find { case (s, e) => !today.isBefore(s) && !today.isAfter(e) }
            .orElse(schedulesRanges.sortBy(_._2).lastOption)
            .getOrElse(today -> today)
          paymentsService.getPaymentsForPeriod(user.vrn, from, to)
        }
        viewResult <- handleData(serviceInfoContent, today, standingRequestOpt, obligationsResult, ddStatusResult, paymentsHistory, paymentsForPeriod, view)
      } yield viewResult).recoverWith { case e =>
        logger.error(s"Unexpected failure in AnnualAccountingController: ${e.getMessage} For: ${user.vrn}")
        serviceErrorHandler.showInternalServerError
      }
    } else {
      serviceErrorHandler.notFoundTemplate.map(NotFound(_))
    }
  }

  private def handleData(
      serviceInfoContent: Html,
      today: LocalDate,
      standingRequestOpt: Option[StandingRequest],
      obligationsResult: ServiceResponse[Option[VatReturnObligations]],
      ddStatusResult: ServiceResponse[models.DirectDebitStatus],
      paymentsHistoryResult: ServiceResponse[Seq[models.viewModels.PaymentsHistoryModel]],
      paymentsResult: ServiceResponse[models.payments.Payments],
      view: AnnualAccountingView
  )(implicit request: Request[AnyContent], user: User): Future[Result] = {
    standingRequestOpt match {
      case Some(standingRequest) =>
        val hasAAData = standingRequest.standingRequests.exists(_.requestCategory == models.ChangedOnVatPeriod.RequestCategoryType4)
        if (!hasAAData) {
          logger.warn(s"[AnnualAccountingController][show] No Annual Accounting (category 4) schedules found for ${user.vrn}")
          return Future.successful(InternalServerError(annualAccountingError()))
        }
        val obligationsOpt = obligationsResult.toOption.flatten
        val hasDirectDebit: Option[Boolean] = ddStatusResult.toOption.map(_.directDebitMandateFound)
        val paymentsHistory = paymentsHistoryResult.toOption.getOrElse(Seq.empty)
        val paymentsOpt = paymentsResult.toOption
        val viewModel = buildViewModel(standingRequest, today, obligationsOpt, paymentsHistory, paymentsOpt, user.isAgent, hasDirectDebit)
        logger.info(s"[AnnualAccountingController][show] rendering AA page for ${user.vrn}")
        Future.successful(Ok(view(viewModel, serviceInfoContent)))
      case None =>
        logger.error(s"Standingrequest API returned None for ${user.vrn} (AA)")
        Future.successful(InternalServerError(annualAccountingError()))
    }
  }
}

object AnnualAccountingController {
  import models.ChangedOnVatPeriod.RequestCategoryType4

  def buildViewModel(
      standingRequestResponse: StandingRequest,
      today: LocalDate,
      returnObligations: Option[VatReturnObligations],
      paymentsHistory: Seq[models.viewModels.PaymentsHistoryModel],
      paymentsOpt: Option[models.payments.Payments],
      isAgent: Boolean,
      hasDirectDebit: Option[Boolean]
  ): AnnualAccountingViewModel = {
    val aaStandingRequests = standingRequestResponse.standingRequests.filter(_.requestCategory == RequestCategoryType4)

    val periods = aaStandingRequests
      .map { srd =>
        val sortedByDue = srd.requestItems.sortBy(ri => LocalDate.parse(ri.dueDate))
        val allStarts = sortedByDue.map(ri => LocalDate.parse(ri.startDate))
        val allEnds = sortedByDue.map(ri => LocalDate.parse(ri.endDate))
        val startDate = allStarts.min
        val endDate = allEnds.max

        val payments = sortedByDue.map { item =>
          val dueDate = LocalDate.parse(item.dueDate)
          val startDate = LocalDate.parse(item.startDate)
          val endDate = LocalDate.parse(item.endDate)

          val clearedDateOpt = paymentsHistory
            .filter(ph => ph.amount == item.amount && ph.taxPeriodFrom.contains(startDate) && ph.taxPeriodTo.contains(endDate))
            .flatMap(_.clearedDate)
            .sorted
            .headOption

          val paymentMatchOpt = paymentsOpt.flatMap { payments =>
            payments.financialTransactions.find { p =>
              p.originalAmount == item.amount &&
              p.due == dueDate &&
              p.periodKey.contains(item.periodKey)
            }
          }

          val status: AAPaymentStatus =
            paymentMatchOpt match {
              case Some(pmt) if pmt.outstandingAmount == 0 =>
                clearedDateOpt match {
                  case Some(cleared) if cleared.isAfter(dueDate) => AAPaymentStatus.PaidLate
                  case _ => AAPaymentStatus.Paid
                }
              case Some(pmt) if pmt.outstandingAmount > 0 =>
                if (dueDate.isBefore(today)) AAPaymentStatus.Overdue else AAPaymentStatus.Upcoming
              case _ =>
                if (dueDate.isBefore(today)) AAPaymentStatus.Overdue else AAPaymentStatus.Upcoming
            }
          AAPayment(isBalancing = false, dueDate = dueDate, amount = Some(item.amount), status = status)
        }

        val balancingDue: Option[LocalDate] = returnObligations.flatMap { obligations =>
          obligations.obligations.find { ob =>
            !ob.periodTo.isBefore(startDate) && !ob.periodFrom.isAfter(endDate)
          }.map(_.due)
        }

        val balancingPayment = AAPayment(
          isBalancing = true,
          dueDate = balancingDue.getOrElse(endDate.plusMonths(2)),
          amount = None,
          status = {
            val bd = balancingDue.getOrElse(endDate.plusMonths(2))
            if (bd.isBefore(today)) AAPaymentStatus.Overdue else AAPaymentStatus.Upcoming
          }
        )

        val isCurrent = !today.isBefore(startDate) && !today.isAfter(endDate)
        val isPast = endDate.isBefore(today)

        AASchedulePeriod(
          startDate = startDate,
          endDate = endDate,
          payments = payments :+ balancingPayment,
          isCurrent = isCurrent,
          isPast = isPast
        )
      }

    val currentPeriodOpt = periods.sortBy(_.startDate).find(p => !p.isPast && (p.isCurrent || p.startDate.isAfter(today)))
    val updatedPeriods = periods.map(p => p.copy(isCurrent = currentPeriodOpt.contains(p)))

    val currentPeriods = updatedPeriods.filter(_.isCurrent).toList
    val pastPeriods = updatedPeriods
      .filter(_.isPast)
      .sortBy(_.endDate)(Ordering[LocalDate].reverse)
      .take(1)
      .toList

    val frequencyOpt = currentPeriods.headOption.flatMap { cp =>
      val nonBalancingCount = cp.payments.count(p => !p.isBalancing)
      if (nonBalancingCount >= models.viewModels.PaymentFrequency.Monthly.instalments)
        Some(models.viewModels.PaymentFrequency.Monthly)
      else if (nonBalancingCount >= models.viewModels.PaymentFrequency.Quarterly.instalments)
        Some(models.viewModels.PaymentFrequency.Quarterly)
      else
        None
    }

    val nextPaymentOpt = currentPeriods
      .flatMap(_.payments)
      .filter(p => !p.dueDate.isBefore(today)) 
      .headOption

    val mostRecentChangedOn: Option[LocalDate] =
      aaStandingRequests
        .flatMap(_.changedOn.map(_.trim))
        .map(LocalDate.parse)
        .sorted(Ordering[LocalDate].reverse)
        .headOption
        .filter(d => !d.isBefore(today.minusMonths(4)))

    AnnualAccountingViewModel(
      changedOn = mostRecentChangedOn,
      currentPeriods = currentPeriods,
      pastPeriods = pastPeriods,
      nextPayment = nextPaymentOpt,
      isAgent = isAgent,
      hasDirectDebit = hasDirectDebit,
      frequency = frequencyOpt
    )
  }
}
