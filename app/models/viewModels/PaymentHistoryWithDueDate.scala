/*
 * Copyright 2025 HM Revenue & Customs
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

package models.viewModels

import java.time.LocalDate
import models.payments.ChargeType
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class PaymentHistoryWithDueDate(
  chargeType: ChargeType,
  dueDate: LocalDate,
  clearedDate: Option[LocalDate]
)

object PaymentHistoryWithDueDate {

  implicit val reads: Reads[Seq[PaymentHistoryWithDueDate]] = { json =>
    val txns = (json \ "financialTransactions").asOpt[Seq[JsValue]].getOrElse(Seq.empty)
    val parsed: Seq[PaymentHistoryWithDueDate] = txns.flatMap { txn =>
      val ct = (txn \ "chargeType").asOpt[ChargeType]
      val due = (txn \ "items")(0).\("dueDate").asOpt[LocalDate]
      val cleared = (txn \ "items")(0).\("clearingDate").asOpt[LocalDate]
      (ct, due) match {
        case (Some(c), Some(d)) => Some(PaymentHistoryWithDueDate(c, d, cleared))
        case _ => None
      }
    }
    JsSuccess(parsed)
  }
}


