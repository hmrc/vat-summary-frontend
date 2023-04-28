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

package views.templates

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import views.ViewBaseSpec
import views.html.templates.VatCertificateSection

class VatCertificateSectionTemplateSpec extends ViewBaseSpec {

  val vatCertificateSection: VatCertificateSection = injector.instanceOf[VatCertificateSection]

  "The vatCertificateSection" when {

    object Selectors {
      val vatCertificateHeading = "#vat-certificate h3 a"
      val vatCertificateParagraph = "#vat-certificate p"
    }

    val view: HtmlFormat.Appendable = vatCertificateSection()
    implicit val document: Document = Jsoup.parse(view.body)

    "the vat certificate feature" should {

      "have the correct heading" in {
        elementText(Selectors.vatCertificateHeading) shouldBe "View VAT certificate"
      }

      "have the correct link" in {
        element(Selectors.vatCertificateHeading).attr("href") shouldBe controllers.routes.VatCertificateController.show.url
      }

      "have the correct paragraph" in {
        elementText(Selectors.vatCertificateParagraph) shouldBe "View and print your VAT certificate."
      }
    }
  }
}
