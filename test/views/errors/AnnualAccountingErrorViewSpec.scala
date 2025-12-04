/*
 * Copyright 2025 HM Revenue & Customs
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
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.errors.AnnualAccountingError

class AnnualAccountingErrorViewSpec extends ViewBaseSpec {

  val view: AnnualAccountingError = injector.instanceOf[AnnualAccountingError]

  "AnnualAccountingError view" should {
    "render the correct heading and link" in {
      val html: Html = view()(
        mockConfig,
        request,
        messages,
        user
      )
      implicit val doc: Document = Jsoup.parse(html.body)
      doc.select("h1").text() shouldBe "Sorry, there is a problem with the service"
      doc.select(".govuk-grid-column-two-thirds a.govuk-link").attr("href") shouldBe controllers.routes.VatDetailsController.details.url
    }
  }
}


