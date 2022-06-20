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

import java.time.LocalDate

import models.viewModels.CrystallisedLPP1ViewModel.form
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.data.FormError

class CrystallisedLPP1ViewModelSpec extends AnyWordSpecLike with Matchers{

  val model: CrystallisedLPP1ViewModel = CrystallisedLPP1ViewModel(
    numberOfDays = 30,
    part1Days = 15,
    part2Days = 30,
    interestRate = 15.0,
    part1UnpaidVAT = 77.00,
    Some(77.00),
    LocalDate.parse("2018-01-01"),
    154.00,
    0.00,
    154.00,
    LocalDate.parse("2018-01-01"),
    LocalDate.parse("2018-02-02"),
    "Late Payment Penalty",
    "XXXXXX1234567890",
    isOverdue = false
  )

  "The makePaymentRedirect value" should {

    "be a payment handoff URL generated from the model's parameters" in {
      val amountInPence = (model.penaltyAmount * 100).toLong
      val chargeTypeEncoded = model.chargeType.replace(" ", "%20")

      model.makePaymentRedirect should include(
        s"/make-payment/$amountInPence/${model.periodTo.getMonthValue}/${model.periodTo.getYear}" +
          s"/${model.periodTo}/$chargeTypeEncoded/${model.dueDate}/${model.chargeReference}"
      )
    }
  }

  "The CrystallisedLPP1ViewModel form" should {

    "bind successfully" when {

      "all values are provided and valid" in {
        form.mapping.bind(Map(
          "numberOfDays" -> "30",
          "part1Days" -> "15",
          "part2Days" -> "30",
          "interestRate" -> "15.0",
          "part1UnpaidVAT" -> "77.00",
          "part2UnpaidVAT" -> "77.00",
          "dueDate" -> "2018-01-01",
          "penaltyAmount" -> "154.00",
          "amountReceived" -> "0.00",
          "leftToPay" -> "154.00",
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "Late Payment Penalty",
          "chargeReference" -> "XXXXXX1234567890",
          "isOverdue" -> "false"
        )) shouldBe Right(model)
      }
    }

    "fail to bind" when {

      "a field is not provided" in {
        form.mapping.bind(Map(
          "numberOfDays" -> "30",
          "part1Days" -> "15",
          "part2Days" -> "30",
          "interestRate" -> "15.0",
          "part1UnpaidVAT" -> "77.00",
          "part2UnpaidVAT" -> "77.00",
          "dueDate" -> "2018-01-01",
          "penaltyAmount" -> "154.00",
          "amountReceived" -> "0.00",
          "leftToPay" -> "154.00",
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeReference" -> "XXXXXX1234567890",
          "isOverdue" -> "false"
        )) shouldBe Left(List(FormError("chargeType", List("error.required"), List())))
      }

      "the periodFrom field is invalid" in {
        form.mapping.bind(Map(
          "numberOfDays" -> "30",
          "part1Days" -> "15",
          "part2Days" -> "30",
          "interestRate" -> "15.0",
          "part1UnpaidVAT" -> "77.00",
          "part2UnpaidVAT" -> "77.00",
          "dueDate" -> "2018-01-01",
          "penaltyAmount" -> "154.00",
          "amountReceived" -> "0.00",
          "leftToPay" -> "154.00",
          "periodFrom" -> "12-13-14-15",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "Late Payment Penalty",
          "chargeReference" -> "XXXXXX1234567890",
          "isOverdue" -> "false"
        )) shouldBe Left(List(FormError("periodFrom", List("error.date"), List())))
      }
      "the periodTo field is invalid" in {
      form.mapping.bind(Map(
        "numberOfDays" -> "30",
        "part1Days" -> "15",
        "part2Days" -> "30",
        "interestRate" -> "15.0",
        "part1UnpaidVAT" -> "77.00",
        "part2UnpaidVAT" -> "77.00",
        "dueDate" -> "2018-01-01",
        "penaltyAmount" -> "154.00",
        "amountReceived" -> "0.00",
        "leftToPay" -> "154.00",
        "periodFrom" -> "2018-01-01",
        "periodTo" -> "nope",
        "chargeType" -> "Late Payment Penalty",
        "chargeReference" -> "XXXXXX1234567890",
        "isOverdue" -> "false"
      )) shouldBe Left(List(FormError("periodTo", List("error.date"), List())))
      }
      "the interest rate field is invalid" in {
        form.mapping.bind(Map(
          "numberOfDays" -> "30",
          "part1Days" -> "15",
          "part2Days" -> "30",
          "interestRate" -> "true",
          "part1UnpaidVAT" -> "77.00",
          "part2UnpaidVAT" -> "77.00",
          "dueDate" -> "2018-01-01",
          "penaltyAmount" -> "154.00",
          "amountReceived" -> "0.00",
          "leftToPay" -> "154.00",
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "Late Payment Penalty",
          "chargeReference" -> "XXXXXX1234567890",
          "isOverdue" -> "false"
        )) shouldBe Left(List(FormError("interestRate", List("error.real"), List())))
      }
      "the dueDate field is invalid" in {
        form.mapping.bind(Map(
          "numberOfDays" -> "30",
          "part1Days" -> "15",
          "part2Days" -> "30",
          "interestRate" -> "15.0",
          "part1UnpaidVAT" -> "77.00",
          "part2UnpaidVAT" -> "77.00",
          "dueDate" -> "true",
          "penaltyAmount" -> "154.00",
          "amountReceived" -> "0.00",
          "leftToPay" -> "154.00",
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "Late Payment Penalty",
          "chargeReference" -> "XXXXXX1234567890",
          "isOverdue" -> "false"
        )) shouldBe Left(List(FormError("dueDate", List("error.date"), List())))
      }
      "the penalty amount field is invalid" in {
        form.mapping.bind(Map(
          "numberOfDays" -> "30",
          "part1Days" -> "15",
          "part2Days" -> "30",
          "interestRate" -> "15.0",
          "part1UnpaidVAT" -> "77.00",
          "part2UnpaidVAT" -> "77.00",
          "dueDate" -> "2018-01-01",
          "penaltyAmount" -> "300.33.44.55",
          "amountReceived" -> "0.00",
          "leftToPay" -> "154.00",
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "Late Payment Penalty",
          "chargeReference" -> "XXXXXX1234567890",
          "isOverdue" -> "false"
        )) shouldBe Left(List(FormError("penaltyAmount", List("error.real"), List())))
      }
      "the amountReceived field is invalid" in {
        form.mapping.bind(Map(
          "numberOfDays" -> "30",
          "part1Days" -> "15",
          "part2Days" -> "30",
          "interestRate" -> "15.0",
          "part1UnpaidVAT" -> "77.00",
          "part2UnpaidVAT" -> "77.00",
          "dueDate" -> "2018-01-01",
          "penaltyAmount" -> "154.00",
          "amountReceived" -> "nope",
          "leftToPay" -> "154.00",
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "Late Payment Penalty",
          "chargeReference" -> "XXXXXX1234567890",
          "isOverdue" -> "false"
        )) shouldBe Left(List(FormError("amountReceived", List("error.real"), List())))
      }
      "the leftToPay field is invalid" in {
        form.mapping.bind(Map(
          "numberOfDays" -> "30",
          "part1Days" -> "15",
          "part2Days" -> "30",
          "interestRate" -> "15.0",
          "part1UnpaidVAT" -> "77.00",
          "part2UnpaidVAT" -> "77.00",
          "dueDate" -> "2018-01-01",
          "penaltyAmount" -> "154.00",
          "amountReceived" -> "0.00",
          "leftToPay" -> "100.11.0",
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "Late Payment Penalty",
          "chargeReference" -> "XXXXXX1234567890",
          "isOverdue" -> "false"
        )) shouldBe Left(List(FormError("leftToPay", List("error.real"), List())))
      }
      "isOverdue field is invalid" in {
        form.mapping.bind(Map(
          "numberOfDays" -> "30",
          "part1Days" -> "15",
          "part2Days" -> "30",
          "interestRate" -> "15.0",
          "part1UnpaidVAT" -> "77.00",
          "part2UnpaidVAT" -> "77.00",
          "dueDate" -> "2018-01-01",
          "penaltyAmount" -> "154.00",
          "amountReceived" -> "0.00",
          "leftToPay" -> "154.00",
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "Late Payment Penalty",
          "chargeReference" -> "XXXXXX1234567890",
          "isOverdue" -> "12.0"
        )) shouldBe Left(List(FormError("isOverdue", List("error.boolean"), List())))
      }
    }
  }
}
