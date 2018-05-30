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

import models.User
import models.viewModels.VatDetailsViewModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.BeforeAndAfterEach
import views.ViewBaseSpec

class VatDetailsViewSpec extends ViewBaseSpec with BeforeAndAfterEach {

  object Selectors {
    val pageHeading = "h1"
    val header = "#content > article > div.grid-row.form-group > div > header"
    val entityNameHeading = "header > p"
    val nextPaymentHeading = "#payments h2"
    val nextPayment = "#payments p"
    val nextReturnHeading = "#next-return h2"
    val nextReturn = "#next-return p"
    val accountDetails = "#account-details"
    val submittedReturns = "#submitted-returns"
    val vatRegNo = ".form-hint"
    val btaBreadcrumb = "div.breadcrumbs li:nth-of-type(1)"
    val btaBreadcrumbLink = "div.breadcrumbs li:nth-of-type(1) a"
    val vatBreadcrumb = "div.breadcrumbs li:nth-of-type(2)"
    val overdueLabel = "span strong"
    val returnsVatLink = "#vat-returns-link"
    val paymentHistory = "#payment-history"
  }

  val currentYear: Int = 2018
  private val user = User("123456789")
  val detailsModel = VatDetailsViewModel(
    Some(LocalDate.parse("2018-12-31")),
    Some(LocalDate.parse("2018-12-31")),
    Some("Cheapo Clothing"),
    currentYear
  )
  val overdueReturnDetailsModel = VatDetailsViewModel(
    Some(LocalDate.parse("2017-01-01")),
    Some(LocalDate.parse("2017-01-01")),
    Some("Cheapo Clothing"),
    currentYear,
    returnObligationOverdue = true
  )
  val overduePaymentDetailsModel = VatDetailsViewModel(
    Some(LocalDate.parse("2017-01-01")),
    Some(LocalDate.parse("2018-12-31")),
    Some("Cheapo Clothing"),
    currentYear,
    paymentOverdue = true
  )
  val paymentErrorDetailsModel = VatDetailsViewModel(
    None,
    Some(LocalDate.parse("2018-12-31")),
    Some("Cheapo Clothing"),
    currentYear,
    paymentError = true
  )
  val returnErrorDetailsModel = VatDetailsViewModel(
    Some(LocalDate.parse("2018-12-31")),
    None,
    Some("Cheapo Clothing"),
    currentYear,
    returnObligationError = true
  )
  val bothErrorDetailsModel = VatDetailsViewModel(
    None,
    None,
    None,
    currentYear,
    paymentError = true,
    returnObligationError = true
  )

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    mockConfig.features.accountDetails(true)
    mockConfig.features.allowPaymentHistory(true)
  }

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
        elementText(Selectors.vatBreadcrumb) shouldBe "Your VAT details"
      }
    }

    "have the correct document title" in {
      document.title shouldBe "Your VAT details"
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
        accountDetails.select("h2").text() shouldBe "Account details"
      }

      s"have a link to ${controllers.routes.AccountDetailsController.accountDetails().url}" in {
        accountDetails.select("a").attr("href") shouldBe controllers.routes.AccountDetailsController.accountDetails().url
      }

      "have the text" in {
        accountDetails.select("p").text() shouldBe "See your business information and other details."
      }
    }

    "have the submitted returns section" should {

      lazy val submittedReturns = element(Selectors.submittedReturns)

      "have the heading" in {
        submittedReturns.select("h2").text() shouldBe "Submitted returns"
      }

      s"have a link to 'returns-url/$currentYear'" in {
        submittedReturns.select("a").attr("href") shouldBe s"returns-url/$currentYear"
      }

      "have the text" in {
        submittedReturns.select("p").text() shouldBe "Check the returns you've sent us."
      }
    }

    "have the payment history section" should {

      lazy val submittedReturns = element(Selectors.paymentHistory)

      "have the heading" in {
        submittedReturns.select("h2").text() shouldBe "Payment history"
      }

      s"have a link to the payment history page" ignore {
        //TODO: Revisit when page has been created
        submittedReturns.select("a").attr("href") shouldBe s""
      }

      "have the text" in {
        submittedReturns.select("p").text() shouldBe "Check the payments you've made."
      }
    }
  }

  "Rendering the VAT details page without the payment history section" should {

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
        elementText(Selectors.vatBreadcrumb) shouldBe "Your VAT details"
      }
    }

    "have the correct document title" in {
      document.title shouldBe "Your VAT details"
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
        accountDetails.select("h2").text() shouldBe "Account details"
      }

      s"have a link to ${controllers.routes.AccountDetailsController.accountDetails().url}" in {
        accountDetails.select("a").attr("href") shouldBe controllers.routes.AccountDetailsController.accountDetails().url
      }

      "have the text" in {
        accountDetails.select("p").text() shouldBe "See your business information and other details."
      }
    }

    "have the submitted returns section" should {

      lazy val submittedReturns = element(Selectors.submittedReturns)

      "have the heading" in {
        submittedReturns.select("h2").text() shouldBe "Submitted returns"
      }

      s"have a link to 'returns-url/$currentYear'" in {
        submittedReturns.select("a").attr("href") shouldBe s"returns-url/$currentYear"
      }

      "have the text" in {
        submittedReturns.select("p").text() shouldBe "Check the returns you've sent us."
      }
    }

    "the payment history section" should {
      "not be rendered" in {
        mockConfig.features.allowPaymentHistory(false)

        lazy val view = views.html.vatDetails.details(user, detailsModel)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        intercept[org.scalatest.exceptions.TestFailedException](element(Selectors.paymentHistory))
      }
    }
  }

  "Rendering the VAT details page with a next return and a next payment" should {

    lazy val view = views.html.vatDetails.details(user, detailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the next return section heading" in {
      elementText(Selectors.nextReturnHeading) shouldBe "Next return due"
    }

    "render the next return section" in {
      elementText(Selectors.nextReturn) shouldBe "31 December 2018"
    }

    "render the next payment section heading" in {
      elementText(Selectors.nextPaymentHeading) shouldBe "Next payment due"
    }

    "render the next payment section" in {
      elementText(Selectors.nextPayment) shouldBe "31 December 2018"
    }

    "render the next payment section vat returns link" in {
      elementText(Selectors.returnsVatLink) shouldBe "View return deadlines"
    }

    "have the correct next payment section vat returns link href" in {
      element(Selectors.returnsVatLink).attr("href") shouldBe mockConfig.vatReturnDeadlinesUrl
    }

  }

  "Rendering the VAT details page without a next return or next payment" should {

    lazy val view = views.html.vatDetails.details(user, VatDetailsViewModel(None, None, None, currentYear))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the next return section heading" in {
      elementText(Selectors.nextReturnHeading) shouldBe "Next return due"
    }

    "render the no return message" in {
      elementText(Selectors.nextReturn) shouldBe "No returns due right now"
    }

    "render the next payment section heading" in {
      elementText(Selectors.nextPaymentHeading) shouldBe "Next payment due"
    }

    "render the next payment section" in {
      elementText(Selectors.nextPayment) shouldBe "No payments due right now"
    }

    "render the next payment section vat returns link" in {
      elementText(Selectors.returnsVatLink) shouldBe "View return deadlines"
    }

    "have the correct next payment section vat returns link href" in {
      element(Selectors.returnsVatLink).attr("href") shouldBe mockConfig.vatReturnDeadlinesUrl
    }
  }

  "Rendering the VAT details page with an overdue return" should {

    lazy val view = views.html.vatDetails.details(user, overdueReturnDetailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the overdue label" in {
      elementText(Selectors.overdueLabel) shouldBe "overdue"
    }
  }

  "Rendering the VAT details page with an overdue payment" should {

    lazy val view = views.html.vatDetails.details(user, overduePaymentDetailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the overdue label" in {
      elementText(Selectors.overdueLabel) shouldBe "overdue"
    }
  }

  "Rendering the VAT details page with a payment error" should {

    lazy val view = views.html.vatDetails.details(user, paymentErrorDetailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the next return section heading" in {
      elementText(Selectors.nextReturnHeading) shouldBe "Next return due"
    }

    "render the next return section" in {
      elementText(Selectors.nextReturn) shouldBe "31 December 2018"
    }

    "render the next payment section heading" in {
      elementText(Selectors.nextPaymentHeading) shouldBe "Next payment due"
    }

    "render the next payment section" in {
      elementText(Selectors.nextPayment) shouldBe "Sorry, there is a problem with the service. Try again later."
    }

    "render the next payment section vat returns link" in {
      elementText(Selectors.returnsVatLink) shouldBe "View return deadlines"
    }

    "have the correct next payment section vat returns link href" in {
      element(Selectors.returnsVatLink).attr("href") shouldBe mockConfig.vatReturnDeadlinesUrl
    }
  }

  "Rendering the VAT details page with a return error" should {

    lazy val view = views.html.vatDetails.details(user, returnErrorDetailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the next return section heading" in {
      elementText(Selectors.nextReturnHeading) shouldBe "Next return due"
    }

    "render the next return section" in {
      elementText(Selectors.nextReturn) shouldBe "Sorry, there is a problem with the service. Try again later."
    }

    "render the next payment section heading" in {
      elementText(Selectors.nextPaymentHeading) shouldBe "Next payment due"
    }

    "render the next payment section" in {
      elementText(Selectors.nextPayment) shouldBe "31 December 2018"
    }

    "render the next payment section vat returns link" in {
      elementText(Selectors.returnsVatLink) shouldBe "View return deadlines"
    }

    "have the correct next payment section vat returns link href" in {
      element(Selectors.returnsVatLink).attr("href") shouldBe mockConfig.vatReturnDeadlinesUrl
    }
  }

  "Rendering the VAT details page with errors in all APIs" should {

    lazy val view = views.html.vatDetails.details(user, bothErrorDetailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the next return section heading" in {
      elementText(Selectors.nextReturnHeading) shouldBe "Next return due"
    }

    "render the next return section" in {
      elementText(Selectors.nextReturn) shouldBe "Sorry, there is a problem with the service. Try again later."
    }

    "render the next payment section heading" in {
      elementText(Selectors.nextPaymentHeading) shouldBe "Next payment due"
    }

    "render the next payment section" in {
      elementText(Selectors.nextPayment) shouldBe "Sorry, there is a problem with the service. Try again later."
    }

    "render the next payment section vat returns link" in {
      elementText(Selectors.returnsVatLink) shouldBe "View return deadlines"
    }

    "have the correct next payment section vat returns link href" in {
      element(Selectors.returnsVatLink).attr("href") shouldBe mockConfig.vatReturnDeadlinesUrl
    }

    "have the correct GA tag regarding the entity name graceful error handling" in {
      element(Selectors.header).attr("data-metrics") shouldBe "error:hidden-text:vat-overview-entity-name"
    }
  }
}
