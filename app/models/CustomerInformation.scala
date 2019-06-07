/*
 * Copyright 2019 HM Revenue & Customs
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

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class CustomerInformation(organisationName: Option[String],
                               firstName: Option[String],
                               lastName: Option[String],
                               tradingName: Option[String],
                               businessAddress: Address,
                               businessEmailAddress: Option[String],
                               isHybridUser: Boolean,
                               customerMigratedToETMPDate: Option[String],
                               registrationDate: Option[String],
                               partyType: Option[String],
                               sicCode: String,
                               bankAccountNumber: Option[String],
                               bankAccountSortCode: Option[String],
                               returnPeriod: Option[String]) {

  def entityName: Option[String] =
    (firstName, lastName, tradingName, organisationName) match {
      case (Some(first), Some(last), None, None) => Some(s"$first $last")
      case (None, None, None, orgName) => orgName
      case _ => tradingName
    }
}

object CustomerInformation {
  implicit val customerInformationWrites: Writes[CustomerInformation] = Json.writes[CustomerInformation]

  implicit val customerInformationReads: Reads[CustomerInformation] = (
    (JsPath \ "customerDetails" \ "organisationName").readNullable[String].orElse(Reads.pure(None)) and
    (JsPath \ "customerDetails" \ "firstName").readNullable[String].orElse(Reads.pure(None)) and
    (JsPath \ "customerDetails" \ "lastName").readNullable[String].orElse(Reads.pure(None)) and
    (JsPath \ "customerDetails" \ "tradingName").readNullable[String].orElse(Reads.pure(None)) and
    (JsPath \ "ppob").read[Address] and
    (JsPath \ "ppob" \ "contactDetails" \ "emailAddress").readNullable[String].orElse(Reads.pure(None)) and
    (JsPath \\ "isPartialMigration").readNullable[Boolean].map(_.contains(true)) and
    (JsPath \\ "customerMigratedToETMPDate").readNullable[String] and
    (JsPath \\ "vatRegistrationDate").readNullable[String] and
    (JsPath \ "partyType").readNullable[String] and
    (JsPath \ "primaryMainCode").read[String] and
    (JsPath \\ "bankAccountNumber").readNullable[String] and
    (JsPath \\ "sortCode").readNullable[String] and
    (JsPath \\ "stdReturnPeriod").readNullable[String]
  )(CustomerInformation.apply _)
}
