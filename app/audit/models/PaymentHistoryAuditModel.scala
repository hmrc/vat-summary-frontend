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

case class PaymentHistoryAuditModel(user: User,
                                    paymentReceived: LocalDate,
                                    periodFrom: LocalDate,
                                    periodTo: LocalDate,
                                    paymentAmount: Long,
                                    description: String) extends AuditModel {

  override val transactionName: String = "view-payment-history"
  override val auditType: String       = "PageHistoryPageView"

  private val buildPeriodDates: String => Map[String, String] = desc =>
    if(desc.equalsIgnoreCase("Officer Assessment")) Map.empty else Map(
      "periodFrom" -> periodFrom.toString,
      "periodTo"   -> periodTo.toString
    )

  override val detail: Map[String, String] = Map(
    "vrn"             -> user.vrn,
    "paymentReceived" -> paymentReceived.toString
  ) ++ buildPeriodDates(description) ++ Map(
    "paymentAmount"   -> paymentAmount.toString,
    "description"     -> description
  )
}


