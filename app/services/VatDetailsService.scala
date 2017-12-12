/*
 * Copyright 2017 HM Revenue & Customs
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
import java.time.temporal.ChronoUnit
import javax.inject.{Inject, Singleton}

import cats.data.EitherT
import cats.implicits._
import connectors.{FinancialDataConnector, VatApiConnector}
import connectors.httpParsers.ObligationsHttpParser._
import models.obligations.Obligation.Status._
import models._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatDetailsService @Inject()(connector: VatApiConnector, financialDataConnector: FinancialDataConnector) {

  def retrieveNextDetail[T <: DueDate](data: Seq[T], date: LocalDate = LocalDate.now()): Option[T] = {
    val presetAndFuture = data
      .filter(o => o.due.isEqual(date) || o.due.isAfter(date))
      .sortWith(_.due isBefore _.due).headOption

    val overdue = data
      .filter(_.due.isBefore(date))
      .sortWith(_.due isBefore _.due).lastOption

    presetAndFuture orElse overdue
  }

  def getVatDetails(user: User, date: LocalDate = LocalDate.now())(implicit hc: HeaderCarrier, ec: ExecutionContext)
  : Future[HttpGetResult[VatDetailsModel]] = {
    val numDaysPrior = 0 //90
    val numDaysAhead = 365 //395
    val dateFrom = date.minus(numDaysPrior, ChronoUnit.DAYS)
    val dateTo = date.plus(numDaysAhead, ChronoUnit.DAYS)

    val result = for {
      returnObligation <- EitherT(connector.getObligations(user.vrn, dateFrom, dateTo, Outstanding))
        .map(res => retrieveNextDetail(res.obligations))
      paymentObligation <- EitherT(financialDataConnector.getPaymentData(user.vrn))
        .map(res => retrieveNextDetail(res.payments))
    } yield VatDetailsModel(returnObligation, paymentObligation)

    result.value
  }

}