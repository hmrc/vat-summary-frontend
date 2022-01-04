/*
 * Copyright 2022 HM Revenue & Customs
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
import views.html.templates.payments.PaymentsHistoryTabs
import views.templates.TemplateBaseSpec

class PaymentHistoryTabsTemplateSpec extends TemplateBaseSpec {

  val paymentsHistoryTabs: PaymentsHistoryTabs = injector.instanceOf[PaymentsHistoryTabs]

  "The payment history tabs template" when {

    val tab1Year = 2022
    val tab2Year = 2023
    val prevPayments = "Previous payments"
    val prevPaymentsHref = "previous-payments"
    val tabs = Seq(tab1Year, tab2Year)

    "the previous payments boolean is set to true" should {

      "render a series of tabs, including the previous payments tab" in {

        val expectedMarkup = Html(
          s"""
            |<ul class="govuk-tabs__list">
            |  <li class="govuk-tabs__list-item govuk-tabs__list-item--selected">
            |    <a class="govuk-tabs__tab" href="#year-$tab1Year">
            |      $tab1Year
            |    </a>
            |  </li>
            |  <li class="govuk-tabs__list-item">
            |    <a class="govuk-tabs__tab" href="#year-$tab2Year">
            |      $tab2Year
            |    </a>
            |  </li>
            |  <li class="govuk-tabs__list-item">
            |    <a class="govuk-tabs__tab" href="#$prevPaymentsHref">
            |      $prevPayments
            |    </a>
            |  </li>
            |</ul>
          """.stripMargin
        )
        val result = paymentsHistoryTabs(tabs, showPreviousPaymentsTab = true)

        formatHtml(result) shouldBe formatHtml(expectedMarkup)
      }
    }

    "the previous payments boolean is set to false" should {

      "render a series of tabs, excluding the previous payments tab" in {

        val expectedMarkup = Html(
          s"""
             |<ul class="govuk-tabs__list">
             |  <li class="govuk-tabs__list-item govuk-tabs__list-item--selected">
             |    <a class="govuk-tabs__tab" href="#year-$tab1Year">
             |      $tab1Year
             |    </a>
             |  </li>
             |  <li class="govuk-tabs__list-item">
             |    <a class="govuk-tabs__tab" href="#year-$tab2Year">
             |      $tab2Year
             |    </a>
             |  </li>
             |</ul>
          """.stripMargin
        )
        val result = paymentsHistoryTabs(tabs, showPreviousPaymentsTab = false)

        formatHtml(result) shouldBe formatHtml(expectedMarkup)
      }
    }
  }
}