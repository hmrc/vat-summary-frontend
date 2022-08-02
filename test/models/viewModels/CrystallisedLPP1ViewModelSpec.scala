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

import common.TestModels.{crystallisedLPP1JsonMax, crystallisedLPP1JsonMin, crystallisedLPP1Model, crystallisedLPP1ModelMin}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import models.viewModels.CrystallisedLPP1ViewModel.form
import play.api.data.FormError
import play.api.libs.json.Json

class CrystallisedLPP1ViewModelSpec extends AnyWordSpecLike with Matchers {

  "The makePaymentRedirect value" should {

    "be a payment handoff URL generated from the model's parameters" in {
      val amountInPence = (crystallisedLPP1Model.leftToPay * 100).toLong
      val chargeTypeEncoded = crystallisedLPP1Model.chargeType.replace(" ", "%20")

      crystallisedLPP1Model.makePaymentRedirect should include(
        s"/make-payment/$amountInPence/${crystallisedLPP1Model.periodTo.getMonthValue}/" +
          s"${crystallisedLPP1Model.periodTo.getYear}/${crystallisedLPP1Model.periodTo}/$chargeTypeEncoded/" +
          s"${crystallisedLPP1Model.dueDate}/${crystallisedLPP1Model.chargeReference}"
      )
    }
  }
  "The CrystallisedLPP1ViewModel form" should {

    "bind successfully" when {

      "all values are provided and valid" in {
        form.mapping.bind(Map(
          "numberOfDays" -> "99",
          "part1Days" -> "10",
          "part2Days" -> "20",
          "part1PenaltyRate" -> "2.4",
          "part2PenaltyRate" -> "2.6",
          "part1UnpaidVAT" -> "111.11",
          "part2UnpaidVAT" -> "222.22",
          "dueDate" -> "2020-01-01",
          "penaltyAmount" -> "500.55",
          "amountReceived" -> "100.11",
          "leftToPay" -> "400.44",
          "periodFrom" -> "2020-03-03",
          "periodTo" -> "2020-04-04",
          "chargeType" -> "VAT Return 1st LPP",
          "chargeReference" -> "CHARGEREF",
          "isOverdue" -> "false"
        )) shouldBe Right(crystallisedLPP1Model)
      }
    }
  }
  "fail to bind" when {

    "a field is not provided" in {
      form.mapping.bind(Map(
        "numberOfDays" -> "99",
        "part1Days" -> "10",
        "part2Days" -> "20",
        "part1PenaltyRate" -> "2.4",
        "part2PenaltyRate" -> "2.6",
        "part1UnpaidVAT" -> "111.11",
        "part2UnpaidVAT" -> "222.22",
        "dueDate" -> "2020-01-01",
        "penaltyAmount" -> "500.55",
        "amountReceived" -> "100.11",
        "leftToPay" -> "400.44",
        "periodFrom" -> "2020-03-03",
        "periodTo" -> "2020-04-04",
        "chargeReference" -> "CHARGEREF",
        "isOverdue" -> "false"
      )) shouldBe Left(List(FormError("chargeType", List("error.required"), List())))
    }

    "the periodFrom field is invalid" in {
      form.mapping.bind(Map(
        "numberOfDays" -> "99",
        "part1Days" -> "10",
        "part2Days" -> "20",
        "part1PenaltyRate" -> "2.4",
        "part2PenaltyRate" -> "2.6",
        "part1UnpaidVAT" -> "111.11",
        "part2UnpaidVAT" -> "222.22",
        "dueDate" -> "2020-01-01",
        "penaltyAmount" -> "500.55",
        "amountReceived" -> "100.11",
        "leftToPay" -> "400.44",
        "periodFrom" -> "12-13-14-15",
        "periodTo" -> "2020-04-04",
        "chargeType" -> "VAT Return 1st LPP",
        "chargeReference" -> "CHARGEREF",
        "isOverdue" -> "false"
      )) shouldBe Left(List(FormError("periodFrom", List("error.date"), List())))
    }
    "the periodTo field is invalid" in {
      form.mapping.bind(Map(
        "numberOfDays" -> "99",
        "part1Days" -> "10",
        "part2Days" -> "20",
        "part1PenaltyRate" -> "2.4",
        "part2PenaltyRate" -> "2.6",
        "part1UnpaidVAT" -> "111.11",
        "part2UnpaidVAT" -> "222.22",
        "dueDate" -> "2020-01-01",
        "penaltyAmount" -> "500.55",
        "amountReceived" -> "100.11",
        "leftToPay" -> "400.44",
        "periodFrom" -> "2020-03-03",
        "periodTo" -> "nope",
        "chargeType" -> "VAT Return 1st LPP",
        "chargeReference" -> "CHARGEREF",
        "isOverdue" -> "false"
      )) shouldBe Left(List(FormError("periodTo", List("error.date"), List())))
    }
    "the part 1 penalty rate field is invalid" in {
      form.mapping.bind(Map(
        "numberOfDays" -> "99",
        "part1Days" -> "10",
        "part2Days" -> "20",
        "part1PenaltyRate" -> "true",
        "part2PenaltyRate" -> "2.6",
        "part1UnpaidVAT" -> "111.11",
        "part2UnpaidVAT" -> "222.22",
        "dueDate" -> "2020-01-01",
        "penaltyAmount" -> "500.55",
        "amountReceived" -> "100.11",
        "leftToPay" -> "400.44",
        "periodFrom" -> "2020-03-03",
        "periodTo" -> "2020-04-04",
        "chargeType" -> "VAT Return 1st LPP",
        "chargeReference" -> "CHARGEREF",
        "isOverdue" -> "false"
      )) shouldBe Left(List(FormError("part1PenaltyRate", List("error.real"), List())))
    }
    "the part 2 penalty rate field is invalid" in {
      form.mapping.bind(Map(
        "numberOfDays" -> "99",
        "part1Days" -> "10",
        "part2Days" -> "20",
        "part1PenaltyRate" -> "2.4",
        "part2PenaltyRate" -> "false",
        "part1UnpaidVAT" -> "111.11",
        "part2UnpaidVAT" -> "222.22",
        "dueDate" -> "2020-01-01",
        "penaltyAmount" -> "500.55",
        "amountReceived" -> "100.11",
        "leftToPay" -> "400.44",
        "periodFrom" -> "2020-03-03",
        "periodTo" -> "2020-04-04",
        "chargeType" -> "VAT Return 1st LPP",
        "chargeReference" -> "CHARGEREF",
        "isOverdue" -> "false"
      )) shouldBe Left(List(FormError("part2PenaltyRate", List("error.real"), List())))
    }
    "the dueDate field is invalid" in {
      form.mapping.bind(Map(
        "numberOfDays" -> "99",
        "part1Days" -> "10",
        "part2Days" -> "20",
        "part1PenaltyRate" -> "2.4",
        "part2PenaltyRate" -> "2.6",
        "part1UnpaidVAT" -> "111.11",
        "part2UnpaidVAT" -> "222.22",
        "dueDate" -> "true",
        "penaltyAmount" -> "500.55",
        "amountReceived" -> "100.11",
        "leftToPay" -> "400.44",
        "periodFrom" -> "2020-03-03",
        "periodTo" -> "2020-04-04",
        "chargeType" -> "VAT Return 1st LPP",
        "chargeReference" -> "CHARGEREF",
        "isOverdue" -> "false"
      )) shouldBe Left(List(FormError("dueDate", List("error.date"), List())))
    }
    "the penalty amount field is invalid" in {
      form.mapping.bind(Map(
        "numberOfDays" -> "99",
        "part1Days" -> "10",
        "part2Days" -> "20",
        "part1PenaltyRate" -> "2.4",
        "part2PenaltyRate" -> "2.6",
        "part1UnpaidVAT" -> "111.11",
        "part2UnpaidVAT" -> "222.22",
        "dueDate" -> "2020-01-01",
        "penaltyAmount" -> "300.33.44.55",
        "amountReceived" -> "100.11",
        "leftToPay" -> "400.44",
        "periodFrom" -> "2020-03-03",
        "periodTo" -> "2020-04-04",
        "chargeType" -> "VAT Return 1st LPP",
        "chargeReference" -> "CHARGEREF",
        "isOverdue" -> "false"
      )) shouldBe Left(List(FormError("penaltyAmount", List("error.real"), List())))
    }
    "the amountReceived field is invalid" in {
      form.mapping.bind(Map(
        "numberOfDays" -> "99",
        "part1Days" -> "10",
        "part2Days" -> "20",
        "part1PenaltyRate" -> "2.4",
        "part2PenaltyRate" -> "2.6",
        "part1UnpaidVAT" -> "111.11",
        "part2UnpaidVAT" -> "222.22",
        "dueDate" -> "2020-01-01",
        "penaltyAmount" -> "500.55",
        "amountReceived" -> "nope",
        "leftToPay" -> "400.44",
        "periodFrom" -> "2020-03-03",
        "periodTo" -> "2020-04-04",
        "chargeType" -> "VAT Return 1st LPP",
        "chargeReference" -> "CHARGEREF",
        "isOverdue" -> "false"
      )) shouldBe Left(List(FormError("amountReceived", List("error.real"), List())))
    }
    "the leftToPay field is invalid" in {
      form.mapping.bind(Map(
        "numberOfDays" -> "99",
        "part1Days" -> "10",
        "part2Days" -> "20",
        "part1PenaltyRate" -> "2.4",
        "part2PenaltyRate" -> "2.6",
        "part1UnpaidVAT" -> "111.11",
        "part2UnpaidVAT" -> "222.22",
        "dueDate" -> "2020-01-01",
        "penaltyAmount" -> "500.55",
        "amountReceived" -> "100.11",
        "leftToPay" -> "100.11.0",
        "periodFrom" -> "2020-03-03",
        "periodTo" -> "2020-04-04",
        "chargeType" -> "VAT Return 1st LPP",
        "chargeReference" -> "CHARGEREF",
        "isOverdue" -> "false"
      )) shouldBe Left(List(FormError("leftToPay", List("error.real"), List())))
    }
    "isOverdue field is invalid" in {
      form.mapping.bind(Map(
        "numberOfDays" -> "99",
        "part1Days" -> "10",
        "part2Days" -> "20",
        "part1PenaltyRate" -> "2.4",
        "part2PenaltyRate" -> "2.6",
        "part1UnpaidVAT" -> "111.11",
        "part2UnpaidVAT" -> "222.22",
        "dueDate" -> "2020-01-01",
        "penaltyAmount" -> "500.55",
        "amountReceived" -> "100.11",
        "leftToPay" -> "400.44",
        "periodFrom" -> "2020-03-03",
        "periodTo" -> "2020-04-04",
        "chargeType" -> "VAT Return 1st LPP",
        "chargeReference" -> "CHARGEREF",
        "isOverdue" -> "12.0"
      )) shouldBe Left(List(FormError("isOverdue", List("error.boolean"), List())))
    }
  }

  "The CrystallisedLPP1ViewModel" should {

    "read from JSON" when {

      "all fields are populated" in {
        crystallisedLPP1JsonMax.as[CrystallisedLPP1ViewModel] shouldBe crystallisedLPP1Model
      }

      "optional fields are missing" in {
        crystallisedLPP1JsonMin.as[CrystallisedLPP1ViewModel] shouldBe crystallisedLPP1ModelMin
      }
    }

    "write to JSON" when {

      "all fields are populated" in {
        Json.toJson(crystallisedLPP1Model) shouldBe crystallisedLPP1JsonMax
      }

      "optional fields are missing" in {
        Json.toJson(crystallisedLPP1ModelMin) shouldBe crystallisedLPP1JsonMin
      }
    }
  }
}
