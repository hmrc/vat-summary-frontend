/*
 * Copyright 2025 HM Revenue & Customs
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
import views.html.templates.AnnualAccountingSection

class AnnualAccountingSectionTemplateSpec extends ViewBaseSpec{

  val annualAccountingSection: AnnualAccountingSection = injector.instanceOf[AnnualAccountingSection]
  object Selectors {
    val annualAccountingHeading = "#vat-AA h3"
    val annualAccountingLink = "#vat-AA h3 a"
    val annualAccountingParagraph = "#vat-AA p"
  }

  def view: HtmlFormat.Appendable = annualAccountingSection()

  implicit def document: Document = Jsoup.parse(view.body)

  "the Annual Accounting section" should {

    "have the correct heading" in {
      elementText(Selectors.annualAccountingHeading) shouldBe "Annual Accounting"
    }

    "have the correct link" in {
      elementAttributes(Selectors.annualAccountingLink).get("href") shouldBe Some("/vat-through-software/interim-payments")
    }

    "have the correct paragraph" in {
      elementText(Selectors.annualAccountingParagraph) shouldBe "View your Annual Accounting schedule on payments."
    }
  }
}
