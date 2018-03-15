/*
 * Copyright 2018 HM Revenue & Customs
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

import java.time.LocalDate
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class NextPaymentSectionTemplateSpec extends ViewBaseSpec {

  "The nextPaymentSection template" when {

    object Selectors {
      val nextPaymentDueHeading = "h2:nth-of-type(1)"
      val nextPaymentDate = "p:nth-of-type(1)"
      val viewPaymentButton = "a:nth-of-type(1)"
      val overdueLabel = "span strong"
    }

    "there is a payment to display" should {

      val paymentDueDate = LocalDate.parse("2017-03-08")

      lazy val view = views.html.templates.nextPaymentSection(Some(paymentDueDate), isOverdue = false)
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
    }

    "there is an overdue return" should {

      val obligationDueDate = LocalDate.parse("2017-04-30")

      lazy val view = views.html.templates.nextPaymentSection(Some(obligationDueDate), isOverdue = true)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the overdue label" in {
        elementText(Selectors.overdueLabel) shouldBe "overdue"
      }
    }

    "there is no payment to display" should {

      lazy val view = views.html.templates.nextPaymentSection(None, isOverdue = false)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the 'Next payment due' heading" in {
        elementText(Selectors.nextPaymentDueHeading) shouldBe "Next payment due"
      }

      "display the 'No payment due right now' message" in {
        elementText(Selectors.nextPaymentDate) shouldBe "No payment due right now"
      }

      "display the 'View payment details' button" in {
        elementText(Selectors.viewPaymentButton) shouldBe "Check what you owe"
      }
    }
  }
}
