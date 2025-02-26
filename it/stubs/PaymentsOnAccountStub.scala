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
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}

object PaymentsOnAccountStub extends WireMockMethods {

  private val standingRequestsUri: String = "/RESTAdapter/VATC/standing-requests/VRN/555555555"

  def stubStandingRequests(response: JsValue, status: Int = OK): StubMapping = {
    when(method = GET, uri = standingRequestsUri)
      .thenReturn(status = status, body = response)
  }

  def stubErrorFromApi(): StubMapping = {
    println("Returning stub error")
    stubStandingRequests(errorJson, INTERNAL_SERVER_ERROR)
  }

  val futurePOAJson: JsValue = Json.parse("""
    {
      "processingDate": "2025-04-05T09:30:47Z",
      "standingRequests": [
        {
          "requestNumber": "20000037272",
          "requestCategory": "3",
          "createdOn": "2025-04-05",
          "changedOn": "2025-04-05",
          "requestItems": [
            {
              "period": "1",
              "periodKey": "25A2",
              "startDate": "2025-04-01",
              "endDate": "2025-06-30",
              "dueDate": "2025-05-31",
              "amount": 22945.23,
              "chargeReference": "XD006411191344",
              "postingDueDate": "2025-05-31"
            }
          ]
        }
      ]
    }
  """)

  val poaJson: JsValue = Json.parse("""
    {
      "processingDate": "2025-02-15T09:30:47Z",
      "standingRequests": [
        {
          "requestNumber": "20000037272",
          "requestCategory": "3",
          "createdOn": "2023-11-30",
          "changedOn": "2024-12-26",
          "requestItems": [
            {
              "period": "1",
              "periodKey": "24A1",
              "startDate": "2024-02-01",
              "endDate": "2024-04-30",
              "dueDate": "2024-03-31",
              "amount": 22945.23,
              "chargeReference": "XD006411191344",
              "postingDueDate": "2024-03-31"
            }
          ]
        }
      ]
    }
  """)

  val todaysPOAJson: JsValue = Json.parse("""
    {
      "processingDate": "2025-03-17T09:30:47Z",
      "standingRequests": [
        {
          "requestNumber": "20000037272",
          "requestCategory": "3",
          "createdOn": "2025-01-05",
          "changedOn": "2025-02-26",
          "requestItems": [
            {
              "period": "1",
              "periodKey": "25A1",
              "startDate": "2025-01-01",
              "endDate": "2025-03-31",
              "dueDate": "2025-03-31",
              "amount": 22945.23,
              "chargeReference": "XD006411191344",
              "postingDueDate": "2025-03-31"
            }
          ]
        }
      ]
    }
  """)

  val errorJson: JsValue = Json.parse("""
    {
      "code": "500",
      "message": "INTERNAL_SERVER_ERROR"
    }
  """)

  val oneOpenRequestJson: JsValue = Json.parse("""
    {
      "processingDate": "2025-03-17T09:30:47Z",
      "standingRequests": [
        {
          "requestNumber": "20000037272",
          "requestCategory": "3",
          "createdOn": "2025-01-05",
          "changedOn": "2025-02-26",
          "requestItems": [
            {
              "period": "1",
              "periodKey": "25A1",
              "startDate": "2025-01-01",
              "endDate": "2025-03-31",
              "dueDate": "2025-03-31",
              "amount": 22945.23,
              "chargeReference": "XD006411191344",
              "postingDueDate": "2025-03-31"
            }
          ]
        }
      ]
    }
  """)

  val emptyStandingRequestJson: JsValue = Json.parse("""
    {
      "processingDate": "2025-03-17T09:30:47Z",
      "standingRequests": []
    }
  """)
}
