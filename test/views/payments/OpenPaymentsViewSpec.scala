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
    val paymentDue = "#payments span[data-due]"
    val paymentAmount = "#payments span[data-amount]"
    val paymentLink = "#payments a"
    val btaBreadcrumb = "div.breadcrumbs li:nth-of-type(1)"
    val btaBreadcrumbLink = "div.breadcrumbs li:nth-of-type(1) a"
    val vatBreadcrumb = "div.breadcrumbs li:nth-of-type(2)"
    val vatBreadcrumbLink = "div.breadcrumbs li:nth-of-type(2) a"
    val paymentBreadcrumb = "div.breadcrumbs li:nth-of-type(3)"
  }

  private val user = User("1111")
  val noPayment = Seq()
  val payment = Seq(
    OpenPaymentsModel(
      "Return",
      543.21,
      LocalDate.parse("2000-04-08"),
      LocalDate.parse("2000-01-01"),
      LocalDate.parse("2000-03-31"),
      "#001"
    )
  )

  "Rendering the open payments page" should {

    lazy val view = views.html.payments.openPayments(user, payment)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "VAT payments"
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "VAT payments"
    }

    "render breadcrumbs which" should {

      "have the text 'Business tax account'" in {
        elementText(Selectors.btaBreadcrumb) shouldBe "Business tax account"
      }

      "link to bta" in {
        element(Selectors.btaBreadcrumbLink).attr("href") shouldBe "bta-url"
      }

      "have the text 'VAT'" in {
        elementText(Selectors.vatBreadcrumb) shouldBe "VAT"
      }

      s"link to ${controllers.routes.VatDetailsController.details().url}" in {
        element(Selectors.vatBreadcrumbLink).attr("href") shouldBe controllers.routes.VatDetailsController.details().url
      }

      "have the text 'VAT payments'" in {
        elementText(Selectors.paymentBreadcrumb) shouldBe "VAT payments"
      }
    }
  }

  "Rendering the open payments page with a payment" should {

    lazy val view = views.html.payments.openPayments(user, payment)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the payment due date" in {
      elementText(Selectors.paymentDue) shouldBe "8 April 2000"
    }

    "render the payment amount" in {
      elementText(Selectors.paymentAmount) shouldBe "£543.21"
    }

    "render the payment link" in {
      elementText(Selectors.paymentLink) shouldBe "(1 January to 31 March 2000 return)"
    }

    "have the correct link destination" in {
      element(Selectors.paymentLink).attr("href") shouldBe "/return/%23001"
    }
  }
}
