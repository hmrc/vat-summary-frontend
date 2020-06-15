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
      val icon = ".icon"
      val header = "p"
      val line1 = "li:nth-of-type(1)"
      val line2 = "li:nth-of-type(2)"
      val line2Link = "li:nth-of-type(2) > a"
      val line3 = "li:nth-of-type(3)"
      val line4 = "li:nth-of-type(4)"
    }

    lazy val view = covidMessageView()
    implicit lazy val render: Document = Jsoup.parse(view.body)

    "have alternate content for the icon" in {
      elementText(Selectors.icon) shouldBe "Warning"
    }

    "have the correct header" in {
      elementText(Selectors.header) shouldBe "You can no longer delay VAT payments because of coronavirus (COVID-19)"
    }

    "have the correct first message" in {
      elementText(Selectors.line1) shouldBe "VAT bills that have a payment date on or after 1 July 2020 must be paid on time."
    }

    "have the correct second message" which {

      "has the correct text" in {
        elementText(Selectors.line2) shouldBe "If you cancelled your Direct Debit, set it up again so you don’t miss a payment. " +
          "Contact us as soon as possible if you cannot pay."
      }

      "has a link to a gov page" in {
        element(Selectors.line2Link).attr("href") shouldBe "https://www.gov.uk/difficulties-paying-hmrc"
      }
    }

    "have the correct third message" in {
      elementText(Selectors.line3) shouldBe "You still need to submit VAT Returns, even if your business has temporarily closed."
    }

    "have the correct fourth message" in {
      elementText(Selectors.line4) shouldBe "You have until 31 March 2021 to pay VAT bills that were due between 20 March and 30 June 2020."
    }

  }
}
