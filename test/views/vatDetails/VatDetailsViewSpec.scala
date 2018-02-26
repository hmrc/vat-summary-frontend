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

package views.vatDetails

import java.time.LocalDate
import models.viewModels.VatDetailsViewModel
import models.User
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class VatDetailsViewSpec extends ViewBaseSpec {

  object Selectors {
    val pageHeading = "h1"
    val entityNameHeading = "h1 span"
    val nextPayment = "#payments h2"
    val nextReturn = "#next-return h2"
    val header = "div.test"
    val accountDetails = "#account-details"
    val submittedReturns = "#submitted-returns"
    val vatRegNo = ".form-hint"
    val btaBreadcrumb = "div.breadcrumbs li:nth-of-type(1)"
    val btaBreadcrumbLink = "div.breadcrumbs li:nth-of-type(1) a"
    val vatBreadcrumb = "div.breadcrumbs li:nth-of-type(2)"
    val overdueLabel = "span strong"
  }

  private val user = User("123456789")
  val detailsModel = VatDetailsViewModel(
    Some(LocalDate.now()),
    Some(LocalDate.now()),
    Some("Cheapo Clothing")
  )
  val overdueDetailsModel = VatDetailsViewModel(
    Some(LocalDate.now()),
    Some(LocalDate.parse("2017-01-01")),
    Some("Cheapo Clothing"),
    isOverdue = true
  )

  "Rendering the VAT details page" should {

    lazy val view = views.html.vatDetails.details(user, detailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render breadcrumbs which" should {

      "have the text 'Business tax account'" in {
        elementText(Selectors.btaBreadcrumb) shouldBe "Business tax account"
      }

      "links to bta" in {
        element(Selectors.btaBreadcrumbLink).attr("href") shouldBe "bta-url"
      }

      "have the text 'VAT'" in {
        elementText(Selectors.vatBreadcrumb) shouldBe "VAT"
      }
    }

    "have the correct document title" in {
      document.title shouldBe "VAT"
    }

    "have the correct entity name" in {
      elementText(Selectors.entityNameHeading) shouldBe detailsModel.entityName.getOrElse("Fail")
    }

    "have the correct VRN message" in {
      elementText(Selectors.vatRegNo) shouldBe s"VAT registration number (VRN): ${user.vrn}"
    }

    "have the account details section" should {

      lazy val accountDetails = element(Selectors.accountDetails)

      "have the heading" in {
        accountDetails.select("h3").text() shouldBe "Account details"
      }

      s"have a link to ${controllers.routes.AccountDetailsController.accountDetails().url}" in {
        accountDetails.select("a").attr("href") shouldBe controllers.routes.AccountDetailsController.accountDetails().url
      }

      "have the text" in {
        accountDetails.select("p").text() shouldBe "See your contact information and other details."
      }
    }

    "have the submitted returns section" should {

      lazy val submittedReturns = element(Selectors.submittedReturns)

      "have the heading" in {
        submittedReturns.select("h3").text() shouldBe "Submitted returns"
      }

      s"have a link to 'returns-url/${LocalDate.now().getYear}'" in {
        submittedReturns.select("a").attr("href") shouldBe s"returns-url/${LocalDate.now().getYear}"
      }

      "have the text" in {
        submittedReturns.select("p").text() shouldBe "Check the returns you've sent us."
      }
    }
  }

  "Rendering the VAT details page with a next return and a next payment" should {

    lazy val view = views.html.vatDetails.details(user, detailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the next return section" in {
      elementText(Selectors.nextReturn) shouldBe "Next return due"
    }

    "render the next payment section" in {
      elementText(Selectors.nextPayment) shouldBe "Next payment due"
    }
  }

  "Rendering the VAT details page without a next return or next payment" should {

    lazy val view = views.html.vatDetails.details(user, VatDetailsViewModel(None, None, None))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the no return message" in {
      elementText(Selectors.nextReturn) shouldBe "No return due"
    }

    "render the no payment message" in {
      elementText(Selectors.nextPayment) shouldBe "No payment due"
    }
  }

  "Rendering the VAT details page with an overdue return" should {

    lazy val view = views.html.vatDetails.details(user, overdueDetailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the overdue label" in {
      elementText(Selectors.overdueLabel) shouldBe "overdue"
    }
  }
}
