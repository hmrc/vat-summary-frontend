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

package controllers.auth

import common.Constants
import controllers.auth.AuthPredicate._
import controllers.auth.AuthPredicates._
import mocks.MockAppConfig
import org.scalatest.EitherValues
import play.api.inject.Injector
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.{ConfidenceLevel, Enrolment, EnrolmentIdentifier, Enrolments}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import uk.gov.hmrc.http.SessionKeys.{authToken, lastRequestTimestamp}
import play.api.test.Helpers._

class AuthPredicateSpec extends UnitSpec with WithFakeApplication with EitherValues {

  lazy val injector: Injector = fakeApplication.injector
  lazy val mockAppConfig: MockAppConfig = new MockAppConfig

  val userWithMtdVatEnrolment = User(
    Enrolments(
      Set(
        Enrolment(
          Constants.VAT_ENROLMENT_KEY, Seq(EnrolmentIdentifier("", "")), "", ConfidenceLevel.L0
        )
      )
    )
  )

  val blankUser = User(
    Enrolments(Set.empty)
  )

  "Timeout predicate" when {

    "lastRequestTimestamp and authToken are not set" should {
      lazy val predicate = timeoutPredicate(FakeRequest())(blankUser)

      "return Success" in {
        predicate.right.value shouldBe Success
      }
    }

    "lastRequestTimestamp and authToken are set" should {
      lazy val request = FakeRequest().withSession(
        authToken -> "authToken",
        lastRequestTimestamp -> "lastRequestTimestamp"
      )

      lazy val predicate = timeoutPredicate(request)(blankUser)

      "return Success" in {
        predicate.right.value shouldBe Success
      }
    }

    "lastRequestTimestamp is set and authToken is not" should {
      lazy val request = FakeRequest().withSession(
        lastRequestTimestamp -> "lastRequestTimestamp"
      )

      lazy val predicate = timeoutPredicate(request)(blankUser)
      lazy val result = predicate.left.value

      "return 303" in {
        status(result) shouldBe 303
      }

      s"redirect to ${controllers.routes.SessionTimeoutController.timeout().url}" in {
        redirectLocation(result) shouldBe Some(controllers.routes.SessionTimeoutController.timeout().url)
      }
    }
  }

  "Enrolled predicate" when {

    "mtdVatId is not empty" should {
      lazy val predicate = enrolledPredicate(FakeRequest())(userWithMtdVatEnrolment)

      "return Success" in {
        predicate.right.value shouldBe Success
      }
    }

    "mtdVatId is empty" should {
      lazy val predicate = enrolledPredicate(FakeRequest())(blankUser)
      lazy val result = predicate.left.value

      "return 303" in {
        status(result) shouldBe 303
      }

      s"redirect to ${controllers.routes.NotEnrolledController.show().url}" in {
        redirectLocation(result) shouldBe Some(controllers.routes.NotEnrolledController.show().url)
      }
    }
  }

  "Predicates" when {

    "Timeout predicate and enrolled predicate fail" should {
      lazy val request = FakeRequest().withSession(
        lastRequestTimestamp -> "lastRequestTimestamp"
      )

      lazy val predicate = predicates(request)(blankUser)
      lazy val result = predicate.left.value

      "return 303" in {
        status(result) shouldBe 303
      }

      s"redirect to ${controllers.routes.SessionTimeoutController.timeout().url}" in {
        redirectLocation(result) shouldBe Some(controllers.routes.SessionTimeoutController.timeout().url)
      }
    }

    "Both predicates pass" should {
      lazy val request = FakeRequest()
      lazy val predicate = predicates(request)(userWithMtdVatEnrolment)

      "return Success" in {
        predicate.right.value shouldBe Success
      }
    }

    "One predicate fails" should {
      lazy val request = FakeRequest()
      lazy val predicate = predicates(request)(blankUser)
      lazy val result = predicate.left.value

      "return 303" in {
        status(result) shouldBe 303
      }

      s"redirect to ${controllers.routes.NotEnrolledController.show().url}" in {
        redirectLocation(result) shouldBe Some(controllers.routes.NotEnrolledController.show().url)
      }
    }
  }
}
