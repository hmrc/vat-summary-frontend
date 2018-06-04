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

import config.AppConfig
import connectors.httpParsers.ResponseHttpParsers.HttpPostResult
import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import services.MetricsService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DDConnector @Inject()(http: HttpClient,
                            appConfig: AppConfig,
                            metrics: MetricsService) {

  private[connectors] lazy val setupUrl: String = s"${appConfig.directDebitServiceUrl + appConfig.setupDirectDebitJourneyPath}"

  def startJourney(vrn: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpPostResult[String]] = {

    import connectors.httpParsers.DDRedirectUrlHttpParser.DDRedirectUrlReads

    val timer = metrics.postSetupDDJourneyTimer.time()

    val backUrl: String = ""
    val returnUrl: String = ""

    val json: JsValue = Json.obj(
      "id" -> s"$vrn",
      "userIdType" -> Json.obj("VRN" -> "VRN"),
      "returnUrl" -> s"$returnUrl",
      "backUrl" -> s"$backUrl"
    )

    http.POST(setupUrl, json).map {
      case url@Right(_) =>
        timer.stop()
        url
      case httpError@Left(error) =>
        metrics.postSetupDDJourneyCounter.inc()
        Logger.warn("DDConnector received error: " + error.message)
        httpError
    }
  }

}
