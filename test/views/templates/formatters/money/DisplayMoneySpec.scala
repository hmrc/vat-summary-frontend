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
    val positiveDecimalValue = BigDecimal(3000.75)

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
        document.body.text() shouldBe "£3,000.75"
      }
    }
  }
}
