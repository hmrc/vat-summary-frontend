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

package connectors.httpParsers

import connectors.httpParsers.ResponseHttpParsers.HttpGetResult
import models.errors.{ServerSideError, UnexpectedStatusError}
import models.payments._
import models.viewModels.PaymentsHistoryModel
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND, OK}
import play.api.libs.json.{JsArray, JsValue, Json}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object PaymentsHistoryHttpParser extends ResponseHttpParsers {

  implicit object PaymentsHistoryReads extends HttpReads[HttpGetResult[Seq[PaymentsHistoryModel]]] {
    override def read(method: String, url: String, response: HttpResponse): HttpGetResult[Seq[PaymentsHistoryModel]] = {
      response.status match {
        case OK => Right(removeNonVatReturnCharges(response.json).as[Seq[PaymentsHistoryModel]])
        case NOT_FOUND =>
          Right(Seq.empty[PaymentsHistoryModel])
        case BAD_REQUEST => handleBadRequest(response.json)
        case status if status >= 500 && status < 600 => Left(ServerSideError(response.status.toString, response.body))
        case _ => Left(UnexpectedStatusError(response.status.toString, response.body))
      }
    }
  }

  private def removeNonVatReturnCharges(json: JsValue): JsValue = {

    val charges: Seq[JsValue] = (json \ "financialTransactions").as[JsArray].value

    val vatReturnCharges = charges.filter { charge =>
      val chargeType: String = (charge \ "chargeType").as[String]
      ChargeType.isValidChargeType(chargeType)
    }
    Json.obj("financialTransactions" -> JsArray(vatReturnCharges))
  }
}
