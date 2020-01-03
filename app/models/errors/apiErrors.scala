/*
 * Copyright 2020 HM Revenue & Customs
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

package models.errors

import play.api.libs.functional.syntax._
import play.api.libs.json._

sealed trait ApiError

case class ApiSingleError(code: String, message: String) extends ApiError

object ApiSingleError {

  implicit val apiSingleErrorWrites: Writes[ApiSingleError] = Json.writes[ApiSingleError]

  implicit val apiSingleErrorReads: Reads[ApiSingleError] = (
    (
      (JsPath \ "code").read[String] or
      (JsPath \ "status").read[String]
    ) and (
      (JsPath \ "body").read[String] or
      (JsPath \ "message").read[String] or
      (JsPath \ "reason").read[String]
    )
  )(ApiSingleError.apply _)
}

case class ApiMultiError(code: String, message: String, errors: Seq[ApiSingleError]) extends ApiError

object ApiMultiError {
  implicit val apiMultiErrorWrites: Writes[ApiMultiError] = Json.writes[ApiMultiError]

  implicit val apiMultiErrorReads: Reads[ApiMultiError] = (
    (JsPath \ "code").read[String] and
      (JsPath \ "message").read[String] and
      (JsPath \ "errors").read(Reads.seq[ApiSingleError])
    ) (ApiMultiError.apply _)
}

case class ApiMultiErrorFinancial(errors: Seq[ApiSingleError]) extends ApiError

object ApiMultiErrorFinancial {
  implicit val apiMultiErrorFinancialWrites: Writes[ApiMultiErrorFinancial] = Json.writes[ApiMultiErrorFinancial]

  implicit val apiMultiErrorFinancialReads: Reads[ApiMultiErrorFinancial] =
    (JsPath \ "failures").read(Reads.seq[ApiSingleError])
      .map(ApiMultiErrorFinancial.apply)
}
