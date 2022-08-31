/*
 * Copyright 2022 HM Revenue & Customs
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
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import connectors.{FinancialDataConnector, VatObligationsConnector, VatSubscriptionConnector}
import controllers.ControllerBaseSpec
import models.ServiceResponse
import models.errors.{BadRequestError, NextPaymentError, ObligationsError}
import models.obligations.{Obligation, VatReturnObligation, VatReturnObligations}
import models.payments._
import uk.gov.hmrc.http.HeaderCarrier
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import scala.concurrent.{ExecutionContext, Future}

class VatDetailsServiceSpec extends ControllerBaseSpec {
  
    val obligations: VatReturnObligations = VatReturnObligations(Seq(VatReturnObligation(
      periodFrom = LocalDate.parse("2017-01-01"),
      periodTo = LocalDate.parse("2017-03-30"),
      due = LocalDate.parse("2017-04-30"),
      status = "O",
      received = None,
      periodKey = "#001"
    )))

    val payments: Seq[Payment] = Seq(
      PaymentWithPeriod(
        ReturnDebitCharge,
        periodFrom = LocalDate.parse("2017-11-22"),
        periodTo = LocalDate.parse("2017-12-22"),
        due = LocalDate.parse("2017-12-26"),
        outstandingAmount = BigDecimal(1000.00),
        periodKey = Some("#003"),
        chargeReference = Some("XD002750002155"),
        ddCollectionInProgress = false,
        accruedInterestAmount = Some(BigDecimal(2))
      )
    )

  val POAPayments: Seq[Payment] = Seq(
      PaymentWithPeriod(
        PaymentOnAccount,
        periodFrom = LocalDate.parse("2017-11-22"),
        periodTo = LocalDate.parse("2017-12-22"),
        due = LocalDate.parse("2017-12-26"),
        outstandingAmount = BigDecimal(1000.00),
        periodKey = Some("#003"),
        chargeReference = Some("XD002750002155"),
        ddCollectionInProgress = false,
        accruedInterestAmount = Some(BigDecimal(2))
      ),
      PaymentWithPeriod(
        CentralAssessmentCharge,
        periodFrom = LocalDate.parse("2017-11-22"),
        periodTo = LocalDate.parse("2017-12-22"),
        due = LocalDate.parse("2017-12-26"),
        outstandingAmount = BigDecimal(0),
        periodKey = Some("#003"),
        chargeReference = Some("XD002750002155"),
        ddCollectionInProgress = false,
        accruedInterestAmount = Some(BigDecimal(2))
      )
    )

    implicit val hc: HeaderCarrier = HeaderCarrier()
    val mockObligationsConnector: VatObligationsConnector = mock[VatObligationsConnector]
    val mockFinancialDataConnector: FinancialDataConnector = mock[FinancialDataConnector]
    val mockSubscriptionConnector: VatSubscriptionConnector = mock[VatSubscriptionConnector]

  def callObligationsConnector(obligationResult: HttpGetResult[VatReturnObligations] = Right(obligations)): Any = {
    (mockObligationsConnector.getVatReturnObligations(_: String, _: Obligation.Status.Value, _: Option[LocalDate], _: Option[LocalDate])
    (_: HeaderCarrier, _: ExecutionContext))
      .expects(*, *, *, *, *, *)
      .returns(Future.successful(obligationResult))
  }
    def callFinancialDataConnector(paymentsResult: HttpGetResult[Payments] = Right(Payments(payments))): Any = {
      (mockFinancialDataConnector.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *)
        .returns(Future.successful(paymentsResult))
    }

    def vatDetailsService: VatDetailsService = {
      new VatDetailsService(
        mockObligationsConnector,
        mockFinancialDataConnector,
        mockAppConfig
      )
    }


  "Calling .getNextReturn" when {

    "the connector returns some obligations" should {

      "return the most recent outstanding obligation" in {
        callObligationsConnector(obligationResult = Right(obligations))
        val result: ServiceResponse[Option[VatReturnObligations]] =
          await(vatDetailsService.getReturnObligations("1111", LocalDate.parse("2018-01-01")))
        result shouldBe Right(Some(obligations))
      }
    }

    "the connector returns no obligations" should {

      "return nothing" in {
        callObligationsConnector(obligationResult = Right(VatReturnObligations(Seq.empty)))
        val result: ServiceResponse[Option[VatReturnObligations]] =
          await(vatDetailsService.getReturnObligations("1111", LocalDate.parse("2018-01-01")))
        result shouldBe Right(None)
      }
    }

    "the connector returns an HttpError" should {

      "return the error" in {
        callObligationsConnector(obligationResult = Left(BadRequestError("TEST_FAIL", "this is a test")))
        val result: ServiceResponse[Option[VatReturnObligations]] =
          await(vatDetailsService.getReturnObligations("1111", LocalDate.parse("2018-01-01")))
        result shouldBe Left(ObligationsError)
      }
    }
  }

  "Calling .getPaymentObligations" when {

    "the connector returns some outstanding payment obligations" should {

      "return the most recent outstanding payment obligation" in {
        callFinancialDataConnector(paymentsResult = Right(Payments(payments)))
        val result: ServiceResponse[Option[Payments]] =
          await(vatDetailsService.getPaymentObligations("1111"))
        result shouldBe Right(Some(Payments(payments)))
      }
    }

    "the connector filters out charge type POA and outstandingAmount = 0" should {

      "return the expected amount of outstanding payment" in {
        callFinancialDataConnector(paymentsResult = Right(Payments(payments ++ POAPayments)))
        val result: ServiceResponse[Option[Payments]] =
          await(vatDetailsService.getPaymentObligations("1111"))
        result shouldBe Right(Some(Payments(payments)))
      }
    }

    "the connector returns no outstanding payment obligations" should {

      "return nothing" in {
        callFinancialDataConnector(paymentsResult = Right(Payments(Seq.empty)))
        val result: ServiceResponse[Option[Payments]] = await(vatDetailsService.getPaymentObligations("1111"))
        result shouldBe Right(None)
      }
    }

    "the connector returns an HttpError" should {

      "return the error" in {
        callFinancialDataConnector(paymentsResult = Left(BadRequestError("TEST_FAIL", "this is a test")))
        val result: ServiceResponse[Option[Payments]] = await(vatDetailsService.getPaymentObligations("1111"))
        result shouldBe Left(NextPaymentError)
      }
    }
  }
}
