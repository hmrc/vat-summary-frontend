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

class NoPaymentsViewSpec extends ViewBaseSpec {

  object Selectors {
    val pageHeading = "h1"
    val secondaryHeading = "h2"
    val noPaymentsDetail = "#noPaymentsDetail"
    val paymentWaitingMessage = "#noPaymentsDetail p:nth-of-type(1)"
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

  "Rendering the no payments page" should {

    lazy val view = views.html.payments.noPayments(user)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "What you owe"
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "What you owe"
    }

    "have the correct secondary heading" in {
      elementText(Selectors.secondaryHeading) shouldBe "You don't owe anything right now."
    }

    "have the correct waiting information" in {
      elementText(Selectors.paymentWaitingMessage) shouldBe
        "If you've submitted a return and need to pay VAT, it can take up to 24 hours to see what you owe."
    }

    lazy val noPaymentDetails = element(Selectors.noPaymentsDetail)

    s"have the correct full link text" in {
      noPaymentDetails.select("p:nth-of-type(2)").text shouldBe "You can still make a payment (opens in a new tab)."
    }

    s"have the correct href" in {
      noPaymentDetails.select("p:nth-of-type(2) a").attr("href") shouldBe "#"
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

      "have the text 'What you owe'" in {
        elementText(Selectors.paymentBreadcrumb) shouldBe "What you owe"
      }
    }
  }

}
