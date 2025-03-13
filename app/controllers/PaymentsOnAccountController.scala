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
import scala.concurrent.{ExecutionContext,Future}
import config.ServiceErrorHandler
import models.StandingRequest
import java.time.LocalDate
import views.html.errors.{PaymentsOnAccountError,NotFound}

class PaymentsOnAccountController @Inject()(authorisedController: AuthorisedController,
                                     dateService: DateService,
                                     paymentsOnAccountService: PaymentsOnAccountService,
                                     serviceInfoService: ServiceInfoService,
                                     serviceErrorHandler: ServiceErrorHandler,
                                     paymentsOnAccountError: PaymentsOnAccountError,
                                     mcc: MessagesControllerComponents,
                                     view: PaymentsOnAccountView,
                                     auditService: AuditingService)
                                    (implicit ec: ExecutionContext,
                                     appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport with LoggerUtil {
  import PaymentsOnAccountController._

  def show: Action[AnyContent] = authorisedController.authorisedAction { implicit request => 
    implicit user => {
      if (appConfig.features.poaActiveFeatureEnabled()) {
        (for {
          serviceInfoContent <- serviceInfoService.getPartial
          today = dateService.now()
          standingRequestOpt <- paymentsOnAccountService.getPaymentsOnAccounts(user.vrn)
        } yield {
          standingRequestOpt match { 
            case Some(standingRequest) => 
              val viewModel = buildViewModel(standingRequest, today)
              Ok(view(viewModel,serviceInfoContent))
            case None => serviceErrorHandler.showInternalServerError
          }
        }).recover {
            case e =>
              logger.error(s"Unexpected failure in PaymentsOnAccountController: ${e.getMessage}")
              serviceErrorHandler.showInternalServerError
          }
        } else {
          Future.successful(NotFound(serviceErrorHandler.notFoundTemplate))
        }
      }
    } 
  }

object PaymentsOnAccountController {

  def buildViewModel(standingRequestResponse: StandingRequest, today: LocalDate): PaymentsOnAccountViewModel = {

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
              dueDate = Some(LocalDate.parse(item.dueDate)),
              amount = Some(item.amount)
            )
        }

        val startDate = LocalDate.parse(items.head.startDate)
        val endDate = LocalDate.parse(items.head.endDate)
        val thirdPaymentDueDate = endDate.plusDays(36)

        val thirdPayment = PaymentDetail(
          paymentType = PaymentType.ThirdPayment,
          dueDate = Some(thirdPaymentDueDate),
          amount = None 
        )

        actualPayments :+ thirdPayment
      }

      val startDate = LocalDate.parse(items.head.startDate)
      val endDate = LocalDate.parse(items.head.endDate)

      val adjustedStart = startDate.plusDays(35)
      val adjustedEnd = endDate.plusDays(35)

      val hasPastPeriods = standingRequests.exists(_.requestItems.exists(ri => LocalDate.parse(ri.dueDate).isBefore(today)))

      val isCurrent: Boolean = if (hasPastPeriods) {
         today.isAfter(adjustedStart.minusDays(1)) && today.isBefore(adjustedEnd.plusDays(1))
      } else today.isBefore(adjustedEnd.plusDays(1))

      val isPast = endDate.isBefore(today.minusDays(35))

      VatPeriod(
        startDate = startDate,
        endDate = endDate,
        payments = paymentsWithPlaceholder,
        isCurrent = isCurrent,
        isPast = isPast
      )
    }

  def currentPeriods: List[VatPeriod] = periods.filter(_.isCurrentOrUpcoming).toList
  def pastPeriods: List[VatPeriod] = periods.filter(_.isPast).toList

  val nextPaymentOpt = currentPeriods
    .flatMap(_.payments)
    .filter(_.dueDate.exists(_.isAfter(today)))
    .sortBy(_.dueDate.getOrElse(LocalDate.MAX))
    .headOption

    val orderedPastPeriods = pastPeriods.sortBy(_.endDate)(Ordering[LocalDate].reverse).toList

    PaymentsOnAccountViewModel(
      breathingSpace = false, 
      periods = periods.toList,
      changedOn = mostRecentChangedOn,
      currentPeriods = currentPeriods,
      pastPeriods = orderedPastPeriods,
      nextPayment = nextPaymentOpt
    )
  }
}