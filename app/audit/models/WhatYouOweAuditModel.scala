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

package audit.models

import helpers.JsonObjectSugar
import models.viewModels.{ChargeDetailsViewModel, CrystallisedViewModel, EstimatedViewModel, StandardChargeViewModel}
import play.api.libs.json.{JsValue, Json}

case class WhatYouOweAuditModel(vrn: String,
                                arn: Option[String],
                                charges: Seq[ChargeDetailsViewModel]) extends ExtendedAuditModel with JsonObjectSugar {
  override val transactionName: String = "view-outstanding-vat-payments"
  override val auditType: String = "CheckWhatYouOwePageView"
  override val detail: JsValue = jsonObjNoNulls(
    "vrn" -> vrn,
    "arn" -> arn,
    "isAgent" -> arn.isDefined,
    "outstandingPayments" -> charges.map(constructAuditJson)
  )

  private def constructAuditJson(charge: ChargeDetailsViewModel) = {

    val mandatoryDetail = Json.obj(
      "paymentType" -> charge.chargeType,
      "amount" -> charge.outstandingAmount
    )

    val specificDetail = charge match {
      case model: StandardChargeViewModel => jsonObjNoNulls(
        "due" -> model.dueDate,
        "periodStartDate" -> model.periodFrom,
        "periodEndDate" -> model.periodTo,
        "periodKey" -> model.periodKey,
        "overdue" -> model.isOverdue
      )
      case model: EstimatedViewModel => Json.obj(
        "periodStartDate" -> model.periodFrom,
        "periodEndDate" -> model.periodTo,
        "overdue" -> false
      )
      case model: CrystallisedViewModel => Json.obj(
        "due" -> model.dueDate,
        "periodStartDate" -> model.periodFrom,
        "periodEndDate" -> model.periodTo,
        "overdue" -> model.isOverdue
      )
      case _ => Json.obj()
    }

    mandatoryDetail ++ specificDetail
  }
}
