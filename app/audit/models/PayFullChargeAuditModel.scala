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

import config.AppConfig
import models.User

case class PayFullChargeAuditModel(user: User)(implicit appConfig: AppConfig) extends AuditModel {

  override val auditType: String = "PaymentsHandOffFullPayment"
  override val transactionName: String = "pay-full-vat-return-charge"

  override val detail: Map[String, String] = Map(
    "vrn" -> user.vrn,
    "taxType" -> "vat",
    "taxReference" -> user.vrn,
    "backUrl" -> appConfig.paymentsBackUrl,
    "returnUrl" -> appConfig.paymentsReturnUrl
  )
}