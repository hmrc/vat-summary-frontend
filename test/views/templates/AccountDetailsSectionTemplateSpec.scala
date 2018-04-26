/*
 * Copyright 2018 HM Revenue & Customs
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

import models.User
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import views.ViewBaseSpec

class AccountDetailsSectionTemplateSpec extends ViewBaseSpec {

  "The accountDetailsSection" when {

    object Selectors {
      val accountDetailsHeading = "#account-details h2 a"
    }

    def view: HtmlFormat.Appendable = views.html.templates.accountDetailsSection(User("123456789"), mockConfig)
    implicit def document: Document = Jsoup.parse(view.body)

    "the account details feature is disabled" should {

      "have the correct heading" in {
        mockConfig.features.accountDetails(false)
        elementText(Selectors.accountDetailsHeading) shouldBe "View VAT certificate (opens in a new tab)"
      }

      "have the correct portal link" in {
        mockConfig.features.accountDetails(false)
        elementAttributes(Selectors.accountDetailsHeading).get("href") shouldBe Some("/vat/trader/123456789/certificate")
      }
    }

    "the account details feature is enabled" should {

      "have the correct heading" in {
        mockConfig.features.accountDetails(true)
        elementText(Selectors.accountDetailsHeading) shouldBe "Account details"
      }
    }
  }
}
