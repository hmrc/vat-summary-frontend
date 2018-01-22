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

package views.payments

import java.time.LocalDate

import models.User
import models.viewModels.OpenPaymentsModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class OpenPaymentsViewSpec extends ViewBaseSpec {

  object Selectors {
    val pageHeading = "h1"
    val paymentType = "td:nth-of-type(1)"
    val paymentDue = "td:nth-of-type(2)"
    val paymentAmount = "td:nth-of-type(3)"
    val paymentLink = "td:nth-of-type(4)"
    val noPaymentMessage = "p#no-payments"
  }

  private val user = User("1111")
  val noPayment = Seq()
  val payment = Seq(
    OpenPaymentsModel(
      "Return",
      543.21,
      LocalDate.parse("2000-02-23"),
      LocalDate.parse("2000-04-12")
    ))

  "Rendering the open payments page" should {

    lazy val view = views.html.payments.openPayments(user, payment)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "VAT payments"
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "VAT payments"
    }
  }

  "Rendering the open payments page with a payment" should {

    lazy val view = views.html.payments.openPayments(user, payment)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the payment type" in {
      elementText(Selectors.paymentType) shouldBe "Return"
    }

    "render the payment due date" in {
      elementText(Selectors.paymentDue) shouldBe "23 February 2000"
    }

    "render the payment amount" in {
      elementText(Selectors.paymentAmount) shouldBe "£543.21"
    }

    "render the payment link" in {
      elementText(Selectors.paymentLink) shouldBe "Check 12 April 2000 return"
    }
  }

  "Rendering the open payments page no payment" should {

    lazy val view = views.html.payments.openPayments(user, noPayment)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the payment type" in {
      elementText(Selectors.noPaymentMessage) shouldBe "You have no open payments."
    }
  }
}
