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

case class CustomerDetails(
                           firstName: Option[String],
                           lastName: Option[String],
                           tradingName: Option[String],
                           organisationName: Option[String]
                          ) {

  def entityName: Option[String] =
    (firstName, lastName, tradingName, organisationName) match {
      case (Some(first), Some(last), None, None) => Some(s"$first $last")
      case (None, None, None, orgName) => orgName
      case _ => tradingName
    }
}

object CustomerDetails {
  implicit val customerDetailsReads: Reads[CustomerDetails] = (
    (__ \ "firstName").readNullable[String].orElse(Reads.pure(None)) and
      (__ \ "lastName").readNullable[String].orElse(Reads.pure(None)) and
      (__ \ "tradingName").readNullable[String].orElse(Reads.pure(None)) and
      (__ \ "organisationName").readNullable[String].orElse(Reads.pure(None))
  )(CustomerDetails.apply _)
}
