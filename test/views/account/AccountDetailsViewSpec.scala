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

package views.account

import models.{Address, User}
import models.viewModels.AccountDetailsModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class AccountDetailsViewSpec extends ViewBaseSpec {

  object Selectors {
    val pageHeading = "h1"
    val editAccountHeading = "div#edit-details p"
    val btaBreadcrumb = "div.breadcrumbs li:nth-of-type(1)"
    val btaBreadcrumbLink = "div.breadcrumbs li:nth-of-type(1) a"
    val vatBreadcrumb = "div.breadcrumbs li:nth-of-type(2)"
    val vatBreadcrumbLink = "div.breadcrumbs li:nth-of-type(2) a"
    val detailsBreadcrumb = "div.breadcrumbs li:nth-of-type(3)"
    val businessDetailsHeading = "h2:nth-of-type(1)"
    val correspondenceDetailsHeading = "h2:nth-of-type(2)"

    val nameRowQuestion = "div#name dt"
    val businessAddressRowQuestion = "div#business-address dt"
    val businessContactRowQuestion = "div#business-contact dt"
    val businessPhoneRowQuestion = "div#business-phone dt"
    val businessMobileRowQuestion = "div#business-mobile dt"
    val correspondenceAddressRowQuestion = "div#correspondence-address dt"
    val correspondenceContactRowQuestion = "div#correspondence-contact dt"
    val correspondenceEmailRowQuestion = "div#correspondence-email dt"
    val correspondencePhoneRowQuestion = "div#correspondence-phone dt"
    val correspondenceMobileRowQuestion = "div#correspondence-mobile dt"

    val nameRowAnswer = "div#name dd"
    val businessAddressRowAnswer = "div#business-address dd"
    val businessContactRowAnswer = "div#business-contact dd"
    val businessPhoneRowAnswer = "div#business-phone dd"
    val businessMobileRowAnswer = "div#business-mobile dd"
    val correspondenceAddressRowAnswer = "div#correspondence-address dd"
    val correspondenceContactRowAnswer = "div#correspondence-contact dd"
    val correspondenceEmailRowAnswer = "div#correspondence-email dd"
    val correspondencePhoneRowAnswer = "div#correspondence-phone dd"
    val correspondenceMobileRowAnswer = "div#correspondence-mobile dd"
  }

  private val user = User("1111")
  val account = AccountDetailsModel(
    "Test User",
    Address(
      "1 Correspondence Address",
      "Correspondence",
      Some("Correspondence"),
      Some("Correspondence"),
      Some("1CPC")
    ),
    Address(
      "1 Business Address",
      "Business",
      Some("Business"),
      Some("Business"),
      Some("1BPC")
    ),
    "123456789",
    "987654321",
    "123456789",
    "user@test.com"
  )

  "Rendering the VAT details page" should {

    lazy val view = views.html.account.accountDetails(user, account)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Account details"
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "Account details"
    }

    "render breadcrumbs which" should {

      "have the text 'Business tax account'" in {
        elementText(Selectors.btaBreadcrumb) shouldBe "Business tax account"
      }

      "link to bta" in {
        element(Selectors.btaBreadcrumbLink).attr("href") shouldBe "bta-url"
      }

      "have the text 'VAT'" in {
        elementText(Selectors.vatBreadcrumb) shouldBe "VAT"
      }

      s"link to ${controllers.routes.VatDetailsController.details().url}" in {
        element(Selectors.vatBreadcrumbLink).attr("href") shouldBe controllers.routes.VatDetailsController.details().url
      }

      "have the text 'Account details'" in {
        elementText(Selectors.detailsBreadcrumb) shouldBe "Account details"
      }
    }

    "have the business details section" in {
      elementText(Selectors.businessDetailsHeading) shouldBe "Business details"
    }

    "have the contact details section" in {
      elementText(Selectors.correspondenceDetailsHeading) shouldBe "Contact details"
    }

    "have the name question" in {
      elementText(Selectors.nameRowQuestion) shouldBe "Name"
    }

    "have the business address question" in {
      elementText(Selectors.businessAddressRowQuestion) shouldBe "Business address"
    }

    "have the business phone question" in {
      elementText(Selectors.businessPhoneRowQuestion) shouldBe "Business phone"
    }

    "have the business mobile phone question" in {
      elementText(Selectors.businessMobileRowQuestion) shouldBe "Mobile phone"
    }

    "have the correspondence address question" in {
      elementText(Selectors.correspondenceAddressRowQuestion) shouldBe "Correspondence address"
    }

    "have the email question" in {
      elementText(Selectors.correspondenceEmailRowQuestion) shouldBe "Email"
    }

    "have the home phone question" in {
      elementText(Selectors.correspondencePhoneRowQuestion) shouldBe "Home phone"
    }

    "have the mobile phone question" in {
      elementText(Selectors.correspondenceMobileRowQuestion) shouldBe "Mobile phone"
    }

    "have the name answer" in {
      elementText(Selectors.nameRowAnswer) shouldBe "Test User"
    }

    "have the business address answer" in {
      elementText(Selectors.businessAddressRowAnswer) shouldBe "1 Business Address, Business, Business, Business 1BPC"
    }

    "have the business phone answer" in {
      elementText(Selectors.businessPhoneRowAnswer) shouldBe "123456789"
    }

    "have the business mobile phone answer" in {
      elementText(Selectors.businessMobileRowAnswer) shouldBe "987654321"
    }

    "have the correspondence address answer" in {
      elementText(Selectors.correspondenceAddressRowAnswer) shouldBe "1 Correspondence Address, Correspondence, Correspondence, Correspondence 1CPC"
    }

    "have the email answer" in {
      elementText(Selectors.correspondenceEmailRowAnswer) shouldBe "user@test.com"
    }

    "have the home phone answer" in {
      elementText(Selectors.correspondencePhoneRowAnswer) shouldBe "123456789"
    }

    "have the mobile phone answer" in {
      elementText(Selectors.correspondenceMobileRowAnswer) shouldBe "987654321"
    }
  }
}
