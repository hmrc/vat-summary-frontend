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

package connectors.httpParsers

import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.errors.{ServerSideError, UnexpectedStatusError}
import models.penalties.PenaltiesSummary
import play.api.http.Status._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.LoggerUtil

object PenaltiesHttpParser extends ResponseHttpParsers with LoggerUtil {

  implicit object PenaltiesReads extends HttpReads[HttpGetResult[PenaltiesSummary]] {
    override def read(method: String, url: String, response: HttpResponse): HttpGetResult[PenaltiesSummary] = {
      response.status match {
        case OK => Right(response.json.as[PenaltiesSummary])
        case NOT_FOUND | NO_CONTENT =>
          logger.debug(s"[PenaltiesHttpParser][read]: Received status: ${response.status}")
          Right(PenaltiesSummary.empty)
        case BAD_REQUEST => handleBadRequest(response.json)
        case status  if status >= 500 & status < 600 => Left(ServerSideError(response.status.toString, response.body))
        case _ => Left(UnexpectedStatusError(response.status.toString, response.body))
      }
    }
  }

}
