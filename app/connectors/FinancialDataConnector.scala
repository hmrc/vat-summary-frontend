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

import java.time.LocalDate

import config.AppConfig
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import javax.inject.{Inject, Singleton}
import models.payments.Payments
import models.viewModels.PaymentsHistoryModel
import play.api.Logger
import services.MetricsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialDataConnector @Inject()(http: HttpClient,
                                       appConfig: AppConfig,
                                       metrics: MetricsService) {

  import connectors.httpParsers.PaymentsHttpParser.PaymentsReads

  private[connectors] def paymentsUrl(vrn: String): String = s"${appConfig.financialDataBaseUrl}/financial-transactions/vat/$vrn"

  def getOpenPayments(vrn: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[Payments]] = {

    val timer = metrics.getOpenPaymentsTimer.time()

    http.GET(paymentsUrl(vrn), Seq("onlyOpenItems" -> "true"))
      .map {
        case payments@Right(_) =>
          timer.stop()
          payments
        case httpError@Left(error) =>
          metrics.getOpenPaymentsCallFailureCounter.inc()
          Logger.warn("FinancialDataConnector received error: " + error.message )
          httpError
      }
  }

  def getVatLiabilities(vrn: String, from: LocalDate, to: LocalDate)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[Seq[PaymentsHistoryModel]]] = {
    Future.successful(Right(Seq(
      PaymentsHistoryModel(
        taxPeriodFrom = LocalDate.parse(s"${from.getYear}-01-01"),
        taxPeriodTo = LocalDate.parse(s"${from.getYear}-02-01"),
        amount = 123456789,
        clearedDate = LocalDate.parse(s"${from.getYear}-03-01")
      ),
      PaymentsHistoryModel(
        taxPeriodFrom = LocalDate.parse(s"${from.getYear}-03-01"),
        taxPeriodTo = LocalDate.parse(s"${from.getYear}-04-01"),
        amount = 987654321,
        clearedDate = LocalDate.parse(s"${from.getYear}-03-01")
      )
    )))
  }
}
