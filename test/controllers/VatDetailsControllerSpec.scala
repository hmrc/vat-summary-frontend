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
import common.FinancialTransactionsConstants._
import common.{SessionKeys, TestModels}
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models._
import models.errors.{NextPaymentError, ObligationsError, _}
import models.obligations.{VatReturnObligation, VatReturnObligations}
import models.payments.{Payment, PaymentNoPeriod, Payments, ReturnDebitCharge}
import models.penalties.PenaltiesSummary
import models.viewModels.VatDetailsViewModel
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import services._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.vatDetails.Details
import common.TestModels._
import org.jsoup.Jsoup

import scala.concurrent.{ExecutionContext, Future}

class VatDetailsControllerSpec extends ControllerBaseSpec {

  val obligations: VatReturnObligations = TestModels.obligations
  val payments: Payments = TestModels.payments
  val mockVatDetailsService: VatDetailsService = mock[VatDetailsService]
  val mockPenaltiesService: PenaltiesService = mock[PenaltiesService]

  def mockReturnObligations(result: ServiceResponse[Option[VatReturnObligations]]): Any =
    (mockVatDetailsService.getReturnObligations(_: String, _: LocalDate)(_: HeaderCarrier, _: ExecutionContext))
      .stubs(*, *, *, *)
      .returns(Future.successful(result))

  def mockPaymentLiabilities(result: ServiceResponse[Option[Payments]]): Any =
    (mockVatDetailsService.getPaymentObligations(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .stubs(*, *, *)
      .returns(Future.successful(result))

  def mockPenaltiesService(result: HttpGetResult[PenaltiesSummary]): Any =
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
    ddInterruptPredicate
  )
  
  "Calling the details action" when {

    "the user is logged in and does not meet the criteria to see an interrupt screen" should {

      lazy val result: Future[Result] = {
        mockPrincipalAuth()
        mockDateServiceCall()
        mockReturnObligations(Right(Some(obligations)))
        mockPaymentLiabilities(Right(Some(payments)))
        mockCustomerInfo(Right(customerInformationMax))
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
        mockServiceInfoCall()
        mockAudit()
        mockPenaltiesService(penaltySummaryNoResponse)
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

    "the user has no ddInterrupt value in session" should {

      lazy val result = {
        mockPrincipalAuth()
        controller.details()(DDInterruptRequest)
      }

      "return 303 (SEE_OTHER)" in {
        status(result) shouldBe Status.SEE_OTHER
      }
      "redirect to the DD interrupt controller" in {
        redirectLocation(result) shouldBe
          Some(controllers.routes.DDInterruptController.directDebitInterruptCall("/homepage").url)
      }
    }

    "the user has penalties" should {

      lazy val result: Future[Result] = {
        mockPrincipalAuth()
        mockDateServiceCall()
        mockReturnObligations(Right(Some(obligations)))
        mockPaymentLiabilities(Right(Some(payments)))
        mockCustomerInfo(Right(customerInformationMax))
        mockServiceInfoCall()
        mockAudit()
        mockPenaltiesService(penaltySummaryResponse)
        controller.details()(fakeRequest)
      }

      "return 200" in {
        status(result) shouldBe OK
      }

      "render the page with a penalties tile" in {
        messages(Jsoup.parse(contentAsString(result)).select("#penalties-heading").text) shouldBe "Penalties and appeals"
      }
    }
  }

  "Calling .detailsRedirectToEmailVerification" should {

    "redirect to email verification" when {

      "all relevant information is returned from vat-subscription" in {

        val result = {
          mockPrincipalAuth()
          mockCustomerInfo(Right(customerInformationMax))
          controller.detailsRedirectToEmailVerification()(fakeRequest)
        }

        status(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(mockAppConfig.verifyEmailUrl)
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
            currentDate = testDate, partyType = Some("7"), userEmailVerified = true
          )
        lazy val result: VatDetailsViewModel = {
          mockDateServiceCall()
          controller.constructViewModel(
            Right(Some(obligations)),
            Right(Some(payments)),
            Right(customerInformationMax),
            None
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
            currentDate = testDate, partyType = Some("7"), userEmailVerified = true
          )
        lazy val result: VatDetailsViewModel = {
          mockDateServiceCall()
          controller.constructViewModel(
            Right(None),
            Right(Some(payments)),
            Right(customerInformationMax),
            None
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
            currentDate = testDate, partyType = Some("7"), userEmailVerified = true
          )
        lazy val result: VatDetailsViewModel = {
          mockDateServiceCall()
          controller.constructViewModel(
            Right(Some(obligations)),
            Right(None),
            Right(customerInformationMax),
            None
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
            currentDate = testDate, partyType = Some("7"), userEmailVerified = true
          )
        lazy val result: VatDetailsViewModel = {
          mockDateServiceCall()
          controller.constructViewModel(
            Right(None),
            Right(None),
            Right(customerInformationMax),
            None
          )
        }

        result shouldBe expected
      }
    }

    "there is no obligation, payment, or entity name" should {

      "return a VatDetailsViewModel with no obligation due date, payment due date, entity name or partyType" in {
        lazy val expected: VatDetailsViewModel =
          VatDetailsViewModel(None, None, None, currentDate = testDate, partyType = None, userEmailVerified = true)
        lazy val result: VatDetailsViewModel = {
          mockDateServiceCall()
          controller.constructViewModel(
            Right(None),
            Right(None),
            Right(customerInformationMin),
            None
          )
        }

        result shouldBe expected
      }
    }

    "there is no obligation, payment, entity name, partyType and there is a non-MTD mandation status" should {

      "return a VatDetailsViewModel with no obligation due date, payment due date or entity name with the showSignUp flag set to true" in {
        lazy val expected: VatDetailsViewModel = VatDetailsViewModel(
          None, None, None, showSignUp = Some(true), currentDate = testDate, partyType = None, userEmailVerified = true)
        lazy val result: VatDetailsViewModel = {
          mockDateServiceCall()
          controller.constructViewModel(
            Right(None),
            Right(None),
            Right(customerInformationMTDfBExempt),
            None
          )
        }

        result shouldBe expected
      }
    }

    "there is an error returned by the obligations API" should {

      "return a VatDetailsViewModel with the returnError flag set" in {
        lazy val expected: VatDetailsViewModel = VatDetailsViewModel(
          None, None, Some(entityName), returnObligationError = true, deregDate = Some(LocalDate.parse("2020-01-01")),
          currentDate = testDate, partyType = Some("7"), userEmailVerified = true)
        lazy val result: VatDetailsViewModel = {
          mockDateServiceCall()
          controller.constructViewModel(
            Left(ObligationsError),
            Right(None),
            Right(customerInformationMax),
            None
          )
        }

        result shouldBe expected
      }
    }

    "there is an error returned by the financial data API" should {

      "return a VatDetailsViewModel with the paymentError flag set" in {
        lazy val expected: VatDetailsViewModel = VatDetailsViewModel(
          None, None, Some(entityName), paymentError = true, deregDate = Some(LocalDate.parse("2020-01-01")),
          currentDate = testDate, partyType = Some("7"), userEmailVerified = true)
        lazy val result: VatDetailsViewModel = {
          mockDateServiceCall()
          controller.constructViewModel(
            Right(None),
            Left(NextPaymentError),
            Right(customerInformationMax),
            None
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
          userEmailVerified = true)
        lazy val result: VatDetailsViewModel = {
          mockDateServiceCall()
          controller.constructViewModel(
            Left(ObligationsError),
            Left(NextPaymentError),
            Right(customerInformationMax),
            None
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
          userEmailVerified = true)
        lazy val result: VatDetailsViewModel = {
          mockDateServiceCall()
          controller.constructViewModel(
            Right(Some(overdueObligations)),
            Right(Some(payments)),
            Right(customerInformationMax),
            None
          )
        }

        result shouldBe expected
      }
    }

    "the user has penalties" should {
      "return a VatDetailsModel with displayPenaltiesTile set to true" in {
        lazy val expectedContent: VatDetailsViewModel = VatDetailsViewModel(
          paymentDueDate, obligationData, Some(entityName), deregDate = Some(LocalDate.parse("2020-01-01")),
          currentDate = testDate, partyType = Some("7"), userEmailVerified = true, penaltiesSummary = Some(penaltiesSummaryModel)
        )
        lazy val result: VatDetailsViewModel = {
          mockDateServiceCall()
          controller.constructViewModel(
            Right(Some(obligations)),
            Right(Some(payments)),
            Right(customerInformationMax),
            Some(penaltiesSummaryModel)
          )
        }
        result shouldBe expectedContent
      }
    }

    "the user has no penalties" should {
      "return a VatDetailsModel with displayPenaltiesTile set to false" in {
        lazy val expectedContent: VatDetailsViewModel = VatDetailsViewModel(
          paymentDueDate, obligationData, Some(entityName), deregDate = Some(LocalDate.parse("2020-01-01")),
          currentDate = testDate, partyType = Some("7"), userEmailVerified = true
        )
        lazy val result: VatDetailsViewModel = {
          mockDateServiceCall()
          controller.constructViewModel(
            Right(Some(obligations)),
            Right(Some(payments)),
            Right(customerInformationMax),
            None
          )
        }
        result shouldBe expectedContent
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

              val testPayment: PaymentNoPeriod = Payment(
                ReturnDebitCharge,
                due = LocalDate.parse("2017-01-01"),
                BigDecimal("10000"),
                Some("ABCD"),
                chargeReference = Some("XD002750002155"),
                ddCollectionInProgress = true
              )

              val result: VatDetailsDataModel = {
                mockDateServiceCall()
                controller.getPaymentObligationDetails(Seq(testPayment))
              }

              result.isOverdue shouldBe false
            }
          }

          "user has no direct debit collection in progress" should {

            "return payment that is overdue" in {

              val testPayment: PaymentNoPeriod = Payment(
                ReturnDebitCharge,
                due = LocalDate.parse("2017-01-01"),
                BigDecimal("10000"),
                Some("ABCD"),
                chargeReference = Some("XD002750002155"),
                ddCollectionInProgress = false
              )

              val result: VatDetailsDataModel = {
                mockDateServiceCall()
                controller.getPaymentObligationDetails(Seq(testPayment))
              }

              result.isOverdue shouldBe true
            }
          }
        }

        "due date of payment is in the future" should {

          "return payment that is not overdue" in {

            val testPayment: PaymentNoPeriod = Payment(
              ReturnDebitCharge,
              due = LocalDate.parse("2020-01-01"),
              BigDecimal("10000"),
              Some("ABCD"),
              chargeReference = Some("XD002750002155"),
              ddCollectionInProgress = false
            )

            val result: VatDetailsDataModel = {
              mockDateServiceCall()
              controller.getPaymentObligationDetails(Seq(testPayment))
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
            controller.getReturnObligationDetails(overdueObligations.obligations)
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
            controller.getReturnObligationDetails(obligations.obligations)
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
        val mandationStatusToCompareMtdfb: HttpGetResult[CustomerInformation] = Right(customerInformationMin)
        val mandationStatusToCompareExempt: HttpGetResult[CustomerInformation] = Right(customerInformationMTDfBExempt)

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
        val mandationStatusToCompareMtdfb: HttpGetResult[CustomerInformation] = Right(customerInformationMin)
        controller.retrieveIsOfStatus(mandationStatusToCompareMtdfb, Seq("randomStatus", "someStatus")) shouldBe Some(false)
      }

      "the expected mandation statuses list is empty" in {
        val mandationStatusToCompare: HttpGetResult[CustomerInformation] = Right(customerInformationMin)
        controller.retrieveIsOfStatus(mandationStatusToCompare, Seq.empty[String]) shouldBe Some(false)
      }

    }

    "return None" when {

      "an error is present" in {
        val errorForTest: HttpGetResult[CustomerInformation] = Left(UnknownError)
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
  }
}
