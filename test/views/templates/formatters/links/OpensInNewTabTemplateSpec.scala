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

package views.templates.formatters.links

import play.twirl.api.Html
import views.html.templates.formatters.links.OpenInNewTab
import views.templates.TemplateBaseSpec

class OpensInNewTabTemplateSpec extends TemplateBaseSpec {

  val openInNewTab: OpenInNewTab = injector.instanceOf[OpenInNewTab]

  "Calling openInNewTab" should {

    val link = "/link"
    val linkText = "link text"

    val expectedMarkup = Html(
      s"""
         |<a href="$link" target="_blank">$linkText (opens in a new tab)</a>
      """.stripMargin
    )

    val markup = openInNewTab(link, linkText)

    "return the correct markup" in {
      formatHtml(markup) shouldBe formatHtml(expectedMarkup)
    }
  }
}
