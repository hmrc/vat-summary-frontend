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

import models.errors._
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}

trait ResponseHttpParsers {

  protected def handleBadRequest(json: JsValue): Left[HttpError, Nothing] = {
    val errorResponse: Option[ApiError] = json.asOpt[ApiMultiError]
      .orElse(json.asOpt[ApiMultiErrorFinancial])
      .orElse(json.asOpt[ApiSingleError])
    errorResponse
      .map(generateClientError)
      .getOrElse(Left(UnknownError))
  }

  private def generateClientError(error: ApiError): Left[HttpError, Nothing] = error match {
    case ApiSingleError(code, message) => Left(BadRequestError(code, message))
    case ApiMultiError(code, _, errorResponse) =>
      Left(MultipleErrors(code, Json.toJson(errorResponse).toString()))
    case ApiMultiErrorFinancial(errorResponse) =>
      Left(MultipleErrors(Status.BAD_REQUEST.toString, Json.toJson(errorResponse).toString()))
  }
}

object ResponseHttpParsers {
  type HttpResult[T] = Either[HttpError, T]
}
