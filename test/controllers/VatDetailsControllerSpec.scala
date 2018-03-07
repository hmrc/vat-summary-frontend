/*
 * Copyright 2018 HM Revenue & Customs
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

import models.obligations.VatReturnObligation
import models.payments.Payment
import models.viewModels.VatDetailsViewModel
import models.{User, VatDetailsModel}
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import services.{AccountDetailsService, DateService, EnrolmentsAuthService, VatDetailsService}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class VatDetailsControllerSpec extends ControllerBaseSpec {

  val payment = Some(Payment(
    LocalDate.parse("2019-01-01"),
    LocalDate.parse("2019-02-02"),
    LocalDate.parse("2019-03-03"),
    1,
    "#001"
  ))
  val obligation = Some(VatReturnObligation(
    LocalDate.parse("2019-04-04"),
    LocalDate.parse("2019-05-05"),
    LocalDate.parse("2019-06-06"),
    "O",
    None,
    "#001"
  ))
  val overduePayment = Some(Payment(
    LocalDate.parse("2017-01-01"),
    LocalDate.parse("2017-02-02"),
    LocalDate.parse("2017-03-03"),
    1,
    "#001"
  ))
  val overdueObligation = Some(VatReturnObligation(
    LocalDate.parse("2017-04-04"),
    LocalDate.parse("2017-05-05"),
    LocalDate.parse("2017-06-06"),
    "O",
    None,
    "#001"
  ))
  val entityName = Some("Cheapo Clothing")

  private trait DetailsTest {
    val authResult: Future[_] =
      Future.successful(Enrolments(Set(
        Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VRN", "123456789")), "")
      )))

    val vatServiceDetailsResult: Future[Right[Nothing, VatDetailsModel]] =
      Future.successful(Right(VatDetailsModel(payment, obligation)))

    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockVatDetailsService: VatDetailsService = mock[VatDetailsService]
    val mockAccountDetailsService: AccountDetailsService = mock[AccountDetailsService]
    val mockDateService: DateService = mock[DateService]

    def setup(): Any = {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *, *)
        .returns(authResult)

      (mockDateService.now: () => LocalDate).stubs().returns(LocalDate.parse("2018-05-01"))

      (mockVatDetailsService.getVatDetails(_: User, _: LocalDate)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *, *)
        .returns(vatServiceDetailsResult)

      (mockAccountDetailsService.getEntityName(_: String)(_: HeaderCarrier, _: ExecutionContext))
        .stubs(*, *, *)
        .returns(Future.successful(entityName))
    }

    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)

    def target: VatDetailsController = {
      setup()
      new VatDetailsController(messages, mockEnrolmentsAuthService, mockAppConfig, mockVatDetailsService, mockAccountDetailsService, mockDateService)
    }
  }

  "Calling the details action" when {

    "the user is logged in" should {

      "return 200" in new DetailsTest {
        private val result = target.details()(fakeRequest)
        status(result) shouldBe Status.OK
      }

      "return HTML" in new DetailsTest {
        private val result = target.details()(fakeRequest)
        contentType(result) shouldBe Some("text/html")
      }

      "return charset utf-8" in new DetailsTest {
        private val result = target.details()(fakeRequest)
        charset(result) shouldBe Some("utf-8")
      }
    }

    "the user is not logged in" should {

      "return 401 (Unauthorised)" in new DetailsTest {
        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())
        val result: Future[Result] = target.details()(fakeRequest)
        status(result) shouldBe Status.UNAUTHORIZED
      }
    }

    "the user is not authenticated" should {

      "return 403 (Forbidden)" in new DetailsTest {
        override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())
        val result: Future[Result] = target.details()(fakeRequest)
        status(result) shouldBe Status.FORBIDDEN
      }
    }
  }

  "Calling .constructViewModel with a VatDetailsModel" when {

    lazy val paymentDueDate: Option[LocalDate] = Some(LocalDate.parse("2019-03-03"))
    lazy val obligationDueDate: Option[LocalDate] = Some(LocalDate.parse("2019-06-06"))
    lazy val overduePaymentDueDate: Option[LocalDate] = Some(LocalDate.parse("2017-03-03"))
    lazy val overdueObligationDueDate: Option[LocalDate] = Some(LocalDate.parse("2017-06-06"))

    "there is both a payment and an obligation" should {

      "return a VatDetailsViewModel with both due dates" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(paymentDueDate, obligationDueDate, entityName)
        lazy val result: VatDetailsViewModel = target.constructViewModel(VatDetailsModel(payment, obligation), entityName)

        result shouldBe expected
      }
    }

    "there is a payment but no obligation" should {

      "return a VatDetailsViewModel with a payment due date and no obligation due date" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(paymentDueDate, None, entityName)
        lazy val result: VatDetailsViewModel = target.constructViewModel(VatDetailsModel(payment, None), entityName)

        result shouldBe expected
      }
    }

    "there is an obligation but no payment" should {

      "return a VatDetailsViewModel with an obligation due date and no payment due date" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(None, obligationDueDate, entityName)
        lazy val result: VatDetailsViewModel = target.constructViewModel(VatDetailsModel(None, obligation), entityName)

        result shouldBe expected
      }
    }

    "there is no obligation or payment" should {

      "return a VatDetailsViewModel with no obligation due date and no payment due date" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(None, None, entityName)
        lazy val result: VatDetailsViewModel = target.constructViewModel(VatDetailsModel(None, None), entityName)

        result shouldBe expected
      }
    }

    "there is no obligation, payment, or entity name" should {

      "return a VatDetailsViewModel with no obligation due date, payment due date, or entity name" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(None, None, None)
        lazy val result: VatDetailsViewModel = target.constructViewModel(VatDetailsModel(None, None), None)

        result shouldBe expected
      }
    }

    "the obligation is overdue" should {

      "return a VatDetailsViewModel with the return overdue flag set" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(paymentDueDate, overdueObligationDueDate, entityName, returnOverdue = true)
        lazy val result: VatDetailsViewModel = target.constructViewModel(VatDetailsModel(payment, overdueObligation), entityName)

        result shouldBe expected
      }
    }

    "the payment is overdue" should {

      "return a VatDetailsViewModel with the payment overdue flag set" in new DetailsTest {
        lazy val expected = VatDetailsViewModel(overduePaymentDueDate, obligationDueDate, entityName, paymentOverdue = true)
        lazy val result: VatDetailsViewModel = target.constructViewModel(VatDetailsModel(overduePayment, obligation), entityName)

        result shouldBe expected
      }
    }
  }
}
