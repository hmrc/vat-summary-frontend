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

import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import connectors.{FinancialDataConnector, VatApiConnector, VatSubscriptionConnector}
import controllers.ControllerBaseSpec
import models.errors.BadRequestError
import models.obligations.{Obligation, VatReturnObligation, VatReturnObligations}
import models.payments.{Payment, Payments}
import models.VatDetailsModel
import uk.gov.hmrc.http.HeaderCarrier
import models._

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
    val mockVatApiConnector: VatApiConnector = mock[VatApiConnector]
    val mockFinancialDataConnector: FinancialDataConnector = mock[FinancialDataConnector]
    val mockSubscriptionConnector: VatSubscriptionConnector = mock[VatSubscriptionConnector]
    lazy val vatDetailsService = new VatDetailsService(mockVatApiConnector, mockFinancialDataConnector, mockSubscriptionConnector)
    lazy val accountDetailsService = new AccountDetailsService(mockSubscriptionConnector)
  }

  "Calling .retrieveNextDetail" when {

    "sequence contains one obligation" should {

      "return current obligation" in new Test {
        val obligations = VatReturnObligations(Seq(currentObligation))
        lazy val result: Option[VatReturnObligation] =
          vatDetailsService.getNextObligation(obligations.obligations, LocalDate.parse("2017-03-30"))

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
        val obligations = VatReturnObligations(Seq(futureObligation, currentObligation))
        lazy val result: Option[VatReturnObligation] =
          vatDetailsService.getNextObligation(obligations.obligations, LocalDate.parse("2017-04-30"))

        result shouldBe Some(currentObligation)
      }

      "return the obligation which is due in the future" in new Test {
        val obligations = VatReturnObligations(Seq(futureObligation, currentObligation))
        lazy val result: Option[VatReturnObligation] =
          vatDetailsService.getNextObligation(obligations.obligations, LocalDate.parse("2017-05-30"))

        result shouldBe Some(futureObligation)
      }

      "return the most recent overdue obligation" in new Test {
        val obligations = VatReturnObligations(Seq(futureObligation, currentObligation))
        lazy val result: Option[VatReturnObligation] =
          vatDetailsService.getNextObligation(obligations.obligations, LocalDate.parse("2017-08-30"))

        result shouldBe Some(futureObligation)
      }
    }
  }

  "Calling .getVatDetails" when {

    "the connector returns some obligations and payments" should {

      "return the most recent outstanding obligation" in new Test {

        (mockVatApiConnector.getVatReturnObligations(_:String, _:LocalDate, _:LocalDate, _:Obligation.Status.Value)
        (_: HeaderCarrier, _: ExecutionContext))
          .expects(*,*,*,*,*,*)
          .returns(Future.successful(Right(VatReturnObligations(Seq(currentObligation)))))

        (mockFinancialDataConnector.getOpenPayments(_:String)
        (_:HeaderCarrier, _:ExecutionContext))
          .expects(*,*,*)
          .returns(Future.successful(Right(Payments(Seq(payment)))))

        val result: HttpGetResult[VatDetailsModel] = await(vatDetailsService.getVatDetails(User("1111")))

        result shouldBe Right(VatDetailsModel(Some(payment), Some(currentObligation)))
      }

    }

    "the connector returns no obligations or payments" should {

      "return nothing" in new Test {

        (mockVatApiConnector.getVatReturnObligations(_:String, _:LocalDate, _:LocalDate, _:Obligation.Status.Value)
        (_: HeaderCarrier, _: ExecutionContext))
          .expects(*,*,*,*,*,*)
          .returns(Future.successful(Right(VatReturnObligations(Seq.empty))))


        (mockFinancialDataConnector.getOpenPayments(_:String)
        (_:HeaderCarrier, _:ExecutionContext))
          .expects(*,*,*)
          .returns(Future.successful(Right(Payments(Seq.empty))))

        val result: HttpGetResult[VatDetailsModel] = await(vatDetailsService.getVatDetails(User("1111")))

        result shouldBe Right(VatDetailsModel(None, None))
      }

    }

    "the connector returns an HttpError" should {

      "return a Future containing the error" in new Test {
        (mockVatApiConnector.getVatReturnObligations(_:String, _:LocalDate, _:LocalDate, _:Obligation.Status.Value)
        (_: HeaderCarrier, _: ExecutionContext))
          .expects(*,*,*,*,*,*)
          .returns(Future.successful(Left(BadRequestError("TEST_FAIL", "this is a test"))))

        val result: HttpGetResult[VatDetailsModel] = await(vatDetailsService.getVatDetails(User("1111")))

        result shouldBe Left(BadRequestError("TEST_FAIL", "this is a test"))
      }

    }

    "the connector returns an Exception" should {

      "return a failed Future containing the exception" in new Test {
        val expected = new RuntimeException("test")
        (mockVatApiConnector.getVatReturnObligations(_:String, _:LocalDate, _:LocalDate, _:Obligation.Status.Value)
        (_: HeaderCarrier, _: ExecutionContext))
          .expects(*,*,*,*,*,*)
          .returns(Future.failed(expected))

        intercept[RuntimeException](await(vatDetailsService.getVatDetails(User("1111"))))
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

        lazy val result: Option[String] = await(accountDetailsService.getEntityName("999999999"))

        result shouldBe Some("Cheapo Clothing")
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

        val result: Option[String] = await(accountDetailsService.getEntityName("999999999"))

        result shouldBe Some("Betty Jones")
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

        val result: Option[String] = await(accountDetailsService.getEntityName("999999999"))

        result shouldBe Some("Cheapo Clothing Ltd")
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

        val result: Option[String] = await(accountDetailsService.getEntityName("999999999"))

        result shouldBe None
      }
    }

    "the connector returns an error" should {

      "return None" in new Test {
        (mockSubscriptionConnector.getCustomerInfo(_: String)(_: HeaderCarrier, _: ExecutionContext))
          .expects(*, *, *)
          .returns(Future.successful(Left(BadRequestError("", ""))))

        val result: Option[String] = await(accountDetailsService.getEntityName("999999999"))

        result shouldBe None
      }
    }
  }
}
