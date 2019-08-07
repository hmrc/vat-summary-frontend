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

import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core.{AffinityGroup, AuthConnector}
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class SignOutControllerSpec extends ControllerBaseSpec {

  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
  implicit val ec: ExecutionContext = injector.instanceOf[ExecutionContext]
  val controller: SignOutController = new SignOutController(messages, mockEnrolmentsAuthService)

  def mockAuth(authResult: Option[AffinityGroup]): Any =
    (mockAuthConnector.authorise(_: Predicate, _: Retrieval[Option[AffinityGroup]])(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returns(Future.successful(authResult))

  "The .signOut action" when {

    "the user is authorised" when {

      "the user is an agent" should {

        lazy val result: Future[Result] = {
          mockAuth(Some(AffinityGroup.Agent))
          controller.signOut(authorised = true)(fakeRequestWithSession)
        }

        "return 303" in {
          status(result) shouldBe Status.SEE_OTHER
        }

        "redirect to the correct survey url" in {
          redirectLocation(result) shouldBe Some(mockAppConfig.signOutUrl("VATCA"))
        }
      }

      "the user is a principal entity" should {

        lazy val result: Future[Result] = {
          mockAuth(Some(AffinityGroup.Individual))
          controller.signOut(authorised = true)(fakeRequestWithSession)
        }

        "return 303" in {
          status(result) shouldBe Status.SEE_OTHER
        }

        "redirect to the correct survey url" in {
          redirectLocation(result) shouldBe Some(mockAppConfig.signOutUrl("VATC"))
        }
      }
    }

    "the user is unauthorised" should {

      lazy val result: Future[Result] = controller.signOut(authorised = false)(fakeRequestWithSession)

      "return 303" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect to the unauthorised sign out URL" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.unauthorisedSignOutUrl)
      }
    }
  }

  "The .timeout action" should {

    lazy val result: Future[Result] = controller.timeout(fakeRequestWithSession)


    "return 303" in {
      status(result) shouldBe Status.SEE_OTHER
    }

    "redirect to the unauthorised sign out URL" in {
      redirectLocation(result) shouldBe Some(mockAppConfig.unauthorisedSignOutUrl)
    }
  }
}
