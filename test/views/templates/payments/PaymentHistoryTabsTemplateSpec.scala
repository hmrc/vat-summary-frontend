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

    val heading = "Service has closed"
    val para = "You can no longer view previous payments. Please refer to your own business records and accounts."

    "render a series of tabs, including the previous payments tab" in {

      val expectedMarkup = Html(
        s"""
          |<h1 class="govuk-heading-s">$heading</h1>
          |<p class="govuk-body" id="previous-payment">
          |  $para
          |</p>
        """.stripMargin
      )
      val result = paymentsHistoryTabs()(messages)

      formatHtml(result) shouldBe formatHtml(expectedMarkup)
    }
  }
}