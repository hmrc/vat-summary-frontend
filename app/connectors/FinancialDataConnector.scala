/*
 * Copyright 2023 HM Revenue & Customs
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
import connectors.httpParsers.ResponseHttpParsers.HttpResult
import models.DirectDebitStatus
import javax.inject.{Inject, Singleton}
import models.payments.Payments
import models.viewModels.PaymentsHistoryModel
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialDataConnector @Inject()(http: HttpClient,
                                       appConfig: AppConfig) extends LoggerUtil{

  private[connectors] def paymentsUrl(vrn: String): String = s"${appConfig.financialDataBaseUrl}/financial-transactions/vat/$vrn"

  private[connectors] def directDebitUrl(vrn: String): String = s"${appConfig.financialDataBaseUrl}/financial-transactions" +
    s"/has-direct-debit/$vrn"

  def getOpenPayments(vrn: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResult[Payments]] = {

    import connectors.httpParsers.PaymentsHttpParser.PaymentsReads

    http.GET(paymentsUrl(vrn), Seq("onlyOpenItems" -> "true"))
      .map {
        case payments@Right(_) =>
          logger.debug(s"[FinancialDataConnector][getOpenPayments] - Payments:\n\n$payments")
          payments
        case httpError@Left(error) =>
          logger.warn("FinancialDataConnector received error: " + error.message)
          httpError
      }
  }

  def getVatLiabilities(vrn: String, from: LocalDate, to: LocalDate)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResult[Seq[PaymentsHistoryModel]]] = {

    import connectors.httpParsers.PaymentsHistoryHttpParser.PaymentsHistoryReads

    logger.debug(s"[FinancialDataConnector][getVatLiabilities] - Calling financial API from $from to $to.")

    http.GET(paymentsUrl(vrn), Seq(
      "dateFrom" -> from.toString,
      "dateTo" -> to.toString
    ))
      .map {
        case payments@Right(_) =>
          logger.debug(s"[FinancialDataConnector][getVatLiabilities] Payments: \n\n $payments")
          payments
        case httpError@Left(error) =>
          logger.warn("[FinancialDataConnector][getVatLiabilities] received error: " + error.message)
          httpError
      }
  }

  def getDirectDebitStatus(vrn: String)
                          (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResult[DirectDebitStatus]] = {

    import connectors.httpParsers.DirectDebitStatusHttpParser.DirectDebitStatusReads

    http.GET(directDebitUrl(vrn)).map {
      case directDebitStatus@Right(_) =>
        directDebitStatus
      case httpError@Left(error) =>
        logger.warn("FinancialDataConnector received error: " + error.message)
        httpError
    }
  }
}
