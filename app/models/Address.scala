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

package models

import play.api.Logging
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Address(line1: String,
                   line2: Option[String],
                   line3: Option[String],
                   line4: Option[String],
                   postCode: Option[String])

object Address extends Logging {
  implicit val addressWrites: Writes[Address] = Json.writes

  implicit val addressReads: Reads[Address] = (
    (__ \ "address" \ "line1").readWithDefault {
        logger.info("[Address] Address line 1 empty")
        ""
    } ~
      (__ \ "address" \ "line2").readNullable[String] ~
      (__ \ "address" \ "line3").readNullable[String] ~
      (__ \ "address" \ "line4").readNullable[String] ~
      (__ \ "address" \ "postCode").readNullable[String]
    )(Address.apply _)
}
