/*
 * Copyright 2025 HM Revenue & Customs
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

import org.jsoup.Jsoup
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.payments.PaymentsOnAccountView
import java.time.LocalDate
import scala.concurrent.Future
import common.TestModels._
import models.StandingRequest
import models.viewModels._
import models.User
import models._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import scala.concurrent.ExecutionContext
import play.api.i18n.{Messages}

class PaymentsOnAccountControllerSpec extends ControllerBaseSpec {

  val paymentsOnAccountView: PaymentsOnAccountView = injector.instanceOf[PaymentsOnAccountView]

  implicit val user: User = User("111111111")
  implicit val hc: HeaderCarrier = HeaderCarrier()

  val testDate: LocalDate = LocalDate.parse("2025-02-24")

  val controller = new PaymentsOnAccountController(
    authorisedController,
    mockDateService,
    mockPaymentsOnAccountService,
    mockServiceInfoService,
    mockServiceErrorHandler,
    mcc,
    paymentsOnAccountView,
    mockAuditService
  )

  "PaymentsOnAccountController.show" when {

    "a user is authenticated and data is retrieved successfully" should {

      lazy val result = {
        mockPrincipalAuth()
        mockServiceInfoCall()
        mockDateServiceCall()
        mockPaymentsOnAccountServiceCall()

        controller.show(fakeRequest)
      }

      "return OK" in {
        status(result) shouldBe OK
      }

      "return the payments on account view" in {
        val document: Document = Jsoup.parse(contentAsString(result))
        document.select("h1").first().text() shouldBe "Payments on account"
      }
    }

    "a user is authenticated but no POA is received" should {
      lazy val result = {
        mockPrincipalAuth()
        mockServiceInfoCall()
        mockDateServiceCall()
        mockPaymentsOnAccountServiceCall(None)

        controller.show(fakeRequest)
      }

      "return INTERNAL_SERVER_ERROR" in {
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }

      "return the error view" in {
        val document: Document = Jsoup.parse(contentAsString(result))
        document.select("h1").first().text() shouldBe "Sorry, there is a problem with the service"
      }
    }

    "a user is unauthenticated" should {
      "redirect to login page" in {
        mockMissingBearerToken()
        val result = controller.show(fakeRequest)
        status(result) shouldBe SEE_OTHER
      }
    }

    "an error occurs in ServiceInfoService" should {
      "return INTERNAL_SERVER_ERROR when ServiceInfoService fails" in {
        mockPrincipalAuth()
        (mockServiceInfoService.getPartial(_: User, _: HeaderCarrier, _: ExecutionContext, _: Messages))
          .stubs(*, *, *, *)
          .returns(Future.failed(new RuntimeException("ServiceInfoService failure")))

        (mockPaymentsOnAccountService.getPaymentsOnAccounts(_: String, _: String, _: String)(_: HeaderCarrier, _: ExecutionContext))
          .stubs(*, *, *, *, *)
          .returns(Future.successful(Some(sampleStandingRequest)))

        val result = controller.show(fakeRequest)
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }
    }

    "an error occurs in PaymentsOnAccountService" should {
      "return INTERNAL_SERVER_ERROR when PaymentsOnAccountService fails" in {
        mockPrincipalAuth()
        mockServiceInfoCall()
        (mockDateService.now _).stubs().returns(testDate)

        (mockPaymentsOnAccountService.getPaymentsOnAccounts(_: String, _: String, _: String)(_: HeaderCarrier, _: ExecutionContext))
          .stubs(*, *, *, *, *)
          .returns(Future.failed(new RuntimeException("PaymentsOnAccountService failure")))

        val result = controller.show(fakeRequest)
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "buildViewModel" should {

    val today: LocalDate = LocalDate.parse("2025-02-24")

    "correctly identify the most recent changedOn date" in {
      val standingRequest = StandingRequest(
        processingDate = "2025-02-24",
        standingRequests = List(
          StandingRequestDetail(
            requestNumber = "20000037272",
            requestCategory = "3",
            createdOn = "2023-11-30",
            changedOn = Some("2025-02-20"),
            requestItems = Nil
          ),
          StandingRequestDetail(
            requestNumber = "20000037277",
            requestCategory = "3",
            createdOn = "2024-11-30",
            changedOn = Some("2025-03-01"),
            requestItems = Nil
          )
        )
      )

      val result = PaymentsOnAccountController.buildViewModel(standingRequest, today)
      result.changedOn shouldBe Some(LocalDate.parse("2025-03-01"))
    }

    "handle empty standingRequests list correctly" in {
      val standingRequest = StandingRequest(processingDate = "2025-02-24", standingRequests = Nil)
      val result = PaymentsOnAccountController.buildViewModel(standingRequest, today)
      result.periods shouldBe empty
      result.currentPeriods shouldBe empty
      result.pastPeriods shouldBe empty
      result.nextPayment shouldBe None
    }

    "classify VAT periods correctly" in {
      val standingRequest = StandingRequest(
        processingDate = "2025-01-01",
        standingRequests = List(
          StandingRequestDetail(
            requestNumber = "20000037272",
            requestCategory = "3",
            createdOn = "2023-11-30",
            changedOn = Some("2025-02-20"),
            requestItems = List(
              RequestItem(
                period = "1",
                periodKey = "24A1",
                startDate = "2025-01-01",
                endDate = "2025-03-31",
                dueDate = "2025-03-31",
                amount = 22945.23,
                chargeReference = Some("XD006411191344"),
                postingDueDate = Some("2025-03-31")
              )
            )
          ),
          StandingRequestDetail(
            requestNumber = "20000037277",
            requestCategory = "3",
            createdOn = "2024-11-30",
            changedOn = Some("2025-03-01"),
            requestItems = List(
              RequestItem(
                period = "2",
                periodKey = "25A1",
                startDate = "2025-04-01",
                endDate = "2025-06-30",
                dueDate = "2025-06-30",
                amount = 122945.23,
                chargeReference = Some("XD006411191345"),
                postingDueDate = Some("2025-06-30")
              )
            )
          )
        )
      )

      val result = PaymentsOnAccountController.buildViewModel(standingRequest, today)

      result.currentPeriods.size shouldBe 2
      result.pastPeriods.size shouldBe 0
    }

    "correctly determine the next payment" in {
      val standingRequest = StandingRequest(
        processingDate = "2025-02-24",
        standingRequests = List(
          StandingRequestDetail(
            requestNumber = "20000037272",
            requestCategory = "3",
            createdOn = "2023-11-30",
            changedOn = Some("2025-02-20"),
            requestItems = List(
              RequestItem(
                period = "1",
                periodKey = "24A1",
                startDate = "2025-01-01",
                endDate = "2025-03-31",
                dueDate = "2025-03-31",
                amount = 22945.23,
                chargeReference = Some("XD006411191344"),
                postingDueDate = Some("2025-03-31")
              ),
              RequestItem(
                period = "2",
                periodKey = "24A1",
                startDate = "2025-01-01",
                endDate = "2025-03-31",
                dueDate = "2025-04-30",
                amount = 22945.23,
                chargeReference = Some("XD006411191345"),
                postingDueDate = Some("2025-04-30")
              )
            )
          )
        )
      )

      val result = PaymentsOnAccountController.buildViewModel(standingRequest, today)
      result.nextPayment shouldBe Some(
        PaymentDetail(PaymentType.FirstPayment, Some(LocalDate.parse("2025-03-31")), Some(22945.23))
      )
    }

    "correctly determine the balancing payment when todays date is within the 35 days after VAT Period ends" in {
      val balancingDay: LocalDate = LocalDate.parse("2025-05-01")

  val standingRequest = StandingRequest(
    processingDate = "2025-02-24",
    standingRequests = List(
      StandingRequestDetail(
        requestNumber = "20000037272",
        requestCategory = "3",
        createdOn = "2023-11-30",
        changedOn = Some("2025-02-20"),
        requestItems = List(
          RequestItem(
            period = "1",
            periodKey = "24A1",
            startDate = "2025-01-01",
            endDate = "2025-03-31",
            dueDate = "2025-03-31",
            amount = 22945.23,
            chargeReference = Some("XD006411191344"),
            postingDueDate = Some("2025-03-31")
          ),
          RequestItem(
            period = "2",
            periodKey = "24A1",
            startDate = "2025-01-01",
            endDate = "2025-03-31",
            dueDate = "2025-04-30", 
            amount = 22945.23,
            chargeReference = Some("XD006411191345"),
            postingDueDate = Some("2025-04-30")
          ),
          RequestItem(
            period = "3",
            periodKey = "24A2",
            startDate = "2025-05-01",
            endDate = "2025-07-31",
            dueDate = "2025-06-30",
            amount = 22945.23,
            chargeReference = Some("XD006411191346"),
            postingDueDate = Some("2025-06-30")
          )
        )
      )
    )
  )

      val result = PaymentsOnAccountController.buildViewModel(standingRequest, balancingDay)
       result.nextPayment shouldBe Some(
        PaymentDetail(PaymentType.ThirdPayment, Some(LocalDate.parse("2025-05-05")), None)
      )
    }
  }
}