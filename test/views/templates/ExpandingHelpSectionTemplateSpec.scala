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

package views.templates

import play.twirl.api.Html
import views.html.templates.expandingHelpSection

class ExpandingHelpSectionTemplateSpec extends TemplateBaseSpec {

  "ExpandingHelpSectionTemplate" should {

    val helpTextTitle = "help"
    val feature = "payments"
    val pageName = "open-payments"
    val content = Html("<p>Some html</p>")

    val expectedMarkup = Html(
      s"""
        |<details>
        |    <summary><span class="summary" data-journey-click="$feature:help:reveal:$pageName">$helpTextTitle</span></summary>
        |    <div class="panel-indent">
        |        $content
        |    </div>
        |</details>
      """.stripMargin
    )

    val markup = expandingHelpSection(helpTextTitle, feature, pageName, content)

    "return the correct markup" in {
      formatHtml(markup) shouldBe formatHtml(expectedMarkup)
    }
  }
}
