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

import audit.models.PayGenericChargeAuditModel
import models.errors.PaymentSetupError
import models.payments.{PaymentDetailsModel, PaymentDetailsModelGeneric}
import models.{ServiceResponse, User}
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.DateService
import uk.gov.hmrc.http.HeaderCarrier
import views.html.errors.PaymentsError

import scala.concurrent.{ExecutionContext, Future}

class MakePaymentControllerSpec extends ControllerBaseSpec {

  val testAmountInPence: Int = 10000
  val testMonth: Int = 2
  val testYear: Int = 2018
  val testChargeType: String = "VAT Return Debit Charge"
  val testNonReturnChargeType: String = "VAT Default Interest Debit"
  val testDueDate: String = "2018-08-08"
  val testChargeReference: String = "XD002750002155"
  val testNoChargeReference: String = "noCR"
  val testVatPeriodEnding: String = "2018-08-08"
  val redirectUrl = "google.com"
  val expectedRedirectLocation: Option[String] = Some(redirectUrl)
  val serviceResponse: Right[Nothing, String] = Right(redirectUrl)
  implicit val dateService: DateService = app.injector.instanceOf[DateService]
  val testLinkId = "test-link-id"
  implicit val testUser: User = User(vrn = testVrn)
  implicit val hc: HeaderCarrier = HeaderCarrier()

  def mockPaymentInfo(serviceResponse: ServiceResponse[String]): Any = {
    (mockPaymentsService.setupPaymentsJourney(_: PaymentDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *)
      .returns(Future.successful(serviceResponse))
  }

  lazy val request: FakeRequest[AnyContentAsFormUrlEncoded] = fakeRequestToPOSTWithSession(
    ("amountInPence", "10000"),
    ("taxPeriodMonth", "02"),
    ("taxPeriodYear", "2018"))

  val paymentsError: PaymentsError = injector.instanceOf[PaymentsError]

  val controller = new MakePaymentController(
    mockPaymentsService,
    authorisedController,
    mockAuditService,
    mcc,
    paymentsError
  )

  "Calling the makePayment action" when {

    "the user is logged in" should {

      lazy val result = {
        mockPrincipalAuth()
        mockAudit()
        mockPaymentInfo(serviceResponse)
        controller.makePayment(
          testAmountInPence,
          testMonth,
          testYear,
          testVatPeriodEnding,
          testNonReturnChargeType,
          testDueDate,
          testChargeReference)(fakeRequestWithSession)
      }

      "return status 303(SEE OTHER)" in {
        status(result) shouldBe Status.SEE_OTHER
      }
      "return the correct redirect location" in {
        redirectLocation(result) shouldBe expectedRedirectLocation
      }
    }

    "the user is not logged in" should {

      lazy val result = {
        mockMissingBearerToken()
        controller.makePayment(
          testAmountInPence,
          testMonth,
          testYear,
          testVatPeriodEnding,
          testChargeType,
          testDueDate,
          testChargeReference)(request)
      }

      "return status 303(SEE OTHER)" in {
        status(result) shouldBe Status.SEE_OTHER
      }
      "return the correct redirect location which should be sign in" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
      }
    }

    "the user is not authenticated" should {

      lazy val result = {
        mockInsufficientEnrolments()
        controller.makePayment(
          testAmountInPence,
          testMonth,
          testYear,
          testVatPeriodEnding,
          testChargeType,
          testDueDate,
          testChargeReference)(request)
      }

      "return status 403(Forbidden" in {
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "the user is insolvent and not continuing to trade" should {

      lazy val result = {
        mockPrincipalAuth()
        controller.makePayment(
          testAmountInPence,
          testMonth,
          testYear,
          testVatPeriodEnding,
          testChargeType,
          testDueDate,
          testChargeReference)(insolventRequest)
      }

      "return status 403(Forbiddden)" in {
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "the user makes an error during set up of payment" should {

      lazy val result = {
        mockPrincipalAuth()
        mockAudit()
        mockPaymentInfo(Left(PaymentSetupError))
        controller.makePayment(
          testAmountInPence,
          testMonth,
          testYear,
          testVatPeriodEnding,
          testChargeType,
          testDueDate,
          testChargeReference)(fakeRequestWithSession)
      }
      "return status 500(Internal Server Error)" in {
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
    "the user is an Agent" should {
      lazy val result = {
        mockAgentAuth()
        mockAudit()
        controller.makePayment(
          testAmountInPence,
          testMonth,
          testYear,
          testVatPeriodEnding,
          testChargeType,
          testDueDate,
          testChargeReference)(fakeRequest)
      }

      "return status 303(SEE OTHER)" in {
        status(result) shouldBe Status.SEE_OTHER
      }
      "return the correct redirect location which is the agent client lookup hub" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.agentClientLookupHubUrl)
      }
    }
  }
  "Calling in the makePaymentNoPeriod action" when {

    "the user is logged in" when {

      "the charge is not a return and the charge reference is not noCR" should {

        lazy val result = {
          mockPrincipalAuth()
          mockAudit()
          mockPaymentInfo(serviceResponse)
          controller.makePaymentNoPeriod(
            testAmountInPence,
            testNonReturnChargeType,
            testDueDate,
            testChargeReference)(fakeRequestWithSession)
        }

        "return a 303" in {
          status(result) shouldBe Status.SEE_OTHER
        }
        "return the expected redirect location" in {
          redirectLocation(result) shouldBe expectedRedirectLocation
        }

      }
      "the charge reference is noCR" should {

        lazy val result = {
          mockPrincipalAuth()
          controller.makePaymentNoPeriod(
            testAmountInPence,
            testNonReturnChargeType,
            testDueDate,
            testNoChargeReference)(fakeRequestWithSession)
        }

        "return ISE (500)" in {
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "render the payments error page" in {
          Jsoup.parse(contentAsString(result)).title shouldBe "There is a problem with the service - Manage your VAT account - GOV.UK"
        }

      }

      "the charge is a return with no period information" should {

        lazy val result = {
          mockPrincipalAuth()
          controller.makePaymentNoPeriod(
            testAmountInPence,
            testChargeType,
            testDueDate,
            testChargeReference)(fakeRequestWithSession)
        }

        "return ISE (500)" in {
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "render the payments error page" in {
          Jsoup.parse(contentAsString(result)).title shouldBe "There is a problem with the service - Manage your VAT account - GOV.UK"
        }
      }
    }

    "The user is not logged in" should {
      lazy val result = {
        mockMissingBearerToken()
        controller.makePaymentNoPeriod(
          testAmountInPence,
          testNonReturnChargeType,
          testDueDate,
          testChargeReference)(request)
      }

      "return status 303(SEE OTHER)" in {
        status(result) shouldBe Status.SEE_OTHER
      }
      "return the correct redirect location which should be sign in" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
      }
    }
    "the user is not authenticated" should {
      lazy val result = {
        mockInsufficientEnrolments()
        controller.makePaymentNoPeriod(
          testAmountInPence,
          testNonReturnChargeType,
          testDueDate,
          testChargeReference)(request)
      }

      "return status 403(Forbidden" in {
        status(result) shouldBe Status.FORBIDDEN
      }
    }
    "the user receives an error during set up of payment" should {
      lazy val result = {
        mockPrincipalAuth()
        mockAudit()
        mockPaymentInfo(Left(PaymentSetupError))
        controller.makePaymentNoPeriod(
          testAmountInPence,
          testNonReturnChargeType,
          testDueDate,
          testChargeReference)(fakeRequestWithSession)
      }

      "return Internal Sever error " in {
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
      "return the correct title" in {
        Jsoup.parse(contentAsString(result)).title shouldBe "There is a problem with the service - Manage your VAT account - GOV.UK"
      }
    }
    "the user is an Agent" should {
      lazy val result = {
        mockAgentAuth()
        mockAudit()
        controller.makePaymentNoPeriod(
          testAmountInPence,
          testNonReturnChargeType,
          testDueDate,
          testChargeReference)(fakeRequest)
      }

      "return status 303(SEE OTHER)" in {
        status(result) shouldBe Status.SEE_OTHER
      }
      "return the correct redirect location which is the agent client lookup hub" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.agentClientLookupHubUrl)
      }
    }
  }

  "Calling the makeFullPaymentHandoff action" when {

    "the user is logged in" should {

      lazy val result = {
        mockPrincipalAuth()
        mockAudit()
        controller.makeFullPaymentHandoff()(fakeRequestWithSession)
      }

      "return status 303(SEE OTHER)" in {
        status(result) shouldBe Status.SEE_OTHER
      }
      "return the correct redirect location" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.unauthenticatedPaymentsUrl)
      }
    }

    "the user is not logged in" should {

      lazy val result = {
        mockMissingBearerToken()
        controller.makeFullPaymentHandoff()(request)
      }

      "return status 303(SEE OTHER)" in {
        status(result) shouldBe Status.SEE_OTHER
      }
      "return the correct redirect location which should be sign in" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
      }
    }

    "the user is not authenticated" should {

      lazy val result = {
        mockInsufficientEnrolments()
        controller.makeFullPaymentHandoff()(request)
      }

      "return status 403(Forbidden" in {
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "the user is insolvent and not continuing to trade" should {

      lazy val result = {
        mockPrincipalAuth()
        controller.makeFullPaymentHandoff()(insolventRequest)
      }

      "return status 403(Forbiddden)" in {
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "the user is an Agent" should {
      lazy val result = {
        mockAgentAuth()
        controller.makeFullPaymentHandoff()(fakeRequest)
      }

      "return status 303(SEE OTHER)" in {
        status(result) shouldBe Status.SEE_OTHER
      }
      "return the correct redirect location which is the agent client lookup hub" in {
        redirectLocation(result) shouldBe Some(mockAppConfig.agentClientLookupHubUrl)
      }
    }
  }


  "makeGenericPayment" when {

    "user is logged in" when {

      "earliest due date is provided" when {

        "setupPaymentsJourney is successful" must {

          "redirect user onwards and trigger audit event" in {

            mockPrincipalAuth()
            mockPaymentInfo(serviceResponse)

            val testPaymentDetails = PaymentDetailsModelGeneric(Some(testDueDate))

            mockAudit(PayGenericChargeAuditModel(testPaymentDetails, redirectUrl, testLinkId))


            val result = controller.makeGenericPayment(
              earliestDueDate = Some(testDueDate),
              linkId = testLinkId
            )(fakeRequestWithSession)

            status(result) shouldBe SEE_OTHER
            redirectLocation(result) shouldBe Some(redirectUrl)
          }
        }

        "setupPaymentsJourney is not successful" must {

          "return ISE" in {

            mockPrincipalAuth()
            mockPaymentInfo(Left(PaymentSetupError))

            val result = controller.makeGenericPayment(
              earliestDueDate = Some(testDueDate),
              linkId = testLinkId
            )(fakeRequestWithSession)

            status(result) shouldBe INTERNAL_SERVER_ERROR
            Jsoup.parse(contentAsString(result)).title shouldBe "There is a problem with the service - Manage your VAT account - GOV.UK"
          }
        }
      }

      "earliest due date is not provided" when {

        "setupPaymentsJourney is successful" must {

          "redirect user onwards and trigger audit event" in {

            mockPrincipalAuth()
            mockPaymentInfo(serviceResponse)

            val testPaymentDetails = PaymentDetailsModelGeneric(None)

            mockAudit(PayGenericChargeAuditModel(testPaymentDetails, redirectUrl, testLinkId))

            val result = controller.makeGenericPayment(
              earliestDueDate = None,
              linkId = testLinkId
            )(fakeRequestWithSession)

            status(result) shouldBe SEE_OTHER
            redirectLocation(result) shouldBe Some(redirectUrl)
          }
        }

        "setupPaymentsJourney is not successful" must {

          "return ISE" in {

            mockPrincipalAuth()
            mockPaymentInfo(Left(PaymentSetupError))

            val result = controller.makeGenericPayment(
              earliestDueDate = None,
              linkId = testLinkId
            )(fakeRequestWithSession)

            status(result) shouldBe INTERNAL_SERVER_ERROR
            Jsoup.parse(contentAsString(result)).title shouldBe "There is a problem with the service - Manage your VAT account - GOV.UK"
          }
        }
      }
    }

    "the user is not logged in" should {

      "redirect to the GG sign in" in {

        mockMissingBearerToken()
        val result = controller.makeGenericPayment(
          earliestDueDate = Some(testDueDate),
          linkId = testLinkId
        )(request)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
      }
    }

    "the user is not authenticated" should {

      "return 403" in {

        mockInsufficientEnrolments()
        val result = controller.makeGenericPayment(
          earliestDueDate = Some(testDueDate),
          linkId = testLinkId
        )(request)

        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "the user is insolvent and not continuing to trade" should {

      "return 403" in {

        mockPrincipalAuth()
        val result = controller.makeGenericPayment(
          earliestDueDate = Some(testDueDate),
          linkId = testLinkId
        )(insolventRequest)

        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "the user is an Agent" should {

      "redirect to agent client lookup hub" in {
        lazy val result = {
          mockAgentAuth()
          controller.makeFullPaymentHandoff()(fakeRequest)
        }

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(mockAppConfig.agentClientLookupHubUrl)
      }
    }
  }
}
