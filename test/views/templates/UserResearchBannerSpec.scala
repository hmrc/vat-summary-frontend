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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class UserResearchBannerSpec extends ViewBaseSpec {

  object Selectors {
    val userResearchHeading = "h3.notice-banner__content"
    val userResearchSurvey = "a.notice-banner__content"
    val userResearchClose = ".notice-banner__close"
  }

  "The User Research banner" should {

    lazy val view = views.html.templates.userResearchBanner()
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct heading message" in {
      elementText(Selectors.userResearchHeading) shouldBe "Please help us improve our service"
    }

    "have the correct text to enter the survey" in {
      elementText(Selectors.userResearchSurvey) shouldBe "Enter our survey"
    }

    "have the correct link to the survey" in {
      element(Selectors.userResearchSurvey).attr("href") shouldBe
        "https://signup.take-part-in-research.service.gov.uk/?utm_campaign=VATviewchange&utm_source=Other&utm_medium=other&t=HMRC&id=34"
    }

    "have the correct rejection text" in {
      elementText(Selectors.userResearchClose) shouldBe "No thanks"
    }
  }
}
