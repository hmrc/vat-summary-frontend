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

package services

import common.TestModels._
import models.CustomerInformation
import models.errors._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.LocalDate

class POACheckServiceSpec extends AnyWordSpec with Matchers{

  val service = new POACheckService()

  val fixedDate: LocalDate = LocalDate.parse("2018-05-01")
  val todayFixedDate: LocalDate = LocalDate.parse("2025-02-28")

  def customerInfoWithDate(date: Option[String]): CustomerInformation = 
    customerInformationMax.copy(poaActiveUntil = date)

  "retrievePoaActiveForCustomer" should {

    "return true when poaActiveUntil is today" in {
      val result = service.retrievePoaActiveForCustomer(
        Right(customerInfoWithDate(Some(fixedDate.toString))),
        fixedDate
      )
      result shouldBe true
    }

    "return true when poaActiveUntil is in the future" in {
      val futureDate = fixedDate.plusDays(5).toString
      val result = service.retrievePoaActiveForCustomer(
        Right(customerInfoWithDate(Some(futureDate))),
        fixedDate
      )
      result shouldBe true
    }

    "return false when poaActiveUntil is in the past" in {
      val pastDate = fixedDate.minusDays(5).toString
      val result = service.retrievePoaActiveForCustomer(
        Right(customerInfoWithDate(Some(pastDate))),
        fixedDate
      )
      result shouldBe false
    }

    "return false when poaActiveUntil is None" in {
      val result = service.retrievePoaActiveForCustomer(
        Right(customerInfoWithDate(None)),
        fixedDate
      )
      result shouldBe false
    }

    "return false when accountDetails is an error" in {
      val result = service.retrievePoaActiveForCustomer(
        Left(BadRequestError("400", "Some error")),
        fixedDate
      )
      result shouldBe false
    }
  }

  "changedOnDateWithInLatestVatPeriod" should {
    "should return the valid changed on date" in {
      val result = service.changedOnDateWithInLatestVatPeriod(
        Some(modelSrChangedOnTest1),
        todayFixedDate
      )
      result shouldBe Some(LocalDate.parse("2025-03-15"))
    }
    "should return none for the changed on date not falling in current valid vat period" in {
      val result = service.changedOnDateWithInLatestVatPeriod(
        Some(modelSrChangedOnTest2),
        todayFixedDate
      )
      result shouldBe None
    }
  }
}
