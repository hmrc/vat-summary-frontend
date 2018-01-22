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

import models.User
import models.viewModels.OpenPaymentsModel
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class OpenPaymentsControllerSpec extends ControllerBaseSpec {

  private trait AccountDetailsTest {
    val authResult: Future[_] =
      Future.successful(Enrolments(Set(
        Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VATRegNo", "123456789")), "")
      )))

    val mockAuthConnector: AuthConnector = mock[AuthConnector]

    def setup(): Any = {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(authResult)
    }

    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)

    def target: OpenPaymentsController = {
      setup()
      new OpenPaymentsController(messages, mockEnrolmentsAuthService, mockAppConfig)
    }
  }

  private trait handleOpenPaymentsModelTest {
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
    val testUser: User = User("999999999")
    implicit val hc: HeaderCarrier = HeaderCarrier()

    def target: OpenPaymentsController = {
      new OpenPaymentsController(messages, mockEnrolmentsAuthService, mockAppConfig)
    }
  }

  "Calling the openPayments action" when {

    "the user is logged in" should {

      "return 200" in new AccountDetailsTest {
        private val result = target.openPayments()(fakeRequest)
        status(result) shouldBe Status.OK
      }

      "return HTML" in new AccountDetailsTest {
        private val result = target.openPayments()(fakeRequest)
        contentType(result) shouldBe Some("text/html")
      }

      "return charset utf-8" in new AccountDetailsTest {
        private val result = target.openPayments()(fakeRequest)
        charset(result) shouldBe Some("utf-8")
      }
    }

    "the user is not logged in" should {

      "return 401 (Unauthorised)" in new AccountDetailsTest {
        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())
        val result: Future[Result] = target.openPayments()(fakeRequest)
        status(result) shouldBe Status.UNAUTHORIZED
      }
    }

    "the user is not authenticated" should {

      "return 403 (Forbidden)" in new AccountDetailsTest {
        override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())
        val result: Future[Result] = target.openPayments()(fakeRequest)
        status(result) shouldBe Status.FORBIDDEN
      }
    }
  }

  "Calling the handleOpenPaymentsModel function" when {

    "the OpenPaymentsService retrieves a valid OpenPaymentsModel" should {

      "return the OpenPaymentsModel" in new handleOpenPaymentsModelTest {
        val exampleOpenPaymentsModel: Seq[OpenPaymentsModel] = {
          Seq(
            OpenPaymentsModel(
              "Return",
              543.21,
              LocalDate.parse("2000-02-23"),
              LocalDate.parse("2000-04-12")),
            OpenPaymentsModel(
              "Return",
              123.45,
              LocalDate.parse("2000-01-10"),
              LocalDate.parse("2000-05-22"))
          )
        }

        private val result = await(target.handleOpenPaymentsModel(testUser))
        result shouldBe exampleOpenPaymentsModel
      }
    }
  }
}
