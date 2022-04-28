/*
 * Copyright 2022 HM Revenue & Customs
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

import common.TestModels._
import models.errors.{PaymentsError, UnknownError}
import models.payments._
import models.viewModels.OpenPaymentsViewModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.http.Status
import play.api.test.Helpers.{contentAsString, redirectLocation, _}
import views.html.errors.PaymentsError
import views.html.payments.{NoPayments, OpenPayments}

class OpenPaymentsControllerSpec extends ControllerBaseSpec {

  val noPayments: NoPayments = injector.instanceOf[NoPayments]
  val mockPaymentsError: PaymentsError = injector.instanceOf[PaymentsError]
  val openPayments: OpenPayments = injector.instanceOf[OpenPayments]

  val controller = new OpenPaymentsController(
    authorisedController,
    mockServiceInfoService,
    mockPaymentsService,
    mockDateService,
    mockAuditService,
    mcc,
    noPayments,
    mockPaymentsError,
    openPayments,
    ddInterruptPredicate,
    mockAccountDetailsService
  )

  def commonPaymentHistoryMocks(isAgent: Boolean = false): Unit = {
    if(isAgent) mockAgentAuth() else mockPrincipalAuth()
    mockDateServiceCall()
    mockAudit()
    mockServiceInfoCall()
  }

  "Calling the openPayments action" when {

    "user is hybrid" should {

      lazy val result = {
        mockPrincipalAuth()
        mockCustomerInfo(Right(customerInformationHybrid))
        controller.openPayments()(fakeRequest)
      }

      "return 303 (SEE OTHER)" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect to VAT overview page" in {
        redirectLocation(result) shouldBe Some(controllers.routes.VatDetailsController.details.url)
      }
    }

    "user is not hybrid" when {

      "the user has open payments" should {

        lazy val result = {
          commonPaymentHistoryMocks()
          mockCustomerInfo(Right(customerInformationMax))
          mockCustomerInfo(Right(customerInformationMax))
          mockOpenPayments(Right(Some(Payments(Seq(payment, payment)))))
          controller.openPayments()(fakeRequest)
        }

        "return 200 (OK)" in {
          status(result) shouldBe Status.OK
        }

        "return the payments view" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.select("h1").first().text() shouldBe "What you owe"
        }
      }

      "the user has a Payment On Account charge returned" should {

        lazy val result = {
          commonPaymentHistoryMocks()
          mockCustomerInfo(Right(customerInformationMax))
          mockOpenPayments(Right(Some(Payments(Seq(payment, paymentOnAccount)))))
          mockCustomerInfo(Right(customerInformationMax))
          controller.openPayments()(fakeRequest)
        }

        "return 200 (OK)" in {
          status(result) shouldBe Status.OK
        }

        "return the payments view with only one payment listed" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.select("payment-2") shouldBe empty
        }
      }

      "the user has no open payments" should {

        lazy val result = {
          commonPaymentHistoryMocks()
          mockCustomerInfo(Right(customerInformationMax))
          mockCustomerInfo(Right(customerInformationMax))
          mockOpenPayments(Right(None))
          controller.openPayments()(fakeRequest)
        }

        "return 200 (OK)" in {
          status(result) shouldBe Status.OK
        }

        "return the payments view" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.select("h1").first().text() shouldBe "What you owe"
        }
      }

      "the user is not authorised" should {

        "return 403 (FORBIDDEN)" in {
          val result = {
            mockInsufficientEnrolments()
            controller.openPayments()(fakeRequest)
          }

          status(result) shouldBe Status.FORBIDDEN
        }
      }

      "user is an Agent" should {

        lazy val result = {
          commonPaymentHistoryMocks(isAgent = true)
          mockCustomerInfo(Right(customerInformationMax))
          mockOpenPayments(Right(Some(Payments(Seq(payment, payment)))))
          controller.openPayments()(agentFinancialRequest)
        }

        "return 200 (OK)" in {
          status(result) shouldBe Status.OK
        }

        "return the payments view" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.select("h1").first().text() shouldBe "What your client owes"
        }
      }

      "the user is not signed in" should {

        lazy val result = {
          mockMissingBearerToken()
          controller.openPayments()(fakeRequest)
        }

        "return 303 (SEE OTHER)" in {
          status(result) shouldBe Status.SEE_OTHER
        }

        "redirect to sign in" in {
          redirectLocation(result) shouldBe Some(mockAppConfig.signInUrl)
        }
      }

      "the payments service returns an error" should {

        lazy val result = {
          commonPaymentHistoryMocks()
          mockCustomerInfo(Right(customerInformationMax))
          mockCustomerInfo(Right(customerInformationMax))
          mockOpenPayments(Left(PaymentsError))
          controller.openPayments()(fakeRequest)
        }

        "return 500 (INTERNAL SERVER ERROR)" in {
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "return the payments error view" in {
          val document: Document = Jsoup.parse(contentAsString(result))
          document.select("h1").first().text() shouldBe "Sorry, there is a problem with the service"
          document.select("#pay-now-content").text() shouldBe "If you know how much you owe, you can still pay now."
        }
      }
    }

    "the call to retrieve hybrid status fails" should {

      lazy val result = {
        commonPaymentHistoryMocks()
        mockCustomerInfo(Left(UnknownError))
        controller.openPayments()(fakeRequest)
      }

      "return 500 (INTERNAL SERVER ERROR)" in {
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

      "render the standard error view" in {
        val document: Document = Jsoup.parse(contentAsString(result))
        document.select("h1").first().text() shouldBe "Sorry, there is a problem with the service"
        document.select("#pay-now-content").size() == 0
      }
    }

    "Calling the .getModel function" when {

        "due date of payments is in the past" when {

          "user has direct debit collections in progress" should {

            "return payments that are not overdue" in {

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
                mandationStatus = "MTDfB"
              )

              val result: OpenPaymentsViewModel = {
                mockDateServiceCall()
                controller.getModel(Seq(testPayment), mandationStatus = "MTDfB")
              }

              result shouldBe expected
            }
          }

          "user has no direct debit collections in progress" should {

            "return payments that are overdue" in {

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
                mandationStatus = "MTDfB"
              )

              val result: OpenPaymentsViewModel = {
                mockDateServiceCall()
                controller.getModel(Seq(testPayment), mandationStatus = "MTDfB")
              }

              result shouldBe expected
            }
          }
        }

        "due date of payments is in the future" should {

          "return payments that are not overdue" in {

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
                isOverdue = false,
              )),
              mandationStatus = "MTDfB"
            )

            val result: OpenPaymentsViewModel = {
              mockDateServiceCall()
              controller.getModel(Seq(testPayment), mandationStatus = "MTDfB")
            }

            result shouldBe expected
          }
        }
    }

    "the user is insolvent and not continuing to trade" should {

      "return 403 (FORBIDDEN)" in {
        val result = {
          mockPrincipalAuth()
          controller.openPayments()(insolventRequest)
        }
        status(result) shouldBe Status.FORBIDDEN
      }
    }

    "the user has no viewDDInterrupt in session" should {

      lazy val result = {
        mockPrincipalAuth()
        mockDateServiceCall()
        controller.openPayments()(DDInterruptRequest)
      }

      "return 303 (SEE OTHER)" in {
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect to the DD interrupt controller" in {
        redirectLocation(result) shouldBe
          Some(controllers.routes.DDInterruptController.directDebitInterruptCall(DDInterruptRequest.uri).url)
      }
    }
  }
}
