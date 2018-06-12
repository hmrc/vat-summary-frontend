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

import connectors.{FinancialDataConnector, VatObligationsConnector, VatSubscriptionConnector}
import controllers.ControllerBaseSpec
import models._
import models.errors.{BadRequestError, CustomerInformationError, NextPaymentError, ObligationsError}
import models.obligations.{Obligation, VatReturnObligation, VatReturnObligations}
import models.payments.{Payment, Payments}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.{ExecutionContext, Future}

class VatDetailsServiceSpec extends ControllerBaseSpec {

  private trait Test {
    val currentObligation: VatReturnObligation = VatReturnObligation(
      LocalDate.parse("2017-01-01"),
      LocalDate.parse("2017-03-30"),
      due = LocalDate.parse("2017-04-30"),
      "O",
      None,
      "#001"
    )

    val payment: Payment = Payment(
      start = LocalDate.parse("2017-11-22"),
      end = LocalDate.parse("2017-12-22"),
      due = LocalDate.parse("2017-12-26"),
      outstandingAmount = BigDecimal(1000.00),
      periodKey = "#003"
    )

    implicit val hc: HeaderCarrier = HeaderCarrier()
    val mockVatApiConnector: VatObligationsConnector = mock[VatObligationsConnector]
    val mockFinancialDataConnector: FinancialDataConnector = mock[FinancialDataConnector]
    val mockSubscriptionConnector: VatSubscriptionConnector = mock[VatSubscriptionConnector]
    val mockDateService: DateService = mock[DateService]
    (mockDateService.now: () => LocalDate).stubs().returns(LocalDate.parse("2018-05-01"))

    lazy val vatDetailsService = new VatDetailsService(mockVatApiConnector,
                                                       mockFinancialDataConnector,
                                                       mockSubscriptionConnector,
                                                       mockAppConfig,
                                                       mockDateService)
    lazy val accountDetailsService = new AccountDetailsService(mockSubscriptionConnector)
  }

  "Calling .retrieveNextDetail" when {

    "sequence contains one obligation" should {

      "return current obligation" in new Test {
        lazy val result: Option[VatReturnObligation] =
          vatDetailsService.getNextObligation(Seq(currentObligation), LocalDate.parse("2017-03-30"))

        result shouldBe Some(currentObligation)
      }
    }

    "sequence contains more than one obligation" should {

      val futureObligation: VatReturnObligation = VatReturnObligation(
        LocalDate.parse("2017-01-01"),
        LocalDate.parse("2017-03-30"),
        due = LocalDate.parse("2017-07-30"),
        "O",
        None,
        "#001"
      )

      "return the obligation which is due today" in new Test {
        val obligations = Seq(futureObligation, currentObligation)
        lazy val result: Option[VatReturnObligation] =
          vatDetailsService.getNextObligation(obligations, LocalDate.parse("2017-04-30"))

        result shouldBe Some(currentObligation)
      }

      "return the obligation which is due in the future" in new Test {
        val obligations = Seq(futureObligation, currentObligation)
        lazy val result: Option[VatReturnObligation] =
          vatDetailsService.getNextObligation(obligations, LocalDate.parse("2017-05-30"))

        result shouldBe Some(futureObligation)
      }

      "return the most recent overdue obligation" in new Test {
        val obligations = Seq(futureObligation, currentObligation)
        lazy val result: Option[VatReturnObligation] =
          vatDetailsService.getNextObligation(obligations, LocalDate.parse("2017-08-30"))

        result shouldBe Some(futureObligation)
      }
    }
  }

  "Calling .getNextReturn" when {

    "the connector returns some obligations" should {

      "return the most recent outstanding obligation" in new Test {

        (mockVatApiConnector.getVatReturnObligations(_: String, _: LocalDate, _: LocalDate, _: Obligation.Status.Value)
        (_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *, *, *)
          .returns(Future.successful(Right(VatReturnObligations(Seq(currentObligation)))))

        val result: ServiceResponse[Option[VatReturnObligations]] =
          await(vatDetailsService.getReturnObligations(User("1111"), LocalDate.parse("2018-01-01")))

        result shouldBe Right(Some(VatReturnObligations(Seq(currentObligation))))
      }
    }

    "the connector returns no obligations" should {

      "return nothing" in new Test {

        (mockVatApiConnector.getVatReturnObligations(_: String, _: LocalDate, _: LocalDate, _: Obligation.Status.Value)
        (_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *, *, *)
          .returns(Future.successful(Right(VatReturnObligations(Seq.empty))))

        val result: ServiceResponse[Option[VatReturnObligations]] =
          await(vatDetailsService.getReturnObligations(User("1111"), LocalDate.parse("2018-01-01")))

        result shouldBe Right(None)
      }

    }

    "the connector returns an HttpError" should {

      "return the error" in new Test {

        (mockVatApiConnector.getVatReturnObligations(_: String, _: LocalDate, _: LocalDate, _: Obligation.Status.Value)
        (_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *, *, *)
          .returns(Future.successful(Left(BadRequestError("TEST_FAIL", "this is a test"))))

        val result: ServiceResponse[Option[VatReturnObligations]] =
          await(vatDetailsService.getReturnObligations(User("1111"), LocalDate.parse("2018-01-01")))

        result shouldBe Left(ObligationsError)
      }

    }

    "the connector returns an Exception" should {

      "return a failed Future containing the exception" in new Test {

        (mockVatApiConnector.getVatReturnObligations(_: String, _: LocalDate, _: LocalDate, _: Obligation.Status.Value)
        (_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *, *, *, *)
          .returns(Future.failed(new RuntimeException("test")))

        intercept[RuntimeException](await(vatDetailsService.getReturnObligations(User("1111"), LocalDate.parse("2018-01-01"))))
      }
    }
  }

  "Calling .getNextPayment" when {

    "the connector returns some payments" should {

      "return the most recent outstanding payment" in new Test {

        (mockFinancialDataConnector.getOpenPayments(_: String)
        (_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(Right(Payments(Seq(payment)))))

        val result: ServiceResponse[Option[Payment]] =
          await(vatDetailsService.getNextPayment(User("1111"), LocalDate.parse("2018-01-01")))

        result shouldBe Right(Some(payment))
      }
    }

    "the connector returns no payments" should {

      "return nothing" in new Test {

        (mockFinancialDataConnector.getOpenPayments(_: String)
        (_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(Right(Payments(Seq.empty))))

        val result: ServiceResponse[Option[Payment]] =
          await(vatDetailsService.getNextPayment(User("1111"), LocalDate.parse("2018-01-01")))

        result shouldBe Right(None)
      }
    }

    "the connector returns an HttpError" should {

      "return the error" in new Test {

        (mockFinancialDataConnector.getOpenPayments(_: String)
        (_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(Left(BadRequestError("TEST_FAIL", "this is a test"))))

        val result: ServiceResponse[Option[Payment]] =
          await(vatDetailsService.getNextPayment(User("1111"), LocalDate.parse("2018-01-01")))

        result shouldBe Left(NextPaymentError)
      }
    }

    "the connector returns an Exception" should {

      "return a failed Future containing the exception" in new Test {

        (mockFinancialDataConnector.getOpenPayments(_: String)
        (_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.failed(new RuntimeException("test")))

        intercept[RuntimeException](await(vatDetailsService.getNextPayment(User("1111"), LocalDate.parse("2018-01-01"))))
      }
    }
  }

  "Calling .getEntityName" when {

    "the connector retrieves a trading name" should {

      "return the trading name" in new Test {
        val exampleCustomerInfo: CustomerInformation = CustomerInformation(
          Some("Cheapo Clothing Ltd"),
          Some("Betty"),
          Some("Jones"),
          Some("Cheapo Clothing"),
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
          Some("bettylucknexttime@gmail.com")
        )

        (mockSubscriptionConnector.getCustomerInfo(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(Right(exampleCustomerInfo)))

        lazy val result: ServiceResponse[Option[String]] = await(accountDetailsService.getEntityName("999999999"))

        result shouldBe Right(Some("Cheapo Clothing"))
      }
    }

    "the connector does not retrieve a trading name or organisation name" should {

      "return the first and last name" in new Test {
        val exampleCustomerInfo: CustomerInformation = CustomerInformation(
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
          Some("bettylucknexttime@gmail.com")
        )

        (mockSubscriptionConnector.getCustomerInfo(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(Right(exampleCustomerInfo)))

        val result: ServiceResponse[Option[String]] = await(accountDetailsService.getEntityName("999999999"))

        result shouldBe Right(Some("Betty Jones"))
      }
    }

    "the connector does not retrieve a trading name or a first and last name" should {

      "return the organisation name" in new Test {
        val exampleCustomerInfo: CustomerInformation = CustomerInformation(
          Some("Cheapo Clothing Ltd"),
          None,
          None,
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
          Some("bettylucknexttime@gmail.com")
        )

        (mockSubscriptionConnector.getCustomerInfo(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(Right(exampleCustomerInfo)))

        val result: ServiceResponse[Option[String]] = await(accountDetailsService.getEntityName("999999999"))

        result shouldBe Right(Some("Cheapo Clothing Ltd"))
      }
    }

    "the connector does not retrieve a trading name, organisation name, or individual names" should {

      "return None" in new Test {
        val exampleCustomerInfo: CustomerInformation = CustomerInformation(
          None,
          None,
          None,
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
          Some("bettylucknexttime@gmail.com")
        )

        (mockSubscriptionConnector.getCustomerInfo(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(Right(exampleCustomerInfo)))

        val result: ServiceResponse[Option[String]] = await(accountDetailsService.getEntityName("999999999"))

        result shouldBe Right(None)
      }
    }

    "the connector returns an error" should {

      "return None" in new Test {
        (mockSubscriptionConnector.getCustomerInfo(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(Left(BadRequestError("", ""))))

        val result: ServiceResponse[Option[String]] = await(accountDetailsService.getEntityName("999999999"))

        result shouldBe Left(CustomerInformationError)
      }
    }
  }
}
