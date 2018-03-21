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

package services

import java.time.LocalDate
import javax.inject.{Inject, Singleton}

import cats.data.EitherT
import cats.implicits._
import config.AppConfig
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import connectors.{FinancialDataConnector, VatApiConnector, VatSubscriptionConnector}
import models._
import models.obligations.Obligation
import models.obligations.Obligation.Status._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatDetailsService @Inject()(vatApiConnector: VatApiConnector,
                                  financialDataConnector: FinancialDataConnector,
                                  subscriptionConnector: VatSubscriptionConnector,
                                  implicit val appConfig: AppConfig,
                                  dateService: DateService) {

  private[services] def getNextObligation[T <: Obligation](obligations: Seq[T], date: LocalDate): Option[T] = {
    val presetAndFuture = obligations
      .filter(obligation => obligation.due == date || obligation.due.isAfter(date))
      .sortWith(_.due isBefore _.due).headOption

    val overdue = obligations
      .filter(_.due.isBefore(date))
      .sortWith(_.due isBefore _.due).lastOption

    presetAndFuture orElse overdue
  }

  def getVatDetails(user: User,
                    date: LocalDate)
                   (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[VatDetailsModel]] = {
    // Static 2018 date range for MVP
    val dateFrom = LocalDate.parse("2018-01-01")
    val dateTo = LocalDate.parse("2018-12-31")

    val result = for {
      nextReturn <- EitherT(vatApiConnector.getVatReturnObligations(user.vrn, dateFrom, dateTo, Outstanding))
        .map(obligations => getNextObligation(obligations.obligations, date))
      nextPayment <- EitherT(financialDataConnector.getOpenPayments(user.vrn))
        .map(payments => getNextObligation(payments.financialTransactions, date)
        .filter(payment => payment.outstandingAmount > 0))
    } yield VatDetailsModel(nextPayment, nextReturn)

    result.value
  }
}
