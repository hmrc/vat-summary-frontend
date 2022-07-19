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

import common.TestModels.{estimatedInterestJson, estimatedInterestModel}
import models.viewModels.EstimatedInterestViewModel.form
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.data.FormError
import play.api.libs.json.Json

class EstimatedInterestViewModelSpec extends AnyWordSpecLike with Matchers {

  "The EstimatedInterestViewModel form" should {

    "bind successfully" when {

      "all values are provided and valid" in {
        form.mapping.bind(Map(
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "VAT Return Debit Charge",
          "interestRate" -> "2.6",
          "currentAmount" -> "300.33",
          "amountReceived" -> "200.22",
          "leftToPay" -> "100.11",
          "isPenalty" -> "false"
        )) shouldBe Right(estimatedInterestModel)
      }
    }

    "fail to bind" when {

      "a field is not provided" in {
        form.mapping.bind(Map(
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "interestRate" -> "2.6",
          "currentAmount" -> "300.33",
          "amountReceived" -> "200.22",
          "leftToPay" -> "100.11",
          "isPenalty" -> "false"
        )) shouldBe Left(List(FormError("chargeType", List("error.required"), List())))
      }

      "the periodFrom field is invalid" in {
        form.mapping.bind(Map(
          "periodFrom" -> "date",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "Example interest charge title",
          "interestRate" -> "2.6",
          "currentAmount" -> "300.33",
          "amountReceived" -> "200.22",
          "leftToPay" -> "100.11",
          "isPenalty" -> "false"
        )) shouldBe Left(List(FormError("periodFrom", List("error.date"), List())))
      }

      "the periodTo field is invalid" in {
        form.mapping.bind(Map(
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "date",
          "chargeType" -> "Example interest charge title",
          "interestRate" -> "2.6",
          "currentAmount" -> "300.33",
          "amountReceived" -> "200.22",
          "leftToPay" -> "100.11",
          "isPenalty" -> "false"
        )) shouldBe Left(List(FormError("periodTo", List("error.date"), List())))
      }

      "the interestRate field is invalid" in {
        form.mapping.bind(Map(
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "Example interest charge title",
          "interestRate" -> "rate",
          "currentAmount" -> "300.33",
          "amountReceived" -> "200.22",
          "leftToPay" -> "100.11",
          "isPenalty" -> "false"
        )) shouldBe Left(List(FormError("interestRate", List("error.real"), List())))
      }

      "the currentAmount field is invalid" in {
        form.mapping.bind(Map(
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "Example interest charge title",
          "interestRate" -> "2.6",
          "currentAmount" -> "amount",
          "amountReceived" -> "200.22",
          "leftToPay" -> "100.11",
          "isPenalty" -> "false"
        )) shouldBe Left(List(FormError("currentAmount", List("error.real"), List())))
      }

      "the amountReceived field is invalid" in {
        form.mapping.bind(Map(
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "Example interest charge title",
          "interestRate" -> "2.6",
          "currentAmount" -> "300.33",
          "amountReceived" -> "amount",
          "leftToPay" -> "100.11",
          "isPenalty" -> "false"
        )) shouldBe Left(List(FormError("amountReceived", List("error.real"), List())))
      }

      "the leftToPay field is invalid" in {
        form.mapping.bind(Map(
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "Example interest charge title",
          "interestRate" -> "2.6",
          "currentAmount" -> "300.33",
          "amountReceived" -> "200.22",
          "leftToPay" -> "left",
          "isPenalty" -> "false"
        )) shouldBe Left(List(FormError("leftToPay", List("error.real"), List())))
      }

      "the isPenalty field is invalid" in {
        form.mapping.bind(Map(
          "periodFrom" -> "2018-01-01",
          "periodTo" -> "2018-02-02",
          "chargeType" -> "Example interest charge title",
          "interestRate" -> "2.6",
          "currentAmount" -> "300.33",
          "amountReceived" -> "200.22",
          "leftToPay" -> "100.11",
          "isPenalty" -> "50"
        )) shouldBe Left(List(FormError("isPenalty", List("error.boolean"), List())))
      }
    }
  }

  "The EstimatedInterestViewModel" should {

    "read from JSON" in {
      estimatedInterestJson.as[EstimatedInterestViewModel] shouldBe estimatedInterestModel
    }

    "write to JSON" in {
      Json.toJson(estimatedInterestModel) shouldBe estimatedInterestJson
    }
  }
}
