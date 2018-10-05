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

import connectors.VatSubscriptionConnector
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.errors.ServerSideError
import models.viewModels.AccountDetailsModel
import models.{Address, CustomerInformation, User}
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import services.{AccountDetailsService, EnrolmentsAuthService}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class AccountDetailsControllerSpec extends ControllerBaseSpec {

  val exampleCunstomerInfo = CustomerInformation(
    None,
    Some("Betty"),
    Some("Jones"),
    None,
    Address("Bedrock Quarry",
      "Bedrock",
      Some("Graveldon"),
      Some("Graveldon"),
      Some("GV2 4BB")
    ),
    Some("01632 982028"),
    Some("07700 900018"),
    Some("bettylucknexttime@gmail.com"),
    Address("13 Pebble Lane",
      "Bedrock",
      Some("Graveldon"),
      Some("Graveldon"),
      Some("GV13 4BJ")
    ),
    Some("01632 960026"),
    Some("07700 900018"),
    Some("bettylucknexttime@gmail.com"),
    isPartialMigration = true
  )

  private trait AccountDetailsTest {
    val runMocks = true
    val authResult: Future[_] =
      Future.successful(Enrolments(Set(
        Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VRN", "123456789")), "")
      )))

    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockVatSubscriptionConnector: VatSubscriptionConnector= mock[VatSubscriptionConnector]

    def setup(): Any = {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(authResult)

      if(runMocks) {
        (mockVatSubscriptionConnector.getCustomerInfo(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Right(exampleCunstomerInfo))
      }
    }

    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
    val mockAccountDetailsService: AccountDetailsService = new AccountDetailsService(mockVatSubscriptionConnector)

    def target: AccountDetailsController = {
      setup()
      new AccountDetailsController(messages, mockEnrolmentsAuthService, mockAccountDetailsService, mockAppConfig)
    }
  }

  private trait HandleAccountDetailsModelTest {
    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockVatSubscriptionConnector: VatSubscriptionConnector= mock[VatSubscriptionConnector]
    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)
    val mockAccountDetailsService: AccountDetailsService = new AccountDetailsService(mockVatSubscriptionConnector)
    val testUser: User = User("999999999")
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val connectorReturn: HttpGetResult[CustomerInformation]

    def setup(): Unit = {
      (mockVatSubscriptionConnector.getCustomerInfo(_: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *)
        .returns(connectorReturn)
    }

    def target: AccountDetailsController = {
      setup()
      new AccountDetailsController(messages, mockEnrolmentsAuthService, mockAccountDetailsService, mockAppConfig)
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
        override val runMocks: Boolean = false
        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())
        val result: Future[Result] = target.accountDetails()(fakeRequest)
        status(result) shouldBe Status.UNAUTHORIZED
      }
    }

    "the user is not authenticated" should {

      "return 403 (Forbidden)" in new AccountDetailsTest {
        override val runMocks: Boolean = false
        override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())
        val result: Future[Result] = target.accountDetails()(fakeRequest)
        status(result) shouldBe Status.FORBIDDEN
      }
    }
  }

  "Calling the handleAccountDetailsModel function" when {

    "the AccountDetailsService retrieves a valid AccountDetailsModel" should {

      "return the AccountDetailsModel" in new HandleAccountDetailsModelTest {
        override val connectorReturn = Right(exampleCunstomerInfo)

        val exampleAccountDetailsModel: AccountDetailsModel = {
          AccountDetailsModel(
            "Betty Jones",
            Address(
              "13 Pebble Lane",
              "Bedrock",
              Some("Graveldon"),
              Some("Graveldon"),
              Some("GV13 4BJ")
            ),
            Address(
              "Bedrock Quarry",
              "Bedrock",
              Some("Graveldon"),
              Some("Graveldon"),
              Some("GV2 4BB")
            ),
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

    "the AccountDetailsService returns an error" should {

      "throw an exception" in new HandleAccountDetailsModelTest {
        override val connectorReturn = Left(ServerSideError("501", "Service not implemented."))

        intercept[Exception](await(target.handleAccountDetailsModel(testUser)))
      }
    }
  }
}
