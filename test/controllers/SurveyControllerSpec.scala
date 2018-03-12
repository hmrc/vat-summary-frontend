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

package controllers

import controllers.survey.SurveyController
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._

import scala.concurrent.Future


class SurveyControllerSpec extends ControllerBaseSpec {

  private trait SurveyControllerTest {
    def target: SurveyController = {
      new SurveyController(messages, mockAuditingService, mockAppConfig)
    }
  }

  "navigating to survey page" should {
    "return ok and show the survey page" in new SurveyControllerTest {
      lazy val request = fakeRequest
      val result: Result = await(target.yourJourney()(fakeRequest))
      status(result) shouldBe Status.OK
      val document: Document = Jsoup.parse(bodyOf(result))

      document.select("h1").first().text() shouldBe "About your journey"
    }

  }

  "posting empty survey data" should {
    "redirect to the thankyou end survey page without error as survey questins are optional" in new SurveyControllerTest {
      lazy val request = fakeRequestToPOSTWithSession(
        ("anyApplicable", ""),
        ("choice1", ""),
        ("choice2", ""),
        ("choice3", ""),
        ("choice4", ""),
        ("choice5", ""),
        ("choice5", ""))
      lazy val result: Future[Result] = target.submit()(request)

      status(result) shouldBe Status.SEE_OTHER
      redirectLocation(result) shouldBe Some(mockAppConfig.surveyThankYouUrl)
    }

  }

}