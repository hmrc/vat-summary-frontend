/*
 * Copyright 2020 HM Revenue & Customs
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

package views.certificate.helpers

import common.TestModels.{exampleNonNSTP, exampleNonStandardTaxPeriods}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.certificate.helpers.NonStandardReturnFrequency

class NonStandardReturnFrequencySpec extends ViewBaseSpec {

  val nonStandardReturnFrequencyView: NonStandardReturnFrequency = injector.instanceOf[NonStandardReturnFrequency]

  object Selectors {
    val heading = "h2"
    val cardClass = ".card-full-container"
    val printButton = "button"
  }

  "The Non-standard Tax Periods card" should {

    lazy val view = nonStandardReturnFrequencyView(
      exampleNonStandardTaxPeriods, exampleNonNSTP)(messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    lazy val card = document.select(Selectors.cardClass).get(0)

    "have the correct heading" in {
      card.select(Selectors.heading).text() shouldBe "Non-standard tax periods"
    }
    "have the correct first paragraph" in {
      card.select("p:nth-child(2)").text() shouldBe "If you do not renew your non-standard tax periods, " +
        "an additional period (6 January 2019 to 31 January 2019) will be added."
    }
    "have the correct second paragraph" in {
      card.select("p:nth-child(3)").text() shouldBe "This will cover the time between your last non-standard tax " +
        "period and the start of your standard tax periods."
    }
    "have a 2018 year" in {
      val row = card.select(".govuk-check-your-answers:nth-of-type(1)")
      row.select("dt").get(0).text() shouldBe "2018"
    }
    "have the first non-standard tax period without years" in {
      val row = card.select(".govuk-check-your-answers:nth-of-type(1)")
      row.select("dd").get(0).text() shouldBe "29 December to 30 December"
    }
    "have the second non-standard tax period with years" in {
      val row = card.select(".govuk-check-your-answers:nth-of-type(1)")
      row.select("dd").get(1).text() shouldBe "31 December 2018 to 1 January 2019"
    }
    "have a 2019 year" in {
      val row = card.select(".govuk-check-your-answers:nth-of-type(1)")
      row.select("dt").get(1).text() shouldBe "2019"
    }
    "have the third non-standard tax period without years" in {
      val row = card.select(".govuk-check-your-answers:nth-of-type(1)")
      row.select("dd").get(2).text() shouldBe "2 January to 3 January"
    }
    "have the final non-standard tax period without years" in {
      val row = card.select(".govuk-check-your-answers:nth-of-type(1)")
      row.select("dd").get(3).text() shouldBe "4 January to 5 January"
    }
  }
}
