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

package views

import models.User
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.GovUkWrapper

class GovUkWrapperSpec extends ViewBaseSpec {

  val navTitleSelector = ".header__menu__proposition-name"
  val accessibilityFooterSelector = "#footer > div > div > div.footer-meta-inner > ul > li:nth-child(2) > a"
  val govUkWrapper: GovUkWrapper = injector.instanceOf[GovUkWrapper]

  "Calling .govUkWrapper" when {

    "user is not known" should {

      lazy val view = govUkWrapper(mockConfig, "title", user = None)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the default nav title 'Making Tax Digital for VAT'" in {
        elementText(navTitleSelector) shouldBe "VAT"
      }

      "default to the 'Making Tax Digital for VAT' page title" in {
        document.title shouldBe "title - VAT - GOV.UK"
      }
    }

    "user is known" when {

      "user is an agent" should {

        lazy val view = govUkWrapper(mockConfig, "title", user = Some(User("999999999", arn = Some("XARN1234567"))))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have a nav title of 'Your client’s VAT details'" in {
          elementText(navTitleSelector) shouldBe "Your client’s VAT details"
        }

        "have the correct page title" in {
          document.title shouldBe "title - Your client’s VAT details - GOV.UK"
        }
      }

      "user is not an agent" should {

        lazy val view = govUkWrapper(mockConfig, "title", user = Some(User("999999999", arn = None)))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have a nav title of 'Business tax account'" in {
          elementText(navTitleSelector) shouldBe "Business tax account"
        }

        "have the correct page title" in {
          document.title shouldBe "title - Business tax account - GOV.UK"
        }
      }
    }

    "the accessibility statement link has been provided in the footer links" should {

      lazy val view = govUkWrapper(mockConfig, "title", user = Some(User("999999999", arn = None)))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct accessibility statement link" in {
        element(accessibilityFooterSelector).attr("href") shouldBe "/vat-through-software/accessibility-statement"
      }
    }
  }
}
