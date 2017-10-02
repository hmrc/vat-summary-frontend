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
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.{ConfidenceLevel, Enrolment, EnrolmentIdentifier, Enrolments}
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier
import scala.concurrent.{ExecutionContext, Future}

class HelloWorldControllerSpec extends ControllerBaseSpec {

  def setupController(enrolments: Enrolments): HelloWorldController = {

    (mockAuthConnector.authorise(_: Predicate, _: Retrieval[Enrolments])(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returns(Future.successful(enrolments))

    new HelloWorldController(messages, mockAuthService, mockAppConfig)
  }

  "Calling the .helloWorld action" when {

    "user is authenticated" should {

      val enrolments = Enrolments(
        Set(
          Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("", "")), "", ConfidenceLevel.L0)
        )
      )

      lazy val target = setupController(enrolments)
      lazy val result = target.helloWorld()(FakeRequest())

      "return 200" in {
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }
    }

    "user is unauthenticated" should {

      val enrolments = Enrolments(
        Set.empty
      )

      lazy val target = setupController(enrolments)
      lazy val result = target.helloWorld()(FakeRequest())

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }
    }
  }
}
