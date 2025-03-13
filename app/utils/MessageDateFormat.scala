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

package utils

import play.api.i18n.Messages

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object MessageDateFormat {

  private[utils] val monthsInCy = Map(
    1 -> "Ionawr",
    2 -> "Chwefror",
    3 -> "Mawrth",
    4 -> "Ebrill",
    5 -> "Mai",
    6 -> "Mehefin",
    7 -> "Gorffennaf",
    8 -> "Awst",
    9 -> "Medi",
    10 -> "Hydref",
    11 -> "Tachwedd",
    12 -> "Rhagfyr"
  )

   private[utils] val monthsInCyShort = Map(
    1 -> "Ion",
    2 -> "Chwef",
    3 -> "Maw",
    4 -> "Ebr",
    5 -> "Mai",
    6 -> "Meh",
    7 -> "Gorff",
    8 -> "Awst",
    9 -> "Medi",
    10 -> "Hyd",
    11 -> "Tach",
    12 -> "Rhag"
  )

  def formatLong(date: LocalDate)(implicit messages: Messages): String = {
    val formatter = messages.lang.code match {
      case "cy" => DateTimeFormatter.ofPattern(s"d '${monthsInCy(date.getMonthValue)}' uuuu")
      case _ => DateTimeFormatter.ofPattern("d MMMM uuuu")
    }

    date.format(formatter)
  }

    def format(date: LocalDate)(implicit messages: Messages): String = {
        val formatter = messages.lang.code match {
            case "cy" => DateTimeFormatter.ofPattern(s"d '${monthsInCyShort(date.getMonthValue)}' uuuu")
            case _ => DateTimeFormatter.ofPattern("d MMM uuuu")
        }
        date.format(formatter)
    }

  def formatNoText(date: LocalDate): String = {
    val formatter = DateTimeFormatter.ofPattern("dd MM yyyy")
    date.format(formatter)
  }
}