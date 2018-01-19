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

import models.User
import models.viewModels.AccountDetailsModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class AccountDetailsViewSpec extends ViewBaseSpec {

  object Selectors {
    val pageHeading = "h1"
    val editAccountHeading = "div#edit-details p"

    val businessDetailsHeading = "h2:nth-of-type(1)"
    val correspondenceDetailsHeading = "h2:nth-of-type(2)"

    val nameRow = "dt#name"
    val businessAddressRow = "dt#business-address"
    val businessContactRow = "dt#business-contact"
    val businessPhoneRow = "dt#business-phone"
    val businessMobileRow = "dt#business-mobile"
    val correspondenceAddressRow = "dt#correspondence-address"
    val correspondenceContactRow = "dt#correspondence-contact"
    val correspondenceEmailRow = "dt#correspondence-email"
    val correspondencePhoneRow = "dt#correspondence-phone"
    val correspondenceMobileRow = "dt#correspondence-mobile"
  }

  private val user = User("1111")
  val account = AccountDetailsModel("", "", "", "", "", "", "", "", "")

  "Rendering the VAT details page" should {

    lazy val view = views.html.account.accountDetails(user, account)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Account details"
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "Account details"
    }

    "have the correct sub heading" in {
      elementText(Selectors.editAccountHeading) shouldBe "You edit these details in your accounting software."
    }

    "have the business details section" in {
      elementText(Selectors.businessDetailsHeading) shouldBe "Business details"
    }

    "have the contact details section" in {
      elementText(Selectors.correspondenceDetailsHeading) shouldBe "Contact details"
    }

    "have the name heading" in {
      elementText(Selectors.nameRow) shouldBe "Name"
    }

    "have the business address heading" in {
      elementText(Selectors.businessAddressRow) shouldBe "Business address"
    }

    "have the business phone heading" in {
      elementText(Selectors.businessPhoneRow) shouldBe "Business phone"
    }

    "have the business mobile phone heading" in {
      elementText(Selectors.businessMobileRow) shouldBe "Mobile phone"
    }

    "have the correspondence address heading" in {
      elementText(Selectors.correspondenceAddressRow) shouldBe "Correspondence address"
    }

    "have the email heading" in {
      elementText(Selectors.correspondenceEmailRow) shouldBe "Email"
    }

    "have the home phone heading" in {
      elementText(Selectors.correspondencePhoneRow) shouldBe "Home phone"
    }

    "have the mobile phone heading" in {
      elementText(Selectors.correspondenceMobileRow) shouldBe "Mobile phone"
    }
  }
}
