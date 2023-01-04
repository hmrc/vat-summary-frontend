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

package models.viewModels

import common.TestModels.{crystallisedInterestCharge, crystallisedInterestJson}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.libs.json.Json

import java.time.LocalDate

class CrystallisedInterestViewModelSpec extends AnyWordSpecLike with Matchers {

  val model: CrystallisedInterestViewModel = CrystallisedInterestViewModel(
    LocalDate.parse("2018-01-01"),
    LocalDate.parse("2018-02-02"),
    "VAT Default Interest",
    2.6,
    LocalDate.parse("2018-03-03"),
    300.33,
    200.22,
    100.11,
    isOverdue = false,
    "XXXXXX1234567890",
    isPenalty = false
  )

  "The makePaymentRedirect value" should {

    "be a payment handoff URL generated from the model's parameters" in {
      val amountInPence = (model.leftToPay * 100).toLong
      val chargeTypeEncoded = model.chargeType.replace(" ", "%20")

      model.makePaymentRedirect should include(
        s"/make-payment/$amountInPence/${model.periodTo.getMonthValue}/${model.periodTo.getYear}" +
        s"/${model.periodTo}/$chargeTypeEncoded/${model.dueDate}/${model.chargeReference}"
      )
    }
  }

  "The CrystallisedInterestViewModel" should {

    "read from JSON" in {
      crystallisedInterestJson.as[CrystallisedInterestViewModel] shouldBe crystallisedInterestCharge
    }

    "write to JSON" in {
      Json.toJson(crystallisedInterestCharge) shouldBe crystallisedInterestJson
    }
  }
}
