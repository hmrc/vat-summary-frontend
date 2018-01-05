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

package views.vatDetails

import models.{User, VatDetailsModel}
import java.time.LocalDate

import models.obligations.Obligation
import models.payments.Payment
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec

class VatDetailsViewSpec extends ViewBaseSpec {

  object Selectors {
    val pageHeading = "h1"
    val nextPaymentHeading = "h2#payment-header"
    val nextReturnHeading = "h2#return-header"
    val accountDetailsHeading = "h3 a"
    val header = "div.test"
  }

  private val date = LocalDate.now()
  private val user = User("1111")
  val obligation = Obligation(date, date, date, "", None, "")
  val payment = Payment(date, date, BigDecimal(100), "", "")

  "Rendering the VAT details page" should {

    lazy val view = views.html.vatDetails.details(user, VatDetailsModel(Some(obligation), Some(payment)))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "VAT"
    }

    "have the account details section" in {
      elementText(Selectors.accountDetailsHeading) shouldBe "VAT account details"
    }
  }

  "Rendering the VAT details page with a next return and a next payment" should {

    lazy val view = views.html.vatDetails.details(user, VatDetailsModel(Some(obligation), Some(payment)))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the next return section" in {
      elementText(Selectors.nextReturnHeading) shouldBe "Next return due"
    }

    "render the next payment section" in {
      elementText(Selectors.nextPaymentHeading) shouldBe "Next payment due"
    }
  }

  "Rendering the VAT details page without a next return or next payment" should {

    lazy val view = views.html.vatDetails.details(user, VatDetailsModel(None, None))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the no return message" in {
      elementText(Selectors.nextReturnHeading) shouldBe "No return due"
    }

    "render the no payment message" in {
      elementText(Selectors.nextPaymentHeading) shouldBe "No payment due"
    }
  }

  "Rendering the VAT details page with a header" should {

    val basicHeaderHtml: Html = Html("""<div class="test">Example</div>""")
    lazy val view = views.html.vatDetails.details(user, VatDetailsModel(Some(obligation), Some(payment)), basicHeaderHtml)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the header" in {
      elementText(Selectors.header) shouldBe "Example"
    }
  }
}
