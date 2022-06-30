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

package views.templates.formatters.money

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.templates.formatters.money.DisplayMoney

class DisplayMoneySpec extends ViewBaseSpec {

  "Calling displayMoney" when {

    val displayMoney = injector.instanceOf[DisplayMoney]
    val positiveWholeValue = BigDecimal(3000)
    val positiveZeroDecimalValue = BigDecimal(3000.00)
    val positiveDecimalValue = BigDecimal(3000.99)
    val positiveOneDecimalValue = BigDecimal(3000.9)
    val positiveOneZeroDecimalValue = BigDecimal(3000.0)

    "the value passed is whole" should {

      lazy val template = displayMoney(positiveWholeValue)
      lazy val document: Document = Jsoup.parse(template.body)

      "return a monetary whole value" in {
        document.body.text() shouldBe "£3,000"
      }
    }

    "the value passed is a decimal" should {

      lazy val template = displayMoney(positiveDecimalValue)
      lazy val document: Document = Jsoup.parse(template.body)

      "return a monetary decimal value" in {
        document.body.text() shouldBe "£3,000.99"
      }
    }

    "the value passed is has only one decimal" should {

      lazy val template = displayMoney(positiveOneDecimalValue)
      lazy val document: Document = Jsoup.parse(template.body)

      "return a monetary decimal value" in {
        document.body.text() shouldBe "£3,000.90"
      }
    }

    "the value passed is has one zero decimal" should {

      lazy val template = displayMoney(positiveOneZeroDecimalValue)
      lazy val document: Document = Jsoup.parse(template.body)

      "return a monetary decimal value" in {
        document.body.text() shouldBe "£3,000"
      }
    }

    "the value passed is has zero decimals" should {

      lazy val template = displayMoney(positiveZeroDecimalValue)
      lazy val document: Document = Jsoup.parse(template.body)

      "return a monetary decimal value" in {
        document.body.text() shouldBe "£3,000"
      }
    }
  }
}
