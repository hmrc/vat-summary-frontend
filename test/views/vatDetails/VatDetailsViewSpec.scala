/*
 * Copyright 2017 HM Revenue & Customs
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

import models.{Obligation, User}
import java.time.LocalDate

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class VatDetailsViewSpec extends ViewBaseSpec {

  object Selectors {
    val pageHeading = "h1"
    val instructions = "p"
    val nextReturnHeading = ".divider--bottom h2"
  }

  private val date = LocalDate.now()
  private val user = User("1111")
  val obligation = Obligation(date, date, date, "", None, "")

  "Rendering the VAT details page" should {

    lazy val view = views.html.vatDetails.details(user, Some(obligation))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "Your VAT"
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "Your VAT VAT registration number (VRN): 1111"
    }
  }

  "Rendering the VAT details page with an obligation" should {

    lazy val view = views.html.vatDetails.details(user, Some(obligation))
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the obligation section" in {
      elementText(Selectors.nextReturnHeading) shouldBe "Next return due"
    }
  }

  "Rendering the VAT details page with no obligation" should {

    lazy val view = views.html.vatDetails.details(user, None)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the no obligation message" in {
      elementText(Selectors.nextReturnHeading) shouldBe "No obligation due"
    }
  }
}
