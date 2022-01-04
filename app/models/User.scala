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

import common.EnrolmentKeys
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier, Enrolments}

case class User(vrn: String, active: Boolean = true, hasNonMtdVat: Boolean = false, arn: Option[String] = None) {
  def isAgent: Boolean = arn.isDefined
}

object User {

  def containsNonMtdVat(enrolments: Set[Enrolment]): Boolean = {
    enrolments.exists(_.key == EnrolmentKeys.vatDecEnrolmentKey) || enrolments.exists(_.key == EnrolmentKeys.vatVarEnrolmentKey)
  }

  def extractVatEnrolments(enrolments: Enrolments): Set[Enrolment] = {
    enrolments.enrolments.collect {
      case mtd@Enrolment(EnrolmentKeys.mtdVatEnrolmentKey, Seq(EnrolmentIdentifier(EnrolmentKeys.mtdVatIdentifierKey, _)), _, _) => mtd
      case nonMtd@Enrolment(EnrolmentKeys.vatDecEnrolmentKey | EnrolmentKeys.vatVarEnrolmentKey, Seq(EnrolmentIdentifier(_, _)), _, _) => nonMtd
    }
  }
}
