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

package audit

import _root_.models.SurveyJourneyModel
import audit.models.ExitSurveyAuditing.ExitSurveyAuditModel
import config.FrontendAuditConnector
import controllers.ControllerBaseSpec
import org.scalatest.BeforeAndAfterEach
import uk.gov.hmrc.play.audit.http.connector.AuditResult.Success

import scala.concurrent.{ExecutionContext, Future}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.model.DataEvent

import scala.concurrent.ExecutionContext.Implicits.global

class AuditingServiceSpec extends ControllerBaseSpec with BeforeAndAfterEach {

  private trait Test {

    lazy val mockAuditConnector: FrontendAuditConnector = mock[FrontendAuditConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()

    def setupMocks(): Unit

    def target(): AuditingService = {
      setupMocks()
      new AuditingService(mockAppConfig, mockAuditConnector)
    }
  }

  "audit" when {

    "given a ExitSurveyAuditModel with fully populated form data" should {
      "extract the data and pass it into the AuditConnector" in new Test {

        val testModel = ExitSurveyAuditModel(SurveyJourneyModel(Some("true"), Some(true), Some(true), Some(true), Some(true), Some(true), Some(true)))
        val expectedData: DataEvent = target().toDataEvent(mockAppConfig.contactFormServiceIdentifier, testModel,
          controllers.survey.routes.SurveyController.yourJourney().url)

        override def setupMocks(): Unit = {
          (mockAuditConnector.sendEvent(_: DataEvent)(_: HeaderCarrier, _: ExecutionContext))
            .stubs(argThat[DataEvent](_.tags == expectedData.tags), *, *)
            .noMoreThanOnce()
            .returns(Future.successful(Success))
        }

        target().audit(testModel, controllers.survey.routes.SurveyController.yourJourney().url)
      }
    }

    "given a ExitSurveyAuditModel with yes/no only data" should {
      "extract the data and pass it into the AuditConnector" in new Test {

        val testModel = ExitSurveyAuditModel(SurveyJourneyModel(Some("true"), None, None, None, None, None, None))
        val expectedData: DataEvent = target().toDataEvent(mockAppConfig.contactFormServiceIdentifier, testModel,
          controllers.survey.routes.SurveyController.yourJourney().url)

        override def setupMocks(): Unit = {
          (mockAuditConnector.sendEvent(_: DataEvent)(_: HeaderCarrier, _: ExecutionContext))
            .stubs(argThat[DataEvent](_.tags == expectedData.tags), *, *)
            .noMoreThanOnce()
            .returns(Future.successful(Success))
        }

        target().audit(testModel, controllers.survey.routes.SurveyController.yourJourney().url)
      }
    }

    "given a ExitSurveyAuditModel with no data" should {
      "extract the data and pass it into the AuditConnector" in new Test {

        val testModel = ExitSurveyAuditModel(SurveyJourneyModel(None, None, None, None, None, None, None))
        val expectedData: DataEvent = target().toDataEvent(mockAppConfig.contactFormServiceIdentifier, testModel,
          controllers.survey.routes.SurveyController.yourJourney().url)

        override def setupMocks(): Unit = {
          (mockAuditConnector.sendEvent(_: DataEvent)(_: HeaderCarrier, _: ExecutionContext))
            .stubs(argThat[DataEvent](_.tags == expectedData.tags), *, *)
            .noMoreThanOnce()
            .returns(Future.successful(Success))
        }

        target().audit(testModel, controllers.survey.routes.SurveyController.yourJourney().url)
      }
    }

  }
}
