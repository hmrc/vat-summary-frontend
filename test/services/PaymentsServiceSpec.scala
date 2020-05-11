/*
 * Copyright 2020 HM Revenue & Customs
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
import models.errors._
import models.payments._
import models.viewModels.PaymentsHistoryModel
import models.{DirectDebitDetailsModel, ServiceResponse}
import org.scalamock.matchers.Matchers
import org.scalamock.scalatest.MockFactory
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.{ExecutionContext, Future}

class PaymentsServiceSpec extends UnitSpec with MockFactory with Matchers with GuiceOneAppPerSuite {

  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

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

      "return a list of payments sorted by due date in descending order" in new Test {
        val payment1 = PaymentWithPeriod(
          ReturnDebitCharge,
          LocalDate.parse("2008-01-01"),
          LocalDate.parse("2009-01-01"),
          LocalDate.parse("2008-11-29"),
          BigDecimal("21.22"),
          "",
          ddCollectionInProgress = false
        )
        val payment2 = PaymentWithPeriod(
          ReturnDebitCharge,
          LocalDate.parse("2008-01-01"),
          LocalDate.parse("2009-01-01"),
          LocalDate.parse("2008-12-01"),
          BigDecimal("21.22"),
          "",
          ddCollectionInProgress = false
        )
        val payment3 = PaymentWithPeriod(
          ReturnDebitCharge,
          LocalDate.parse("2008-01-01"),
          LocalDate.parse("2009-01-01"),
          LocalDate.parse("2009-01-01"),
          BigDecimal("21.22"),
          "",
          ddCollectionInProgress = false
        )
        val payment4 = PaymentWithPeriod(
          ReturnDebitCharge,
          LocalDate.parse("2008-01-01"),
          LocalDate.parse("2009-01-01"),
          LocalDate.parse("2008-11-30"),
          BigDecimal("21.22"),
          "",
          ddCollectionInProgress = false
        )

        val payments = Payments(Seq(payment1, payment2, payment3, payment4))
        val sortedPayments = Payments(Seq(payment3, payment2, payment4, payment1))
        override val responseFromFinancialDataConnector = Right(payments)
        val paymentsResponse: ServiceResponse[Option[Payments]] = await(target.getOpenPayments("123456789"))

        paymentsResponse shouldBe Right(Some(sortedPayments))
      }
    }

    "the user has no payments outstanding" should {

      "return an empty list of payments" in new Test {
        val payments = Payments(Seq(PaymentWithPeriod(
          ReturnCreditCharge,
          LocalDate.parse("2008-12-06"),
          LocalDate.parse("2009-01-04"),
          LocalDate.parse("2008-12-06"),
          BigDecimal("-1000"),
          "",
          ddCollectionInProgress = false
        )))
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

    val amountInPence: Int = 123456
    val taxPeriodMonth: Int = 2
    val taxPeriodYear: Int = 2018
    val dueDate: String = "2018-08-08"

    val paymentDetails = PaymentDetailsModelWithPeriod("vat",
      "123456789",
      amountInPence,
      taxPeriodMonth,
      taxPeriodYear,
      "http://domain/path",
      "http://domain/return-path",
      ReturnDebitCharge,
      dueDate
    )

    "setting up the payments journey is successful" should {

      "return a redirect url" in new Test {
        val redirectUrl: String = "http://www.google.com"
        override val connectorResponse: HttpPostResult[String] = Right(redirectUrl)
        val expectedResult: ServiceResponse[String] = Right(redirectUrl)
        private val result: ServiceResponse[String] = await(target.setupPaymentsJourney(paymentDetails))

        result shouldBe expectedResult
      }
    }

    "setting up the payments journey is unsuccessful" should {

      "return an error" in new Test {
        override val connectorResponse: HttpPostResult[String] = Left(UnknownError)
        val expectedResult: ServiceResponse[String] = Left(PaymentSetupError)
        private val result: ServiceResponse[String] = await(target.setupPaymentsJourney(paymentDetails))

        result shouldBe expectedResult
      }
    }
  }

  "Calling the .getPaymentsHistory method" when {

    trait Test {
      val exampleAmount = 1000L
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

    val taxPeriodYear: Int = 2018

    "return a seq of payment history models sorted by clearing date in descending order" in new Test {

      val paymentModel1 = PaymentsHistoryModel(
        chargeType = ReturnCreditCharge,
        taxPeriodFrom = Some(LocalDate.parse("2018-01-01")),
        taxPeriodTo = Some(LocalDate.parse("2018-01-01")),
        amount = exampleAmount,
        clearedDate = Some(LocalDate.parse("2018-01-30"))
      )
      val paymentModel2 = PaymentsHistoryModel(
        chargeType = ReturnCreditCharge,
        taxPeriodFrom = Some(LocalDate.parse("2018-01-01")),
        taxPeriodTo = Some(LocalDate.parse("2018-01-01")),
        amount = exampleAmount,
        clearedDate = Some(LocalDate.parse("2018-02-28"))
      )
      val paymentModel3 = PaymentsHistoryModel(
        chargeType = ReturnCreditCharge,
        taxPeriodFrom = Some(LocalDate.parse("2018-01-01")),
        taxPeriodTo = Some(LocalDate.parse("2018-01-01")),
        amount = exampleAmount,
        clearedDate = Some(LocalDate.parse("2018-03-01"))
      )
      val paymentModel4 = PaymentsHistoryModel(
        chargeType = ReturnCreditCharge,
        taxPeriodFrom = Some(LocalDate.parse("2018-01-01")),
        taxPeriodTo = Some(LocalDate.parse("2018-01-01")),
        amount = exampleAmount,
        clearedDate = Some(LocalDate.parse("2018-01-31"))
      )

      val paymentSeq: HttpGetResult[Seq[PaymentsHistoryModel]] =
        Right(Seq(paymentModel1, paymentModel2, paymentModel3, paymentModel4))
      val sortedPayments: ServiceResponse[Seq[PaymentsHistoryModel]] =
        Right(Seq(paymentModel3, paymentModel2, paymentModel4, paymentModel1))
      override val connectorResponse: HttpGetResult[Seq[PaymentsHistoryModel]] = paymentSeq
      private val result: ServiceResponse[Seq[PaymentsHistoryModel]] =
        await(target.getPaymentsHistory("999999999", taxPeriodYear))

      result shouldBe sortedPayments
    }

    "return a http error" in new Test {
      override val connectorResponse: HttpGetResult[Seq[PaymentsHistoryModel]] = Left(BadRequestError("400", ""))
      private val result: ServiceResponse[Seq[PaymentsHistoryModel]] =
        await(target.getPaymentsHistory("999999999", taxPeriodYear))

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
        val redirectUrl: String = "http://www.google.com"
        override val connectorResponse: HttpPostResult[String] = Right(redirectUrl)
        val expectedResult: ServiceResponse[String] = Right(redirectUrl)
        private val result: ServiceResponse[String] = await(target.setupDirectDebitJourney(directDebitDetails))

        result shouldBe expectedResult
      }
    }

    "setting up the payments journey is unsuccessful" should {

      "return an error" in new Test {
        override val connectorResponse: HttpPostResult[String] = Left(UnknownError)
        val expectedResult: ServiceResponse[String] = Left(DirectDebitSetupError)
        private val result: ServiceResponse[String] = await(target.setupDirectDebitJourney(directDebitDetails))

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
      val responseFromFinancialDataConnector: HttpGetResult[Boolean]

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
        override val responseFromFinancialDataConnector = Right(true)
        val paymentsResponse: ServiceResponse[Boolean] = await(target.getDirectDebitStatus("123456789"))

        paymentsResponse shouldBe Right(true)
      }
    }

    "the connector call fails" should {

      "return None" in new Test {
        override val responseFromFinancialDataConnector = Left(ServerSideError(Status.GATEWAY_TIMEOUT.toString, ""))
        val paymentsResponse: ServiceResponse[Boolean] = await(target.getDirectDebitStatus("123456789"))

        paymentsResponse shouldBe Left(DirectDebitStatusError)
      }
    }
  }
}
