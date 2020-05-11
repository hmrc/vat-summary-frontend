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

package views.templates

import models.User
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.templates.NextPaymentSection

class NextPaymentSectionTemplateSpec extends ViewBaseSpec {

  val nextPaymentSection: NextPaymentSection = injector.instanceOf[NextPaymentSection]

  "The nextPaymentSection template" when {

    implicit val user: User = User("999999999")

    object Selectors {
      val nextPaymentDueHeading = "h2:nth-of-type(1)"
      val nextPaymentDate = "p:nth-of-type(1)"
      val viewPaymentButton = "a:nth-of-type(1)"
      val portalLink = "a"
    }

    "there is a payment to display" when {

      "payment is not overdue" should {

        lazy val view = nextPaymentSection(
          Some("2017-03-08"),
          hasMultiple = false,
          isError = false,
          isHybridUser = false,
          isOverdue = false
        )
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "display the 'Next payment due' heading" in {
          elementText(Selectors.nextPaymentDueHeading) shouldBe "Next payment due"
        }

        "display the due date of the payment" in {
          elementText(Selectors.nextPaymentDate) shouldBe "8 March 2017"
        }

        "display the 'View payment details' button" in {
          elementText(Selectors.viewPaymentButton) shouldBe "Check what you owe"
        }

        "not display overdue flag" in {
          elementExtinct(".task-overdue")
        }
      }

      "payment is overdue" should {

        lazy val view = nextPaymentSection(
          Some("2017-03-08"),
          hasMultiple = false,
          isError = false,
          isHybridUser = false,
          isOverdue = true
        )
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "display overdue flag" in {
          elementText(".task-overdue")shouldBe "overdue"
        }
      }
    }

    "there is no payment to display" should {

      lazy val view = nextPaymentSection(None,
        hasMultiple = false,
        isError = false,
        isHybridUser = false,
        isOverdue = false
      )
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the 'Next payment due' heading" in {
        elementText(Selectors.nextPaymentDueHeading) shouldBe "Next payment due"
      }

      "display the 'No payments due right now' message" in {
        elementText(Selectors.nextPaymentDate) shouldBe "No payments due right now"
      }

      "display the 'View payment details' button" in {
        elementText(Selectors.viewPaymentButton) shouldBe "Check what you owe"
      }
    }

    "there is an error retrieving the payment" should {

      lazy val view = nextPaymentSection(None,
        hasMultiple = false,
        isError = true,
        isHybridUser = false,
        isOverdue = false
      )
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the 'Next payment due' heading" in {
        elementText(Selectors.nextPaymentDueHeading) shouldBe "Next payment due"
      }

      "display the error message" in {
        elementText(Selectors.nextPaymentDate) shouldBe "Sorry, there is a problem with the service. Try again later."
      }

      "display the 'View payment details' button" in {
        elementText(Selectors.viewPaymentButton) shouldBe "Check what you owe"
      }

    }

    "there are multiple payments to display" should {

      lazy val view = nextPaymentSection(Some("2"),
        hasMultiple = true,
        isError = false,
        isHybridUser = false,
        isOverdue = false
      )
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the 'Next payment due' heading" in {
        elementText(Selectors.nextPaymentDueHeading) shouldBe "Next payment due"
      }

      "display the 'payments due' message" in {
        elementText(Selectors.nextPaymentDate) shouldBe "You have 2 payments due"
      }

      "display the 'View payment details' button" in {
        elementText(Selectors.viewPaymentButton) shouldBe "Check what you owe"
      }

    }

    "the user is hybrid" should {

      lazy val view = nextPaymentSection(None,
        hasMultiple = false,
        isError = false,
        isHybridUser = true,
        isOverdue = false
      )
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the 'Next payment due' heading" in {
        elementText(Selectors.nextPaymentDueHeading) shouldBe "Next payment due"
      }

      "display a link to the portal" in {
        element(Selectors.portalLink).attr("href") shouldBe "/vat-through-software/portal-what-you-owe"
      }

      "have the correct link text" in {
        elementText(Selectors.portalLink) shouldBe "Check what you owe and make a payment (opens in a new tab)"
      }
    }
  }
}
