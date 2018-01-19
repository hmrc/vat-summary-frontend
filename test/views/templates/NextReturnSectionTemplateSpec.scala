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

import models.VatReturnObligation
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class NextReturnSectionTemplateSpec extends ViewBaseSpec {

  "nextReturnSection template" when {

    object Selectors {
      val nextReturnDueHeading = "h2:nth-of-type(1)"
      val nextReturnDate = "p:nth-of-type(1)"
      val viewReturnsButton = "a:nth-of-type(1)"
    }

    "there is an VAT return to display" should {

      val obligation = VatReturnObligation(
        start = LocalDate.parse("2017-01-01"),
        end = LocalDate.parse("2017-03-31"),
        due = LocalDate.parse("2017-04-30"),
        "O",
        None,
        "#001"
      )

      lazy val view = views.html.templates.nextReturnSection(Some(obligation))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the 'Next return due' heading" in {
        elementText(Selectors.nextReturnDueHeading) shouldBe "Next return due"
      }

      "display the due of the return" in {
        elementText(Selectors.nextReturnDate) shouldBe "30 April 2017"
      }

      "display the 'View return deadlines' link" in {
        elementText(Selectors.viewReturnsButton) shouldBe "View your returns"
      }
    }

    "there is no VAT return to display" should {

      lazy val view = views.html.templates.nextReturnSection(None)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the 'No return due' heading" in {
        elementText(Selectors.nextReturnDueHeading) shouldBe "No return due"
      }

    }


  }
}
