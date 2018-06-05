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

package audit.models

import models.User
import models.obligations.VatReturnObligations

case class ViewNextOpenVatObligationAuditModel(user: User,
                                               obligations: Option[VatReturnObligations]) extends AuditModel {

  private val openObligation: Map[String, String] = obligations match {
    case Some(obs) if obs.obligations.size > 1 => Map(
      "numberOfObligations" -> obs.obligations.size.toString
    )
    case Some(obs) => Map(
      "obligationOpen" -> "yes",
      "obligationDueBy" -> obs.obligations.head.due.toString,
      "obligationPeriodFrom" -> obs.obligations.head.start.toString,
      "obligationPeriodTo" -> obs.obligations.head.end.toString
    )
    case _ => Map("obligationOpen" -> "no")
  }

  override val auditType: String = "OverviewPageView"

  override val transactionName: String = "view-next-open-vat-obligation"

  override val detail: Map[String, String] = Map("vrn" -> user.vrn) ++ openObligation
}
