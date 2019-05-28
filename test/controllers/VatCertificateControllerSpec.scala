/*
 * Copyright 2019 HM Revenue & Customs
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

import controllers.predicates.HybridUserPredicate
import play.api.http.Status
import play.api.test.Helpers._
import services.{AccountDetailsService, EnrolmentsAuthService}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class VatCertificateControllerSpec extends ControllerBaseSpec {

  private trait Test {
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
    val mockAccountDetailsService: AccountDetailsService = mock[AccountDetailsService]
    val mockHybridUserPredicate: HybridUserPredicate = new HybridUserPredicate(mockAccountDetailsService)
    val mockAuthorisedController: AuthorisedController = new AuthorisedController(
      messages,
      mockEnrolmentsAuthService,
      mockHybridUserPredicate,
      mockAppConfig
    )

    val authResult: Future[_] =
      Future.successful(Enrolments(Set(
        Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VRN", "123456789")), "")
      )))

    def setup(): Any = {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returns(authResult)
    }

    def target: VatCertificateController = {
      setup()
      new VatCertificateController(messages, mockAuthorisedController)
    }
  }

  "The show() action" when {

    "the user is logged in with valid credentials" should {

      "return OK (200)" in new Test {
        private val result = target.show()(fakeRequest)
        status(result) shouldBe Status.OK
      }

      "return HTML" in new Test {
        private val result = target.show()(fakeRequest)
        contentType(result) shouldBe Some("text/html")
      }
    }

    "the user is logged in with invalid credentials" should {

      "return Forbidden (403)" in new Test {
        override val authResult: Future[_] = Future.failed(InsufficientEnrolments())
        private val result = target.show()(fakeRequest)
        status(result) shouldBe Status.FORBIDDEN
      }

      "return HTML" in new Test {
        override val authResult: Future[_] = Future.failed(InsufficientEnrolments())
        private val result = target.show()(fakeRequest)
        contentType(result) shouldBe Some("text/html")
      }
    }

    "the user is not logged in" should {

      "return Unauthorised (401)" in new Test {
        override val authResult: Future[_] = Future.failed(MissingBearerToken())
        private val result = target.show()(fakeRequest)
        status(result) shouldBe Status.UNAUTHORIZED
      }

      "return HTML" in new Test {
        override val authResult: Future[_] = Future.failed(MissingBearerToken())
        private val result = target.show()(fakeRequest)
        contentType(result) shouldBe Some("text/html")
      }
    }
  }
}
