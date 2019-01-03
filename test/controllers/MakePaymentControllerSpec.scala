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
import models.errors.PaymentSetupError
import models.payments.PaymentDetailsModel
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{AccountDetailsService, EnrolmentsAuthService, PaymentsService}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class MakePaymentControllerSpec extends ControllerBaseSpec {

  val testAmountInPence: Int = 10000
  val testMonth: Int = 2
  val testYear: Int = 2018
  val testChargeType: String = "VAT Return Debit Charge"
  val testDueDate: String = "2018-08-08"

  private trait MakePaymentDetailsTest {
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

    def target: MakePaymentController = {
      setup()
      new MakePaymentController(
        messages,
        mockEnrolmentsAuthService,
        mockPaymentsService,
        mockAppConfig,
        mockAuthorisedController,
        mockAuditService)
    }
  }

  "Calling the makePayment action" when {

    "the user is logged in" should {

      "redirected when returned Right ServiceResponse" in new MakePaymentDetailsTest {

        val redirectUrl = "http://www.google.com"
        val expectedRedirectLocation = Some(redirectUrl)
        val serviceResponse = Right(redirectUrl)

        override def setup(): Any = {
          super.setup()

          (mockPaymentsService.setupPaymentsJourney(_: PaymentDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(serviceResponse))
        }

        lazy val result: Future[Result] = target.makePaymentNoPeriod(testAmountInPence, testChargeType, testDueDate)(fakeRequestWithSession)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe expectedRedirectLocation
      }

      "Internal Service Error when Left returned" in new MakePaymentDetailsTest {

        val redirectUrl = "http://www.google.com"
        val expectedRedirectLocation = Some(redirectUrl)
        val serviceResponse = Right(redirectUrl)

        override def setup(): Any = {
          super.setup()

          (mockPaymentsService.setupPaymentsJourney(_: PaymentDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(Left(PaymentSetupError)))
        }

        lazy val result: Future[Result] = target.makePaymentNoPeriod(testAmountInPence, testChargeType, testDueDate)(fakeRequestWithSession)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }

    "the user is not logged in" should {
      "return 401 (Unauthorised)" in new MakePaymentDetailsTest {
        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequestToPOSTWithSession(
          ("amountInPence", "10000"),
          ("taxPeriodMonth", "02"),
          ("taxPeriodYear", "2018"))
        lazy val result: Future[Result] = target.makePayment(testAmountInPence, testMonth, testYear, testChargeType, testDueDate)(request)

        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())

        status(result) shouldBe Status.UNAUTHORIZED
      }
    }

    "the user is not authenticated" should {
      "return 403 (Forbidden)" in new MakePaymentDetailsTest {
        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequestToPOSTWithSession(
          ("amountInPence", "10000"),
          ("taxPeriodMonth", "02"),
          ("taxPeriodYear", "2018"))
        lazy val result: Future[Result] = target.makePayment(testAmountInPence, testMonth, testYear, testChargeType, testDueDate)(request)

        override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())

        status(result) shouldBe Status.FORBIDDEN
      }
    }
  }

  "Calling the makePaymentNoPeriod action" when {

    "the user is logged in" should {

      "redirected when returned Right ServiceResponse" in new MakePaymentDetailsTest {

        val redirectUrl = "http://www.google.com"
        val expectedRedirectLocation = Some(redirectUrl)
        val serviceResponse = Right(redirectUrl)

        override def setup(): Any = {
          super.setup()

          (mockPaymentsService.setupPaymentsJourney(_: PaymentDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(serviceResponse))
        }

        lazy val result: Future[Result] = target.makePayment(testAmountInPence, testMonth, testYear, testChargeType, testDueDate)(fakeRequestWithSession)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe expectedRedirectLocation
      }

      "Internal Service Error when Left returned" in new MakePaymentDetailsTest {

        val redirectUrl = "http://www.google.com"
        val expectedRedirectLocation = Some(redirectUrl)
        val serviceResponse = Right(redirectUrl)

        override def setup(): Any = {
          super.setup()

          (mockPaymentsService.setupPaymentsJourney(_: PaymentDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(Left(PaymentSetupError)))
        }

        lazy val result: Future[Result] = target.makePayment(testAmountInPence, testMonth, testYear, testChargeType, testDueDate)(fakeRequestWithSession)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }

    "the user is not logged in" should {
      "return 401 (Unauthorised)" in new MakePaymentDetailsTest {
        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequestToPOSTWithSession(
          ("amountInPence", "10000"),
          ("taxPeriodMonth", "02"),
          ("taxPeriodYear", "2018"))
        lazy val result: Future[Result] = target.makePayment(testAmountInPence, testMonth, testYear, testChargeType, testDueDate)(request)

        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())

        status(result) shouldBe Status.UNAUTHORIZED
      }
    }

    "the user is not authenticated" should {
      "return 403 (Forbidden)" in new MakePaymentDetailsTest {
        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequestToPOSTWithSession(
          ("amountInPence", "10000"),
          ("taxPeriodMonth", "02"),
          ("taxPeriodYear", "2018"))
        lazy val result: Future[Result] = target.makePayment(testAmountInPence, testMonth, testYear, testChargeType, testDueDate)(request)

        override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())

        status(result) shouldBe Status.FORBIDDEN
      }
    }
  }
}

