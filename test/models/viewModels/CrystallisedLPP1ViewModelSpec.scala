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

package models.viewModels

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import java.time.LocalDate

class CrystallisedLPP1ViewModelSpec extends AnyWordSpecLike with Matchers {

  val model: CrystallisedLPP1ViewModel = CrystallisedLPP1ViewModel(
    99,
    10,
    Some(20),
    2.4,
    111.11,
    Some(222.22),
    LocalDate.parse("2020-01-01"),
    500.55,
    100.11,
    400.44,
    LocalDate.parse("2020-03-03"),
    LocalDate.parse("2020-04-04"),
    "VAT Return 1st LPP",
    "CHARGEREF",
    isOverdue = false
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
}
