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

package audit

import java.time.LocalDate

import _root_.models.payments._
import _root_.models.viewModels.PaymentsHistoryModel
import audit.models.ViewVatPaymentHistoryAuditModel
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.{JsValue, Json}
import org.scalatest.matchers.should.Matchers

class ViewVatPaymentHistoryAuditModelSpec extends AnyWordSpecLike with Matchers {

  val onePayment: Seq[PaymentsHistoryModel] =
    Seq(
      PaymentsHistoryModel(
        chargeType = ReturnDebitCharge,
        taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
        taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
        amount = 150,
        clearedDate = Some(LocalDate.of(2018, 1, 10))
      )
    )

  val onePaymentPerPeriod: Seq[PaymentsHistoryModel] = Seq(
    PaymentsHistoryModel(
      chargeType = ReturnDebitCharge,
      taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
      taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
      amount = 150,
      clearedDate = Some(LocalDate.of(2018, 1, 10))
    ),
    PaymentsHistoryModel(
      chargeType = ReturnCreditCharge,
      taxPeriodFrom = Some(LocalDate.of(2018, 5, 1)),
      taxPeriodTo = Some(LocalDate.of(2018, 7, 31)),
      amount = 600,
      clearedDate = Some(LocalDate.of(2018, 3, 10))
    ))

  val multiplesPaymentsPerPeriod: Seq[PaymentsHistoryModel] = Seq(
    PaymentsHistoryModel(
      chargeType = ReturnDebitCharge,
      taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
      taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
      amount = 150,
      clearedDate = Some(LocalDate.of(2018, 1, 10))
    ),
    PaymentsHistoryModel(
      chargeType = ReturnDebitCharge,
      taxPeriodFrom = Some(LocalDate.of(2018, 8, 1)),
      taxPeriodTo = Some(LocalDate.of(2018, 10, 31)),
      amount = 100,
      clearedDate = Some(LocalDate.of(2018, 3, 10))
    ),
    PaymentsHistoryModel(
      chargeType = ReturnCreditCharge,
      taxPeriodFrom = Some(LocalDate.of(2018, 5, 1)),
      taxPeriodTo = Some(LocalDate.of(2018, 7, 31)),
      amount = 600,
      clearedDate = Some(LocalDate.of(2018, 4, 10))
    ),
    PaymentsHistoryModel(
      chargeType = ReturnCreditCharge,
      taxPeriodFrom = Some(LocalDate.of(2018, 5, 1)),
      taxPeriodTo = Some(LocalDate.of(2018, 7, 31)),
      amount = 500,
      clearedDate = Some(LocalDate.of(2018, 5, 10))
    )
  )

  val vrn: String = "999999999"

  " ViewVatPaymentHistoryAuditModel" should {

    "be constructed correctly when there is one payment only" in {
      val testData = ViewVatPaymentHistoryAuditModel(
        vrn,
        payments = onePayment
      )

      val expectedJson: JsValue = Json.parse(
        s"""{
              "vrn": "999999999",
              "payments": [{
                "vrn": "999999999",
                "chargeType": "VAT Return Debit Charge",
                "periodFrom": "2018-08-01",
                "periodTo": "2018-10-31",
                "clearingDate": "2018-01-10",
                "paymentAmount": 150
              }]
            }""".stripMargin
      )

      testData.detail shouldBe expectedJson
    }

    "be constructed correctly when there is one payment per period" in {

      val testData = ViewVatPaymentHistoryAuditModel(
        vrn,
        payments = onePaymentPerPeriod
      )

      val expectedJson: JsValue = Json.parse(
        s"""{
                  "vrn": "999999999",
                  "payments": [{
                    "vrn": "999999999",
                    "chargeType": "VAT Return Debit Charge",
                    "periodFrom": "2018-08-01",
                    "periodTo": "2018-10-31",
                    "clearingDate": "2018-01-10",
                    "paymentAmount": 150
                  }, {
                    "vrn": "999999999",
                    "chargeType": "VAT Return Credit Charge",
                    "periodFrom": "2018-05-01",
                    "periodTo": "2018-07-31",
                    "clearingDate": "2018-03-10",
                    "paymentAmount": 600
                  }]
                }""".stripMargin
      )

      testData.detail shouldBe expectedJson
    }


    "be constructed correctly when there are multiple payments per period" in {

      val testData = ViewVatPaymentHistoryAuditModel(
        vrn,
        payments = multiplesPaymentsPerPeriod
      )

      val expectedJson: JsValue = Json.parse(
        s"""{
              "vrn": "999999999",
              "payments": [{
                "vrn": "999999999",
                "chargeType": "VAT Return Debit Charge",
                "periodFrom": "2018-08-01",
                "periodTo": "2018-10-31",
                "clearingDate": "2018-01-10",
                "paymentAmount": 150
              }, {
                "vrn": "999999999",
                "chargeType": "VAT Return Debit Charge",
                "periodFrom": "2018-08-01",
                "periodTo": "2018-10-31",
                "clearingDate": "2018-03-10",
                "paymentAmount": 100
              }, {
                "vrn": "999999999",
                "chargeType": "VAT Return Credit Charge",
                "periodFrom": "2018-05-01",
                "periodTo": "2018-07-31",
                "clearingDate": "2018-04-10",
                "paymentAmount": 600
              }, {
                "vrn": "999999999",
                "chargeType": "VAT Return Credit Charge",
                "periodFrom": "2018-05-01",
                "periodTo": "2018-07-31",
                "clearingDate": "2018-05-10",
                "paymentAmount": 500
              }]
            }""".stripMargin
      )

      testData.detail shouldBe expectedJson
    }

  }
}
