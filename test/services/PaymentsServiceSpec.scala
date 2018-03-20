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

import connectors.FinancialDataConnector
import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.errors.ServerSideError
import models.payments.{Payment, Payments}
import org.scalamock.matchers.Matchers
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec
import org.scalamock.scalatest.MockFactory
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class PaymentsServiceSpec extends UnitSpec with MockFactory with Matchers {

  private trait Test {

    implicit val hc: HeaderCarrier = HeaderCarrier()
    val mockFinancialDataConnector: FinancialDataConnector = mock[FinancialDataConnector]
    val responseFromConnector: HttpGetResult[Payments]

    def setup(): Any = {
      (mockFinancialDataConnector.getOpenPayments(_: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *)
        .returns(Future.successful(responseFromConnector))
    }

    def target: PaymentsService = {
      setup()
      new PaymentsService(mockFinancialDataConnector)
    }
  }

  "Calling the .getOpenPayments function" when {

    "the user has payments outstanding" should {

      "return a list of payments" in new Test {
        val payments = Payments(Seq(Payment(
          LocalDate.parse("2008-12-06"),
          LocalDate.parse("2009-01-04"),
          LocalDate.parse("2008-12-06"),
          BigDecimal("21.22"),
          "ABCD"
        )))
        override val responseFromConnector = Right(payments)
        val paymentsResponse: Option[Payments] = await(target.getOpenPayments("123456789"))

        paymentsResponse shouldBe Some(payments)
      }
    }

    "the user has no payments outstanding" should {

      "return an empty list of payments" in new Test {
        val payments = Payments(Seq.empty)
        override val responseFromConnector = Right(payments)
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
        override val responseFromConnector = Left(ServerSideError(Status.GATEWAY_TIMEOUT, errorResponse))
        val paymentsResponse: Option[Payments] = await(target.getOpenPayments("123456789"))

        paymentsResponse shouldBe None
      }
    }
  }

}