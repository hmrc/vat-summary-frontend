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

package views.certificate

import java.time.{LocalDate, Period}

import models.Address
import models.viewModels.VatCertificateViewModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.HtmlFormat
import views.ViewBaseSpec
class VatCertificateViewSpec extends ViewBaseSpec {

  object Selectors {
    val heading = "h1"
    val cardClass = ".card-full-container"
  }
  lazy val model = VatCertificateViewModel(
    "5555555555",
    Some(LocalDate.parse("2017-01-01")),
    LocalDate.parse("2018-01-01"),
    None,
    Some("ABC Traders"),
    "partyType.11",
    "6602",
    Address("Line 1", Some("Line 2"), None, None, Some("TF4 3ER")),
    Some("ABCStudios@example.com"),
    Some("333*****"),
    Some("****11"),
    "returnPeriod.MM"
  )

  "The VAT Certificate page" when {
    "Accessed by a non-agent" should {

      lazy val view = views.html.certificate.vatCertificate(HtmlFormat.empty, model, false)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct title" in {
        document.title shouldBe "Your VAT Certificate"
      }

      "have the correct heading" in {
        elementText(Selectors.heading) shouldBe "Your VAT Certificate"
      }

      "have the about your registration card" that {
        lazy val card = document.select(Selectors.cardClass).first()
        "contains the correct heading" in {
          card.select("h2").text() shouldBe "About your registration"
        }
        "contains the VRN" in {
          val vrnRow = card.select("tr:nth-of-type(1)")
          vrnRow.select("td").first().text() shouldBe "VAT registration number (VRN)"
          vrnRow.select("td").get(1).text() shouldBe "5555555555"
        }
        "contains the registration date" in {
          val regDateRow = card.select("tr:nth-of-type(2)")
          val date = LocalDate.now().minus(Period.ofDays(2))
          regDateRow.select("td").first().text() shouldBe "Registration date"
          regDateRow.select("td").get(1).text() shouldBe s"1 January 2017"
        }
        "contains the certificate date" in {
          val certDateRow = card.select("tr:nth-of-type(3)")
          val date = LocalDate.now()
          certDateRow.select("td").first().text() shouldBe "Certificate date"
          certDateRow.select("td").get(1).text() shouldBe s"1 January 2018"
        }
      }

      "have the about the business card" that {
        lazy val card = document.select(Selectors.cardClass).get(1)
        "contains the correct heading" in {
          card.select("h2").text() shouldBe "About the business"
        }
        "contains the business name" in {
          val vrnRow = card.select("tr:nth-of-type(1)")
          vrnRow.select("td").first().text() shouldBe "Business name"
          vrnRow.select("td").get(1).text() shouldBe "Not provided"
        }
        "contains the trading name" in {
          val regDateRow = card.select("tr:nth-of-type(2)")
          val date = LocalDate.now().minus(Period.ofDays(2))
          regDateRow.select("td").first().text() shouldBe "Trading name"
          regDateRow.select("td").get(1).text() shouldBe s"ABC Traders"
        }
        "contains the business type" in {
          val certDateRow = card.select("tr:nth-of-type(3)")
          val date = LocalDate.now()
          certDateRow.select("td").first().text() shouldBe "Business type"
          certDateRow.select("td").get(1).text() shouldBe s"Organisation"
        }
        "contains the trade classification" in {
          val certDateRow = card.select("tr:nth-of-type(4)")
          val date = LocalDate.now()
          certDateRow.select("td").first().text() shouldBe "Trade classification (SIC code)"
          certDateRow.select("td").get(1).text() shouldBe s"6602"
        }
        "contains the address" in {
          val certDateRow = card.select("tr:nth-of-type(5)")
          val date = LocalDate.now()
          certDateRow.select("td").first().text() shouldBe "Principal place of business address"
          certDateRow.select("td").get(1).text() shouldBe s"Line 1 Line 2 TF4 3ER"
        }
        "contains the email address" in {
          val certDateRow = card.select("tr:nth-of-type(6)")
          val date = LocalDate.now()
          certDateRow.select("td").first().text() shouldBe "Email address"
          certDateRow.select("td").get(1).text() shouldBe s"ABCStudios@example.com"
        }
      }

      "have the banking details card" that {
        lazy val card = document.select(Selectors.cardClass).get(2)
        "contains the correct heading" in {
          card.select("h2").text() shouldBe "Banking details"
        }
        "contains the business name" in {
          val vrnRow = card.select("tr:nth-of-type(1)")
          vrnRow.select("td").first().text() shouldBe "Account number"
          vrnRow.select("td").get(1).text() shouldBe "333*****"
        }
        "contains the trading name" in {
          val regDateRow = card.select("tr:nth-of-type(2)")
          val date = LocalDate.now().minus(Period.ofDays(2))
          regDateRow.select("td").first().text() shouldBe "Sort code"
          regDateRow.select("td").get(1).text() shouldBe s"****11"
        }
      }
      "have the return details card" that {
        lazy val card = document.select(Selectors.cardClass).get(3)
        "contains the correct heading" in {
          card.select("h2").text() shouldBe "Return details"
        }
        "contains the business name" in {
          val vrnRow = card.select("tr:nth-of-type(1)")
          vrnRow.select("td").first().text() shouldBe "VAT Return dates"
          vrnRow.select("td").get(1).text() shouldBe "Every month"
        }
      }
    }
    "Accessed by an agent" should {

      lazy val view = views.html.certificate.vatCertificate(HtmlFormat.empty, model, true)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct title" in {
        document.title shouldBe "Your client's VAT Certificate"
      }

      "have the correct heading" in {
        elementText(Selectors.heading) shouldBe "Your client's VAT Certificate"
      }

      "have the about your registration card" that {
        lazy val card = document.select(Selectors.cardClass).first()
        "contains the correct heading" in {
          card.select("h2").text() shouldBe "About your registration"
        }
        "contains the VRN" in {
          val vrnRow = card.select("tr:nth-of-type(1)")
          vrnRow.select("td").first().text() shouldBe "VAT registration number (VRN)"
          vrnRow.select("td").get(1).text() shouldBe "5555555555"
        }
        "contains the registration date" in {
          val regDateRow = card.select("tr:nth-of-type(2)")
          val date = LocalDate.now().minus(Period.ofDays(2))
          regDateRow.select("td").first().text() shouldBe "Registration date"
          regDateRow.select("td").get(1).text() shouldBe s"1 January 2017"
        }
        "contains the certificate date" in {
          val certDateRow = card.select("tr:nth-of-type(3)")
          val date = LocalDate.now()
          certDateRow.select("td").first().text() shouldBe "Certificate date"
          certDateRow.select("td").get(1).text() shouldBe s"1 January 2018"
        }
      }

      "have the about the business card" that {
        lazy val card = document.select(Selectors.cardClass).get(1)
        "contains the correct heading" in {
          card.select("h2").text() shouldBe "About the business"
        }
        "contains the business name" in {
          val vrnRow = card.select("tr:nth-of-type(1)")
          vrnRow.select("td").first().text() shouldBe "Business name"
          vrnRow.select("td").get(1).text() shouldBe "Not provided"
        }
        "contains the trading name" in {
          val regDateRow = card.select("tr:nth-of-type(2)")
          val date = LocalDate.now().minus(Period.ofDays(2))
          regDateRow.select("td").first().text() shouldBe "Trading name"
          regDateRow.select("td").get(1).text() shouldBe s"ABC Traders"
        }
        "contains the business type" in {
          val certDateRow = card.select("tr:nth-of-type(3)")
          val date = LocalDate.now()
          certDateRow.select("td").first().text() shouldBe "Business type"
          certDateRow.select("td").get(1).text() shouldBe s"Organisation"
        }
        "contains the trade classification" in {
          val certDateRow = card.select("tr:nth-of-type(4)")
          val date = LocalDate.now()
          certDateRow.select("td").first().text() shouldBe "Trade classification (SIC code)"
          certDateRow.select("td").get(1).text() shouldBe s"6602"
        }
        "contains the address" in {
          val certDateRow = card.select("tr:nth-of-type(5)")
          val date = LocalDate.now()
          certDateRow.select("td").first().text() shouldBe "Principal place of business address"
          certDateRow.select("td").get(1).text() shouldBe s"Line 1 Line 2 TF4 3ER"
        }
        "contains the email address" in {
          val certDateRow = card.select("tr:nth-of-type(6)")
          val date = LocalDate.now()
          certDateRow.select("td").first().text() shouldBe "Email address"
          certDateRow.select("td").get(1).text() shouldBe s"ABCStudios@example.com"
        }
      }

      "have the banking details card" that {
        lazy val card = document.select(Selectors.cardClass).get(2)
        "contains the correct heading" in {
          card.select("h2").text() shouldBe "Banking details"
        }
        "contains the business name" in {
          val vrnRow = card.select("tr:nth-of-type(1)")
          vrnRow.select("td").first().text() shouldBe "Account number"
          vrnRow.select("td").get(1).text() shouldBe "333*****"
        }
        "contains the trading name" in {
          val regDateRow = card.select("tr:nth-of-type(2)")
          val date = LocalDate.now().minus(Period.ofDays(2))
          regDateRow.select("td").first().text() shouldBe "Sort code"
          regDateRow.select("td").get(1).text() shouldBe s"****11"
        }
      }
      "have the return details card" that {
        lazy val card = document.select(Selectors.cardClass).get(3)
        "contains the correct heading" in {
          card.select("h2").text() shouldBe "Return details"
        }
        "contains the business name" in {
          val vrnRow = card.select("tr:nth-of-type(1)")
          vrnRow.select("td").first().text() shouldBe "VAT Return dates"
          vrnRow.select("td").get(1).text() shouldBe "Every month"
        }
      }

      "have a link to change action for client" that {
        lazy val link = document.select("p").get(2)
        "has the correct text" in {
          link.select("a").text() shouldBe "Back to your client options"
        }
        "has the correct link" in {
          link.select("a").first().attr("href") shouldBe "/vat-through-software/vat-certificate/change-client-action"
        }
      }
      "have a link to change client" that {
        lazy val link = document.select("p").get(3)
        "has the correct text" in {
          link.select("a").text() shouldBe "Change client"
        }
        "has the correct link" in {
          link.select("a").first().attr("href") shouldBe "/vat-through-software/vat-certificate/change-client-vat-number"
        }
      }
    }
  }
}
