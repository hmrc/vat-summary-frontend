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

import models.DirectDebitDetailsModel

case class DirectDebitAuditModel(directDebit: DirectDebitDetailsModel,
                                 hasActiveDirectDebit: Option[Boolean],
                                 description: String) extends AuditModel {

  override val transactionName: String = "direct-debits-handOff"
  override val auditType: String = "PaymentsHandOff"

  val directDebitStatus: String = hasActiveDirectDebit match {
    case Some(status) => status.toString
    case None => "API Error"
  }

  override val detail: Map[String, String] = Map(
    "taxType" -> "vat",
    "returnUrl" -> directDebit.returnUrl,
    "backUrl" -> directDebit.backUrl,
    "vrn" -> directDebit.userId,
    "hasActiveDirectDebit" -> directDebitStatus
  )
}
