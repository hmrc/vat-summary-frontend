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
    val firstYear = "#year-2018"
    val secondYear = "#year-2019"
    val firstPeriod = "#nstp-card > div.govuk-grid-column-full.govuk-body > dl > dd:nth-child(2)"
    val secondPeriod = "#nstp-card > div.govuk-grid-column-full.govuk-body > dl > dd:nth-child(3)"
    val thirdPeriod = "#nstp-card > div.govuk-grid-column-full.govuk-body > dl > dd:nth-child(5)"
    val fourthPeriod = "#nstp-card > div.govuk-grid-column-full.govuk-body > dl > dd:nth-child(6)"
    def nthParagraph: Int => String = n => s"p:nth-child($n)"
  }

  "The Non-standard Tax Periods card" should {

    lazy val view = nonStandardReturnFrequencyView(
      exampleNonStandardTaxPeriods, exampleNonNSTP)(messages)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct heading" in {
      elementText(Selectors.heading) shouldBe "Non-standard tax periods"
    }

    "have the correct first paragraph" in {
      elementWholeText(Selectors.nthParagraph(2)) shouldBe "If you do not renew your non-standard tax periods, " +
        "an additional period (6\u00a0January\u00a02019 to 31\u00a0January\u00a02019) will be added."
    }

    "have the correct second paragraph" in {
      elementText(Selectors.nthParagraph(3)) shouldBe "This will cover the time between your last non-standard tax " +
        "period and the start of your standard tax periods."
    }

    "have a 2018 year" in {
      elementText(Selectors.firstYear) shouldBe "2018"
    }

    "have the first non-standard tax period without years" in {
      elementWholeText(Selectors.firstPeriod) shouldBe "29\u00a0December to 30\u00a0December"
    }

    "have the second non-standard tax period with years" in {
      elementWholeText(Selectors.secondPeriod) shouldBe "31\u00a0December\u00a02018 to 1\u00a0January\u00a02019"
    }

    "have a 2019 year" in {
      elementText(Selectors.secondYear) shouldBe "2019"
    }

    "have the third non-standard tax period without years" in {
      elementWholeText(Selectors.thirdPeriod) shouldBe "2\u00a0January to 3\u00a0January"
    }

    "have the fourth non-standard tax period without years" in {
      elementWholeText(Selectors.fourthPeriod) shouldBe "4\u00a0January to 5\u00a0January"
    }
  }
}
