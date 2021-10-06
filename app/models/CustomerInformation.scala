/*
 * Copyright 2021 HM Revenue & Customs
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

case class CustomerInformation(details: CustomerDetails,
                               businessAddress: Address,
                               emailAddress: Option[Email],
                               isHybridUser: Boolean,
                               customerMigratedToETMPDate: Option[String],
                               hybridToFullMigrationDate: Option[String],
                               partyType: Option[String],
                               sicCode: Option[String],
                               returnPeriod: Option[String],
                               nonStdTaxPeriods: Option[Seq[TaxPeriod]],
                               firstNonNSTPPeriod: Option[TaxPeriod],
                               pendingMandationStatus: Option[String],
                               deregistration: Option[Deregistration],
                               changeIndicators: Option[ChangeIndicators],
                               isMissingTrader: Boolean,
                               hasPendingPpobChanges: Boolean,
                               mandationStatus: String) {

  def extractDate: Option[String] = hybridToFullMigrationDate match {
    case Some(_) => hybridToFullMigrationDate
    case _ => customerMigratedToETMPDate
  }

  def extractHybridToFullDate: Option[String] = hybridToFullMigrationDate match {
    case Some(_) => hybridToFullMigrationDate
    case _ => None
  }

  val partyTypeMessageKey: String = partyType.fold("common.notProvided")(x => s"partyType.$x")
  val returnPeriodMessageKey: String = returnPeriod.fold("common.notProvided"){
    case x @ ("MM" | "MA" | "MB" | "MC") => s"returnPeriod.$x"
    case _ => "returnPeriod.nonStandard"
  }
}

object CustomerInformation {
  implicit val customerInformationReads: Reads[CustomerInformation] = (
    (__ \ "customerDetails").read[CustomerDetails] and
    (__ \ "ppob").read[Address] and
    (__ \ "ppob" \ "contactDetails").readNullable[Email].orElse(Reads.pure(None)) and
    (__ \\ "isPartialMigration").readNullable[Boolean].map(_.contains(true)) and
    (__ \\ "customerMigratedToETMPDate").readNullable[String].orElse(Reads.pure(None)) and
    (__ \\ "hybridToFullMigrationDate").readNullable[String].orElse(Reads.pure(None)) and
    (__ \ "partyType").readNullable[String].orElse(Reads.pure(None)) and
    (__ \ "primaryMainCode").readNullable[String] and
    (__ \\ "stdReturnPeriod").readNullable[String].orElse(Reads.pure(None)) and
    (__ \\ "nonStdTaxPeriods").readNullable[Seq[TaxPeriod]].orElse(Reads.pure(None)) and
    (__ \\ "firstNonNSTPPeriod").readNullable[TaxPeriod].orElse(Reads.pure(None)) and
    (__ \ "pendingChanges" \ "mandationStatus").readNullable[String].orElse(Reads.pure(None)) and
    (__ \ "deregistration").readNullable[Deregistration].orElse(Reads.pure(None)) and
    (__ \ "changeIndicators").readNullable[ChangeIndicators].orElse(Reads.pure(None)) and
    (__ \ "missingTrader").read[Boolean] and
    (__ \ "changeIndicators" \ "PPOBDetails").readNullable[Boolean].orElse(Reads.pure(None)).map(_.contains(true)) and
    (__ \ "mandationStatus").read[String]
  )(CustomerInformation.apply _)
}
