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

package models.viewModels

import common.TestModels.{crystallisedInterestCharge, crystallisedInterestJson}
import models.viewModels.CrystallisedInterestViewModel.form
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.data.FormError
import play.api.libs.json.Json

import java.time.LocalDate

class CrystallisedInterestViewModelSpec extends AnyWordSpecLike with Matchers {

  val model: CrystallisedInterestViewModel = CrystallisedInterestViewModel(
    LocalDate.parse("2018-01-01"),
    LocalDate.parse("2018-02-02"),
    "VAT Default Interest",
    2.6,
    LocalDate.parse("2018-03-03"),
    300.33,
    200.22,
    100.11,
    isOverdue = false,
    "XXXXXX1234567890",
    isPenalty = false
  )

  "The makePaymentRedirect value" should {

    "be a payment handoff URL generated from the model's parameters" in {
      val amountInPence = (model.leftToPay * 100).toLong
      val chargeTypeEncoded = model.chargeType.replace(" ", "%20")

      model.makePaymentRedirect should include(
        s"/make-payment/$amountInPence/${model.periodTo.getMonthValue}/${model.periodTo.getYear}" +
        s"/${model.periodTo}/$chargeTypeEncoded/${model.dueDate}/${model.chargeReference}"
      )
    }
  }

  "The CrystallisedInterestViewModel form" should {

    "bind successfully" when {

      "all values are provided and valid" in {
        form.mapping.bind(Map(
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "VAT Default Interest",
          "interestRate" -> "2.6",
          "dueDate" -> "2018-03-03",
          "interestAmount" -> "300.33",
          "amountReceived" -> "200.22",
          "leftToPay" -> "100.11",
          "isOverdue" -> "false",
          "chargeReference" -> "XXXXXX1234567890",
          "isPenalty" -> "false"
        )) shouldBe Right(model)
      }
    }

    "fail to bind" when {

      "a field is not provided" in {
        form.mapping.bind(Map(
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "interestRate" -> "2.6",
          "dueDate" -> "2018-03-03",
          "interestAmount" -> "300.33",
          "amountReceived" -> "200.22",
          "leftToPay" -> "100.11",
          "isOverdue" -> "false",
          "chargeReference" -> "XXXXXX1234567890",
          "isPenalty" -> "false"
        )) shouldBe Left(List(FormError("chargeType", List("error.required"), List())))
      }

      "the periodFrom field is invalid" in {
        form.mapping.bind(Map(
          "periodFrom" -> "12-13-14-15",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "VAT Default Interest",
          "interestRate" -> "2.6",
          "dueDate" -> "2018-03-03",
          "interestAmount" -> "300.33",
          "amountReceived" -> "200.22",
          "leftToPay" -> "100.11",
          "isOverdue" -> "false",
          "chargeReference" -> "XXXXXX1234567890",
          "isPenalty" -> "false"
        )) shouldBe Left(List(FormError("periodFrom", List("error.date"), List())))
      }

      "the periodTo field is invalid" in {
        form.mapping.bind(Map(
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "nope",
          "chargeType" -> "VAT Default Interest",
          "interestRate" -> "2.6",
          "dueDate" -> "2018-03-03",
          "interestAmount" -> "300.33",
          "amountReceived" -> "200.22",
          "leftToPay" -> "100.11",
          "isOverdue" -> "false",
          "chargeReference" -> "XXXXXX1234567890",
          "isPenalty" -> "false"
        )) shouldBe Left(List(FormError("periodTo", List("error.date"), List())))
      }

      "the interestRate field is invalid" in {
        form.mapping.bind(Map(
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "VAT Default Interest",
          "interestRate" -> "true",
          "dueDate" -> "2018-03-03",
          "interestAmount" -> "300.33",
          "amountReceived" -> "200.22",
          "leftToPay" -> "100.11",
          "isOverdue" -> "false",
          "chargeReference" -> "XXXXXX1234567890",
          "isPenalty" -> "false"
        )) shouldBe Left(List(FormError("interestRate", List("error.real"), List())))
      }

      "the dueDate field is invalid" in {
        form.mapping.bind(Map(
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "VAT Default Interest",
          "interestRate" -> "2.6",
          "dueDate" -> "nope",
          "interestAmount" -> "300.33",
          "amountReceived" -> "200.22",
          "leftToPay" -> "100.11",
          "isOverdue" -> "false",
          "chargeReference" -> "XXXXXX1234567890",
          "isPenalty" -> "false"
        )) shouldBe Left(List(FormError("dueDate", List("error.date"), List())))
      }

      "the interestAmount field is invalid" in {
        form.mapping.bind(Map(
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "VAT Default Interest",
          "interestRate" -> "2.6",
          "dueDate" -> "2018-03-03",
          "interestAmount" -> "300.33.44.55",
          "amountReceived" -> "200.22",
          "leftToPay" -> "100.11",
          "isOverdue" -> "false",
          "chargeReference" -> "XXXXXX1234567890",
          "isPenalty" -> "false"
        )) shouldBe Left(List(FormError("interestAmount", List("error.real"), List())))
      }

      "the amountReceived field is invalid" in {
        form.mapping.bind(Map(
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "VAT Default Interest",
          "interestRate" -> "2.6",
          "dueDate" -> "2018-03-03",
          "interestAmount" -> "300.33",
          "amountReceived" -> "nope",
          "leftToPay" -> "100.11",
          "isOverdue" -> "false",
          "chargeReference" -> "XXXXXX1234567890",
          "isPenalty" -> "false"
        )) shouldBe Left(List(FormError("amountReceived", List("error.real"), List())))
      }

      "the leftToPay field is invalid" in {
        form.mapping.bind(Map(
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "VAT Default Interest",
          "interestRate" -> "2.6",
          "dueDate" -> "2018-03-03",
          "interestAmount" -> "300.33",
          "amountReceived" -> "200.22",
          "leftToPay" -> "100.11.0",
          "isOverdue" -> "false",
          "chargeReference" -> "XXXXXX1234567890",
          "isPenalty" -> "false"
        )) shouldBe Left(List(FormError("leftToPay", List("error.real"), List())))
      }

      "the isOverdue field is invalid" in {
        form.mapping.bind(Map(
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "VAT Default Interest",
          "interestRate" -> "2.6",
          "dueDate" -> "2018-03-03",
          "interestAmount" -> "300.33",
          "amountReceived" -> "200.22",
          "leftToPay" -> "100.11",
          "isOverdue" -> "5",
          "chargeReference" -> "XXXXXX1234567890",
          "isPenalty" -> "false"
        )) shouldBe Left(List(FormError("isOverdue", List("error.boolean"), List())))
      }
    }

    "the isPenalty field is invalid" in {
      form.mapping.bind(Map(
        "periodFrom" -> "2018-01-01",
        "periodTo" -> "2018-02-02",
        "chargeType" -> "VAT Default Interest",
        "interestRate" -> "2.6",
        "dueDate" -> "2018-03-03",
        "interestAmount" -> "300.33",
        "amountReceived" -> "200.22",
        "leftToPay" -> "100.11",
        "isOverdue" -> "true",
        "chargeReference" -> "XXXXXX1234567890",
        "isPenalty" -> "100"
      )) shouldBe Left(List(FormError("isPenalty", List("error.boolean"), List())))
    }
  }

  "The CrystallisedInterestViewModel" should {

    "read from JSON" in {
      crystallisedInterestJson.as[CrystallisedInterestViewModel] shouldBe crystallisedInterestCharge
    }

    "write to JSON" in {
      Json.toJson(crystallisedInterestCharge) shouldBe crystallisedInterestJson
    }
  }
}
