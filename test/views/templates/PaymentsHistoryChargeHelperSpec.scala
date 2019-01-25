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

import uk.gov.hmrc.play.test.UnitSpec
import views.templates.PaymentsHistoryChargeHelper

class PaymentsHistoryChargeHelperSpec extends UnitSpec {

  "Calling getChargeType" when {

    "the lookup string is a valid charge type" should {

      PaymentsHistoryChargeHelper.values.foreach { paymentHistoryChargeType =>
        lazy val result = PaymentsHistoryChargeHelper.getChargeType(paymentHistoryChargeType.name)

        s"return the charge type associated with ${paymentHistoryChargeType.name}" in {
          result shouldBe Some(paymentHistoryChargeType)
        }
      }

    }
  }

    "the lookup String is an invalid charge type" should {

      "return a None" in {

        val result = PaymentsHistoryChargeHelper.getChargeType("invalid")

        result shouldBe None

      }
    }
}
