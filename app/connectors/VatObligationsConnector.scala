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
import models.obligations.Obligation.Status
import models.obligations.VatReturnObligations
import play.api.Logger
import play.api.http.HeaderNames
import services.MetricsService
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatObligationsConnector @Inject()(http: HttpClient,
                                        appConfig: AppConfig,
                                        metrics: MetricsService) {

  private[connectors] def obligationsUrl(vrn: String): String = {
    val baseUrl: String = if (appConfig.features.enableVatObligationsService()) {
      appConfig.vatObligationsBaseUrl + "/vat-obligations"
    } else {
      appConfig.vatApiBaseUrl
    }
    s"$baseUrl/$vrn/obligations"
  }

  def getVatReturnObligations(vrn: String,
                              status: Status.Value,
                              from: Option[LocalDate] = None,
                              to: Option[LocalDate] = None)(implicit hc: HeaderCarrier,
                              ec: ExecutionContext): Future[HttpGetResult[VatReturnObligations]] = {

    val timer = metrics.getObligationsTimer.time()

    import connectors.httpParsers.VatReturnObligationsHttpParser.VatReturnsReads

    val headerCarrier = hc.withExtraHeaders(
      HeaderNames.ACCEPT -> "application/vnd.hmrc.1.0+json",
      HeaderNames.CONTENT_TYPE -> "application/json"
    )

    val httpRequest = http.GET(
      obligationsUrl(vrn),
      (from, to) match {
        case (Some(f), Some(t)) => Seq("from" -> f.toString, "to" -> t.toString, "status" -> status.toString)
        case _ => Seq("status" -> status.toString)
      }
    )(implicitly[HttpReads[HttpGetResult[VatReturnObligations]]],
      headerCarrier,
      implicitly[ExecutionContext]
    )


    httpRequest.map {
      case vatReturns@Right(_) =>
        timer.stop()
        vatReturns
      case httpError@Left(error) =>
        metrics.getObligationsCallFailureCounter.inc()
        Logger.warn("VatObligationsConnector received error: " + error.message)
        httpError
    }
  }
}
