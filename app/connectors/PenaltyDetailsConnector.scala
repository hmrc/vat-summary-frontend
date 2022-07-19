/*
 * Copyright 2022 HM Revenue & Customs
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

import config.AppConfig
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import javax.inject.Inject
import models.penalties.PenaltyDetails
import services.MetricsService
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

class PenaltyDetailsConnector @Inject()(http: HttpClient,
                                        appConfig: AppConfig,
                                        metrics: MetricsService) extends LoggerUtil {

  private[connectors] def penaltyDetailsUrl(idType: String, idValue: String): String = s"${appConfig.financialDataBaseUrl}/penalty/$idType/$idValue"

  def getPenaltyDetails(idType: String, idValue: String)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[PenaltyDetails]] = {

    import connectors.httpParsers.PenaltyDetailsHttpParser.PenaltyDetailsReads

    val timer = metrics.getPenaltyDataTimer.time()

    http.GET(penaltyDetailsUrl(idType,idValue)).map {
      case penaltyDetails@Right(_) =>
        timer.stop()
        penaltyDetails
      case httpError@Left(error) =>
        metrics.getPenaltyDataFailureCounter.inc()
        logger.warn("PenaltyDetailsConnector received error: " + error.message)
        httpError
    }
  }
}
