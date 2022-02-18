/*
 * Copyright 2022 HM Revenue & Customs
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

package views.errors

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.errors.PaymentsError

class PaymentsErrorViewSpec extends ViewBaseSpec {

  val paymentsErrorView: PaymentsError = injector.instanceOf[PaymentsError]
  object Selectors {
    val breadcrumb = ".govuk-breadcrumbs__list-item > a"
    val breadcrumb2 = ".govuk-breadcrumbs__list-item:nth-child(2) > a"
    val heading = "h1"
    val payNow = "#pay-now-content"
    val payNowLink = s"$payNow > a"
  }

  "Rendering the payments error page" should {

    lazy val view = paymentsErrorView()
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have breadcrumbs" which {

      "has a link to BTA" which {

        "has the correct text" in {
          elementText(Selectors.breadcrumb) shouldBe "Business tax account"
        }

        "has the correct href" in {
          element(Selectors.breadcrumb).attr("href") shouldBe mockConfig.btaHomeUrl
        }
      }

      "has a link to the VAT overview" which {

        "has the correct text" in {
          elementText(Selectors.breadcrumb2) shouldBe "Your VAT account"
        }

        "has the correct href" in {
          element(Selectors.breadcrumb2).attr("href") shouldBe controllers.routes.VatDetailsController.details.url
        }
      }

    }

    "have the correct document title" in {
      document.title shouldBe "There is a problem with the service - Manage your VAT account - GOV.UK"
    }

    "have the correct page heading" in {
      elementText(Selectors.heading) shouldBe "Sorry, there is a problem with the service"
    }

    "have the correct pay now message" in {
      elementText(Selectors.payNow) shouldBe "If you know how much you owe, you can still pay now."
    }

    "have the correct pay now link" in {
      element(Selectors.payNowLink).attr("href") shouldBe "unauthenticated-payments-url"
    }

  }
}
