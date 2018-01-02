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

import models.payments.Payment
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class ReturnPaymentTemplateSpec extends ViewBaseSpec {

  "The returnPayment template" when {

    object Selectors {
      val nextPaymentDueHeading = "h2:nth-of-type(1)"
      val nextPaymentDate = "p.lede:nth-of-type(1)"
      val outstandingAmount = "p:nth-of-type(2)"
      val viewPaymentButton = "a:nth-of-type(1)"
    }

    "there is a payment to display" should {

      val payment = Payment(
        LocalDate.parse("2017-03-01"),
        LocalDate.parse("2017-03-08"),
        9999,
        "O",
        "#001"
      )

      lazy val view = views.html.templates.returnPayment(Some(payment))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the 'Next payment due' heading" in {
        elementText(Selectors.nextPaymentDueHeading) shouldBe "Next payment due"
      }

      "display the due date of the payment" in {
        elementText(Selectors.nextPaymentDate) shouldBe "8 March 2017"
      }

      "display the outstanding amount of the payment" in {
        elementText(Selectors.outstandingAmount) shouldBe "£9,999"
      }

      "display the 'View payment details' button" in {
        elementText(Selectors.viewPaymentButton) shouldBe "View payment details"
      }
    }

    "there is no payment to display" should {

      lazy val view = views.html.templates.returnPayment(None)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the 'No payment due' heading" in {
        elementText(Selectors.nextPaymentDueHeading) shouldBe "No payment due"
      }
    }
  }
}
