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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.partials.covid.CovidMessage

class CovidPartialViewSpec extends ViewBaseSpec {

  val covidMessageView: CovidMessage = injector.instanceOf[CovidMessage]
  "Rendering the covid partial" should {

    object Selectors {
      val header = "h3"
      val mainBody = "p:nth-child(2)"
      val message1 = "p:nth-child(4)"
      val message2 = "div.panel"
      val bullet1 = "li:nth-child(1)"
      val bullet2 = "li:nth-child(2)"
    }

    lazy val view = covidMessageView()
    lazy val render: Document = Jsoup.parse(view.body)

    "have the correct header" in {
      render.select(Selectors.header).text() shouldBe "Coronavirus (COVID 19) VAT deferral"
    }

    "have the correct first message" in {
      render.select(Selectors.mainBody).text() shouldBe "If you have VAT payments that are due between 20 March and 30 June 2020, you can choose to:"
    }

    "have a list of items" which {

      "has the first item correct" in {
        render.select(Selectors.bullet1).text() shouldBe "defer them without paying interest or penalties"
      }

      "has the second item correct" in {
        render.select(Selectors.bullet2).text() shouldBe "pay the VAT due as normal"
      }
    }

    "have the correct message1" in {
      render.select(Selectors.message1).text() shouldBe "You must continue to submit your returns as normal. " +
        "If you choose to defer your VAT payment, you must pay the VAT due on or before 31 March 2021. You do not need " +
        "to tell HMRC that you are deferring your VAT payment."
    }

    "have the correct message2" in {
      render.select(Selectors.message2).text() shouldBe "If you normally pay by Direct Debit, you must contact your bank to cancel it as soon as possible."
    }

  }
}
