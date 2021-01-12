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

  mockConfig.features.vatCertNSTPs(true)

  val vatCertificateView: VatCertificate = injector.instanceOf[VatCertificate]

  object Selectors {
    val heading = "h1"
    val cardClass = ".card-full-container"
    val printButton = "button"
    val fullNameSelector = "#content > article > div:nth-child(5) > div.column-full.card-full > dl:nth-child(1) > div > dd"
    val backLink = ".link-back"
  }

  lazy val model: VatCertificateViewModel = VatCertificateViewModel(
    "5555555555",
    Some(LocalDate.parse("2017-01-01")),
    LocalDate.parse("2018-01-01"),
    None,
    Some("ABC Traders"),
    "partyType.11",
    "6602",
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
        document.title shouldBe "Your VAT Certificate - Business tax account - GOV.UK"
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
        lazy val card = document.select(Selectors.cardClass).first()
        "contains the correct heading" in {
          card.select("h2").text() shouldBe "About your registration"
        }
        "contains the VRN" in {
          val vrnRow = card.select(".govuk-check-your-answers:nth-of-type(1)")
          vrnRow.select("dt").text() shouldBe "VAT registration number (VRN)"
          vrnRow.select("dd").text() shouldBe "5555555555"
        }
        "contains the registration date" in {
          val regDateRow = card.select(".govuk-check-your-answers:nth-of-type(2)")
          regDateRow.select("dt").text() shouldBe "Registration date"
          regDateRow.select("dd").text() shouldBe s"1 January 2017"
        }
        "contains the certificate date" in {
          val certDateRow = card.select(".govuk-check-your-answers:nth-of-type(3)")
          certDateRow.select("dt").text() shouldBe "Certificate date"
          certDateRow.select("dd").text() shouldBe s"1 January 2018"
        }
      }

      "have the about the business card" that {
        lazy val card = document.select(Selectors.cardClass).get(1)
        "contains the correct heading" in {
          card.select("h2").text() shouldBe "About the business"
        }
        "contains the business name" in {
          val vrnRow = card.select(".govuk-check-your-answers:nth-of-type(1)")
          vrnRow.select("dt").text() shouldBe "Business name"
          vrnRow.select("dd").text() shouldBe "Not provided"
        }
        "contains the trading name" in {
          val regDateRow = card.select(".govuk-check-your-answers:nth-of-type(2)")
          regDateRow.select("dt").text() shouldBe "Trading name"
          regDateRow.select("dd").text() shouldBe s"ABC Traders"
        }
        "contains the business type" in {
          val certDateRow = card.select(".govuk-check-your-answers:nth-of-type(3)")
          certDateRow.select("dt").text() shouldBe "Business type"
          certDateRow.select("dd").text() shouldBe s"Organisation"
        }
        "contains the trade classification" in {
          val certDateRow = card.select(".govuk-check-your-answers:nth-of-type(4)")
          certDateRow.select("dt").text() shouldBe "Trade classification (SIC code)"
          certDateRow.select("dd").text() shouldBe s"6602"
        }
        "contains the address" in {
          val certDateRow = card.select(".govuk-check-your-answers:nth-of-type(5)")
          certDateRow.select("dt").text() shouldBe "Principal place of business address"
          certDateRow.select("dd").text() shouldBe s"Line 1 Line 2 TF4 3ER"
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
          val row = card.select(".govuk-check-your-answers:nth-of-type(1)")
          row.select("dt").text() shouldBe "VAT Return dates"
          row.select("dd").text() shouldBe "Every month"
        }
      }

      "not have the non-standard tax periods card" in {
        document.select(Selectors.cardClass).contains("Non-standard tax periods") shouldBe false
      }
    }

    "accessed by a user with non-standard tax periods" when {

      "the vatCertNSTPs feature is on" should {

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

      "the vatCertNSTPs feature is off" should {

        "have the return details card" in {
          mockConfig.features.vatCertNSTPs(false)
          lazy val view = vatCertificateView(
            HtmlFormat.empty, modelWithNSTP)(messages, mockConfig, request, user)
          lazy implicit val document: Document = Jsoup.parse(view.body)
          elementText("#content > article > div:nth-child(6) > " +
            "div.column-two-thirds > h2") shouldBe "Return details"
        }

        "not have the non-standard return details card" in {
          mockConfig.features.vatCertNSTPs(false)
          lazy val view = vatCertificateView(
            HtmlFormat.empty, modelWithNSTP)(messages, mockConfig, request, user)
          lazy implicit val document: Document = Jsoup.parse(view.body)
          document.select(Selectors.cardClass).contains("Non-standard tax periods") shouldBe false
        }
      }
    }

    "accessed by a sole trader" should {
      lazy val view = vatCertificateView(HtmlFormat.empty, soleTrader)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct title" in {
        document.title shouldBe "Your VAT Certificate - Business tax account - GOV.UK"
      }

      "have the correct heading" in {
        elementText(Selectors.heading) shouldBe "Your VAT Certificate"
      }

      "display the traders full name" in {
        elementText(Selectors.fullNameSelector) shouldBe "Sole Person"
      }

      "display the traders trading name" in {
        document.body().toString should include("Trading name")
      }

      "not display the business name" in {
        document.body().toString shouldNot include("Business name")
      }

    }

    "accessed by an individual" should {
      lazy val view = vatCertificateView(HtmlFormat.empty, individual)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct title" in {
        document.title shouldBe "Your VAT Certificate - Business tax account - GOV.UK"
      }

      "have the correct heading" in {
        elementText(Selectors.heading) shouldBe "Your VAT Certificate"
      }

      "display the traders full name" in {
        elementText(Selectors.fullNameSelector) shouldBe "Andy Vidual"
      }

      "display the traders trading name" in {
        document.body().toString should include("Trading name")
      }

      "not display the business name" in {
        document.body().toString shouldNot include("Business name")
      }
    }

    "accessed by a sole trader without a trading name" should {
      lazy val view = vatCertificateView(HtmlFormat.empty, soleTraderWithoutTradingName)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct title" in {
        document.title shouldBe "Your VAT Certificate - Business tax account - GOV.UK"
      }

      "have the correct heading" in {
        elementText(Selectors.heading) shouldBe "Your VAT Certificate"
      }

      "display the traders full name" in {
        elementText(Selectors.fullNameSelector) shouldBe "Sole Person"
      }

      "not display the traders trading name" in {
        document.body().toString shouldNot include("Trading name")
      }

      "not display the business name" in {
        document.body().toString shouldNot include("Business name")
      }
    }
  }
}
