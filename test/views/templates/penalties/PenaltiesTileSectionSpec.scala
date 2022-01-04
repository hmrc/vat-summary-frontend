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

package views.templates.penalties

import org.jsoup.Jsoup
import views.ViewBaseSpec
import views.html.templates.penalties.PenaltiesTileSection

class PenaltiesTileSectionSpec extends ViewBaseSpec {

  val penaltiesTileSection: PenaltiesTileSection = injector.instanceOf[PenaltiesTileSection]

  "PenaltiesTileSection View" should {
    lazy val view = penaltiesTileSection()(messages, mockConfig)
    lazy implicit val document = Jsoup.parse(view.body)


    "have the correct title" in {
      elementText("#penalties-heading") shouldBe "Penalties and appeals"
    }

    "have the correct link to the penalties-frontend service" in {
      element("#penalties-link").attr("href") shouldBe "/vat-through-software/test-only/penalties-stub"
    }

    "have the correct card paragraph" in {
      elementText("#penalties-section-body") shouldBe "View your penalties, make an appeal against a " +
        "penalty and see the status of any current appeals."
    }

  }
}
