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

package testOnly.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec

class BtaStubViewSpec extends ViewBaseSpec {

  "Rendering the BTA landing page test harness" should {

    object Selectors {
      val pageHeading = "#content h1"
      val h2 = "#content h2"
    }

    val htmlPartial = Html("<h2>Partial</h2>")
    lazy val view = testOnly.views.html.btaStub(htmlPartial)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "BTA Landing Page Stub - VAT - GOV.UK"
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "BTA Landing Page Stub"
    }

    "contain content of a partial" in {
      elementText(Selectors.h2) shouldBe "Partial"
    }
  }
}
