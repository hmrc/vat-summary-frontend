/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.{LocalDate, Period}

import audit.AuditingService
import audit.models.ViewVatPaymentHistoryAuditModel
import common.SessionKeys
import config.{AppConfig, ServiceErrorHandler}
import javax.inject.{Inject, Singleton}
import models.viewModels.{PaymentsHistoryModel, PaymentsHistoryViewModel}
import models.{ServiceResponse, User}
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import services._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.payments.PaymentHistory

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentHistoryController @Inject()(val paymentsService: PaymentsService,
                                         authorisedController: AuthorisedController,
                                         dateService: DateService,
                                         serviceInfoService: ServiceInfoService,
                                         val enrolmentsAuthService: EnrolmentsAuthService,
                                         accountDetailsService: AccountDetailsService,
                                         serviceErrorHandler: ServiceErrorHandler,
                                         mcc: MessagesControllerComponents,
                                         implicit  val ec: ExecutionContext,
                                         paymentHistoryView: PaymentHistory)
                                        (implicit val appConfig: AppConfig,
                                         auditingService: AuditingService)
  extends FrontendController(mcc) with I18nSupport {

  def currentYear: Int = dateService.now().getYear
  def previousYear: Int = currentYear - 1

  def paymentHistory(): Action[AnyContent] = authorisedController.authorisedMigratedUserAction { implicit request =>
    implicit user =>
      for {
        migrationDate <- getMigratedToETMPDate
        serviceInfoContent <- serviceInfoService.getPartial
        validYears = getValidYears(user.vrn, migrationDate)
        migratedWithin15Months = customerMigratedWithin15M(migrationDate)
        paymentsServiceYearOne <-
          if (validYears.contains(currentYear)) { paymentsService.getPaymentsHistory(user.vrn, validYears.head) }
          else { Future.successful(Right(Seq.empty)) }
        paymentsServiceYearTwo <-
          if (validYears.contains(previousYear)) { paymentsService.getPaymentsHistory(user.vrn, validYears.drop(1).head) }
          else { Future.successful(Right(Seq.empty)) }
      } yield {
        val showPreviousPaymentsTab: Boolean = migratedWithin15Months && user.hasNonMtdVat
        generateViewModel(paymentsServiceYearOne, paymentsServiceYearTwo, showPreviousPaymentsTab, migrationDate) match {
          case Some(model) =>
            auditEvent(user.vrn, model.transactions)
            Ok(paymentHistoryView(model, serviceInfoContent))
          case None =>
            Logger.warn("[PaymentHistoryController][paymentHistory] error generating view model")
            serviceErrorHandler.showInternalServerError
        }
      }
  }

  private[controllers] def getMigratedToETMPDate(implicit request: Request[_], user: User): Future[Option[LocalDate]] =
    request.session.get(SessionKeys.migrationToETMP) match {
      case Some(date) if date.nonEmpty => Future.successful(Some(LocalDate.parse(date)))
      case Some(_) => Future.successful(None)
      case None => accountDetailsService.getAccountDetails(user.vrn) map {
        case Right(details) => details.customerMigratedToETMPDate.map(LocalDate.parse)
        case Left(_) => None
      }
    }

  private[controllers] def customerMigratedWithin15M(migrationDate: Option[LocalDate]): Boolean =
    migrationDate match {
      case Some(date) =>
        val prevPaymentsMonthLimit = 14
        val monthsSinceMigration = Math.abs(Period.between(dateService.now(), date).toTotalMonths)
        0 to prevPaymentsMonthLimit contains monthsSinceMigration
      case None => false
    }

  private[controllers] def getValidYears(vrn: String,
                                         migrationDate: Option[LocalDate])(implicit user: User): Seq[Int] =
    migrationDate match {
      case Some(date) if date.getYear == currentYear => Seq(currentYear)
      case _ => Seq(currentYear, previousYear)
    }

  private[controllers] def generateViewModel(paymentsServiceYearOne: ServiceResponse[Seq[PaymentsHistoryModel]],
                                             paymentsServiceYearTwo: ServiceResponse[Seq[PaymentsHistoryModel]],
                                             showPreviousPaymentsTab: Boolean,
                                             customerMigratedToETMPDate: Option[LocalDate]): Option[PaymentsHistoryViewModel] =
    (paymentsServiceYearOne, paymentsServiceYearTwo) match {
      case (Right(yearOneTrans), Right(yearTwoTrans)) =>
        val migratedThisYear: Boolean = customerMigratedToETMPDate.fold(false)(_.getYear == currentYear)
        val tabOne: Int = currentYear
        val tabTwo: Option[Int] = if(migratedThisYear) None else Some(previousYear)
        val transactions = (yearOneTrans ++ yearTwoTrans).distinct
        Some(PaymentsHistoryViewModel(
          tabOne,
          tabTwo,
          showPreviousPaymentsTab,
          transactions
        ))
      case _ => None
  }

  private[controllers] def auditEvent(vrn: String, payments: Seq[PaymentsHistoryModel])
                                     (implicit hc: HeaderCarrier): Unit =
    auditingService.extendedAudit(ViewVatPaymentHistoryAuditModel(vrn, payments),
      routes.PaymentHistoryController.paymentHistory().url)
}
