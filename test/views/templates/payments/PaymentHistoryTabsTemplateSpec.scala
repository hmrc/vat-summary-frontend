/*
 * Copyright 2023 HM Revenue & Customs
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

  "The payment history tabs template" should {

    val youCan = "You can"
    val viewPrefPayments = "view your previous payments (opens in a new tab)"
    val beforeMTD = "if you made payments before joining Making Tax Digital."

    "render a series of tabs, including the previous payments tab" in {

      val expectedMarkup = Html(
        s"""
          |<p class="govuk-body" id="previous-payment">
          |  $youCan
          |  <a class="govuk-link" rel="noreferrer noopener" href="/previousPaymentsPortal" target="_blank">$viewPrefPayments</a>
          |  $beforeMTD
          |</p>
        """.stripMargin
      )
      val result = paymentsHistoryTabs()(messages, mockAppConfig, user)

      formatHtml(result) shouldBe formatHtml(expectedMarkup)
    }
  }
}