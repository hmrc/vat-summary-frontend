/*
 * Copyright 2019 HM Revenue & Customs
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

  private[connectors] def paymentsUrl(vrn: String): String = s"${appConfig.financialDataBaseUrl}/financial-transactions/vat/$vrn"

  private[connectors] def directDebitUrl(vrn: String): String = s"${appConfig.financialDataBaseUrl}/financial-transactions" +
    s"/has-direct-debit/$vrn"

  def getOpenPayments(vrn: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[Payments]] = {

    import connectors.httpParsers.PaymentsHttpParser.PaymentsReads

    val timer = metrics.getOpenPaymentsTimer.time()

    http.GET(paymentsUrl(vrn), Seq("onlyOpenItems" -> "true"))
      .map {
        case payments@Right(_) =>
          timer.stop()
          payments
        case httpError@Left(error) =>
          metrics.getOpenPaymentsCallFailureCounter.inc()
          Logger.warn("FinancialDataConnector received error: " + error.message)
          httpError
      }
  }

  def getVatLiabilities(vrn: String, from: LocalDate, to: LocalDate)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[Seq[PaymentsHistoryModel]]] = {

    import connectors.httpParsers.PaymentsHistoryHttpParser.PaymentsHistoryReads

    val timer = metrics.getPaymentHistoryTimer.time()

    http.GET(paymentsUrl(vrn), Seq(
      "dateFrom" -> s"${from.getYear}-01-01",
      "dateTo" -> s"${to.getYear}-12-31"
    ))
      .map {
        case payments@Right(_) =>
          timer.stop()
          payments
        case httpError@Left(error) =>
          metrics.getPaymentHistoryFailureCounter.inc()
          Logger.warn("[FinancialDataConnector][getVatLiabilities] received error: " + error.message)
          httpError
      }
  }

  def getDirectDebitStatus(vrn: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[Boolean]] = {

    import connectors.httpParsers.DirectDebitStatusHttpParser.DirectDebitStatusReads

    val timer = metrics.getDirectDebitStatusTimer.time()

    http.GET(directDebitUrl(vrn)).map {
      case directDebitStatus@Right(_) =>
        timer.stop()
        directDebitStatus
      case httpError@Left(error) =>
        metrics.getDirectDebitStatusFailureCounter.inc()
        Logger.warn("FinancialDataConnector received error: " + error.message)
        httpError
    }
  }
}
