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

import java.time.LocalDate

import models.viewModels.PaymentsHistoryModel
import play.api.libs.json.{JsValue, Json, Writes}

case class ViewVatPaymentHistoryAuditModel(vrn: String,
                                           payments: Seq[PaymentsHistoryModel]) extends ExtendedAuditModel {

  override val auditType: String = "PageHistoryPageView"

  override val transactionName: String = "view-payment-history"

  case class PaymentHistoryDetails(vrn: String,
                                   clearingSAPDocument: Option[String],
                                   chargeType: String,
                                   periodFrom: Option[LocalDate],
                                   periodTo: Option[LocalDate],
                                   clearingDate: Option[LocalDate],
                                   amount: BigDecimal)

  implicit val auditPaymentsHistoryWrites: Writes[PaymentHistoryDetails] = Json.writes[PaymentHistoryDetails]

  private case class AuditDetail(vrn: String, payments: Seq[PaymentHistoryDetails])
  private implicit val auditDetailWrites: Writes[AuditDetail] = Json.writes[AuditDetail]

  private def paymentsHistory = payments.map{ payment =>

    PaymentHistoryDetails(
      vrn,
      payment.clearingSAPDocument,
      payment.chargeType.value,
      payment.taxPeriodFrom,
      payment.taxPeriodTo,
      payment.clearedDate,
      payment.amount
    )
  }

  // only audit items with clearing date
  private val eventData = AuditDetail(vrn, paymentsHistory.filter(_.clearingDate.isDefined))
  override val detail: JsValue = Json.toJson(eventData)

}
