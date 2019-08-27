/*
 * Copyright 2019 HM Revenue & Customs
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

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.{ExecutionContext, Future}

class VatDetailsServiceSpec extends ControllerBaseSpec {

  private trait Test {
    val obligations: VatReturnObligations = VatReturnObligations(Seq(VatReturnObligation(
      start = LocalDate.parse("2017-01-01"),
      end = LocalDate.parse("2017-03-30"),
      due = LocalDate.parse("2017-04-30"),
      status = "O",
      received = None,
      periodKey = "#001"
    )))
    lazy val obligationResult: HttpGetResult[VatReturnObligations] = Right(obligations)
    val obligationsCall: Boolean = false

    val payments: Payments = Payments(Seq(
      PaymentWithPeriod(
        ReturnDebitCharge,
        start = LocalDate.parse("2017-11-22"),
        end = LocalDate.parse("2017-12-22"),
        due = LocalDate.parse("2017-12-26"),
        outstandingAmount = BigDecimal(1000.00),
        periodKey = "#003"
      ),
      PaymentWithPeriod(
        ReturnDebitCharge,
        start = LocalDate.parse("2016-11-22"),
        end = LocalDate.parse("2016-12-22"),
        due = LocalDate.parse("2016-12-26"),
        outstandingAmount = BigDecimal(1000.00),
        periodKey = "#003"
      ),
      PaymentWithPeriod(
        ReturnDebitCharge,
        start = LocalDate.parse("2015-11-22"),
        end = LocalDate.parse("2015-12-22"),
        due = LocalDate.parse("2015-12-26"),
        outstandingAmount = BigDecimal(1000.00),
        periodKey = "#003"
      ),
      PaymentWithPeriod(
        ReturnDebitCharge,
        start = LocalDate.parse("2011-11-22"),
        end = LocalDate.parse("2011-12-22"),
        due = LocalDate.parse("2011-12-26"),
        outstandingAmount = BigDecimal(1000.00),
        periodKey = "#003"
      ),
      PaymentWithPeriod(
        ReturnDebitCharge,
        start = LocalDate.parse("2013-11-22"),
        end = LocalDate.parse("2013-12-22"),
        due = LocalDate.parse("2013-12-26"),
        outstandingAmount = BigDecimal(1000.00),
        periodKey = "#003"
      )))
    lazy val paymentsResult: HttpGetResult[Payments] = Right(payments)
    val paymentsCall: Boolean = false

    implicit val hc: HeaderCarrier = HeaderCarrier()
    val mockObligationsConnector: VatObligationsConnector = mock[VatObligationsConnector]
    val mockFinancialDataConnector: FinancialDataConnector = mock[FinancialDataConnector]
    val mockSubscriptionConnector: VatSubscriptionConnector = mock[VatSubscriptionConnector]

    def setup(obligationsCall: Boolean = false, paymentsCall: Boolean = false): Any = {
      if(obligationsCall) {
        (mockObligationsConnector.getVatReturnObligations(_: String, _: Obligation.Status.Value, _: Option[LocalDate], _: Option[LocalDate])
        (_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *, *, *)
          .returns(Future.successful(obligationResult))
      }
      if(paymentsCall) {
        (mockFinancialDataConnector.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(paymentsResult))
      }
    }

    def target: VatDetailsService = {
      setup(obligationsCall, paymentsCall)
      new VatDetailsService(
        mockObligationsConnector,
        mockFinancialDataConnector,
        mockAppConfig
      )
    }
  }

  "Calling .getNextReturn" when {

    "the connector returns some obligations" should {

      "return the most recent outstanding obligation" in new Test {
        override val obligationsCall = true
        val result: ServiceResponse[Option[VatReturnObligations]] =
          await(target.getReturnObligations("1111", LocalDate.parse("2018-01-01")))
        result shouldBe Right(Some(obligations))
      }
    }

    "the connector returns no obligations" should {

      "return nothing" in new Test {
        override val obligationsCall = true
        override val obligations = VatReturnObligations(Seq.empty)
        val result: ServiceResponse[Option[VatReturnObligations]] =
          await(target.getReturnObligations("1111", LocalDate.parse("2018-01-01")))
        result shouldBe Right(None)
      }
    }

    "the connector returns an HttpError" should {

      "return the error" in new Test {
        override val obligationsCall = true
        override lazy val obligationResult = Left(BadRequestError("TEST_FAIL", "this is a test"))
        val result: ServiceResponse[Option[VatReturnObligations]] =
          await(target.getReturnObligations("1111", LocalDate.parse("2018-01-01")))
        result shouldBe Left(ObligationsError)
      }
    }
  }

  "Calling .getPaymentObligations" when {

    "the connector returns some outstanding payment obligations" should {

      "return the most recent outstanding payment obligation" in new Test {
        override val paymentsCall = true
        val result: ServiceResponse[Option[Payments]] = await(target.getPaymentObligations("1111"))
        result shouldBe Right(Some(payments))
      }
    }

    "the connector returns no outstanding payment obligations" should {

      "return nothing" in new Test {
        override val paymentsCall = true
        override val payments = Payments(Seq.empty)
        val result: ServiceResponse[Option[Payments]] = await(target.getPaymentObligations("1111"))
        result shouldBe Right(None)
      }
    }

    "the connector returns an HttpError" should {

      "return the error" in new Test {
        override val paymentsCall = true
        override lazy val paymentsResult = Left(BadRequestError("TEST_FAIL", "this is a test"))
        val result: ServiceResponse[Option[Payments]] = await(target.getPaymentObligations("1111"))
        result shouldBe Left(NextPaymentError)
      }
    }
  }
}
