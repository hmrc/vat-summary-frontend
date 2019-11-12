/*
 * Copyright 2019 HM Revenue & Customs
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
import config.AppConfig
import javax.inject.{Inject, Singleton}
import models.{ServiceResponse, User}
import models.viewModels.{PaymentsHistoryModel, PaymentsHistoryViewModel}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Request}
import services.{AccountDetailsService, DateService, EnrolmentsAuthService, PaymentsService, ServiceInfoService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class PaymentHistoryController @Inject()(val messagesApi: MessagesApi,
                                         val paymentsService: PaymentsService,
                                         authorisedController: AuthorisedController,
                                         dateService: DateService,
                                         serviceInfoService: ServiceInfoService,
                                         val enrolmentsAuthService: EnrolmentsAuthService,
                                         accountDetailsService: AccountDetailsService)
                                        (implicit val appConfig: AppConfig,
                                         auditingService: AuditingService)
  extends FrontendController with I18nSupport {

  def currentYear: Int = dateService.now().getYear
  def previousYear: Int = currentYear - 1

  def paymentHistory(year: Int): Action[AnyContent] = authorisedController.authorisedMigratedUserAction { implicit request =>
    implicit user =>
       for {
        migrationDate <- getMigratedToETMPDate
        serviceInfoContent <- serviceInfoService.getPartial
        validYears = getValidYears(user.vrn, migrationDate, Some(year))
        migratedWithin15Months = customerMigratedWithin15M(migrationDate)
        paymentsServiceYearOne <-
          if (validYears.contains(year)) { paymentsService.getPaymentsHistory(user.vrn, validYears.head) }
          else { Future.successful(Right(Seq.empty)) }
        paymentsServiceYearTwo <-
          if (validYears.length == 2 && validYears.contains(year)) { paymentsService.getPaymentsHistory(user.vrn, validYears.drop(1).head) }
          else { Future.successful(Right(Seq.empty)) }
      } yield {
        if(validYears.isEmpty) {
          NotFound(views.html.errors.notFound())
        } else {
          val showPreviousPaymentsTab: Boolean = migratedWithin15Months && user.hasNonMtdVat
          generateViewModel(paymentsServiceYearOne, paymentsServiceYearTwo, showPreviousPaymentsTab, year, migrationDate) match {
            case Some(model) =>
              auditEvent(user.vrn, model.transactions, year)
              Ok(views.html.payments.paymentHistory(model, serviceInfoContent))
            case None =>
              Logger.warn("[PaymentHistoryController][paymentHistory] error generating view model")
              InternalServerError(views.html.errors.standardError(appConfig,
                messagesApi.apply("standardError.title"),
                messagesApi.apply("standardError.heading"),
                messagesApi.apply("standardError.message"))
              )
          }
        }
      }
  }

  def previousPayments(): Action[AnyContent] = authorisedController.authorisedMigratedUserAction { implicit request =>
    implicit user =>
      for {
        migrationDate <- getMigratedToETMPDate
        serviceInfoContent <- serviceInfoService.getPartial
        migratedWithin15Months = customerMigratedWithin15M(migrationDate)
      } yield {
        if (migratedWithin15Months && user.hasNonMtdVat) {
          val validYears = getValidYears(user.vrn, migrationDate, None)
          val tabOne = validYears.headOption
          val tabTwo = validYears.drop(1).headOption
          val model = PaymentsHistoryViewModel(
            tabOne,
            tabTwo,
            previousPaymentsTab = true,
            None,
            Seq.empty,
            currentYear
          )
          Ok(views.html.payments.paymentHistory(model, serviceInfoContent))
        } else {
          NotFound(views.html.errors.notFound())
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
                                         migrationDate: Option[LocalDate],
                                         givenYear: Option[Int])(implicit user: User): Seq[Int] =
    (migrationDate, givenYear) match {
      case (_, Some(year)) if year > currentYear | year < previousYear => Seq.empty
      case (Some(date), _) if date.getYear == currentYear => Seq(currentYear)
      case _ => Seq(currentYear, currentYear - 1)
    }

  private[controllers] def generateViewModel(paymentsServiceYearOne: ServiceResponse[Seq[PaymentsHistoryModel]],
                                             paymentsServiceYearTwo: ServiceResponse[Seq[PaymentsHistoryModel]],
                                             showPreviousPaymentsTab: Boolean,
                                             selectedYear: Int,
                                             customerMigratedToETMPDate: Option[LocalDate]): Option[PaymentsHistoryViewModel] =
    (paymentsServiceYearOne, paymentsServiceYearTwo) match {
      case (Right(yearOneTrans), Right(yearTwoTrans)) =>
        val migratedThisYear = customerMigratedToETMPDate.fold(false)(_.getYear == currentYear)
        val (tabOne, tabTwo) =
          generateTabs(yearOneTrans.isEmpty, yearTwoTrans.isEmpty, showPreviousPaymentsTab, migratedThisYear)
        val transactions = yearOneTrans ++ yearTwoTrans
        if (yearTwoTrans.isEmpty && selectedYear == previousYear && !showPreviousPaymentsTab) {
          None
        } else {
          Some(PaymentsHistoryViewModel(
            tabOne,
            tabTwo,
            showPreviousPaymentsTab,
            Some(selectedYear),
            transactions,
            currentYear
          ))
        }
      case _ => None
  }

  def generateTabs(yearOneEmpty: Boolean,
                   yearTwoEmpty: Boolean,
                   showPreviousPaymentsTab: Boolean,
                   migratedThisYear: Boolean): (Option[Int], Option[Int]) =
    (yearOneEmpty, yearTwoEmpty) match {
      case _ if showPreviousPaymentsTab && migratedThisYear => (Some(currentYear), None)
      case _ if showPreviousPaymentsTab => (Some(currentYear), Some(previousYear))
      case (true, true) => (None, None)
      case (false, true) => (Some(currentYear), None)
      case _ => (Some(currentYear), Some(previousYear))
    }

  private[controllers] def auditEvent(vrn: String, payments: Seq[PaymentsHistoryModel], year: Int)
                                     (implicit hc: HeaderCarrier): Unit =
    auditingService.extendedAudit(ViewVatPaymentHistoryAuditModel(vrn, payments),
      routes.PaymentHistoryController.paymentHistory(year = year).url)
}
