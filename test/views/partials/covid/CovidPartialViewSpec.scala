/*
 * Copyright 2020 HM Revenue & Customs
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

package views.partials.covid

import common.MessageLookup.CovidMessages._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.partials.covid.CovidMessage

class CovidPartialViewSpec extends ViewBaseSpec {

  val covidMessageView: CovidMessage = injector.instanceOf[CovidMessage]
  "Rendering the covid partial" should {

    object Selectors {
      val icon = ".icon"
      val line1 = "li:nth-of-type(1)"
      val line2 = "li:nth-of-type(2)"
    }

    "The covid partial pre end of June 2020" should {

      lazy val view = covidMessageView(preCovidDeadline = true)
      implicit lazy val render: Document = Jsoup.parse(view.body)

      "have the correct first message" in {
        elementText(Selectors.line1) shouldBe line1
      }

      "have the correct second message" which {

        "has the correct text" in {
          elementText(Selectors.line2) shouldBe line2
        }

      }
    }
  }
}