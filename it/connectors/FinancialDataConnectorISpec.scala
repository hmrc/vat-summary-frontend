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

package connectors

import java.time.LocalDate

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.IntegrationBaseSpec
import models.errors.BadRequestError
import models.payments.{Payment, Payments}
import common.FinancialTransactionsConstants
import models.viewModels.PaymentsHistoryModel
import stubs.FinancialDataStub
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class FinancialDataConnectorISpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping

    val connector: FinancialDataConnector = app.injector.instanceOf[FinancialDataConnector]
    implicit val hc: HeaderCarrier = HeaderCarrier()
  }

  "calling getOpenPayments with a status of 'O'" should {

    "return all outstanding payments for a given period" in new Test {
      override def setupStubs(): StubMapping = FinancialDataStub.stubAllOutstandingOpenPayments

      val expected = Right(Payments(Seq(
        Payment(
          "VAT Return Debit Charge",
          LocalDate.parse("2015-03-01"),
          LocalDate.parse("2015-03-31"),
          LocalDate.parse("2019-01-15"),
          10000,
          "15AC"
        ),
        Payment(
          "VAT Return Debit Charge",
          LocalDate.parse("2015-03-01"),
          LocalDate.parse("2015-03-31"),
          LocalDate.parse("2019-01-16"),
          10000,
          "15AC"
        )
      )))

      setupStubs()
      private val result = await(connector.getOpenPayments("123456789"))

      result shouldEqual expected
    }

    "return an empty list of payments" in new Test {
      override def setupStubs(): StubMapping = FinancialDataStub.stubNoPayments

      val expected = Right(Payments(Seq.empty))

      setupStubs()
      private val result = await(connector.getOpenPayments("123456789"))

      result shouldEqual expected
    }
  }

  "calling getOpenPayments with an invalid VRN" should {

    "return an BadRequestError" in new Test {
      override def setupStubs(): StubMapping = FinancialDataStub.stubInvalidVrn

      val expected = Left(BadRequestError(
        code = "INVALID_VRN",
        errorResponse = "VRN was invalid!"
      ))

      setupStubs()
      private val result = await(connector.getOpenPayments("111"))

      result shouldEqual expected
    }
  }

  "calling getVatLiabilities" should {

    "return a PaymentsHistoryModel" in new Test {
      override def setupStubs(): StubMapping = FinancialDataStub.stubAllOutstandingPayments


      val expected = Right(Seq(
        PaymentsHistoryModel(
          chargeType    =  FinancialTransactionsConstants.vatReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.parse("2018-08-01")),
          taxPeriodTo   = Some(LocalDate.parse("2018-10-31")),
          amount        = 150,
          clearedDate   = Some(LocalDate.parse("2018-01-10"))
        ),
        PaymentsHistoryModel(
          chargeType    =  FinancialTransactionsConstants.vatReturnCreditCharge,
          taxPeriodFrom = Some(LocalDate.parse("2018-05-01")),
          taxPeriodTo   = Some(LocalDate.parse("2018-07-31")),
          amount        = 600,
          clearedDate   = Some(LocalDate.parse("2018-03-10"))
        )
      ))

      setupStubs()
      private val result = await(connector.getVatLiabilities(
        "5555555555",
        LocalDate.parse("2018-01-01"),
        LocalDate.parse("2018-12-31")
      ))

      result shouldEqual expected
    }
  }

  "calling getDirectDebitStatus with a valid VRN" should {
    "return a Boolean" in new Test {
      override def setupStubs(): StubMapping = FinancialDataStub.stubSuccessfulDirectDebit

      val expected = Right(true)

      setupStubs()
      private val result = await(connector.getDirectDebitStatus("111111111"))

      result shouldEqual expected
    }
  }

  "calling getDirectDebitStatus with an invalid VRN" should {

    "return an BadRequestError" in new Test {
      override def setupStubs(): StubMapping = FinancialDataStub.stubInvalidVrnDirectDebit

      val expected = Left(BadRequestError(
        code = "INVALID_VRN",
        errorResponse = "VRN was invalid!"
      ))

      setupStubs()
      private val result = await(connector.getDirectDebitStatus("111"))

      result shouldEqual expected
    }
  }
}
