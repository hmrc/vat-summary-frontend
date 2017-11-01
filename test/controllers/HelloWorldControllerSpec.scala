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

import play.api.http.Status
import play.api.test.Helpers._
import services.AuthService
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class HelloWorldControllerSpec extends ControllerBaseSpec {

  private trait Test {
    val enrolments: Enrolments
    val mockAuthConnector: AuthConnector = mock[AuthConnector]

    def setup() {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[Enrolments])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(Future.successful(enrolments))
    }

    val mockAuthorisedFunctions: AuthorisedFunctions = new AuthService(mockAuthConnector)

    def target: HelloWorldController = {
      setup()
      new HelloWorldController(messages, mockAuthorisedFunctions, mockAppConfig)
    }
  }

  "Calling the .helloWorld action" when {

    "user is authenticated" should {

      val goodEnrolments: Enrolments = Enrolments(
        Set(
          Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("", "VRN1234567890")), "", ConfidenceLevel.L0)
        )
      )

      "return 200" in new Test {
        override val enrolments: Enrolments = goodEnrolments
        val result = target.helloWorld(fakeRequest)

        status(result) shouldBe Status.OK
      }

      "return HTML" in new Test {
        override val enrolments: Enrolments = goodEnrolments
        val result = target.helloWorld(fakeRequest)

        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }
    }

    "user is unauthenticated" should {

      val noEnrolments = Enrolments(Set.empty)

      "return 303" in new Test {
        val enrolments: Enrolments = noEnrolments
        val result = target.helloWorld(fakeRequest)

        status(result) shouldEqual 303
      }

      "redirect the user to the unauthorised page" in new Test {
        val enrolments: Enrolments = noEnrolments
        val result = target.helloWorld(fakeRequest)

        redirectLocation(result) shouldBe Some(routes.ErrorsController.unauthorised().url)
      }
    }
  }
}
