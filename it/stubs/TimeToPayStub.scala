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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WireMockMethods
import play.api.libs.json.{JsObject, JsValue, Json}

object TimeToPayStub extends WireMockMethods {

  private val timeToPayUrl: String = "/essttp-backend/vat/vat-service/journey/start"

  def stubESSTTPBackend(status: Int, response: JsValue): StubMapping =
    when(method = POST, uri = timeToPayUrl).thenReturn(status = status, body = response)

  val timeToPayResponseJson: JsObject = Json.obj(
    "journeyId" -> "592d4a09cdc8e04b00021459",
    "nextUrl" -> "/time-to-pay"
  )
}
