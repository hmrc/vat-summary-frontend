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

package models.viewModels

import java.time.LocalDate

import play.api.libs.json._

case class PaymentsHistoryModel(taxPeriodFrom: LocalDate,
                                taxPeriodTo: LocalDate,
                                amount: Long,
                                clearedDate: LocalDate)

object PaymentsHistoryModel {
  implicit class JsonOps(json: JsValue) {
    def get[T](key: String)(implicit reads: Reads[T]): T = json.\(key).validate[T].fold(
      _ => throw new IllegalStateException(s"The data for key $key could not be found in the Json"),
      identity
    )
  }

  implicit val reads: Reads[Seq[PaymentsHistoryModel]] = new Reads[Seq[PaymentsHistoryModel]] {
    override def reads(json: JsValue): JsResult[Seq[PaymentsHistoryModel]] = {
      val transactionsList = json.get[List[JsValue]]("financialTransactions").filter {
        _.get[String]("chargeType") == "VAT Return charge"
      }

      def getItemsForPeriod(transaction: JsValue): List[(Long, LocalDate)] = {
        transaction.\("items").validate[List[JsValue]].fold(
          _ => throw new IllegalStateException(s"The data for key items could not be found in the Json"),
          list => if(list.nonEmpty) {
            list.map(item => item.get[Long]("amount") -> item.get[LocalDate]("clearingDate"))
          } else {
            throw new IllegalStateException("The items list was found but the list was empty")
          }
        )
      }

      val seq = transactionsList map { transaction =>
        getItemsForPeriod(transaction) map { case (amount, clearedDate) =>
          PaymentsHistoryModel(
            taxPeriodFrom = transaction.get[LocalDate]("taxPeriodFrom"),
            taxPeriodTo   = transaction.get[LocalDate]("taxPeriodTo"),
            amount        = amount,
            clearedDate   = clearedDate
          )
        }
      }
      JsSuccess(seq.flatten)
    }
  }
}
