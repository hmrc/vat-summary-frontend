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

import mocks.MockAppConfig
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewSpec
import common.MessageLookup.SessionTimeout._

class SessionTimeoutViewSpec extends ViewSpec {

  "Rendering the session timeout page" should {

    object Selectors {
      val pageHeading = "#content h1"
      val instructions = "#content p"
      val link = "#content p a"
    }

    lazy val mockAppConfig: MockAppConfig = new MockAppConfig {
      override val ggSignInUrl: String = "sign-in"
    }

    lazy val view = views.html.errors.sessionTimeout(mockAppConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe title
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe title
    }

    "have the correct instructions on the page" in {
      elementText(Selectors.instructions) shouldBe instructions
    }

    "contain a link to sign in" in {
      element(Selectors.link).attr("href") shouldBe "sign-in"
    }
  }
}
