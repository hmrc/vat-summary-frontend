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

package services

import com.google.inject.Inject
import connectors.PenaltyDetailsConnector
import connectors.httpParsers.ResponseHttpParsers.HttpResult
import models.penalties.PenaltyDetails
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class PenaltyDetailsService @Inject()(connector: PenaltyDetailsConnector) {

  def getPenaltyDetails(idValue: String)(implicit hc: HeaderCarrier, ec: ExecutionContext):
    Future[HttpResult[PenaltyDetails]] = connector.getPenaltyDetails(idValue)
}
