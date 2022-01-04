/*
 * Copyright 2022 HM Revenue & Customs
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

import common.SessionKeys
import common.TestModels.customerInformationMax
import controllers.ControllerBaseSpec
import play.api.http.Status
import play.api.mvc.Results.Ok
import play.api.mvc.{AnyContent, AnyContentAsEmpty, Request, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AgentPredicateSpec extends ControllerBaseSpec {

  lazy val authResponse: Enrolments = Enrolments(
    Set(
      Enrolment(
        "HMRC-AS-AGENT",
        Seq(EnrolmentIdentifier("AgentReferenceNumber", "XARN1234567")),
        "Activated"
      )
    )
  )

  def target(request: Request[AnyContent], financialRequest: Boolean = false): Future[Result] =
    agentPredicate.authoriseAsAgent(
      _ => _ => Future.successful(Ok("welcome")), financialRequest
    )(request)

  lazy val requestWithVRN: FakeRequest[AnyContentAsEmpty.type] = fakeRequest.withSession("mtdVatvcClientVrn" -> "123456789")

  "AgentPredicate .authoriseAsAgent" when {

    "mtdVatvcClientVrn is in session" when {

      "agent has delegated enrolment for VRN" when {

        "agent has HMRC-AS-AGENT enrolment" when {

          "the request is for a financial page" should {

            lazy val result: Future[Result] = {
              (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
                .expects(*, *, *, *)
                .returns(Future.successful(authResponse))

              mockCustomerInfo(Right(customerInformationMax))
              mockDateServiceCall()
              target(requestWithVRN, financialRequest = true)
            }

            "successfully pass the request through the predicate" in {
              status(result) shouldBe Status.OK
              contentAsString(result) shouldBe "welcome"
            }

            "add values to the session to prove financial access" in {
              session(result).get(SessionKeys.financialAccess) shouldBe Some("true")
              session(result).get(SessionKeys.insolventWithoutAccessKey) shouldBe Some("false")
            }
          }

          "the request is not for a financial page" should {

            lazy val result: Future[Result] = {
              (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
                .expects(*, *, *, *)
                .returns(Future.successful(authResponse))
              target(requestWithVRN)
            }

            "successfully pass the request through the predicate" in {
              status(result) shouldBe Status.OK
              contentAsString(result) shouldBe "welcome"
            }

            "not add values to the session as the request was not forwarded to the financial predicate" in {
              session(result).get(SessionKeys.financialAccess) shouldBe None
              session(result).get(SessionKeys.insolventWithoutAccessKey) shouldBe None
            }
          }
        }

        "agent does not have HMRC-AS-AGENT enrolment" should {

          "return 403" in {

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

            lazy val result: Future[Result] = target(requestWithVRN)

            status(result) shouldBe Status.FORBIDDEN
          }
        }
      }

      "agent does not have delegated enrolment for VRN" should {

        "redirect to agent-client-lookup unauthorised page" in {

          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *, *)
            .returns(Future.failed(InsufficientEnrolments()))

          lazy val result: Future[Result] = target(requestWithVRN)

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(mockAppConfig.agentClientUnauthorisedUrl("/"))
        }
      }

      "user has no session" should {

        "redirect to sign in" in {

          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *, *)
            .returns(Future.failed(MissingBearerToken()))

          lazy val result: Future[Result] = target(requestWithVRN)

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
        }
      }
    }

    "mtdVatvcClientVrn is not in session" should {

      "redirect to agent-client lookup VRN lookup page" in {

        lazy val result: Future[Result] = target(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(mockAppConfig.agentClientLookupStartUrl("/"))
      }
    }
  }
}
