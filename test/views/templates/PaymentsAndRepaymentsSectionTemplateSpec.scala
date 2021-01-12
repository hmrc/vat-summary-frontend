/*
 * Copyright 2021 HM Revenue & Customs
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
import views.html.templates.PaymentsAndRepaymentsSection

class PaymentsAndRepaymentsSectionTemplateSpec extends ViewBaseSpec {

  val paymentsAndRepaymentsSection: PaymentsAndRepaymentsSection = injector.instanceOf[PaymentsAndRepaymentsSection]
  object Selectors {
    val paymentsAndRepaymentsHeading = "#payments-and-repayments h3"
    val paymentsAndRepaymentsLink = "#payments-and-repayments h3 a"
    val paymentsAndRepaymentsParagraph = "#payments-and-repayments p"
  }

  def view: HtmlFormat.Appendable = paymentsAndRepaymentsSection()

  implicit def document: Document = Jsoup.parse(view.body)

  "the payments and repayments section" should {

    "have the correct heading" in {
      elementText(Selectors.paymentsAndRepaymentsHeading) shouldBe "Payments and repayments"
    }

    "have the correct link" in {
      elementAttributes(Selectors.paymentsAndRepaymentsLink).get("href") shouldBe Some("/vat-repayment-tracker" +
        "/manage-or-track-vrt")
    }

    "have the correct paragraph" in {
      elementText(Selectors.paymentsAndRepaymentsParagraph) shouldBe "Manage your Direct Debit, repayment bank " +
        "account details and track what HMRC owe you."
    }

  }
}
