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

import org.scalatest.Matchers
import utils.{MoneyPounds, RenderableMoneyMessage}
import uk.gov.hmrc.play.test.UnitSpec

class MoneyPoundsSpec extends UnitSpec with Matchers {

  "quantity" should {

    "return the formatted value with 2 decimal places" in {
      MoneyPounds(0.99, 2).quantity shouldBe "0.99"
      MoneyPounds(99).quantity         shouldBe "99.00"
    }

    "return the formatted value with no decimal places" in {
      MoneyPounds(999.99999, 0).quantity shouldBe "999"
      MoneyPounds(999, 0).quantity       shouldBe "999"
    }

    "return the formatted value (with grouping separators) and no decimal places" in {
      MoneyPounds(9999999.99999, 0).quantity shouldBe "9,999,999"
      MoneyPounds(9999, 0).quantity          shouldBe "9,999"
    }

    "return the formatted value (with grouping separators) and 2 decimal places" in {
      MoneyPounds(9999999.99999, 2).quantity shouldBe "9,999,999.99"
      MoneyPounds(9999, 2).quantity          shouldBe "9,999.00"
    }

    "return the formatted value (with grouping separators) and 2 decimal places rounding up" in {
      MoneyPounds(999999.99999, 2, true).quantity shouldBe "1,000,000.00"
    }

    "return the formatted value (with grouping separators) and no decimal places rounding up" in {
      MoneyPounds(0.00, 0, true).quantity shouldBe "0"
    }

  }

  "Money" should {

    "include a pound (£) sign before a number" in {
      RenderableMoneyMessage(MoneyPounds(10.50)).render.toString() should include("&pound;10.50")
    }

    "be prefixed by a minus if the number is negative" in {
      RenderableMoneyMessage(MoneyPounds(-10.50)).render.toString() should include("&minus;&pound;10.50")
    }

  }

}