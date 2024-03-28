/*
 * Copyright 2023 HM Revenue & Customs
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

package views.payments

import common.TestModels._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.ChargeTypeDetailsView

class ChargeTypeDetailsViewSpec extends ViewBaseSpec {

  val chargeTypeDetailsView: ChargeTypeDetailsView = injector.instanceOf[ChargeTypeDetailsView]

  object Selectors {
    val pageHeading = "h1"
    val caption = ".govuk-caption-xl"
    val breadcrumbs = "govuk-breadcrumbs"
    val btaBreadcrumb = "li.govuk-breadcrumbs__list-item > a"
    val vatBreadcrumb = "li.govuk-breadcrumbs__list-item:nth-child(2) > a"
    val openPaymentsBreadcrumb = "li.govuk-breadcrumbs__list-item:nth-child(3) > a"
    val backLink = ".govuk-back-link"
    val dueDateKey = ".govuk-summary-list__key"
    val dueDateValue = ".govuk-summary-list__value"
    val chargeDueKey = ".govuk-summary-list__row:nth-of-type(2) > .govuk-summary-list__key"
    val chargeDueValue = ".govuk-summary-list__row:nth-of-type(2) > .govuk-summary-list__value"
    val clearedAmountKey = ".govuk-summary-list__row:nth-of-type(3) > .govuk-summary-list__key"
    val clearedAmountValue = ".govuk-summary-list__row:nth-of-type(3) > .govuk-summary-list__value"
    val outstandingAmountKey = ".govuk-summary-list__row:nth-of-type(4) > .govuk-summary-list__key"
    val outstandingAmountValue = ".govuk-summary-list__row:nth-of-type(4) > .govuk-summary-list__value"
    val button = "#content .govuk-button"
    val whatYouOweLink = "#whatYouOweLink"
    val viewReturn = "#view-return"
    val viewReturnLink = "#view-return > a"
    val insetText = ".govuk-inset-text"
  }

  object VATOverpaymentSelectors {
    val pageHeading = "h1"
    val firstParagraph = "#content > div > div > p:nth-child(2)"
    val secondParagraph = "#content > div > div > p:nth-child(3)"
    val bulletList = "ul.govuk-list--bullet"
    val firstBullet = s"$bulletList > li:nth-of-type(1)"
    val secondBullet = s"$bulletList > li:nth-of-type(2)"
    val thirdBullet = s"$bulletList > li:nth-of-type(3)"
  }


  "Rendering the Charge Type Details page for a principal user" when {

    "the user has a cleared amount and a period for the charge" when {

      "the charge isn't overdue" should {

          lazy val view = {
            chargeTypeDetailsView(whatYouOweCharge, Html(""))(request, messages, mockConfig, user)
          }
        lazy val viewAsString = view.toString
          lazy implicit val document: Document = Jsoup.parse(view.body)

          "have the correct document title" in {
            document.title shouldBe "VAT - Manage your VAT account - GOV.UK"
          }

          "have the correct page heading" in {
            elementText(Selectors.pageHeading) shouldBe "1 January 2021 to 31 March 2021 VAT"
          }

          "use a non breaking space for the page heading" in {
            viewAsString.contains("1\u00a0January\u00a02021 to 31\u00a0March\u00a02021 VAT")
          }

          "have a period caption" in {
            elementText(Selectors.caption) shouldBe "1 January 2021 to 31 March 2021"
          }

          "use a non breaking space for the period caption" in {
            viewAsString.contains("1\u00a0January\u00a02021 to 31\u00a0March\u00a02021")
          }

          "render breadcrumbs which" should {

            "have the text 'Business tax account'" in {
              elementText(Selectors.btaBreadcrumb) shouldBe "Business tax account"
            }

            "link to bta" in {
              element(Selectors.btaBreadcrumb).attr("href") shouldBe "bta-url"
            }

            "have the text 'Your VAT account'" in {
              elementText(Selectors.vatBreadcrumb) shouldBe "Your VAT account"
            }

            "link to the VAT overview page" in {
              element(Selectors.vatBreadcrumb).attr("href") shouldBe vatDetailsUrl
            }

            "have the text 'What you owe'" in {
              elementText(Selectors.openPaymentsBreadcrumb) shouldBe "What you owe"
            }

            "link to the what you owe page" in {
              element(Selectors.openPaymentsBreadcrumb).attr("href") shouldBe whatYouOweUrl
            }
          }

          "have the correct first column in the first line" in {
            elementText(Selectors.dueDateKey) shouldBe "Due date"
          }

          "display the correct due date for the charge" in {
            elementText(Selectors.dueDateValue) shouldBe "8 April 2021"
          }

          "use a non breaking space to display the due date for the charge" in {
            viewAsString.contains("Due date 8\u00a0April\u00a02021")
          }

          "have the correct first column in the second line" in {
            elementText(Selectors.chargeDueKey) shouldBe "Original charge"
          }

          "display the correct total amount due" in {
            elementText(Selectors.chargeDueValue) shouldBe "£3,333.33"
          }

          "have the correct first column in the third line" in {
            elementText(Selectors.clearedAmountKey) shouldBe "Amount received"
          }

          "display the correct cleared amount" in {
            elementText(Selectors.clearedAmountValue) shouldBe "£2,222.22"
          }

          "have the correct first column in the fourth line" in {
            elementText(Selectors.outstandingAmountKey) shouldBe "Left to pay"
          }

          "display the correct outstanding amount" in {
            elementText(Selectors.outstandingAmountValue) shouldBe "£1,111.11"
          }

          "have a button" which {

            "has the correct button text" in {
              elementText(Selectors.button) shouldBe "Pay now"
            }

            "has the correct href location" in {
              element(Selectors.button).attr("href") shouldBe controllers.routes.MakePaymentController.makePayment(
                111111, 3, 2021, "2021-03-31", "VAT Return Debit Charge", "2021-04-08", "noCR"
              ).url
            }
          }

          "have a link to the What you owe page" which {

            "has the correct link text" in {
              elementText(Selectors.whatYouOweLink) shouldBe "Return to what you owe"
            }

            "has the correct href" in {
              element(Selectors.whatYouOweLink).attr("href") shouldBe whatYouOweUrl
            }
          }
        }

      "the charge is overdue" should {

          lazy val view = {
            chargeTypeDetailsView(whatYouOweChargeOverdue, Html(""))(request, messages, mockConfig, user)
          }
          lazy val viewAsstring = view.toString
          lazy implicit val document: Document = Jsoup.parse(view.body)

          "display the overdue label" in {
            elementText(Selectors.dueDateValue) shouldBe "8 April 2021 overdue"
          }

          "use a non breaking space to display the overdue label" in {
            viewAsstring.contains("8\u00a0April\u00a02021 overdue")
          }
        }
    }

    "the user has a cleared amount and no period for the charge" should {

        lazy val view = {
          chargeTypeDetailsView(whatYouOweChargeNoPeriod, Html(""))(request, messages, mockConfig, user)
        }
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "not have a period caption" in {
          elementExtinct(Selectors.caption)
        }
      }

    "the user only has the periodFrom field but not the periodTo field" should {

        lazy val view = {
          chargeTypeDetailsView(whatYouOweChargeNoPeriodTo, Html(""))(request, messages, mockConfig, user)
        }
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "not have a period caption" in {
          elementExtinct(Selectors.caption)
        }
      }

    "the user only has the periodTo field but not the periodFrom field" should {

        lazy val view = {
          chargeTypeDetailsView(whatYouOweChargeNoPeriodFrom, Html(""))(request, messages, mockConfig, user)
        }
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "not have a period caption" in {
          elementExtinct(Selectors.caption)
        }
      }

    "the charge allows the user to view a VAT return" should {

        lazy val view = {
          chargeTypeDetailsView(chargeModel1, Html(""))(request, messages, mockConfig, user)
        }
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have a link to view the VAT return" that {

          "has the correct text" in {
            elementText(Selectors.viewReturn) shouldBe "View this VAT Return."
          }

          "has the correct href" in {
            element(Selectors.viewReturnLink).attr("href") shouldBe mockConfig.vatReturnUrl("18AA")
          }
        }
      }

    "the charge does not allow the user to view a VAT return" should {

        lazy val view = {
          chargeTypeDetailsView(whatYouOweChargeNoViewReturn, Html(""))(request, messages, mockConfig, user)
        }
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "not have a link to view the VAT return" in {
          elementExtinct(Selectors.viewReturn)
        }

      }

    "the charge is a Default Surcharge" when {

      "the charge has a charge reference" should {

          lazy val view = {
            chargeTypeDetailsView(whatYouOweCharge.copy(chargeType = "VAT Debit Default Surcharge", chargeReference = Some("XD002750002155")),
              Html(""))(request, messages, mockConfig, user)
          }
          lazy implicit val document: Document = Jsoup.parse(view.body)

          "display the inset text" in {
            elementText(Selectors.insetText) shouldBe "You must use the 14-character reference number XD002750002155 when making this payment."
          }
        }

      "the charge does not have a charge reference" should {

          lazy val view = {
            chargeTypeDetailsView(whatYouOweCharge.copy(chargeType = "VAT Debit Default Surcharge"),
              Html(""))(request, messages, mockConfig, user)
          }
          lazy implicit val document: Document = Jsoup.parse(view.body)

          "not display the inset text" in {
            elementExtinct(Selectors.insetText)
          }
        }
    }
  }

  "Rendering the Charge Type Details page for an agent" when {

    "the charge is any charge type" should {

        lazy val view = {
          chargeTypeDetailsView(whatYouOweCharge, Html(""))(request, messages, mockConfig, agentUser)
        }
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "not render breadcrumbs" in {
          elementExtinct(Selectors.breadcrumbs)
        }

        "have a backLink" which {

          "has the text Back" in {
            elementText(Selectors.backLink) shouldBe "Back"
          }

          "has the correct href" in {
            element(Selectors.backLink).attr("href") shouldBe whatYouOweUrl
          }
        }

        "not have the make payment button" in {
          elementExtinct(Selectors.button)
        }

        "have a link to the What your client owes page" which {

          "has the correct link text" in {
            elementText(Selectors.whatYouOweLink) shouldBe "Return to what your client owes"
          }

          "has the correct href" in {
            element(Selectors.whatYouOweLink).attr("href") shouldBe whatYouOweUrl
          }
        }
      }

    "the charge is a Default Surcharge" should {

        "the charge has a charge reference" should {

          lazy val view = {
            chargeTypeDetailsView(whatYouOweCharge.copy(chargeType = "VAT Debit Default Surcharge", chargeReference = Some("XD002750002155")),
              Html(""))(request, messages, mockConfig, agentUser)
          }
          lazy implicit val document: Document = Jsoup.parse(view.body)

          "display the inset text" in {
            elementText(Selectors.insetText) shouldBe "Your client must use the 14-character reference number XD002750002155 when making this payment."
          }
        }

        "the charge does not have a charge reference" should {

          lazy val view = {
            chargeTypeDetailsView(whatYouOweCharge.copy(chargeType = "VAT Debit Default Surcharge"),
              Html(""))(request, messages, mockConfig, agentUser)
          }
          lazy implicit val document: Document = Jsoup.parse(view.body)

          "not display the inset text" in {
            elementExtinct(Selectors.insetText)
          }
        }
      }

    "the charge type VATOverpaymentforTax" should {

      "correctly show content for agent user" in {
        lazy val view = {
          chargeTypeDetailsView(vatOverpaymentTax,
            Html(""))(request, messages, mockConfig, agentUser)
        }
        lazy implicit val document: Document = Jsoup.parse(view.body)

        document.title() shouldBe "VAT correction - Your client’s VAT details - GOV.UK"
        elementText(VATOverpaymentSelectors.firstParagraph) shouldBe "Your client needs to pay this because HMRC paid them more VAT than we owed them."
        elementText(VATOverpaymentSelectors.secondParagraph) shouldBe "This could be because of:"
        elementText(VATOverpaymentSelectors.firstBullet) shouldBe "an error correction"
        elementText(VATOverpaymentSelectors.secondBullet) shouldBe "an officer’s assessment"
        elementText(VATOverpaymentSelectors.thirdBullet) shouldBe "a reallocation of funds across your client’s account"
      }

      "correctly show content for user" in {
        lazy val view = {
          chargeTypeDetailsView(vatOverpaymentTax,
            Html(""))(request, messages, mockConfig, user)
        }
        lazy implicit val document: Document = Jsoup.parse(view.body)

        document.title() shouldBe "VAT correction - Manage your VAT account - GOV.UK"
        elementText(VATOverpaymentSelectors.firstParagraph) shouldBe "You need to pay this because HMRC paid you more VAT than we owed you."
        elementText(VATOverpaymentSelectors.secondParagraph) shouldBe "This could be because of:"
        elementText(VATOverpaymentSelectors.firstBullet) shouldBe "an error correction"
        elementText(VATOverpaymentSelectors.secondBullet) shouldBe "an officer’s assessment"
        elementText(VATOverpaymentSelectors.thirdBullet) shouldBe "a reallocation of funds across your account"
      }
    }
  }
}
