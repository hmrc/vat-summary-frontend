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

package controllers

import javax.inject.{Inject, Singleton}

import config.AppConfig
import controllers.auth.actions.VatUserAction
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.api.test.Helpers._
import services.AuthService
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

class AuthorisedActionsSpec extends ControllerBaseSpec {

  private trait Test {
    val enrolments: Enrolments
    val mockAuthConnector: AuthConnector = mock[AuthConnector]

    def setup() {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[Enrolments])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(Future.successful(enrolments))
    }

    val mockAuthorisedFunctions: AuthorisedFunctions = new AuthService(mockAuthConnector)

    def target: TestAuthorisedActionsController = {
      setup()
      new TestAuthorisedActionsController(mockAppConfig, messages, mockAuthorisedFunctions)
    }
  }

  "Calling the .authorisedAction action" when {

    "user is authorised" should {

      val goodEnrolments = Enrolments(
        Set(
          Enrolment("HMRC-MTD-VAT",
            Seq(EnrolmentIdentifier("", "")),
            "",
            ConfidenceLevel.L0)
        )
      )

      "return 200" in new Test {
        override val enrolments: Enrolments = goodEnrolments
        val result = target.authorisedActions(fakeRequest)

        status(result) shouldEqual 200
      }
    }

    "user is not authorised" should {

      val noEnrolments = Enrolments(Set.empty)

      "return 303" in new Test {
        val enrolments: Enrolments = noEnrolments
        val result = target.authorisedActions(fakeRequest)

        status(result) shouldEqual 303
      }

      "redirect the user to the unauthorised page" in new Test {
        val enrolments: Enrolments = noEnrolments
        val result = target.authorisedActions(fakeRequest)

        redirectLocation(result) shouldBe Some(routes.ErrorsController.unauthorised().url)
      }
    }
  }
}

@Singleton
class TestAuthorisedActionsController @Inject()(val appConfig: AppConfig,
                                                val messagesApi: MessagesApi,
                                                val authFunctions: AuthorisedFunctions)
  extends FrontendController with VatUserAction with I18nSupport {

  val authorisedActions: Action[AnyContent] = VatUserAction.async { implicit request => implicit user =>
    Future.successful(Ok)
  }
}
