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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import views.ViewBaseSpec

class PaymentsAndRepaymentsSectionTemplateSpec extends ViewBaseSpec {

  object Selectors {
    val paymentsAndRepaymentsHeading = "#payments-and-repayments h2"
    val paymentsAndRepaymentsLink = "#payments-and-repayments h2 a"
    val paymentsAndRepaymentsParagraph = "#payments-and-repayments p"
  }

  def view: HtmlFormat.Appendable = views.html.templates.paymentsAndRepaymentsSection()

  implicit def document: Document = Jsoup.parse(view.body)

  "the payments and repayments section" should {

    "have the correct heading" in {
      elementText(Selectors.paymentsAndRepaymentsHeading) shouldBe "Manage bank accounts or view repayments"
    }

    "have the correct link" in {
      elementAttributes(Selectors.paymentsAndRepaymentsLink).get("href") shouldBe Some("/vat-repayment-tracker" +
        "/manage-or-track-vrt")
    }

    "have the correct paragraph" in {
      elementText(Selectors.paymentsAndRepaymentsParagraph) shouldBe "Change or add Direct Debit details or repayment" +
        " bank account details and if HMRC owes you money, find out when you'll be paid."
    }

  }
}
