/*
 * Copyright 2020 HM Revenue & Customs
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

package views.partials.btaHome

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.partials.btaHome.PartialMigration

class PartialMigrationViewSpec extends ViewBaseSpec {

  val partialMigrationView: PartialMigration = injector.instanceOf[PartialMigration]
  "Rendering the partialMigration partial" should {

    object Selectors {
      val pageHeading = "h2"
      val paragraph1 = "p:nth-of-type(1)"
      val paragraph2 = "p:nth-of-type(2)"
      val paragraph3 = "p:nth-of-type(3)"
    }

    lazy val view = partialMigrationView()
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "Your Making Tax Digital for VAT account is being set up"
    }

    "have the correct recently signed up message on the page" in {
      elementText(Selectors.paragraph1) shouldBe
      "You were recently signed up to the new Making Tax Digital service. This is so you can submit your VAT Returns to HMRC using compatible software."
    }

    "have the correct setup time message on the page" in {
      elementText(Selectors.paragraph2) shouldBe "It will take up to 72 hours for your account to be set up."
    }

    "have the correct check again later message on the page" in {
      elementText(Selectors.paragraph3) shouldBe "Check back again later."
    }
  }
}
