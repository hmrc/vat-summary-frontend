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

  private trait Test {
    val firstName: Option[String] = Some("Pepsi")
    val secondName: Option[String] = Some("Mac")
    val tradingName: Option[String] = Some("Cheapo Clothing")
    val organisationName: Option[String] = Some("Cheapo Clothing Ltd")
    val dummyAddress = Address("","",None,None,None)

    lazy val customerInfo: CustomerInformation = CustomerInformation(
      organisationName,
      firstName,
      secondName,
      tradingName,
      dummyAddress,
      None,
      None,
      None,
      dummyAddress,
      None,
      None,
      None
    )
  }

  "A CustomerInformation object" should {

    val exampleInputString =
      """{
        |"organisationName":"Cheapo Clothing Ltd",
        |"firstName":"Pepsi",
        |"lastName":"Mac",
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

    "be parsed from appropriate JSON" in new Test {
      val result: CustomerInformation = Json.parse(exampleInputString).as[CustomerInformation]
      result shouldBe customerInfo
    }
  }

  "Calling .entityName" when {

    "the model contains a trading name" should {

      "return the trading name" in new Test {
        val result: Option[String] = customerInfo.entityName
        result shouldBe Some("Cheapo Clothing")
      }
    }

    "the model does not contain a trading name or organisation name" should {

      "return the first and last name" in new Test {
        override val organisationName: Option[String] = None
        override val tradingName: Option[String] = None
        val result: Option[String] = customerInfo.entityName
        result shouldBe Some("Pepsi Mac")
      }
    }

    "the model does not contain a trading name, first name or last name" should {

      "return the organisation name" in new Test {
        override val firstName: Option[String] = None
        override val secondName: Option[String] = None
        override val tradingName: Option[String] = None
        val result: Option[String] = customerInfo.entityName
        result shouldBe Some("Cheapo Clothing Ltd")
      }
    }

    "the model does not contains a trading name, organisation name, or individual names" should {

      "return None" in new Test {
        override val firstName: Option[String] = None
        override val secondName: Option[String] = None
        override val tradingName: Option[String] = None
        override val organisationName: Option[String] = None
        val result: Option[String] = customerInfo.entityName
        result shouldBe None
      }
    }
  }
}
