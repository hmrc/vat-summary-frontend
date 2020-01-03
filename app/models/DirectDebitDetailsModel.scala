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

package models

import play.api.libs.json.{JsObject, Json, Reads, Writes}

case class DirectDebitDetailsModel(userId: String,
                                   userIdType: String,
                                   returnUrl: String,
                                   backUrl: String)

object DirectDebitDetailsModel {
  implicit val writes: Writes[DirectDebitDetailsModel] = new Writes[DirectDebitDetailsModel] {
    def writes(directDebitDetail: DirectDebitDetailsModel): JsObject = Json.obj(
      "userId" -> directDebitDetail.userId,
      "userIdType" -> directDebitDetail.userIdType,
      "returnUrl" -> directDebitDetail.returnUrl,
      "backUrl" -> directDebitDetail.backUrl
    )
  }

  implicit val reads: Reads[DirectDebitDetailsModel] = Json.reads[DirectDebitDetailsModel]
}
