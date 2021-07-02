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

package views.interrupt

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.interrupt.DDInterruptNoDD
import common.MessageLookup.noDDInterruptMessages

class DDInterruptNoDDSpec extends ViewBaseSpec {

  val redirectUrl = "/vat-through-software/vat-overview"

  object Selectors {
    val backLink = ".govuk-back-link"
    val title = "title"
    val text = "h1"
    val detailsSummary = ".govuk-details__summary-text"
    val detailsText = ".govuk-details__text"
    val para1 = "div > .govuk-body:nth-of-type(1)"
    val para2 = "div > .govuk-body:nth-of-type(2)"
    val para3 = "div > .govuk-body:nth-of-type(3)"
    val setupButton = ".govuk-button:nth-child(6)"
    val continueButton = ".govuk-button:nth-child(7)"
  }

  lazy val DDInterruptView: DDInterruptNoDD = injector.instanceOf[DDInterruptNoDD]

  "The DD interrupt screen for users" should {

    lazy val view = DDInterruptView(redirectUrl)
    implicit lazy val document: Document = Jsoup.parse(view.body)

    "have a back link" that {

      "has the correct href" in {
        element(Selectors.backLink).attr("href") shouldBe mockConfig.btaHomeUrl
      }

      "has the correct text" in {
        elementText(Selectors.backLink) shouldBe noDDInterruptMessages.backLinkText
      }

    }

    "have the correct page title" in {
      elementText(Selectors.title) shouldBe noDDInterruptMessages.pageTitle
    }

    "have the correct h1 text" in {
      elementText(Selectors.text) shouldBe noDDInterruptMessages.title
    }

    "have a progressive disclosure section" that {

      "has the correct summary" in {
        elementText(Selectors.detailsSummary) shouldBe noDDInterruptMessages.detailsSummary
      }

      "has the correct content" in {
        elementText(Selectors.detailsText) shouldBe noDDInterruptMessages.detailsText
      }

    }

    "have the correct paragraph 1 text" in {
      elementText(Selectors.para1) shouldBe noDDInterruptMessages.para1
    }

    "have the correct paragraph 2 text" in {
      elementText(Selectors.para2) shouldBe noDDInterruptMessages.para2
    }

    "have the correct paragraph 3 text" in {
      elementText(Selectors.para3) shouldBe noDDInterruptMessages.para3
    }

    "have a button to set up a direct debit" that {

      "has the correct text" in {
        elementText(Selectors.setupButton) shouldBe noDDInterruptMessages.setupButtonText
      }

      "has the correct href" in {
        element(Selectors.setupButton).attr("href") shouldBe mockConfig.paymentsAndRepaymentsUrl
      }
    }

    "have a button to continue to the VAT account" that {

      "has the correct text" in {
        elementText(Selectors.continueButton) shouldBe noDDInterruptMessages.continueButtonText
      }

      "has the correct href" in {
        element(Selectors.continueButton).attr("href") shouldBe controllers.routes.VatDetailsController.details().url
      }
    }

  }

}
