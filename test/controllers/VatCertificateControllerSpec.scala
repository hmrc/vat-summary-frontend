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
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.Helpers._
import services.{AccountDetailsService, EnrolmentsAuthService}
import uk.gov.hmrc.auth.core.AffinityGroup.Agent
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class VatCertificateControllerSpec extends ControllerBaseSpec {

  private trait Test {
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

    val vatCertificateSwitch: Boolean = true
    val agentAccessSwitch: Boolean = true

    def setup(): Any = {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[~[Enrolments, Option[AffinityGroup]]])(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *)
      .returns(authResult)
      mockAppConfig.features.vatCertificateEnabled(vatCertificateSwitch)
      mockAppConfig.features.agentAccess(agentAccessSwitch)
    }

    def target: VatCertificateController = {
      setup()
      new VatCertificateController(messages, mockAuthorisedController)
    }
  }

  "The show() action" when {

    "the vat certificate feature switch is on" when {

      "the user is non-agent" when {

        "authorised" should {

          "return OK (200)" in new Test {
            private val result = target.show()(fakeRequest)
            status(result) shouldBe Status.OK
          }

          "return HTML" in new Test {
            private val result = target.show()(fakeRequest)
            contentType(result) shouldBe Some("text/html")
          }
        }
      }

      "the user is an agent" when {

        "allow agent access feature switch is on" when {

          "user is authorised" should {

            "return OK (200)" in new Test {
              override def setup(): Unit = {
                (mockAuthConnector.authorise(_: Predicate, _: Retrieval[~[Enrolments, Option[AffinityGroup]]])(_: HeaderCarrier, _: ExecutionContext))
                  .expects(*, *, *, *)
                  .returns(agentAuthResult)
                  .noMoreThanOnce()

                (mockAuthConnector.authorise(_: Predicate, _: Retrieval[Enrolments])(_: HeaderCarrier, _: ExecutionContext))
                  .expects(*, *, *, *)
                  .returns(Future.successful(agentEnrolments))
                  .noMoreThanOnce()
              }

              private val result = target.show()(fakeRequest.withSession("CLIENT_VRN" -> "123456789"))

              status(result) shouldBe Status.OK
              contentType(result) shouldBe Some("text/html")
            }
          }

          "user is unauthorised" should {

            "return FORBIDDEN and agent unauthorised page" in new Test {

              override def setup(): Unit = {
                (mockAuthConnector.authorise(_: Predicate, _: Retrieval[~[Enrolments, Option[AffinityGroup]]])(_: HeaderCarrier, _: ExecutionContext))
                  .expects(*, *, *, *)
                  .returns(Future.successful(new ~(otherEnrolment, Some(Agent))))
                  .noMoreThanOnce()

                (mockAuthConnector.authorise(_: Predicate, _: Retrieval[Enrolments])(_: HeaderCarrier, _: ExecutionContext))
                  .expects(*, *, *, *)
                  .returns(Future.successful(otherEnrolment))
                  .noMoreThanOnce()
              }

              private val result = target.show()(fakeRequest.withSession("CLIENT_VRN" -> "123456789"))

              status(result) shouldBe Status.FORBIDDEN
              Jsoup.parse(bodyOf(result)).title() shouldBe "You can’t use this service yet"
            }
          }
        }

        "allow agent access feature switch is off" should {

          "return FORBIDDEN and non-agent unauthorised page" in new Test {

            override val agentAccessSwitch: Boolean = false
            override val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = agentAuthResult

            private val result = target.show()(fakeRequest.withSession("CLIENT_VRN" -> "123456789"))

            status(result) shouldBe Status.FORBIDDEN
            Jsoup.parse(bodyOf(result)).title() shouldBe "You are not authorised to use this service"
          }
        }
      }

      "the user is logged in with invalid credentials" should {

        "return Forbidden (403)" in new Test {
          override val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = Future.failed(InsufficientEnrolments())
          private val result = target.show()(fakeRequest)
          status(result) shouldBe Status.FORBIDDEN
        }

        "return HTML" in new Test {
          override val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = Future.failed(InsufficientEnrolments())
          private val result = target.show()(fakeRequest)
          contentType(result) shouldBe Some("text/html")
        }
      }

      "the user is not logged in" should {

        "return Unauthorised (401)" in new Test {
          override val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = Future.failed(MissingBearerToken())
          private val result = target.show()(fakeRequest)
          status(result) shouldBe Status.UNAUTHORIZED
        }

        "return HTML" in new Test {
          override val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = Future.failed(MissingBearerToken())
          private val result = target.show()(fakeRequest)
          contentType(result) shouldBe Some("text/html")
        }
      }
    }

    "the vat certificate feature switch is off and a user is logged in with valid credentials" should {

      "return Not Found (404)" in new Test {
        override val vatCertificateSwitch: Boolean = false
        private val result = target.show()(fakeRequest)
        status(result) shouldBe Status.NOT_FOUND
      }

      "return HTML" in new Test {
        override val vatCertificateSwitch: Boolean = false
        private val result = target.show()(fakeRequest)
        contentType(result) shouldBe Some("text/html")
      }
    }
  }

  "The changeClient() action" when {
    "the vat certificate feature switch is on" when {
      "the user is non-agent" should {
        "return Not Found (404)" in new Test {
          private val result = target.changeClient()(fakeRequest)
          status(result) shouldBe Status.NOT_FOUND
        }

        "return HTML" in new Test {
          override val vatCertificateSwitch: Boolean = false
          private val result = target.changeClient()(fakeRequest)
          contentType(result) shouldBe Some("text/html")
        }
      }

      "the user is an agent" should {
        "redirect the user to the ACLUFE" in new Test {
          override def setup(): Unit = {
            (mockAuthConnector.authorise(_: Predicate, _: Retrieval[~[Enrolments, Option[AffinityGroup]]])(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *, *)
              .returns(agentAuthResult)
              .noMoreThanOnce()

            (mockAuthConnector.authorise(_: Predicate, _: Retrieval[Enrolments])(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *, *)
              .returns(Future.successful(agentEnrolments))
              .noMoreThanOnce()
          }

          private val result = target.changeClient()(fakeRequest)
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some("agent-client-lookup-start-url//")
        }

      }

      "the user is unauthorised" should {
        "return Unauthorised (401)" in new Test {
          override val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = Future.failed(MissingBearerToken())
          private val result = target.changeClient()(fakeRequest)
          status(result) shouldBe Status.UNAUTHORIZED
        }

        "return HTML" in new Test {
          override val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = Future.failed(MissingBearerToken())
          private val result = target.changeClient()(fakeRequest)
          contentType(result) shouldBe Some("text/html")
        }
      }
    }
    "the vat certificate feature switch is off and a user is logged in with valid credentials" should {

      "return Not Found (404)" in new Test {
        override val vatCertificateSwitch: Boolean = false
        private val result = target.changeClient()(fakeRequest)
        status(result) shouldBe Status.NOT_FOUND
      }

      "return HTML" in new Test {
        override val vatCertificateSwitch: Boolean = false
        private val result = target.changeClient()(fakeRequest)
        contentType(result) shouldBe Some("text/html")
      }
    }
  }

  "The changeClientAction() action" when {
    "the vat certificate feature switch is on" when {
      "the user is non-agent" should {
        "return Not Found (404)" in new Test {
          private val result = target.changeClientAction()(fakeRequest)
          status(result) shouldBe Status.NOT_FOUND
        }

        "return HTML" in new Test {
          override val vatCertificateSwitch: Boolean = false
          private val result = target.changeClientAction()(fakeRequest)
          contentType(result) shouldBe Some("text/html")
        }
      }

      "the user is an agent" should {
        "redirect the user to the ACLUFE" in new Test {
          override def setup(): Unit = {
            (mockAuthConnector.authorise(_: Predicate, _: Retrieval[~[Enrolments, Option[AffinityGroup]]])(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *, *)
              .returns(agentAuthResult)
              .noMoreThanOnce()

            (mockAuthConnector.authorise(_: Predicate, _: Retrieval[Enrolments])(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *, *)
              .returns(Future.successful(agentEnrolments))
              .noMoreThanOnce()
          }

          private val result = target.changeClientAction()(fakeRequest)
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some("agent-client-lookup-start-url//")
        }

      }

      "the user is unauthorised" should {
        "return Unauthorised (401)" in new Test {
          override val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = Future.failed(MissingBearerToken())
          private val result = target.changeClientAction()(fakeRequest)
          status(result) shouldBe Status.UNAUTHORIZED
        }

        "return HTML" in new Test {
          override val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = Future.failed(MissingBearerToken())
          private val result = target.changeClientAction()(fakeRequest)
          contentType(result) shouldBe Some("text/html")
        }
      }
    }
    "the vat certificate feature switch is off and a user is logged in with valid credentials" should {

      "return Not Found (404)" in new Test {
        override val vatCertificateSwitch: Boolean = false
        private val result = target.changeClientAction()(fakeRequest)
        status(result) shouldBe Status.NOT_FOUND
      }

      "return HTML" in new Test {
        override val vatCertificateSwitch: Boolean = false
        private val result = target.changeClientAction()(fakeRequest)
        contentType(result) shouldBe Some("text/html")
      }
    }
  }
}
