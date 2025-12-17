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
import connectors.httpParsers.ResponseHttpParsers.HttpResult
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
  val exampleAmount = 1000L

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
        lazy val connectorResponse: HttpResult[String] = Right(redirectUrl)
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
        lazy val connectorResponse: HttpResult[String] = Left(UnknownError)
        (mockPaymentsConnector.setupJourney(_: PaymentDetailsModel)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(connectorResponse))
        val expectedResult: ServiceResponse[String] = Left(PaymentSetupError)
        val result: ServiceResponse[String] = await(paymentsService.setupPaymentsJourney(paymentDetails))

        result shouldBe expectedResult
      }
    }
  }

  "The .getPaymentsHistory method" should {

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

    val currentDate = LocalDate.parse("2018-05-01")

    "return a seq of payment history models sorted by clearing date in descending order" in {

      val unsortedPayments: HttpResult[Seq[PaymentsHistoryModel]] =
        Right(Seq(paymentModel1, paymentModel2, paymentModel3, paymentModel4))
      val sortedPayments: ServiceResponse[Seq[PaymentsHistoryModel]] =
        Right(Seq(paymentModel3, paymentModel2, paymentModel4, paymentModel1))

      val result: ServiceResponse[Seq[PaymentsHistoryModel]] = {
        (mockFinancialDataConnector.getVatLiabilities(_: String, _: LocalDate, _: LocalDate)
                                                     (_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *, *)
          .returns(Future.successful(unsortedPayments))

        await(paymentsService.getPaymentsHistory(
          "999999999", currentDate, Some(LocalDate.parse("2018-01-01")))
        )
      }

      result shouldBe sortedPayments
    }

    "call the API with the migration date as the 'from' date" when {

      "the migration date is within the last 2 years" in {

        val migrationDate = LocalDate.parse("2018-01-01")
        val result: ServiceResponse[Seq[PaymentsHistoryModel]] = {
          (mockFinancialDataConnector.getVatLiabilities(_: String, _: LocalDate, _: LocalDate)
                                                       (_: HeaderCarrier, _: ExecutionContext))
            .expects(*, migrationDate, currentDate, *, *)
            .returns(Future.successful(Right(Seq(paymentModel1))))

          await(paymentsService.getPaymentsHistory("999999999", currentDate, Some(migrationDate)))
        }

        result shouldBe Right(Seq(paymentModel1))
      }
    }

    "call the API with the date 2 years ago as the 'from' date" when {

      "the migration date is older than 2 years" in {

        val migrationDate = LocalDate.parse("2015-01-01")
        val result: ServiceResponse[Seq[PaymentsHistoryModel]] = {
          (mockFinancialDataConnector.getVatLiabilities(_: String, _: LocalDate, _: LocalDate)
                                                       (_: HeaderCarrier, _: ExecutionContext))
            .expects(*, currentDate.minusYears(2), currentDate, *, *)
            .returns(Future.successful(Right(Seq(paymentModel1))))

          await(paymentsService.getPaymentsHistory("999999999", currentDate, Some(migrationDate)))
        }

        result shouldBe Right(Seq(paymentModel1))
      }

      "the migration date is not provided" in {

        val result: ServiceResponse[Seq[PaymentsHistoryModel]] = {
          (mockFinancialDataConnector.getVatLiabilities(_: String, _: LocalDate, _: LocalDate)
                                                       (_: HeaderCarrier, _: ExecutionContext))
            .expects(*, currentDate.minusYears(2), currentDate, *, *)
            .returns(Future.successful(Right(Seq(paymentModel1))))

          await(paymentsService.getPaymentsHistory("999999999", currentDate, None))
        }

        result shouldBe Right(Seq(paymentModel1))
      }
    }

    "return an error when the connector returns an error" in {
      lazy val errorResponse: HttpResult[Seq[PaymentsHistoryModel]] = Left(BadRequestError("400", ""))
      val result: ServiceResponse[Seq[PaymentsHistoryModel]] = {
        (mockFinancialDataConnector.getVatLiabilities(_: String, _: LocalDate, _: LocalDate)
                                                     (_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *, *)
          .returns(Future.successful(errorResponse))
        await(paymentsService.getPaymentsHistory("999999999", LocalDate.parse("2008-01-01"), None))
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

  "Calling the .getLiabilitiesWithDueDate function" when {

    val fromDate = LocalDate.parse("2018-01-01")
    val toDate = LocalDate.parse("2018-02-01")
    val paymentWithDueDate = models.viewModels.PaymentHistoryWithDueDate(
      chargeType = ReturnDebitCharge,
      dueDate = toDate,
      clearedDate = Some(toDate)
    )

    "the connector call returns data" should {
      "return the rows" in {
        (mockFinancialDataConnector.getVatLiabilitiesWithDueDate(_: String, _: LocalDate, _: LocalDate)
          (_: HeaderCarrier, _: ExecutionContext))
          .expects(*, fromDate, toDate, *, *)
          .returns(Future.successful(Right(Seq(paymentWithDueDate))))

        await(paymentsService.getLiabilitiesWithDueDate("123456789", toDate, Some(fromDate))) shouldBe Right(Seq(paymentWithDueDate))
      }
    }

    "the connector call fails" should {
      "return VatLiabilitiesError" in {
        (mockFinancialDataConnector.getVatLiabilitiesWithDueDate(_: String, _: LocalDate, _: LocalDate)
          (_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *, *)
          .returns(Future.successful(Left(ServerSideError("500", ""))))

        await(paymentsService.getLiabilitiesWithDueDate("123456789", toDate, None)) shouldBe Left(VatLiabilitiesError)
      }
    }
  }

  "Calling the .getPaymentsForPeriod function" when {

    val fromDate = LocalDate.parse("2018-01-01")
    val toDate = LocalDate.parse("2018-02-01")
    val payments = PaymentsWithOptionalOutstanding(Seq.empty)

    "the connector call returns data" should {
      "return the payments" in {
        (mockFinancialDataConnector.getPaymentsForPeriod(_: String, _: LocalDate, _: LocalDate)
          (_: HeaderCarrier, _: ExecutionContext))
          .expects(*, fromDate, toDate, *, *)
          .returns(Future.successful(Right(payments)))

        await(paymentsService.getPaymentsForPeriod("123456789", fromDate, toDate)) shouldBe Right(payments)
      }
    }

    "the connector call fails" should {
      "return PaymentsError" in {
        (mockFinancialDataConnector.getPaymentsForPeriod(_: String, _: LocalDate, _: LocalDate)
          (_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *, *)
          .returns(Future.successful(Left(ServerSideError("500", ""))))

        await(paymentsService.getPaymentsForPeriod("123456789", fromDate, toDate)) shouldBe Left(PaymentsError)
      }
    }
  }
}
