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

package models.payments

import java.time.LocalDate
import play.api.libs.functional.syntax._
import common.FinancialTransactionsConstants
import play.api.libs.json._

case class TransactionSubItem(amount: BigDecimal,
                              clearingDate: Option[LocalDate] = None,
                              clearingReason: Option[String] = None,
                              dueDate: Option[LocalDate] = None)

object TransactionSubItem {

  implicit val reads: Reads[TransactionSubItem] = (
    (JsPath \ FinancialTransactionsConstants.amount).read[BigDecimal] and
    (JsPath \ FinancialTransactionsConstants.clearingDate).readNullable[LocalDate] and
    (JsPath \ FinancialTransactionsConstants.clearingReason).readNullable[String] and
    (JsPath \ FinancialTransactionsConstants.dueDate).readNullable[LocalDate]
  )(TransactionSubItem.apply _)

}
