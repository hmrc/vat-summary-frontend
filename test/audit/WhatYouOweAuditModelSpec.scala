/*
 * Copyright 2023 HM Revenue & Customs
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

import audit.models.WhatYouOweAuditModel
import common.TestModels.{crystallisedPenaltyModel, estimatedInterestModel, standardChargeModelMin, whatYouOweChargeModel}
import _root_.models.viewModels.ChargeDetailsViewModel
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.i18n.Messages
import play.api.libs.json.Json

class WhatYouOweAuditModelSpec extends AnyWordSpecLike with Matchers {

  "WhatYouOweAuditModel" should {

    val exampleModel = WhatYouOweAuditModel("123456789", None, Seq())

    "have the correct transaction name" in {
      exampleModel.transactionName shouldBe "view-outstanding-vat-payments"
    }

    "have the correct audit type" in {
      exampleModel.auditType shouldBe "CheckWhatYouOwePageView"
    }

    "have the correct detail" when {

      "the charge model is a StandardChargeViewModel with maximum fields" in {
        val model = exampleModel.copy(charges = Seq(whatYouOweChargeModel))
        val expectedDetail = Json.obj(
          "vrn" -> model.vrn,
          "isAgent" -> false,
          "outstandingPayments" -> Json.arr(Json.obj(
            "paymentType" -> whatYouOweChargeModel.chargeType,
            "amount" -> whatYouOweChargeModel.outstandingAmount,
            "due" -> whatYouOweChargeModel.dueDate,
            "periodStartDate" -> whatYouOweChargeModel.periodFrom,
            "periodEndDate" -> whatYouOweChargeModel.periodTo,
            "periodKey" -> whatYouOweChargeModel.periodKey,
            "overdue" -> whatYouOweChargeModel.isOverdue
          ))
        )

        model.detail shouldBe expectedDetail
      }

      "the charge model is a StandardChargeViewModel with minimum fields" in {
        val model = exampleModel.copy(charges = Seq(standardChargeModelMin))
        val expectedDetail = Json.obj(
          "vrn" -> model.vrn,
          "isAgent" -> false,
          "outstandingPayments" -> Json.arr(Json.obj(
            "paymentType" -> standardChargeModelMin.chargeType,
            "amount" -> standardChargeModelMin.outstandingAmount,
            "due" -> standardChargeModelMin.dueDate,
            "overdue" -> standardChargeModelMin.isOverdue
          ))
        )

        model.detail shouldBe expectedDetail
      }

      "the charge model is an EstimatedViewModel" in {
        val model = exampleModel.copy(charges = Seq(estimatedInterestModel))
        val expectedDetail = Json.obj(
          "vrn" -> model.vrn,
          "isAgent" -> false,
          "outstandingPayments" -> Json.arr(Json.obj(
            "paymentType" -> estimatedInterestModel.chargeType,
            "amount" -> estimatedInterestModel.outstandingAmount,
            "periodStartDate" -> estimatedInterestModel.periodFrom,
            "periodEndDate" -> estimatedInterestModel.periodTo,
            "overdue" -> false
          ))
        )

        model.detail shouldBe expectedDetail
      }

      "the charge model is a CrystallisedViewModel" in {
        val model = exampleModel.copy(charges = Seq(crystallisedPenaltyModel))
        val expectedDetail = Json.obj(
          "vrn" -> model.vrn,
          "isAgent" -> false,
          "outstandingPayments" -> Json.arr(Json.obj(
            "paymentType" -> crystallisedPenaltyModel.chargeType,
            "amount" -> crystallisedPenaltyModel.outstandingAmount,
            "due" -> crystallisedPenaltyModel.dueDate,
            "periodStartDate" -> crystallisedPenaltyModel.periodFrom,
            "periodEndDate" -> crystallisedPenaltyModel.periodTo,
            "overdue" -> crystallisedPenaltyModel.isOverdue
          ))
        )

        model.detail shouldBe expectedDetail
      }

      "the charge model is not recognised" in {
        object UnknownCharge extends ChargeDetailsViewModel {
          override val chargeType: String = "Unknown"
          override val outstandingAmount: BigDecimal = 1
          override def description(isAgent: Boolean)(implicit messages: Messages): String = ""
        }

        val model = exampleModel.copy(charges = Seq(UnknownCharge))
        val expectedDetail = Json.obj(
          "vrn" -> model.vrn,
          "isAgent" -> false,
          "outstandingPayments" -> Json.arr(Json.obj(
            "paymentType" -> UnknownCharge.chargeType,
            "amount" -> UnknownCharge.outstandingAmount
          ))
        )

        model.detail shouldBe expectedDetail
      }

      "there are no charge models (no payments due)" in {
        val expectedDetail = Json.obj(
          "vrn" -> exampleModel.vrn,
          "isAgent" -> false,
          "outstandingPayments" -> Json.arr()
        )

        exampleModel.detail shouldBe expectedDetail
      }

      "there is an ARN (agent user)" in {
        val model = exampleModel.copy(arn = Some("XARN1234567"))
        val expectedDetail = Json.obj(
          "vrn" -> model.vrn,
          "arn" -> model.arn,
          "isAgent" -> true,
          "outstandingPayments" -> Json.arr()
        )

        model.detail shouldBe expectedDetail
      }
    }
  }
}
