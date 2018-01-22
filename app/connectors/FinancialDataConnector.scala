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

package connectors

import javax.inject.{Inject, Singleton}

import config.AppConfig
import connectors.httpParsers.PaymentsHttpParser.HttpGetResult
import models.payments.Payments
import play.api.Logger
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialDataConnector @Inject()(http: HttpClient, appConfig: AppConfig) {

  import connectors.httpParsers.PaymentsHttpParser.PaymentsReads

  private[connectors] def paymentsUrl(vrn: String): String = s"/financial-transactions/vrn/$vrn"

  def getPaymentsForVatReturns(vrn: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[Payments]] = {
    http.GET(paymentsUrl(vrn)).map {
      case payments@Right(_) => payments
      case httpError@Left(error) =>
        Logger.info("FinancialDataConnector received error: " + error.message)
        httpError
    }
  }

}
