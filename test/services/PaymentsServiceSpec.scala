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
import models.payments.{Payment, Payments}
import org.scalamock.matchers.Matchers
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.{ExecutionContext, Future}

class PaymentsServiceSpec extends UnitSpec {

//  private trait Test {
//
//    implicit val hc: HeaderCarrier = HeaderCarrier()
//    val mockFinancialDataConnector: FinancialDataConnector = mock[FinancialDataConnector]
//    lazy val service = new PaymentsService(mockFinancialDataConnector)
//  }

//  "getOpenPayments" should {
//    "retrive a list of payments" in new Test {
//      val payments = await(service.getOpenPayments())
//
//      (mockFinancialDataConnector.getOpenPaymnents()
//      (_: HeaderCarrier, _: ExecutionContext))
//        .expects(*, *)
//        .returns(Future.successful(Right(Payments(Seq(Payment(LocalDate.parse("2008-12-06"), LocalDate.parse("2009-01-04"), LocalDate.parse("2008-12-06"), BigDecimal("21.22"), "ABCD"))))))
//
//      payment.size shouldBe 1
//
//    }
//  }

}
