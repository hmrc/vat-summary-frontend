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

package models.viewModels

import common.FinancialTransactionsConstants
import models.payments._
import play.api.libs.json._

import java.time.LocalDate

case class PaymentsHistoryModel(clearingSAPDocument: Option[String],
                                chargeType: ChargeType,
                                taxPeriodFrom: Option[LocalDate],
                                taxPeriodTo: Option[LocalDate],
                                amount: BigDecimal,
                                clearedDate: Option[LocalDate] = None)

object PaymentsHistoryModel {

  val allocatedCharge : String = "allocated to charge"

  implicit val reads: Reads[Seq[PaymentsHistoryModel]] = json => {

    val transactions: Seq[JsValue] = (json \ FinancialTransactionsConstants.financialTransactions).as[Seq[JsValue]]

    JsSuccess(
      transactions flatMap { transaction =>

        val chargeType: ChargeType = ChargeType.apply((transaction \ FinancialTransactionsConstants.chargeType).as[String])

        val transactions: Seq[Option[PaymentsHistoryModel]] = getSubItemsForTransaction(transaction) map {
          subItem => generatePaymentModel(chargeType, subItem, transaction)
        }

        transactions.flatten.filter(_.clearedDate.isDefined)
      }
    )
  }

  private[models] def generatePaymentModel(chargeType: ChargeType,
                                           subItem: TransactionSubItem,
                                           transaction: JsValue): Option[PaymentsHistoryModel] =
    (chargeType.value, subItem.amount, subItem.clearingReason) match {
      case (PaymentOnAccount.value, Some(subItemAmount), None) =>
        Some(PaymentsHistoryModel(
          clearingSAPDocument = subItem.clearingSAPDocument,
          chargeType = UnallocatedPayment,
          taxPeriodFrom = None,
          taxPeriodTo = None,
          amount = subItemAmount.unary_-,
          clearedDate = subItem.dueDate
        ))
      case (PaymentOnAccount.value, Some(subItemAmount), Some("Outgoing Payment")) =>
        Some(PaymentsHistoryModel(
          clearingSAPDocument = subItem.clearingSAPDocument,
          chargeType = Refund,
          taxPeriodFrom = None,
          taxPeriodTo = None,
          amount = subItemAmount,
          clearedDate = subItem.clearingDate
        ))
      case (PaymentOnAccount.value, _, _) =>
        None
      case (_, Some(subItemAmount), _) =>
        Some(PaymentsHistoryModel(
          clearingSAPDocument = subItem.clearingSAPDocument,
          chargeType = chargeType,
          taxPeriodFrom = (transaction \ FinancialTransactionsConstants.taxPeriodFrom).asOpt[LocalDate],
          taxPeriodTo = (transaction \ FinancialTransactionsConstants.taxPeriodTo).asOpt[LocalDate],
          amount = subItemAmount,
          clearedDate = subItem.clearingDate
        ))
      case (_, None, _) =>
        None
    }

  private[models] def getSubItemsForTransaction(transaction: JsValue): Seq[TransactionSubItem] =
    (transaction \ FinancialTransactionsConstants.items).asOpt[Seq[TransactionSubItem]].getOrElse(Seq())
}
