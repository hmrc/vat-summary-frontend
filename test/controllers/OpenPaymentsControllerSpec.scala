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

import java.time.LocalDate

import audit.AuditingService
import audit.models.ExtendedAuditModel
import common.TestModels._
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.errors.{DirectDebitStatusError, PaymentsError, UnknownError}
import models.payments._
import models.viewModels.OpenPaymentsViewModel
import models.{CustomerInformation, User}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.http.Status
import play.api.mvc.{Request, Result}
import play.api.test.Helpers.{redirectLocation, _}
import play.twirl.api.Html
import services._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.errors.PaymentsError
import views.html.payments.{NoPayments, OpenPayments}

import scala.concurrent.{ExecutionContext, Future}

class OpenPaymentsControllerSpec extends ControllerBaseSpec {

  private trait Test {
    val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = successfulAuthResult
    val serviceInfoServiceResult: Future[Html] = Future.successful(Html(""))
    val accountDetailsResponse: HttpGetResult[CustomerInformation] = Right(customerInformationMax)
    val mockServiceInfoService: ServiceInfoService = mock[ServiceInfoService]

    def setupMocks(): Unit = {
      (mockDateService.now: () => LocalDate).stubs().returns(LocalDate.parse("2018-05-01"))


      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(authResult)

      (mockAuditService.extendedAudit(_: ExtendedAuditModel, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *, *)
        .returns({})

      (mockServiceInfoService.getPartial(_: Request[_], _: User, _: ExecutionContext))
        .stubs(*, *, *)
        .returns(serviceInfoServiceResult)
    }

    val payment: PaymentWithPeriod = Payment(
      ReturnDebitCharge,
      LocalDate.parse("2017-01-01"),
      LocalDate.parse("2017-01-01"),
      LocalDate.parse("2017-01-01"),
      BigDecimal("10000"),
      Some("ABCD"),
      chargeReference = Some("XD002750002155"),
      ddCollectionInProgress = false
    )

    val paymentOnAccount: PaymentNoPeriod = Payment(
      PaymentOnAccount,
      LocalDate.parse("2017-01-01"),
      BigDecimal("0"),
      None,
      chargeReference = Some("XD002750002155"),
      ddCollectionInProgress = false
    )

    val mockDateService: DateService = mock[DateService]
    val mockPaymentsService: PaymentsService = mock[PaymentsService]
    val mockAuditService: AuditingService = mock[AuditingService]
    val NoPayments: NoPayments = injector.instanceOf[NoPayments]
    val mockPaymentsError: PaymentsError = injector.instanceOf[PaymentsError]
    val openPayments: OpenPayments = injector.instanceOf[OpenPayments]
    val mockAuthorisedController: AuthorisedController = new AuthorisedController(
      mcc,
      enrolmentsAuthService,
      hybridUserPredicate,
      agentPredicate,
      mockAccountDetailsService,
      mockServiceErrorHandler,
      mockAppConfig,
      ec,
      unauthorised
    )

    val testUser: User = User("999999999")
    implicit val hc: HeaderCarrier = HeaderCarrier()

    def target: OpenPaymentsController = {
      setupMocks()
      new OpenPaymentsController(
        enrolmentsAuthService,
        mockAuthorisedController,
        mockServiceInfoService,
        mockPaymentsService,
        mockDateService,
        mockAppConfig,
        mockAuditService,
        mcc,
        ec,
        NoPayments,
        mockPaymentsError,
        openPayments
      )
    }
  }

  "Calling the openPayments action" when {

    "user is hybrid" should {

      "redirect to VAT overview page" in new Test {

        override val accountDetailsResponse: Right[Nothing, CustomerInformation] = Right(customerInformationHybrid)

        override def setupMocks(): Unit = {
          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *, *)
            .returns(authResult)

          (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(accountDetailsResponse)
        }

        private val result = target.openPayments()(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.VatDetailsController.details().url)
      }
    }

    "user is not hybrid" when {

      "the user has open payments" should {

        "return 200 (OK)" in new Test {
          override def setupMocks(): Unit = {
            super.setupMocks()
            (mockPaymentsService.getDirectDebitStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .stubs(*, *, *)
              .returns(Right(true))

            (mockPaymentsService.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *)
              .returns(Future.successful(Right(Some(Payments(Seq(payment, payment))))))

            (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *)
              .returns(accountDetailsResponse)
          }

          private val result = target.openPayments()(fakeRequest)

          status(result) shouldBe Status.OK
        }

        "return the payments view" in new Test {
          override def setupMocks(): Unit = {
            super.setupMocks()
            (mockPaymentsService.getDirectDebitStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .stubs(*, *, *)
              .returns(Right(true))

            (mockPaymentsService.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *)
              .returns(Future.successful(Right(Some(Payments(Seq(payment, payment))))))

            (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *)
              .returns(accountDetailsResponse)
          }

          val result: Result = await(target.openPayments()(fakeRequest))
          val document: Document = Jsoup.parse(bodyOf(result))

          document.select("h1").first().text() shouldBe "What you owe"
        }
      }

      "the user has a Payment On Account charge returned" should {

        "return the payments view with only one payment listed" in new Test {

          override def setupMocks(): Unit = {
            super.setupMocks()
            (mockPaymentsService.getDirectDebitStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .stubs(*, *, *)
              .returns(Right(true))

            (mockPaymentsService.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *)
              .returns(Future.successful(Right(Some(Payments(Seq(payment, paymentOnAccount))))))

            (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *)
              .returns(accountDetailsResponse)
          }

          val result: Result = await(target.openPayments()(fakeRequest))
          val document: Document = Jsoup.parse(bodyOf(result))

          document.select("payment-2") shouldBe empty

        }
      }

      "the user has no open payments" should {

        "return 200 (OK)" in new Test {
          override def setupMocks(): Unit = {
            super.setupMocks()
            (mockPaymentsService.getDirectDebitStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .stubs(*, *, *)
              .returns(Right(true))

            (mockPaymentsService.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *)
              .returns(Future.successful(Right(None)))

            (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *)
              .returns(accountDetailsResponse)
          }

          private val result = target.openPayments()(fakeRequest)

          status(result) shouldBe Status.OK
        }

        "return the payments view" in new Test {
          override def setupMocks(): Unit = {
            super.setupMocks()
            (mockPaymentsService.getDirectDebitStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .stubs(*, *, *)
              .returns(Right(true))

            (mockPaymentsService.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *)
              .returns(Future.successful(Right(None)))

            (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *)
              .returns(accountDetailsResponse)
          }

          val result: Result = await(target.openPayments()(fakeRequest))
          val document: Document = Jsoup.parse(bodyOf(result))

          document.select("h1").first().text() shouldBe "What you owe"
        }
      }

      "the user is not authorised" should {

        "return 403 (Forbidden)" in new Test {
          override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())
          private val result = target.openPayments()(fakeRequest)
          status(result) shouldBe Status.FORBIDDEN
        }
      }

      "user is an Agent" should {

        "redirect to Agent Hub page" in new Test {
          override val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = agentAuthResult
          val result: Future[Result] = target.openPayments()(fakeRequest)

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(mockAppConfig.agentClientLookupHubUrl)
        }
      }

      "the user is not signed in" should {

        "redirect to sign in" in new Test {
          override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())
          private val result = target.openPayments()(fakeRequest)

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
        }
      }

      "the paymentsService returns an error" should {

        "return the payments view" in new Test {
          override def setupMocks(): Unit = {
            super.setupMocks()
            (mockPaymentsService.getDirectDebitStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *)
              .returns(Left(DirectDebitStatusError))

            (mockPaymentsService.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *)
              .returns(Future.successful(Right(Some(Payments(Seq(payment, payment))))))

            (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *)
              .returns(accountDetailsResponse)
          }

          val result: Result = await(target.openPayments()(fakeRequest))
          val document: Document = Jsoup.parse(bodyOf(result))

          document.select("h1").first().text() shouldBe "What you owe"
        }
      }

      "an error occurs upstream" should {

        "return 500 (Internal server error)" in new Test {
          override def setupMocks(): Unit = {
            super.setupMocks()
            (mockPaymentsService.getDirectDebitStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .stubs(*, *, *)
              .returns(Right(true))

            (mockPaymentsService.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *)
              .returns(Future.successful(Left(PaymentsError)))

            (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *)
              .returns(accountDetailsResponse)
          }

          private val result = target.openPayments()(fakeRequest)

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "return the payments error view" in new Test {

          override def setupMocks(): Unit = {
            super.setupMocks()
            (mockPaymentsService.getDirectDebitStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .stubs(*, *, *)
              .returns(Right(true))

            (mockPaymentsService.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *)
              .returns(Future.successful(Left(PaymentsError)))

            (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
              .expects(*, *, *)
              .returns(accountDetailsResponse)
          }

          val result: Result = await(target.openPayments()(fakeRequest))
          val document: Document = Jsoup.parse(bodyOf(result))


          document.select("h1").first().text() shouldBe "Sorry, there is a problem with the service"
        }
      }
    }

    "the call to retrieve hybrid status fails" should {

      "return Internal Server Error" in new Test {

        override def setupMocks(): Unit = {

          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *, *)
            .returns(authResult)

          (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Left(UnknownError))
        }

        private val result = target.openPayments()(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }

    "Calling the .getModel function" when {

      "ddCollectionInProgressEnabled feature switch is on" when {

        "due date of payments is in the past" when {

          "user has direct debit collections in progress" should {

            "return payments that are not overdue" in new Test {

              override def setupMocks(): Unit = (
                mockDateService.now: () => LocalDate).stubs().returns(LocalDate.parse("2018-05-01")
              )

              val testPayment: PaymentWithPeriod = Payment(
                ReturnDebitCharge,
                LocalDate.parse("2017-01-01"),
                LocalDate.parse("2017-01-01"),
                due = LocalDate.parse("2017-01-01"),
                BigDecimal("10000"),
                Some("ABCD"),
                chargeReference = None,
                ddCollectionInProgress = true
              )

              val expected: OpenPaymentsViewModel = OpenPaymentsViewModel(
                Seq(OpenPaymentsModel(
                  testPayment.chargeType,
                  testPayment.outstandingAmount,
                  testPayment.due,
                  testPayment.periodFrom,
                  testPayment.periodTo,
                  testPayment.periodKey,
                  isOverdue = false
                )),
                Some(true)
              )
              val result: OpenPaymentsViewModel = target.getModel(Seq(testPayment), Some(true))

              result shouldBe expected
            }
          }

          "user has no direct debit collections in progress" should {

            "return payments that are overdue" in new Test {

              override def setupMocks(): Unit = (
                mockDateService.now: () => LocalDate).stubs().returns(LocalDate.parse("2018-05-01")
              )

              val testPayment: PaymentWithPeriod = Payment(
                ReturnDebitCharge,
                LocalDate.parse("2017-01-01"),
                LocalDate.parse("2017-01-01"),
                due = LocalDate.parse("2017-01-01"),
                BigDecimal("10000"),
                Some("ABCD"),
                chargeReference = None,
                ddCollectionInProgress = false
              )

              val expected: OpenPaymentsViewModel = OpenPaymentsViewModel(
                Seq(OpenPaymentsModel(
                  testPayment.chargeType,
                  testPayment.outstandingAmount,
                  testPayment.due,
                  testPayment.periodFrom,
                  testPayment.periodTo,
                  testPayment.periodKey,
                  isOverdue = true
                )),
                Some(true)
              )
              val result: OpenPaymentsViewModel = target.getModel(Seq(testPayment), Some(true))

              result shouldBe expected
            }
          }
        }

        "due date of payments is in the future" should {

          "return payments that are not overdue" in new Test {

            override def setupMocks(): Unit = (
              mockDateService.now: () => LocalDate).stubs().returns(LocalDate.parse("2018-05-01")
            )

            val testPayment: PaymentWithPeriod = Payment(
              ReturnDebitCharge,
              LocalDate.parse("2017-01-01"),
              LocalDate.parse("2017-01-01"),
              due = LocalDate.parse("2020-01-01"),
              BigDecimal("10000"),
              Some("ABCD"),
              chargeReference = None,
              ddCollectionInProgress = false
            )

            val expected: OpenPaymentsViewModel = OpenPaymentsViewModel(
              Seq(OpenPaymentsModel(
                testPayment.chargeType,
                testPayment.outstandingAmount,
                testPayment.due,
                testPayment.periodFrom,
                testPayment.periodTo,
                testPayment.periodKey,
                isOverdue = false
              )),
              Some(true)
            )
            val result: OpenPaymentsViewModel = target.getModel(Seq(testPayment), Some(true))

            result shouldBe expected
          }
        }
      }

      "ddCollectionInProgressEnabled feature switch is off" when {

        "due date of payments are in the past and have no direct debit" should {

          "return payments that are not overdue" in new Test {

            mockAppConfig.features.ddCollectionInProgressEnabled(false)

            override def setupMocks(): Unit = (
              mockDateService.now: () => LocalDate).stubs().returns(LocalDate.parse("2018-05-01")
            )

            val testPayment: PaymentWithPeriod = Payment(
              ReturnDebitCharge,
              LocalDate.parse("2017-01-01"),
              LocalDate.parse("2017-01-01"),
              due = LocalDate.parse("2017-01-01"),
              BigDecimal("10000"),
              Some("ABCD"),
              chargeReference = None,
              ddCollectionInProgress = false
            )

            val expected: OpenPaymentsViewModel = OpenPaymentsViewModel(
              Seq(OpenPaymentsModel(
                testPayment.chargeType,
                testPayment.outstandingAmount,
                testPayment.due,
                testPayment.periodFrom,
                testPayment.periodTo,
                testPayment.periodKey,
                isOverdue = false
              )),
              Some(true)
            )
            val result: OpenPaymentsViewModel = target.getModel(Seq(testPayment), Some(true))

            result shouldBe expected
          }
        }
      }
    }
    "the user is insolvent and not continuing to trade" should {

      "return 403 (Forbidden)" in new Test {

        authResult
        private val result = target.openPayments()(insolventRequest)
        status(result) shouldBe Status.FORBIDDEN
      }
    }
  }
}
