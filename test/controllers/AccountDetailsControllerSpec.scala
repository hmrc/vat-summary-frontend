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

import models.User
import models.viewModels.AccountDetailsModel
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AccountDetailsControllerSpec extends ControllerBaseSpec {

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

    def target: AccountDetailsController = {
      setup()
      new AccountDetailsController(messages, mockEnrolmentsAuthService, mockAppConfig)
    }
  }

  private trait HandleAccountDetailsModelTest {
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
    val testUser: User = User("999999999")
    implicit val hc: HeaderCarrier = HeaderCarrier()

    def target: AccountDetailsController = {
      new AccountDetailsController(messages, mockEnrolmentsAuthService, mockAppConfig)
    }
  }

  "Calling the accountDetails action" when {

    "the user is logged in" should {

      "return 200" in new AccountDetailsTest {
        private val result = target.accountDetails()(fakeRequest)
        status(result) shouldBe Status.OK
      }

      "return HTML" in new AccountDetailsTest {
        private val result = target.accountDetails()(fakeRequest)
        contentType(result) shouldBe Some("text/html")
      }

      "return charset utf-8" in new AccountDetailsTest {
        private val result = target.accountDetails()(fakeRequest)
        charset(result) shouldBe Some("utf-8")
      }
    }

    "the user is not logged in" should {

      "return 401 (Unauthorised)" in new AccountDetailsTest {
        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())
        val result: Future[Result] = target.accountDetails()(fakeRequest)
        status(result) shouldBe Status.UNAUTHORIZED
      }
    }

    "the user is not authenticated" should {

      "return 403 (Forbidden)" in new AccountDetailsTest {
        override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())
        val result: Future[Result] = target.accountDetails()(fakeRequest)
        status(result) shouldBe Status.FORBIDDEN
      }
    }
  }

  "Calling the handleAccountDetailsModel function" when {

    "the AccountDetailsService retrieves a valid AccountDetailsModel" should {

      "return the AccountDetailsModel" in new HandleAccountDetailsModelTest {
        val exampleAccountDetailsModel: AccountDetailsModel = {
          AccountDetailsModel(
            "Betty Jones",
            "Bedrock Quarry, Bedrock, Graveldon",
            "GV2 4BB",
            "13 Pebble lane, Bedrock, Graveldon",
            "GV13 4BJ",
            "01632 982028",
            "07700 900018",
            "01632 960026",
            "bettylucknexttime@gmail.com"
          )
        }

        private val result = await(target.handleAccountDetailsModel(testUser))
        result shouldBe exampleAccountDetailsModel
      }
    }
  }
}














