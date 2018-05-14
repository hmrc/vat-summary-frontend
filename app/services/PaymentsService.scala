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

import connectors.{FinancialDataConnector, PaymentsConnector}
import javax.inject.{Inject, Singleton}
import models.ServiceResponse
import models.errors.PaymentSetupError
import models.payments.{PaymentDetailsModel, Payments}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentsService @Inject()(financialDataConnector: FinancialDataConnector, paymentsConnector: PaymentsConnector) {
  def getOpenPayments(vrn: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[Payments]] =
    financialDataConnector.getOpenPayments(vrn).map {
      case Right(Payments(payments)) if payments.nonEmpty =>
        Some(Payments(payments.filter(payment => payment.outstandingAmount > 0)))
      case Right(emptyPayments) => Some(emptyPayments)
      case Left(_) => None
    }

  def setupPaymentsJourney(journeyDetails: PaymentDetailsModel)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ServiceResponse[String]] =
    paymentsConnector.setupJourney(journeyDetails).map {
      case Right(url) => Right(url)
      case Left(_) => Left(PaymentSetupError)
    }

}
