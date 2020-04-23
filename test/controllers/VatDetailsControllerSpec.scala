/*
 * Copyright 2020 HM Revenue & Customs
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
import common.TestModels._
import common.{SessionKeys, TestModels}
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import controllers.predicates.{AgentPredicate, HybridUserPredicate}
import models._
import models.errors.{BadRequestError, NextPaymentError, ObligationsError}
import models.obligations.{VatReturnObligation, VatReturnObligations}
import models.payments.{Payment, PaymentNoPeriod, Payments, ReturnDebitCharge}
import models.viewModels.VatDetailsViewModel
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, Request, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class VatDetailsControllerSpec extends ControllerBaseSpec {

  private trait DetailsTest {

    val obligations: VatReturnObligations = TestModels.obligations
    val payments: Payments = TestModels.payments

    val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = successfulAuthResult
    val vatServiceReturnsResult: Future[ServiceResponse[Option[VatReturnObligations]]] = Future.successful(Right(Some(obligations)))
    val vatServicePaymentsResult: Future[ServiceResponse[Option[Payments]]] = Future.successful(Right(Some(payments)))
    val accountDetailsServiceResult: Future[HttpGetResult[CustomerInformation]] = Future.successful(Right(customerInformationMax))
    val mandationStatusServiceResult: Future[HttpGetResult[MandationStatus]] = Future.successful(Right(validMandationStatus))
    val serviceInfoServiceResult: Future[Html] = Future.successful(Html(""))

    val mockServiceInfoService: ServiceInfoService = mock[ServiceInfoService]
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockVatDetailsService: VatDetailsService = mock[VatDetailsService]
    val mockAccountDetailsService: AccountDetailsService = mock[AccountDetailsService]
    val mockDateService: DateService = mock[DateService]
    val mockAuditService: AuditingService = mock[AuditingService]
    val mockHybridUserPredicate: HybridUserPredicate = new HybridUserPredicate(mockAccountDetailsService, mockServiceErrorHandler)
    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
    val mockAgentPredicate: AgentPredicate = new AgentPredicate(mockEnrolmentsAuthService, messages, mockAppConfig)
    val mockMandationService: MandationStatusService = mock[MandationStatusService]

    def setup(needMandationCall: Boolean = true): Any = {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *, *)
        .returns(authResult)

      (mockDateService.now: () => LocalDate).stubs().returns(LocalDate.parse("2018-05-01"))

      (mockVatDetailsService.getReturnObligations(_: String, _: LocalDate)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *, *)
        .returns(vatServiceReturnsResult)

      (mockVatDetailsService.getPaymentObligations(_: String)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *)
        .returns(vatServicePaymentsResult)

      (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *)
        .returns(accountDetailsServiceResult)

      (mockAuditService.audit(_: AuditModel, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *, *)
        .returns({})

      (mockServiceInfoService.getPartial(_: Request[_], _: User, _: ExecutionContext))
        .stubs(*,*,*)
        .returns(serviceInfoServiceResult)

      if(needMandationCall) {
        (mockMandationService.getMandationStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returning(mandationStatusServiceResult).anyNumberOfTimes()
      }
    }

    val mockAuthorisedController: AuthorisedController = new AuthorisedController(
      messages,
      mockEnrolmentsAuthService,
      mockHybridUserPredicate,
      mockAgentPredicate,
      mockAppConfig
    )

    mockAppConfig.features.submitReturnFeatures(true)

    def target(needMandationCall: Boolean = true): VatDetailsController = {
      setup(needMandationCall)
      new VatDetailsController(messages,
        mockEnrolmentsAuthService,
        mockAppConfig,
        mockVatDetailsService,
        mockServiceInfoService,
        mockAuthorisedController,
        mockAccountDetailsService,
        mockDateService,
        mockAuditService,
        mockMandationService)
    }
  }

  "Calling the details action" when {

    "the user is logged in and does not have a customerMigratedToETMPDate in session" should {

      "return 200" in new DetailsTest {
        private val result = target().details()(fakeRequest)
        status(result) shouldBe Status.OK
      }

      "return HTML" in new DetailsTest {
        private val result = target().details()(fakeRequest)
        contentType(result) shouldBe Some("text/html")
      }

      "return charset utf-8" in new DetailsTest {
        private val result = target().details()(fakeRequest)
        charset(result) shouldBe Some("utf-8")
      }

      "put a customerMigratedToETMPDate key into the session" in new DetailsTest {
        private val result = target().details()(fakeRequest)
        session(result).get(SessionKeys.migrationToETMP) shouldBe Some("2017-05-05")
      }

      "put a mandation status in the session" in new DetailsTest {
        private val result = target().details()(fakeRequest)
        session(result).get(SessionKeys.mandationStatus) shouldBe Some("MTDfB")
      }

      "not overwrite the mandation status in the session" in new DetailsTest {
        val fakeRequestWithSession: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(
          SessionKeys.mandationStatus -> "Non MTDfB")

        private val result = target().details()(fakeRequestWithSession)
        session(result).get(SessionKeys.mandationStatus) shouldBe Some("Non MTDfB")
      }

      "not put the mandation status in the session if there is an error" in new DetailsTest {
        (mockMandationService.getMandationStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returning(Future.successful(Left(BadRequestError("", ""))))

        private val result = target(false).details()(fakeRequestWithSession)
        session(result).get(SessionKeys.mandationStatus) shouldBe None

      }
    }

    "the user is logged in and has a customerMigratedToETMPDate in session" should {

      "return 200" in new DetailsTest {
        private val result = target().details()(fakeRequestWithSession)
        status(result) shouldBe Status.OK
      }

      "return HTML" in new DetailsTest {
        private val result = target().details()(fakeRequestWithSession)
        contentType(result) shouldBe Some("text/html")
      }

      "return charset utf-8" in new DetailsTest {
        private val result = target().details()(fakeRequestWithSession)
        charset(result) shouldBe Some("utf-8")
      }

      "not overwrite the customerMigratedToETMPDate value in the session" in new DetailsTest {
        private val result = target().details()(fakeRequestWithSession)
        session(result).get(SessionKeys.migrationToETMP) shouldBe Some("2018-01-01")
      }
    }

    "the user is not logged in" should {

      "return SEE_OTHER" in new DetailsTest {
        override val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = Future.failed(MissingBearerToken())
        val result: Future[Result] = target().details()(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
      }
    }

    "the user does not have sufficient enrolments" should {

      "return 403 (Forbidden)" in new DetailsTest {
        override val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = Future.failed(InsufficientEnrolments())
        val result: Future[Result] = target().details()(fakeRequest)
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "the user is not authenticated" should {

      "return 403 (Forbidden)" in new DetailsTest {
        override val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = Future.failed(InsufficientConfidenceLevel())
        val result: Future[Result] = target().details()(fakeRequest)
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "user is an Agent" should {

      "redirect to Agent Action page" in new DetailsTest {
        override val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = agentAuthResult
        val result: Future[Result] = target().details()(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(mockAppConfig.agentClientLookupActionUrl)
      }
    }

    "the user is hybrid" should {

      "not attempt to retrieve payment obligations" in new DetailsTest {
        mockAppConfig.features.submitReturnFeatures(true)

        override val accountDetailsServiceResult: Future[HttpGetResult[CustomerInformation]] =
          Future.successful(Right(customerInformationHybrid))

        override def setup(needMandationCall: Boolean = true): Unit = {

          (mockServiceInfoService.getPartial(_: Request[_], _: User,  _: ExecutionContext))
            .stubs(*,*,*)
            .returns(serviceInfoServiceResult)

          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .stubs(*, *, *, *)
            .returns(authResult)

          (mockDateService.now: () => LocalDate).stubs().returns(LocalDate.parse("2018-05-01"))

          (mockAccountDetailsService.getAccountDetails(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .stubs(*, *, *)
            .returns(accountDetailsServiceResult)

          (mockVatDetailsService.getReturnObligations(_: String, _: LocalDate)(_: HeaderCarrier, _: ExecutionContext))
            .stubs(*, *, *, *)
            .returns(vatServiceReturnsResult)

          (mockVatDetailsService.getPaymentObligations(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .stubs(*, *, *)
            .never()

          (mockAuditService.audit(_: AuditModel, _: String)(_: HeaderCarrier, _: ExecutionContext))
            .stubs(*, *, *, *)
            .returns({})

          (mockMandationService.getMandationStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returning(mandationStatusServiceResult).once()
        }

        val result: Future[Result] = target().details()(fakeRequest)
        status(result) shouldBe Status.OK
      }
    }

    "the submit return feature switch is turned off" should {
      "the View Returns link should be displayed" in new DetailsTest {
        mockAppConfig.features.submitReturnFeatures(false)
        lazy val result: Future[Result] = target().details()(fakeRequest)
        status(result) shouldBe OK
        await(bodyOf(result)).contains(messages("returnObligation.viewReturns")) shouldBe true
      }
    }

    "the submit return feature switch is turned on" should {
      "return a VatDetailsViewModel as a non MTDfB user" in new DetailsTest {
        (mockMandationService.getMandationStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(Right(MandationStatus("Non MTDfB"))))

        mockAppConfig.features.submitReturnFeatures(true)
        lazy val result: Future[Result] = target().details()(fakeRequest)
        status(result) shouldBe OK
        await(bodyOf(result)).contains(messages("returnObligation.submit")) shouldBe true
      }

      "return a VatDetailsViewModel as a MTDfB user if no mandation status is returned" in new DetailsTest {
        (mockMandationService.getMandationStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(Left(BadRequestError("AN ERROR", "HAS OCCURRED"))))

        mockAppConfig.features.submitReturnFeatures(true)
        lazy val result: Future[Result] = target().details()(fakeRequest)
        status(result) shouldBe OK
        await(bodyOf(result)).contains(messages("returnObligation.submit")) shouldBe false
        await(bodyOf(result)).contains(messages("returnObligation.viewReturns")) shouldBe false
      }
    }
  }

  "Calling .constructViewModel with a VatDetailsModel" when {

    lazy val paymentDueDate: Option[String] = Some("2019-03-03")
    lazy val obligationData: Option[String] = Some("2019-06-06")

    "there is both a payment and an obligation" should {

      "return a VatDetailsViewModel with both due dates" in new DetailsTest {
        lazy val expected =
          VatDetailsViewModel(
            paymentDueDate, obligationData, Some(entityName), deregDate = Some(LocalDate.parse("2020-01-01")), currentDate = testDate, partyType = Some("7")
          )
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(Some(obligations)),
          Right(Some(payments)),
          Right(customerInformationMax),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }

    "there is a payment but no obligation" should {

      "return a VatDetailsViewModel with a payment due date and no obligation due date" in new DetailsTest {
        lazy val expected =
          VatDetailsViewModel(
            paymentDueDate, None, Some(entityName), deregDate = Some(LocalDate.parse("2020-01-01")), currentDate = testDate, partyType = Some("7")
          )
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(None),
          Right(Some(payments)),
          Right(customerInformationMax),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }

    "there is an obligation but no payment" should {

      "return a VatDetailsViewModel with an obligation due date and no payment due date" in new DetailsTest {
        lazy val expected =
          VatDetailsViewModel(
            None, obligationData, Some(entityName), deregDate = Some(LocalDate.parse("2020-01-01")), currentDate = testDate, partyType = Some("7")
          )
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(Some(obligations)),
          Right(None),
          Right(customerInformationMax),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }

    "there is no obligation or payment" should {

      "return a VatDetailsViewModel with no obligation due date and no payment due date" in new DetailsTest {
        lazy val expected =
          VatDetailsViewModel(
            None, None, Some(entityName), deregDate = Some(LocalDate.parse("2020-01-01")), currentDate = testDate, partyType = Some("7")
          )
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(None),
          Right(None),
          Right(customerInformationMax),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }

    "there is no obligation, payment, or entity name" should {

      "return a VatDetailsViewModel with no obligation due date, payment due date, entity name or partyType" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(None, None, None, customerInfoError = true, currentDate = testDate, partyType = None)
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(None),
          Right(None),
          Right(customerInformationMin),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }

    "there is no obligation, payment, entity name or partyType with a non-MTDfB user" should {

      "return a VatDetailsViewModel with no obligation due date, payment due date, or entity name with the isNonMTDfBOrNonDigital flag set to true" in
        new DetailsTest {
        lazy val expected = VatDetailsViewModel(
          None, None, None, isNonMTDfBUser = Some(true), isNonMTDfBOrNonDigitalUser = Some(true), customerInfoError = true, currentDate = testDate, partyType = None)
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(None),
          Right(None),
          Right(customerInformationMin),
          Right(validNonMTDfBMandationStatus)
        )
        result shouldBe expected
      }
    }

    "there is no obligation, payment, entity name or partyType with a non-Digital user" should {

      "return a VatDetailsViewModel with no obligation due date, payment due date, or entity name with the isNonMTDfBOrNonDigital flag set to true" in
        new DetailsTest {
        lazy val expected = VatDetailsViewModel(
          None, None, None, isNonMTDfBOrNonDigitalUser = Some(true), customerInfoError = true, currentDate = testDate, partyType = None)
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(None),
          Right(None),
          Right(customerInformationMin),
          Right(validNonDigitalMandationStatus)
        )
        result shouldBe expected
      }
    }

    "there is an error from VAT API" should {
      "return a VatDetailsViewModel with the returnError flag set" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(
          None, None, Some(entityName), returnObligationError = true, deregDate = Some(LocalDate.parse("2020-01-01")), currentDate = testDate, partyType = Some("7"))
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Left(ObligationsError),
          Right(None),
          Right(customerInformationMax),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }

    "there is an error from Financial Data API" should {

      "return a VatDetailsViewModel with the paymentError flag set" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(
          None, None, Some(entityName), paymentError = true, deregDate = Some(LocalDate.parse("2020-01-01")), currentDate = testDate, partyType = Some("7"))
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(None),
          Left(NextPaymentError),
          Right(customerInformationMax),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }

    "there is an error from both APIs" should {

      "return a VatDetailsViewModel with the returnError and paymentError flags set" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(
          None,
          None,
          Some(entityName),
          returnObligationError = true,
          paymentError = true,
          deregDate = Some(LocalDate.parse("2020-01-01")),
          currentDate = testDate,
          partyType = Some("7"))
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Left(ObligationsError),
          Left(NextPaymentError),
          Right(customerInformationMax),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }

    "the obligation is overdue" should {

      "return a VatDetailsViewModel with the return overdue flag set" in new DetailsTest {
        val overdueObligationDueDate: Option[String] = Some("2017-06-06")
        override val obligations: VatReturnObligations = overdueObligations

        lazy val expected = VatDetailsViewModel(
          paymentDueDate,
          overdueObligationDueDate,
          Some(entityName),
          returnObligationOverdue = true,
          deregDate = Some(LocalDate.parse("2020-01-01")),
          currentDate = testDate,
          partyType = Some("7"))
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(Some(obligations)),
          Right(Some(payments)),
          Right(customerInformationMax),
          Right(validMandationStatus)
        )

        result shouldBe expected
      }
    }

    "the partyType is not returned" should {

      "set the customerInfoError to true" in new DetailsTest {

        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(None),
          Right(None),
          Right(customerInformationMax.copy(partyType = None)),
          Right(validMandationStatus)
        )

        result.customerInfoError shouldBe true
      }
    }
  }

  "Calling .getObligationDetails" when {

    "there is a single obligation" should {

      "return a VatDetailsDataModel with the correct data" in new DetailsTest {

        val expected = VatDetailsDataModel(
          Some("2019-06-06"),
          hasMultiple = false,
          isOverdue = false,
          hasError = false
        )

        val result: VatDetailsDataModel = target().getObligationDetails(obligations.obligations, isOverdue = false)
        result shouldBe expected
      }
    }

    "there are multiple obligations" should {

      "return a VatDetailsDataModel with the hasMultiple flag set" in new DetailsTest {

        val multipleObligations: Seq[VatReturnObligation] = Seq(
          VatReturnObligation(
            LocalDate.parse("2019-04-04"),
            LocalDate.parse("2019-05-05"),
            LocalDate.parse("2019-06-06"),
            "O",
            None,
            "#001"
          ),
          VatReturnObligation(
            LocalDate.parse("2020-04-04"),
            LocalDate.parse("2020-05-05"),
            LocalDate.parse("2020-06-06"),
            "O",
            None,
            "#001"
          )
        )

        val expected = VatDetailsDataModel(
          Some("2"),
          hasMultiple = true,
          isOverdue = false,
          hasError = false
        )

        val result: VatDetailsDataModel = target().getObligationDetails(multipleObligations, isOverdue = false)
        result shouldBe expected
      }
    }
  }

  "Calling .getPaymentObligationDetails" when {

    "there is at least one obligation" when {

      "ddCollectionInProgressEnabled feature switch is on" when {

        "due date of payment is in the past" when {

          "user has direct debit collection in progress" should {

            "return payment that is not overdue" in new DetailsTest {

              mockAppConfig.features.ddCollectionInProgressEnabled(true)

              val testPayment: PaymentNoPeriod = Payment(
                ReturnDebitCharge,
                due = LocalDate.parse("2017-01-01"),
                BigDecimal("10000"),
                Some("ABCD"),
                ddCollectionInProgress = true
              )

              val result: VatDetailsDataModel = target().getPaymentObligationDetails(Seq(testPayment))

              result.isOverdue shouldBe false
            }
          }

          "user has no direct debit collection in progress" should {

            "return payment that is overdue" in new DetailsTest {

              mockAppConfig.features.ddCollectionInProgressEnabled(true)

              val testPayment: PaymentNoPeriod = Payment(
                ReturnDebitCharge,
                due = LocalDate.parse("2017-01-01"),
                BigDecimal("10000"),
                Some("ABCD"),
                ddCollectionInProgress = false
              )

              val result: VatDetailsDataModel = target().getPaymentObligationDetails(Seq(testPayment))

              result.isOverdue shouldBe true
            }
          }
        }

        "due date of payment is in the future" should {

          "return payment that is not overdue" in new DetailsTest {

            mockAppConfig.features.ddCollectionInProgressEnabled(true)

            val testPayment: PaymentNoPeriod = Payment(
              ReturnDebitCharge,
              due = LocalDate.parse("2020-01-01"),
              BigDecimal("10000"),
              Some("ABCD"),
              ddCollectionInProgress = false
            )

            val result: VatDetailsDataModel = target().getPaymentObligationDetails(Seq(testPayment))

            result.isOverdue shouldBe false
          }
        }
      }

      "ddCollectionInProgressEnabled feature switch is off" when {

        "due date of payment is in the past and has no direct debit" should {

          "return payment that isn't not overdue" in new DetailsTest {

            mockAppConfig.features.ddCollectionInProgressEnabled(false)

            val testPayment: PaymentNoPeriod = Payment(
              ReturnDebitCharge,
              due = LocalDate.parse("2017-01-01"),
              BigDecimal("10000"),
              Some("ABCD"),
              ddCollectionInProgress = false
            )

            val result: VatDetailsDataModel = target().getPaymentObligationDetails(Seq(testPayment))

            result.isOverdue shouldBe false
          }
        }
      }
    }
  }

  "Calling .getReturnObligationDetails" when {

    "there is at least one obligation" when {

      "obligation is overdue" should {

        "return VatDetailsDataModel with overdue flag set to true" in new DetailsTest {

          val expected = VatDetailsDataModel(
            displayData = Some("2017-06-06"),
            hasMultiple = false,
            isOverdue = true,
            hasError = false
          )

          val result: VatDetailsDataModel = target().getReturnObligationDetails(overdueObligations.obligations)
          result shouldBe expected
        }
      }

      "obligation is not overdue" should {

        "return VatDetailsDataModel with overdue flag set to false" in new DetailsTest {

          val expected = VatDetailsDataModel(
            displayData = Some("2019-06-06"),
            hasMultiple = false,
            isOverdue = false,
            hasError = false
          )

          val result: VatDetailsDataModel = target().getReturnObligationDetails(obligations.obligations)
          result shouldBe expected
        }
      }
    }
  }

  "Calling .retrieveMandationStatus" should {

    "return a mandation status" when {

      "it is available in session" in new DetailsTest {
        implicit val fakeRequestWithSession: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession(
          "mtdVatMandationStatus" -> "Non MTDfB"
        )

        val result: Future[HttpGetResult[MandationStatus]] = target(false).retrieveMandationStatus("111111111")(fakeRequestWithSession)

        await(result) shouldBe Right(MandationStatus("Non MTDfB"))
      }

      "it is needs to be collected from the mandation service" in new DetailsTest {
        implicit val fakeRequestWithEmptySession: FakeRequest[AnyContentAsEmpty.type] = FakeRequest().withSession()

        override def setup(needMandationCall: Boolean): Any = {
          (mockMandationService.getMandationStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returning(Future.successful(Right(validNonMTDfBMandationStatus))).once()
        }

        val result: Future[HttpGetResult[MandationStatus]] = target().retrieveMandationStatus("111111111")(fakeRequestWithEmptySession)

        await(result) shouldBe Right(MandationStatus("Non MTDfB"))
      }
    }

    "return a HTTP error" when {

      "one is received from the mandation service layer" in new DetailsTest {
        (mockMandationService.getMandationStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returning(Future.successful(Left(BadRequestError("", ""))))

        val result: Future[HttpGetResult[MandationStatus]] = target(false).retrieveMandationStatus("111111111")(fakeRequest)
        await(result) shouldBe Left(BadRequestError("", ""))
      }
    }
  }
}
