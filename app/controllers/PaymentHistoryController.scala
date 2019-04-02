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
import connectors.VatSubscriptionConnector
import javax.inject.{Inject, Singleton}
import models.{ServiceResponse, User}
import models.viewModels.{PaymentsHistoryModel, PaymentsHistoryViewModel}
import java.time.{LocalDate, Period}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.{DateService, EnrolmentsAuthService, PaymentsService, AccountDetailsService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.CustomerInfoDataRetriever

import scala.concurrent.Future

@Singleton
class PaymentHistoryController @Inject()(val messagesApi: MessagesApi,
                                         val paymentsService: PaymentsService,
                                         val vatSubscriptionConnector: VatSubscriptionConnector,
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
            case Some(date) if !date.isEmpty => Future.successful(Some(LocalDate.parse(date)))
            case Some(_) => Future.successful(None)
            case _ => accountDetailsService.getAccountDetails(user.vrn) map {
              details => CustomerInfoDataRetriever.retrieveCustomerMigratedToETMPDate(details)
            }
        }

        customerMigratedToETMPDate flatMap {
          customerMigratedDate =>
            if (isValidSearchYear(year)) {
              getFinancialTransactions(user, year, customerMigratedDate).map {
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

  private[controllers] def customerMigratedDateWithin15M(dateOption: Option[LocalDate]): Boolean = {

    dateOption match {
      case Some(date: LocalDate) => Period.between(LocalDate.now(), date).getMonths < 15
      case _ => false
    }

  }

  private[controllers] def getFinancialTransactions(user: User, selectedYear: Int, customerMigratedDate: Option[LocalDate])
                                                   (implicit hc: HeaderCarrier): Future[ServiceResponse[PaymentsHistoryViewModel]] = {
    val currentYear = dateService.now().getYear
    val potentialYears = List(currentYear, currentYear - 1)

    def getPaymentHistory(year: Int): Future[(Int, ServiceResponse[Seq[PaymentsHistoryModel]])] = {
      paymentsService.getPaymentsHistory(user, year) map (year -> _)
    }

    def getYearsWithData = Future.sequence(potentialYears map getPaymentHistory) map (_.toMap)

    getYearsWithData map { yearAndPayments =>
      yearAndPayments(selectedYear).right map {
        transactions =>
          PaymentsHistoryViewModel(
            displayedYears = yearAndPayments.collect { case (year, data) if data.isRight && data.right.get.nonEmpty => year }.toSeq,
            selectedYear = selectedYear,
            transactions = transactions,
            hasHmrcVatDecOrg = user.hasNonMtdVat,
            userVrn = user.vrn,
            customerMigratedToETMPDateWithin15M = customerMigratedDate.fold(false)(date => customerMigratedDateWithin15M(Some(date)))
          )
      }
    }
  }

  private[controllers] def isValidSearchYear(year: Int, upperBound: Int = dateService.now().getYear) = {
    year <= upperBound && year >= upperBound - 1
  }

  private[controllers] def auditEvent(vrn: String, payments: Seq[PaymentsHistoryModel], year: Int)(implicit hc: HeaderCarrier): Unit = {
    auditingService.extendedAudit(ViewVatPaymentHistoryAuditModel(vrn, payments),
      routes.PaymentHistoryController.paymentHistory(year).url)
  }

}

