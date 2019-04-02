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

import audit.AuditingService
import audit.models.ViewVatPaymentHistoryAuditModel
import config.AppConfig
import javax.inject.{Inject, Singleton}
import models.{ServiceResponse, User}
import models.viewModels.{PaymentsHistoryModel, PaymentsHistoryViewModel}
import java.time.{LocalDate, Period}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.{AccountDetailsService, DateService, EnrolmentsAuthService, PaymentsService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class PaymentHistoryController @Inject()(val messagesApi: MessagesApi,
                                         val paymentsService: PaymentsService,
                                         authorisedController: AuthorisedController,
                                         dateService: DateService,
                                         val enrolmentsAuthService: EnrolmentsAuthService,
                                         val accountDetailsService: AccountDetailsService,
                                         implicit val appConfig: AppConfig,
                                         auditingService: AuditingService)
  extends FrontendController with I18nSupport {

  def paymentHistory(year: Int): Action[AnyContent] = authorisedController.authorisedMigratedUserAction {
    implicit request =>
      implicit user =>

        val customerMigratedToETMPDate: Future[Option[LocalDate]] =
          request.session.get("customerMigratedToETMPDate") match {
            case Some(date) if date.nonEmpty => Future.successful(Some(LocalDate.parse(date)))
            case Some(_) => Future.successful(None)
            case None => accountDetailsService.getAccountDetails(user.vrn) map {
              case Right(details) => details.customerMigratedToETMPDate.map(LocalDate.parse)
              case Left(_) => None
            }
          }

        customerMigratedToETMPDate flatMap { customerMigratedDate =>
          val migratedWithin15M = customerMigratedDate.fold(false)(customerMigratedWithin15M)
          if (isValidSearchYear(year, migratedWithin15M = migratedWithin15M)) {
            getFinancialTransactions(user, year, migratedWithin15M).map {
              case Right(model) =>
                auditEvent(user.vrn, model.transactions, year)
                Ok(views.html.payments.paymentHistory(model))
                  .addingToSession("customerMigratedToETMPDate" -> customerMigratedDate.getOrElse("").toString)
              case Left(error) =>
                Logger.warn("[PaymentHistoryController][paymentHistory] error: " + error.toString)
                InternalServerError(views.html.errors.standardError(appConfig,
                  messagesApi.apply("standardError.title"),
                  messagesApi.apply("standardError.heading"),
                  messagesApi.apply("standardError.message"))
                )
            }
          } else {
            Future.successful(NotFound(views.html.errors.notFound()))
          }
        }
  }

  private[controllers] def customerMigratedWithin15M(date: LocalDate): Boolean = {
    val prevPaymentsMonthLimit = 14
    val monthsSinceMigration = Math.abs(Period.between(dateService.now(), date).toTotalMonths)
    0 to prevPaymentsMonthLimit contains monthsSinceMigration
  }

  private[controllers] def getFinancialTransactions(user: User, selectedYear: Int, migratedWithin15M: Boolean)
                                                   (implicit hc: HeaderCarrier): Future[ServiceResponse[PaymentsHistoryViewModel]] = {
    val currentYear = dateService.now().getYear
    val potentialYears = List(currentYear, currentYear - 1)

    def getPaymentHistory(year: Int): Future[(Int, ServiceResponse[Seq[PaymentsHistoryModel]])] = {
      paymentsService.getPaymentsHistory(user, year) map (year -> _)
    }

    def getYearsWithData = Future.sequence(potentialYears map getPaymentHistory) map (_.toMap)

    getYearsWithData map { yearAndPayments =>
      yearAndPayments.get(selectedYear) match {
        case Some(result) => result.right.map { transactions =>
          PaymentsHistoryViewModel(
            displayedYears = yearAndPayments.collect { case (year, data) if data.isRight && data.right.get.nonEmpty => year }.toSeq,
            selectedYear = selectedYear,
            transactions = transactions,
            migratedToETMPWithin15M = migratedWithin15M
          )
        }
        case None => Right(PaymentsHistoryViewModel(
          displayedYears = yearAndPayments.collect { case (year, data) if data.isRight && data.right.get.nonEmpty => year }.toSeq,
          selectedYear = selectedYear,
          transactions = Seq.empty,
          migratedToETMPWithin15M = migratedWithin15M
        ))
      }
    }
  }

  private[controllers] def isValidSearchYear(year: Int,
                                             upperBound: Int = dateService.now().getYear,
                                             migratedWithin15M: Boolean)(implicit user: User) = {
    val lowerBound = if(migratedWithin15M && user.hasNonMtdVat) 2 else 1
    year <= upperBound && year >= upperBound - lowerBound
  }

  private[controllers] def auditEvent(vrn: String, payments: Seq[PaymentsHistoryModel], year: Int)(implicit hc: HeaderCarrier): Unit =
    auditingService.extendedAudit(ViewVatPaymentHistoryAuditModel(vrn, payments),
      routes.PaymentHistoryController.paymentHistory(year).url)
}
