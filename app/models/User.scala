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

import common.EnrolmentKeys._
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier, Enrolments, InternalError}

case class User(vrn: String, active: Boolean = true, hasNonMtdVat: Boolean = false)

object User {
  def apply(authorisedEnrolments: Enrolments): User = {

    val vatEnrolments = authorisedEnrolments.enrolments.collect {
      case mtd@Enrolment(`mtdVatEnrolmentKey`, EnrolmentIdentifier("VRN", _) :: _, _, _) => mtd
      case Enrolment(`mtdVatEnrolmentKey`, EnrolmentIdentifier(_, _) :: _, _, _) => throw InternalError("VAT identifier invalid")
      case nonMtd@Enrolment(`vatDecEnrolmentKey` | `vatVarEnrolmentKey`, EnrolmentIdentifier(_, _) :: _, _, _) => nonMtd
    }

    val containsNonMtdVat = vatEnrolments.exists(_.key == vatDecEnrolmentKey) || vatEnrolments.exists(_.key == vatVarEnrolmentKey)


    vatEnrolments.collectFirst {
      case Enrolment(_, EnrolmentIdentifier(_, vrn) :: _, status, _) if vrn.matches("\\d{9}") =>
        User(vrn, status == "Activated", containsNonMtdVat)
    }.getOrElse(throw InternalError("VRN is invalid"))

  }
}
