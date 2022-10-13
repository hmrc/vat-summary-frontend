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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import models.penalties.{LPPDetails, PenaltyDetails}
import play.api.http.Status
import play.api.libs.json.{JsObject, JsValue, Json}

object PenaltyDetailsStub extends WireMockMethods{

  private val penaltyDetailsServiceUrl: String = "/penalty/VAT/([0-9]+)"

  def stubPenaltyDetails(status: Int = Status.OK, response: JsValue = penaltyDetailsJsonMax): StubMapping = {
    when(method = GET, uri = penaltyDetailsServiceUrl)
      .thenReturn(status = status, body = response)
  }

  val estimatedLPP1DetailsModel: LPPDetails = LPPDetails(
    principalChargeReference = "XD002750002156",
    penaltyCategory = "LPP1",
    Some(100.11),
    Some("15"),
    Some(2.4),
    Some(200.22),
    Some("30"),
    Some(4.2),
    Some("31"),
    Some(5.5),
    penaltyChargeReference = None
  )

  val estimatedLPP2DetailsModel: LPPDetails = estimatedLPP1DetailsModel.copy(
    principalChargeReference = "XD002750002157",
    penaltyCategory = "LPP2"
  )

  val crystallisedLPP1DetailsModel: LPPDetails = estimatedLPP1DetailsModel.copy(
    principalChargeReference = "XXXXXXXXXXXXXXXX",
    penaltyChargeReference = Some("XD002750002158")
  )

  val crystallisedLPP2DetailsModel: LPPDetails = estimatedLPP1DetailsModel.copy(
    principalChargeReference = "XXXXXXXXXXXXXXXX",
    penaltyChargeReference = Some("XD002750002159"),
    penaltyCategory = "LPP2"
  )

  val penaltyDetailsModelMax: PenaltyDetails = PenaltyDetails(
    LPPDetails = Seq(estimatedLPP1DetailsModel, estimatedLPP2DetailsModel, crystallisedLPP1DetailsModel, crystallisedLPP2DetailsModel)
  )

  val estimatedLPP1PenDetails: JsObject = Json.obj(
    "principalChargeReference" -> "XD002750002156",
    "penaltyCategory" -> "LPP1",
    "LPP1LRCalculationAmount" -> 100.11,
    "LPP1LRDays" -> "15",
    "LPP1LRPercentage" -> 2.4,
    "LPP1HRCalculationAmount" -> 200.22,
    "LPP1HRDays" -> "30",
    "LPP1HRPercentage" -> 4.2,
    "LPP2Days" -> "31",
    "LPP2Percentage" -> 5.5
  )

  val estimatedLPP2PenDetails: JsObject = Json.obj(
    "principalChargeReference" -> "XD002750002157",
    "penaltyCategory" -> "LPP2",
    "LPP1LRCalculationAmount" -> 100.11,
    "LPP1LRDays" -> "15",
    "LPP1LRPercentage" -> 2.4,
    "LPP1HRCalculationAmount" -> 200.22,
    "LPP1HRDays" -> "30",
    "LPP1HRPercentage" -> 4.2,
    "LPP2Days" -> "31",
    "LPP2Percentage" -> 5.5
  )

  val crystallisedLPP1Details: JsObject = Json.obj(
    "principalChargeReference" -> "XXXXXXXXXXXXXXXX",
    "penaltyCategory" -> "LPP1",
    "LPP1LRCalculationAmount" -> 100.11,
    "LPP1LRDays" -> "15",
    "LPP1LRPercentage" -> 2.4,
    "LPP1HRCalculationAmount" -> 200.22,
    "LPP1HRDays" -> "30",
    "LPP1HRPercentage" -> 4.2,
    "LPP2Days" -> "31",
    "LPP2Percentage" -> 5.5,
    "penaltyChargeReference" -> "XD002750002158"
  )

  val crystallisedLPP2Details: JsObject = Json.obj(
    "principalChargeReference" -> "XXXXXXXXXXXXXXXX",
    "penaltyCategory" -> "LPP2",
    "LPP1LRCalculationAmount" -> 100.11,
    "LPP1LRDays" -> "15",
    "LPP1LRPercentage" -> 2.4,
    "LPP1HRCalculationAmount" -> 200.22,
    "LPP1HRDays" -> "30",
    "LPP1HRPercentage" -> 4.2,
    "LPP2Days" -> "31",
    "LPP2Percentage" -> 5.5,
    "penaltyChargeReference" -> "XD002750002159"
  )

  val penaltyDetailsJsonMax : JsObject = Json.obj(
    "LPPDetails" -> Json.arr(estimatedLPP1PenDetails, estimatedLPP2PenDetails, crystallisedLPP1Details, crystallisedLPP2Details)
  )

  val errorJson: JsObject = Json.obj(
    "code" -> "500",
    "message" -> "INTERNAL_SERVER_ERROR"
  )
}
