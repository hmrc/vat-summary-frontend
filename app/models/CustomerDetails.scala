/*
 * Copyright 2022 HM Revenue & Customs
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

import java.time.LocalDate

case class CustomerDetails(firstName: Option[String],
                           lastName: Option[String],
                           tradingName: Option[String],
                           organisationName: Option[String],
                           isInsolvent: Boolean,
                           continueToTrade: Option[Boolean],
                           insolvencyType: Option[String],
                           insolvencyDate: Option[String],
                           vatRegistrationDate: Option[String]) {

  val entityName: Option[String] =
    (firstName, lastName, tradingName, organisationName) match {
      case (Some(first), Some(last), None, None) => Some(s"$first $last")
      case (None, None, None, orgName) => orgName
      case _ => tradingName
    }

  val exemptInsolvencyTypes = Seq("07", "12", "13", "14")
  val blockedInsolvencyTypes = Seq("08", "09", "10", "15")

  val isInsolventWithoutAccess: Boolean = (isInsolvent, insolvencyType) match {
    case (true, Some(inType)) if exemptInsolvencyTypes.contains(inType) => false
    case (true, Some(inType)) if blockedInsolvencyTypes.contains(inType) => true
    case (true, _) if continueToTrade.contains(false) => true
    case _ => false
  }

  def insolvencyDateFutureUserBlocked(today: LocalDate): Boolean =
    (isInsolvent, insolvencyType, insolvencyDate, continueToTrade) match {
      case (_, Some(inType), _, _) if exemptInsolvencyTypes.contains(inType) => false
      case (true, Some(_), Some(date), Some(true)) if LocalDate.parse(date).isAfter(today) => true
      case _ => false
    }
}

object CustomerDetails {
  implicit val customerDetailsReads: Reads[CustomerDetails] = (
    (__ \ "firstName").readNullable[String] and
    (__ \ "lastName").readNullable[String] and
    (__ \ "tradingName").readNullable[String] and
    (__ \ "organisationName").readNullable[String] and
    (__ \ "isInsolvent").read[Boolean] and
    (__ \ "continueToTrade").readNullable[Boolean] and
    (__ \ "insolvencyType").readNullable[String] and
    (__ \ "insolvencyDate").readNullable[String] and
    (__ \ "effectiveRegistrationDate").readNullable[String]
  )(CustomerDetails.apply _)
}
