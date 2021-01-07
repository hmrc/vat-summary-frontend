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

package audit.models

import models.User
import models.payments.OpenPaymentsModel
import play.api.libs.json.{JsValue, Json, Writes}

case class ViewOutstandingVatPaymentsAuditModel(user: User,
                                                payments: Seq[OpenPaymentsModel]) extends ExtendedAuditModel {

  override val auditType: String = "CheckWhatYouOwePageView"

  override val transactionName: String = "view-outstanding-vat-payments"

  private case class AuditDetail(vrn: String, outstandingPayments: Seq[OpenPaymentsModel])
  private implicit val auditDetailWrites: Writes[AuditDetail] = Json.writes[AuditDetail]

  private val eventData = AuditDetail(user.vrn, payments)
  override val detail: JsValue = Json.toJson(eventData)

}
