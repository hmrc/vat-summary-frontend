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

package views.templates.formatters.tabs

import play.twirl.api.Html
import views.templates.TemplateBaseSpec

class ActiveTabTemplateSpec extends TemplateBaseSpec {

  "The active tab template" should {

    "render an active tab component" in {
      val tabName = "Tabby"
      val hiddenText = "This is an active tab"

      val expectedMarkup = Html(
        """
          |<li class="tabs-nav__tab tabs-nav__tab--active font-medium" role="presentation">
          |  <a href="#" class="in-selected-tab" role="tab"
          |    aria-controls="Tabby" aria-selected="true" tabindex="0">
          |    Tabby
          |</a>
          |  <span class="visuallyhidden">This is an active tab</span>
          |</li>
        """.stripMargin
      )
      val result = views.html.templates.formatters.tabs.activeTab(tabName, hiddenText)

      formatHtml(result) shouldBe formatHtml(expectedMarkup)
    }
  }
}
