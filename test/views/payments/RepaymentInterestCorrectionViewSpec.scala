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

import common.TestModels.vatOverpaymentForRPI
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.RepaymentInterestCorrectionView

class RepaymentInterestCorrectionViewSpec extends ViewBaseSpec {

  val injectedView: RepaymentInterestCorrectionView = injector.instanceOf[RepaymentInterestCorrectionView]
  val whatYouOweLink: String = controllers.routes.WhatYouOweController.show.url

  "Rendering the VAT Overpayment for RPI breakdown page for a principal user" when {

    lazy val view = injectedView(vatOverpaymentForRPI, Html(""))(request, messages, mockConfig, user)
    lazy val viewAsString = view.toString
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Repayment interest correction - Manage your VAT account - GOV.UK"
    }

    "have the correct page heading" in {
      elementText("h1") shouldBe "7 April 2018 to 10 April 2018 Repayment interest correction"
    }

    "use a non breaking space to display the page heading" in {
      viewAsString.contains("7\u00a0April\u00a02018 to 10\u00a0April\u00a02018 Repayment interest correction")
    }

    "have a period caption" in {
      elementText(".govuk-caption-xl") shouldBe "7 April 2018 to 10 April 2018"
    }

    "use a non breaking space to display the period caption" in {
      viewAsString.contains("7\u00a0April\u00a02018 to 10\u00a0April\u00a02018")
    }

    "have the correct first paragraph" in {
      elementText("#repayment-interest") shouldBe "You need to pay this because HMRC paid you too much repayment interest."
    }

    "have the correct second paragraph" in {
      elementText("#amount-changed") shouldBe "This happened because your VAT amount changed after we paid you the repayment interest."
    }

    "have the correct third paragraph" in {
      elementText("#reduced-amount") shouldBe "The change reduced the amount of repayment interest we owed you."
    }

    "not render backlink" in {
      elementExtinct(".govuk-back-link")
    }

    "render breadcrumbs which" should {

      "have the text 'Business tax account'" in {
        elementText("body > div > div.govuk-breadcrumbs > ol > li:nth-child(1) > a") shouldBe "Business tax account"
      }

      "link to bta" in {
        element("body > div > div.govuk-breadcrumbs > ol > li:nth-child(1) > a").attr("href") shouldBe "bta-url"
      }

      "have the text 'Your VAT account'" in {
        elementText("body > div > div.govuk-breadcrumbs > ol > li:nth-child(2) > a") shouldBe "Your VAT account"
      }

      "link to VAT overview page" in {
        element("body > div > div.govuk-breadcrumbs > ol > li:nth-child(2) > a").attr("href") shouldBe
          controllers.routes.VatDetailsController.details().url
      }

      "have the text 'What You Owe'" in {
        elementText("body > div > div.govuk-breadcrumbs > ol > li:nth-child(3) > a") shouldBe "What you owe"
      }

      "link to the what you owe page" in {
        element("body > div > div.govuk-breadcrumbs > ol > li:nth-child(3) > a").attr("href") shouldBe
          whatYouOweLink
      }
    }

    "have the correct heading for the first row" in {
      elementText(".govuk-summary-list__row:nth-child(1) > dt") shouldBe "Due date"
    }

    "display when the correction charge is due by" in {
      elementText(".govuk-summary-list__row:nth-child(1) > dd") shouldBe "15 April 2018"
    }

    "use a non breaking space to display when the correction charge is due by" in {
      viewAsString.contains("15\u00a0April\u00a02018")
    }

    "have the correct heading for the second row" in {
      elementText(".govuk-summary-list__row:nth-child(2) > dt") shouldBe "Correction charge"
    }

    "display the original amount of the penalty charge" in {
      elementText(".govuk-summary-list__row:nth-child(2) > dd") shouldBe s"£${vatOverpaymentForRPI.correctionCharge.setScale(2)}"
    }

    "have the correct heading for the third row" in {
      elementText(".govuk-summary-list__row:nth-child(3) > dt") shouldBe "Amount received"
    }

    "display the amount received" in {
      elementText(".govuk-summary-list__row:nth-child(3) > dd") shouldBe s"£${vatOverpaymentForRPI.amountReceived.setScale(2)}"
    }

    "have the correct heading for the fourth row" in {
      elementText(".govuk-summary-list__row:nth-child(4) > dt") shouldBe "Left to pay"
    }

    "display the outstanding amount" in {
      elementText(".govuk-summary-list__row:nth-child(4) > dd") shouldBe s"£${vatOverpaymentForRPI.leftToPay.setScale(2)}"
    }

    "have a pay now button" which {

      "has the correct link text" in {
        elementText("#content > div > div > a") shouldBe "Pay now"
      }


      "has the correct href" in {
        element("#content > div > div > a").attr("href") shouldBe controllers.routes.MakePaymentController.makePayment(
          16000, 4, 2018, "2018-04-10", "VAT Overpayment for RPI", "2018-04-15", "ABCD"
        ).url
      }
    }

    "have a link to the what you owe page" which {

      "has the correct link text" in {
        elementText("#whatYouOweLink") shouldBe "Return to what you owe"
      }

      "has the correct href" in {
        element("#whatYouOweLink").attr("href") shouldBe whatYouOweLink
      }
    }
  }

  "Rendering the VAT Overpayment for RPI breakdown page for an agent" when {

    lazy val view = injectedView(vatOverpaymentForRPI, Html(""))(request, messages, mockConfig, agentUser)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Repayment interest correction - Your client’s VAT details - GOV.UK"
    }

    "have the correct first paragraph" in {
      elementText("#repayment-interest") shouldBe "Your client needs to pay this because HMRC paid them too much repayment interest."
    }

    "have the correct second paragraph" in {
      elementText("#amount-changed") shouldBe "This happened because your client’s VAT amount changed after we paid them the repayment interest."
    }

    "have the correct third paragraph" in {
      elementText("#reduced-amount") shouldBe "The change reduced the amount of repayment interest we owed your client."
    }

    "have a link to the what you owe page" which {

      "has the correct link text" in {
        elementText("#whatYouOweLink") shouldBe "Return to what your client owes"
      }

      "has the correct href" in {
        element("#whatYouOweLink").attr("href") shouldBe whatYouOweLink
      }
    }

    "not render breadcrumbs" in {
      elementExtinct(".govuk-breadcrumbs")
    }

    "have a back link" which {

      "has the text Back" in {
        elementText(".govuk-back-link") shouldBe "Back"
      }

      "has the correct href" in {
        element(".govuk-back-link").attr("href") shouldBe whatYouOweLink
      }
    }

    "not have a pay now button" in {
      elementExtinct("#content > div > div > a")
    }
  }

}
