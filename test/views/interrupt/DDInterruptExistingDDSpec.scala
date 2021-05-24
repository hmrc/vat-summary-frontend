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
import views.html.interrupt.DDInterruptExistingDD
import common.MessageLookup.existingDDInterruptMessages

class DDInterruptExistingDDSpec extends ViewBaseSpec {

  object Selectors {
    val backLink = ".govuk-back-link"
    val title = "title"
    val text = "h1"
    val insetText = ".govuk-inset-text"
    val para1 = "#content > div > div > p:nth-child(4)"
    val para2 = "#content > div > div > p:nth-child(5)"
    val validateButton = ".govuk-button:nth-child(6)"
    val continueButton = ".govuk-button:nth-child(7)"
  }

  lazy val DDInterruptView: DDInterruptExistingDD = injector.instanceOf[DDInterruptExistingDD]

  "The DD interrupt screen for users" should {

    lazy val view = DDInterruptView()
    implicit lazy val document: Document = Jsoup.parse(view.body)

    "have a back link" that {

      "has the correct href" in {
        element(Selectors.backLink).attr("href") shouldBe mockConfig.btaHomeUrl
      }

      "has the correct text" in {
        elementText(Selectors.backLink) shouldBe existingDDInterruptMessages.backLinkText
      }

    }

      "have the correct page title" in {
        elementText(Selectors.title) shouldBe existingDDInterruptMessages.pageTitle
      }

      "have the correct h1 text" in {
        elementText(Selectors.text) shouldBe existingDDInterruptMessages.title
      }

      "has information displayed and inset" in {
          elementText(Selectors.insetText) shouldBe existingDDInterruptMessages.insetText
        }

      "have the correct paragraph 1 text" in {
        elementText(Selectors.para1) shouldBe existingDDInterruptMessages.para1
      }

      "have the correct paragraph 2 text" in {
        elementText(Selectors.para2) shouldBe existingDDInterruptMessages.para2
      }

      "have a button to validate the direct debit" that {

        "has the correct text" in {
          elementText(Selectors.validateButton) shouldBe existingDDInterruptMessages.validateButtonText
        }

        "has the correct href" in {
          element(Selectors.validateButton).attr("href") shouldBe mockConfig.paymentsAndRepaymentsUrl
        }
      }

      "have a button to continue to the VAT account" that {

        "has the correct text" in {
          elementText(Selectors.continueButton) shouldBe existingDDInterruptMessages.continueButtonText
        }

        "has the correct href" in {
          element(Selectors.continueButton).attr("href") shouldBe controllers.routes.VatDetailsController.details().url
        }
      }

    }

}
