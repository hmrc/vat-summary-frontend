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

class  MtdSignUpSectionTemplateSpec extends ViewBaseSpec {

  object Selectors {
    val mtdSignupSection = "#mtd-signup"
    val mtdSignupSectionHeading = s"$mtdSignupSection > h2:nth-child(1)"
    val mtdSignupSectionHeadingLink = s"$mtdSignupSectionHeading a"
    val mtdSignupSectionParagraph = s"$mtdSignupSection > p:nth-child(2)"
  }

  "The mtdSignupSection" should {

    val view: HtmlFormat.Appendable = views.html.templates.mtdSignupSection(vrn = "123456789")
    implicit val document: Document = Jsoup.parse(view.body)

    "have the correct heading" in {
      elementText(Selectors.mtdSignupSectionHeading) shouldBe "Sign up for Making Tax Digital for VAT"
    }

    "have the correct link" in {
      element(Selectors.mtdSignupSectionHeadingLink).attr("href") shouldBe "/vat-through-software/sign-up/" +
        "vat-number/123456789"
    }

    "have the correct paragraph" in {
      elementText(Selectors.mtdSignupSectionParagraph) shouldBe "If your taxable turnover exceeds the VAT threshold, " +
        "you must sign up to Making Tax Digital for VAT."
    }
  }
}
