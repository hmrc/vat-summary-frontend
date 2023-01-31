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

package connectors.httpParsers

import play.api.http.Status._
import connectors.httpParsers.ResponseHttpParsers._
import models.errors.UnexpectedStatusError
import models.essttp.TTPResponseModel
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.LoggerUtil

object TimeToPayHttpParser extends ResponseHttpParsers with LoggerUtil {

  implicit object TimeToPayReads extends HttpReads[HttpPostResult[TTPResponseModel]] {

    override def read(method: String, url: String, response: HttpResponse): HttpPostResult[TTPResponseModel] = {

      response.status match {
        case OK => Right(response.json.as[TTPResponseModel])
        case BAD_REQUEST => handleBadRequest(response.json)
        case _ => Left(UnexpectedStatusError(response.status.toString, response.body))
      }

    }
  }
}

