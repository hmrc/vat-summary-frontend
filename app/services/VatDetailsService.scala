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
import connectors.httpParsers.VatReturnObligationsHttpParser._
import connectors.{FinancialDataConnector, VatApiConnector}
import models.VatReturnObligation.Status._
import models._
import models.viewModels.VatDetailsModel
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatDetailsService @Inject()(vatApiConnector: VatApiConnector, financialDataConnector: FinancialDataConnector) {

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
                    date: LocalDate = LocalDate.now())
                   (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[VatDetailsModel]] = {
    val numDaysPrior = 0
    val numDaysAhead = 365
    val dateFrom = date.minusDays(numDaysPrior)
    val dateTo = date.plusDays(numDaysAhead)

    val result = for {
      nextReturn <- EitherT(vatApiConnector.getVatReturnObligations(user.vrn, dateFrom, dateTo, Outstanding))
        .map(obligations => getNextObligation(obligations.obligations, date))
      nextPayment <- EitherT(financialDataConnector.getPaymentsForVatReturns(user.vrn))
        .map(payments => getNextObligation(payments.payments, date))
    } yield VatDetailsModel(nextReturn, nextPayment)

    result.value
  }

  def getTradingName(user: User): Future[String] = {
    vatApiConnector.getTradingName(user.vrn)
  }
}