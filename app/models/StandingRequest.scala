/*
 * Copyright 2025 HM Revenue & Customs
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

package models

import play.api.libs.json._

import java.time.LocalDate
import java.time.format.DateTimeFormatter

case class RequestItem(
  period: String,
  periodKey: String,
  startDate: String,  
  endDate: String,
  dueDate: String,
  amount: BigDecimal,
  chargeReference: Option[String],
  postingDueDate: Option[String] 
)

object RequestItem {
  implicit val format: Format[RequestItem] = Json.format[RequestItem]
}

case class StandingRequestDetail(
  requestNumber: String,
  requestCategory: String,
  createdOn: String,
  changedOn: Option[String],
  requestItems: List[RequestItem]
)

object StandingRequestDetail {
  implicit val format: Format[StandingRequestDetail] = Json.format[StandingRequestDetail]
}

case class StandingRequest(
  processingDate: String,  
  standingRequests: List[StandingRequestDetail]
)

object StandingRequest {
  implicit val format: Format[StandingRequest] = Json.format[StandingRequest]
}

case class ChangedOnVatPeriod(startDate: Option[LocalDate], endDate: Option[LocalDate], isCurrent: Boolean)

object ChangedOnVatPeriod {
  val RequestCategoryType3 = "3"
  val dateFormatter = DateTimeFormatter.ofPattern("d MMM uuuu")
}