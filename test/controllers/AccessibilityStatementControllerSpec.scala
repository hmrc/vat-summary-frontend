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

import common.TestModels._
import controllers.predicates.{AgentPredicate, HybridUserPredicate}
import play.api.http.Status
import services.{AccountDetailsService, EnrolmentsAuthService}
import uk.gov.hmrc.auth.core.AffinityGroup.Individual
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.auth.core.{AffinityGroup, AuthConnector, Enrolments}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AccessibilityStatementControllerSpec extends ControllerBaseSpec {

  trait Test {
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
    val mockAccountDetailsService: AccountDetailsService = mock[AccountDetailsService]
    val mockHybridUserPredicate: HybridUserPredicate = new HybridUserPredicate(mockAccountDetailsService)
    val mockAgentPredicate: AgentPredicate = new AgentPredicate(mockEnrolmentsAuthService, messages, mockAppConfig)
    val mockAuthorisedController: AuthorisedController = new AuthorisedController(
      messages,
      mockEnrolmentsAuthService,
      mockHybridUserPredicate,
      mockAgentPredicate,
      mockAppConfig
    )
    val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = successfulAuthResult

    def setup: Any =
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[~[Enrolments, Option[AffinityGroup]]])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(authResult)
        .noMoreThanOnce()

    def controller: AccessibilityStatementController = {
      setup
      new AccessibilityStatementController(mockAuthorisedController, messages)
    }
  }

  "The show() action" when {

    "the user is an authenticated principal entity" should {

      "return 200" in new Test {
        private val result = controller.show(fakeRequest)
        status(result) shouldBe Status.OK
      }
    }

    "the user is an authenticated agent entity" should {

      "return 200" in new Test {
        override val authResult: Future[Enrolments ~ Option[AffinityGroup]] = agentAuthResult
        override def setup: Any = {
          super.setup
          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[Enrolments])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *, *)
            .returns(Future.successful(agentEnrolments))
            .noMoreThanOnce()
        }

        private val result = controller.show(fakeRequest.withSession("CLIENT_VRN" -> "999999999"))

        status(result) shouldBe Status.OK
      }
    }

    "the user is unauthorised" should {

      "return 403" in new Test {
        override val authResult: Future[Enrolments ~ Option[AffinityGroup]] = Future.successful(new ~(
          otherEnrolment,
          Some(Individual)
        ))
        override def setup: Any = {
          super.setup
          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[Enrolments])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *, *)
            .returns(Future.successful(otherEnrolment))
            .noMoreThanOnce()
        }

        private val result = controller.show(fakeRequest)

        status(result) shouldBe Status.FORBIDDEN
      }
    }
  }
}
