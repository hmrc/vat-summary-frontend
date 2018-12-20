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

package views.templates.formatters.dates

import java.time.LocalDate
import play.api.i18n.Messages
import java.time.format.{DateTimeFormatter, ResolverStyle}

object DisplayDateRangeHelper {

  def displayDateRange(from: LocalDate, to: LocalDate, useShortDayFormat: Boolean = false)
                      (implicit messages: Messages): String = {
    s"${displayDate(from, from.getYear != to.getYear, useShortDayFormat)} " +
      s"${messages("common.dateRangeSeparator")} " +
      s"${displayDate(to, true, useShortDayFormat)}"
  }

  def displayDate(date: LocalDate, showYear: Boolean = true, useShortDayFormat: Boolean = false): String = {
    val format = (if (useShortDayFormat) "d MMM" else "d MMMM") + (if (showYear) " uuuu" else "")
    val formatter = DateTimeFormatter.ofPattern(format).withResolverStyle(ResolverStyle.STRICT)
    formatter.format(date)
  }

}
