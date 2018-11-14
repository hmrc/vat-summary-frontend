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

package views.partials.btaHome

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class VatSectionViewSpec extends ViewBaseSpec {

  "Rendering the BTA partial" should {

    object Selectors {
      val pageHeading = "h2"
      val viewDeadlines = "p:nth-of-type(1)"
      val updateDetails = "p:nth-of-type(2)"
      val viewVatLink = "a"
    }

    lazy val view = views.html.partials.btaHome.vatSection()
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "VAT - Making Tax Digital"
    }

    "have the correct service features message on the page" in {
      elementText(Selectors.viewDeadlines) shouldBe
      "You can view what you owe, your return deadlines, your payment history, previously submitted returns and make VAT payments."
    }

    "have the correct update details message on the page" in {
      elementText(Selectors.updateDetails) shouldBe "You can also update some of your VAT details."
    }

    "have the correct link address" in {
      element(Selectors.viewVatLink).attr("href") shouldBe "mock-url"
    }
  }
}
