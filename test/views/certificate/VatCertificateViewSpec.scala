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

package views.certificate

import java.time.LocalDate

import common.TestModels.{exampleNonNSTP, exampleNonStandardTaxPeriods}
import models.Address
import models.viewModels.VatCertificateViewModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import views.ViewBaseSpec
import views.html.certificate.VatCertificate

class VatCertificateViewSpec extends ViewBaseSpec {

  val vatCertificateView: VatCertificate = injector.instanceOf[VatCertificate]

  object Selectors {
    val heading = "h1"
    val cardClass = ".card-full-container"
    val aboutYourRegHeading = ".govuk-grid-column-two-thirds > h2"
    val vrnRow = "#content > div:nth-child(3) > .govuk-grid-column-full > dl:nth-child(1) > div"
    val vrnTitle = s"$vrnRow > dt"
    val vrn = s"$vrnRow > dd"
    val regDateRow = "#content > div:nth-child(3) > .govuk-grid-column-full dl:nth-of-type(2) > div"
    val regDateTitle = s"$regDateRow > dt"
    val regDate = s"$regDateRow > dd"
    val certDateRow = "#content > div:nth-child(3) > .govuk-grid-column-full dl:nth-of-type(3) > div"
    val certDateTitle = s"$certDateRow > dt"
    val certDate = s"$certDateRow > dd"
    val aboutTheBusinessHeading = "#content > div:nth-child(4) > div.govuk-grid-column-two-thirds > h2"
    val businessNameRow = "#content > div:nth-child(4) > .govuk-grid-column-full > dl:nth-child(1) > div"
    val businessNameTitle = s"$businessNameRow > dt"
    val businessName = s"$businessNameRow > dd"
    val tradingNameRow = "#content > div:nth-child(4) > div.govuk-grid-column-full.card-full > dl:nth-child(2) > div"
    val tradingNameTitle = s"$tradingNameRow > dt"
    val tradingName = s"$tradingNameRow > dd"
    val businessTypeRow = "#content > div:nth-child(4) > div.govuk-grid-column-full.card-full > dl:nth-child(3) > div"
    val businessTypeTitle = s"$businessTypeRow > dt"
    val businessType = s"$businessTypeRow > dd"
    val tradeClassificationRow = "#content > div:nth-child(4) > div.govuk-grid-column-full.card-full > dl:nth-child(4) > div"
    val tradeClassificationTitle = s"$tradeClassificationRow > dt"
    val tradeClassification = s"$tradeClassificationRow > dd"
    val ppobRow = "#content > div:nth-child(4) > div.govuk-grid-column-full.card-full > dl:nth-child(5) > div"
    val ppobRowTitle = s"$ppobRow > dt"
    val ppob = s"$ppobRow > dd"
    val printButton = ".govuk-button"
    val fullNameSelector = "#content > div:nth-child(4) > .govuk-grid-column-full > dl:nth-child(1) > div > dd"
    val backLink = ".govuk-back-link"
  }

  lazy val model: VatCertificateViewModel = VatCertificateViewModel(
    "5555555555",
    Some(LocalDate.parse("2017-01-01")),
    LocalDate.parse("2018-01-01"),
    None,
    Some("ABC Traders"),
    "partyType.11",
    Some("6602"),
    Address("Line 1", Some("Line 2"), None, None, Some("TF4 3ER")),
    "returnPeriod.MM",
    None,
    None,
    None
  )

  lazy val modelWithNSTP: VatCertificateViewModel = model.copy(
    nonStdTaxPeriods = Some(exampleNonStandardTaxPeriods),
    firstNonNSTPPeriod = exampleNonNSTP
  )

  lazy val soleTrader: VatCertificateViewModel = model.copy(
    businessName = Some("ABC Business"),
    businessTypeMsgKey = "partyType.1",
    fullName = Some("Sole Person")
  )

  lazy val individual: VatCertificateViewModel = model.copy(
    businessName = Some("ABC Business"),
    businessTypeMsgKey = "partyType.Z1",
    fullName = Some("Andy Vidual")
  )

  lazy val soleTraderWithoutTradingName: VatCertificateViewModel = soleTrader.copy(tradingName = None)

  "The VAT Certificate page" when {

    "accessed by a principal user" should {
      lazy val view = vatCertificateView(HtmlFormat.empty, model)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct title" in {
        document.title shouldBe "Your VAT Certificate - Manage your VAT account - GOV.UK"
      }

      "have the correct heading" in {
        elementText(Selectors.heading) shouldBe "Your VAT Certificate"
      }

      "not have a back link" in {
        elementExtinct(Selectors.backLink)
      }
    }

    "accessed by an agent user" should {
      lazy val view = vatCertificateView(HtmlFormat.empty, model)(messages, mockConfig, request, agentUser)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct title" in {
        document.title shouldBe "Your client’s VAT Certificate - Your client’s VAT details - GOV.UK"
      }

      "have the correct heading" in {
        elementText(Selectors.heading) shouldBe "Your client’s VAT Certificate"
      }

      "have a back link" which {

        "has the correct text" in {
          elementText(Selectors.backLink) shouldBe "Back"
        }

        "has the correct href" in {
          element(Selectors.backLink).attr("href") shouldBe mockConfig.agentClientLookupHubUrl
        }
      }
    }

    "accessed by a principal or agent user" should {

      lazy val view = vatCertificateView(HtmlFormat.empty, model)(messages, mockConfig, request, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the about your registration card" that {

        "contains the correct heading" in {
          elementText(Selectors.aboutYourRegHeading) shouldBe "About your registration"
        }

        "contains the VRN row" in {
          elementText(Selectors.vrnTitle) shouldBe "VAT registration number (VRN)"
          elementText(Selectors.vrn) shouldBe "5555555555"
        }

        "contains the registration date" in {
          elementText(Selectors.regDateTitle) shouldBe "Registration date"
          elementText(Selectors.regDate) shouldBe "1 January 2017"
        }

        "contains the certificate date" in {
          elementText(Selectors.certDateTitle) shouldBe "Certificate date"
          elementText(Selectors.certDate) shouldBe "1 January 2018"
        }

      }

      "have the about the business card" that {

        "contains the correct heading" in {
          elementText(Selectors.aboutTheBusinessHeading) shouldBe "About the business"
        }

        "contains the business name" in {
          elementText(Selectors.businessNameTitle) shouldBe "Business name"
          elementText(Selectors.businessName) shouldBe "Not provided"
        }

        "contains the trading name" in {
          elementText(Selectors.tradingNameTitle) shouldBe "Trading name"
          elementText(Selectors.tradingName) shouldBe "ABC Traders"
        }

        "contains the business type" in {
          elementText(Selectors.businessTypeTitle) shouldBe "Business type"
          elementText(Selectors.businessType) shouldBe "Organisation"
        }

        "contains the trade classification" in {
          elementText(Selectors.tradeClassificationTitle) shouldBe "Trade classification (SIC code)"
          elementText(Selectors.tradeClassification) shouldBe "6602"
        }

        "contains the address" in {
          elementText(Selectors.ppobRowTitle) shouldBe "Principal place of business address"
          elementText(Selectors.ppob) shouldBe "Line 1 Line 2 TF4 3ER"
        }
      }

      "have a button to print the certificate" that {

        "has the correct text" in {
          elementText(Selectors.printButton) shouldBe "Print certificate"
        }

        "evokes the javascript print event on click" in {
          element(Selectors.printButton).attr("onclick") shouldBe "javascript:window.print()"
        }
      }
    }

    "accessed by a user with standard tax periods" should {

      lazy val view = vatCertificateView(HtmlFormat.empty, model)(messages, mockConfig, request, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the return details card" which {
        lazy val card = document.select(Selectors.cardClass).get(2)
        "contains the correct heading" in {
          card.select("h2").text() shouldBe "Return details"
        }

        "contains the return frequency" in {
          document.select("#stp-dt").text() shouldBe "VAT Return dates"
          document.select("#stp-dd").text() shouldBe "Every month"
        }

      }

      "not have the non-standard tax periods card" in {
        document.select(Selectors.cardClass).contains("Non-standard tax periods") shouldBe false
      }
    }

    "accessed by a user with non-standard tax periods" when {

      lazy val view = vatCertificateView(
        HtmlFormat.empty, modelWithNSTP)(messages, mockConfig, request, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the non-standard tax periods card" which {
        lazy val card = document.select(Selectors.cardClass).get(2)
        "contains the correct heading" in {
          card.select("h2").text() shouldBe "Non-standard tax periods"
        }
      }

      "not have the return details card" in {
        document.select(Selectors.cardClass).contains("Return details") shouldBe false
      }
    }

    "accessed by a sole trader" should {
      lazy val view = vatCertificateView(HtmlFormat.empty, soleTrader)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct title" in {
        document.title shouldBe "Your VAT Certificate - Manage your VAT account - GOV.UK"
      }

      "have the correct heading" in {
        elementText(Selectors.heading) shouldBe "Your VAT Certificate"
      }

      "display the trader's full name" in {
        elementText(Selectors.fullNameSelector) shouldBe "Sole Person"
      }

      "display the trader's trading name" in {
        elementText(Selectors.tradingName) shouldBe "ABC Traders"
      }

      "not display the business name" in {
        elementText(Selectors.businessName) shouldNot include("Business name")
      }

    }

    "accessed by an individual" should {
      lazy val view = vatCertificateView(HtmlFormat.empty, individual)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct title" in {
        document.title shouldBe "Your VAT Certificate - Manage your VAT account - GOV.UK"
      }

      "have the correct heading" in {
        elementText(Selectors.heading) shouldBe "Your VAT Certificate"
      }

      "display the trader's full name" in {
        elementText(Selectors.fullNameSelector) shouldBe "Andy Vidual"
      }

      "display the trader's trading name" in {
        elementText(Selectors.tradingName) shouldBe "ABC Traders"
      }

      "not display the business name" in {
        elementText(Selectors.businessName) shouldNot include("Business name")
      }
    }

    "accessed by a sole trader without a trading name" should {
      lazy val view = vatCertificateView(HtmlFormat.empty, soleTraderWithoutTradingName)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct title" in {
        document.title shouldBe "Your VAT Certificate - Manage your VAT account - GOV.UK"
      }

      "have the correct heading" in {
        elementText(Selectors.heading) shouldBe "Your VAT Certificate"
      }

      "display the traders full name" in {
        elementText(Selectors.fullNameSelector) shouldBe "Sole Person"
      }

      "not display the traders trading name" in {
        elementText(Selectors.tradingName) shouldNot be("Trading name")
      }

      "not display the business name" in {
        elementText(Selectors.businessName) shouldNot be("Business name")
      }
    }
  }
}
