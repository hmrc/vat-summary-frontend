/*
 * Copyright 2017 HM Revenue & Customs
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

package views.templates

import java.time.LocalDate
import models.Obligation
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class ReturnObligationTemplateSpec extends ViewBaseSpec {

  "returnObligation template" when {

    object Selectors {
      val nextReturnDue = "p:nth-of-type(1)"
      val obligationPeriod = "p:nth-of-type(2)"
      val nextPayment = "p:nth-of-type(3)"
      val submitMethod = "p:nth-of-type(4)"
      val softwareLink = s"$submitMethod a"
    }

    "start and end dates are in the same year" should {

      val obligation = Obligation(
        start = LocalDate.parse("2017-01-01"),
        end = LocalDate.parse("2017-12-31"),
        due = LocalDate.parse("2018-01-31"),
        "O",
        None,
        "#001"
      )

      lazy val view = views.html.templates.returnObligation(obligation)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "contain a first paragraph with correct text" in {
        elementText(Selectors.nextReturnDue) shouldEqual "Next return due: 31 January 2018"
      }

      "contain a second paragraph with correct text" in {
        elementText(Selectors.obligationPeriod) shouldEqual "For the period 1 January to 31 December 2017"
      }

      "contain a third paragraph with correct text" in {
        elementText(Selectors.nextPayment) shouldEqual "This will calculate your next payment."
      }

      "contain a fourth paragraph which" should {

        "contain the correct text" in {
          elementText(Selectors.submitMethod) shouldEqual "You submit using software"
        }

        "contain a softwareLink to '#'" in {
          elementAttributes(Selectors.softwareLink) shouldBe Map("href" -> "#")
        }
      }
    }

    "start and end dates are not in the same year" should {

      val obligation = Obligation(
        start = LocalDate.parse("2017-12-31"),
        end = LocalDate.parse("2018-03-01"),
        due = LocalDate.parse("2018-01-31"),
        "O",
        None,
        "#001"
      )

      lazy val view = views.html.templates.returnObligation(obligation)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "contain a second paragraph with correct text" in {
        elementText(Selectors.obligationPeriod) shouldEqual "For the period 31 December 2017 to 1 March 2018"
      }
    }
  }
}
