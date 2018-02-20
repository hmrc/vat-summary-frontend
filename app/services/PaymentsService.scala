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


import javax.inject.{Inject, Singleton}

import connectors.FinancialDataConnector
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.payments.Payments
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentsService @Inject()(connector: FinancialDataConnector){
  def getOpenPayments(vrn: String) (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[Payments]] =
    connector.getOpenPayments(vrn).map {
      case Right(Payments(payments)) if payments.nonEmpty =>
        Some(Payments(payments.filter(payment => payment.outstandingAmount > 0)))
      case Right(emptyPayments) => Some(emptyPayments)
      case Left(_) => None
    }
}
