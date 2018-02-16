/*
 * Copyright 2018 HM Revenue & Customs
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

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class CustomerInformation(organisationName: Option[String],
                               firstName: Option[String],
                               lastName: Option[String],
                               tradingName: Option[String],
                               businessLine1: String,
                               businessLine2: String,
                               businessLine3: Option[String],
                               businessLine4: Option[String],
                               businessPostCode: Option[String],
                               businessPrimaryPhoneNumber: Option[String],
                               businessMobileNumber: Option[String],
                               businessEmailAddress: Option[String],
                               correspondenceLine1: String,
                               correspondenceLine2: String,
                               correspondenceLine3: Option[String],
                               correspondenceLine4: Option[String],
                               correspondencePostCode: Option[String],
                               correspondencePrimaryPhoneNumber: Option[String],
                               correspondenceMobileNumber: Option[String],
                               correspondenceEmailAddress: Option[String]
                              )

object CustomerInformation {
  implicit val customerInformationWrites: Writes[CustomerInformation] = Json.writes[CustomerInformation]

  implicit val customerInformationReads: Reads[CustomerInformation] = (
    (JsPath \ "approvedInformation" \ "customerDetails" \ "organisationName").readNullable[String] and
    (JsPath \ "approvedInformation" \ "customerDetails" \\ "firstName").readNullable[String] and
    (JsPath \ "approvedInformation" \ "customerDetails" \\ "lastName").readNullable[String] and
    (JsPath \ "approvedInformation" \ "customerDetails" \ "tradingName").readNullable[String] and

    (JsPath \ "approvedInformation" \ "PPOB" \\ "line1").read[String] and
    (JsPath \ "approvedInformation" \ "PPOB" \\ "line2").read[String] and
    (JsPath \ "approvedInformation" \ "PPOB" \\ "line3").readNullable[String] and
    (JsPath \ "approvedInformation" \ "PPOB" \\ "line4").readNullable[String] and
    (JsPath \ "approvedInformation" \ "PPOB" \\ "postCode").readNullable[String] and
    (JsPath \ "approvedInformation" \ "PPOB" \\ "primaryPhoneNumber").readNullable[String] and
    (JsPath \ "approvedInformation" \ "PPOB" \\ "mobileNumber").readNullable[String] and
    (JsPath \ "approvedInformation" \ "PPOB" \\ "emailAddress").readNullable[String] and

    (JsPath \ "approvedInformation" \ "correspondenceContactDetails" \\ "line1").read[String] and
    (JsPath \ "approvedInformation" \ "correspondenceContactDetails" \\ "line2").read[String] and
    (JsPath \ "approvedInformation" \ "correspondenceContactDetails" \\ "line3").readNullable[String] and
    (JsPath \ "approvedInformation" \ "correspondenceContactDetails" \\ "line4").readNullable[String] and
    (JsPath \ "approvedInformation" \ "correspondenceContactDetails" \\ "postCode").readNullable[String] and
    (JsPath \ "approvedInformation" \ "correspondenceContactDetails" \\ "primaryPhoneNumber").readNullable[String] and
    (JsPath \ "approvedInformation" \ "correspondenceContactDetails" \\ "mobileNumber").readNullable[String] and
    (JsPath \ "approvedInformation" \ "correspondenceContactDetails" \\ "emailAddress").readNullable[String]

    ) (CustomerInformation.apply _)
}
