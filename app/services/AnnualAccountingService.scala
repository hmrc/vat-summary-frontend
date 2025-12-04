/*
 * Copyright 2025 HM Revenue & Customs
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

import connectors.StandingRequestsConnector
import models.StandingRequest
import uk.gov.hmrc.http.HeaderCarrier
import utils.LoggerUtil
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

class AnnualAccountingService @Inject()(standingRequestsConnector: StandingRequestsConnector)
  extends LoggerUtil {

  def getStandingRequests(vrn: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[StandingRequest]] = {
    standingRequestsConnector.getStandingRequests(vrn).map {
      case Right(standingRequest) =>
        logger.info(s"Successfully retrieved standing requests on Annual Accounting for $vrn")
        println(Some(standingRequest))
        Some(standingRequest)
      case Left(error) =>
        logger.warn(s"Error retrieving standing requests on Annual Accounting: ${error.message}")
        None
    }
  }
}
