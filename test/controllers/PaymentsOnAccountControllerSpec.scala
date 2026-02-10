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
import models.obligations.{VatReturnObligations, VatReturnObligation}
import models.User
import models._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import scala.concurrent.ExecutionContext
import play.api.i18n.{Messages}
import views.html.errors.PaymentsOnAccountError
import services.VatDetailsService

class PaymentsOnAccountControllerSpec extends ControllerBaseSpec {

  val paymentsOnAccountView: PaymentsOnAccountView =
    injector.instanceOf[PaymentsOnAccountView]
  val paymentsOnAccountErrorView: PaymentsOnAccountError =
    injector.instanceOf[PaymentsOnAccountError]

  implicit val user: User = User("111111111")
  implicit val hc: HeaderCarrier = HeaderCarrier()

  val mockVatDetailsService: VatDetailsService = mock[VatDetailsService]

  val testDate: LocalDate = LocalDate.parse("2025-02-24")

  val controller = new PaymentsOnAccountController(
    authorisedController,
    mockDateService,
    mockPaymentsOnAccountService,
    mockAccountDetailsService,
    mockServiceInfoService,
    mockServiceErrorHandler,
    mockVatDetailsService,
    mcc,
    paymentsOnAccountView,
  )

    def mockGetObligationsCall() = (mockVatDetailsService.getReturnObligations(_: String)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *)
        .returns(Future.successful(Right(None)))

  private def mockGetEntityName(name: Option[String]) =
    (mockAccountDetailsService.getEntityName(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .stubs(*, *, *)
      .returns(Future.successful(Right(name)))

  "PaymentsOnAccountController.show" when {

    "a user is authenticated and data is retrieved successfully" should {
      lazy val result = {
        mockPrincipalAuth()
        mockServiceInfoCall()
        mockGetEntityName(Some("Entity"))
        mockDateServiceCall()
        mockPaymentsOnAccountServiceCall()
        mockGetObligationsCall()
        controller.show(fakeRequest)
      }

      "return OK" in {
        mockAppConfig.features.poaActiveFeatureEnabled(true)
        status(result) shouldBe OK
      }

      "return the payments on account view" in {
        mockAppConfig.features.poaActiveFeatureEnabled(true)
        val document: Document = Jsoup.parse(contentAsString(result))
        document.select("h1").first().text() shouldBe "Payments on account"
      }
    }

    "a user is authenticated but no POA is received" should {
      lazy val result = {
        mockPrincipalAuth()
        mockServiceInfoCall()
        mockGetEntityName(Some("Entity"))
        mockDateServiceCall()
        mockPaymentsOnAccountServiceCall(None)

        controller.show(fakeRequest)
      }

      "return INTERNAL_SERVER_ERROR" in {
        mockAppConfig.features.poaActiveFeatureEnabled(true)
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }

      "return the error view" in {
        val document: Document = Jsoup.parse(contentAsString(result))
        document
          .select("h1")
          .first()
          .text() shouldBe "Sorry, there is a problem with the service"
      }
    }

    "a user is unauthenticated" should {
      "redirect to login page" in {
        mockAppConfig.features.poaActiveFeatureEnabled(false)
        mockMissingBearerToken()
        val result = controller.show(fakeRequest)
        status(result) shouldBe SEE_OTHER
      }
    }

    "an error occurs in ServiceInfoService" should {
      "return INTERNAL_SERVER_ERROR when ServiceInfoService fails" in {
        mockAppConfig.features.poaActiveFeatureEnabled(true)
        mockPrincipalAuth()
        (mockServiceInfoService
          .getPartial(
            _: User,
            _: HeaderCarrier,
            _: ExecutionContext,
            _: Messages
          ))
          .stubs(*, *, *, *)
          .returns(
            Future.failed(new RuntimeException("ServiceInfoService failure"))
          )

        (mockPaymentsOnAccountService
          .getPaymentsOnAccounts(_: String)(
            _: HeaderCarrier,
            _: ExecutionContext
          ))
          .stubs(*, *, *)
          .returns(Future.successful(Some(sampleStandingRequest)))

        val result = controller.show(fakeRequest)
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }
    }

    "an error occurs in PaymentsOnAccountService" should {
      "return INTERNAL_SERVER_ERROR when PaymentsOnAccountService fails" in {
        mockAppConfig.features.poaActiveFeatureEnabled(true)
        mockPrincipalAuth()
        mockServiceInfoCall()
        (mockDateService.now _).stubs().returns(testDate)

        (mockPaymentsOnAccountService
          .getPaymentsOnAccounts(_: String)(
            _: HeaderCarrier,
            _: ExecutionContext
          ))
          .stubs(*, *, *)
          .returns(
            Future.failed(
              new RuntimeException("PaymentsOnAccountService failure")
            )
          )

        val result = controller.show(fakeRequest)
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }
    }

    "feature flag poaActiveFeatureEnabled flag is false" should {
      "return NOT_FOUND" in {
        mockAppConfig.features.poaActiveFeatureEnabled(false)
        mockPrincipalAuth()
        mockServiceInfoCall()
        val result = controller.show(fakeRequest)
        status(result) shouldBe NOT_FOUND
        val document: Document = Jsoup.parse(contentAsString(result))
        document
          .select("h1")
          .first()
          .text() shouldBe "This page cannot be found"
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

      val result =
        PaymentsOnAccountController.buildViewModel(
          standingRequest,
          today,
          None,
          None)
      result.changedOn shouldBe Some(LocalDate.parse("2025-03-01"))
    }

    "handle empty standingRequests list correctly" in {
      val standingRequest =
        StandingRequest(processingDate = "2025-02-24", standingRequests = Nil)
      val result =
        PaymentsOnAccountController.buildViewModel(
          standingRequest,
          today,
          None,
          None)
      result.periods shouldBe empty
      result.currentPeriods shouldBe empty
      result.pastPeriods shouldBe empty
      result.nextPayment shouldBe None
    }

    "ensure pastPeriods are sorted in descending order by endDate" in {
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
                periodKey = "23A1",
                startDate = "2023-01-01",
                endDate = "2023-03-31",
                dueDate = "2023-04-30",
                amount = 1000.00,
                chargeReference = Some("XD006411191300"),
                postingDueDate = Some("2023-04-30")
              )
            )
          ),
          StandingRequestDetail(
            requestNumber = "20000037273",
            requestCategory = "3",
            createdOn = "2024-11-30",
            changedOn = Some("2025-03-01"),
            requestItems = List(
              RequestItem(
                period = "2",
                periodKey = "24A1",
                startDate = "2024-01-01",
                endDate = "2024-03-31",
                dueDate = "2024-04-30",
                amount = 2000.00,
                chargeReference = Some("XD006411191301"),
                postingDueDate = Some("2024-04-30")
              )
            )
          ),
          StandingRequestDetail(
            requestNumber = "20000037274",
            requestCategory = "3",
            createdOn = "2025-01-30",
            changedOn = Some("2025-03-01"),
            requestItems = List(
              RequestItem(
                period = "3",
                periodKey = "25A1",
                startDate = "2025-01-01",
                endDate = "2025-03-31",
                dueDate = "2025-04-30",
                amount = 3000.00,
                chargeReference = Some("XD006411191302"),
                postingDueDate = Some("2025-04-30")
              )
            )
          )
        )
      )

      val result =
        PaymentsOnAccountController.buildViewModel(
          standingRequest,
          today,
          None,
          None)
      val sortedPastPeriods = result.pastPeriods.map(_.endDate)

      sortedPastPeriods shouldBe List(
        LocalDate.parse("2024-03-31"),
        LocalDate.parse("2023-03-31")
      )
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

      val result =
        PaymentsOnAccountController.buildViewModel(
          standingRequest,
          today,
          None,
          None)

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

      val result =
        PaymentsOnAccountController.buildViewModel(
          standingRequest,
          today,
          None,
          None)
      result.nextPayment shouldBe Some(
        PaymentDetail(
          PaymentType.FirstPayment,
           DueDate(Some(LocalDate.parse("2025-03-31"))),
          Some(22945.23)
        )
      )
    }

    "correctly determine the balancing payment when today's date is more than 35 days after VAT Period ends" in {
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

      val result = PaymentsOnAccountController.buildViewModel(
        standingRequest,
        balancingDay,
        None,
        None
      )

      val vatPeriodEnd = LocalDate.parse("2025-03-31")
      val thresholdDate = vatPeriodEnd.plusDays(35)
      val testDate = vatPeriodEnd.plusDays(36)

      assert(testDate.isAfter(thresholdDate))

      result.nextPayment shouldBe Some(
        PaymentDetail(
          PaymentType.ThirdPayment,
           DueDate(Some(testDate)),
          None
        )
      )
    }

    "correctly determine the third payment date when a vat obligation is due in that period." in {
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

      val result = PaymentsOnAccountController.buildViewModel(
        standingRequest,
        balancingDay,
        Some(VatReturnObligations(List(VatReturnObligation(
          LocalDate.parse("2025-02-01"),
          LocalDate.parse("2025-04-30"),
          LocalDate.parse("2025-05-31"),
          "O",
          None,
          "25A1")))),
        None
      )

      val vatPeriodEnd = LocalDate.parse("2025-03-31")

      result.nextPayment shouldBe Some(
        PaymentDetail(
          PaymentType.ThirdPayment,
           DueDate(Some(LocalDate.parse("2025-05-06")), Some(LocalDate.parse("2025-05-31"))),
          None
        )
      )
    }


    "ensure VAT period is marked as current on its startDate only if there are no past periods" in {
      val today: LocalDate =
        LocalDate.parse("2025-04-01")

      val standingRequest = StandingRequest(
        processingDate = today.toString,
        standingRequests = List(
          StandingRequestDetail(
            requestNumber = "20000037272",
            requestCategory = "3",
            createdOn = "2023-11-30",
            changedOn = Some("2025-02-20"),
            requestItems = List(
              RequestItem(
                period = "1",
                periodKey = "25A1",
                startDate = "2025-04-01",
                endDate = "2025-06-30",
                dueDate = "2025-06-30",
                amount = 22945.23,
                chargeReference = Some("XD006411191344"),
                postingDueDate = Some("2025-06-30")
              )
            )
          ),
          StandingRequestDetail(
            requestNumber = "20000037273",
            requestCategory = "3",
            createdOn = "2024-11-30",
            changedOn = Some("2025-03-01"),
            requestItems = List(
              RequestItem(
                period = "2",
                periodKey = "25A2",
                startDate = "2025-07-01",
                endDate = "2025-09-30",
                dueDate = "2025-09-30",
                amount = 122945.23,
                chargeReference = Some("XD006411191345"),
                postingDueDate = Some("2025-09-30")
              )
            )
          )
        )
      )

      val result =
        PaymentsOnAccountController.buildViewModel(
          standingRequest,
          today,
          None,
          None)

      val hasPastPeriods = result.pastPeriods.nonEmpty

      if (!hasPastPeriods) {
        val currentPeriods = result.currentPeriods.filter(_.isCurrent)
        currentPeriods.size shouldBe 1
        currentPeriods.head.startDate shouldBe today
      } else {
        result.currentPeriods.count(_.isCurrent) shouldBe 0
      }

      val futurePeriods = result.periods.filter(_.startDate.isAfter(today))
      futurePeriods.foreach { period =>
        period.isCurrent shouldBe false
      }
    }

    "ensure only one VAT period is marked as current" in {

      val today: LocalDate = LocalDate.parse("2025-02-24")

      val standingRequest = StandingRequest(
        processingDate = today.toString,
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
                dueDate = "2025-04-30",
                amount = 22945.23,
                chargeReference = Some("XD006411191344"),
                postingDueDate = Some("2025-04-30")
              )
            )
          ),
          StandingRequestDetail(
            requestNumber = "20000037273",
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
          ),
          StandingRequestDetail(
            requestNumber = "20000037274",
            requestCategory = "3",
            createdOn = "2025-01-30",
            changedOn = Some("2025-03-01"),
            requestItems = List(
              RequestItem(
                period = "3",
                periodKey = "25A2",
                startDate = "2025-07-01",
                endDate = "2025-09-30",
                dueDate = "2025-09-30",
                amount = 150000.00,
                chargeReference = Some("XD006411191346"),
                postingDueDate = Some("2025-09-30")
              )
            )
          )
        )
      )

      val result =
        PaymentsOnAccountController.buildViewModel(
          standingRequest,
          today,
          None,
          None)

      val currentPeriods = result.currentPeriods.filter(_.isCurrent)
      currentPeriods.size shouldBe 1

      val expectedCurrentPeriodStartDate = LocalDate.parse("2025-01-01")
      currentPeriods.head.startDate shouldBe expectedCurrentPeriodStartDate

      val futurePeriods = result.periods.filter(_.startDate.isAfter(today))
      futurePeriods.foreach { period =>
        period.isCurrent shouldBe false
      }
    }
  }
}
