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

package utils

import common.SpecBase

import java.time.LocalDate

class LocalDateHelperSpec extends SpecBase with LocalDateHelper{

  "paymentDetailsFormat" when {

    "date is in the future" must {

      "convert to string correctly in ISO format" in {

        val year = LocalDate.now().getYear +1

        val date = LocalDate.of(year, 5, 25)

        val expectedResult = s"${year.toString}-05-25"
        val actualResult = date.paymentDetailsFormat

        expectedResult mustBe actualResult
      }
    }

    "date is in the past" must {

      "convert to string correctly in ISO format" in {

        val year = LocalDate.now().getYear - 1

        val date = LocalDate.of(year, 12, 30)

        val expectedResult = s"${year.toString}-12-30"
        val actualResult = date.paymentDetailsFormat

        expectedResult mustBe actualResult
      }
    }

    "date is a leap day" must {

      "convert to string correctly in ISO format" in {

        val date = LocalDate.of(2024, 2, 29)

        val expectedResult = "2024-02-29"
        val actualResult = date.paymentDetailsFormat

        expectedResult mustBe actualResult
      }
    }
  }

}
