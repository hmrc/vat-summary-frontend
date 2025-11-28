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
    mcc: MessagesControllerComponents,
    view: AnnualAccountingView,
    auditService: AuditingService
)(implicit ec: ExecutionContext, appConfig: AppConfig)
    extends FrontendController(mcc)
    with I18nSupport
    with LoggerUtil {

  import AnnualAccountingController._

  def show: Action[AnyContent] = authorisedController.authorisedActionAllowAgents { implicit request => implicit user =>
    if (appConfig.features.poaScheduleFeature()) {
      (for {
        serviceInfoContent <- serviceInfoService.getPartial
        today = dateService.now()
        standingRequestOpt <- paymentsOnAccountService.getPaymentsOnAccounts(user.vrn)
        obligationsResult <- vatDetailService.getReturnObligations(user.vrn)
        ddStatusResult <- paymentsService.getDirectDebitStatus(user.vrn)
        viewResult <- handleData(serviceInfoContent, today, standingRequestOpt, obligationsResult, ddStatusResult, view)
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
      view: AnnualAccountingView
  )(implicit request: Request[AnyContent], user: User): Future[Result] = {
    standingRequestOpt match {
      case Some(standingRequest) =>
        val obligationsOpt = obligationsResult.toOption.flatten
        val hasDirectDebit: Option[Boolean] = ddStatusResult.toOption.map(_.directDebitMandateFound)
        val viewModel = buildViewModel(standingRequest, today, obligationsOpt, user.isAgent, hasDirectDebit)
        logger.info(s"[AnnualAccountingController][show] rendering AA page for ${user.vrn}")
        Future.successful(Ok(view(viewModel, serviceInfoContent)))
      case None =>
        logger.error(s"Standingrequest API returned None for ${user.vrn} (AA)")
        serviceErrorHandler.showInternalServerError
    }
  }
}

object AnnualAccountingController {
  import models.ChangedOnVatPeriod.RequestCategoryType4

  def buildViewModel(
      standingRequestResponse: StandingRequest,
      today: LocalDate,
      returnObligations: Option[VatReturnObligations],
      isAgent: Boolean,
      hasDirectDebit: Option[Boolean]
  ): AnnualAccountingViewModel = {
    val aaStandingRequests = standingRequestResponse.standingRequests.filter(_.requestCategory == RequestCategoryType4)

    val periods = aaStandingRequests
      .flatMap(_.requestItems)
      .groupBy(_.periodKey)
      .toSeq.sortBy(_._1)
      .map { case (_, items) =>
        val sortedByPeriod = items.sortBy(_.period)
        val startDate = LocalDate.parse(sortedByPeriod.head.startDate)
        val endDate = LocalDate.parse(sortedByPeriod.last.endDate)

        val payments = sortedByPeriod.map { item =>
          val dueDate = LocalDate.parse(item.dueDate)
          val status = if (dueDate.isAfter(today) || dueDate.isEqual(today)) AAPaymentStatus.Upcoming else AAPaymentStatus.Paid
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
          status = if (balancingDue.exists(!_.isAfter(today))) AAPaymentStatus.Paid else AAPaymentStatus.Upcoming
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
    val pastPeriods = updatedPeriods.filter(_.isPast).sortBy(_.endDate)(Ordering[LocalDate].reverse).toList

    val nextPaymentOpt = currentPeriods
      .flatMap(_.payments)
      .filter(_.dueDate.isAfter(today))
      .sortBy(_.dueDate)
      .headOption

    val mostRecentChangedOn: Option[LocalDate] = aaStandingRequests.flatMap(_.changedOn.map(_.trim)).map(LocalDate.parse).sorted(Ordering[LocalDate].reverse).headOption

    AnnualAccountingViewModel(
      changedOn = mostRecentChangedOn,
      currentPeriods = currentPeriods,
      pastPeriods = pastPeriods,
      nextPayment = nextPaymentOpt,
      isAgent = isAgent,
      hasDirectDebit = hasDirectDebit
    )
  }
}
