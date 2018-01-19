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

package models.payments

import java.time.LocalDate

import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec

class PaymentsSpec extends UnitSpec {

  "A payment" should {

    val examplePayment = Payment(
      LocalDate.parse("2017-03-01"),
      LocalDate.parse("2017-03-08"),
      9999,
      "O",
      "#001"
    )

    val exampleString =
      """{
        |"end":"2017-03-01",
        |"due":"2017-03-08",
        |"outstandingAmount":9999,
        |"status":"O",
        |"periodKey":"#001"
        |}"""
        .stripMargin.replace("\n", "")

    "parse to JSON" in {
      val result = Json.toJson(examplePayment).toString
      result shouldEqual exampleString
    }

    "be parsed from appropriate JSON" in {
      val result = Json.parse(exampleString).as[Payment]
      result shouldEqual examplePayment
    }

  }

  "Payments" should {

    val examplePayments = Payments(
      Seq(
        Payment(
          LocalDate.parse("2017-03-01"),
          LocalDate.parse("2017-03-08"),
          9999,
          "O",
          "#001"
        ),
        Payment(
          LocalDate.parse("2017-04-01"),
          LocalDate.parse("2017-05-08"),
          7777,
          "O",
          "#002"
        )
      )
    )

    val exampleString =
      """{
        |"payments":[{
        |"end":"2017-03-01",
        |"due":"2017-03-08",
        |"outstandingAmount":9999,
        |"status":"O",
        |"periodKey":"#001"
        |},{
        |"end":"2017-04-01",
        |"due":"2017-05-08",
        |"outstandingAmount":7777,
        |"status":"O",
        |"periodKey":"#002"
        |}]}"""
        .stripMargin.replace("\n", "")

    "parse to JSON" in {
      val result = Json.toJson(examplePayments).toString
      result shouldEqual exampleString
    }

    "be parsed from appropriate JSON" in {
      val result = Json.parse(exampleString).as[Payments]
      result shouldEqual examplePayments
    }
  }
}
