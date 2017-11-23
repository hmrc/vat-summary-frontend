/*
 * Copyright 2017 HM Revenue & Customs
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

package utils

import models.{BadRequestError, HttpError, Obligations, ServerSideError}
import play.api.http.Status._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object HttpResponseParsers {

  type HttpGetResult[T] = Either[HttpError, T]

  implicit object ObligationsReads extends HttpReads[HttpGetResult[Obligations]] {
    override def read(method: String, url: String, response: HttpResponse): HttpGetResult[Obligations] = {
      response.status match {
        case OK => Right(response.json.as[Obligations])
        case BAD_REQUEST => Left(BadRequestError) // TODO: Need to check the client errors to make more specific error tpyes
        case s if s >= 500 && s <= 599 => Left(ServerSideError)
      }
    }
  }

}
