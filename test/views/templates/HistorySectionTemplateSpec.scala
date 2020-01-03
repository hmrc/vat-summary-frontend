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

package views.templates

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class HistorySectionTemplateSpec extends ViewBaseSpec {

  "The history section template" when {

    val currentYear: Int = 2009

    object Selectors {
      val viewPastPayments = ".list > li:nth-child(1) > a:nth-child(1)"
      val viewPastReturns = ".list > li:nth-child(2) > a:nth-child(1)"
      val historyHeading = "#history > h2:nth-child(1)"

    }

    "the user is Not Hybrid" should {

      lazy val view = views.html.templates.historySection(currentYear, isHybridUser = false)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the 'History' heading" in {
        elementText(Selectors.historyHeading) shouldBe "History"
      }

      "display the past payments text" in {
        elementText(Selectors.viewPastPayments) shouldBe "View past payments"
      }


      "have a link to the payment history" in {
        element(Selectors.viewPastPayments).attr("href") shouldBe controllers.routes.PaymentHistoryController.paymentHistory().url
      }

      "have the past returns text" in {
        elementText(Selectors.viewPastReturns) shouldBe "View past returns"
      }

      "have a link to the Submitted returns" in {
        element(Selectors.viewPastReturns).attr("href") shouldBe s"returns-url/$currentYear"
      }
    }

    "the user is Hybrid" should {

      lazy val view = views.html.templates.historySection(currentYear, isHybridUser = true)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the past payments text " in {
        elementText(Selectors.viewPastPayments) shouldBe "View past payments (opens in a new tab)"
      }

      "have a link to the portal via the PortalController" in {
        element(Selectors.viewPastPayments).attr("href") shouldBe "/vat-through-software/portal-payment-history"
      }

    }
  }

}
