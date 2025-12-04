/*
 * Copyright 2025 HM Revenue & Customs
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

package services

import common.TestModels.{modelSrChangedOnAnnualAccountingTest1, modelSrChangedOnAnnualAccountingTest2}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.LocalDate

class AnnualAccountingCheckServiceSpec extends AnyWordSpec with Matchers {

  val service = new AnnualAccountingCheckService()

  val fixedDate: LocalDate = LocalDate.parse("2018-05-01")
  val todayFixedDate: LocalDate = LocalDate.parse("2025-05-28")

  "changedOnDateWithinLast3Months" should {

    "should return the valid changed on date" in {
      val result = service.changedOnDateWithinLast3Months(
        Some(modelSrChangedOnAnnualAccountingTest1),
        todayFixedDate
      )
      result shouldBe Some(LocalDate.parse("2025-03-15"))
    }

    "should return none for the chaned on date not falling in current 4 month period" in {
      val result = service.changedOnDateWithinLast3Months(
        Some(modelSrChangedOnAnnualAccountingTest2),
        todayFixedDate
      )
      result shouldBe None
    }
  }
}
