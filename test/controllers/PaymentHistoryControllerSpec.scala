/*
 * Copyright 2018 HM Revenue & Customs
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

import java.time.LocalDate

import audit.AuditingService
import audit.models.AuditModel
import models.{ServiceResponse, User}
import models.errors.VatLiabilitiesError
import models.viewModels.{PaymentsHistoryModel, PaymentsHistoryViewModel}
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import services.{DateService, EnrolmentsAuthService, PaymentsService}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class PaymentHistoryControllerSpec extends ControllerBaseSpec {

  private trait Test {
    val serviceResult: ServiceResponse[Seq[PaymentsHistoryModel]] =
      Right(Seq(
        PaymentsHistoryModel(
          taxPeriodFrom = LocalDate.parse(s"2018-01-01"),
          taxPeriodTo   = LocalDate.parse(s"2018-02-01"),
          amount        = 123456789,
          clearedDate   = LocalDate.parse(s"2018-03-01")
        ),
        PaymentsHistoryModel(
          taxPeriodFrom = LocalDate.parse(s"2018-03-01"),
          taxPeriodTo   = LocalDate.parse(s"2018-04-01"),
          amount        = 987654321,
          clearedDate   = LocalDate.parse(s"2018-03-01")
        )
      ))
    val serviceResultYearOne: ServiceResponse[Seq[PaymentsHistoryModel]] = serviceResult
    val serviceResultYearTwo: ServiceResponse[Seq[PaymentsHistoryModel]] = serviceResult

    val authResult: Future[_] =
      Future.successful(Enrolments(Set(
        Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VRN", "123456789")), "")
      )))

    val serviceCall: Boolean = true
    val authCall: Boolean = true
    val targetYear: Int = 2018
    val testUser: User = User("999999999")
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockPaymentsService: PaymentsService = mock[PaymentsService]
    val mockDateService: DateService = mock[DateService]
    val mockAuditService: AuditingService = mock[AuditingService]

    mockAppConfig.features.allowPaymentHistory(true)

    def setup(): Any = {
      (mockDateService.now: () => LocalDate)
        .stubs()
        .returns(LocalDate.parse("2018-05-01"))

      if (authCall) {
        (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *)
          .returns(authResult)
      }

      if (serviceCall) {
        (mockAuditService.audit(_: AuditModel, _: String)(_: HeaderCarrier, _: ExecutionContext))
          .stubs(*, *, *, *)
          .returns({})

        (mockPaymentsService.getPaymentsHistory(_: User, _: Int)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *).noMoreThanOnce()
          .returns(serviceResultYearOne)

        (mockPaymentsService.getPaymentsHistory(_: User, _: Int)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *).noMoreThanOnce()
          .returns(serviceResultYearTwo)

        (mockPaymentsService.getPaymentsHistory(_: User, _: Int)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *).noMoreThanOnce()
          .returns(serviceResult)
      }
    }

    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)

    def target: PaymentHistoryController = {
      setup()
      new PaymentHistoryController(
        messages,
        mockPaymentsService,
        mockDateService,
        mockEnrolmentsAuthService,
        mockAppConfig,
        mockAuditService)
    }
  }

  "Calling the paymentHistory action" when {

    "the user is logged in" should {

      "return 200" in new Test {
        private val result = target.paymentHistory(targetYear)(fakeRequest)
        status(result) shouldBe Status.OK
      }

      "return HTML" in new Test {
        private val result = target.paymentHistory(targetYear)(fakeRequest)
        contentType(result) shouldBe Some("text/html")
      }

      "return charset utf-8" in new Test {
        private val result = target.paymentHistory(targetYear)(fakeRequest)
        charset(result) shouldBe Some("utf-8")
      }
    }

    "the user is not logged in" should {

      "return 401 (Unauthorised)" in new Test {
        override val serviceCall = false
        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())
        val result: Future[Result] = target.paymentHistory(targetYear)(fakeRequest)
        status(result) shouldBe Status.UNAUTHORIZED
      }
    }

    "the user is not authenticated" should {

      "return 403 (Forbidden)" in new Test {
        override val serviceCall = false
        override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())
        val result: Future[Result] = target.paymentHistory(targetYear)(fakeRequest)
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "the user enters an invalid search year for their payment history" should {

      "return 404 (Not Found)" in new Test {
        override val serviceCall = false
        override val targetYear = 2021
        private val result = target.paymentHistory(targetYear)(fakeRequest)
        status(result) shouldBe Status.NOT_FOUND
      }
    }

    "the allowPaymentHistory feature is disabled" should {

      "return 404 (Not Found)" in new Test {
        mockAppConfig.features.allowPaymentHistory(false)
        override val serviceCall = false
        private val result = target.paymentHistory(targetYear)(fakeRequest)
        status(result) shouldBe Status.NOT_FOUND
      }
    }
  }

  "Calling the getFinancialTransactions function" when {

    "the user has no transactions for a specific year" should {

      "return the correct year tabs" in new Test {
        override val authCall = false
        override val serviceResultYearTwo = Left(VatLiabilitiesError)

        val examplePaymentHistory: ServiceResponse[PaymentsHistoryViewModel] =
          Right(PaymentsHistoryViewModel(
            Seq(2018),
            2018,
            Seq(
              PaymentsHistoryModel(
                taxPeriodFrom = LocalDate.parse(s"2018-01-01"),
                taxPeriodTo   = LocalDate.parse(s"2018-02-01"),
                amount        = 123456789,
                clearedDate   = LocalDate.parse(s"2018-03-01")
              ),
              PaymentsHistoryModel(
                taxPeriodFrom = LocalDate.parse(s"2018-03-01"),
                taxPeriodTo   = LocalDate.parse(s"2018-04-01"),
                amount        = 987654321,
                clearedDate   = LocalDate.parse(s"2018-03-01")
              )
            )
          )
        )

        private val result = await(target.getFinancialTransactions(testUser, targetYear))
        result shouldBe examplePaymentHistory
      }
    }

    "the PaymentsService retrieves a valid PaymentHistoryModel" should {

      "return the PaymentHistoryModel" in new Test {
        override val authCall = false

        (mockPaymentsService.getPaymentsHistory(_: User, _: Int)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *).noMoreThanOnce()
          .returns(serviceResultYearTwo)

        (mockPaymentsService.getPaymentsHistory(_: User, _: Int)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *).noMoreThanOnce()
          .returns(serviceResultYearOne)

        val examplePaymentHistory: ServiceResponse[PaymentsHistoryViewModel] =
          Right(PaymentsHistoryViewModel(
            Seq(2018, 2017),
            2018,
            Seq(PaymentsHistoryModel(
              taxPeriodFrom = LocalDate.parse(s"2018-01-01"),
              taxPeriodTo   = LocalDate.parse(s"2018-02-01"),
              amount        = 123456789,
              clearedDate   = LocalDate.parse(s"2018-03-01")
            ),
            PaymentsHistoryModel(
              taxPeriodFrom = LocalDate.parse(s"2018-03-01"),
              taxPeriodTo   = LocalDate.parse(s"2018-04-01"),
              amount        = 987654321,
              clearedDate   = LocalDate.parse(s"2018-03-01")
            ))
          ))

        private val result = await(target.getFinancialTransactions(testUser, targetYear))
        result shouldBe examplePaymentHistory
      }
    }

    "the PaymentsService returns an error" should {

      "throw an exception" in new Test {
        override val authCall = false
        override val serviceResult = Left(VatLiabilitiesError)

        intercept[Exception](await(target.getFinancialTransactions(testUser, targetYear)))
      }
    }
  }

  "Calling .isValidSearchYear" when {

    "the year is on the upper search boundary" should {

      "return true" in new Test {
        override val authResult: Future[_] = Future.successful("")
        override def setup(): Any = "" // Prevent the unused mocks causing trouble
        val result: Boolean = target.isValidSearchYear(2018, 2018)
        result shouldBe true
      }
    }

    "the year is above the upper search boundary" should {

      "return false" in new Test {
        override val authResult: Future[_] = Future.successful("")
        override def setup(): Any = "" // Prevent the unused mocks causing trouble
        val result: Boolean = target.isValidSearchYear(2019, 2018)
        result shouldBe false
      }
    }

    "the year is on the lower boundary" should {

      "return true" in new Test {
        override val authResult: Future[_] = Future.successful("")
        override def setup(): Any = "" // Prevent the unused mocks causing trouble
        val result: Boolean = target.isValidSearchYear(2017, 2018)
        result shouldBe true
      }
    }

    "the year is below the lower boundary" should {

      "return false" in new Test {
        override val authResult: Future[_] = Future.successful("")
        override def setup(): Any = "" // Prevent the unused mocks causing trouble
        val result: Boolean = target.isValidSearchYear(2014, 2018)
        result shouldBe false
      }
    }

    "the year is between the upper and lower boundaries" should {

      "return true" in new Test {
        override val authResult: Future[_] = Future.successful("")
        override def setup(): Any = "" // Prevent the unused mocks causing trouble
        val result: Boolean = target.isValidSearchYear(2017, 2018)
        result shouldBe true
      }
    }
  }
}
