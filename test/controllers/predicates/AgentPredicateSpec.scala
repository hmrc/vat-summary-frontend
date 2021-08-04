/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.predicates

import controllers.ControllerBaseSpec
import play.api.http.Status
import play.api.mvc.Results.Ok
import play.api.mvc.{AnyContent, Request, Result}
import play.api.test.Helpers._
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AgentPredicateSpec extends ControllerBaseSpec {

  private trait Test {
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
    lazy val authResponse: Enrolments = Enrolments(
      Set(
        Enrolment(
          "HMRC-AS-AGENT",
          Seq(EnrolmentIdentifier("AgentReferenceNumber", "XARN1234567")),
          "Activated"
        )
      )
    )

    lazy val mockAgentPredicate: AgentPredicate = new AgentPredicate(
      mockEnrolmentsAuthService,
      mcc,
      mockAppConfig,
      ec,
      agentUnauthorised
    )

    def target(request: Request[AnyContent]): Future[Result] = mockAgentPredicate.authoriseAsAgent({
      _ =>
        _ =>
          Future.successful(Ok("welcome"))
    })(request)
  }

  "AgentPredicate .authoriseAsAgent" when {

    "CLIENT_VRN is in session" when {

      "agent has delegated enrolment for VRN" when {

        "agent has HMRC-AS-AGENT enrolment" should {

          "return the result of the original code block" in new Test {

            (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *, *)
              .returns(Future.successful(authResponse))

            lazy val result: Future[Result] = target(fakeRequest.withSession("CLIENT_VRN" -> "123456789"))

            status(result) shouldBe Status.OK
            contentAsString(result) shouldBe "welcome"
          }
        }

        "agent does not have HMRC-AS-AGENT enrolment" should {

          "return 403" in new Test {

            val otherEnrolment: Enrolments = Enrolments(
              Set(
                Enrolment(
                  "OTHER-ENROLMENT",
                  Seq(EnrolmentIdentifier("AA", "AA")),
                  "Activated"
                )
              )
            )

            (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *, *)
              .returns(Future.successful(otherEnrolment))

            lazy val result: Future[Result] = target(fakeRequest.withSession("CLIENT_VRN" -> "123456789"))

            status(result) shouldBe Status.FORBIDDEN
          }
        }
      }

      "agent does not have delegated enrolment for VRN" should {

        "redirect to agent-client-lookup unauthorised page" in new Test {

          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *, *)
            .returns(Future.failed(InsufficientEnrolments()))

          lazy val result: Future[Result] = target(fakeRequest.withSession("CLIENT_VRN" -> "123456789"))

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(mockAppConfig.agentClientUnauthorisedUrl("/"))
        }
      }

      "user has no session" should {

        "redirect to sign in" in new Test {

          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *, *)
            .returns(Future.failed(MissingBearerToken()))

          lazy val result: Future[Result] = target(fakeRequest.withSession("CLIENT_VRN" -> "123456789"))

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
        }
      }
    }

    "CLIENT_VRN is not in session" should {

      "redirect to agent-client lookup VRN lookup page" in new Test {

        lazy val result: Future[Result] = target(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(mockAppConfig.agentClientLookupStartUrl("/"))
      }
    }
  }
}
