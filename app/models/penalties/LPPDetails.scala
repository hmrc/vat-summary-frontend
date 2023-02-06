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

package models.penalties

import play.api.libs.json.{Reads, Json}

case class LPPDetails(principalChargeReference: String,
                      penaltyCategory: String,
                      LPP1LRCalculationAmount: Option[BigDecimal],
                      LPP1LRDays: Option[String],
                      LPP1LRPercentage: Option[Double],
                      LPP1HRCalculationAmount: Option[BigDecimal],
                      LPP1HRDays: Option[String],
                      LPP1HRPercentage: Option[Double],
                      LPP2Days: Option[String],
                      LPP2Percentage: Option[Double],
                      penaltyChargeReference: Option[String],
                      timeToPay: Boolean)

object LPPDetails {

  implicit val format: Reads[LPPDetails] = Json.reads[LPPDetails]

}
