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

package connectors

import config.AppConfig
import connectors.httpParsers.ResponseHttpParsers.HttpPostResult
import javax.inject.{Inject, Singleton}
import models.DirectDebitDetailsModel
import play.api.Logger
import services.MetricsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DirectDebitConnector @Inject()(http: HttpClient,
                                     appConfig: AppConfig,
                                     metrics: MetricsService) {

  private[connectors] def setupUrl: String = {
      if (appConfig.features.useDirectDebitDummyPage()) {
        appConfig.selfLookup + testOnly.controllers.routes.DirectDebitStubController.startJourney().url
      } else {
        appConfig.directDebitServiceUrl + appConfig.setupDirectDebitsJourneyPath
      }
  }

  def setupJourney(data: DirectDebitDetailsModel)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpPostResult[String]] = {

    import connectors.httpParsers.DirectDebitRedirectUrlHttpParser.DirectDebitRedirectUrlReads

    val timer = metrics.postSetupDirectDebitJourneyTimer.time()

    http.POST(setupUrl, data)
      .map {
        case url@Right(_) =>
          timer.stop()
          url
        case httpError@Left(error) =>
          metrics.postSetupDirectDebitJourneyCounter.inc()
          Logger.warn("DirectDebitConnector received error: " + error.message)
          httpError
      }
  }

}
