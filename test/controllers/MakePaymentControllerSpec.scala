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
import play.api.http.Status
import play.api.mvc.Result
import play.api.test.Helpers._
import services.{AccountDetailsService, EnrolmentsAuthService}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class MakePaymentControllerSpec extends ControllerBaseSpec {

  val testAmountInPence:Int = 10000
  val testMonth: Int = 2
  val testYear:Int = 2018

  val expectedPaymentSessionCookieJson =
    """
      |{"taxType":"vat",
      |"reference":"123456789",
      |"amountInPence":10000,
      |"extras":{"vatPeriod":{"month":2,"year":2018}},
      |"returnUrl":"payments-return-url"}""".stripMargin.replaceAll(System.lineSeparator, "")

  private trait MakePaymentDetailsTest {
    val authResult: Future[_] =
      Future.successful(Enrolments(Set(
        Enrolment("HMRC-MTD-VAT", Seq(EnrolmentIdentifier("VRN", "123456789")), "")
      )))

    val mockAuthConnector: AuthConnector = mock[AuthConnector]
    val mockVatSubscriptionConnector: VatSubscriptionConnector = mock[VatSubscriptionConnector]

    def setup(): Any = {
      (mockAuthConnector.authorise(_: Predicate, _: Retrieval[_])(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(authResult)
    }

    val mockEnrolmentsAuthService: EnrolmentsAuthService = new EnrolmentsAuthService(mockAuthConnector)

    def target: MakePaymentController = {
      setup()
      new MakePaymentController(messages, mockEnrolmentsAuthService, mockAppConfig)
    }
  }

  "Calling the makePayment action" when {

    "the user is logged in" should {
      "have valid cookie data stored in session and redirect to payments frontend if the posted data is valid" in new MakePaymentDetailsTest {
        lazy val result: Future[Result] = target.makePayment(testAmountInPence, testMonth, testYear)(fakeRequestWithSession)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some("payments-url")

        await(result).session.get("payment-data") shouldBe Some(expectedPaymentSessionCookieJson.toString)

      }

    }

    "the user is not logged in" should {
      "return 401 (Unauthorised)" in new MakePaymentDetailsTest {
        lazy val request = fakeRequestToPOSTWithSession(
          ("amountInPence", "10000"),
          ("taxPeriodMonth", "02"),
          ("taxPeriodYear", "2018"))
        lazy val result: Future[Result] = target.makePayment(testAmountInPence, testMonth, testYear)(request)

        override val authResult: Future[Nothing] = Future.failed(MissingBearerToken())

        status(result) shouldBe Status.UNAUTHORIZED
      }
    }

    "the user is not authenticated" should {
      "return 403 (Forbidden)" in new MakePaymentDetailsTest {
        lazy val request = fakeRequestToPOSTWithSession(
          ("amountInPence", "10000"),
          ("taxPeriodMonth", "02"),
          ("taxPeriodYear", "2018"))
        lazy val result: Future[Result] = target.makePayment(testAmountInPence, testMonth, testYear)(request)

        override val authResult: Future[Nothing] = Future.failed(InsufficientEnrolments())

        status(result) shouldBe Status.FORBIDDEN
      }
    }

  }
}

