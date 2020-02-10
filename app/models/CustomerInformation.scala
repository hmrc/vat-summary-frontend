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

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class CustomerInformation(organisationName: Option[String],
                               firstName: Option[String],
                               lastName: Option[String],
                               tradingName: Option[String],
                               businessAddress: Address,
                               isHybridUser: Boolean,
                               customerMigratedToETMPDate: Option[String],
                               registrationDate: Option[String],
                               partyType: Option[String],
                               sicCode: String,
                               returnPeriod: Option[String],
                               nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                               firstNonNSTPPeriod: Option[TaxPeriod],
                               pendingMandationStatus: Option[String],
                               deregistration: Option[Deregistration],
                               changeIndicators: Option[ChangeIndicators]) {

  def entityName: Option[String] =
    (firstName, lastName, tradingName, organisationName) match {
      case (Some(first), Some(last), None, None) => Some(s"$first $last")
      case (None, None, None, orgName) => orgName
      case _ => tradingName
    }

  val partyTypeMessageKey: String = partyType.fold("common.notProvided")(x => s"partyType.$x")
  val returnPeriodMessageKey: String = returnPeriod.fold("common.notProvided"){
    case x @ ("MM" | "MA" | "MB" | "MC") => s"returnPeriod.$x"
    case _ => "returnPeriod.nonStandard"
  }
}

object CustomerInformation {
  implicit val customerInformationReads: Reads[CustomerInformation] = (
    (JsPath \ "customerDetails" \ "organisationName").readNullable[String].orElse(Reads.pure(None)) and
    (JsPath \ "customerDetails" \ "firstName").readNullable[String].orElse(Reads.pure(None)) and
    (JsPath \ "customerDetails" \ "lastName").readNullable[String].orElse(Reads.pure(None)) and
    (JsPath \ "customerDetails" \ "tradingName").readNullable[String].orElse(Reads.pure(None)) and
    (JsPath \ "ppob").read[Address] and
    (JsPath \\ "isPartialMigration").readNullable[Boolean].map(_.contains(true)) and
    (JsPath \\ "customerMigratedToETMPDate").readNullable[String] and
    (JsPath \\ "vatRegistrationDate").readNullable[String] and
    (JsPath \ "partyType").readNullable[String] and
    (JsPath \ "primaryMainCode").read[String] and
    (JsPath \\ "stdReturnPeriod").readNullable[String] and
    (JsPath \\ "nonStdTaxPeriods").readNullable[Seq[TaxPeriod]] and
    (JsPath \\ "firstNonNSTPPeriod").readNullable[TaxPeriod] and
    (JsPath \ "pendingChanges" \ "mandationStatus").readNullable[String].orElse(Reads.pure(None)) and
    (JsPath \ "deregistration").readNullable[Deregistration] and
    (JsPath \ "changeIndicators").readNullable[ChangeIndicators]
  )(CustomerInformation.apply _)
}
