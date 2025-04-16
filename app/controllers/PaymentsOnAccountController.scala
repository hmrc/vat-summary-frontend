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
import com.google.inject.Inject
import config.AppConfig
import models.viewModels._
import play.api.i18n.I18nSupport
import services._
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.payments.{PaymentsOnAccountView}
import scala.concurrent.{ExecutionContext, Future}
import config.ServiceErrorHandler
import models.StandingRequest
import java.time.LocalDate
import views.html.errors.{PaymentsOnAccountError, NotFound}
import models.obligations.VatReturnObligations

class PaymentsOnAccountController @Inject() (
    authorisedController: AuthorisedController,
    dateService: DateService,
    paymentsOnAccountService: PaymentsOnAccountService,
    serviceInfoService: ServiceInfoService,
    serviceErrorHandler: ServiceErrorHandler,
    paymentsOnAccountError: PaymentsOnAccountError,
    vatDetailService: VatDetailsService,
    mcc: MessagesControllerComponents,
    view: PaymentsOnAccountView,
    auditService: AuditingService
)(implicit ec: ExecutionContext, appConfig: AppConfig)
    extends FrontendController(mcc)
    with I18nSupport
    with LoggerUtil {
  import PaymentsOnAccountController._

  def show: Action[AnyContent] =
    authorisedController.authorisedActionAllowAgents {
      implicit request => implicit user =>
        {
          if (appConfig.features.poaActiveFeatureEnabled()) {
            (for {
              serviceInfoContent <- serviceInfoService.getPartial
              today = dateService.now()
              standingRequestOpt <- paymentsOnAccountService
                .getPaymentsOnAccounts(user.vrn)
              obligationsResult <- vatDetailService.getReturnObligations(
                user.vrn
              )
            } yield {
              standingRequestOpt match {
                case Some(standingRequest) =>
                  val obligationsOpt = obligationsResult.toOption.flatten
                  val viewModel =
                    buildViewModel(standingRequest, today, obligationsOpt)
                  logger.info(s"[PaymentsOnAccountController] [show] successfully rendering POA page for ${user.vrn}")
                  Ok(view(viewModel, serviceInfoContent))
                case None => 
                  logger.error(
                    s"Standingrequest API returned None for ${user.vrn}"
                  )
                  serviceErrorHandler.showInternalServerError
              }
            }).recover { case e =>
              logger.error(
                s"Unexpected failure in PaymentsOnAccountController: ${e.getMessage} For: ${user.vrn}"
              )
              serviceErrorHandler.showInternalServerError
            }
          } else {
            Future.successful(NotFound(serviceErrorHandler.notFoundTemplate))
          }
        }
    }
}

object PaymentsOnAccountController {

  def buildViewModel(
      standingRequestResponse: StandingRequest,
      today: LocalDate,
      returnObligations: Option[VatReturnObligations] = None
  ): PaymentsOnAccountViewModel = {

  val standingRequests = standingRequestResponse.standingRequests

  val mostRecentChangedOn: Option[LocalDate] = standingRequests
    .flatMap(req => req.changedOn.map(c => LocalDate.parse(c.trim)))
    .sorted(Ordering[LocalDate].reverse)
    .headOption

  val periods = standingRequests
    .flatMap(_.requestItems)
    .groupBy(_.periodKey)
    .toSeq.sortBy(_._1)
    .map { case (periodKey, items) =>

      val sortedPayments = items.sortBy(_.period)

      val paymentsWithPlaceholder = {
        val actualPayments = sortedPayments.take(2).zipWithIndex.map { case (item, index) =>
            PaymentDetail(
              paymentType = if (index == 0) PaymentType.FirstPayment else PaymentType.SecondPayment,
              dueDate = DueDate(Some(LocalDate.parse(item.dueDate))),
              amount = Some(item.amount)
            )
        }

        val startDate = LocalDate.parse(items.head.startDate)
        val endDate = LocalDate.parse(items.head.endDate)
        val fallbackDueDate = endDate.plusDays(36)

val thirdPaymentDueDate: Option[LocalDate] =
  returnObligations.flatMap { obligations =>
    val matchOpt = obligations.obligations.find { ob =>
      val overlaps =
        !ob.periodTo.isBefore(startDate) && !ob.periodFrom.isAfter(endDate)
      overlaps
    }
    matchOpt.map(_.due)
  }

        val thirdPayment = PaymentDetail(
          paymentType = PaymentType.ThirdPayment,
          dueDate = DueDate(Some(fallbackDueDate), thirdPaymentDueDate),
          amount = None
        )

        actualPayments :+ thirdPayment
      }

      val startDate = LocalDate.parse(items.head.startDate)
      val endDate = LocalDate.parse(items.head.endDate)

      val isCurrent = !today.isBefore(startDate.plusDays(35)) && !today.isAfter(endDate.plusDays(35))

      val isPast = endDate.isBefore(today.minusDays(35))

      VatPeriod(
        startDate = startDate,
        endDate = endDate,
        payments = paymentsWithPlaceholder,
        isCurrent = isCurrent,
        isPast = isPast
      )
    }

  val hasPastPeriods = periods.exists(_.isPast)

  val currentPeriodOpt = if (hasPastPeriods) {
    periods
      .sortBy(_.startDate)
      .find(p => p.startDate.isBefore(today.plusDays(1)) && !p.isPast)
  } else {
    periods.find(_.startDate.isBefore(today.plusDays(1)))
  }

  val updatedPeriods = periods.map(period =>
    period.copy(isCurrent = currentPeriodOpt.contains(period))
  )

  val currentPeriods = updatedPeriods.filter(_.isCurrentOrUpcoming).toList
  val pastPeriods = updatedPeriods.filter(_.isPast).toList

  val nextPaymentOpt = currentPeriods
    .flatMap(_.payments)
    .filter(_.dueDate.dueDate.exists(_.isAfter(today)))
    .sortBy(_.dueDate.dueDate.getOrElse(LocalDate.MAX))
    .headOption

  val orderedPastPeriods = pastPeriods.sortBy(_.endDate)(Ordering[LocalDate].reverse).toList

  PaymentsOnAccountViewModel(
    breathingSpace = false,
    periods = updatedPeriods.toList,
    changedOn = mostRecentChangedOn,
    currentPeriods = currentPeriods,
    pastPeriods = orderedPastPeriods,
    nextPayment = nextPaymentOpt
  )
  }
}
