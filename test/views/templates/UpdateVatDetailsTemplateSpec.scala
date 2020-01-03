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

class UpdateVatDetailsTemplateSpec extends ViewBaseSpec {

  "The updateVatDetailsSection" when {

    object Selectors {
      val updateVatDetails = "#update-vat-details"
    }

    def view: HtmlFormat.Appendable = views.html.templates.updateVatDetailsSection()
    implicit def document: Document = Jsoup.parse(view.body)

    "the update Vat details feature" should {

      "have the correct heading" in {
        element(Selectors.updateVatDetails).select("h2").text() shouldBe "Your business details"
      }

      "have the correct hand-off link" in {
        element(Selectors.updateVatDetails).select("a").attr("href") shouldBe "/vat-through-software/account/change-business-details"
      }

      "have the correct text" in {
        element(Selectors.updateVatDetails).select("p").text() shouldBe "Change your business, contact or VAT details."
      }
    }
  }
}
