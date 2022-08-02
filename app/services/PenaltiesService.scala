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

package services

import config.AppConfig
import connectors.PenaltiesConnector
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.penalties.PenaltiesSummary
import uk.gov.hmrc.http.HeaderCarrier
import javax.inject.Inject
import models.errors.PenaltiesFeatureSwitchError

import scala.concurrent.{ExecutionContext, Future}

class PenaltiesService @Inject()(penaltiesConnector: PenaltiesConnector){

  def getPenaltiesInformation(vrn: String)
                             (implicit hc: HeaderCarrier, ec: ExecutionContext, appConfig: AppConfig): Future[HttpGetResult[PenaltiesSummary]] =
    if (appConfig.features.penaltiesAndInterestWYOEnabled()) {
      penaltiesConnector.getPenaltiesDataForVRN(vrn)
    } else {
      Future.successful(Left(PenaltiesFeatureSwitchError))
    }
}
