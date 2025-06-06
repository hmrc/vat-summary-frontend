/*
 * Copyright 2023 HM Revenue & Customs
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

import common.MandationStatus._
import common.TestModels._
import common.{SessionKeys, TestModels}
import connectors.httpParsers.ResponseHttpParsers.HttpResult
import models._
import models.errors.{NextPaymentError, ObligationsError, _}
import models.obligations.{VatReturnObligation, VatReturnObligations}
import models.payments.{PaymentNoPeriod, Payments, ReturnDebitCharge}
import models.penalties.PenaltiesSummary
import models.viewModels.VatDetailsViewModel
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import services._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.vatDetails.Details

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class VatDetailsControllerSpec extends ControllerBaseSpec {

  val obligations: VatReturnObligations = TestModels.obligations
  val payments: Payments = TestModels.payments
  val mockVatDetailsService: VatDetailsService = mock[VatDetailsService]
  val mockPenaltiesService: PenaltiesService = mock[PenaltiesService]
  val duplicateObligations: VatReturnObligations = TestModels.duplicateObligations

  def mockReturnObligations(result: ServiceResponse[Option[VatReturnObligations]]): Any =
    (mockVatDetailsService.getReturnObligations(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .stubs(*, *, *)
      .returns(Future.successful(result))

  def mockPaymentLiabilities(result: ServiceResponse[Option[Payments]]): Any =
    (mockVatDetailsService.getPaymentObligations(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .stubs(*, *, *)
      .returns(Future.successful(result))

  def mockPenaltiesService(result: HttpResult[PenaltiesSummary]): Any =
    (mockPenaltiesService.getPenaltiesInformation(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .stubs(*, *, *)
      .returns(Future.successful(result))
  
  val controller = new VatDetailsController(
    mockVatDetailsService,
    mockServiceInfoService,
    authorisedController,
    mockAccountDetailsService,
    mockDateService,
    mockAuditService,
    mockPenaltiesService,
    mcc,
    injector.instanceOf[Details],
    mockServiceErrorHandler,
    mockPOACheckService,
    mockPaymentsOnAccountService
  )
  
  "Calling the details action" when {

    "the user is logged in" should {

      lazy val result: Future[Result] = {
        mockPrincipalAuth()
        mockDateServiceCall()
        mockReturnObligations(Right(Some(obligations)))
        mockPaymentLiabilities(Right(Some(payments)))
        mockCustomerInfo(Right(customerInformationMax))
        mockPOACheckServiceCall()
        mockChangedOnDateWithInLatestVatPeriod()
        mockPOACheckServiceCall()
        mockServiceInfoCall()
        mockAudit()
        mockPenaltiesService(penaltySummaryNoResponse)
        controller.details()(fakeRequest)
      }

      "return 200" in {
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
      }

      "return charset utf-8" in {
        charset(result) shouldBe Some("utf-8")
      }

      "return the VAT overview view" in {
        contentAsString(result).contains("Your VAT account") shouldBe true
      }

      "put a customerMigratedToETMPDate key into the session" in {
        session(result).get(SessionKeys.migrationToETMP) shouldBe Some("2017-05-05")
      }

      "put a mandation status in the session" in {
        session(result).get(SessionKeys.mandationStatus) shouldBe Some("MTDfB")
      }
    }

    "the user is not logged in" should {

      lazy val result: Future[Result] = {
        mockMissingBearerToken()
        controller.details()(fakeRequest)
      }

      "return 303 (SEE_OTHER)" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect to the sign-in URL" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
      }
    }

    "the user does not have sufficient enrolments" should {

      "return 403 (FORBIDDEN)" in {
        val result: Future[Result] = {
          mockInsufficientEnrolments()
          controller.details()(fakeRequest)
        }
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "user is an Agent" should {

      lazy val result: Future[Result] = {
        mockAgentAuth()
        controller.details()(fakeRequest)
      }

      "return 303 (SEE_OTHER)" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect to the Agent Hub page" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.agentClientLookupHubUrl)
      }
    }

    "the user is hybrid" should {

      "return 200 (OK) without calling the API for payment information" in {

        val result = {
          mockPrincipalAuth()
          mockDateServiceCall()
          mockReturnObligations(Right(Some(obligations)))
          mockCustomerInfo(Right(customerInformationHybrid))
          mockPOACheckServiceCall()
          mockChangedOnDateWithInLatestVatPeriod()
          mockPOACheckServiceCall()
          mockAudit()
          mockServiceInfoCall()
          mockPenaltiesService(penaltySummaryNoResponse)
          controller.details()(fakeRequest)
        }

        status(result) shouldBe Status.OK
      }
    }

    "the user is non-MTD" should {

      lazy val result = {
        mockPrincipalAuth()
        mockDateServiceCall()
        mockReturnObligations(Right(Some(obligations)))
        mockPaymentLiabilities(Right(Some(payments)))
        mockCustomerInfo(Right(customerInformationNonMTDfB))
        mockPOACheckServiceCall()
        mockChangedOnDateWithInLatestVatPeriod()
        mockServiceInfoCall()
        mockAudit()
        mockPenaltiesService(penaltySummaryNoResponse)
        mockPOACheckServiceCall()
        controller.details()(fakeRequest)
      }

      "return 200 (OK)" in {
        status(result) shouldBe OK
      }

      "render the page with a 'Submit' link" in {
        contentAsString(result).contains(messages("returnObligation.submit")) shouldBe true
      }
    }

    "the user is a missing trader" should {

      lazy val result = {
        mockPrincipalAuth()
        mockDateServiceCall()
        mockReturnObligations(Right(Some(obligations)))
        mockPaymentLiabilities(Right(Some(payments)))
        mockCustomerInfo(Right(customerInformationMax.copy(isMissingTrader = true)))
        mockServiceInfoCall()
        mockPOACheckServiceCall()
        mockAudit()
        mockPenaltiesService(penaltySummaryNoResponse)
        controller.details()(fakeRequest)
      }

      "return 303 (SEE_OTHER)" in {
        status(result) shouldBe SEE_OTHER
      }

      "redirect to the manage-vat-subscription-frontend missing trader URL" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.missingTraderRedirectUrl)
      }
    }

    "the user has penalties" should {

      lazy val result: Future[Result] = {
        mockPrincipalAuth()
        mockDateServiceCall()
        mockReturnObligations(Right(Some(obligations)))
        mockPaymentLiabilities(Right(Some(payments)))
        mockCustomerInfo(Right(customerInformationMax))
        mockPOACheckServiceCall()
        mockChangedOnDateWithInLatestVatPeriod()
        mockPOACheckServiceCall()
        mockServiceInfoCall()
        mockAudit()
        mockPenaltiesService(penaltySummaryResponse)
        controller.details()(fakeRequest)
      }

      "return 200" in {
        status(result) shouldBe OK
      }

      "render the page with a penalties tile" in {
        messages(Jsoup.parse(contentAsString(result)).select("#penalties-heading").text) shouldBe "Penalties for late VAT Returns and payments"
      }
    }

    "the customer details call fails" should {

      "not add any values to session" in {

        lazy val result: Future[Result] = {
          mockPrincipalAuth()
          mockDateServiceCall()
          mockReturnObligations(Right(Some(obligations)))
          mockPaymentLiabilities(Right(Some(payments)))
          mockCustomerInfo(Left(UnknownError))
          mockServiceInfoCall()
          mockPOACheckServiceCall()
          mockChangedOnDateWithInLatestVatPeriod()
          mockPOACheckServiceCall()
          mockAudit()
          mockPenaltiesService(penaltySummaryResponse)
          controller.details()(fakeRequest)
        }
        session(result).get(SessionKeys.migrationToETMP) shouldBe None
        session(result).get(SessionKeys.mandationStatus) shouldBe None
      }

    }

  }

  "Calling .detailsRedirectToEmailVerification" when {

    "all relevant information is returned from vat-subscription" when {

      "there is no pending PPOB change" should {

        val result = {
          mockPrincipalAuth()
          mockCustomerInfo(Right(customerInformationMax))
          controller.detailsRedirectToEmailVerification()(fakeRequest.withSession())
        }

        "redirect to email verification" in {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(mockAppConfig.verifyEmailUrl)
        }

        "add the inFlightContactKey to session" in {
          session(result).get(SessionKeys.inFlightContactKey) shouldBe Some("false")
        }
      }

      "there is a pending PPOB change" should {

        val result = {
          mockPrincipalAuth()
          mockCustomerInfo(Right(customerInformationMax.copy(hasPendingPpobChanges = true)))
          controller.detailsRedirectToEmailVerification()(fakeRequest)
        }

        "redirect to email verification" in {
          status(result) shouldBe SEE_OTHER
          redirectLocation(result) shouldBe Some(mockAppConfig.verifyEmailUrl)
        }

        "not add the inFlightContactKey to session" in {
          session(result).get(SessionKeys.inFlightContactKey) shouldBe None
        }
      }
    }

    "return an internal server error" when {

      "account details cannot be retrieved" in {
        val result = {
          mockPrincipalAuth()
          mockCustomerInfo(Left(UnknownError))
          controller.detailsRedirectToEmailVerification()(fakeRequest)
        }

        status(result) shouldBe INTERNAL_SERVER_ERROR
      }

      "no email information is returned in customer information" in {
        val result = {
          mockPrincipalAuth()
          mockCustomerInfo(Right(customerInformationMax.copy(emailAddress = None)))
          controller.detailsRedirectToEmailVerification()(fakeRequest)
        }

        status(result) shouldBe INTERNAL_SERVER_ERROR
      }

      "the email address is empty" in {
        val result = {
          mockPrincipalAuth()
          mockCustomerInfo(Right(customerInformationMax.copy(emailAddress = Some(Email(None, None)))))
          controller.detailsRedirectToEmailVerification()(fakeRequest)
        }

        status(result) shouldBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "Calling .constructViewModel" when {

    lazy val paymentDueDate: Option[String] = Some("2019-03-03")
    lazy val obligationData: Option[String] = Some("2019-06-06")

    "there is both a payment and an obligation" should {

      "return a VatDetailsViewModel with both due dates" in {
        lazy val expected: VatDetailsViewModel =
          VatDetailsViewModel(
            paymentDueDate, obligationData, Some(entityName), deregDate = Some(LocalDate.parse("2020-01-01")),
            currentDate = testDate, partyType = Some("7"), userEmailVerified = true, emailAddress = Some(email), mandationStatus = "MTDfB", isPoaActiveForCustomer = false
          )
        lazy val result: VatDetailsViewModel = {
          mockPOACheckServiceCall()
          mockDateServiceCall()
          mockChangedOnDateWithInLatestVatPeriod()
          controller.constructViewModel(
            Right(Some(obligations)),
            Right(Some(payments)),
            Right(customerInformationMax),
            None,
            None,
            mockTodayDate
          )
        }

        result shouldBe expected
      }
    }

    "there is a payment but no obligation" should {

      "return a VatDetailsViewModel with a payment due date and no obligation due date" in {
        lazy val expected: VatDetailsViewModel =
          VatDetailsViewModel(
            paymentDueDate, None, Some(entityName), deregDate = Some(LocalDate.parse("2020-01-01")),
            currentDate = testDate, partyType = Some("7"), userEmailVerified = true, emailAddress = Some(email), mandationStatus = "MTDfB"
          )
        lazy val result: VatDetailsViewModel = {
          mockPOACheckServiceCall()
          mockDateServiceCall()
          mockChangedOnDateWithInLatestVatPeriod()
          controller.constructViewModel(
            Right(None),
            Right(Some(payments)),
            Right(customerInformationMax),
            None,
            None,
            mockTodayDate
          )
        }

        result shouldBe expected
      }
    }

    "there is an obligation but no payment" should {

      "return a VatDetailsViewModel with an obligation due date and no payment due date" in {
        lazy val expected: VatDetailsViewModel =
          VatDetailsViewModel(
            None, obligationData, Some(entityName), deregDate = Some(LocalDate.parse("2020-01-01")),
            currentDate = testDate, partyType = Some("7"), userEmailVerified = true, emailAddress = Some(email), mandationStatus = "MTDfB"
          )
        lazy val result: VatDetailsViewModel = {
          mockPOACheckServiceCall()
          mockDateServiceCall()
          mockChangedOnDateWithInLatestVatPeriod()
          controller.constructViewModel(
            Right(Some(obligations)),
            Right(None),
            Right(customerInformationMax),
            None,
            None,
            mockTodayDate
          )
        }

        result shouldBe expected
      }
    }

    "there is no obligation or payment" should {

      "return a VatDetailsViewModel with no obligation due date and no payment due date" in {
        lazy val expected: VatDetailsViewModel =
          VatDetailsViewModel(
            None, None, Some(entityName), deregDate = Some(LocalDate.parse("2020-01-01")),
            currentDate = testDate, partyType = Some("7"), userEmailVerified = true, emailAddress = Some(email), mandationStatus = "MTDfB"
          )
        lazy val result: VatDetailsViewModel = {
          mockPOACheckServiceCall()
          mockChangedOnDateWithInLatestVatPeriod()
          mockDateServiceCall()
          controller.constructViewModel(
            Right(None),
            Right(None),
            Right(customerInformationMax),
            None,
            None,
            mockTodayDate
          )
        }

        result shouldBe expected
      }
    }

    "there is no obligation, payment, or entity name" should {

      "return a VatDetailsViewModel with no obligation due date, payment due date, entity name or partyType" in {
        lazy val expected: VatDetailsViewModel =
          VatDetailsViewModel(None, None, None, currentDate = testDate, partyType = None, userEmailVerified = true, mandationStatus = "MTDfB")
        lazy val result: VatDetailsViewModel = {
          mockPOACheckServiceCall()
          mockDateServiceCall()
          mockChangedOnDateWithInLatestVatPeriod()
          controller.constructViewModel(
            Right(None),
            Right(None),
            Right(customerInformationMin),
            None,
            None,
            mockTodayDate
          )
        }

        result shouldBe expected
      }
    }

    "there is no obligation, payment, entity name, partyType and there is a non-MTD mandation status" should {

      "return a VatDetailsViewModel with no obligation due date, payment due date or entity name with the showSignUp flag set to true" in {
        lazy val expected: VatDetailsViewModel = VatDetailsViewModel(
          None, None, None, isNonMTDfB = Some(true), currentDate = testDate, partyType = None, userEmailVerified = true, mandationStatus = "MTDfB Exempt")
        lazy val result: VatDetailsViewModel = {
          mockPOACheckServiceCall()
          mockDateServiceCall()
          mockChangedOnDateWithInLatestVatPeriod()
          controller.constructViewModel(
            Right(None),
            Right(None),
            Right(customerInformationMTDfBExempt),
            None,
            None,
            mockTodayDate
          )
        }

        result shouldBe expected
      }
    }

    "there is an error returned by the obligations API" should {

      "return a VatDetailsViewModel with the returnError flag set" in {
        lazy val expected: VatDetailsViewModel = VatDetailsViewModel(
          None, None, Some(entityName), returnObligationError = true, deregDate = Some(LocalDate.parse("2020-01-01")),
          currentDate = testDate, partyType = Some("7"), userEmailVerified = true, emailAddress = Some(email), mandationStatus = "MTDfB")
        lazy val result: VatDetailsViewModel = {
          mockPOACheckServiceCall()
          mockDateServiceCall()
          mockChangedOnDateWithInLatestVatPeriod()
          controller.constructViewModel(
            Left(ObligationsError),
            Right(None),
            Right(customerInformationMax),
            None,
            None,
            mockTodayDate
          )
        }

        result shouldBe expected
      }
    }

    "there is an error returned by the financial data API" should {

      "return a VatDetailsViewModel with the paymentError flag set" in {
        lazy val expected: VatDetailsViewModel = VatDetailsViewModel(
          None, None, Some(entityName), paymentError = true, deregDate = Some(LocalDate.parse("2020-01-01")),
          currentDate = testDate, partyType = Some("7"), userEmailVerified = true, emailAddress = Some(email), mandationStatus = "MTDfB")
        lazy val result: VatDetailsViewModel = {
          mockPOACheckServiceCall()
          mockDateServiceCall()
          mockChangedOnDateWithInLatestVatPeriod()
          controller.constructViewModel(
            Right(None),
            Left(NextPaymentError),
            Right(customerInformationMax),
            None,
            None,
            mockTodayDate
          )
        }

        result shouldBe expected
      }
    }

    "there is an error from both obligation and financial APIs" should {

      "return a VatDetailsViewModel with the returnError and paymentError flags set" in {
        lazy val expected: VatDetailsViewModel = VatDetailsViewModel(
          None,
          None,
          Some(entityName),
          returnObligationError = true,
          paymentError = true,
          deregDate = Some(LocalDate.parse("2020-01-01")),
          currentDate = testDate,
          partyType = Some("7"),
          userEmailVerified = true,
          emailAddress = Some(email),
          mandationStatus = "MTDfB"
        )
        lazy val result: VatDetailsViewModel = {
          mockDateServiceCall()
          mockPOACheckServiceCall()
          mockChangedOnDateWithInLatestVatPeriod()
          controller.constructViewModel(
            Left(ObligationsError),
            Left(NextPaymentError),
            Right(customerInformationMax),
            None,
            None,
            mockTodayDate
          )
        }

        result shouldBe expected
      }
    }

    "the obligation is overdue" should {

      "return a VatDetailsViewModel with the return overdue flag set" in {
        val overdueObligationDueDate: Option[String] = Some("2017-06-06")

        lazy val expected: VatDetailsViewModel = VatDetailsViewModel(
          paymentDueDate,
          overdueObligationDueDate,
          Some(entityName),
          returnObligationOverdue = true,
          deregDate = Some(LocalDate.parse("2020-01-01")),
          currentDate = testDate,
          partyType = Some("7"),
          userEmailVerified = true,
          emailAddress = Some(email),
          mandationStatus = "MTDfB"
        )
        lazy val result: VatDetailsViewModel = {
          mockPOACheckServiceCall()
          mockDateServiceCall()
          mockChangedOnDateWithInLatestVatPeriod()
          controller.constructViewModel(
            Right(Some(overdueObligations)),
            Right(Some(payments)),
            Right(customerInformationMax),
            None,
            None,
            mockTodayDate
          )
        }

        result shouldBe expected
      }
    }

    "the user has penalties" should {
      "return a VatDetailsModel with displayPenaltiesTile set to true" in {
        lazy val expectedContent: VatDetailsViewModel = VatDetailsViewModel(
          paymentDueDate, obligationData, Some(entityName), deregDate = Some(LocalDate.parse("2020-01-01")),
          currentDate = testDate, partyType = Some("7"), userEmailVerified = true,
          emailAddress = Some(email), penaltiesSummary = Some(penaltiesSummaryModel), mandationStatus = "MTDfB"
        )
        lazy val result: VatDetailsViewModel = {
          mockPOACheckServiceCall()
          mockDateServiceCall()
          mockChangedOnDateWithInLatestVatPeriod()
          controller.constructViewModel(
            Right(Some(obligations)),
            Right(Some(payments)),
            Right(customerInformationMax),
            Some(penaltiesSummaryModel),
            None,
            mockTodayDate
          )
        }
        result shouldBe expectedContent
      }
    }

    "the user has no penalties" should {
      "return a VatDetailsModel with displayPenaltiesTile set to false" in {
        lazy val expectedContent: VatDetailsViewModel = VatDetailsViewModel(
          paymentDueDate, obligationData, Some(entityName), deregDate = Some(LocalDate.parse("2020-01-01")),
          currentDate = testDate, partyType = Some("7"), userEmailVerified = true, emailAddress = Some(email), mandationStatus = "MTDfB"
        )
        lazy val result: VatDetailsViewModel = {
          mockDateServiceCall()
          mockPOACheckServiceCall()
          mockChangedOnDateWithInLatestVatPeriod()
          controller.constructViewModel(
            Right(Some(obligations)),
            Right(Some(payments)),
            Right(customerInformationMax),
            None,
            None,
            mockTodayDate
          )
        }
        result shouldBe expectedContent
      }
    }

    "the customer info call fails" should {

      "return a VatDetailsModel and pendingDereg set to false" in {

        lazy val expectedContent: VatDetailsViewModel = VatDetailsViewModel(
          paymentDueDate,
          obligationData,
          None,
          isNonMTDfB = None,
          customerInfoError = true,
          currentDate = testDate,
          partyType = None,
          userEmailVerified = true,
          mandationStatus = "ERROR"
        )

        lazy val result: VatDetailsViewModel = {
          mockDateServiceCall()
          mockPOACheckServiceCall()
          mockChangedOnDateWithInLatestVatPeriod()
          controller.constructViewModel(
            Right(Some(obligations)),
            Right(Some(payments)),
            Left(BadRequestError("", "")),
            None,
            None,
            mockTodayDate
          )
        }
        result shouldBe expectedContent
      }
    }

    "there is a payment on account active and has standing request data" should {

      "return a VatDetailsViewModel with a POA changed on date that matches the Standing Request data" in {
        lazy val expected: VatDetailsViewModel =
          VatDetailsViewModel(
            paymentDueDate, None, Some(entityName), deregDate = Some(LocalDate.parse("2020-01-01")),
            currentDate = testDate, partyType = Some("7"), userEmailVerified = true, emailAddress = Some(email),
            mandationStatus = "MTDfB", isPoaActiveForCustomer = true, poaChangedOn = Some(LocalDate.parse("2025-02-20"))
          )

        lazy val result: VatDetailsViewModel = {
          mockPOACheckServiceCallTrue()
          mockDateServiceCall()
          mockChangedOnDateWithInLatestVatPeriod(poaActive = true, Some(LocalDate.parse("2025-02-20")))
          controller.constructViewModel(
            Right(None),
            Right(Some(payments)),
            Right(customerInformationMax),
            None,
            Some(standingRequestSample2),
            mockTodayDate
          )
        }

        result shouldBe expected
      }

      "return a VatDetailsViewModel with no POA changed on date when there is no Standing Request data" in {
        lazy val expected: VatDetailsViewModel =
          VatDetailsViewModel(
            paymentDueDate, None, Some(entityName), deregDate = Some(LocalDate.parse("2020-01-01")),
            currentDate = testDate, partyType = Some("7"), userEmailVerified = true, emailAddress = Some(email),
            mandationStatus = "MTDfB", isPoaActiveForCustomer = true, poaChangedOn = None
          )


        lazy val result: VatDetailsViewModel = {
          mockPOACheckServiceCallTrue()
          mockDateServiceCall()
          mockChangedOnDateWithInLatestVatPeriod()
          controller.constructViewModel(
            Right(None),
            Right(Some(payments)),
            Right(customerInformationMax),
            None,
            Some(standingRequestSample2),
            mockTodayDate
          )
        }

        result shouldBe expected
      }

    }
  }

  "Calling .retrieveEmailVerifiedIfExist" should {

    "return true" when {

      "account details returns an error" in {
        controller.retrieveEmailVerifiedIfExist(Left(UnknownError)) shouldBe true
      }

      "customer information does not contain any email information" in {
        val customerInfo: CustomerInformation = customerInformationMax.copy(emailAddress = None)

        controller.retrieveEmailVerifiedIfExist(Right(customerInfo)) shouldBe true
      }

      "no email is returned, regardless of validation status" in {
        val customerInfo: CustomerInformation = customerInformationMax.copy(emailAddress = Some(Email(None, Some(true))))

        controller.retrieveEmailVerifiedIfExist(Right(customerInfo)) shouldBe true
      }

      "the email is verified" in {
        val customerInfo: CustomerInformation =
          customerInformationMax.copy(emailAddress = Some(Email(Some("asdf@adf.com"), Some(true))))

        controller.retrieveEmailVerifiedIfExist(Right(customerInfo)) shouldBe true
      }

      "there is a pending PPOB section" in {
        val customerInfo: CustomerInformation = customerInformationMax.copy(hasPendingPpobChanges = true)

        controller.retrieveEmailVerifiedIfExist(Right(customerInfo)) shouldBe true
      }
    }

    "return false" when {

      "the email is not verified" in {
        val customerInfo: CustomerInformation =
          customerInformationMax.copy(emailAddress = Some(Email(Some("asdf@asdf.com"), Some(false))))

        controller.retrieveEmailVerifiedIfExist(Right(customerInfo)) shouldBe false
      }

      "no verification is returned" in {
        val customerInfo: CustomerInformation =
          customerInformationMax.copy(emailAddress = Some(Email(Some("asdf@asdf.com"), None)))

        controller.retrieveEmailVerifiedIfExist(Right(customerInfo)) shouldBe false
      }
    }
  }

  "Calling .getObligationDetails" when {

    "there is a single obligation" should {

      "return a VatDetailsDataModel with the correct data" in {

        val expected: VatDetailsDataModel = VatDetailsDataModel(
          Some("2019-06-06"),
          hasMultiple = false,
          isOverdue = false,
          hasError = false
        )

        val result: VatDetailsDataModel = controller.getObligationDetails(obligations.obligations, isOverdue = false)
        result shouldBe expected
      }
    }

    "there are multiple obligations" should {

      "return a VatDetailsDataModel with the hasMultiple flag set" in {

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

        val result: VatDetailsDataModel = controller.getObligationDetails(multipleObligations, isOverdue = false)
        result shouldBe expected
      }
    }
  }

  "Calling .getPaymentObligationDetails" when {

    "there is at least one obligation" when {

        "due date of payment is in the past" when {

          "user has direct debit collection in progress" should {

            "return payment that is not overdue" in {

              val testPayment: PaymentNoPeriod = PaymentNoPeriod(
                ReturnDebitCharge,
                due = LocalDate.parse("2017-01-01"),
                BigDecimal("10000"),
                Some("ABCD"),
                chargeReference = Some("XD002750002155"),
                ddCollectionInProgress = true,
                accruingInterestAmount = Some(BigDecimal(2)),
                accruingPenaltyAmount = Some(BigDecimal(100.00)),
                penaltyType = Some("LPP1"),
                BigDecimal(10000),
                None
              )

              val result: VatDetailsDataModel = {
                mockDateServiceCall()
                controller.getPaymentObligationDetails(Seq(testPayment), mockTodayDate)
              }

              result.isOverdue shouldBe false
            }
          }

          "user has no direct debit collection in progress" should {

            "return payment that is overdue" in {

              val testPayment: PaymentNoPeriod = PaymentNoPeriod(
                ReturnDebitCharge,
                due = LocalDate.parse("2017-01-01"),
                BigDecimal("10000"),
                Some("ABCD"),
                chargeReference = Some("XD002750002155"),
                ddCollectionInProgress = false,
                accruingInterestAmount = Some(BigDecimal(2)),
                accruingPenaltyAmount = Some(BigDecimal(100.00)),
                penaltyType = Some("LPP1"),
                BigDecimal(10000),
                None
              )

              val result: VatDetailsDataModel = {
                mockDateServiceCall()
                controller.getPaymentObligationDetails(Seq(testPayment), mockTodayDate)
              }

              result.isOverdue shouldBe true
            }
          }
        }

        "due date of payment is in the future" should {

          "return payment that is not overdue" in {

            val testPayment: PaymentNoPeriod = PaymentNoPeriod(
              ReturnDebitCharge,
              due = LocalDate.parse("2020-01-01"),
              BigDecimal("10000"),
              Some("ABCD"),
              chargeReference = Some("XD002750002155"),
              ddCollectionInProgress = false,
              accruingInterestAmount = Some(BigDecimal(2)),
              accruingPenaltyAmount = Some(BigDecimal(100.00)),
              penaltyType = Some("LPP1"),
              BigDecimal(10000),
              None
            )

            val result: VatDetailsDataModel = {
              mockDateServiceCall()
              controller.getPaymentObligationDetails(Seq(testPayment), mockTodayDate)
            }

            result.isOverdue shouldBe false
          }
        }
    }
  }

  "Calling .getReturnObligationDetails" when {

    "there is at least one obligation" when {

      "obligation is overdue" should {

        "return VatDetailsDataModel with overdue flag set to true" in {

          val expected: VatDetailsDataModel = VatDetailsDataModel(
            displayData = Some("2017-06-06"),
            hasMultiple = false,
            isOverdue = true,
            hasError = false
          )

          val result: VatDetailsDataModel = {
            mockDateServiceCall()
            controller.getReturnObligationDetails(overdueObligations.obligations, mockTodayDate)
          }
          result shouldBe expected
        }
      }

      "obligation is not overdue" should {

        "return VatDetailsDataModel with overdue flag set to false" in {

          val expected: VatDetailsDataModel = VatDetailsDataModel(
            displayData = Some("2019-06-06"),
            hasMultiple = false,
            isOverdue = false,
            hasError = false
          )

          val result: VatDetailsDataModel = {
            mockDateServiceCall()
            controller.getReturnObligationDetails(obligations.obligations, mockTodayDate)
          }
          result shouldBe expected
        }
      }

      "where duplicate obligations are present" should {

        "filter them out amd set the has multiple param to false" in {

          val expected: VatDetailsDataModel = VatDetailsDataModel(
            displayData = Some("2019-06-06"),
            hasMultiple = false,
            isOverdue = false,
            hasError = false
          )

          val result: VatDetailsDataModel = {
            mockDateServiceCall()
            controller.getReturnObligationDetails(duplicateObligations.obligations, mockTodayDate)
          }
          result shouldBe expected

        }
      }
    }
  }

  "Calling .retrieveIsOfStatus" should {

    "return true" when {

      "the mandation status in the CustomerInformation matches the expected status" in {
        val customerInfoToCompare: Either[Nothing, CustomerInformation] = Right(customerInformationMin)
        controller.retrieveIsOfStatus(customerInfoToCompare, Seq(mtdfb)) shouldBe Some(true)
      }

      "the mandation status in the CustomerInformation matches one of the expected statuses" in {
        val mandationStatusToCompareMtdfb: HttpResult[CustomerInformation] = Right(customerInformationMin)
        val mandationStatusToCompareExempt: HttpResult[CustomerInformation] = Right(customerInformationMTDfBExempt)

        controller.retrieveIsOfStatus(mandationStatusToCompareMtdfb, Seq(mtdfb, mtdfbExempt)) shouldBe Some(true)
        controller.retrieveIsOfStatus(mandationStatusToCompareExempt, Seq(mtdfb, mtdfbExempt)) shouldBe Some(true)
      }

    }

    "return false" when {

      "the mandation status does not match the expected status" in {
        val mandationStatusToCompare: Either[Nothing, CustomerInformation] = Right(customerInformationJunkStatus)
        controller.retrieveIsOfStatus(mandationStatusToCompare, Seq(mtdfb)) shouldBe Some(false)
      }

      "the mandation status does not match any of the expected statuses" in {
        val mandationStatusToCompareMtdfb: HttpResult[CustomerInformation] = Right(customerInformationMin)
        controller.retrieveIsOfStatus(mandationStatusToCompareMtdfb, Seq("randomStatus", "someStatus")) shouldBe Some(false)
      }

      "the expected mandation statuses list is empty" in {
        val mandationStatusToCompare: HttpResult[CustomerInformation] = Right(customerInformationMin)
        controller.retrieveIsOfStatus(mandationStatusToCompare, Seq.empty[String]) shouldBe Some(false)
      }

    }

    "return None" when {

      "an error is present" in {
        val errorForTest: HttpResult[CustomerInformation] = Left(UnknownError)
        controller.retrieveIsOfStatus(errorForTest, Seq.empty[String]) shouldBe None
      }
    }
  }

  "Calling .redirectForMissingTrader" when {

    "the user is a missing trader and has no inflight changes" should {

      "return true" in {
        controller.redirectForMissingTrader(Right(customerInformationMax.copy(isMissingTrader = true))) shouldBe true
      }
    }

    "the user is a missing trader and has an inflight PPOB change" should {

      "return false" in {
        controller.redirectForMissingTrader(Right(
          customerInformationMax.copy(isMissingTrader = true, hasPendingPpobChanges = true)
        )) shouldBe false
      }
    }

    "the user is not a missing trader" should {

      "return false" in {
        controller.redirectForMissingTrader(Right(customerInformationMax)) shouldBe false
      }
    }

    "there is no customer information" should {

      "return false" in {
        controller.redirectForMissingTrader(Left(BadRequestError("", ""))) shouldBe false
      }
    }
  }

  // def customerInfoWithDate(date: Option[String]): CustomerInformation =
  //     customerInformationMax.copy(poaActiveUntil = date)

  // val fixedDate: LocalDate = LocalDate.parse("2018-05-01")

  // "retrievePoaActiveForCustomer" should {
  //   "return true when poaActiveUntil is today" in {
  //     mockDateServiceCall()
  //     val result = controller.retrievePoaActiveForCustomer(Right(customerInfoWithDate(Some(fixedDate.toString))))
  //     result shouldBe true
  //   }
  //   "return true when poaActiveUntil is in the future" in {
  //     mockDateServiceCall()
  //     val futureDate = fixedDate.plusDays(5).toString
  //     val result = controller.retrievePoaActiveForCustomer(Right(customerInfoWithDate(Some(futureDate))))
  //     result shouldBe true
  //   }
  //   "return false when poaActiveUntil is in the past" in {
  //     mockDateServiceCall()
  //     val pastDate = fixedDate.minusDays(5).toString
  //     val result = controller.retrievePoaActiveForCustomer(Right(customerInfoWithDate(Some(pastDate))))
  //     result shouldBe false
  //   }
  //   "return false when poaActiveUntil is None" in {
  //     mockDateServiceCall()
  //     val result = controller.retrievePoaActiveForCustomer(Right(customerInfoWithDate(None)))
  //     result shouldBe false
  //   }
  //   "return false when accountDetails is an error" in {
  //     mockDateServiceCall()
  //     val result = controller.retrievePoaActiveForCustomer(Left(UnknownError))
  //     result shouldBe false
  //   }
  // }

}
