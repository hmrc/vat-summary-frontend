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

import audit.AuditingService
import audit.models.ExitSurveyAuditing.ExitSurveyAuditModel
import config.FrontendAuditConnector
import controllers.survey.SurveyController
import models.SurveyJourneyModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult.Success
import uk.gov.hmrc.play.audit.model.DataEvent

import scala.concurrent.{ExecutionContext, Future}


class SurveyControllerSpec extends ControllerBaseSpec {

  private trait SurveyControllerTest {
    lazy val mockAuditConnector: FrontendAuditConnector = mock[FrontendAuditConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()

    lazy val mockAuditingService: AuditingService = new AuditingService(mockAppConfig, mockAuditConnector)

    val testModel: ExitSurveyAuditModel = ExitSurveyAuditModel(SurveyJourneyModel(Some("yes"),
      Some(true), Some(true), Some(true), Some(true), Some(true), Some(true)))
    val expectedData: DataEvent = mockAuditingService.toDataEvent(mockAppConfig.contactFormServiceIdentifier,
      testModel, controllers.survey.routes.SurveyController.yourJourney().url)

    def target: SurveyController = {

      (mockAuditConnector.sendEvent(_: DataEvent)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(argThat[DataEvent](_.tags == expectedData.tags), *, *)
        .noMoreThanOnce()
        .returns(Future.successful(Success))

      new SurveyController(messages, mockAuditingService, mockAppConfig)
    }
  }

  "navigating to survey page" should {
    "return ok and show the survey page" in new SurveyControllerTest {
      lazy val request: FakeRequest[AnyContentAsEmpty.type] = fakeRequest
      val result: Result = await(target.yourJourney()(fakeRequest))
      status(result) shouldBe Status.OK
      val document: Document = Jsoup.parse(bodyOf(result))

      document.select("h1").first().text() shouldBe "About your journey"
    }

  }

  "posting valid survey data" should {
    "redirect to the thankyou end survey page" in new SurveyControllerTest {

      lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequestToPOSTWithSession(
        ("anyApplicable", "yes"),
        ("choice1", "true"),
        ("choice2", "true"),
        ("choice3", "true"),
        ("choice4", "true"),
        ("choice5", "true"),
        ("choice6", "true"))
      lazy val result: Future[Result] = target.submit()(request)

      status(result) shouldBe Status.SEE_OTHER

      redirectLocation(result) shouldBe Some(mockAppConfig.surveyThankYouUrl)
    }

  }

  "posting invalid survey data" should {
    "return a bad request" in new SurveyControllerTest {

      lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequestToPOSTWithSession(
        ("anyApplicable", "yes"),
        ("choice1", "this is bad"),
        ("choice2", "this is bad"),
        ("choice3", "this is bad"),
        ("choice4", "this is bad"),
        ("choice5", "this is bad"),
        ("choice6", "this is bad"))
      lazy val result: Future[Result] = target.submit()(request)

      status(result) shouldBe Status.BAD_REQUEST
    }

  }

  "posting empty survey data" should {
    "redirect to the thankyou end survey page without error as survey questions are optional" in new SurveyControllerTest {

      lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequestToPOSTWithSession()
      lazy val result: Future[Result] = target.submit()(request)

      status(result) shouldBe Status.SEE_OTHER

      redirectLocation(result) shouldBe Some(mockAppConfig.surveyThankYouUrl)
    }

  }

}