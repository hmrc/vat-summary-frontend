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

import connectors.httpParsers.CustomerInfoHttpParser.HttpGetResult
import connectors.{FinancialDataConnector, VatApiConnector}
import controllers.ControllerBaseSpec
import models.errors.BadRequestError
import models.obligations.{VatReturnObligation, VatReturnObligations}
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
      end = LocalDate.parse("2017-12-22"),
      due = LocalDate.parse("2017-12-26"),
      outstandingAmount = BigDecimal(1000.00),
      periodKey = "#003"
    )

    implicit val hc: HeaderCarrier = HeaderCarrier()
    val mockVatApiConnector: VatApiConnector = mock[VatApiConnector]
    val mockFinancialDataConnector: FinancialDataConnector = mock[FinancialDataConnector]
    lazy val service = new VatDetailsService(mockVatApiConnector, mockFinancialDataConnector)
  }

  "Calling .retrieveNextDetail" when {

    "sequence contains one obligation" should {

      "return current obligation" in new Test {
        val obligations = VatReturnObligations(Seq(currentObligation))
        lazy val result: Option[VatReturnObligation] =
          service.getNextObligation(obligations.obligations, LocalDate.parse("2017-03-30"))

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
          service.getNextObligation(obligations.obligations, LocalDate.parse("2017-04-30"))

        result shouldBe Some(currentObligation)
      }

      "return the obligation which is due in the future" in new Test {
        val obligations = VatReturnObligations(Seq(futureObligation, currentObligation))
        lazy val result: Option[VatReturnObligation] =
          service.getNextObligation(obligations.obligations, LocalDate.parse("2017-05-30"))

        result shouldBe Some(futureObligation)
      }

      "return the most recent overdue obligation" in new Test {
        val obligations = VatReturnObligations(Seq(futureObligation, currentObligation))
        lazy val result: Option[VatReturnObligation] =
          service.getNextObligation(obligations.obligations, LocalDate.parse("2017-08-30"))

        result shouldBe Some(futureObligation)
      }
    }
  }

  "Calling .getVatDetails" when {

    "the connector returns some obligations and payments" should {

      "return the most recent outstanding obligation" in new Test {

        (mockVatApiConnector.getVatReturnObligations(_:String, _:LocalDate, _:LocalDate, _:VatReturnObligation.Status.Value)
        (_: HeaderCarrier, _: ExecutionContext))
          .expects(*,*,*,*,*,*)
          .returns(Future.successful(Right(VatReturnObligations(Seq(currentObligation)))))

        (mockFinancialDataConnector.getPaymentsForVatReturns(_:String)
        (_:HeaderCarrier, _:ExecutionContext))
          .expects(*,*,*)
          .returns(Future.successful(Right(Payments(Seq(payment)))))

        val result: HttpGetResult[VatDetailsModel] = await(service.getVatDetails(User("1111")))

        result shouldBe Right(VatDetailsModel(Some(currentObligation), Some(payment)))
      }

    }

    "the connector returns no obligations or payments" should {

      "return nothing" in new Test {

        (mockVatApiConnector.getVatReturnObligations(_:String, _:LocalDate, _:LocalDate, _:VatReturnObligation.Status.Value)
        (_: HeaderCarrier, _: ExecutionContext))
          .expects(*,*,*,*,*,*)
          .returns(Future.successful(Right(VatReturnObligations(Seq.empty))))

        (mockFinancialDataConnector.getPaymentsForVatReturns(_:String)
        (_:HeaderCarrier, _:ExecutionContext))
          .expects(*,*,*)
          .returns(Future.successful(Right(Payments(Seq.empty))))

        val result: HttpGetResult[VatDetailsModel] = await(service.getVatDetails(User("1111")))

        result shouldBe Right(VatDetailsModel(None, None))
      }

    }

    "the connector returns an HttpError" should {

      "return a Future containing the error" in new Test {
        (mockVatApiConnector.getVatReturnObligations(_:String, _:LocalDate, _:LocalDate, _:VatReturnObligation.Status.Value)
        (_: HeaderCarrier, _: ExecutionContext))
          .expects(*,*,*,*,*,*)
          .returns(Future.successful(Left(BadRequestError("TEST_FAIL", "this is a test"))))

        val result: HttpGetResult[VatDetailsModel] = await(service.getVatDetails(User("1111")))

        result shouldBe Left(BadRequestError("TEST_FAIL", "this is a test"))
      }

    }

    "the connector returns an Exception" should {

      "return a failed Future containing the exception" in new Test {
        val expected = new RuntimeException("test")
        (mockVatApiConnector.getVatReturnObligations(_:String, _:LocalDate, _:LocalDate, _:VatReturnObligation.Status.Value)
        (_: HeaderCarrier, _: ExecutionContext))
          .expects(*,*,*,*,*,*)
          .returns(Future.failed(expected))

        intercept[RuntimeException](await(service.getVatDetails(User("1111"))))
      }

    }

  }

  "Calling .getCustomerInfo" should {

    "return a user's information" in new Test {

      val exampleCustomerInfo: CustomerInformation = CustomerInformation("Cheapo Clothing Ltd")

      (mockVatApiConnector.getCustomerInfo(_: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *)
        .returns(Future.successful(Right(exampleCustomerInfo)))

      lazy val result: HttpGetResult[CustomerInformation] = await(service.getCustomerInfo(User("999999999")))

      result shouldBe Right(exampleCustomerInfo)
    }
  }
}
