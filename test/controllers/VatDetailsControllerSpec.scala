/*
 * Copyright 2017 HM Revenue & Customs
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
import models.obligations.Obligation
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

  private trait Test {
    val runMock: Boolean = true
    val authResult: Future[_]
    val vatServiceResult: Future[Either[HttpError, VatDetailsModel]]
    val btaPartialResult: Html = Html("<div>example</div>")
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockService: VatDetailsService = mock[VatDetailsService]
    val mockBtaHeaderPartialService: BtaHeaderPartialService = mock[BtaHeaderPartialService]

    def setup(): Any = {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(authResult)

      if (runMock) {
        (mockService.getVatDetails(_: User, _: LocalDate)(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(vatServiceResult)

        (mockBtaHeaderPartialService.btaHeaderPartial()(_: Request[AnyContent]))
          .expects(*)
          .returns(btaPartialResult)
      }
    }

    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)

    def target: VatDetailsController = {
      setup()
      new VatDetailsController(messages, mockEnrolmentsAuthService, mockBtaHeaderPartialService, mockAppConfig, mockService)
    }
  }

  "Calling the details action" when {

    "the user is logged in" when {

      "the vat api service has returned an obligation" should {

        "return 200" in new Test {
          override val vatServiceResult: Future[Right[Nothing, VatDetailsModel]] = Future.successful {
            Right(
              VatDetailsModel(
                Some(
                  Obligation(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-03-30"), LocalDate.parse("2017-07-30"), "O", None, "#004")
                ), None
              )
            )
          }

          override val authResult: Future[Enrolments] = Future.successful(Enrolments(Set(Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VATRegNum", "123")), ""))))
          private val result = target.details()(fakeRequest)
          status(result) shouldBe Status.OK
        }

        "return HTML" in new Test {
          override val vatServiceResult: Future[Right[Nothing, VatDetailsModel]] = Future.successful {
            Right(
              VatDetailsModel(
                Some(
                  Obligation(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-03-30"), LocalDate.parse("2017-07-30"), "O", None, "#004")
                ), None
              )
            )
          }

          override val authResult: Future[Enrolments] = Future.successful(Enrolments(Set(Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VATRegNum", "123")), ""))))
          private val result = target.details()(fakeRequest)
          contentType(result) shouldBe Some("text/html")
        }

        "return charset utf-8" in new Test {
          override val vatServiceResult: Future[Right[Nothing, VatDetailsModel]] = Future.successful {
            Right(
              VatDetailsModel(
                Some(
                  Obligation(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-03-30"), LocalDate.parse("2017-07-30"), "O", None, "#004")
                ), None
              )
            )
          }

          override val authResult: Future[Enrolments] = Future.successful(Enrolments(Set(Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VATRegNum", "123")), ""))))
          private val result = target.details()(fakeRequest)
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the vat api service hasn't returned an obligation" should {

        "return 200" in new Test {
          override val vatServiceResult: Future[Right[Nothing, VatDetailsModel]] = Future.successful {
            Right(
              VatDetailsModel(
                None, None
              )
            )
          }

          override val authResult: Future[Enrolments] = Future.successful(Enrolments(Set(Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VATRegNum", "123")), ""))))
          private val result = target.details()(fakeRequest)
          status(result) shouldBe Status.OK
        }

        "return HTML" in new Test {
          override val vatServiceResult: Future[Right[Nothing, VatDetailsModel]] = Future.successful {
            Right(
              VatDetailsModel(
                None, None
              )
            )
          }

          override val authResult: Future[Enrolments] = Future.successful(Enrolments(Set(Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VATRegNum", "123")), ""))))
          private val result = target.details()(fakeRequest)
          contentType(result) shouldBe Some("text/html")
        }

        "return charset utf-8" in new Test {
          override val vatServiceResult: Future[Right[Nothing, VatDetailsModel]] = Future.successful {
            Right(
              VatDetailsModel(
                None, None
              )
            )
          }

          override val authResult: Future[Enrolments] = Future.successful(Enrolments(Set(Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VATRegNum", "123")), ""))))
          private val result = target.details()(fakeRequest)
          charset(result) shouldBe Some("utf-8")
        }
      }

      "the vat api service has returned an error" should {

        "return 200" in new Test {
          override val vatServiceResult: Future[Left[BadRequestError, Nothing]] = Future.successful(Left(BadRequestError("", "")))

          override val authResult: Future[Enrolments] = Future.successful(Enrolments(Set(Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VATRegNum", "123")), ""))))
          private val result = target.details()(fakeRequest)
          status(result) shouldBe Status.OK
        }

        "return HTML" in new Test {
          override val vatServiceResult: Future[Left[BadRequestError, Nothing]] = Future.successful(Left(BadRequestError("", "")))

          override val authResult: Future[Enrolments] = Future.successful(Enrolments(Set(Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VATRegNum", "123")), ""))))
          private val result = target.details()(fakeRequest)
          contentType(result) shouldBe Some("text/html")
        }

        "return charset utf-8" in new Test {
          override val vatServiceResult: Future[Left[BadRequestError, Nothing]] = Future.successful(Left(BadRequestError("", "")))

          override val authResult: Future[Enrolments] = Future.successful(Enrolments(Set(Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VATRegNum", "123")), ""))))
          private val result = target.details()(fakeRequest)
          charset(result) shouldBe Some("utf-8")
        }
      }

    }

    "the user is not logged in" should {

      "return 303" in new Test {
        override val runMock: Boolean = false
        override val vatServiceResult: Future[Right[Nothing, VatDetailsModel]] = Future.successful {
          Right(
            VatDetailsModel(
              Some(
                Obligation(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-03-30"), LocalDate.parse("2017-07-30"), "O", None, "#004")
              ), None
            )
          )
        }

        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())
        val result: Future[Result] = target.details()(fakeRequest)
        status(result) shouldBe Status.SEE_OTHER
      }

      "redirect the user to the session timeout page" in new Test {
        override val runMock: Boolean = false
        override val vatServiceResult: Future[Right[Nothing, VatDetailsModel]] = Future.successful {
          Right(
            VatDetailsModel(
              Some(
                Obligation(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-03-30"), LocalDate.parse("2017-07-30"), "O", None, "#004")
              ), None
            )
          )
        }

        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())
        val result: Future[Result] = target.details()(fakeRequest)
        redirectLocation(result) shouldBe Some(routes.ErrorsController.sessionTimeout().url)
      }
    }
  }
}
