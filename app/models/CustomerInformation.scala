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
                               businessAddress: Address,
                               businessPrimaryPhoneNumber: Option[String],
                               businessMobileNumber: Option[String],
                               businessEmailAddress: Option[String],
                               correspondenceAddress: Address,
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

    (JsPath \ "approvedInformation" \ "PPOB").read[Address] and
    (JsPath \ "approvedInformation" \ "PPOB" \\ "primaryPhoneNumber").readNullable[String] and
    (JsPath \ "approvedInformation" \ "PPOB" \\ "mobileNumber").readNullable[String] and
    (JsPath \ "approvedInformation" \ "PPOB" \\ "emailAddress").readNullable[String] and

    (JsPath \ "approvedInformation" \ "correspondenceContactDetails").read[Address] and
    (JsPath \ "approvedInformation" \ "correspondenceContactDetails" \\ "primaryPhoneNumber").readNullable[String] and
    (JsPath \ "approvedInformation" \ "correspondenceContactDetails" \\ "mobileNumber").readNullable[String] and
    (JsPath \ "approvedInformation" \ "correspondenceContactDetails" \\ "emailAddress").readNullable[String]

    ) (CustomerInformation.apply _)
}
