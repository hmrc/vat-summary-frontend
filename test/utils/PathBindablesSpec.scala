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

package utils

import common.TestModels.chargeModel1
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class PathBindablesSpec extends AnyWordSpecLike with Matchers {

  "standardChargePathBinder" should {

    val minModel = chargeModel1.copy(
      clearedAmount = None, periodKey = None, chargeReference = None, periodTo = None, periodFrom = None
    )
    val maxUrl = "VAT Return Debit Charge+111.11+333.33+222.22+2018-03-01+18AA+true+ABCD+2018-01-01+2018-02-01"
    val minUrl = "VAT Return Debit Charge+111.11+333.33+0+2018-03-01+None+true+None+None+None"

    "successfully bind a URL to a StandardChargeViewModel" when {

      "all parameters are defined" in {
        PathBindables.standardChargePathBinder.bind("", maxUrl) shouldBe Right(chargeModel1)
      }

      "some parameters are not defined" in {
        PathBindables.standardChargePathBinder.bind("", minUrl) shouldBe Right(minModel)
      }
    }

    "fail to bind a URL to a StandardChargeViewModel" when {

      "there is an invalid value provided for a date parameter" in {
        val url = "VAT Return Debit Charge+111.11+333.33+0+2018-03-33+None+true+None+None+None"
        PathBindables.standardChargePathBinder.bind("", url) shouldBe
          Left("Failed to bind due to error: java.time.format.DateTimeParseException: Text '2018-03-33' " +
            "could not be parsed: Invalid value for DayOfMonth (valid values 1 - 28/31): 33")
      }

      "there is an invalid value provided for a numeric parameter" in {
        val url = "VAT Return Debit Charge+one+333.33+0+2018-03-01+None+true+None+None+None"
        PathBindables.standardChargePathBinder.bind("", url) shouldBe
          Left("Failed to bind due to error: java.lang.NumberFormatException")
      }

      "there is an invalid value provided for a boolean parameter" in {
        val url = "VAT Return Debit Charge+111.11+333.33+0+2018-03-01+None+truest+None+None+None"
        PathBindables.standardChargePathBinder.bind("", url) shouldBe
          Left("Failed to bind due to error: java.lang.IllegalArgumentException: For input string: \"truest\"")
      }

      "there are not enough parameters" in {
        val url = "VAT Return Debit Charge+111.11+333.33"
        PathBindables.standardChargePathBinder.bind("", url) shouldBe
          Left("Failed to bind due to error: java.lang.ArrayIndexOutOfBoundsException: 3")
      }

      "the URL is in an unexpected format" in {
        val url = "what-you-owe"
        PathBindables.standardChargePathBinder.bind("", url) shouldBe
          Left("Failed to bind due to error: java.lang.ArrayIndexOutOfBoundsException: 1")
      }
    }

    "successfully unbind a StandardChargeViewModel to the expected URL" when {

      "all parameters are defined" in {
        PathBindables.standardChargePathBinder.unbind("", chargeModel1) shouldBe maxUrl
      }

      "some parameters are not defined" in {
        PathBindables.standardChargePathBinder.unbind("", minModel) shouldBe minUrl
      }
    }
  }
}
