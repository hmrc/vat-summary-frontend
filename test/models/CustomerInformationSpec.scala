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

package models

import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec

class CustomerInformationSpec extends UnitSpec {

  "A CustomerInformation object" should {

    val dummyAddress =Address("","",None,None,None)

    val exampleCustomerInfo: CustomerInformation = CustomerInformation(
      Some("Cheapo Clothing Ltd"),
      Some("Betty"),
      Some("Jones"),
      Some("Cheapo Clothing"),
      dummyAddress,
      None,
      None,
      None,
      dummyAddress,
      None,
      None,
      None
    )

    val exampleInputString =
      """{
        |"organisationName":"Cheapo Clothing Ltd",
        |"firstName":"Betty",
        |"lastName":"Jones",
        |"tradingName":"Cheapo Clothing",
        |"PPOB":{
        |  "address":{
        |    "line1":"Bedrock Quarry",
        |    "line2":"Bedrock",
        |    "line3":"Graveldon",
        |    "line4":"Graveldon",
        |    "postCode":"GV2 4BB"
        |  },
        |  "contactDetails":{
        |    "primaryPhoneNumber":"01632 982028",
        |    "mobileNumber":"07700 900018",
        |    "emailAddress":"bettylucknexttime@gmail.com"
        |  }
        |},
        |"correspondenceContactDetails":{
        |  "address":{
        |    "line1":"13 Pebble Lane",
        |    "line2":"Bedrock",
        |    "line3":"Graveldon",
        |    "line4":"Graveldon",
        |    "postCode":"GV13 4BJ"
        |  },
        |  "contactDetails":{
        |    "primaryPhoneNumber":"01632 960026",
        |    "mobileNumber":"07700 900018",
        |    "emailAddress":"bettylucknexttime@gmail.com"
        |  }
        |}
        |}"""
        .stripMargin.replace("\n", "")

    "be parsed from appropriate JSON" in {
      val result = Json.parse(exampleInputString).as[CustomerInformation]
      result shouldBe exampleCustomerInfo
    }
  }
}
