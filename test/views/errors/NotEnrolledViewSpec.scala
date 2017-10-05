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

package views.errors

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewSpec

class NotEnrolledViewSpec extends ViewSpec {

  "Rendering the not enrolled page" should {

    object Selectors {
      val pageHeading = "#content h1"
    }

    lazy val view = views.html.errors.notEnrolled(mockConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Not enrolled - TODO"
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "Not enrolled - TODO"
    }
  }
}
