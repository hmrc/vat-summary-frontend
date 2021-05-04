/*
 * Copyright 2021 HM Revenue & Customs
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

package config

import mocks.MockAppConfig
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalamock.scalatest.MockFactory
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.ViewBaseSpec
import views.html.errors.StandardError

class ServiceErrorHandlerSpec extends ViewBaseSpec with MockFactory with GuiceOneAppPerSuite{

  implicit val mockAppConfig: AppConfig = new MockAppConfig(app.configuration)
  implicit lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
  implicit val fakeError: StandardError = injector.instanceOf[StandardError]

  val service: ServiceErrorHandler = new ServiceErrorHandler(messagesApi,mockAppConfig, fakeError)

  "calling .notFoundTemplate " should {

    object Selectors {
      val pageHeading = "h1"
      val message = ".govuk-body-l"
    }
    lazy val view=service.notFoundTemplate
    lazy implicit val document: Document = Jsoup.parse(view.body)


    "display the correct title" in {
      document.title shouldBe "Page not found - VAT - GOV.UK"
    }

    "displays the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "This page cannot be found"
    }

    "displays the correct message" in {
      element(Selectors.message).text() shouldBe "Please check that you have entered the correct web address."
    }
  }
}
