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

import common.TestModels._
import models.User
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.test.Helpers._
import services.VatDetailsService
import uk.gov.hmrc.http.HeaderCarrier
import views.html.annual.AnnualAccountingView

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class AnnualAccountingControllerSpec extends ControllerBaseSpec {

  val view: AnnualAccountingView = injector.instanceOf[AnnualAccountingView]
  implicit val user: User = User("111111111")
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val msgs: Messages = messages

  val mockVatDetailsService: VatDetailsService = mock[VatDetailsService]

  val errorView: views.html.errors.AnnualAccountingError = injector.instanceOf[views.html.errors.AnnualAccountingError]

  private val aaStandingRequest: models.StandingRequest = {
    import models._
    val items = List(
      RequestItem("1", "25A1", "2025-02-01", "2025-04-30", "2025-03-31", 100, None, None),
      RequestItem("2", "25A1", "2025-02-01", "2025-04-30", "2025-04-30", 200, None, None),
      RequestItem("3", "25A1", "2025-02-01", "2025-11-30", "2025-11-30", 300, None, None)
    )
    StandingRequest("2025-01-01", List(
      StandingRequestDetail("REQ-AA", "4", "2025-01-01", Some("2025-01-15"), items)
    ))
  }

  val controller = new AnnualAccountingController(
    authorisedController,
    mockDateService,
    mockPaymentsOnAccountService,
    mockPaymentsService,
    mockServiceInfoService,
    mockServiceErrorHandler,
    mockVatDetailsService,
    errorView,
    mcc,
    view,
    mockAuditService
  )

  private def mockGetObligationsNone() =
    (mockVatDetailsService.getReturnObligations(_: String)(_: HeaderCarrier, _: ExecutionContext))
      .stubs(*, *, *)
      .returns(Future.successful(Right(None)))

  "AnnualAccountingController.show" when {

    "feature flag is enabled and data is available" should {
      lazy val result = {
        mockAppConfig.features.annualAccountingFeatureEnabled(true)
        mockPrincipalAuth()
        mockServiceInfoCall()
        mockDateServiceCall()
        mockPaymentsOnAccountServiceCall(Some(aaStandingRequest))
        mockGetObligationsNone()
        mockGetDirectDebitStatus(Right(directDebitEnrolled))
        (mockPaymentsService.getLiabilitiesWithDueDate(_: String, _: LocalDate, _: Option[LocalDate])(_: HeaderCarrier, _: ExecutionContext))
          .stubs(*, *, *, *, *)
          .returns(Future.successful(Right(Seq.empty)))
        (mockPaymentsService.getPaymentsForPeriod(_: String, _: LocalDate, _: LocalDate)(_: HeaderCarrier, _: ExecutionContext))
          .stubs(*, *, *, *, *)
          .returns(Future.successful(Right(models.payments.Payments(Seq.empty))))
        controller.show(fakeRequest)
      }

      "return OK" in {
        status(result) shouldBe OK
      }

      "render the Interim payments heading" in {
        val document: Document = Jsoup.parse(contentAsString(result))
        document.select("h1").first().text() shouldBe "Interim payments"
      }
    }

    "feature flag is disabled" should {
      "return NOT_FOUND" in {
        mockAppConfig.features.annualAccountingFeatureEnabled(false)
        mockPrincipalAuth()
        mockServiceInfoCall()
        val result = controller.show(fakeRequest)
        status(result) shouldBe NOT_FOUND
      }
    }

    "no standing request returned" should {
      "return INTERNAL_SERVER_ERROR" in {
        mockAppConfig.features.annualAccountingFeatureEnabled(true)
        mockPrincipalAuth()
        mockServiceInfoCall()
        mockDateServiceCall()
        mockPaymentsOnAccountServiceCall(None)
        mockGetObligationsNone()
        mockGetDirectDebitStatus(Right(directDebitEnrolled))
        (mockPaymentsService.getLiabilitiesWithDueDate(_: String, _: LocalDate, _: Option[LocalDate])(_: HeaderCarrier, _: ExecutionContext))
          .stubs(*, *, *, *, *)
          .returns(Future.successful(Right(Seq.empty)))
        (mockPaymentsService.getPaymentsForPeriod(_: String, _: LocalDate, _: LocalDate)(_: HeaderCarrier, _: ExecutionContext))
          .stubs(*, *, *, *, *)
          .returns(Future.successful(Right(models.payments.Payments(Seq.empty))))
        val result = controller.show(fakeRequest)
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }
    }

    "standing requests exist but contain no category 4 schedules" should {
      "return INTERNAL_SERVER_ERROR" in {
        mockAppConfig.features.annualAccountingFeatureEnabled(true)
        mockPrincipalAuth()
        mockServiceInfoCall()
        mockDateServiceCall()
        val nonAaStandingRequest = aaStandingRequest.copy(
          standingRequests = aaStandingRequest.standingRequests.map(_.copy(requestCategory = "3"))
        )
        mockPaymentsOnAccountServiceCall(Some(nonAaStandingRequest))
        mockGetObligationsNone()
        mockGetDirectDebitStatus(Right(directDebitEnrolled))
        (mockPaymentsService.getLiabilitiesWithDueDate(_: String, _: LocalDate, _: Option[LocalDate])(_: HeaderCarrier, _: ExecutionContext))
          .stubs(*, *, *, *, *)
          .returns(Future.successful(Right(Seq.empty)))
        (mockPaymentsService.getPaymentsForPeriod(_: String, _: LocalDate, _: LocalDate)(_: HeaderCarrier, _: ExecutionContext))
          .stubs(*, *, *, *, *)
          .returns(Future.successful(Right(models.payments.Payments(Seq.empty))))
        val result = controller.show(fakeRequest)
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }
    }
  }
}


