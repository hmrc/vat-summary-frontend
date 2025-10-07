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

import config.AppConfig
import connectors.httpParsers.ResponseHttpParsers.HttpResult

import javax.inject.Inject
import models.penalties.PenaltyDetails
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

class PenaltyDetailsConnector @Inject()(http: HttpClient,
                                        appConfig: AppConfig) extends LoggerUtil{

  private[connectors] def penaltyDetailsUrl(idValue: String): String =
    s"${appConfig.financialDataBaseUrl}/financial-transactions/penalty/VAT/$idValue"

  def getPenaltyDetails(idValue: String)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResult[PenaltyDetails]] = {

    import connectors.httpParsers.PenaltyDetailsHttpParser.PenaltyDetailsReads

    http.GET(penaltyDetailsUrl(idValue))
      .map {
        {
          case penalties@Right(_) =>
            logger.info("[PenaltyDetailsConnector][getPenaltyDetails] Successfully retrieved LPP details")
            logger.debug(s"[PenaltyDetailsConnector][getPenaltyDetails] - Penalties:\n\n$penalties")
            penalties
          case httpError@Left(error) =>
            logger.warn("penaltyDetailsConnector received error: " + error.message)
            httpError
        }
      }

    }
}
