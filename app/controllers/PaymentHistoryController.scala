/*
 * Copyright 2018 HM Revenue & Customs
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
import config.AppConfig
import javax.inject.{Inject, Singleton}

import models.{ServiceResponse, User}
import models.viewModels.{PaymentsHistoryModel, PaymentsHistoryViewModel}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.{DateService, EnrolmentsAuthService, PaymentsService}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

@Singleton
class PaymentHistoryController @Inject()(val messagesApi: MessagesApi,
                                         val paymentsService: PaymentsService,
                                         dateService: DateService,
                                         val enrolmentsAuthService: EnrolmentsAuthService,
                                         implicit val appConfig: AppConfig,
                                         auditingService: AuditingService)
  extends AuthorisedController with I18nSupport {


  def paymentHistory(year: Int): Action[AnyContent] = authorisedAction { implicit request =>
    user =>
      if (isValidSearchYear(year) && appConfig.features.allowPaymentHistory()) {
        getFinancialTransactions(user, year).map {
          case Right(model) => Ok(views.html.payments.paymentHistory(model))
          case Left(_)      => InternalServerError(views.html.errors.paymentsError())
        }
      } else {
        Future.successful(NotFound(views.html.errors.notFound()))
      }
  }

  private[controllers] def getFinancialTransactions(user: User, selectedYear: Int)
                                                   (implicit hc: HeaderCarrier): Future[ServiceResponse[PaymentsHistoryViewModel]] = {
    val currentYear    = dateService.now().getYear
    val potentialYears = List(currentYear, currentYear - 1)

    def getPaymentHistory(year: Int): Future[(Int, ServiceResponse[Seq[PaymentsHistoryModel]])] = {
      paymentsService.getPaymentsHistory(user, year) map(year -> _)
    }

    def getYearsWithData = Future.sequence(potentialYears map getPaymentHistory) map(_.toMap)

    getYearsWithData map { map =>
      map(selectedYear).right map { transactions =>
        PaymentsHistoryViewModel(
          displayedYears = map.collect { case (year, data) if data.isRight => year }.toSeq,
          selectedYear   = selectedYear,
          transactions   = transactions
        )
      }
    }
  }

  private[controllers] def isValidSearchYear(year: Int, upperBound: Int = dateService.now().getYear) = {
    year <= upperBound && year >= upperBound - 1
  }
}
