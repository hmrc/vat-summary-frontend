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

import uk.gov.hmrc.play.test.UnitSpec
import views.templates.ChargeType

class ChargeTypeSpec extends UnitSpec {

  "Calling getChargeType" when {

    "the lookup String is a valid ChargeType" should {

      "return the ChargeType accociated with the lookup String" in {

        val result = ChargeType.getChargeType("VAT Return Debit Charge")

        result shouldBe Some(ChargeType.VAT_RETURN_DEBIT_CHARGE)

      }
    }

    "the lookup String is an invalid ChargeType" should {

      "return a None" in {
        
        val result = ChargeType.getChargeType("invalid")

        result shouldBe None

      }
    }
  }
}
