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

import common.TestModels.{chargeModel1, chargeModel2, overdueCrystallisedLPICharge, whatYouOweViewModel2Charge, whatYouOweViewModelBreathingSpace}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.WhatYouOwe

class WhatYouOweViewSpec extends ViewBaseSpec {

  val whatYouOweView: WhatYouOwe = injector.instanceOf[WhatYouOwe]
  val id = "1236543"

  "The what you owe page for a principal user" when {

    "there is at least one overdue payment and the overdueTimeToPayDescriptionEnabled feature switch is on" should {

      lazy val view = {
        mockConfig.features.overdueTimeToPayDescriptionEnabled(true)
        whatYouOweView(whatYouOweViewModel2Charge, Html(""))
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct title" in {
        document.title shouldBe "What you owe - Manage your VAT account - GOV.UK"
      }

      "have the correct heading" in {
        elementText("h1") shouldBe "What you owe"
      }

      "have breadcrumbs" which {

        "have the text 'Business tax account'" in {
          elementText("li.govuk-breadcrumbs__list-item > a") shouldBe "Business tax account"
        }

        "link to BTA" in {
          element("li.govuk-breadcrumbs__list-item > a").attr("href") shouldBe "bta-url"
        }

        "have the text 'Your VAT account'" in {
          elementText("li.govuk-breadcrumbs__list-item:nth-child(2) > a") shouldBe "Your VAT account"
        }

        "link to the VAT overview page" in {
          element("li.govuk-breadcrumbs__list-item:nth-child(2) > a").attr("href") shouldBe
            controllers.routes.VatDetailsController.details.url
        }
      }

      "have the correct subtitle for total amount" in {
        elementText("p.govuk-body:nth-of-type(1)") shouldBe "Total amount to pay"
      }

      "have the correct total amount" in {
        elementText("p.govuk-body:nth-of-type(2)") shouldBe "£" + whatYouOweViewModel2Charge.totalAmount
      }

      "not show the breathing space inset text" in {
        elementExtinct("#breathing-space-inset")
      }

      "have a charges table" which {

        def tableHeadCell(col: Int): String = s"th.govuk-table__header:nth-child($col)"

        def tableBodyCell(row: Int, col: Int): String = s"tr.govuk-table__row:nth-child($row) > td:nth-child($col)"

        "has the correct heading row" which {

          "has the correct first column heading" in {
            elementText(tableHeadCell(1)) shouldBe "Payment type"
          }

          "has the correct second column heading" in {
            elementText(tableHeadCell(2)) shouldBe "Amount due"
          }
        }

        "has the correct row for an example first charge showing the View VAT Return link" which {

          "has the correct charge description text for a charge" in {
            elementText(tableBodyCell(1, 1) + " .govuk-tag") + " " +
              elementText(tableBodyCell(1, 1) + "> a") + " " +
              elementText(tableBodyCell(1, 1) + "> span > .what-you-owe-due-date") + " " +
              elementText(tableBodyCell(1, 1) + "> span > a > .what-you-owe-view-return") shouldBe
              "overdue VAT for period 1\u00a0Jan to 1\u00a0Feb\u00a02018 due 1\u00a0March\u00a02018 View VAT Return"
          }

          "has a link to the breakdown page" in {
            val url = controllers.routes.ChargeBreakdownController.showBreakdown(chargeModel1.generateHash(user.vrn)).url
            element(tableBodyCell(1, 1) + "> a").attr("href") shouldBe url
          }

          "has an overdue label" in {
            elementText(tableBodyCell(1, 1) + " .govuk-tag") shouldBe "overdue"
          }

          "has due hint text" which {

            "has the correct text" in {
              elementText(tableBodyCell(1, 1) + "> span > .what-you-owe-due-date") + " " +
                elementText(tableBodyCell(1, 1) + "> span > a > .what-you-owe-view-return") shouldBe "due 1\u00a0March\u00a02018 View VAT Return"
            }

            "has the correct href" in {
              element(tableBodyCell(1, 1) + "> span > a").attr("href") shouldBe mockConfig.vatReturnUrl("18AA")
            }

          }

          "has the correct amount" in {
            elementText(tableBodyCell(1, 2)) shouldBe "£" + chargeModel1.outstandingAmount
          }
        }

        "has the correct row for an example second charge not showing the View VAT Return link" which {

          "has the correct charge description text for a charge that does not allow a user to view a VAT return" in {
            elementText(tableBodyCell(2, 1)) shouldBe
              "Penalty for not filing correctly because you did not use the correct digital channel for the period " +
                "1\u00a0Jan to 1\u00a0Feb\u00a02018 due 1\u00a0December\u00a02018"
          }

          "has a link to the breakdown page" in {

            val url = controllers.routes.ChargeBreakdownController.showBreakdown(chargeModel2.generateHash(user.vrn)).url
            element(tableBodyCell(2, 1) + "> a").attr("href") shouldBe url
          }

          "does not have an overdue label" in {
            elementExtinct(tableBodyCell(2, 1) + " .govuk-tag")
          }

          "has the correct due hint text" in {
            elementText(tableBodyCell(2, 1) + "> span") shouldBe "due 1\u00a0December\u00a02018"
          }

          "has the correct amount" in {
            elementText(tableBodyCell(2, 2)) shouldBe "£" + chargeModel2.outstandingAmount.setScale(2)
          }
        }

        "has the correct row for an example overdue crystallised charge" which {
          "has the correct charge description" in {
            elementText(tableBodyCell(3, 1)) shouldBe
              "overdue Interest on central assessment of VAT for period 1\u00a0Jan to 1\u00a0Mar\u00a02021 due 8\u00a0April\u00a02021"
          }
          "has an overdue label" in {
            elementText(tableBodyCell(3, 1) + " .govuk-tag") shouldBe "overdue"
          }
          "has the correct due hint text" in {
            elementText(tableBodyCell(3, 1) + "> span") shouldBe "due 8\u00a0April\u00a02021"
          }
          "has a form with the correct action" in {

            element(tableBodyCell(3, 1) + "> a").attr("href") shouldBe
              controllers.routes.ChargeBreakdownController.showBreakdown(
                overdueCrystallisedLPICharge.generateHash(user.vrn)
              ).url
          }
          "has the correct amount" in {
            elementText(tableBodyCell(3, 2)) shouldBe "£" + overdueCrystallisedLPICharge.leftToPay.setScale(2)
          }
        }

        "has the correct total row" which {

          "has the correct description text" in {
            elementText(tableBodyCell(4, 1)) shouldBe "Total"
          }

          "has the correct total amount" in {
            elementText(tableBodyCell(4, 2)) shouldBe "£" + whatYouOweViewModel2Charge.totalAmount
          }
        }
      }

      "have a section regarding payments you make" which {

        "has the correct first paragraph" in {
          elementText("p.govuk-body:nth-of-type(3)") shouldBe "Payments can take up to 5 working days to clear, depending on " +
            "the payment method you use (opens in a new tab)."
        }

        "has the correct link text in the first paragraph" in {
          elementText("p.govuk-body:nth-of-type(3) > a") shouldBe "the payment method you use (opens in a new tab)"
        }

        "has the correct link location in the first paragraph" in {
          element("p.govuk-body:nth-of-type(3) > a").attr("href") shouldBe mockConfig.govUkPayVATUrl
        }

        "has the correct second paragraph" in {
          elementText("p.govuk-body:nth-of-type(4)") shouldBe "You will be charged interest if your payment does not clear by the payment due date."
        }
      }

      "have a pay now button" which {

        "has the correct text" in {
          elementText("#content .govuk-button") shouldBe "Pay now"
        }

        "has the correct link" in {
          element("#content .govuk-button").attr("href") shouldBe
            controllers.routes.MakePaymentController.makeGenericPayment(
              earliestDueDate = whatYouOweViewModel2Charge.earliestDueDateFormatted,
              linkId = "what-you-owe-pay-now-button"
            ).url
        }
      }

      "have a section for guidance if the user cannot pay today" which {

        "has the correct heading" in {
          elementText("#cannot-pay-heading") shouldBe "Payment help"
        }

        "has the correct first paragraph text" in {
          elementText("#cannot-pay-paragraph") shouldBe "If you cannot pay today, you might be able to " +
            "set up a payment plan to pay in instalments."
        }

        "has the correct link text" in {
          elementText("#cannot-pay-paragraph > a") shouldBe "set up a payment plan"
        }

        "has the correct link location" in {
          element("#cannot-pay-paragraph > a").attr("href") shouldBe controllers.routes.TimeToPayController.redirect.url
        }
        "has the correct second paragraph text" in {
          elementText("#cannot-pay-p2") shouldBe "If you’ve already set up a plan," +
            " you do not need to pay any charges today that:"
        }
        "has correct bullet point text" in {
          elementText("#cannot-pay-bullet") shouldBe "show as estimates on this page are included in your existing payment plan"
        }
      }

      "have a section for guidance if what the user owes is missing" which {

        "has the correct heading" in {
          elementText("#what-you-owe-incorrect") shouldBe "What you owe is incorrect or missing"
        }

        "has the correct first paragraph" which {

          "has the correct text" in {
            elementText("#incorrect-p1") shouldBe "If what you owe is incorrect, check if you can " +
              "correct errors on your VAT Return (opens in a new tab)."
          }

          "has the correct link text" in {
            elementText("#incorrect-p1 > a") shouldBe "correct errors on your VAT Return (opens in a new tab)"
          }

          "has the correct link location" in {
            element("#incorrect-p1 > a").attr("href") shouldBe mockConfig.govUKCorrections
          }
        }

        "has the correct second paragraph" which {

          "has the correct text" in {
            elementText("#incorrect-p2") shouldBe "After you have submitted a return, it can take " +
              "24 hours for what you owe to show here. You can still make a payment (opens in a new tab)."
          }

          "has the correct link text" in {
            elementText("#incorrect-p2 > a") shouldBe "make a payment (opens in a new tab)"
          }

          "has the correct link location" in {
            element("#incorrect-p2 > a").attr("href") shouldBe
              controllers.routes.MakePaymentController.makeGenericPayment(
                earliestDueDate = whatYouOweViewModel2Charge.earliestDueDateFormatted,
                linkId = "what-you-owe-make-payment-link"
              ).url
          }
        }
      }
    }

    "the overdueTimeToPayDescriptionEnabled feature switch is off" should {

      lazy val view = whatYouOweView(whatYouOweViewModel2Charge, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "not have a section for guidance if the user cannot pay today" in {

        mockConfig.features.overdueTimeToPayDescriptionEnabled(false)
        elementExtinct("#cannot-pay-heading")
        elementExtinct("#cannot-pay-paragraph")
      }
    }

    "the user is in BreathingSpace" should {

      lazy val view = whatYouOweView(whatYouOweViewModelBreathingSpace, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      "have the breathing space inset text" in {
        elementText("#breathing-space-inset") shouldBe "Interest and penalties do not " +
          "build up during Breathing Space."
      }
    }
  }

  "The what you owe page for an agent" when {

    "there is at least one overdue payment and the overdueTimeToPayDescriptionEnabled feature switch is on" should {

      lazy val view = whatYouOweView(whatYouOweViewModel2Charge, Html(""))(request, messages, mockConfig, agentUser)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct title" in {
        document.title shouldBe "What your client owes - Your client’s VAT details - GOV.UK"
      }

      "have the correct heading" in {
        elementText("h1") shouldBe "What your client owes"
      }

      "have a Back link" which {

        "has the correct text" in {
          elementText(".govuk-back-link") shouldBe "Back"
        }

        "has the correct href" in {
          element(".govuk-back-link").attr("href") shouldBe mockConfig.agentClientLookupHubUrl
        }
      }

      "have a section regarding payments your client makes" which {

        "has the correct first paragraph" in {
          elementText("p.govuk-body:nth-of-type(3)") shouldBe "Payments can take up to 5 working days to clear, depending on " +
            "the payment method your client uses (opens in a new tab)."
        }

        "has the correct link text in the first paragraph" in {
          elementText("p.govuk-body:nth-of-type(3) > a") shouldBe "the payment method your client uses (opens in a new tab)"
        }

        "has the correct link location in the first paragraph" in {
          element("p.govuk-body:nth-of-type(3) > a").attr("href") shouldBe mockConfig.govUkPayVATUrl
        }

        "has the correct second paragraph" in {
          elementText("p.govuk-body:nth-of-type(4)") shouldBe "They will be charged interest if their payment does not clear by the payment due date."
        }
      }

      "not have a pay now button" in {
        elementExtinct("#content .govuk-button")
      }

      "have a payment help section" which {

        lazy val view = {
          mockConfig.features.overdueTimeToPayDescriptionEnabled(true)
          whatYouOweView(whatYouOweViewModel2Charge, Html(""))(request, messages, mockConfig, agentUser)
        }
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "has the correct title" in {
          elementText(".govuk-details__summary-text") shouldBe "Payment help"
        }

        "has the correct first paragraph text" in {
          elementText("#payment-help-agent-p1") shouldBe "If your client cannot pay today, they might be able to " +
            "set up a payment plan (opens in a new tab) to pay in instalments."
        }

        "has the correct link text" in {
          elementText("#payment-help-agent-p1 > a") shouldBe "set up a payment plan (opens in a new tab)"
        }

        "has the correct link location" in {
          element("#payment-help-agent-p1 > a").attr("href") shouldBe mockConfig.govUKDifficultiesPayingUrl
        }
        "has the correct second paragraph text" in {
          elementText("#payment-help-agent-p2") shouldBe "If they’ve already set up a plan," +
            " they do not need to pay any charges today that:"
        }
        "has correct bullet point text" in {
          elementText("#agent-help-bullet") shouldBe "show as estimates on this page are included in their existing payment plan"
        }
      }

      "have a section for guidance if what the user owes is missing" which {

        "has the correct heading" in {
          elementText("#content h2") shouldBe "The amount owed is incorrect or missing"
        }

        "has the correct first paragraph" which {

          "has the correct text" in {
            elementText("p.govuk-body:nth-of-type(5)") shouldBe "If the amount owed is incorrect, check if you can " +
              "correct errors on your client’s VAT Return (opens in a new tab)."
          }

          "has the correct link text" in {
            elementText("p.govuk-body:nth-of-type(5) > a") shouldBe
              "correct errors on your client’s VAT Return (opens in a new tab)"
          }

          "has the correct link location" in {
            element("p.govuk-body:nth-of-type(5) > a").attr("href") shouldBe mockConfig.govUKCorrections
          }
        }

        "has the correct second paragraph" in {
          elementText("p.govuk-body:nth-of-type(6)") shouldBe "After you have submitted a return, it can take " +
            "24 hours for what is owed to show here."
        }
      }
    }

    "there is at least one overdue payment and the overdueTimeToPayDescriptionEnabled feature switch is off" should {

      "not have a payment help section" in {

        lazy val view = whatYouOweView(whatYouOweViewModel2Charge, Html(""))(request, messages, mockConfig, agentUser)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        mockConfig.features.overdueTimeToPayDescriptionEnabled(false)

        elementExtinct(".govuk-details__summary-text")
        elementExtinct(".govuk-details__text")
      }
    }
  }

  "The webchat link is displayed" when {
    "the webchatEnabled feature switch is switched on for principal user" in {
      lazy val view = {
        mockConfig.features.webchatEnabled(true)
        whatYouOweView(whatYouOweViewModel2Charge, Html(""))
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      document.select("#webchatLink-id").text() shouldBe "Ask HMRC (opens in a new tab)"
      document.select("#webchatLink-id").attr("href") shouldBe "https://www.tax.service.gov.uk/ask-hmrc/chat/vat-online?ds"
    }

    "the webchatEnabled feature switch is switched on for an agent" in {
      lazy val view = {
        mockConfig.features.webchatEnabled(true)
        whatYouOweView(whatYouOweViewModel2Charge, Html(""))(request, messages, mockConfig, agentUser)
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      document.select("#webchatLink-id").text() shouldBe "Ask HMRC (opens in a new tab)"
      document.select("#webchatLink-id").attr("href") shouldBe "https://www.tax.service.gov.uk/ask-hmrc/chat/vat-online?ds"
    }
  }

  "The webchat link is not displayed" when {
    "the webchatEnabled feature switch is switched off for principal user" in {
      lazy val view = {
        mockConfig.features.webchatEnabled(false)
        whatYouOweView(whatYouOweViewModel2Charge, Html(""))
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      document.select("#webchatLink-id").size shouldBe 0
    }

    "the webchatEnabled feature switch is switched off for an agent" in {
      lazy val view = {
        mockConfig.features.webchatEnabled(false)
        whatYouOweView(whatYouOweViewModel2Charge, Html(""))(request, messages, mockConfig, agentUser)
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      document.select("#webchatLink-id").size shouldBe 0
    }
  }
}
