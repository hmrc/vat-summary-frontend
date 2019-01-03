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

package views.errors

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class SessionTimeoutViewSpec extends ViewBaseSpec {

  "Rendering the session timeout page" should {

    object Selectors {
      val pageHeading = "#content h1"
      val instructions = "#content p"
    }

    lazy val view = views.html.errors.sessionTimeout()
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Your session has timed out"
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "Your session has timed out"
    }

    "have the correct instructions on the page" in {
      elementText(Selectors.instructions) shouldBe "To view your VAT summary, you will have to sign in using your Government Gateway ID."
    }
  }
}
