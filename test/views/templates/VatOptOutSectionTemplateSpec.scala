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
import play.twirl.api.HtmlFormat
import views.ViewBaseSpec

class  VatOptOutSectionTemplateSpec extends ViewBaseSpec {

  object Selectors {
    val vatOptOutSection = "#vat-optout"
    val vatOptOutHeading = s"$vatOptOutSection > h3:nth-child(1)"
    val vatOptOutHeadingLink = s"$vatOptOutHeading a"
    val vatOptOutParagraph = s"$vatOptOutSection > p:nth-child(2)"
  }

  "The vatOptOutSection" should {

    val view: HtmlFormat.Appendable = views.html.templates.vatOptOutSection(pendingOptOut = false)
    implicit val document: Document = Jsoup.parse(view.body)

    "have the correct heading" in {
      elementText(Selectors.vatOptOutHeading) shouldBe "Opt out of Making Tax Digital for VAT"
    }

    "have the correct link" in {
      element(Selectors.vatOptOutHeadingLink).attr("href") shouldBe "/vat-through-software/account/opt-out"
    }

    "have the correct paragraph" in {
      elementText(Selectors.vatOptOutParagraph) shouldBe "You cannot opt out if your taxable turnover has been above £85,000 since 1 April 2019."
    }

    "the user has an opt out request pending" should {

      val view: HtmlFormat.Appendable = views.html.templates.vatOptOutSection(pendingOptOut = true)
      implicit val document: Document = Jsoup.parse(view.body)

      "not have a link to the opt out service" in {
        elementExtinct(Selectors.vatOptOutHeadingLink)
      }

      "have the correct paragraph" in {
        elementText(Selectors.vatOptOutParagraph) shouldBe "Your request to opt out is being processed."
      }
    }
  }
}