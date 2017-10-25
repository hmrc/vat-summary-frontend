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

import play.api.mvc.{Action, AnyContent}
import play.api.test.FakeRequest
import services.AuthService
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedControllerSpec extends ControllerBaseSpec {

  private trait Test extends AuthenticatedController {
    override val authService: AuthService = mockAuthService

    val action: Action[AnyContent] = AuthenticatedAction {
      implicit req => user => Ok
    }
  }

  "Calling the .helloWorld action" when {

    "user is authorised" in new Test {
      val enrolments = Enrolments(
        Set(
          Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("", "")), "", ConfidenceLevel.L0)
        )
      )

      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[Enrolments])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(Future.successful(enrolments))

      val result = action(FakeRequest())

      status(result) shouldEqual 200
    }

    "user is not authorised" in new Test {
      val enrolments = Enrolments(Set.empty)

      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[Enrolments])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(Future.successful(enrolments))

      val result = action(FakeRequest())

      status(result) shouldEqual 303
    }

    "a BearerTokenExpired exception is thrown" in new Test {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[Enrolments])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(Future.failed(new BearerTokenExpired))

      val result = action(FakeRequest())

      status(result) shouldEqual 303
    }
  }
}
