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

import java.time.{LocalDate, Period}
import audit.AuditingService
import audit.models.ViewVatPaymentHistoryAuditModel
import common.SessionKeys
import config.{AppConfig, ServiceErrorHandler}
import connectors.httpParsers.ResponseHttpParsers.HttpResult
import javax.inject.{Inject, Singleton}
import models.viewModels.{PaymentsHistoryModel, PaymentsHistoryViewModel}
import models.{CustomerInformation, ServiceResponse}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.payments.PaymentHistory
import scala.concurrent.ExecutionContext

@Singleton
class PaymentHistoryController @Inject()(paymentsService: PaymentsService,
                                         authorisedController: AuthorisedController,
                                         dateService: DateService,
                                         serviceInfoService: ServiceInfoService,
                                         accountDetailsService: AccountDetailsService,
                                         serviceErrorHandler: ServiceErrorHandler,
                                         mcc: MessagesControllerComponents,
                                         paymentHistoryView: PaymentHistory,
                                         auditingService: AuditingService)
                                        (implicit appConfig: AppConfig,
                                         ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  def currentYear: Int = dateService.now().getYear
  def previousYear: Int = currentYear - 1

  def paymentHistory: Action[AnyContent] = authorisedController.financialAction { implicit request =>
    implicit user =>
      for {
        customerInfo <- accountDetailsService.getAccountDetails(user.vrn)
        migrationDate = getMigratedToETMPDate(customerInfo)
        hybridToFullMigrationDate = getHybridToFullMigrationDate(customerInfo)
        showInsolvencyContent = showInsolventContent(customerInfo)
        serviceInfoContent <- serviceInfoService.getPartial
        migratedWithin15Months = customerMigratedWithin15M(migrationDate)
        payments <- paymentsService.getPaymentsHistory(user.vrn, dateService.now(), migrationDate)
      } yield {
        val clientName = request.session.get(SessionKeys.mtdVatvcAgentClientName)
        val showPreviousPaymentsTab: Boolean = (migratedWithin15Months || migrationDate.isEmpty) && user.hasNonMtdVat
        generateViewModel(
          payments,
          showPreviousPaymentsTab,
          migrationDate,
          showInsolvencyContent,
          clientName
        ) match {
          case Some(model) =>
            auditEvent(user.vrn, model.transactions)
            Ok(paymentHistoryView(model, serviceInfoContent, checkIfMigrationWithinLastThreeYears(hybridToFullMigrationDate)))
          case None =>
            logger.warn("[PaymentHistoryController][paymentHistory] error generating view model")
            serviceErrorHandler.showInternalServerError
        }
      }
  }

  private[controllers] def getMigratedToETMPDate(customerInfo: HttpResult[CustomerInformation]): Option[LocalDate] =
    customerInfo match {
      case Right(information) => information.extractDate.map(LocalDate.parse)
      case Left(_) => None
    }

  private[controllers] def getHybridToFullMigrationDate(customerInfo: HttpResult[CustomerInformation]): Option[LocalDate] =
    customerInfo match {
      case Right(information) => information.hybridToFullMigrationDate.map(LocalDate.parse)
      case Left(_) => None
    }

  private[controllers] def showInsolventContent(customerInfo: HttpResult[CustomerInformation]): Boolean =
    customerInfo match {
      case Right(information) if information.details.isInsolvent =>
        !information.details.exemptInsolvencyTypes.contains(information.details.insolvencyType.getOrElse(""))
      case _ => false
    }

  private[controllers] def customerMigratedWithin15M(migrationDate: Option[LocalDate]): Boolean =
    migrationDate match {
      case Some(date) =>
        val prevPaymentsMonthLimit: Int = 14
        val monthsSinceMigration: Int = Math.abs(Period.between(dateService.now(), date).toTotalMonths.toInt)
        0 to prevPaymentsMonthLimit contains monthsSinceMigration
      case None => false
    }

  private[controllers] def generateViewModel(paymentsServiceResponse: ServiceResponse[Seq[PaymentsHistoryModel]],
                                             showPreviousPaymentsTab: Boolean,
                                             migrationDate: Option[LocalDate],
                                             showInsolvencyContent: Boolean,
                                             clientName: Option[String]): Option[PaymentsHistoryViewModel] =
    paymentsServiceResponse match {
      case Right(trans) =>
        val migratedThisYear: Boolean = migrationDate.fold(false)(_.getYear == currentYear)
        val migratedPreviousYear: Boolean = migrationDate.fold(false)(_.getYear == previousYear)
        val tabOne: Int = currentYear
        val tabTwo: Option[Int] = if(migratedThisYear) None else Some(previousYear)
        val tabThree: Option[Int] = if(migratedPreviousYear || migratedThisYear) None else Some(previousYear - 1)
        val transactions = trans.distinct.filter(model => isLast24Months(model.clearedDate))
        Some(PaymentsHistoryViewModel(
          tabOne,
          tabTwo,
          tabThree,
          showPreviousPaymentsTab,
          transactions,
          showInsolvencyContent,
          clientName
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
      routes.PaymentHistoryController.paymentHistory.url)

  private[controllers] def checkIfMigrationWithinLastThreeYears(migrationDate: Option[LocalDate]): Boolean = {
    migrationDate match {
      case Some(date) => date.isAfter(dateService.now().minusMonths(36))
      case _ => false
    }
  }
}
