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

import javax.inject.Inject

import play.api.http.Status
import play.api.mvc.{Action, AnyContent, Result}
import play.api.test.Helpers._
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

class AuthBaseSpec extends ControllerBaseSpec {

  protected class DummyController @Inject()(val authFunctions: AuthorisedFunctions, val authConnector: AuthConnector)
    extends FrontendController with AuthBase {

    def loadAction(): Action[AnyContent] = authorisedAction { implicit request =>
      Future.successful(Ok(""))
    }
  }

  private trait Test {
    val success: Boolean = true
    val mockAuthConnector: AuthConnector = mock[AuthConnector]

    def setup(): Any = if(success) {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(Future.successful(()))
    }
    else {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(Future.failed(MissingBearerToken()))
    }

    val mockAuthorisedFunctions: AuthorisedFunctions = new EnrolmentsAuthService(mockAuthConnector)

    def target: DummyController = {
      setup()
      new DummyController(mockAuthorisedFunctions, mockAuthConnector)
    }
  }

  "Calling the authorisedAction function" when {

    "the user is logged in" should {

      "return 200" in new Test {
        val result: Future[Result] = target.loadAction()(fakeRequest)
        status(result) shouldBe Status.OK
      }
    }

    "the user is not logged in" should {

      "return 303" in new Test {
        override val success: Boolean = false
        val result: Future[Result] = target.loadAction()(fakeRequest)
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect the user to the session timeout page" in new Test {
        override val success: Boolean = false
        val result: Future[Result] = target.loadAction()(fakeRequest)
        redirectLocation(result) shouldBe Some(routes.ErrorsController.sessionTimeout().url)
      }
    }
  }
}
