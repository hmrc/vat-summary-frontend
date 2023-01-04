/*
 * Copyright 2023 HM Revenue & Customs
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

class DirectDebitErrorViewSpec extends ViewBaseSpec {

  val paymentsErrorView: PaymentsError = injector.instanceOf[PaymentsError]
  object Selectors {
    val heading = "h1"
    val message = "p:nth-of-type(1)"
  }

  "Rendering the payments error page" should {

    lazy val view = paymentsErrorView()
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "There is a problem with the service - Manage your VAT account - GOV.UK"
    }

    "have the correct page heading" in {
      elementText(Selectors.heading) shouldBe "Sorry, there is a problem with the service"
    }
  }
}
