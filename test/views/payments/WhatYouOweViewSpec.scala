/*
 * Copyright 2022 HM Revenue & Customs
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

import common.TestModels.{chargeModel1, chargeModel2, overdueCrystallisedInterestCharge, whatYouOweViewModel2Charge}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.WhatYouOwe

class WhatYouOweViewSpec extends ViewBaseSpec {

  val whatYouOweView: WhatYouOwe = injector.instanceOf[WhatYouOwe]

  "The what you owe page for a principal user" should {

    lazy val view = whatYouOweView(whatYouOweViewModel2Charge, Html(""))
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
          elementText(tableBodyCell(1, 1)) shouldBe
            s"overdue ${chargeModel1.title} ${chargeModel1.description(isAgent = false)} due 1 March 2018 View VAT Return"
        }

        "has an overdue label" in {
          elementText(tableBodyCell(1, 1) + " .govuk-tag") shouldBe "overdue"
        }

        "has due hint text" which {

          "has the correct text" in {
            elementText(tableBodyCell(1, 1) + "> span") shouldBe "due 1 March 2018 View VAT Return"
          }

          "has the correct href" in {
            element(tableBodyCell(1, 1) + "> span > a").attr("href") shouldBe mockConfig.vatReturnUrl("18AA")
          }

        }

        "has a form with the correct action" in {
          element(tableBodyCell(1, 1) + " > form").attr("action") shouldBe
            testOnly.controllers.routes.ChargeBreakdownController.chargeBreakdown.url
        }

        "has the correct amount" in {
          elementText(tableBodyCell(1, 2)) shouldBe "£" + chargeModel1.outstandingAmount
        }
      }

      "has the correct row for an example second charge not showing the View VAT Return link" which {

        "has the correct charge description text for a charge that does not allow a user to view a VAT return" in {
          elementText(tableBodyCell(2, 1)) shouldBe
            s"${chargeModel2.title} ${chargeModel2.description(isAgent = false)} due 1 December 2018"
        }

        "does not have an overdue label" in {
          elementExtinct(tableBodyCell(2, 1) + " .govuk-tag")
        }

        "has the correct due hint text" in {
          elementText(tableBodyCell(2, 1) + "> span") shouldBe "due 1 December 2018"
        }

        "has a form with the correct action" in {
          element(tableBodyCell(2, 1) + " > form").attr("action") shouldBe
            testOnly.controllers.routes.ChargeBreakdownController.chargeBreakdown.url
        }

        "has the correct amount" in {
          elementText(tableBodyCell(2, 2)) shouldBe "£" + chargeModel2.outstandingAmount.toInt
        }
      }
      "has the correct row for an example overdue crystallised charge" which {
        "has the correct charge description" in {
          elementText(tableBodyCell(3, 1)) shouldBe
            s"overdue Interest on central assessment of VAT for period 1 Jan to 1 Mar 2021 due 8 April 2021"
        }
        "has an overdue label" in {
          elementText(tableBodyCell(3, 1) + " .govuk-tag") shouldBe "overdue"
        }
        "has the correct due hint text" in {
          elementText(tableBodyCell(3, 1) + "> span") shouldBe "due 8 April 2021"
        }
        "has a form with the correct action" in {
          element(tableBodyCell(3, 1) + " > form").attr("action") shouldBe
            testOnly.controllers.routes.ChargeBreakdownController.crystallisedInterestBreakdown.url
        }
        "has the correct amount" in {
          elementText(tableBodyCell(3, 2)) shouldBe "£" + overdueCrystallisedInterestCharge.leftToPay.toInt
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

      "has the correct leading text" in {
        elementText("p.govuk-body:nth-of-type(3)") shouldBe "Any payments you make:"
      }

      "has the correct first bullet point" which {

        "has the correct text" in {
          elementText(".govuk-list > li:nth-of-type(1)") shouldBe "may take up to 5 working days to process, " +
            "depending on the payment method you use (opens in a new tab)"
        }

        "has the correct link text" in {
          elementText(".govuk-list > li > a") shouldBe "the payment method you use (opens in a new tab)"
        }

        "has the correct link location" in {
          element(".govuk-list > li > a").attr("href") shouldBe mockConfig.govUkPayVATUrl
        }
      }

      "has the correct text in the second bullet point" in {
        elementText(".govuk-list > li:nth-of-type(2)") shouldBe "must reach us by the due date - you’ll be charged " +
          "interest and may have to pay a penalty if your payment is late"
      }
    }

    "have a pay now button" which {

      "has the correct text" in {
        elementText(".govuk-button") shouldBe "Pay now"
      }

      "has the correct link" in {
        element(".govuk-button").attr("href") shouldBe mockConfig.unauthenticatedPaymentsUrl
      }
    }

    "have a section for guidance if the user cannot pay today" which {

      "has the correct heading" in {
        elementText("#cannot-pay-heading") shouldBe "If you cannot pay today"
      }

      "has the correct paragraph text" in {
        elementText("p.govuk-body:nth-of-type(4)") shouldBe "If you cannot pay a tax bill, you can ask HMRC about " +
          "setting up a Time to Pay Arrangement (opens in a new tab). This allows you to pay your bill in instalments."
       }

      "has the correct link text" in {
        elementText("p.govuk-body:nth-of-type(4) > a") shouldBe "setting up a Time to Pay Arrangement (opens in a new tab)"
      }

      "has the correct link location" in {
        element("p.govuk-body:nth-of-type(4) > a").attr("href") shouldBe mockConfig.govUKDifficultiesPayingUrl
      }
    }

    "have a section for guidance if what the user owes is missing" which {

      "has the correct heading" in {
        elementText("h2:nth-of-type(2)") shouldBe "What you owe is incorrect or missing"
      }

      "has the correct first paragraph" which {

        "has the correct text" in {
          elementText("p.govuk-body:nth-of-type(5)") shouldBe "If what you owe is incorrect, check if you can " +
            "correct errors on your VAT Return (opens in a new tab)."
        }

        "has the correct link text" in {
          elementText("p.govuk-body:nth-of-type(5) > a") shouldBe "correct errors on your VAT Return (opens in a new tab)"
        }

        "has the correct link location" in {
          element("p.govuk-body:nth-of-type(5) > a").attr("href") shouldBe mockConfig.govUKCorrections
        }
      }

      "has the correct second paragraph" which {

        "has the correct text" in {
          elementText("p.govuk-body:nth-of-type(6)") shouldBe "After you have submitted a return, it can take " +
            "24 hours for what you owe to show here. You can still make a payment (opens in a new tab)."
        }

        "has the correct link text" in {
          elementText("p.govuk-body:nth-of-type(6) > a") shouldBe "make a payment (opens in a new tab)"
        }

        "has the correct link location" in {
          element("p.govuk-body:nth-of-type(6) > a").attr("href") shouldBe mockConfig.unauthenticatedPaymentsUrl
        }
      }
    }
  }

  "The what you owe page for an agent" should {

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

      "has the correct leading text" in {
        elementText("p.govuk-body:nth-of-type(3)") shouldBe "Any payments your client makes:"
      }

      "has the correct first bullet point" which {

        "has the correct text" in {
          elementText(".govuk-list > li:nth-of-type(1)") shouldBe "may take up to 5 working days to process, " +
            "depending on the payment method they use (opens in a new tab)"
        }

        "has the correct link text" in {
          elementText(".govuk-list > li > a") shouldBe "the payment method they use (opens in a new tab)"
        }

        "has the correct link location" in {
          element(".govuk-list > li > a").attr("href") shouldBe mockConfig.govUkPayVATUrl
        }
      }

      "has the correct text in the second bullet point" in {
        elementText(".govuk-list > li:nth-of-type(2)") shouldBe "must reach us by the due date - your client will be " +
          "charged interest and may have to pay a penalty if their payment is late"
      }
    }

    "not have a pay now button" in {
      elementExtinct(".govuk-button")
    }

    "have a payment help section" which {

      "has the correct title" in {
        elementText(".govuk-details__summary-text") shouldBe "Payment help"
      }

      "has the correct paragraph text" in {
        elementText(".govuk-details__text") shouldBe "If your client cannot pay a tax bill, they can ask HMRC about " +
          "setting up a Time to Pay Arrangement (opens in a new tab). This allows them to pay their bill in instalments."
      }

      "has the correct link text" in {
        elementText(".govuk-details__text > p > a") shouldBe "setting up a Time to Pay Arrangement (opens in a new tab)"
      }

      "has the correct link location" in {
        element(".govuk-details__text > p > a").attr("href") shouldBe mockConfig.govUKDifficultiesPayingUrl
      }
    }

    "have a section for guidance if what the user owes is missing" which {

      "has the correct heading" in {
        elementText("h3:nth-of-type(1)") shouldBe "The amount owed is incorrect or missing"
      }

      "has the correct first paragraph" which {

        "has the correct text" in {
          elementText("p.govuk-body:nth-of-type(4)") shouldBe "If the amount owed is incorrect, check if you can " +
            "correct errors on your client’s VAT Return (opens in a new tab)."
        }

        "has the correct link text" in {
          elementText("p.govuk-body:nth-of-type(4) > a") shouldBe
            "correct errors on your client’s VAT Return (opens in a new tab)"
        }

        "has the correct link location" in {
          element("p.govuk-body:nth-of-type(4) > a").attr("href") shouldBe mockConfig.govUKCorrections
        }
      }

      "has the correct second paragraph" in {
        elementText("p.govuk-body:nth-of-type(5)") shouldBe "After you have submitted a return, it can take " +
          "24 hours for what is owed to show here."
      }
    }
  }
}
