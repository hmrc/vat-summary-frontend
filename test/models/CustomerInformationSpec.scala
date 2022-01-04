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

package models

import common.TestJson.{customerInfoJsonMax, customerInfoJsonMin}
import common.TestModels.{customerInformationMax, customerInformationMin}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

class CustomerInformationSpec extends AnyWordSpecLike with Matchers  {

  "A CustomerInformation object" when {

    "all available fields can be found in the JSON" should {

      "parse to a model" in {
        val result: CustomerInformation = customerInfoJsonMax.as[CustomerInformation]
        result shouldBe customerInformationMax
      }
    }

    "the minimum amount of available fields can be found in the JSON" should {

      "parse to a model" in {
        val result: CustomerInformation = customerInfoJsonMin.as[CustomerInformation]
        result shouldBe customerInformationMin
      }
    }
  }

  ".extractDate" when {

    "hybridToFullMigrationDate is available" should {

      "return the date from hybridToFullMigrationDate" in {
        val customerInfo = customerInformationMax
        customerInfo.extractDate shouldBe customerInfo.hybridToFullMigrationDate
      }
    }

    "hybridToFullMigrationDate is missing" should {

      "return customerMigratedToETMPDate" in {
        val customerInfo = customerInformationMax.copy(hybridToFullMigrationDate = None)
        customerInfo.extractDate shouldBe customerInfo.customerMigratedToETMPDate
      }
    }

    "hybridToFullMigrationDate and customerMigratedToETMPDate are unavailable" should {

      "return customerMigratedToETMPDate" in {
        val customerInfo = customerInformationMax.copy(customerMigratedToETMPDate = None, hybridToFullMigrationDate = None)
        customerInfo.extractDate shouldBe customerInfo.customerMigratedToETMPDate
      }
    }
  }

  "Calling .partyTypeMessageKey" when {

    "the model contains a partyType" should {

      "return the message key for that party type" in {
        customerInformationMax.partyTypeMessageKey shouldBe "partyType.7"
      }

      "the model does not contain a partyType" should {

        "return the message key for 'Not provided'" in {
          customerInformationMin.partyTypeMessageKey shouldBe "common.notProvided"
        }
      }
    }
  }

  "Calling .returnPeriodMessageKey" when {

    "the return period is a standard quarterly stagger of 'MM'" should {

      "return the message key for monthly stagger" in {
        customerInformationMax.copy(returnPeriod = Some("MM")).returnPeriodMessageKey shouldBe "returnPeriod.MM"
      }
    }

    "the return period is a standard quarterly stagger of 'MA'" should {

      "return the message key for the quarterly stagger type A" in {
        customerInformationMax.copy(returnPeriod = Some("MA")).returnPeriodMessageKey shouldBe "returnPeriod.MA"
      }
    }

    "the return period is a standard quarterly stagger of 'MB'" should {

      "return the message key for the quarterly stagger type B" in {
        customerInformationMax.copy(returnPeriod = Some("MB")).returnPeriodMessageKey shouldBe "returnPeriod.MB"
      }
    }

    "the return period is a standard quarterly stagger of 'MC'" should {

      "return the message key for the quarterly stagger type C" in {
        customerInformationMax.copy(returnPeriod = Some("MC")).returnPeriodMessageKey shouldBe "returnPeriod.MC"
      }
    }

    "the return period is non standard" should {

      "return the message key for 'Non-standard'" in {
        customerInformationMax.copy(returnPeriod = Some("MZ")).returnPeriodMessageKey shouldBe "returnPeriod.nonStandard"
      }
    }

    "the model does not contain a returnPeriod" should {

      "return the message key for 'Not provided'" in {
        customerInformationMin.returnPeriodMessageKey shouldBe "common.notProvided"
      }
    }
  }
}
