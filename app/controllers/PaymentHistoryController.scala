/*
 * Copyright 2021 HM Revenue & Customs
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
import config.{AppConfig, ServiceErrorHandler}
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import javax.inject.{Inject, Singleton}
import models.viewModels.{PaymentsHistoryModel, PaymentsHistoryViewModel}
import models.{CustomerInformation, ServiceResponse}
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
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

  def paymentHistory(): Action[AnyContent] = authorisedController.financialAction { implicit request =>
    implicit user =>
      for {
        customerInfo <- accountDetailsService.getAccountDetails(user.vrn)
        migrationDate = getMigratedToETMPDate(customerInfo)
        showInsolvencyContent = showInsolventContent(customerInfo)
        serviceInfoContent <- serviceInfoService.getPartial
        validYears = getValidYears(migrationDate)
        migratedWithin15Months = customerMigratedWithin15M(migrationDate)
        paymentsServiceYearOne <-
          if (validYears.contains(currentYear)) { paymentsService.getPaymentsHistory(user.vrn, validYears.head) }
          else { Future.successful(Right(Seq.empty)) }
        paymentsServiceYearTwo <-
          if (validYears.contains(previousYear)) { paymentsService.getPaymentsHistory(user.vrn, validYears.drop(1).head) }
          else { Future.successful(Right(Seq.empty)) }
        paymentsServiceYearThree <-
          if (validYears.contains(currentYear - 2)) { paymentsService.getPaymentsHistory(user.vrn, validYears.drop(2).head) }
          else { Future.successful(Right(Seq.empty)) }
      } yield {
        val showPreviousPaymentsTab: Boolean = (migratedWithin15Months || migrationDate.isEmpty) && user.hasNonMtdVat
        generateViewModel(
          paymentsServiceYearOne,
          paymentsServiceYearTwo,
          paymentsServiceYearThree,
          showPreviousPaymentsTab,
          migrationDate,
          showInsolvencyContent
        ) match {
          case Some(model) =>
            auditEvent(user.vrn, model.transactions)
            Ok(paymentHistoryView(model, serviceInfoContent))
          case None =>
            Logger.warn("[PaymentHistoryController][paymentHistory] error generating view model")
            serviceErrorHandler.showInternalServerError
        }
      }
  }

  private[controllers] def getMigratedToETMPDate(customerInfo: HttpGetResult[CustomerInformation]): Option[LocalDate] =
    customerInfo match {
      case Right(information) => information.extractDate.map(LocalDate.parse)
      case Left(_) => None
    }

  private[controllers] def showInsolventContent(customerInfo: HttpGetResult[CustomerInformation]): Boolean =
    customerInfo match {
      case Right(information) if information.details.isInsolvent =>
        !information.details.exemptInsolvencyTypes.contains(information.details.insolvencyType.getOrElse(""))
      case _ => false
    }

  private[controllers] def customerMigratedWithin15M(migrationDate: Option[LocalDate]): Boolean =
    migrationDate match {
      case Some(date) =>
        val prevPaymentsMonthLimit = 14
        val monthsSinceMigration = Math.abs(Period.between(dateService.now(), date).toTotalMonths)
        0 to prevPaymentsMonthLimit contains monthsSinceMigration
      case None => false
    }

  private[controllers] def getValidYears(migrationDate: Option[LocalDate]): Seq[Int] =
    migrationDate match {
      case Some(date) if date.getYear == currentYear => Seq(currentYear)
      case Some(date) if date.getYear == previousYear => Seq(currentYear, previousYear)
      case _ => Seq(currentYear, previousYear, currentYear - 2)
    }

  private[controllers] def generateViewModel(paymentsServiceYearOne: ServiceResponse[Seq[PaymentsHistoryModel]],
                                             paymentsServiceYearTwo: ServiceResponse[Seq[PaymentsHistoryModel]],
                                             paymentsServiceYearThree: ServiceResponse[Seq[PaymentsHistoryModel]],
                                             showPreviousPaymentsTab: Boolean,
                                             customerMigratedToETMPDate: Option[LocalDate],
                                             showInsolvencyContent: Boolean): Option[PaymentsHistoryViewModel] =
    (paymentsServiceYearOne, paymentsServiceYearTwo, paymentsServiceYearThree) match {
      case (Right(yearOneTrans), Right(yearTwoTrans), Right(yearThreeTrans)) =>
        val migratedThisYear: Boolean = customerMigratedToETMPDate.fold(false)(_.getYear == currentYear)
        val migratedPreviousYear: Boolean = customerMigratedToETMPDate.fold(false)(_.getYear == previousYear)
        val tabOne: Int = currentYear
        val tabTwo: Option[Int] = if(migratedThisYear) None else Some(previousYear)
        val tabThree: Option[Int] = if(migratedPreviousYear || migratedThisYear) None else Some(previousYear - 1)
        val transactions =
          (yearOneTrans ++ yearTwoTrans ++ yearThreeTrans).distinct.filter(model => isLast24Months(model.clearedDate))
        Some(PaymentsHistoryViewModel(
          tabOne,
          tabTwo,
          tabThree,
          showPreviousPaymentsTab,
          transactions,
          showInsolvencyContent
        ))
      case _ => None
  }

  private[controllers] def isLast24Months(input: Option[LocalDate]): Boolean = {
    val monthsToCheck: Int = 24
    val currentDate: LocalDate = dateService.now()
    input.fold(true) { inputDate =>
      inputDate.isAfter(currentDate.minusMonths(monthsToCheck)) || inputDate.isEqual(currentDate.minusMonths(monthsToCheck))
    }
  }

  private[controllers] def auditEvent(vrn: String, payments: Seq[PaymentsHistoryModel])
                                     (implicit hc: HeaderCarrier): Unit =
    auditingService.extendedAudit(ViewVatPaymentHistoryAuditModel(vrn, payments),
      routes.PaymentHistoryController.paymentHistory().url)
}
