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

import models.errors.{BadRequestError, HttpError}
import models.obligations.VatReturnObligation
import models.payments.Payment
import models.viewModels.VatDetailsViewModel
import models.{User, VatDetailsModel}
import play.api.http.Status
import play.api.mvc.{AnyContent, Request, Result}
import play.api.test.Helpers._
import play.twirl.api.Html
import services.{BtaHeaderPartialService, EnrolmentsAuthService, VatDetailsService}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class VatDetailsControllerSpec extends ControllerBaseSpec {

  private trait DetailsTest {
    val runMock: Boolean = true
    val authResult: Future[_] =
      Future.successful(Enrolments(Set(
        Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VATRegNo", "123456789")), "")
      )))

    val vatServiceDetailsResult: Future[Right[Nothing, VatDetailsModel]] = Future.successful {
      Right(
        VatDetailsModel(
          Some(
            VatReturnObligation(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-03-30"), LocalDate.parse("2017-07-30"), "O", None, "#004")
          ), None
        )
      )
    }
    val vatServiceTradingNameResult: String = "Trading Name"
    val btaPartialResult: Html = Html("<div>example</div>")
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockVatDetailsService: VatDetailsService = mock[VatDetailsService]
    val mockBtaHeaderPartialService: BtaHeaderPartialService = mock[BtaHeaderPartialService]

    def setup(): Any = {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(authResult)

      if (runMock) {
        (mockVatDetailsService.getVatDetails(_: User, _: LocalDate)(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(vatServiceDetailsResult)

        (mockVatDetailsService.getTradingName(_: User))
          .expects(*)
          .returns(vatServiceTradingNameResult)

        (mockBtaHeaderPartialService.btaHeaderPartial()(_: Request[AnyContent]))
          .expects(*)
          .returns(btaPartialResult)
      }
    }

    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)

    def target: VatDetailsController = {
      setup()
      new VatDetailsController(messages, mockEnrolmentsAuthService, mockBtaHeaderPartialService, mockAppConfig, mockVatDetailsService)
    }
  }

  private trait HandleVatDetailsModelTest {
    val vatServiceResult: Future[Either[HttpError, VatDetailsModel]]
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockVatDetailsService: VatDetailsService = mock[VatDetailsService]
    val mockBtaHeaderPartialService: BtaHeaderPartialService = mock[BtaHeaderPartialService]
    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
    val testUser: User = User("999999999")
    implicit val hc: HeaderCarrier = HeaderCarrier()

    def setup(): Any = {
      (mockVatDetailsService.getVatDetails(_: User, _: LocalDate)(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(vatServiceResult)
    }

    def target: VatDetailsController = {
      setup()
      new VatDetailsController(messages, mockEnrolmentsAuthService, mockBtaHeaderPartialService, mockAppConfig, mockVatDetailsService)
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
        override val runMock: Boolean = false
        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())
        val result: Future[Result] = target.details()(fakeRequest)
        status(result) shouldBe Status.UNAUTHORIZED
      }
    }

    "the user is not authenticated" should {

      "return 403 (Forbidden)" in new DetailsTest {
        override val runMock: Boolean = false
        override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())
        val result: Future[Result] = target.details()(fakeRequest)
        status(result) shouldBe Status.FORBIDDEN
      }
    }
  }

  "Calling the handleVatDetailsModel function" when {

    "the vatDetailsService retrieves a valid VatDetailsModel" should {

      "return the VatDetailsModel" in new HandleVatDetailsModelTest {
        override val vatServiceResult: Future[Right[Nothing, VatDetailsModel]] = Future.successful {
          Right(
            VatDetailsModel(
              Some(
                VatReturnObligation(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-03-30"), LocalDate.parse("2017-07-30"), "O", None, "#004")
              ), None
            )
          )
        }
        private val result = await(target.handleVatDetailsModel(testUser))
        result shouldBe vatServiceResult.b
      }
    }

    "the vatDetailsService retrieves an error" should {

      "return an empty VatDetailsModel" in new HandleVatDetailsModelTest {
        override val vatServiceResult: Future[Left[BadRequestError, Nothing]] = Future.successful(Left(BadRequestError("", "")))
        val blankModel: VatDetailsModel = VatDetailsModel(None, None)
        private val result = await(target.handleVatDetailsModel(testUser))
        result shouldBe blankModel
      }
    }
  }

  "Calling .constructViewModel with a VatDetailsModel" when {

    lazy val mockEnrolmentsAuthService = mock[EnrolmentsAuthService]
    lazy val mockBtaHeaderPartialService = mock[BtaHeaderPartialService]
    lazy val mockVatDetailsService = mock[VatDetailsService]
    lazy val controller = new VatDetailsController(messages, mockEnrolmentsAuthService, mockBtaHeaderPartialService, mockAppConfig, mockVatDetailsService)
    val payment = Payment(
      LocalDate.now(),
      LocalDate.now(),
      1,
      "O",
      "#001"
    )
    val obligation = VatReturnObligation(
      LocalDate.now(),
      LocalDate.now(),
      LocalDate.now(),
      "O",
      None,
      "#001"
    )

    "there is both a payment and an obligation" should {

      lazy val result = controller.constructViewModel(VatDetailsModel(Some(obligation), Some(payment)))

      "return a VatDetailsViewModel with both due dates" in {
        result shouldEqual VatDetailsViewModel(Some(payment.due), Some(obligation.due))
      }
    }

    "there is a payment but no obligation" should {

      lazy val result = controller.constructViewModel(VatDetailsModel(None, Some(payment)))

      "return a VatDetailsViewModel with a payment due date and no obligation due date" in {
        result shouldEqual VatDetailsViewModel(Some(payment.due), None)
      }
    }

    "there is an obligation but no payment" should {

      lazy val result = controller.constructViewModel(VatDetailsModel(Some(obligation), None))

      "return a VatDetailsViewModel with an obligation due date and no payment due date" in {
        result shouldEqual VatDetailsViewModel(None, Some(obligation.due))
      }
    }

    "there is no obligation or payment" should {

      lazy val result = controller.constructViewModel(VatDetailsModel(None, None))

      "return a VatDetailsViewModel with no obligation due date and no payment due date" in {
        result shouldEqual VatDetailsViewModel(None, None)
      }
    }
  }
}
