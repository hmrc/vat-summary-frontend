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

package views.templates.formatters.dates

import java.time.LocalDate

import views.ViewBaseSpec
import views.templates.formatters.dates.DisplayDateRangeHelper.{displayDate, displayDateRange}

class DisplayDateRangeHelperSpec extends ViewBaseSpec {

  "Calling displayDate" when {

    val date = LocalDate.parse("2017-01-01")

    "showYear is true" should {

      lazy val formattedDate = displayDate(date)

      "render the date with year" in {
        formattedDate shouldBe "1 January 2017"
      }
    }

    "showYear is true and use short month format is true" should {

      lazy val formattedDate = displayDate(date, useShortDayFormat = true)

      "render the date with year" in {
        formattedDate shouldBe "1 Jan 2017"
      }
    }

    "showYear is false" should {

      lazy val formattedDate = displayDate(date, showYear = false)

      "render the date without year" in {
        formattedDate shouldBe "1 January"
      }
    }

    "showYear is false and use short month format is true" should {

      lazy val formattedDate = displayDate(date, showYear = false, useShortDayFormat = true)

      "render the date without year" in {
        formattedDate shouldBe "1 Jan"
      }
    }
  }

  "Calling displayDateRange template" when {

    val startDate: LocalDate = LocalDate.parse("2017-01-01")

    "start and end dates are in the same year" should {

      val endDate: LocalDate = LocalDate.parse("2017-04-01")
      val formattedDate = displayDateRange(startDate, endDate)

      "render the correct text" in {
        formattedDate shouldBe "1 January to 1 April 2017"
      }
    }

    "start and end dates are in the same year with short month format" should {

      val endDate: LocalDate = LocalDate.parse("2017-04-01")
      val formattedDate = displayDateRange(startDate, endDate, useShortDayFormat = true)

      "render the correct text" in {
        formattedDate shouldBe "1 Jan to 1 Apr 2017"
      }
    }

    "start and end dates are not in the same year" should {

      val endDate: LocalDate = LocalDate.parse("2018-04-01")
      val formattedDate = displayDateRange(startDate, endDate)

      "render the correct text" in {
        formattedDate shouldBe "1 January 2017 to 1 April 2018"
      }
    }

    "start and end dates are not in the same year with short month format" should {

      val endDate: LocalDate = LocalDate.parse("2018-04-01")
      val formattedDate = displayDateRange(startDate, endDate, useShortDayFormat = true)

      "render the correct text" in {
        formattedDate shouldBe "1 Jan 2017 to 1 Apr 2018"
      }
    }
  }
}
