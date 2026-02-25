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
import models.{DirectDebitStatus, RequestItem, ServiceResponse, StandingRequest, User}
import models.obligations.VatReturnObligations
import models.payments.{PaymentWithOptionalOutstanding, PaymentsWithOptionalOutstanding}
import models.viewModels.AAPaymentStatus.{Overdue, Paid, PaidLate, Upcoming}
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
    accountDetailsService: AccountDetailsService,
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
        entityNameResult <- accountDetailsService.getEntityName(user.vrn)
        today = dateService.now()
        standingRequestOpt <- paymentsOnAccountService.getPaymentsOnAccounts(user.vrn)
        obligationsResult <- vatDetailService.getReturnObligations(user.vrn)
        ddStatusResult <- paymentsService.getDirectDebitStatus(user.vrn)
        paymentsHistoryByDue <- paymentsService.getLiabilitiesWithDueDate(user.vrn, today, None)
        paymentsForPeriod <- {
          val aaRequests = standingRequestOpt.toSeq.flatMap(_.standingRequests).filter(_.requestCategory == models.ChangedOnVatPeriod.RequestCategoryType4)
          val schedulesRanges = aaRequests.flatMap { d =>
            val byDue = d.requestItems.sortBy(ri => LocalDate.parse(ri.dueDate))
            byDue.headOption.map { first =>
              val last = byDue.last
              (LocalDate.parse(first.startDate), LocalDate.parse(last.endDate))
            }
          }
          val (from, to) = schedulesRanges.find { case (s, e) => !today.isBefore(s) && !today.isAfter(e) }
            .orElse(schedulesRanges.sortBy(_._2).lastOption)
            .getOrElse(today -> today)
          paymentsService.getPaymentsForPeriod(user.vrn, from, to)
        }
        viewResult <- handleData(
          serviceInfoContent,
          today,
          standingRequestOpt,
          obligationsResult,
          ddStatusResult,
          paymentsHistoryByDue,
          paymentsForPeriod,
          view,
          entityNameResult.toOption
        )
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
      ddStatusResult: ServiceResponse[DirectDebitStatus],
      paymentsHistoryByDueResult: ServiceResponse[Seq[PaymentHistoryWithDueDate]],
      paymentsResult: ServiceResponse[PaymentsWithOptionalOutstanding],
      view: AnnualAccountingView,
      entityNameOpt: Option[Option[String]]
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
        val chargesWithDueDates = paymentsHistoryByDueResult.toOption.getOrElse(Seq.empty)
        val paymentsDetails = paymentsResult.toOption
        val viewModel = buildViewModel(
          standingRequest,
          today,
          obligationsOpt,
          chargesWithDueDates,
          paymentsDetails,
          user.isAgent,
          hasDirectDebit,
          displayName = entityNameOpt.flatten
        )
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
  import models.payments.{AAQuarterlyInstalments, AAMonthlyInstalment}

  def buildViewModel(
      standingRequestResponse: StandingRequest,
      today: LocalDate,
      returnObligations: Option[VatReturnObligations],
      chargesWithDueDates: Seq[PaymentHistoryWithDueDate],
      paymentsDetails: Option[PaymentsWithOptionalOutstanding],
      isAgent: Boolean,
      hasDirectDebit: Option[Boolean],
      displayName: Option[String]
  ): AnnualAccountingViewModel = {

    val aaStandingRequests = standingRequestResponse.standingRequests.filter(_.requestCategory == RequestCategoryType4)

    val periods = aaStandingRequests
      .flatMap { srd =>
        if (srd.requestItems.isEmpty) {
          Nil
        } else {
          val requestItemsSortedByDueDate = srd.requestItems.sortBy(ri => LocalDate.parse(ri.dueDate))
          val first = requestItemsSortedByDueDate.head
          val last = requestItemsSortedByDueDate.last
          val scheduleStart = LocalDate.parse(first.startDate)
          val scheduleEnd = LocalDate.parse(last.endDate)

          val payments = processPayments(today, requestItemsSortedByDueDate, chargesWithDueDates, paymentsDetails)

          val balancingDue: Option[LocalDate] = returnObligations.flatMap { obligations =>
            obligations.obligations.find { ob =>
              !ob.periodTo.isBefore(scheduleStart) && !ob.periodFrom.isAfter(scheduleEnd)
            }.map(_.due)
          }

          val balancingPayment = AAPayment(
            isBalancing = true,
            dueDate = balancingDue.getOrElse(scheduleEnd.plusMonths(2)),
            amount = None,
            status = {
              val bd = balancingDue.getOrElse(scheduleEnd.plusMonths(2))
              if (bd.isBefore(today)) AAPaymentStatus.Overdue else AAPaymentStatus.Upcoming
            }
          )

          List(
            AASchedulePeriod(
              startDate = scheduleStart,
              endDate = scheduleEnd,
              payments = payments :+ balancingPayment,
              isCurrent = false,
              isPast = false
            )
          )
        }
      }

    val periodsWithFlagsCleared = periods.map(_.copy(isCurrent = false, isPast = false))
    val sortedPeriods = periodsWithFlagsCleared.sortBy(_.startDate)

    val currentPeriodOpt =
      sortedPeriods.find(p => !today.isBefore(p.startDate) && !today.isAfter(p.endDate))
        .orElse(sortedPeriods.reverse.headOption)

    val pastPeriodOpt = currentPeriodOpt.flatMap { cp =>
      sortedPeriods
        .filter(_.endDate.isBefore(cp.startDate))
        .sortBy(_.endDate)(Ordering[LocalDate].reverse)
        .headOption
    }

    val updatedPeriods = periodsWithFlagsCleared.map { p =>
      p.copy(
        isCurrent = currentPeriodOpt.contains(p),
        isPast = pastPeriodOpt.contains(p)
      )
    }

    val currentPeriods = updatedPeriods.filter(_.isCurrent)
    val pastPeriods = updatedPeriods.filter(_.isPast)

    val frequencyOpt = currentPeriods.headOption.flatMap { cp =>
      val nonBalancingCount = cp.payments.count(p => !p.isBalancing)
      if (nonBalancingCount >= PaymentFrequency.Monthly.instalments)
        Some(PaymentFrequency.Monthly)
      else if (nonBalancingCount >= PaymentFrequency.Quarterly.instalments)
        Some(PaymentFrequency.Quarterly)
      else
        None
    }

    val nextPaymentOpt = currentPeriods
      .flatMap(_.payments)
      .filter(p => !p.dueDate.isBefore(today))
      .sortBy(_.dueDate)
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
      frequency = frequencyOpt,
      displayName = displayName
    )
  }

   private def processPayments(today: LocalDate,
                              requestItemsSortedByDueDate: List[RequestItem],
                              paymentsHistoryByDueDate: Seq[PaymentHistoryWithDueDate],
                              paymentsDetails: Option[PaymentsWithOptionalOutstanding]): List[AAPayment] =
    requestItemsSortedByDueDate.map { requestItem =>
      val dueDate: LocalDate = LocalDate.parse(requestItem.dueDate)

      val aaTransactionForDueDate: Option[PaymentWithOptionalOutstanding] = paymentsDetails.flatMap { payments =>
        val annualAccountingTransactions = payments.financialTransactions.filter(payment =>
          payment.chargeType == AAQuarterlyInstalments || payment.chargeType == AAMonthlyInstalment
        )
        annualAccountingTransactions.find(_.due == dueDate)
      }

    val status: AAPaymentStatus = getPaymentStatus(today, dueDate, paymentsHistoryByDueDate, aaTransactionForDueDate)

    AAPayment(isBalancing = false, dueDate, Some(requestItem.amount), status)
  }

   private def getPaymentStatus(today: LocalDate,
                               dueDate: LocalDate,
                               paymentsHistoryByDueDate: Seq[PaymentHistoryWithDueDate],
                               aaTransactionForDueDate: Option[PaymentWithOptionalOutstanding]):AAPaymentStatus = {

    val clearedDateForAPaymentThatMatchesDueDate: Option[LocalDate] = paymentsHistoryByDueDate.find(_.dueDate == dueDate).flatMap(_.clearedDate)

    val paymentMatchesAndHasBeenPaidInFull: Boolean = aaTransactionForDueDate.exists(_.chargeHasBeenPaidWithNoOutstanding)
    val paymentHasClearedDateWhichIsAfterDueDate: Boolean = clearedDateForAPaymentThatMatchesDueDate.exists(_.isAfter(dueDate))

    if (paymentMatchesAndHasBeenPaidInFull) {
      if (paymentHasClearedDateWhichIsAfterDueDate) PaidLate else Paid
    } else {
      if (dueDate.isBefore(today)) Overdue else Upcoming
    }
  }

}
