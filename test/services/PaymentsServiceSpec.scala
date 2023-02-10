/*
 * Copyright 2023 HM Revenue & Customs
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
import models.errors._
import models.payments._
import models.viewModels.PaymentsHistoryModel
import models.{DirectDebitStatus, ServiceResponse}
import org.scalatest.matchers.should.Matchers
import org.scalamock.scalatest.MockFactory
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import uk.gov.hmrc.http.HeaderCarrier
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import scala.concurrent.{ExecutionContext, Future}

class PaymentsServiceSpec extends AnyWordSpecLike with MockFactory with Matchers with GuiceOneAppPerSuite {

  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val mockFinancialDataConnector: FinancialDataConnector = mock[FinancialDataConnector]
  val mockPaymentsConnector: PaymentsConnector = mock[PaymentsConnector]
  lazy val paymentsService: PaymentsService = new PaymentsService(mockFinancialDataConnector, mockPaymentsConnector)
  implicit val hc: HeaderCarrier = HeaderCarrier()

  "Calling the .getOpenPayments function" when {

    "the user has payments outstanding" should {

      "return a list of payments sorted by due date in descending order" in {

        val payment1 = PaymentWithPeriod(
          ReturnDebitCharge,
          LocalDate.parse("2008-01-01"),
          LocalDate.parse("2009-01-01"),
          LocalDate.parse("2008-11-29"),
          BigDecimal("21.22"),
          Some(""),
          Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruingInterestAmount = Some(BigDecimal(2)),
          accruingPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP1"),
          10000,
          Some(200.00)
        )

        val payment2 = payment1.copy(due = LocalDate.parse("2008-12-01"))
        val payment3 = payment1.copy(due = LocalDate.parse("2009-01-01"))
        val payment4 = payment1.copy(due = LocalDate.parse("2008-11-30"))

        val payments = Payments(Seq(payment1, payment2, payment3, payment4))
        lazy val responseFromFinancialDataConnector = Right(payments)

        val sortedPayments = Payments(Seq(payment3, payment2, payment4, payment1))
        val paymentsResponse: ServiceResponse[Option[Payments]] = {
          (mockFinancialDataConnector.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(responseFromFinancialDataConnector))
          await(paymentsService.getOpenPayments("123456789"))
        }

        paymentsResponse shouldBe Right(Some(sortedPayments))
      }
    }

    "the user has no charges with positive outstanding amounts" should {

      "return an empty list of payments" in {
        val payments = Payments(Seq(PaymentWithPeriod(
          ReturnCreditCharge,
          LocalDate.parse("2008-12-06"),
          LocalDate.parse("2009-01-04"),
          LocalDate.parse("2008-12-06"),
          BigDecimal("-1000"),
          Some(""),
          Some("XD002750002155"),
          ddCollectionInProgress = false,
          accruingInterestAmount = Some(BigDecimal(2)),
          accruingPenaltyAmount = Some(BigDecimal(100.00)),
          penaltyType = Some("LPP1"),
          BigDecimal("10000"),
          None
        )))
        lazy val responseFromFinancialDataConnector = Right(payments)

        val paymentsResponse: ServiceResponse[Option[Payments]] = {
          (mockFinancialDataConnector.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(responseFromFinancialDataConnector))
          await(paymentsService.getOpenPayments("123456789"))
        }

        paymentsResponse shouldBe Right(None)
      }
    }

    "the user has an outstanding Payment On Account charge" should {

      "filter out the Payment On Account charge" in {
        val payment1 = PaymentWithPeriod(
          PaymentOnAccount,
          LocalDate.parse("2018-01-01"),
          LocalDate.parse("2018-02-02"),
          LocalDate.parse("2018-03-03"),
          BigDecimal("21.22"),
          None,
          None,
          ddCollectionInProgress = false,
          accruingInterestAmount = None,
          accruingPenaltyAmount = None,
          penaltyType = None,
          BigDecimal("10000"),
          None
        )

        val payments = Payments(Seq(payment1))
        lazy val responseFromFinancialDataConnector = Right(payments)

        val paymentsResponse: ServiceResponse[Option[Payments]] = {
          (mockFinancialDataConnector.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(responseFromFinancialDataConnector))
          await(paymentsService.getOpenPayments("123456789"))
        }

        paymentsResponse shouldBe Right(None)
      }
    }

    "the user has a charge returned that has no amount outstanding" should {

      "filter out the invalid charge" in {
        val payment1 = PaymentWithPeriod(
          ReturnDebitCharge,
          LocalDate.parse("2018-01-01"),
          LocalDate.parse("2018-02-02"),
          LocalDate.parse("2018-03-03"),
          BigDecimal("0"),
          None,
          None,
          ddCollectionInProgress = false,
          accruingInterestAmount = None,
          accruingPenaltyAmount = None,
          penaltyType = None,
          BigDecimal("10000"),
          None
        )

        val payments = Payments(Seq(payment1))
        lazy val responseFromFinancialDataConnector = Right(payments)

        val paymentsResponse: ServiceResponse[Option[Payments]] = {
          (mockFinancialDataConnector.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(responseFromFinancialDataConnector))
          await(paymentsService.getOpenPayments("123456789"))
        }

        paymentsResponse shouldBe Right(None)
      }
    }

    "the connector call fails" should {

      "return None" in {
        lazy val responseFromFinancialDataConnector = Left(ServerSideError(Status.GATEWAY_TIMEOUT.toString, ""))

        val paymentsResponse: ServiceResponse[Option[Payments]] = {
          (mockFinancialDataConnector.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(responseFromFinancialDataConnector))
          await(paymentsService.getOpenPayments("123456789"))
        }

        paymentsResponse shouldBe Left(PaymentsError)
      }
    }
  }

  "Calling the .setupPaymentJourney method" when {

    val amountInPence: Int = 123456
    val taxPeriodMonth: Int = 2
    val taxPeriodYear: Int = 2018
    val dueDate: String = "2018-08-08"
    val chargeReference: Option[String] = Some("XD002750002155")
    val vatPeriodEnding: String = "2018-08-08"

    val paymentDetails = PaymentDetailsModelWithPeriod("vat",
      "123456789",
      amountInPence,
      taxPeriodMonth,
      taxPeriodYear,
      vatPeriodEnding,
      "http://domain/path",
      "http://domain/return-path",
      ReturnDebitCharge,
      dueDate,
      chargeReference
    )

    "setting up the payments journey is successful" should {

      "return a redirect url" in {
        val redirectUrl: String = "http://www.google.com"
        lazy val connectorResponse: HttpPostResult[String] = Right(redirectUrl)
        (mockPaymentsConnector.setupJourney(_: PaymentDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(connectorResponse))
        val expectedResult: ServiceResponse[String] = Right(redirectUrl)
        val result: ServiceResponse[String] = await(paymentsService.setupPaymentsJourney(paymentDetails))

        result shouldBe expectedResult
      }
    }

    "setting up the payments journey is unsuccessful" should {

      "return an error" in {
        lazy val connectorResponse: HttpPostResult[String] = Left(UnknownError)
        (mockPaymentsConnector.setupJourney(_: PaymentDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(connectorResponse))
        val expectedResult: ServiceResponse[String] = Left(PaymentSetupError)
        val result: ServiceResponse[String] = await(paymentsService.setupPaymentsJourney(paymentDetails))

        result shouldBe expectedResult
      }
    }
  }

  "Calling the .getPaymentsHistory method" when {

    val exampleAmount = 1000L
    implicit val hc: HeaderCarrier = HeaderCarrier()

    val taxPeriodYear: Int = 2018

    "return a seq of payment history models sorted by clearing date in descending order" in {

      val paymentModel1 = PaymentsHistoryModel(
        clearingSAPDocument = Some("002828853334"),
        chargeType = ReturnCreditCharge,
        taxPeriodFrom = Some(LocalDate.parse("2018-01-01")),
        taxPeriodTo = Some(LocalDate.parse("2018-01-01")),
        amount = exampleAmount,
        clearedDate = Some(LocalDate.parse("2018-01-30"))
      )
      val paymentModel2 = PaymentsHistoryModel(
        clearingSAPDocument = Some("002828853335"),
        chargeType = ReturnCreditCharge,
        taxPeriodFrom = Some(LocalDate.parse("2018-01-01")),
        taxPeriodTo = Some(LocalDate.parse("2018-01-01")),
        amount = exampleAmount,
        clearedDate = Some(LocalDate.parse("2018-02-28"))
      )
      val paymentModel3 = PaymentsHistoryModel(
        clearingSAPDocument = Some("002828853336"),
        chargeType = ReturnCreditCharge,
        taxPeriodFrom = Some(LocalDate.parse("2018-01-01")),
        taxPeriodTo = Some(LocalDate.parse("2018-01-01")),
        amount = exampleAmount,
        clearedDate = Some(LocalDate.parse("2018-03-01"))
      )
      val paymentModel4 = PaymentsHistoryModel(
        clearingSAPDocument = Some("002828853337"),
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
      lazy val connectorResponse: HttpGetResult[Seq[PaymentsHistoryModel]] = paymentSeq

      val result: ServiceResponse[Seq[PaymentsHistoryModel]] = {
        (mockFinancialDataConnector.getVatLiabilities(_: String, _: LocalDate, _: LocalDate)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *, *)
          .returns(Future.successful(connectorResponse))
        await(paymentsService.getPaymentsHistory("999999999", taxPeriodYear))
      }

      result shouldBe sortedPayments
    }

    "return a http error" in {
      lazy val connectorResponse: HttpGetResult[Seq[PaymentsHistoryModel]] = Left(BadRequestError("400", ""))
      val result: ServiceResponse[Seq[PaymentsHistoryModel]] = {
        (mockFinancialDataConnector.getVatLiabilities(_: String, _: LocalDate, _: LocalDate)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *, *)
          .returns(Future.successful(connectorResponse))
        await(paymentsService.getPaymentsHistory("999999999", taxPeriodYear))
      }

      result shouldBe Left(VatLiabilitiesError)
    }
  }

  "Calling the .getDirectDebitStatus function" when {

    "the connector call returns a successful model" should {

      "return a DirectDebitStatus" in {
        lazy val responseFromFinancialDataConnector = Right(DirectDebitStatus(directDebitMandateFound = false, None))
        val paymentsResponse: ServiceResponse[DirectDebitStatus] = {
          (mockFinancialDataConnector.getDirectDebitStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(responseFromFinancialDataConnector))
          await(paymentsService.getDirectDebitStatus("123456789"))
        }

        paymentsResponse shouldBe Right(DirectDebitStatus(directDebitMandateFound = false, None))
      }
    }

    "the connector call fails" should {

      "return a DirectDebitStatusError" in {
        lazy val responseFromFinancialDataConnector = Left(ServerSideError(Status.GATEWAY_TIMEOUT.toString, ""))
        val paymentsResponse: ServiceResponse[DirectDebitStatus] = {
          (mockFinancialDataConnector.getDirectDebitStatus(_: String)(_: HeaderCarrier, _: ExecutionContext))
            .expects(*, *, *)
            .returns(Future.successful(responseFromFinancialDataConnector))
          await(paymentsService.getDirectDebitStatus("123456789"))
        }

        paymentsResponse shouldBe Left(DirectDebitStatusError)
      }
    }
  }
}
