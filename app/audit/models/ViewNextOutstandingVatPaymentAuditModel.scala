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

package audit.models

import models.User
import models.payments.Payments

case class ViewNextOutstandingVatPaymentAuditModel(user: User,
                                                   payments: Option[Payments]) extends AuditModel {

  private val paymentDetails: Map[String, String] = payments match {

    case Some(data) if data.financialTransactions.size > 1 => Map(
      "numberOfPayments" -> data.financialTransactions.size.toString
    )
    case Some(data) => data.financialTransactions.head.auditDetails
    case _ => Map("paymentOutstanding" -> "no")
  }

  override val auditType: String = "OverviewPageView"

  override val transactionName: String = "view-next-outstanding-vat-payment"

  override val detail: Map[String, String] = Map("vrn" -> user.vrn) ++ paymentDetails

}
