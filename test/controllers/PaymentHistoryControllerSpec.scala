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

import java.time.LocalDate

import audit.AuditingService
import audit.models.ExtendedAuditModel
import common.TestModels.{customerInformation, customerInformationHybrid}
import connectors.VatSubscriptionConnector
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import controllers.predicates.HybridUserPredicate
import models.errors.{UnknownError, VatLiabilitiesError}
import models.payments.ReturnDebitCharge
import models.viewModels.{PaymentsHistoryModel, PaymentsHistoryViewModel}
import models.{CustomerInformation, ServiceResponse, User}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import services.{AccountDetailsService, DateService, EnrolmentsAuthService, PaymentsService}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class PaymentHistoryControllerSpec extends ControllerBaseSpec {

  private trait Test {
    val serviceResultYearOne: ServiceResponse[Seq[PaymentsHistoryModel]] =
      Right(Seq(
        PaymentsHistoryModel(
          chargeType    = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.parse("2018-01-01")),
          taxPeriodTo   = Some(LocalDate.parse("2018-02-01")),
          amount        = 123456789,
          clearedDate   = Some(LocalDate.parse("2018-03-01"))
        ),
        PaymentsHistoryModel(
          chargeType    = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.parse("2018-03-01")),
          taxPeriodTo   = Some(LocalDate.parse("2018-04-01")),
          amount        = 987654321,
          clearedDate   = Some(LocalDate.parse("2018-05-01"))
        )
      ))
    val serviceResultYearTwo: ServiceResponse[Seq[PaymentsHistoryModel]] =
      Right(Seq(
        PaymentsHistoryModel(
          chargeType    = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.parse("2017-01-01")),
          taxPeriodTo   = Some(LocalDate.parse("2017-02-01")),
          amount        = 123456789,
          clearedDate   = Some(LocalDate.parse("2017-03-01"))
        ),
        PaymentsHistoryModel(
          chargeType    = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.parse("2017-03-01")),
          taxPeriodTo   = Some(LocalDate.parse("2017-04-01")),
          amount        = 987654321,
          clearedDate   = Some(LocalDate.parse("2017-05-01"))
        )
      ))

    val displayedYears = Seq(2018, 2017)
    lazy val examplePaymentHistory: ServiceResponse[PaymentsHistoryViewModel] =
      Right(PaymentsHistoryViewModel(
        displayedYears,
        targetYear,
        Seq(
          PaymentsHistoryModel(
            chargeType    = ReturnDebitCharge,
            taxPeriodFrom = Some(LocalDate.parse("2018-01-01")),
            taxPeriodTo   = Some(LocalDate.parse("2018-02-01")),
            amount        = 123456789,
            clearedDate   = Some(LocalDate.parse("2018-03-01"))
          ),
          PaymentsHistoryModel(
            chargeType    = ReturnDebitCharge,
            taxPeriodFrom = Some(LocalDate.parse("2018-03-01")),
            taxPeriodTo   = Some(LocalDate.parse("2018-04-01")),
            amount        = 987654321,
            clearedDate   = Some(LocalDate.parse("2018-05-01"))
          )
        ),
        migratedToETMPWithin15M = false
      ))

    val authResult: Future[_] =
      Future.successful(Enrolments(Set(
        Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VRN", "123456789")), "")
      )))

    val serviceCall: Boolean = true
    val accountDetailsCall: Boolean = false
    val authCall: Boolean = true
    val targetYear: Int = 2018
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val accountDetailsResponse: HttpGetResult[CustomerInformation] = Right(customerInformation)
    val mockAccountDetailsService: AccountDetailsService = mock[AccountDetailsService]
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockPaymentsService: PaymentsService = mock[PaymentsService]
    val mockVatSubscriptionConnector: VatSubscriptionConnector = mock[VatSubscriptionConnector]
    val mockDateService: DateService = mock[DateService]
    val mockAuditService: AuditingService = mock[AuditingService]
    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
    val mockHybridUserPredicate: HybridUserPredicate = new HybridUserPredicate(mockAccountDetailsService)
    val mockAuthorisedController: AuthorisedController = new AuthorisedController(
      messages,
      mockEnrolmentsAuthService,
      mockHybridUserPredicate,
      mockAppConfig
    )

    def setup(): Any = {
      (mockDateService.now: () => LocalDate)
        .stubs()
        .returns(LocalDate.parse("2018-05-01"))

      if (authCall) {
        (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *)
          .returns(authResult)
        (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(accountDetailsResponse)
      }

      if (serviceCall) {
        (mockAuditService.extendedAudit(_: ExtendedAuditModel, _: String)(_: HeaderCarrier, _: ExecutionContext))
          .stubs(*, *, *, *)
          .returns({})

        (mockPaymentsService.getPaymentsHistory(_: User, _: Int)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *).noMoreThanOnce()
          .returns(serviceResultYearOne)

        (mockPaymentsService.getPaymentsHistory(_: User, _: Int)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *).noMoreThanOnce()
          .returns(serviceResultYearTwo)
      }

      if(accountDetailsCall) {
        (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(accountDetailsResponse)
      }
    }

    def target: PaymentHistoryController = {
      setup()
      new PaymentHistoryController(
        messages,
        mockPaymentsService,
        mockAuthorisedController,
        mockDateService,
        mockEnrolmentsAuthService,
        mockAccountDetailsService,
        mockAppConfig,
        mockAuditService)
    }
  }

  "Calling the paymentHistory action" when {

    "the user is logged in and there is no customerMigratedToETMPDate in session" when {

      "return 200" in new Test {
        override val accountDetailsCall: Boolean = true
        private val result = target.paymentHistory(targetYear)(fakeRequest)
        status(result) shouldBe Status.OK
      }

      "return HTML" in new Test {
        override val accountDetailsCall: Boolean = true
        private val result = target.paymentHistory(targetYear)(fakeRequest)
        contentType(result) shouldBe Some("text/html")
      }

      "return charset utf-8" in new Test {
        override val accountDetailsCall: Boolean = true
        private val result = target.paymentHistory(targetYear)(fakeRequest)
        charset(result) shouldBe Some("utf-8")
      }

      "put the customerMigratedToETMPDate in session" in new Test {
        override val accountDetailsCall: Boolean = true
        private val result = target.paymentHistory(targetYear)(fakeRequest)
        session(result).get("customerMigratedToETMPDate") shouldBe Some("2019-01-01")
      }
    }

    "the user is logged in and there is a customerMigratedToETMPDate in session" should {

      "return 200" in new Test {
        private val result = target.paymentHistory(targetYear)(fakeRequestWithCustomerMigratedDate("2019-01-01"))
        status(result) shouldBe Status.OK
      }
    }

    "the user is logged in and there is an empty string in customerMigratedToETMPDate in session" should {

      "return 200" in new Test {
        private val result = target.paymentHistory(targetYear)(fakeRequestWithCustomerMigratedDate(""))
        status(result) shouldBe Status.OK
      }
    }

    "the user is not logged in" should {

      "return 401 (Unauthorised)" in new Test {
        override def setup(): Unit = {
          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *, *)
            .returns(Future.failed(MissingBearerToken()))
        }
        val result: Future[Result] = target.paymentHistory(targetYear)(fakeRequest)
        status(result) shouldBe Status.UNAUTHORIZED
      }
    }

    "the user is not authenticated" should {

      "return 403 (Forbidden)" in new Test {
        override def setup(): Unit = {
          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *, *)
            .returns(Future.failed(InsufficientEnrolments()))
        }
        val result: Future[Result] = target.paymentHistory(targetYear)(fakeRequest)
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "user is hybrid" should {

      "redirect to VAT overview page" in new Test {

        override val accountDetailsResponse: Right[Nothing, CustomerInformation] = Right(customerInformationHybrid)

        override def setup(): Unit = {
          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *, *)
            .returns(authResult)

          (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(accountDetailsResponse)
        }

        private val result = target.paymentHistory(targetYear)(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.VatDetailsController.details().url)
      }
    }

    "the call to retrieve hybrid status fails" should {

      "return Internal Server Error" in new Test {

        override def setup(): Unit = {

          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *, *)
            .returns(authResult)

          (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Left(UnknownError))
        }

        private val result = target.paymentHistory(targetYear)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }

    "the user enters an invalid search year for their payment history" should {

      "return 404 (Not Found)" in new Test {
        override val accountDetailsCall: Boolean = true
        override val serviceCall = false
        override val targetYear = 2021
        private val result = target.paymentHistory(targetYear)(fakeRequest)
        status(result) shouldBe Status.NOT_FOUND
      }
    }

    "an error occurs upstream" should {

      "return a 500" in new Test {
        override val accountDetailsCall: Boolean = true
        override val serviceResultYearOne = Left(VatLiabilitiesError)
        override val serviceResultYearTwo = Left(VatLiabilitiesError)
        private val result: Result = await(target.paymentHistory(targetYear)(fakeRequest))

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

      "return the standard error view" in new Test {
        override val accountDetailsCall: Boolean = true
        override val serviceResultYearOne = Left(VatLiabilitiesError)
        override val serviceResultYearTwo = Left(VatLiabilitiesError)
        private val result: Result = await(target.paymentHistory(targetYear)(fakeRequest))

        val document: Document = Jsoup.parse(bodyOf(result))

        document.select("h1").first().text() shouldBe "Sorry, there is a problem with the service"
      }
    }
  }

  "Calling the getFinancialTransactions function" when {

    "the user has no transactions for a specific year" should {

      "return the correct year tabs" in new Test {
        override val authCall = false
        override val serviceResultYearTwo = Left(VatLiabilitiesError)
        override val displayedYears = Seq(2018)
        private val result = await(target.getFinancialTransactions(user, targetYear, migratedWithin15M = false))
        result shouldBe examplePaymentHistory
      }
    }

    "the PaymentsService retrieves a valid PaymentHistoryModel" should {

      "return the PaymentHistoryModel" in new Test {
        override val authCall = false
        private val result = await(target.getFinancialTransactions(user, targetYear, migratedWithin15M = false))
        result shouldBe examplePaymentHistory
      }
    }

    "the PaymentsService returns a Left" should {

      "return the Left" in new Test {
        override val authCall = false
        override val serviceResultYearOne = Left(VatLiabilitiesError)
        override val serviceResultYearTwo = Left(VatLiabilitiesError)
        private val result = await(target.getFinancialTransactions(user, targetYear, migratedWithin15M = false))
        result shouldBe Left(VatLiabilitiesError)
      }
    }

    "the selected year does not exist in the potential payment years" should {

      "return a Right(PaymentHistoryViewModel) with an empty sequence of transactions" in new Test {
        override val authCall: Boolean = false
        private val result = await(target.getFinancialTransactions(user, targetYear - 2, migratedWithin15M = false))
        result shouldBe
          Right(PaymentsHistoryViewModel(displayedYears, targetYear - 2, Seq.empty, migratedToETMPWithin15M = false))
      }
    }
  }

  "Calling .customerMigratedWithin15M" when {

    "the interval between dates is less than 15 months" should {

      "return true" in new Test {
        override val authCall = false
        target.customerMigratedWithin15M(LocalDate.parse("2017-02-02")) shouldBe true
      }
    }

    "the interval between dates is 15 months or greater" should {

      "return false" in new Test {
        override val authCall = false
        target.customerMigratedWithin15M(LocalDate.parse("2017-02-01")) shouldBe false
      }
    }

    "the interval is 0 days" should {

      "return true" in new Test {
        override val authCall = false
        target.customerMigratedWithin15M(LocalDate.parse("2018-05-01")) shouldBe true
      }
    }
  }

  "Calling .isValidSearchYear" when {

    val currentYear = 2018

    "the user was not migrated to ETMP within 15 months" when {

      "they do not have the VATDEC enrolment" when {

        "the year is on the upper search boundary" should {

          "return true" in new Test {
            override val authResult: Future[_] = Future.successful("")
            override def setup(): Any = "" // Prevent the unused mocks causing trouble
            val result: Boolean = target.isValidSearchYear(currentYear, currentYear, migratedWithin15M = false)
            result shouldBe true
          }
        }

        "the year is above the upper search boundary" should {

          "return false" in new Test {
            override val authResult: Future[_] = Future.successful("")
            override def setup(): Any = "" // Prevent the unused mocks causing trouble
            val result: Boolean = target.isValidSearchYear(currentYear + 1, currentYear, migratedWithin15M = false)
            result shouldBe false
          }
        }

        "the year is on the lower boundary" should {

          "return true" in new Test {
            override val authResult: Future[_] = Future.successful("")
            override def setup(): Any = "" // Prevent the unused mocks causing trouble
            val result: Boolean = target.isValidSearchYear(currentYear - 1, currentYear, migratedWithin15M = false)
            result shouldBe true
          }
        }

        "the year is below the lower boundary" should {

          "return false" in new Test {
            override val authResult: Future[_] = Future.successful("")
            override def setup(): Any = "" // Prevent the unused mocks causing trouble
            val result: Boolean = target.isValidSearchYear(currentYear - 3, currentYear, migratedWithin15M = false)
            result shouldBe false
          }
        }

        "the year is between the upper and lower boundaries" should {

          "return true" in new Test {
            override val authResult: Future[_] = Future.successful("")
            override def setup(): Any = "" // Prevent the unused mocks causing trouble
            val result: Boolean = target.isValidSearchYear(currentYear - 1, currentYear, migratedWithin15M = false)
            result shouldBe true
          }
        }
      }

      "they do have the VATDEC enrolment" when {


        "the year is two years below the upper boundary" should {

          "return false" in new Test {
            override val authResult: Future[_] = Future.successful("")
            override def setup(): Any = "" // Prevent the unused mocks causing trouble
            val result: Boolean =
              target.isValidSearchYear(currentYear - 2, currentYear, migratedWithin15M = false)(vatDecUser)
            result shouldBe false
          }
        }
      }
    }

    "the user was migrated to ETMP within 15 months" when {

      "they do not have the VATDEC enrolment" when {

        "the year is two years below the upper boundary" should {

          "return false" in new Test {
            override val authResult: Future[_] = Future.successful("")
            override def setup(): Any = "" // Prevent the unused mocks causing trouble
            val result: Boolean = target.isValidSearchYear(currentYear - 2, currentYear, migratedWithin15M = true)
            result shouldBe false
          }
        }
      }

      "they do have the VATDEC enrolment" when {

        "the year is two years below the upper boundary" should {

          "return true" in new Test {
            override val authResult: Future[_] = Future.successful("")
            override def setup(): Any = "" // Prevent the unused mocks causing trouble
            val result: Boolean =
              target.isValidSearchYear(currentYear - 2, currentYear, migratedWithin15M = true)(vatDecUser)
            result shouldBe true
          }
        }
      }
    }
  }
}
