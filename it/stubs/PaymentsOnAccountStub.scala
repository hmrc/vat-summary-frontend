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

  private val standingRequestsUri: String = "/vat-subscription/([0-9]+)/standing-requests"

  def stubStandingRequests(response: JsValue, status: Int = OK): StubMapping = {
    when(method = GET, uri = standingRequestsUri)
      .thenReturn(status = status, body = response)
  }

  def stubErrorFromApi(): StubMapping = {
    stubStandingRequests(errorJson, INTERNAL_SERVER_ERROR)
  }

  val poaJson: JsValue = Json.parse("""
  {
    "processingDate": "2024-07-15T09:30:47Z",
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
          },
          {
            "period": "2",
            "periodKey": "24A1",
            "startDate": "2024-02-01",
            "endDate": "2024-04-30",
            "dueDate": "2024-04-30",
            "amount": 22945.23,
            "chargeReference": "XD006411191345",
            "postingDueDate": "2024-04-30"
          },
          {
            "period": "3",
            "periodKey": "24A2",
            "startDate": "2024-05-01",
            "endDate": "2024-07-31",
            "dueDate": "2024-06-30",
            "amount": 22945.23,
            "chargeReference": "XD006411191346",
            "postingDueDate": "2024-06-30"
          },
          {
            "period": "4",
            "periodKey": "24A2",
            "startDate": "2024-05-01",
            "endDate": "2024-07-31",
            "dueDate": "2024-07-31",
            "amount": 22945.23,
            "chargeReference": "XD006411191347",
            "postingDueDate": "2024-07-31"
          },
          {
            "period": "5",
            "periodKey": "24A3",
            "startDate": "2024-08-01",
            "endDate": "2024-10-31",
            "dueDate": "2024-09-30",
            "amount": 22945.23,
            "chargeReference": "XD006411191348",
            "postingDueDate": "2024-09-30"
          },
          {
            "period": "6",
            "periodKey": "24A3",
            "startDate": "2024-08-01",
            "endDate": "2024-10-31",
            "dueDate": "2024-10-31",
            "amount": 22945.23,
            "chargeReference": "XD006411191349",
            "postingDueDate": "2024-10-31"
          },
          {
            "period": "7",
            "periodKey": "24A4",
            "startDate": "2024-11-01",
            "endDate": "2025-01-31",
            "dueDate": "2024-12-31",
            "amount": 22945.23,
            "chargeReference": "XD006411191350",
            "postingDueDate": "2024-12-31"
          },
          {	"period": "8",
            "periodKey": "24A4",
            "startDate": "2024-11-01",
            "endDate": "2025-01-31",
            "dueDate": "2025-01-31",
            "amount": 22945.23,
            "chargeReference": "XD006411191351",
            "postingDueDate": "2025-01-31"
          }

        ]
      },
      {
        "requestNumber": "20000037277",
        "requestCategory": "3",
        "createdOn": "2024-11-30",
        "changedOn": "2025-01-26",
        "requestItems": [
          {
            "period": "1",
            "periodKey": "25A1",
            "startDate": "2025-02-01",
            "endDate": "2025-04-30",
            "dueDate": "2025-03-31",
            "amount": 122945.23
          },
          {
            "period": "2",
            "periodKey": "25A1",
            "startDate": "2025-02-01",
            "endDate": "2025-04-30",
            "dueDate": "2025-04-30",
            "amount": 122945.23
          },
          {
            "period": "3",
            "periodKey": "25A2",
            "startDate": "2025-05-01",
            "endDate": "2025-07-31",
            "dueDate": "2025-06-30",
            "amount": 122945.23
          },
          {
            "period": "4",
            "periodKey": "25A2",
            "startDate": "2025-05-01",
            "endDate": "2025-07-31",
            "dueDate": "2025-07-31",
            "amount": 122945.23
          },
          {
            "period": "5",
            "periodKey": "25A3",
            "startDate": "2025-08-01",
            "endDate": "2025-10-31",
            "dueDate": "2025-09-30",
            "amount": 122945.23
          },
          {
            "period": "6",
            "periodKey": "25A3",
            "startDate": "2025-08-01",
            "endDate": "2025-10-31",
            "dueDate": "2025-10-31",
            "amount": 122945.23
          },
          {
            "period": "7",
            "periodKey": "25A4",
            "startDate": "2025-11-01",
            "endDate": "2026-01-31",
            "dueDate": "2025-12-31",
            "amount": 122945.23
          },
          {
            "period": "8",
            "periodKey": "25A4",
            "startDate": "2025-11-01",
            "endDate": "2026-01-31",
            "dueDate": "2026-01-31",
            "amount": 122945.23
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

  val emptyStandingRequestJson: JsValue = Json.parse("""
    {
      "processingDate": "2025-03-17T09:30:47Z",
      "standingRequests": []
    }
  """)
}
