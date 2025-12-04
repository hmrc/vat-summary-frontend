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
import models.ChangedOnVatPeriod.RequestCategoryType4
import models.{StandingRequest, StandingRequestDetail}
import utils.LoggerUtil

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.util.Try

class AnnualAccountingCheckService @Inject() extends LoggerUtil {

  def changedOnDateWithinLast3Months(standingRequestScheduleOpt: Option[StandingRequest], today: LocalDate): Option[LocalDate] = {
    standingRequestScheduleOpt match {
      case Some(standingRequestSchedule) => standingRequestSchedule.standingRequests.filter(_.requestCategory.equals(RequestCategoryType4)).flatMap(sr =>
        doesSrChangedOnFallsInLast4Months(sr,today)).sorted.reverse.headOption match {
        case Some(date) =>
          logger.debug(
            s"[changedOnDateWithinLast4Months]: Date condition for Changed On passed, with  standingRequestSchedule ($standingRequestSchedule)"
          )
          Some(date)
        case None =>
          logger.debug(
            s"[changedOnDateWithinLast4Months]: Date condition for Changed On failed, with  standingRequestSchedule ($standingRequestSchedule)"
          )
          None
      }
      case None =>
        logger.debug(
          s"[changedOnDateWithinLast4Months]: standingRequestSchedule for this customer is empty"
        )
        None
    }
  }

  private def doesSrChangedOnFallsInLast4Months(standingRequest: StandingRequestDetail, today: LocalDate): Option[LocalDate] = {
    val dateFormat: String = "yyyy-MM-dd"
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat)
    val changedOnOpt = if (standingRequest.changedOn.isDefined) Try(LocalDate.parse(standingRequest.changedOn.get, formatter)).toOption else None
    val changedOnThreshold = 4
    val fourMonthsAgo = today.minusMonths(changedOnThreshold)

    if(changedOnOpt.isDefined && changedOnOpt.get.isAfter(fourMonthsAgo)) {
      changedOnOpt
    } else {
      None
    }
  }
}
