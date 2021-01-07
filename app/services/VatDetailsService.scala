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

package services

import java.time.LocalDate

import config.AppConfig
import connectors.{FinancialDataConnector, VatObligationsConnector}
import javax.inject.{Inject, Singleton}
import models.ServiceResponse
import models.errors.{NextPaymentError, ObligationsError}
import models.obligations.Obligation.Status._
import models.obligations.VatReturnObligations
import models.payments.Payments
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatDetailsService @Inject()(vatObligationsConnector: VatObligationsConnector,
                                  financialDataConnector: FinancialDataConnector,
                                  implicit val appConfig: AppConfig) {

  def getReturnObligations(vrn: String, date: LocalDate)
                          (implicit hc: HeaderCarrier,
                           ec: ExecutionContext): Future[ServiceResponse[Option[VatReturnObligations]]] =

    vatObligationsConnector.getVatReturnObligations(vrn, Outstanding).map {
      case Right(nextReturns) if nextReturns.obligations.nonEmpty => Right(Some(nextReturns))
      case Right(_) => Right(None)
      case Left(_) => Left(ObligationsError)
    }

  def getPaymentObligations(vrn: String)
                           (implicit hc: HeaderCarrier,
                            ec: ExecutionContext): Future[ServiceResponse[Option[Payments]]] =

    financialDataConnector.getOpenPayments(vrn).map {
      case Right(payments) =>
        val outstandingPayments = payments.financialTransactions.filter(_.outstandingAmount > 0)
        if(outstandingPayments.nonEmpty) {
          Right(Some(Payments(outstandingPayments)))
        } else {
          Right(None)
        }
      case Left(_) => Left(NextPaymentError)
    }
}
