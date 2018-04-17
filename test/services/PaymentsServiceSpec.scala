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

package services

import java.time.LocalDate

import connectors.httpParsers.ResponseHttpParsers.{HttpGetResult, HttpPostResult}
import connectors.{FinancialDataConnector, PaymentsConnector}
import models.errors.{ServerSideError, UnknownError}
import models.payments.{Payment, PaymentDetailsModel, Payments}
import org.scalamock.matchers.Matchers
import org.scalamock.scalatest.MockFactory
import play.api.http.Status
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class PaymentsServiceSpec extends UnitSpec with MockFactory with Matchers {

  "Calling the .getOpenPayments function" when {

    trait Test {

      implicit val hc: HeaderCarrier = HeaderCarrier()
      val mockFinancialDataConnector: FinancialDataConnector = mock[FinancialDataConnector]
      val mockPaymentsConnector: PaymentsConnector = mock[PaymentsConnector]
      val responseFromFinancialDataConnector: HttpGetResult[Payments]

      def setup(): Any = {
        (mockFinancialDataConnector.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(responseFromFinancialDataConnector))
      }

      def target: PaymentsService = {
        setup()
        new PaymentsService(mockFinancialDataConnector, mockPaymentsConnector)
      }
    }


    "the user has payments outstanding" should {

      "return a list of payments" in new Test {
        val payments = Payments(Seq(Payment(
          LocalDate.parse("2008-12-06"),
          LocalDate.parse("2009-01-04"),
          LocalDate.parse("2008-12-06"),
          BigDecimal("21.22"),
          "ABCD"
        )))
        override val responseFromFinancialDataConnector = Right(payments)
        val paymentsResponse: Option[Payments] = await(target.getOpenPayments("123456789"))

        paymentsResponse shouldBe Some(payments)
      }
    }

    "the user has no payments outstanding" should {

      "return an empty list of payments" in new Test {
        val payments = Payments(Seq.empty)
        override val responseFromFinancialDataConnector = Right(payments)
        val paymentsResponse: Option[Payments] = await(target.getOpenPayments("123456789"))

        paymentsResponse shouldBe Some(payments)
      }
    }

    "the connector call fails" should {

      val errorResponse: String =
        """
          | "code" -> "GATEWAY_TIMEOUT",
          | "message" -> "Gateway Timeout"
          | """.stripMargin

      "return None" in new Test {
        override val responseFromFinancialDataConnector = Left(ServerSideError(Status.GATEWAY_TIMEOUT.toString, errorResponse))
        val paymentsResponse: Option[Payments] = await(target.getOpenPayments("123456789"))

        paymentsResponse shouldBe None
      }
    }
  }

  "Calling the .setupJourney method" when {

    trait Test {

      implicit val hc: HeaderCarrier = HeaderCarrier()
      val mockFinancialDataConnector: FinancialDataConnector = mock[FinancialDataConnector]
      val mockPaymentsConnector: PaymentsConnector = mock[PaymentsConnector]

      def setup(): Any

      def target: PaymentsService = {
        setup()
        new PaymentsService(mockFinancialDataConnector, mockPaymentsConnector)
      }
    }

    val amountInPence = 123456
    val taxPeriodMonth = 2
    val taxPeriodYear = 2018

    val paymentDetails = PaymentDetailsModel("vat",
      "123456789",
      amountInPence,
      taxPeriodMonth,
      taxPeriodYear,
      "http://domain/path"
    )

    "the connector is successful" should {

      "return a redirect url" in new Test {

        val expectedRedirectUrl = "http://www.google.com"
        val expectedResult: HttpPostResult[String] = Right(expectedRedirectUrl)

        override def setup(): Any = {
          (mockPaymentsConnector.setupJourney(_: PaymentDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Right(expectedRedirectUrl))
        }


        val result: String = await(target.setupPaymentsJourney(paymentDetails))

        result shouldBe expectedRedirectUrl
      }
    }

    "the connector is unsuccessful" should {

      "throw an exception" in new Test {

        val expectedResult: HttpPostResult[String] = Left(UnknownError)

        override def setup(): Any = {
          (mockPaymentsConnector.setupJourney(_: PaymentDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(expectedResult)
        }

        the[Exception] thrownBy {
          await(target.setupPaymentsJourney(paymentDetails))
        } should have message "Received an unknown error."
      }
    }
  }
}
