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

package models.viewModels

import java.time.LocalDate

import common.FinancialTransactionsConstants
import models.payments._
import play.api.libs.json._

import scala.collection.immutable
import scala.util.Try

case class PaymentsHistoryModel(chargeType: ChargeType,
                                taxPeriodFrom: Option[LocalDate],
                                taxPeriodTo: Option[LocalDate],
                                amount: BigDecimal,
                                clearedDate: Option[LocalDate] = None,
                                clearingReason: Option[String] = None)

object PaymentsHistoryModel {

  implicit val writes: Writes[PaymentsHistoryModel] = Json.writes[PaymentsHistoryModel]

  implicit val reads: Reads[Seq[PaymentsHistoryModel]] = new Reads[Seq[PaymentsHistoryModel]] {

    override def reads(json: JsValue): JsResult[Seq[PaymentsHistoryModel]] = {

      val transactions: Seq[JsValue] = (json \ FinancialTransactionsConstants.financialTransactions).as[Seq[JsValue]]
      val filteredTransactions: Seq[JsValue] = transactions.filter { transaction =>
        ChargeType.isValidChargeType((transaction \ FinancialTransactionsConstants.chargeType).as[String])
      }

      def getSubItemsForTransaction(transaction: JsValue): Seq[TransactionSubItem] = {
        val subItems = (transaction \ FinancialTransactionsConstants.items).as[Seq[TransactionSubItem]]
        if(subItems.isEmpty) throw new IllegalStateException("No sub items found for transaction") else subItems
      }

      val paymentHistoryCharges: Seq[PaymentsHistoryModel] = filteredTransactions flatMap { transaction =>

        val chargeType: String = (transaction \ FinancialTransactionsConstants.chargeType).as[String]

        getSubItemsForTransaction(transaction) map { subItem =>
          PaymentsHistoryModel(
            chargeType = ChargeType.apply(chargeType),
            taxPeriodFrom = (transaction \ FinancialTransactionsConstants.taxPeriodFrom).asOpt[LocalDate],
            taxPeriodTo = (transaction \ FinancialTransactionsConstants.taxPeriodTo).asOpt[LocalDate],
            amount = subItem.amount,
            clearedDate = subItem.clearingDate,
            clearingReason = subItem.clearingReason
          )
        }
      }

      JsSuccess(paymentHistoryCharges.filter(_.clearedDate.isDefined))
    }
  }
}
