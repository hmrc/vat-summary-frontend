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

package testOnly.controllers

import controllers.ControllerBaseSpec
import play.api.http.Status
import play.api.mvc.{AnyContent, Request, Result}
import play.api.test.Helpers._
import play.twirl.api.Html
import services.EnrolmentsAuthService
import testOnly.services.BtaStubService
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class BtaStubControllerSpec extends ControllerBaseSpec {

  private trait Test {
    val runMock: Boolean = true
    val authResult: Future[_]
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockService: BtaStubService = mock[BtaStubService]

    def setup(): Any ={
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(authResult)

      if(runMock) {(mockService.getPartial(_: String)(_: Request[AnyContent]))
        .expects(*, *)
        .returns(Future.successful(Html("Some HTML")))
      }
    }

    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)

    def target: BtaStubController = {
      setup()
      new BtaStubController(messages, mockEnrolmentsAuthService, mockService, mockAppConfig)
    }
  }

  "Calling the landingPage action" when {

    "the user is logged in" should {

      "return 200" in new Test {
        override val authResult: Future[Unit] = Future.successful(())
        private val result = target.viewVat()(fakeRequest)
        status(result) shouldBe Status.OK
      }

      "return HTML" in new Test {
        override val authResult: Future[Unit] = Future.successful(())
        private val result = target.viewVat()(fakeRequest)
        contentType(result) shouldBe Some("text/html")
      }

      "return charset utf-8" in new Test {
        override val authResult: Future[Unit] = Future.successful(())
        private val result = target.viewVat()(fakeRequest)
        charset(result) shouldBe Some("utf-8")
      }
    }

    "the user is not logged in" should {

      "return 401 (Unauthorised)" in new Test {
        override val runMock: Boolean = false
        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())
        val result: Future[Result] = target.viewVat()(fakeRequest)
        status(result) shouldBe Status.UNAUTHORIZED
      }
    }
  }
}
