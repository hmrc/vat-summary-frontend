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
import connectors.httpParsers.TimeToPayHttpParser.TimeToPayReads
import models.ESSTTP.{TTPRequestModel, TTPResponseModel}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TimeToPayConnector @Inject()(http: HttpClient,
                                   appConfig: AppConfig) {

  private[connectors] lazy val timeToPayURI: String =
    appConfig.essttpService + "/essttp-backend/vat/vat-service/journey/start"

  def setupJourney(requestModel: TTPRequestModel)
                  (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResult[TTPResponseModel]] =
    http.POST(timeToPayURI, requestModel)
}
