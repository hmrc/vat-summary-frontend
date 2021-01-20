/*
 * Copyright 2021 HM Revenue & Customs
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

import common.TestJson.{customerDetailsJsonMax, customerDetailsJsonMin}
import common.TestModels.{customerDetailsInsolvent, customerDetailsMax, customerDetailsMin}
import uk.gov.hmrc.play.test.UnitSpec

class CustomerDetailsSpec extends UnitSpec {

  "A CustomerDetails object" when {

    "all available fields can be found in the JSON" should {

      "parse to a model" in {
        val result: CustomerDetails = customerDetailsJsonMax.as[CustomerDetails]
        result shouldBe customerDetailsMax
      }
    }

    "the minimum amount of available fields can be found in the JSON" should {

      "parse to a model" in {
        val result: CustomerDetails = customerDetailsJsonMin.as[CustomerDetails]
        result shouldBe customerDetailsMin
      }
    }
  }

  "Calling .entityName" when {

    "the model contains a trading name" should {

      "return the trading name" in {
        val result: Option[String] = customerDetailsMax.entityName
        result shouldBe Some("Cheapo Clothing")
      }
    }

    "the model does not contain a trading name or organisation name" should {

      "return the first and last name" in {
        val customerInfoSpecific = customerDetailsMax.copy(tradingName = None, organisationName = None)
        val result: Option[String] = customerInfoSpecific.entityName
        result shouldBe Some("Betty Jones")
      }
    }

    "the model does not contain a trading name, first name or last name" should {

      "return the organisation name" in {
        val customerInfoSpecific = customerDetailsMax.copy(tradingName = None, firstName = None, lastName = None)
        val result: Option[String] = customerInfoSpecific.entityName
        result shouldBe Some("Cheapo Clothing Ltd")
      }
    }

    "the model does not contains a trading name, organisation name, or individual names" should {

      "return None" in {
        val result: Option[String] = customerDetailsMin.entityName
        result shouldBe None
      }
    }
  }

  "calling .isInsolventWithoutAccess" should {

    "return true when the user is insolvent and not continuing to trade" in {
      customerDetailsInsolvent.isInsolventWithoutAccess shouldBe true
    }

    "return false when the user is insolvent but is continuing to trade" in {
      customerDetailsInsolvent.copy(continueToTrade = Some(true)).isInsolventWithoutAccess shouldBe false
    }

    "return false when the user is not insolvent, regardless of the continueToTrade flag" in {
      customerDetailsMax.isInsolventWithoutAccess shouldBe false
      customerDetailsMax.copy(continueToTrade = Some(false)).isInsolventWithoutAccess shouldBe false
      customerDetailsMax.copy(continueToTrade = None).isInsolventWithoutAccess shouldBe false
    }
  }
}
