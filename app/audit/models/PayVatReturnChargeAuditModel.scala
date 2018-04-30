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
import models.payments.PaymentDetailsModel

case class PayVatReturnChargeAuditModel(user: User,
                                        payment: PaymentDetailsModel,
                                        returnUrl: String) extends AuditModel {

  override val auditType: String = "PaymentsHandOff"

  override val transactionName: String = "pay-vat-return-charge"

  override val detail: Map[String, String] = Map(
    "vrn" -> user.vrn,
    "taxType" -> payment.taxType,
    "taxReference" -> payment.taxReference,
    "amountInPence" -> payment.amountInPence.toString,
    "taxPeriodMonth" -> payment.taxPeriodMonth.toString,
    "taxPeriodYear" -> payment.taxPeriodYear.toString,
    "returnUrl" -> payment.returnUrl,
    "backUrl" -> payment.backUrl
  )

}
