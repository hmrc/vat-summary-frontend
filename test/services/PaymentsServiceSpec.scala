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
import connectors.{DirectDebitConnector, FinancialDataConnector, PaymentsConnector}
import models.{DirectDebitDetailsModel, DirectDebitStatus, ServiceResponse, User}
import models.errors._
import models.payments.{Payment, PaymentDetailsModel, Payments}
import models.viewModels.PaymentsHistoryModel
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
      val mockDirectDebitConnector: DirectDebitConnector = mock[DirectDebitConnector]
      val responseFromFinancialDataConnector: HttpGetResult[Payments]

      def setup(): Any = {
        (mockFinancialDataConnector.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(responseFromFinancialDataConnector))
      }

      def target: PaymentsService = {
        setup()
        new PaymentsService(mockFinancialDataConnector, mockPaymentsConnector, mockDirectDebitConnector)
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
        val paymentsResponse: ServiceResponse[Option[Payments]] = await(target.getOpenPayments("123456789"))

        paymentsResponse shouldBe Right(Some(payments))
      }
    }

    "the user has no payments outstanding" should {

      "return an empty list of payments" in new Test {
        val payments = Payments(Seq.empty)
        override val responseFromFinancialDataConnector = Right(payments)
        val paymentsResponse: ServiceResponse[Option[Payments]] = await(target.getOpenPayments("123456789"))

        paymentsResponse shouldBe Right(None)
      }
    }

    "the connector call fails" should {

      "return None" in new Test {
        override val responseFromFinancialDataConnector = Left(ServerSideError(Status.GATEWAY_TIMEOUT.toString, ""))
        val paymentsResponse: ServiceResponse[Option[Payments]] = await(target.getOpenPayments("123456789"))

        paymentsResponse shouldBe Left(PaymentsError)
      }
    }
  }

  "Calling the .setupPaymentJourney method" when {

    trait Test {
      implicit val hc: HeaderCarrier = HeaderCarrier()
      val mockFinancialDataConnector: FinancialDataConnector = mock[FinancialDataConnector]
      val mockPaymentsConnector: PaymentsConnector = mock[PaymentsConnector]
      val mockDirectDebitConnector: DirectDebitConnector = mock[DirectDebitConnector]
      val connectorResponse: HttpPostResult[String]

      def setup(): Unit = {
        (mockPaymentsConnector.setupJourney(_: PaymentDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(connectorResponse)
      }

      def target: PaymentsService = {
        setup()
        new PaymentsService(mockFinancialDataConnector, mockPaymentsConnector, mockDirectDebitConnector)
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
      "http://domain/path",
      "http://domain/return-path"
    )

    "setting up the payments journey is successful" should {

      "return a redirect url" in new Test {
        val redirectUrl = "http://www.google.com"
        override val connectorResponse: HttpPostResult[String] = Right(redirectUrl)
        val expectedResult: ServiceResponse[String] = Right(redirectUrl)
        private val result = await(target.setupPaymentsJourney(paymentDetails))

        result shouldBe expectedResult
      }
    }

    "setting up the payments journey is unsuccessful" should {

      "return an error" in new Test {
        override val connectorResponse: HttpPostResult[String] = Left(UnknownError)
        val expectedResult: ServiceResponse[String] = Left(PaymentSetupError)
        private val result = await(target.setupPaymentsJourney(paymentDetails))

        result shouldBe expectedResult
      }
    }
  }

  "Calling the .getPaymentsHistory method" when {

    trait Test {
      implicit val hc: HeaderCarrier = HeaderCarrier()
      val mockFinancialDataConnector: FinancialDataConnector = mock[FinancialDataConnector]
      val mockPaymentsConnector: PaymentsConnector = mock[PaymentsConnector]
      val mockDirectDebitConnector: DirectDebitConnector = mock[DirectDebitConnector]
      val connectorResponse: HttpGetResult[Seq[PaymentsHistoryModel]]

      def setup(): Unit = {
        (mockFinancialDataConnector.getVatLiabilities(_: String, _: LocalDate, _: LocalDate)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *, *)
          .returns(connectorResponse)
      }

      def target: PaymentsService = {
        setup()
        new PaymentsService(mockFinancialDataConnector, mockPaymentsConnector, mockDirectDebitConnector)
      }
    }

    val taxPeriodYear = 2018

    "return a seq of payment history models" in new Test {
      val paymentSeq = Right(Seq(
        PaymentsHistoryModel(
          taxPeriodFrom = LocalDate.parse("2018-01-01"),
          taxPeriodTo = LocalDate.parse("2018-01-26"),
          amount = 10000,
          clearedDate = LocalDate.parse("2018-01-13")
        )
      ))

      override val connectorResponse: HttpGetResult[Seq[PaymentsHistoryModel]] = paymentSeq
      private val result = await(target.getPaymentsHistory(User("999999999"), taxPeriodYear))

      result shouldBe paymentSeq
    }

    "return a http error" in new Test {
      override val connectorResponse: HttpGetResult[Seq[PaymentsHistoryModel]] = Left(BadRequestError("400", ""))
      private val result = await(target.getPaymentsHistory(User("999999999"), taxPeriodYear))

      result shouldBe Left(VatLiabilitiesError)
    }
  }

  "Calling the .setupDirectDebitJourney method" when {


    trait Test {
      implicit val hc: HeaderCarrier = HeaderCarrier()
      val mockFinancialDataConnector: FinancialDataConnector = mock[FinancialDataConnector]
      val mockPaymentsConnector: PaymentsConnector = mock[PaymentsConnector]
      val mockDirectDebitConnector: DirectDebitConnector = mock[DirectDebitConnector]
      val connectorResponse: HttpPostResult[String]

      def setup(): Unit = {
        (mockDirectDebitConnector.setupJourney(_: DirectDebitDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(connectorResponse)
      }

      def target: PaymentsService = {
        setup()
        new PaymentsService(mockFinancialDataConnector, mockPaymentsConnector, mockDirectDebitConnector)
      }
    }

    val directDebitDetails: DirectDebitDetailsModel = DirectDebitDetailsModel(
      "111111111",
      "VRN",
      "http://domain/path",
      "http://domain/return-path"
    )

    "setting up the direct debit journey is successful" should {

      "return a redirect url" in new Test {
        val redirectUrl = "http://www.google.com"
        override val connectorResponse: HttpPostResult[String] = Right(redirectUrl)
        val expectedResult: ServiceResponse[String] = Right(redirectUrl)
        private val result = await(target.setupDirectDebitJourney(directDebitDetails))

        result shouldBe expectedResult
      }
    }

    "setting up the payments journey is unsuccessful" should {

      "return an error" in new Test {
        override val connectorResponse: HttpPostResult[String] = Left(UnknownError)
        val expectedResult: ServiceResponse[String] = Left(DirectDebitSetupError)
        private val result = await(target.setupDirectDebitJourney(directDebitDetails))

        result shouldBe expectedResult
      }
    }
  }

  "Calling the .getDirectDebitStatus function" when {

    trait Test {
      implicit val hc: HeaderCarrier = HeaderCarrier()
      val mockFinancialDataConnector: FinancialDataConnector = mock[FinancialDataConnector]
      val mockPaymentsConnector: PaymentsConnector = mock[PaymentsConnector]
      val mockDirectDebitConnector: DirectDebitConnector = mock[DirectDebitConnector]
      val responseFromFinancialDataConnector: HttpGetResult[DirectDebitStatus]

      def setup(): Any = {
        (mockFinancialDataConnector.getDirectDebitStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(responseFromFinancialDataConnector))
      }

      def target: PaymentsService = {
        setup()
        new PaymentsService(mockFinancialDataConnector, mockPaymentsConnector, mockDirectDebitConnector)
      }
    }

    "the user has a direct debit setup" should {

      "return a DirectDebitStatus with true" in new Test {
        val directDebitStatus = DirectDebitStatus(true)
        override val responseFromFinancialDataConnector = Right(directDebitStatus)
        val paymentsResponse: ServiceResponse[DirectDebitStatus] = await(target.getDirectDebitStatus("123456789"))

        paymentsResponse shouldBe Right(directDebitStatus)
      }
    }

    "the connector call fails" should {

      "return None" in new Test {
        override val responseFromFinancialDataConnector = Left(ServerSideError(Status.GATEWAY_TIMEOUT.toString, ""))
        val paymentsResponse: ServiceResponse[DirectDebitStatus] = await(target.getDirectDebitStatus("123456789"))

        paymentsResponse shouldBe Left(DirectDebitStatusError)
      }
    }
  }
}
