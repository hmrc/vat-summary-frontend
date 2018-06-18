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

import config.AppConfig
import connectors.{FinancialDataConnector, VatObligationsConnector, VatSubscriptionConnector}
import javax.inject.{Inject, Singleton}
import models._
import models.errors.{NextPaymentError, ObligationsError}
import models.obligations.Obligation.Status._
import models.obligations.VatReturnObligations
import models.payments.Payments
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatDetailsService @Inject()(vatObligationsConnector: VatObligationsConnector,
                                  financialDataConnector: FinancialDataConnector,
                                  subscriptionConnector: VatSubscriptionConnector,
                                  implicit val appConfig: AppConfig,
                                  dateService: DateService) {

  def getReturnObligations(user: User,
                           date: LocalDate)
                          (implicit hc: HeaderCarrier,
                           ec: ExecutionContext): Future[ServiceResponse[Option[VatReturnObligations]]] = {

    val dateFrom = LocalDate.parse("2018-01-01")
    val dateTo = LocalDate.parse("2018-12-31")

    vatObligationsConnector.getVatReturnObligations(user.vrn, dateFrom, dateTo, Outstanding).map {
      case Right(nextReturns) if nextReturns.obligations.nonEmpty => Right(Some(nextReturns))
      case Right(_) => Right(None)
      case Left(_) => Left(ObligationsError)
    }
  }

  def getPaymentObligations(user: User)
                   (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ServiceResponse[Option[Payments]]] = {

    financialDataConnector.getOpenPayments(user.vrn).map {
      case Right(nextPayments) if nextPayments.financialTransactions.nonEmpty=> Right(Some(nextPayments))
      case Right(_) => Right(None)
      case Left(_) => Left(NextPaymentError)
    }
  }
}
