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

package views.templates.payments

import play.twirl.api.Html
import views.templates.TemplateBaseSpec

class PaymentHistoryTabsTemplateSpec extends TemplateBaseSpec {

  "The payment history tabs template" should {

    "render a series of tabs, with the selected tab rendered as active" in {
      val tab1Year = "2022"
      val tab2Year = "2023"
      val hiddenText1 = "View payment history from 2022"
      val hiddenText2 = "Currently viewing payment history from 2023"
      def tabUrl(year: String): String = s"/vat-through-software/payment-history/$year"

      val tabs = Seq(2022, 2023)
      val selectedYear = 2023

      val expectedMarkup = Html(
        s"""
          |<li class="tabs-nav__tab font-medium" role="presentation">
          |  <a href="${tabUrl(tab1Year)}" role="tab" aria-controls="$tab1Year" aria-selected="false" tabindex="-1">
          |    $tab1Year
          |    <span class="visuallyhidden">$hiddenText1</span>
          |  </a>
          |</li>
          |<li class="tabs-nav__tab tabs-nav__tab--active font-medium" role="presentation">
          |  <a href="#" class="in-selected-tab" role="tab" aria-controls="$tab2Year" aria-selected="true" tabindex="0">
          |    $tab2Year
          |</a>
          |  <span class="visuallyhidden">$hiddenText2</span>
          |</li>
        """.stripMargin
      )
      val result = views.html.templates.payments.paymentsHistoryTabs(tabs, selectedYear)

      formatHtml(result) shouldBe formatHtml(expectedMarkup)
    }
  }
}