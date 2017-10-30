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

import controllers.auth.AuthPredicate._
import controllers.auth.AuthPredicates._
import mocks.MockAppConfig
import org.scalatest.EitherValues
import play.api.inject.Injector
import play.api.test.FakeRequest
import uk.gov.hmrc.auth.core.{ConfidenceLevel, Enrolment, EnrolmentIdentifier, Enrolments}
import uk.gov.hmrc.play.test._
import uk.gov.hmrc.http.SessionKeys.{authToken, lastRequestTimestamp}
import play.api.test.Helpers._
import common.EnrolmentKeys._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc.AnyContentAsEmpty

class AuthPredicatesSpec extends UnitSpec with GuiceOneAppPerSuite with EitherValues {

  lazy val injector: Injector = fakeApplication.injector
  lazy val mockAppConfig: MockAppConfig = new MockAppConfig

  implicit lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  val userWithMtdVatEnrolment = User(
    Enrolments(
      Set(
        Enrolment(
          VAT_ENROLMENT_KEY, Seq(EnrolmentIdentifier("", "")), "", ConfidenceLevel.L0
        )
      )
    )
  )

  val blankUser = User(
    Enrolments(Set.empty)
  )

  "Timeout predicate" when {

    "lastRequestTimestamp and authToken are not set" should {
      lazy val predicate = timeoutPredicate(fakeRequest)(blankUser)

      "return Success" in {
        predicate.right.value shouldBe Success
      }
    }

    "lastRequestTimestamp and authToken are set" should {
      lazy val request = fakeRequest.withSession(
        authToken -> "authToken",
        lastRequestTimestamp -> "lastRequestTimestamp"
      )

      lazy val predicate = timeoutPredicate(request)(blankUser)

      "return Success" in {
        predicate.right.value shouldBe Success
      }
    }

    "lastRequestTimestamp is set and authToken is not" should {
      lazy val request = fakeRequest.withSession(
        lastRequestTimestamp -> "lastRequestTimestamp"
      )

      lazy val predicate = timeoutPredicate(request)(blankUser)
      lazy val result = predicate.left.value

      "return 303" in {
        status(result) shouldBe 303
      }

      s"redirect to ${controllers.routes.ErrorsController.sessionTimeout().url}" in {
        redirectLocation(result) shouldBe Some(controllers.routes.ErrorsController.sessionTimeout().url)
      }
    }
  }

  "Authorised predicate" when {

    "Vrn is not empty" should {
      lazy val predicate = enrolledPredicate(fakeRequest)(userWithMtdVatEnrolment)

      "return Success" in {
        predicate.right.value shouldBe Success
      }
    }

    "Vrn is empty" should {
      lazy val predicate = enrolledPredicate(fakeRequest)(blankUser)
      lazy val result = predicate.left.value

      "return 303" in {
        status(result) shouldBe 303
      }

      s"redirect to ${controllers.routes.ErrorsController.unauthorised().url}" in {
        redirectLocation(result) shouldBe Some(controllers.routes.ErrorsController.unauthorised().url)
      }
    }
  }

  "Predicates" when {

    "Timeout predicate and authorised predicate fail" should {
      lazy val request = fakeRequest.withSession(
        lastRequestTimestamp -> "lastRequestTimestamp"
      )

      lazy val predicate = enrolledUserPredicate(request)(blankUser)
      lazy val result = predicate.left.value

      "return 303" in {
        status(result) shouldBe 303
      }

      s"redirect to ${controllers.routes.ErrorsController.sessionTimeout().url}" in {
        redirectLocation(result) shouldBe Some(controllers.routes.ErrorsController.sessionTimeout().url)
      }
    }

    "Both predicates pass" should {
      lazy val request = fakeRequest
      lazy val predicate = enrolledUserPredicate(request)(userWithMtdVatEnrolment)

      "return Success" in {
        predicate.right.value shouldBe Success
      }
    }

    "One predicate fails" should {
      lazy val request = fakeRequest
      lazy val predicate = enrolledUserPredicate(request)(blankUser)
      lazy val result = predicate.left.value

      "return 303" in {
        status(result) shouldBe 303
      }

      s"redirect to ${controllers.routes.ErrorsController.unauthorised().url}" in {
        redirectLocation(result) shouldBe Some(controllers.routes.ErrorsController.unauthorised().url)
      }
    }
  }
}
