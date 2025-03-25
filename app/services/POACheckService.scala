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

package services

import com.google.inject.Inject
import services.DateService
import connectors.httpParsers.ResponseHttpParsers.HttpResult
import models.CustomerInformation
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try
import utils.LoggerUtil

class POACheckService @Inject() () extends LoggerUtil {
  def retrievePoaActiveForCustomer(
      accountDetails: HttpResult[CustomerInformation],
      today: LocalDate
  ): Boolean = {
    accountDetails match {
      case Right(customerDetails) =>
        isDateEqualsTodayFuture(
          customerDetails.poaActiveUntil,
          today
        )
      case _ => false
    }
  }

  private def isDateEqualsTodayFuture(
      poaActiveUntil: Option[String],
      currentDate: LocalDate
  ): Boolean = {
    val dateFormat: String = "yyyy-MM-dd"
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat)
    poaActiveUntil match {
      case Some(poaActiveUntilDate) =>
        val parsedDate = Try(LocalDate.parse(poaActiveUntilDate, formatter))
          .getOrElse(LocalDate.MIN)
        if (
          parsedDate.isAfter(currentDate) || parsedDate.isEqual(currentDate)
        ) {
          logger.info(
            s"Date condition met, parsedDate ($parsedDate) is today or in the future"
          )
          true
        } else {
          logger.info(
            s"Date condition failed, parsedDate ($parsedDate) is in the past or not available"
          )
          false
        }
      case _ => false
    }
  }

}
