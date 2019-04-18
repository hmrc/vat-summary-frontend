/*
 * Copyright 2019 HM Revenue & Customs
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

import common.TestModels.{customerInformation, customerInformationNoEntityName}
import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec

class CustomerInformationSpec extends UnitSpec {

  "A CustomerInformation object" should {

    val exampleInputString =
      """{
        |"organisationName":"Cheapo Clothing Ltd",
        |"firstName":"Betty",
        |"lastName":"Jones",
        |"tradingName":"Cheapo Clothing",
        |"isPartialMigration": false,
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
      val result: CustomerInformation = Json.parse(exampleInputString).as[CustomerInformation]
      result shouldBe customerInformation
    }
  }

  "A CustomerInformation object with no isPartialMigration value" should {

    val exampleJson = Json.obj(
      "organisationName" -> "Cheapo Clothing Ltd",
      "firstName" -> "Betty",
      "lastName" -> "Jones",
      "tradingName" -> "Cheapo Clothing"
    )

    "be parsed from appropriate JSON" in {
      exampleJson.as[CustomerInformation] shouldBe customerInformation
    }
  }

  "Calling .entityName" when {

    "the model contains a trading name" should {

      "return the trading name" in {
        val result: Option[String] = customerInformation.entityName
        result shouldBe Some("Cheapo Clothing")
      }
    }

    "the model does not contain a trading name or organisation name" should {

      "return the first and last name" in {
        val customerInfoSpecific = customerInformation.copy(tradingName = None, organisationName = None)
        val result: Option[String] = customerInfoSpecific.entityName
        result shouldBe Some("Betty Jones")
      }
    }

    "the model does not contain a trading name, first name or last name" should {

      "return the organisation name" in {
        val customerInfoSpecific = customerInformation.copy(tradingName = None, firstName = None, lastName = None)
        val result: Option[String] = customerInfoSpecific.entityName
        result shouldBe Some("Cheapo Clothing Ltd")
      }
    }

    "the model does not contains a trading name, organisation name, or individual names" should {

      "return None" in {
        val result: Option[String] = customerInformationNoEntityName.entityName
        result shouldBe None
      }
    }
  }
}
