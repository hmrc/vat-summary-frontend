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

import play.api.libs.functional.syntax._
import play.api.libs.json._

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
                               correspondenceEmailAddress: Option[String],
                               isPartialMigration: Boolean
                              ) {
  def entityName: Option[String] = {
    (firstName, lastName, tradingName, organisationName) match {
      case (Some(first), Some(last), None, None) => Some(s"$first $last")
      case (None, None, None, orgName) => orgName
      case _ => tradingName
    }
  }
}

object CustomerInformation {
  implicit val customerInformationWrites: Writes[CustomerInformation] = Json.writes[CustomerInformation]

  private def createCustomerWithNameOnly(organisationName: Option[String],
                                         firstName: Option[String],
                                         lastName: Option[String],
                                         tradingName: Option[String],
                                         isPartialMigration: Option[Boolean]): CustomerInformation = {

    val dummyAddress = Address("", "", None, None, None)
    CustomerInformation(
      organisationName = organisationName,
      firstName = firstName,
      lastName = lastName,
      tradingName = tradingName,
      businessAddress = dummyAddress,
      businessPrimaryPhoneNumber = None,
      businessMobileNumber = None,
      businessEmailAddress = None,
      correspondenceAddress = dummyAddress,
      correspondencePrimaryPhoneNumber = None,
      correspondenceMobileNumber = None,
      correspondenceEmailAddress = None,
      isPartialMigration = isPartialMigration.contains(true)
    )
  }

  implicit val customerInformationReads: Reads[CustomerInformation] = (
    (JsPath \ "organisationName").readNullable[String] and
      (JsPath \ "firstName").readNullable[String] and
      (JsPath \ "lastName").readNullable[String] and
      (JsPath \ "tradingName").readNullable[String] and
      (JsPath \ "isPartialMigration").readNullable[Boolean]
    ) (createCustomerWithNameOnly _)
}
