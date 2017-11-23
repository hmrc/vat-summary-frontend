/*
 * Copyright 2017 HM Revenue & Customs
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

package views.templates.formatters

import java.time.LocalDate
import java.time.format.{DateTimeFormatter, ResolverStyle}
import views.templates.formatters.DateFormat._
import uk.gov.hmrc.play.test.UnitSpec

class DateFormatSpec extends UnitSpec {

  "DateFormat .dateFormat" should {

    val date = LocalDate.parse("2017-08-14")
    val result = dateFormat.format(date)

    "return a formatted date" in {
      result shouldEqual "14 August 2017"
    }
  }

  "DateFormat .dateFormatNoYear" should {

    val date = LocalDate.parse("2017-08-14")
    val result = dateFormatNoYear.format(date)

    "return a formatted date with no year" in {
      result shouldEqual "14 August"
    }
  }

  "DateFormat .datesInYear" when {

    "dates are in the same year" should {

      val startDate = LocalDate.parse("2017-08-14")
      val endDate = LocalDate.parse("2017-08-31")

      val result = DateFormat.datesInYear(startDate, endDate)

      "return true" in {
        result shouldEqual true
      }
    }

    "dates are not in the same year" should {

      val startDate = LocalDate.parse("2017-08-14")
      val endDate = LocalDate.parse("2016-08-31")

      val result = DateFormat.datesInYear(startDate, endDate)

      "return false" in {
        result shouldEqual false
      }
    }
  }
}
