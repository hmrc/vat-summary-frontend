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

package views.vatDetails

import models.User
import models.viewModels.VatDetailsViewModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.exceptions.TestFailedException
import play.twirl.api.Html
import views.ViewBaseSpec
import common.TestModels.testDate

class VatDetailsViewSpec extends ViewBaseSpec {

  object Selectors {
    val pageHeading = "h1"
    val header = ".page-heading"
    val entityNameHeading = ".form-hint > p:nth-child(2)"
    val nextPaymentHeading = "#payments h2"
    val nextPayment = "#payments p"
    val nextReturnHeading = "#next-return h2"
    val nextReturn = "#next-return p"
    val paymentsAndRepaymentsSection = "#payments-and-repayments"
    val mtdSignupSection = "#mtd-signup"
    val vatCertificate = "#vat-certificate"
    val updateVatDetails = "#update-vat-details"
    val submittedReturns = "#submitted-returns"
    val vatRegNo = ".form-hint"
    val btaBreadcrumb = "div.breadcrumbs li:nth-of-type(1)"
    val btaBreadcrumbLink = "div.breadcrumbs li:nth-of-type(1) a"
    val vatBreadcrumb = "div.breadcrumbs li:nth-of-type(2)"
    val overdueLabel = "span strong"
    val returnsVatLink = "#vat-returns-link"
    val historyHeading = "#history > h2"
    val historyPastPayments = "ul.list > li:nth-child(1) > a:nth-child(1)"
    val historyPastReturns = "ul.list > li:nth-child(2) > a:nth-child(1)"
    val serviceInfoNav = ".service-info nav"
    val apiError = "h3:nth-child(2).heading-medium"
    val vatOptOutLink = "#vat-optout"
    val deregHeading = "#cancel-vat"
    val covidPartial = ".warning-banner"
  }

  override implicit val user: User = User("123456789")
  val detailsModel = VatDetailsViewModel(
    Some("2018-12-31"),
    Some("2018-12-31"),
    Some("Cheapo Clothing"),
    currentDate = testDate,
    partyType = Some("1")
  )
  val nonMtdDetailsModel = VatDetailsViewModel(
    None,
    None,
    None,
    isNonMTDfBOrNonDigitalUser = Some(true),
    currentDate = testDate,
    partyType = Some("1")
  )
  val hybridDetailsModel = VatDetailsViewModel(
    Some("2018-12-31"),
    Some("2018-12-31"),
    Some("Cheapo Clothing"),
    isHybridUser = true,
    currentDate = testDate,
    partyType = Some("1")
  )
  val overdueReturnDetailsModel = VatDetailsViewModel(
    Some("2017-01-01"),
    Some("2017-01-01"),
    Some("Cheapo Clothing"),
    returnObligationOverdue = true,
    currentDate = testDate,
    partyType = Some("1")
  )
  val multipleReturnsDetailsModel = VatDetailsViewModel(
    Some("2017-01-01"),
    Some("2"),
    Some("Cheapo Clothing"),
    hasMultipleReturnObligations = true,
    currentDate = testDate,
    partyType = Some("1")
  )
  val paymentErrorDetailsModel = VatDetailsViewModel(
    None,
    Some("2018-12-31"),
    Some("Cheapo Clothing"),
    paymentError = true,
    currentDate = testDate,
    partyType = Some("1")
  )
  val returnErrorDetailsModel = VatDetailsViewModel(
    Some("2018-12-31"),
    None,
    Some("Cheapo Clothing"),
    returnObligationError = true,
    currentDate = testDate,
    partyType = Some("1")
  )
  val bothErrorDetailsModel = VatDetailsViewModel(
    None,
    None,
    None,
    paymentError = true,
    returnObligationError = true,
    isNonMTDfBUser = None,
    isNonMTDfBOrNonDigitalUser = None,
    customerInfoError = true,
    currentDate = testDate,
    partyType = Some("1")
  )

  "Rendering the VAT details page for an mtd user" should {

    lazy val view = views.html.vatDetails.details(detailsModel, Html("<nav>BTA Links</nav>"))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render service information content" in {
      elementText(Selectors.serviceInfoNav) shouldBe "BTA Links"
    }

    "render breadcrumbs which" should {

      "have the text 'Business tax account'" in {
        elementText(Selectors.btaBreadcrumb) shouldBe "Business tax account"
      }

      "links to bta" in {
        element(Selectors.btaBreadcrumbLink).attr("href") shouldBe "bta-url"
      }

      "have the text 'VAT'" in {
        elementText(Selectors.vatBreadcrumb) shouldBe "Your VAT account"
      }
    }

    "have the correct document title" in {
      document.title shouldBe "Your VAT account - Business tax account - GOV.UK"
    }

    "have the correct entity name" in {
      elementText(Selectors.entityNameHeading) shouldBe detailsModel.entityName.getOrElse("Fail")
    }

    "have the correct VRN message" in {
      elementText(Selectors.vatRegNo) should include(s"VAT registration number (VRN): ${user.vrn}")
    }

    "have the history section" when {

      "the user is NOT Hybrid" should {

        "have the heading" in {
          elementText(Selectors.historyHeading) shouldBe "History"
        }

        lazy val view = views.html.vatDetails.details(detailsModel)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the payment history text" in {
          elementText(Selectors.historyPastPayments) shouldBe "View past payments"
        }

        "have a link to the payment history" in {
          element(Selectors.historyPastPayments).attr("href") shouldBe controllers.routes.PaymentHistoryController.paymentHistory().url
        }

        "have the past returns text" in {
          elementText(Selectors.historyPastReturns) shouldBe "View past returns"
        }

        "have a link to the Submitted returns" in {
          element(Selectors.historyPastReturns).attr("href") shouldBe "returns-url"
        }
      }

      "the user is Hybrid" should {

        lazy val view = views.html.vatDetails.details(hybridDetailsModel)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the past payments text" in {
          elementText(Selectors.historyPastPayments) shouldBe "View past payments (opens in a new tab)"
        }

        "have a link to the portal via the PortalController" in {
          element(Selectors.historyPastPayments).attr("href") shouldBe "/vat-through-software/portal-payment-history"
        }

      }
    }

    "have the payments and repayments section" which {

      lazy val paymentsAndRepaymentsSection = element(Selectors.paymentsAndRepaymentsSection)

      "has the correct heading" in {
        paymentsAndRepaymentsSection.select("h3").text() shouldBe "Payments and repayments"
      }

      "has a link to the vat-repayment-tracker service" in {
        paymentsAndRepaymentsSection.select("h3 a").attr("href") shouldBe s"/vat-repayment-tracker" +
          s"/manage-or-track-vrt"
      }

      "has the correct paragraph" in {
        paymentsAndRepaymentsSection.select("p").text() shouldBe "Manage your Direct Debit, repayment bank account " +
          "details and track what HMRC owe you."
      }
    }

    "have the vat certificate section" which {

      lazy val vatCertificate = element(Selectors.vatCertificate)

      "has the correct heading" in {
        vatCertificate.select("h3").text() shouldBe "View VAT certificate"
      }

      "has the correct paragraph" in {
        vatCertificate.select("p").text() shouldBe "View and print your VAT certificate."
      }
    }

    "have the opt out section" which {

      lazy val optOutLink = element(Selectors.vatOptOutLink)

      "has the correct heading" in {
        optOutLink.select("h3").text() shouldBe "Opt out of Making Tax Digital for VAT"
      }

      "has the correct paragraph" in {
        optOutLink.select("p").text() shouldBe "You cannot opt out if your taxable turnover has been above £85,000 since 1 April 2019."
      }
    }

    "have the update your VAT details section" which {

      lazy val updateVatDetails = element(Selectors.updateVatDetails)

      "has the correct heading" in {
        updateVatDetails.select("h3").text() shouldBe "Your business details"
      }

      "has the correct paragraph" in {
        updateVatDetails.select("p").text() shouldBe "Change your business details or VAT Return dates."
      }
    }

    "have the deregister for VAT section" which {

      lazy val deregSection = element(Selectors.deregHeading)

      "has the correct heading" in {
        deregSection.select("h3").text() shouldBe "Cancel VAT registration"
      }

      "has the correct paragraph" in {
        deregSection.select("p").text() shouldBe
          "Cancel your VAT registration if you’re closing the business, transferring ownership or do not need to be VAT registered."
      }
    }

    "not have the mtd signup section" in {
      elementExtinct(Selectors.mtdSignupSection)
    }
  }

  "Rendering the VAT details page for a non mtd user" should {

    lazy val view = views.html.vatDetails.details(nonMtdDetailsModel, Html("<nav>BTA Links</nav>"))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the mtd sign up section" which {

      lazy val mtdSignupSection = element(Selectors.mtdSignupSection)

      "has the correct heading" in {
        mtdSignupSection.select("h3").text() shouldBe "Sign up for Making Tax Digital for VAT"
      }

      "has a link to the vat-sign-up service" in {
        mtdSignupSection.select("h3 a").attr("href") shouldBe s"/vat-through-software/sign-up/vat-number/${user.vrn}"
      }

      "has the correct paragraph" in {
        mtdSignupSection.select("p").text() shouldBe "If your taxable turnover exceeds the VAT threshold, " +
          "you must sign up to Making Tax Digital for VAT."
      }
    }

    "not have the Opt out section" in {
      elementExtinct(Selectors.vatOptOutLink)
    }
  }

  "Rendering the VAT details page for a hybrid user" should {

    "not display the payments and repayments section" in {
      lazy val view = views.html.vatDetails.details(detailsModel.copy(isHybridUser = true))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      elementExtinct(Selectors.paymentsAndRepaymentsSection)
    }
  }

  "Rendering the VAT details page" when {

    "the paymentsAndRepayments feature switch is false" should {

      "not display the payments and repayments section" in {
        mockConfig.features.paymentsAndRepaymentsEnabled(false)
        lazy val view = views.html.vatDetails.details(detailsModel)
        lazy implicit val document: Document = Jsoup.parse(view.body)
        elementExtinct(Selectors.paymentsAndRepaymentsSection)
      }
    }

    "the optOut feature switch is false" should {

      "not display the opt out section" in {
        mockConfig.features.vatOptOutEnabled(false)
        lazy val view = views.html.vatDetails.details(detailsModel)
        lazy implicit val document: Document = Jsoup.parse(view.body)
        elementExtinct(Selectors.vatOptOutLink)
      }
    }

    "the mtdSignup feature switch is false" should {

      "not display the mtd signup section" in {
        mockConfig.features.mtdSignUp(false)
        lazy val view = views.html.vatDetails.details(detailsModel)
        lazy implicit val document: Document = Jsoup.parse(view.body)
        elementExtinct(Selectors.mtdSignupSection)
      }
    }
  }

  "Rendering the VAT details page with a next return and a next payment" should {

    lazy val view = views.html.vatDetails.details(detailsModel, Html(""))
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

    lazy val view = views.html.vatDetails.details(VatDetailsViewModel(None, None, None, customerInfoError = true, currentDate = testDate, partyType = Some("1")))
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

    lazy val view = views.html.vatDetails.details(overdueReturnDetailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the overdue label" in {
      elementText(Selectors.overdueLabel) shouldBe "overdue"
    }
  }

  "Rendering the VAT details page with multiple return obligations" should {

    lazy val view = views.html.vatDetails.details(multipleReturnsDetailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the correct message regarding the number of obligations due" in {
      elementText(Selectors.nextReturn) shouldBe "You have 2 returns due"
    }
  }

  "Rendering the VAT details page with a payment error" should {

    lazy val view = views.html.vatDetails.details(paymentErrorDetailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the next payment section heading" in {
      elementText(Selectors.nextPaymentHeading) shouldBe "Next payment due"
    }

    "render the next payment section" in {
      elementText(Selectors.nextPayment) shouldBe "Sorry, there is a problem with the service. Try again later."
    }
  }

  "Rendering the VAT details page with a return error" should {

    lazy val view = views.html.vatDetails.details(returnErrorDetailsModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the next return section heading" in {
      elementText(Selectors.nextReturnHeading) shouldBe "Next return due"
    }

    "render the next return section" in {
      elementText(Selectors.nextReturn) shouldBe "Sorry, there is a problem with the service. Try again later."
    }

    "render the next payment section vat returns link" in {
      elementText(Selectors.returnsVatLink) shouldBe "View return deadlines"
    }
  }

  "Rendering the VAT details page with errors in all APIs" should {

    lazy val view = views.html.vatDetails.details(bothErrorDetailsModel)
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

    "not render the next payment section vat returns link" in {
      intercept[TestFailedException](elementText(selector = Selectors.returnsVatLink)
      )
    }

    "render Api graceful error message in Opt out section" in {
      elementText(Selectors.apiError) shouldBe "Sorry, there is a problem with the service. Try again later."
    }

    "not display the payments and repayments section" in {
      elementExtinct(Selectors.paymentsAndRepaymentsSection)
    }

  }

  "Rendering the overview page" should {

    "display the covid message" when {

      "the display covid feature switch is on" in {
        mockConfig.features.displayCovidMessage(true)

        lazy val view = views.html.vatDetails.details(detailsModel, Html("<nav>BTA Links</nav>"))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        document.select(Selectors.covidPartial).toString should include("Coronavirus (COVID 19) VAT deferral")
      }

    }

    "not display the covid message" when {

      "the display covid feature switch is off" in {
        mockConfig.features.displayCovidMessage(false)

        lazy val view = views.html.vatDetails.details(detailsModel, Html("<nav>BTA Links</nav>"))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        elementExtinct(Selectors.covidPartial)
      }

    }

  }

}
