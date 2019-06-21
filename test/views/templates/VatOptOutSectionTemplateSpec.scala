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

class  VatOptOutSectionTemplateSpec extends ViewBaseSpec {

  "The vatOptOutSection" should {

    object Selectors {
      val vatOptOutHeading = "#vat-optout > h2:nth-child(1) a"
      val vatOptOutParagraph = "#vat-optout > p:nth-child(2)"
    }

    def view: HtmlFormat.Appendable = views.html.templates.vatOptOutSection(Some(false))
    implicit def document: Document = Jsoup.parse(view.body)

    "have the correct heading" in {
      elementText(Selectors.vatOptOutHeading) shouldBe "Opt out of Making Tax Digital for VAT"
    }

    "have the correct link" in {
      element(Selectors.vatOptOutHeading).attr("href") shouldBe "/vat-through-software/account/opt-out"
    }

    "have the correct paragraph" in {
      elementText(Selectors.vatOptOutParagraph) shouldBe "You cannot opt out if your taxable turnover has been above £85,000 since 1 April 2019."
    }

  }
}