/*
 * Copyright 2018 HM Revenue & Customs
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
import play.api.libs.json.Json

object CustomerInfoStub extends WireMockMethods {

  private val customerInfoUri = "/customer-information/vat/([0-9]+)"

  def stubCustomerInfo: StubMapping = {
    when(method = GET, uri = customerInfoUri)
      .thenReturn(status = OK, body = customerInfo)
  }

  def stubErrorFromApi: StubMapping = {
    when(method = GET, uri = customerInfoUri)
      .thenReturn(status = INTERNAL_SERVER_ERROR, body = errorJson)
  }

  private val customerInfo = Json.parse(
    """{
      |  "organisationName" : "Cheapo Clothing Ltd",
      |  "title" : "0001",
      |  "firstName" : "Betty",
      |  "middleName" : "Vatreturn",
      |  "lastName" : "Jones",
      |  "tradingName" : "Cheapo Clothing",
      |  "mandationStatus" : "1",
      |  "registrationReason" : "0001",
      |  "effectiveRegistrationDate" : "2017-08-21",
      |  "businessStartDate" : "2017-01-01",
      |  "PPOB":{
      |    "address":{
      |      "line1":"Bedrock Quarry",
      |      "line2":"Bedrock",
      |      "line3":"Graveldon",
      |      "line4":"Graveldon",
      |      "postCode":"GV2 4BB"
      |    },
      |    "contactDetails":{
      |      "primaryPhoneNumber":"01632 982028",
      |      "mobileNumber":"07700 900018",
      |      "emailAddress":"bettylucknexttime@gmail.com"
      |    }
      |  },
      |  "correspondenceContactDetails":{
      |    "address":{
      |      "line1":"13 Pebble Lane",
      |      "line2":"Bedrock",
      |      "line3":"Graveldon",
      |      "line4":"Graveldon",
      |      "postCode":"GV13 4BJ"
      |    },
      |    "contactDetails":{
      |      "primaryPhoneNumber":"01632 960026",
      |      "mobileNumber":"07700 900018",
      |      "emailAddress":"bettylucknexttime@gmail.com"
      |    }
      |  }
      |}""".stripMargin
  )

  private val errorJson = Json.obj(
    "code" -> "500",
    "message" -> "INTERNAL_SERVER_ERROR"
  )
}
