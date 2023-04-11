/*
 * Copyright 2023 HM Revenue & Customs
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

package audit.models

import models.{ServiceResponse, User}
import models.obligations.VatReturnObligations

case class ViewNextOpenVatObligationAuditModel(user: User,
                                               obligations: ServiceResponse[Option[VatReturnObligations]]) extends AuditModel {

  private val openObligation: Map[String, String] = obligations match {
    case Right(Some(obs)) if obs.obligations.size > 1 => Map(
      "numberOfObligations" -> obs.obligations.size.toString
    )
    case Right(Some(obs)) => Map(
      "obligationOpen" -> "yes",
      "obligationDueBy" -> obs.obligations.head.due.toString,
      "obligationPeriodFrom" -> obs.obligations.head.periodFrom.toString,
      "obligationPeriodTo" -> obs.obligations.head.periodTo.toString
    )
    case Right(None) => Map("obligationOpen" -> "no")
    case Left(_) => Map("obligationsTileError" -> "true")
  }

  override val auditType: String = "OverviewPageView"

  override val transactionName: String = "view-next-open-vat-obligation"

  override val detail: Map[String, String] = Map("vrn" -> user.vrn) ++ openObligation
}
