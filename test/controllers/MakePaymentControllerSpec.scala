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

package controllers

import audit.AuditingService
import audit.models.AuditModel
import common.TestModels.{agentAuthResult, successfulAuthResult}
import models.errors.PaymentSetupError
import models.payments.PaymentDetailsModel
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.mvc.{AnyContentAsFormUrlEncoded, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.PaymentsService
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.errors.PaymentsError

import scala.concurrent.{ExecutionContext, Future}

class MakePaymentControllerSpec extends ControllerBaseSpec {

  val testAmountInPence: Int = 10000
  val testMonth: Int = 2
  val testYear: Int = 2018
  val testChargeType: String = "VAT Return Debit Charge"
  val testNonReturnChargeType: String = "VAT Default Interest"
  val testDueDate: String = "2018-08-08"
  val testChargeReference: String = "XD002750002155"
  val testVatPeriodEnding: String = "2018-08-08"

  private trait MakePaymentDetailsTest {
    val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = successfulAuthResult

    val mockPaymentsService: PaymentsService = mock[PaymentsService]
    val mockAuditService: AuditingService = mock[AuditingService]
    val paymentsError: PaymentsError = injector.instanceOf[PaymentsError]

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
        enrolmentsAuthService,
        mockPaymentsService,
        mockAppConfig,
        authorisedController,
        mockAuditService,
        mcc,
        ec,
        paymentsError)
    }
  }

  "Calling the makePayment action" when {

    "the user is logged in" should {

      "redirect when returned Right ServiceResponse" in new MakePaymentDetailsTest {

        val redirectUrl = "http://www.google.com"
        val expectedRedirectLocation: Option[String] = Some(redirectUrl)
        val serviceResponse = Right(redirectUrl)

        override def setup(): Any = {
          super.setup()

          (mockPaymentsService.setupPaymentsJourney(_: PaymentDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(serviceResponse))
        }

        lazy val result: Future[Result] = target.makePaymentNoPeriod(
          testAmountInPence,
          testNonReturnChargeType,
          testDueDate,
          testChargeReference)(fakeRequestWithSession)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe expectedRedirectLocation
      }

      "Internal Server Error when Left returned" in new MakePaymentDetailsTest {

        override def setup(): Any = {
          super.setup()

          (mockPaymentsService.setupPaymentsJourney(_: PaymentDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(Left(PaymentSetupError)))
        }

        lazy val result: Future[Result] = target.makePaymentNoPeriod(
          testAmountInPence,
          testNonReturnChargeType,
          testDueDate,
          testChargeReference)(fakeRequestWithSession)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        Jsoup.parse(bodyOf(result)).title shouldBe "There is a problem with the service - Business tax account - GOV.UK"
      }
    }

    "the user is not logged in" should {

      "redirect to sign in" in new MakePaymentDetailsTest {
        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequestToPOSTWithSession(
          ("amountInPence", "10000"),
          ("taxPeriodMonth", "02"),
          ("taxPeriodYear", "2018"))
        lazy val result: Future[Result] = target.makePayment(
          testAmountInPence,
          testMonth,
          testYear,
          testVatPeriodEnding,
          testChargeType,
          testDueDate,
          testChargeReference)(request)

        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
      }
    }

    "the user is not authenticated" should {
      "return 403 (Forbidden)" in new MakePaymentDetailsTest {
        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequestToPOSTWithSession(
          ("amountInPence", "10000"),
          ("taxPeriodMonth", "02"),
          ("taxPeriodYear", "2018"))
        lazy val result: Future[Result] = target.makePayment(
          testAmountInPence,
          testMonth,
          testYear,
          testVatPeriodEnding,
          testChargeType,
          testDueDate,
          testChargeReference)(request)

        override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())

        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "user is an Agent" should {

      "redirect to Agent Hub page" in new MakePaymentDetailsTest {
        override val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = agentAuthResult
        val result: Future[Result] = target.makePayment(
          testAmountInPence,
          testMonth,
          testYear,
          testVatPeriodEnding,
          testChargeType,
          testDueDate,
          testChargeReference)(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(mockAppConfig.agentClientLookupHubUrl)
      }
    }
  }

  "Calling the makePaymentNoPeriod action" when {

    "the user is logged in" should {

      "redirected when returned Right ServiceResponse" in new MakePaymentDetailsTest {

        val redirectUrl = "http://www.google.com"
        val expectedRedirectLocation: Option[String] = Some(redirectUrl)
        val serviceResponse = Right(redirectUrl)

        override def setup(): Any = {
          super.setup()

          (mockPaymentsService.setupPaymentsJourney(_: PaymentDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(serviceResponse))
        }

        lazy val result: Future[Result] = target.makePayment(
          testAmountInPence,
          testMonth,
          testYear,
          testVatPeriodEnding,
          testChargeType,
          testDueDate,
          testChargeReference)(fakeRequestWithSession)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe expectedRedirectLocation
      }

      "Internal Service Error when Left returned" in new MakePaymentDetailsTest {

        override def setup(): Any = {
          super.setup()

          (mockPaymentsService.setupPaymentsJourney(_: PaymentDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(Left(PaymentSetupError)))
        }

        lazy val result: Future[Result] = target.makePayment(
          testAmountInPence,
          testMonth,
          testYear,
          testVatPeriodEnding,
          testChargeType,
          testDueDate,
          testChargeReference)(fakeRequestWithSession)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }

    "the user is not logged in" should {

      "redirect to sign in" in new MakePaymentDetailsTest {
        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequestToPOSTWithSession(
          ("amountInPence", "10000"),
          ("taxPeriodMonth", "02"),
          ("taxPeriodYear", "2018"))
        lazy val result: Future[Result] = target.makePayment(
          testAmountInPence,
          testMonth,
          testYear,
          testVatPeriodEnding,
          testChargeType,
          testDueDate,
          testChargeReference)(request)

        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
      }
    }

    "the user is not authenticated" should {
      "return 403 (Forbidden)" in new MakePaymentDetailsTest {
        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequestToPOSTWithSession(
          ("amountInPence", "10000"),
          ("taxPeriodMonth", "02"),
          ("taxPeriodYear", "2018"))
        lazy val result: Future[Result] = target.makePayment(
          testAmountInPence,
          testMonth,
          testYear,
          testVatPeriodEnding,
          testChargeType,
          testDueDate,
          testChargeReference)(request)

        override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())

        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "the user is insolvent and not continuing to trade" should {

      "return 403 (Forbidden)" in new MakePaymentDetailsTest {
        override val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = successfulAuthResult
        lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequestToPOSTWithSession(
          ("amountInPence", "10000"),
          ("taxPeriodMonth", "02"),
          ("taxPeriodYear", "2018"))
        lazy val result: Future[Result] = target.makePayment(
          testAmountInPence,
          testMonth,
          testYear,
          testVatPeriodEnding,
          testChargeType,
          testDueDate,
          testChargeReference)(insolventRequest)

        status(result) shouldBe Status.FORBIDDEN
      }
    }
  }
}

