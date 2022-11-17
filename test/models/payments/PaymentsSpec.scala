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

package models.payments

import java.time.LocalDate
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json
import org.scalatest.matchers.should.Matchers

class PaymentsSpec extends AnyWordSpecLike with Matchers {

  "A payment" should {

    val examplePayment = PaymentWithPeriod(
      ReturnDebitCharge,
      LocalDate.parse("2017-01-01"),
      LocalDate.parse("2017-03-01"),
      LocalDate.parse("2017-03-08"),
      9999,
      Some("#001"),
      Some("XD002750002155"),
      ddCollectionInProgress = false,
      Some(2),
      Some(2.22),
      Some(BigDecimal(100.00)),
      Some("LPP1"),
      BigDecimal(10000),
      Some(50.55)
    )

    val exampleInputString =
      """{
        |"chargeType":"VAT Return Debit Charge",
        |"taxPeriodFrom":"2017-01-01",
        |"taxPeriodTo":"2017-03-01",
        |"items":[{"dueDate":"2017-03-08"}, {"dueDate":"2017-03-09"}],
        |"outstandingAmount":9999,
        |"periodKey":"#001",
        |"chargeReference": "XD002750002155",
        |"accruedInterestAmount": 2,
        |"interestRate": 2.22,
        |"accruedPenaltyAmount" : 100.00,
        |"penaltyType" : "LPP1",
        |"originalAmount" : 10000,
        |"clearedAmount" : 50.55
        |}"""
        .stripMargin.replace("\n", "")

    "be parsed from appropriate JSON" in {
      val result = Json.parse(exampleInputString).as[Payment]
      result shouldEqual examplePayment
    }

  }

  "Payments" should {

    val examplePayments = Payments(
      Seq(
        PaymentWithPeriod(
          ReturnDebitCharge,
          LocalDate.parse("2017-01-01"),
          LocalDate.parse("2017-03-01"),
          LocalDate.parse("2017-03-08"),
          9999,
          Some("#001"),
          Some("XD002750002155"),
          ddCollectionInProgress = false,
          Some(2),
          Some(2.22),
          Some(BigDecimal(100.00)),
          Some("LPP1"),
          BigDecimal(10000),
          Some(50.55)
        ),
        PaymentWithPeriod(
          ReturnCreditCharge,
          LocalDate.parse("2017-02-01"),
          LocalDate.parse("2017-04-01"),
          LocalDate.parse("2017-05-08"),
          7777,
          Some("#002"),
          Some("XD002750002155"),
          ddCollectionInProgress = false,
          Some(2),
          Some(2.22),
          Some(BigDecimal(100.00)),
          Some("LPP1"),
          BigDecimal(10000),
          Some(60.66)
        )
      )
    )

    val exampleInputString =
      """{
        |"financialTransactions":[{
        |"chargeType":"VAT Return Debit Charge",
        |"taxPeriodFrom":"2017-01-01",
        |"taxPeriodTo":"2017-03-01",
        |"items":[{"dueDate":"2017-03-08"}, {"dueDate":"2017-03-09"}],
        |"outstandingAmount":9999,
        |"periodKey":"#001",
        |"chargeReference": "XD002750002155",
        |"accruedInterestAmount": 2,
        |"interestRate": 2.22,
        |"accruedPenaltyAmount" : 100.00,
        |"penaltyType" : "LPP1",
        |"originalAmount" : 10000,
        |"clearedAmount" : 50.55
        |},{
        |"chargeType":"VAT Return Credit Charge",
        |"taxPeriodFrom":"2017-02-01",
        |"taxPeriodTo":"2017-04-01",
        |"items":[{"dueDate":"2017-05-08"}, {"dueDate":"2017-05-09"}],
        |"outstandingAmount":7777,
        |"periodKey":"#002",
        |"chargeReference": "XD002750002155",
        |"accruedInterestAmount": 2,
        |"interestRate": 2.22,
        |"accruedPenaltyAmount" : 100.00,
        |"penaltyType" : "LPP1",
        |"originalAmount" : 10000,
        |"clearedAmount" : 60.66
        |}]}"""
        .stripMargin.replace("\n", "")

    "be parsed from appropriate JSON" in {
      val result = Json.parse(exampleInputString).as[Payments]
      result shouldEqual examplePayments
    }
  }
}
