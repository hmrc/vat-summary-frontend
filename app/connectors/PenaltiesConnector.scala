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
import models.penalties.PenaltiesSummary
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import connectors.httpParsers.PenaltiesHttpParser.PenaltiesReads
import models.errors.PenaltiesFeatureSwitchError

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PenaltiesConnector @Inject()(http: HttpClient)
                                  (implicit appConfig: AppConfig){

  def getPenaltiesDataForVRN(vrn: String)
                            (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[PenaltiesSummary]] =
    if (appConfig.features.penaltiesServiceEnabled()){
      http.GET(appConfig.penaltiesUrl(vrn))
    } else {
      Future.successful(Left(PenaltiesFeatureSwitchError))
    }
}
