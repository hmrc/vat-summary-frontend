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

import audit.AuditingService
import audit.models.AuditModel
import connectors.VatSubscriptionConnector
import controllers.predicates.HybridUserPredicate
import models.DirectDebitDetailsModel
import models.errors.DirectDebitSetupError
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import services.{AccountDetailsService, EnrolmentsAuthService, PaymentsService}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class DirectDebitControllerSpec extends ControllerBaseSpec {

  private trait DirectDebitDetailsTest {
    val authResult: Future[_] =
      Future.successful(Enrolments(Set(
        Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VRN", "123456789")), "")
      )))

    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockVatSubscriptionConnector: VatSubscriptionConnector = mock[VatSubscriptionConnector]
    val mockPaymentsService: PaymentsService = mock[PaymentsService]
    val mockAuditService: AuditingService = mock[AuditingService]
    val mockAccountDetailsService: AccountDetailsService = mock[AccountDetailsService]
    val mockHybridUserPredicate: HybridUserPredicate = new HybridUserPredicate(mockAccountDetailsService)
    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
    val mockAuthorisedController: AuthorisedController = new AuthorisedController(
      messages,
      mockEnrolmentsAuthService,
      mockHybridUserPredicate,
      mockAppConfig
    )

    def setup(): Any = {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(authResult)

      (mockAuditService.audit(_: AuditModel, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *, *)
        .returns({})
    }


    def target: DirectDebitController = {
      setup()
      new DirectDebitController(
        messages,
        mockEnrolmentsAuthService,
        mockAppConfig,
        mockPaymentsService,
        mockAuthorisedController,
        mockAuditService)
    }
  }

  "Calling the directDebit action" when {

    "the user is logged in" should {

      "redirect to the correct redirect url" in new DirectDebitDetailsTest {

        mockAppConfig.features.useDirectDebitDummyPage(false)

        val redirectUrl = "http://google.com/"
        val expectedRedirectLocation = Some(redirectUrl)
        val serviceResponse = Right(redirectUrl)

        override def setup(): Any = {
          super.setup()

          (mockPaymentsService.setupDirectDebitJourney(_: DirectDebitDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(serviceResponse))
        }

        lazy val result: Future[Result] = target.directDebits()(fakeRequestWithSession)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe expectedRedirectLocation
      }

    }

    "the user is not logged in" should {
      "return 401 (Unauthorised)" in new DirectDebitDetailsTest {
        lazy val result: Future[Result] = target.directDebits()(fakeRequest)

        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())

        status(result) shouldBe Status.UNAUTHORIZED
      }
    }

    "the user is not authenticated" should {
      "return 403 (Forbidden)" in new DirectDebitDetailsTest {
        lazy val result: Future[Result] = target.directDebits()(fakeRequest)

        override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())

        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "an error occurs upstream" should {

      "return 500 (Internal server error)" in new DirectDebitDetailsTest {
        override def setup(): Any = {
          super.setup()

          (mockPaymentsService.setupDirectDebitJourney(_: DirectDebitDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(Left(DirectDebitSetupError)))
        }

        private val result = target.directDebits()(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

      "return the standard error view" in new DirectDebitDetailsTest {
        override def setup(): Unit = {
          super.setup()
          (mockPaymentsService.setupDirectDebitJourney(_: DirectDebitDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(Left(DirectDebitSetupError)))
        }

        val result: Result = await(target.directDebits()(fakeRequest))
        val document: Document = Jsoup.parse(bodyOf(result))


        document.select("h1").first().text() shouldBe "Sorry, there is a problem with the service"
      }
    }
  }
}
