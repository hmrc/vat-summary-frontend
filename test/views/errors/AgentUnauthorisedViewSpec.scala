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

package views.errors

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class AgentUnauthorisedViewSpec extends ViewBaseSpec {

  "Rendering the agent unauthorised page" should {

    object Selectors {
      val pageHeading = "#content h1"
      val instructions = "article p"
      val instructionsLink = "article p > a"
      val button = "#content .button"
    }

    lazy val view = views.html.errors.agentUnauthorised()(request, messages, mockConfig)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    s"have the correct document title" in {
      document.title shouldBe "You can’t use this service yet - VAT - GOV.UK"
    }

    s"have a the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "You can’t use this service yet"
    }

    s"have the correct instructions on the page" in {
      elementText(Selectors.instructions) shouldBe "To use this service, you need to set up an agent services account."
    }

    s"have a link to GOV.UK guidance" in {
      element(Selectors.instructionsLink).attr("href") shouldBe "guidance/get-an-hmrc-agent-services-account"
    }

    s"have a Sign out button" in {
      elementText(Selectors.button) shouldBe "Sign out"
    }

    s"have a link to sign out" in {
      element(Selectors.button).attr("href") shouldBe controllers.routes.SignOutController.signOut(authorised = false).url
    }
  }
}

