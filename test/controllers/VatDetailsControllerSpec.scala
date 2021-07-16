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
import audit.models.AuditModel
import common.FinancialTransactionsConstants._
import common.TestModels._
import common.{SessionKeys, TestModels}
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models._
import models.errors.{NextPaymentError, ObligationsError, _}
import models.obligations.{VatReturnObligation, VatReturnObligations}
import models.payments.{Payment, PaymentNoPeriod, Payments, ReturnDebitCharge}
import models.viewModels.VatDetailsViewModel
import play.api.http.Status
import play.api.mvc.{Request, Result}
import play.api.test.Helpers._
import play.twirl.api.Html
import services._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.vatDetails.Details

import scala.concurrent.{ExecutionContext, Future}

class VatDetailsControllerSpec extends ControllerBaseSpec {

  private trait DetailsTest {

    val obligations: VatReturnObligations = TestModels.obligations
    val payments: Payments = TestModels.payments

    val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = successfulAuthResult
    val vatServiceReturnsResult: Future[ServiceResponse[Option[VatReturnObligations]]] =
      Future.successful(Right(Some(obligations)))
    val vatServicePaymentsResult: Future[ServiceResponse[Option[Payments]]] =
      Future.successful(Right(Some(payments)))
    val accountDetailsServiceResult: Future[HttpGetResult[CustomerInformation]] =
      Future.successful(Right(customerInformationMax))
    val serviceInfoServiceResult: Future[Html] = Future.successful(Html(""))
    val ddResult: Future[ServiceResponse[DirectDebitStatus]] =
      Future.successful(Right(DirectDebitStatus(directDebitMandateFound = false, None)))

    val mockServiceInfoService: ServiceInfoService = mock[ServiceInfoService]
    val mockVatDetailsService: VatDetailsService = mock[VatDetailsService]
    val mockAuditService: AuditingService = mock[AuditingService]
    val mockPaymentsService: PaymentsService = mock[PaymentsService]

    def setup(): Any = {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *, *)
        .returns(authResult)

      mockDateServiceCall()

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

      (mockPaymentsService.getDirectDebitStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *)
        .returns(ddResult)
    }

    def target(): VatDetailsController = {
      setup()
      new VatDetailsController(
        enrolmentsAuthService,
        mockAppConfig,
        mockVatDetailsService,
        mockServiceInfoService,
        authorisedController,
        mockAccountDetailsService,
        mockDateService,
        mockAuditService,
        mcc,
        ec,
        injector.instanceOf[Details],
        mockServiceErrorHandler,
        ddInterruptPredicate
      )
    }
  }

  "Calling the details action" when {

    "the user is logged in and does not meet the criteria to see an interrupt screen" should {

      object Test extends DetailsTest { lazy val result: Future[Result] = target().details()(fakeRequest) }

      "return 200" in {
        status(Test.result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(Test.result) shouldBe Some("text/html")
      }

      "return charset utf-8" in {
        charset(Test.result) shouldBe Some("utf-8")
      }

      "return the VAT overview view" in {
        await(bodyOf(Test.result)).contains("Your VAT account") shouldBe true
      }

      "put a customerMigratedToETMPDate key into the session" in {
        session(Test.result).get(SessionKeys.migrationToETMP) shouldBe Some("2017-05-05")
      }

      "put a mandation status in the session" in {
        session(Test.result).get(SessionKeys.mandationStatus) shouldBe Some("MTDfB")
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

      "redirect to Agent Hub page" in new DetailsTest {
        override val authResult: Future[~[Enrolments, Option[AffinityGroup]]] = agentAuthResult
        val result: Future[Result] = target().details()(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(mockAppConfig.agentClientLookupHubUrl)
      }
    }

    "the user is hybrid" should {

      "not attempt to retrieve payment obligations" in new DetailsTest {

        override val accountDetailsServiceResult: Future[HttpGetResult[CustomerInformation]] =
          Future.successful(Right(customerInformationHybrid))

        override def setup(): Unit = {

          (mockServiceInfoService.getPartial(_: Request[_], _: User,  _: ExecutionContext))
            .stubs(*,*,*)
            .returns(serviceInfoServiceResult)

          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .stubs(*, *, *, *)
            .returns(authResult)

          mockDateServiceCall()

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

          (mockPaymentsService.getDirectDebitStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .stubs(*, *, *)
            .returns(ddResult)
        }

        val result: Future[Result] = target().details()(fakeRequest)
        status(result) shouldBe Status.OK
      }
    }

    "return a VatDetailsViewModel as a non MTDfB user" in new DetailsTest {
      override val accountDetailsServiceResult: Future[HttpGetResult[CustomerInformation]] =
        Future.successful(Right(customerInformationNonMTDfB))

      lazy val result: Future[Result] = target().details()(fakeRequest)
      status(result) shouldBe OK
      await(bodyOf(result)).contains(messages("returnObligation.submit")) shouldBe true
    }

    "the user is a missing trader" should {

      "redirect to the manage-vat-subscription-frontend missing trader URL" in new DetailsTest {
        override val accountDetailsServiceResult: Future[HttpGetResult[CustomerInformation]] = Future.successful(Right(
          customerInformationMax.copy(isMissingTrader = true)
        ))
        lazy val result: Future[Result] = target().details()(fakeRequest)
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some("/missing-trader")
      }
    }

  "Calling .detailsRedirectToEmailVerification" should {

    "redirect to email verification" when {

      "All relevant information is returned from vat-subscription" in new DetailsTest {
        override val accountDetailsServiceResult: Future[HttpGetResult[CustomerInformation]] = Future.successful(Right(
          customerInformationMax
        ))

        lazy val result: Future[Result] = target().detailsRedirectToEmailVerification()(fakeRequest)
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(mockAppConfig.verifyEmailUrl)
      }

    }

    "return an internal server error" when {

      "account details is an error" in new DetailsTest {
        override val accountDetailsServiceResult: Future[HttpGetResult[CustomerInformation]] = Future.successful(Left(
          UnknownError
        ))

        lazy val result: Future[Result] = target().detailsRedirectToEmailVerification()(fakeRequest)
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }

      "no email information is returned in customer information" in new DetailsTest {
        override val accountDetailsServiceResult: Future[HttpGetResult[CustomerInformation]] = Future.successful(Right(
          customerInformationMax.copy(emailAddress = None)
        ))

        lazy val result: Future[Result] = target().detailsRedirectToEmailVerification()(fakeRequest)
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }

      "the email address is empty" in new DetailsTest {
        override val accountDetailsServiceResult: Future[HttpGetResult[CustomerInformation]] = Future.successful(Right(
          customerInformationMax.copy(emailAddress = Some(Email(None, None)))
        ))

        lazy val result: Future[Result] = target().detailsRedirectToEmailVerification()(fakeRequest)
        status(result) shouldBe INTERNAL_SERVER_ERROR
        }
      "the user has no ddInterrupt value in session" in new DetailsTest {
        override def setup(): Any = {
          (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
            .stubs(*, *, *, *)
            .returns(authResult)
        }
        lazy val result = target.details()(DDInterruptRequest)
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(controllers.routes.DDInterruptController.directDebitInterruptCall("/homepage").url)
      }
      }
    }
  }

  "Calling .constructViewModel with a VatDetailsModel" when {

    lazy val paymentDueDate: Option[String] = Some("2019-03-03")
    lazy val obligationData: Option[String] = Some("2019-06-06")

    "there is both a payment and an obligation" should {

      "return a VatDetailsViewModel with both due dates" in new DetailsTest {
        lazy val expected: VatDetailsViewModel =
          VatDetailsViewModel(
            paymentDueDate, obligationData, Some(entityName), deregDate = Some(LocalDate.parse("2020-01-01")), currentDate = testDate, partyType = Some("7"), userEmailVerified = true
          )
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(Some(obligations)),
          Right(Some(payments)),
          Right(customerInformationMax)
        )

        result shouldBe expected
      }
    }

    "there is a payment but no obligation" should {

      "return a VatDetailsViewModel with a payment due date and no obligation due date" in new DetailsTest {
        lazy val expected: VatDetailsViewModel =
          VatDetailsViewModel(
            paymentDueDate, None, Some(entityName), deregDate = Some(LocalDate.parse("2020-01-01")), currentDate = testDate, partyType = Some("7"), userEmailVerified = true
          )
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(None),
          Right(Some(payments)),
          Right(customerInformationMax)
        )

        result shouldBe expected
      }
    }

    "there is an obligation but no payment" should {

      "return a VatDetailsViewModel with an obligation due date and no payment due date" in new DetailsTest {
        lazy val expected: VatDetailsViewModel =
          VatDetailsViewModel(
            None, obligationData, Some(entityName), deregDate = Some(LocalDate.parse("2020-01-01")), currentDate = testDate, partyType = Some("7"), userEmailVerified = true
          )
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(Some(obligations)),
          Right(None),
          Right(customerInformationMax)
        )

        result shouldBe expected
      }
    }

    "there is no obligation or payment" should {

      "return a VatDetailsViewModel with no obligation due date and no payment due date" in new DetailsTest {
        lazy val expected: VatDetailsViewModel =
          VatDetailsViewModel(
            None, None, Some(entityName), deregDate = Some(LocalDate.parse("2020-01-01")), currentDate = testDate, partyType = Some("7"), userEmailVerified = true
          )
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(None),
          Right(None),
          Right(customerInformationMax)
        )

        result shouldBe expected
      }
    }

    "there is no obligation, payment, or entity name" should {

      "return a VatDetailsViewModel with no obligation due date, payment due date, entity name or partyType" in new DetailsTest {
        lazy val expected: VatDetailsViewModel = VatDetailsViewModel(None, None, None, currentDate = testDate, partyType = None, userEmailVerified = true)
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(None),
          Right(None),
          Right(customerInformationMin)
        )

        result shouldBe expected
      }
    }

    "there is no obligation, payment, entity name or partyType when the showSignUp flag is true" should {

      "return a VatDetailsViewModel with no obligation due date, payment due date, or entity name with the isNonMTDfBOrNonDigital flag set to true" in
        new DetailsTest {
        lazy val expected: VatDetailsViewModel = VatDetailsViewModel(
          None, None, None, showSignUp = Some(true), currentDate = testDate, partyType = None, userEmailVerified = true)
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(None),
          Right(None),
          Right(customerInformationMTDfBExempt)
        )
        result shouldBe expected
      }
    }

    "there is an error from VAT API" should {
      "return a VatDetailsViewModel with the returnError flag set" in new DetailsTest {
        lazy val expected: VatDetailsViewModel = VatDetailsViewModel(
          None, None, Some(entityName), returnObligationError = true,
          deregDate = Some(LocalDate.parse("2020-01-01")), currentDate = testDate, partyType = Some("7"), userEmailVerified = true)
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Left(ObligationsError),
          Right(None),
          Right(customerInformationMax)
        )

        result shouldBe expected
      }
    }

    "there is an error from Financial Data API" should {

      "return a VatDetailsViewModel with the paymentError flag set" in new DetailsTest {
        lazy val expected: VatDetailsViewModel = VatDetailsViewModel(
          None, None, Some(entityName), paymentError = true, deregDate = Some(LocalDate.parse("2020-01-01")), currentDate = testDate, partyType = Some("7"), userEmailVerified = true)
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(None),
          Left(NextPaymentError),
          Right(customerInformationMax)
        )

        result shouldBe expected
      }
    }

    "there is an error from both APIs" should {

      "return a VatDetailsViewModel with the returnError and paymentError flags set" in new DetailsTest {
        lazy val expected: VatDetailsViewModel = VatDetailsViewModel(
          None,
          None,
          Some(entityName),
          returnObligationError = true,
          paymentError = true,
          deregDate = Some(LocalDate.parse("2020-01-01")),
          currentDate = testDate,
          partyType = Some("7"),
          userEmailVerified = true)
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Left(ObligationsError),
          Left(NextPaymentError),
          Right(customerInformationMax)
        )

        result shouldBe expected
      }
    }

    "the obligation is overdue" should {

      "return a VatDetailsViewModel with the return overdue flag set" in new DetailsTest {
        val overdueObligationDueDate: Option[String] = Some("2017-06-06")
        override val obligations: VatReturnObligations = overdueObligations

        lazy val expected: VatDetailsViewModel = VatDetailsViewModel(
          paymentDueDate,
          overdueObligationDueDate,
          Some(entityName),
          returnObligationOverdue = true,
          deregDate = Some(LocalDate.parse("2020-01-01")),
          currentDate = testDate,
          partyType = Some("7"),
          userEmailVerified = true)
        lazy val result: VatDetailsViewModel = target().constructViewModel(
          Right(Some(obligations)),
          Right(Some(payments)),
          Right(customerInformationMax)
        )

        result shouldBe expected
      }
    }
  }

  "Calling .retrieveEmailVerifiedIfExist" should {

    "return true" when {

      "account details returns an error" in new DetailsTest {
        target().retrieveEmailVerifiedIfExist(Left(UnknownError)) shouldBe true
      }

      "customer information does not contain any email information" in new DetailsTest {
        val customerInfo: CustomerInformation = customerInformationMax.copy(emailAddress = None)

        target().retrieveEmailVerifiedIfExist(Right(customerInfo)) shouldBe true
      }

      "no email is returned, regardless of validation status" in new DetailsTest {
        val customerInfo: CustomerInformation = customerInformationMax.copy(emailAddress = Some(Email(None, Some(true))))

        target().retrieveEmailVerifiedIfExist(Right(customerInfo)) shouldBe true
      }

      "the email is verified" in new DetailsTest {
        val customerInfo: CustomerInformation =
          customerInformationMax.copy(emailAddress = Some(Email(Some("asdf@adf.com"), Some(true))))

        target().retrieveEmailVerifiedIfExist(Right(customerInfo)) shouldBe true
      }

      "there is a pending PPOB section" in new DetailsTest {
        val customerInfo: CustomerInformation = customerInformationMax.copy(hasPendingPpobChanges = true)

        target().retrieveEmailVerifiedIfExist(Right(customerInfo)) shouldBe true
      }
    }

    "return false" when {

      "the email is not verified" in new DetailsTest {
        val customerInfo: CustomerInformation =
          customerInformationMax.copy(emailAddress = Some(Email(Some("asdf@asdf.com"), Some(false))))

        target().retrieveEmailVerifiedIfExist(Right(customerInfo)) shouldBe false
      }

      "no verification is returned" in new DetailsTest {
        val customerInfo: CustomerInformation =
          customerInformationMax.copy(emailAddress = Some(Email(Some("asdf@asdf.com"), None)))

        target().retrieveEmailVerifiedIfExist(Right(customerInfo)) shouldBe false
      }
    }
  }

  "Calling .getObligationDetails" when {

    "there is a single obligation" should {

      "return a VatDetailsDataModel with the correct data" in new DetailsTest {

        val expected: VatDetailsDataModel = VatDetailsDataModel(
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

        val expected: VatDetailsDataModel = VatDetailsDataModel(
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

        "due date of payment is in the past" when {

          "user has direct debit collection in progress" should {

            "return payment that is not overdue" in new DetailsTest {

              val testPayment: PaymentNoPeriod = Payment(
                ReturnDebitCharge,
                due = LocalDate.parse("2017-01-01"),
                BigDecimal("10000"),
                Some("ABCD"),
                chargeReference = Some("XD002750002155"),
                ddCollectionInProgress = true
              )

              val result: VatDetailsDataModel = target().getPaymentObligationDetails(Seq(testPayment))

              result.isOverdue shouldBe false
            }
          }

          "user has no direct debit collection in progress" should {

            "return payment that is overdue" in new DetailsTest {

              val testPayment: PaymentNoPeriod = Payment(
                ReturnDebitCharge,
                due = LocalDate.parse("2017-01-01"),
                BigDecimal("10000"),
                Some("ABCD"),
                chargeReference = Some("XD002750002155"),
                ddCollectionInProgress = false
              )

              val result: VatDetailsDataModel = target().getPaymentObligationDetails(Seq(testPayment))

              result.isOverdue shouldBe true
            }
          }
        }

        "due date of payment is in the future" should {

          "return payment that is not overdue" in new DetailsTest {

            val testPayment: PaymentNoPeriod = Payment(
              ReturnDebitCharge,
              due = LocalDate.parse("2020-01-01"),
              BigDecimal("10000"),
              Some("ABCD"),
              chargeReference = Some("XD002750002155"),
              ddCollectionInProgress = false
            )

            val result: VatDetailsDataModel = target().getPaymentObligationDetails(Seq(testPayment))

            result.isOverdue shouldBe false
          }
        }
    }
  }

  "Calling .getReturnObligationDetails" when {

    "there is at least one obligation" when {

      "obligation is overdue" should {

        "return VatDetailsDataModel with overdue flag set to true" in new DetailsTest {

          val expected: VatDetailsDataModel = VatDetailsDataModel(
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

          val expected: VatDetailsDataModel = VatDetailsDataModel(
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

  "Calling .retrieveIsOfStatus" should {

    "return true" when {

      "the mandation status in the CustomerInformation matches the expected status" in new DetailsTest {
        val customerInfoToCompare: Either[Nothing, CustomerInformation] = Right(customerInformationMin)
        target().retrieveIsOfStatus(customerInfoToCompare, Seq(mtdfb)) shouldBe Some(true)
      }

      "the mandation status in the CustomerInformation matches one of the expected statuses" in new DetailsTest {
        val mandationStatusToCompareMtdfb: HttpGetResult[CustomerInformation] = Right(customerInformationMin)
        val mandationStatusToCompareExempt: HttpGetResult[CustomerInformation] = Right(customerInformationMTDfBExempt)

        val controller: VatDetailsController = target()

        controller.retrieveIsOfStatus(mandationStatusToCompareMtdfb, Seq(mtdfb, mtdfbExempt)) shouldBe Some(true)
        controller.retrieveIsOfStatus(mandationStatusToCompareExempt, Seq(mtdfb, mtdfbExempt)) shouldBe Some(true)
      }

    }

    "return false" when {

      "the mandation status does not match the expected status" in new DetailsTest {
        val mandationStatusToCompare: Either[Nothing, CustomerInformation] = Right(customerInformationJunkStatus)
        target().retrieveIsOfStatus(mandationStatusToCompare, Seq(mtdfb)) shouldBe Some(false)
      }

      "the mandation status does not match any of the expected statuses" in new DetailsTest {
        val mandationStatusToCompareMtdfb: HttpGetResult[CustomerInformation] = Right(customerInformationMin)
        target().retrieveIsOfStatus(mandationStatusToCompareMtdfb, Seq("randomStatus", "someStatus")) shouldBe Some(false)
      }

      "the expected mandation statuses list is empty" in new DetailsTest {
        val mandationStatusToCompare: HttpGetResult[CustomerInformation] = Right(customerInformationMin)
        target().retrieveIsOfStatus(mandationStatusToCompare, Seq.empty[String]) shouldBe Some(false)
      }

    }

    "return None" when {

      "an error is present" in new DetailsTest {
        val errorForTest: HttpGetResult[CustomerInformation] = Left(UnknownError)
        target().retrieveIsOfStatus(errorForTest, Seq.empty[String]) shouldBe None
      }
    }
  }

  "Calling .redirectForMissingTrader" when {

    "the user is a missing trader and has no inflight changes" should {

      "return true" in new DetailsTest {
        target().redirectForMissingTrader(Right(customerInformationMax.copy(isMissingTrader = true))) shouldBe true
      }
    }

    "the user is a missing trader and has an inflight PPOB change" should {

      "return false" in new DetailsTest {
        target().redirectForMissingTrader(Right(
          customerInformationMax.copy(isMissingTrader = true, hasPendingPpobChanges = true)
        )) shouldBe false
      }
    }

    "the user is not a missing trader" should {

      "return false" in new DetailsTest {
        target().redirectForMissingTrader(Right(customerInformationMax)) shouldBe false
      }
    }
  }
}
