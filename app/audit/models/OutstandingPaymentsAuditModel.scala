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

import java.time.LocalDate

import models.User
import models.viewModels.OpenPaymentsModel
import play.api.libs.json.{JsValue, Json, Writes}

case class OutstandingPaymentsAuditModel(user: User,
                                         payments: Seq[OpenPaymentsModel]) extends ExtendedAuditModel {

  override val auditType: String = "CheckWhatYouOwePageView"

  override val transactionName: String = "OutstandingVatPayments"

  private case class OutstandingPaymentDetails(paymentType: String,
                                               amount: BigDecimal,
                                               due: LocalDate,
                                               start: LocalDate,
                                               end: LocalDate,
                                               periodKey: String,
                                               overdue: Boolean)
  private implicit val auditPaymentsWrites: Writes[OutstandingPaymentDetails] = Json.writes[OutstandingPaymentDetails]

  private case class AuditDetail(vrn: String, outstandingPayments: Seq[OutstandingPaymentDetails])
  private implicit val auditDetailWrites: Writes[AuditDetail] = Json.writes[AuditDetail]

  private val outstandingPayments = payments.map{ payment =>
    OutstandingPaymentDetails(
      payment.paymentType,
      payment.amount,
      payment.due,
      payment.start,
      payment.end,
      payment.periodKey,
      payment.overdue)
  }

  private val eventData = AuditDetail(user.vrn, outstandingPayments)
  override val detail: JsValue = Json.toJson(eventData)

}
