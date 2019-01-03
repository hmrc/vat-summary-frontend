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
import play.api.libs.json._

case class PaymentsHistoryModel(chargeType: String,
                                 taxPeriodFrom: Option[LocalDate],
                                 taxPeriodTo: Option[LocalDate],
                                 amount: BigDecimal,
                                 clearedDate: Option[LocalDate] = None)

object PaymentsHistoryModel {

  implicit class JsonOps(json: JsValue) {
    def get[T](key: String)(implicit reads: Reads[T]): T = json.\(key).validate[T].fold(
      _ => throw new IllegalStateException(s"The data for key $key could not be found in the Json: ${json.toString}"),
      identity
    )
  }

  implicit val writes: Writes[PaymentsHistoryModel] = Json.writes[PaymentsHistoryModel]

  implicit val reads: Reads[Seq[PaymentsHistoryModel]] = new Reads[Seq[PaymentsHistoryModel]] {
    override def reads(json: JsValue): JsResult[Seq[PaymentsHistoryModel]] = {

      val validTypes: Set[String] = Set(
        FinancialTransactionsConstants.vatReturnCharge,
        FinancialTransactionsConstants.officerAssessmentCharge,
        FinancialTransactionsConstants.vatCentralAssessment,
        FinancialTransactionsConstants.vatDefaultSurcharge,
        FinancialTransactionsConstants.errorCorrectionChargeType,
        FinancialTransactionsConstants.vatRepaySupplement
      )

      val transactionsList: List[JsValue] = json.get[List[JsValue]](FinancialTransactionsConstants.financialTransactions).filter { transaction =>
        val transactions = transaction.get[String]("mainType")

        validTypes.contains(transactions)

      }

      def getItemsForPeriod(transaction: JsValue): List[(BigDecimal, Option[LocalDate])] = {
        transaction.\("items").validate[List[JsValue]].fold(
          _ => throw new IllegalStateException(s"The data for key items could not be found in the Json"),
          list => if (list.nonEmpty) {
            list.map(item => item.get[BigDecimal]("amount") ->
              getOptionDate(item, FinancialTransactionsConstants.clearingDate))
          } else {
            throw new IllegalStateException("The items list was found but the list was empty")
          }
        )
      }

      def getOptionDate(js: JsValue, key: String) = (js \ s"$key").asOpt[LocalDate]

      val extractedItems: Seq[List[PaymentsHistoryModel]] = transactionsList.map { transaction =>
        getItemsForPeriod(transaction) map { case (amount, clearedDate) =>
          PaymentsHistoryModel(
            chargeType = transaction.get[String](FinancialTransactionsConstants.chargeType),
            taxPeriodFrom = getOptionDate(transaction, FinancialTransactionsConstants.taxPeriodFrom),
            taxPeriodTo = getOptionDate(transaction, FinancialTransactionsConstants.taxPeriodTo),
            amount = amount,
            clearedDate = clearedDate
          )
        }
      }

      JsSuccess(extractedItems.flatten.filter(_.clearedDate.isDefined))
    }
  }
}
