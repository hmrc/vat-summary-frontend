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

package views.templates.formatters.dates

import java.time.LocalDate

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class DisplayDateTemplateSpec extends ViewBaseSpec {

  "Calling displayDate" when {

    val date = LocalDate.parse("2017-01-01")

    "showYear is true" should {

      lazy val template = views.html.templates.formatters.dates.displayDate(date)
      lazy val document: Document = Jsoup.parse(template.body)

      "render the date with year" in {
        document.body().text() shouldEqual "1 January 2017"
      }
    }

    "showYear is false" should {

      lazy val template = views.html.templates.formatters.dates.displayDate(date, showYear = false)
      lazy val document: Document = Jsoup.parse(template.body)

      "render the date without year" in {
        document.body().text() shouldEqual "1 January"
      }
    }
  }
}
