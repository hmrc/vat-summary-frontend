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

package views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.html.MainTemplate

class MainTemplateSpec extends ViewBaseSpec {

  val injectedView: MainTemplate = injector.instanceOf[MainTemplate]

  object Selectors {
    val pageTitle = ".hmrc-header__service-name"
  }

  "The MainTemplate" when {

    "the user is an individual or organisation" should {

      lazy val view = injectedView(title = "Title of page", appConfig = mockConfig, user = Some(user))(Html("Test"))(request, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the client nav title" in {
        elementText(Selectors.pageTitle) shouldBe "Manage your VAT account"
      }

      "have the correct service URL" in {
        element(".hmrc-header__service-name").attr("href") shouldBe controllers.routes.VatDetailsController.details.url
      }
    }

    "the user is an agent" should {

      lazy val view = injectedView(title = "Title of page", appConfig = mockConfig, user = Some(agentUser))(Html("Test"))(request, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct agent title" in {

        elementText(Selectors.pageTitle) shouldBe "Your client’s VAT details"
      }

      "have the correct service URL" in {
        element(".hmrc-header__service-name").attr("href") shouldBe mockConfig.agentClientLookupHubUrl
      }
    }

    "the user is not known" should {

      lazy val view = injectedView(title = "Title of page", appConfig = mockConfig, user = None)(Html("Test"))(request, messages)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have no nav title" in {
        elementText(Selectors.pageTitle) shouldBe "VAT"
      }

      "have the correct service URL" in {
        element(".hmrc-header__service-name").attr("href") shouldBe ""
      }
    }
  }
}
