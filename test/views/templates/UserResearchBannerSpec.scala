/*
 * Copyright 2019 HM Revenue & Customs
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

  mockConfig.features.userResearchBanner(true)

  object Selectors {
    val userResearchHeading = "h3.notice-banner__content"
    val userResearchSurvey = "a.notice-banner__content"
    val userResearchClose = "a.notice-banner__close"
    val userResearchCloseText = "a.notice-banner__close span:nth-of-type(1)"
    val userResearchCloseContext = "a.notice-banner__close span:nth-of-type(2)"
  }

  "The User Research banner" should {

    lazy val view = views.html.templates.userResearchBanner(mockConfig)
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

    "have an empty href for exiting in the banner" in {
      element(Selectors.userResearchClose).attr("href") shouldBe "#"
    }

    "have the correct close survey text" in {
      elementText(Selectors.userResearchCloseText) shouldBe "No thanks"
    }

    "have hidden context on the close survey link" in {
      elementText(Selectors.userResearchCloseContext) shouldBe "I do not want to enter the survey"
    }
  }
}
