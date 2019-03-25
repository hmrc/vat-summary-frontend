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

package views.templates

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class NextReturnSectionTemplateSpec extends ViewBaseSpec {

  "nextReturnSection template" when {

    object Selectors {
      val nextReturnDueHeading = "h2:nth-of-type(1)"
      val nextReturnDate = "p:nth-of-type(1)"
      val viewReturnsButton = "a:nth-of-type(1)"
      val overdueLabel = "span strong"
    }

    "there is an VAT return to display" should {

      val obligationDueDate: Option[String] = Some("2019-04-30")

      lazy val view = views.html.templates.nextReturnSection(
        obligationDueDate, hasMultiple = false, isOverdue = false, isError = false, isNonMTDfB = false
      )
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the 'Next return due' heading" in {
        elementText(Selectors.nextReturnDueHeading) shouldBe "Next return due"
      }

      "display the due of the return" in {
        elementText(Selectors.nextReturnDate) shouldBe "30 April 2019"
      }

      "display the 'View return deadlines' link" in {
        elementText(Selectors.viewReturnsButton) shouldBe "View return deadlines"
      }
    }

    "there is an VAT return to display for a non-MTDfB user" should {

      val obligationDueDate: Option[String] = Some("2019-04-30")

      lazy val view = views.html.templates.nextReturnSection(
        obligationDueDate, hasMultiple = false, isOverdue = false, isError = false, isNonMTDfB = true
      )
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the 'Next return due' heading" in {
        elementText(Selectors.nextReturnDueHeading) shouldBe "Next return due"
      }

      "display the due of the return" in {
        elementText(Selectors.nextReturnDate) shouldBe "30 April 2019"
      }

      "display the 'View return deadlines' link" in {
        elementText(Selectors.viewReturnsButton) shouldBe "Submit return"
      }
    }

    "there is an overdue return" should {

      val obligationDueDate: Option[String] = Some("2017-04-30")

      lazy val view = views.html.templates.nextReturnSection(
        obligationDueDate, hasMultiple = false, isOverdue = true, isError = false, isNonMTDfB = false
      )
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the overdue label" in {
        elementText(Selectors.overdueLabel) shouldBe "overdue"
      }
    }

    "there is no VAT return to display" should {

      lazy val view = views.html.templates.nextReturnSection(None, hasMultiple = false, isOverdue = false, isError = false, isNonMTDfB = false)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the 'Next return due' heading" in {
        elementText(Selectors.nextReturnDueHeading) shouldBe "Next return due"
      }

      "display the 'No returns due right now' message" in {
        elementText(Selectors.nextReturnDate) shouldBe "No returns due right now"
      }

      "display the 'View return deadlines' link" in {
        elementText(Selectors.viewReturnsButton) shouldBe "View return deadlines"
      }
    }

    "there are multiple obligations to display" should {

      lazy val view = views.html.templates.nextReturnSection(Some("2"), hasMultiple = true, isOverdue = false, isError = false, isNonMTDfB = false)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the 'Next return due' heading" in {
        elementText(Selectors.nextReturnDueHeading) shouldBe "Next return due"
      }

      "display the multiple returns message" in {
        elementText(Selectors.nextReturnDate) shouldBe "You have 2 returns due"
      }

      "display the 'View return deadlines' link" in {
        elementText(Selectors.viewReturnsButton) shouldBe "View return deadlines"
      }
    }

    "there is an error retrieving the return" should {

      lazy val view = views.html.templates.nextReturnSection(None, hasMultiple = false, isOverdue = false, isError = true, isNonMTDfB = false)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the 'Next return due' heading" in {
        elementText(Selectors.nextReturnDueHeading) shouldBe "Next return due"
      }

      "display the error message" in {
        elementText(Selectors.nextReturnDate) shouldBe "Sorry, there is a problem with the service. Try again later."
      }

      "display the 'View return deadlines' button" in {
        elementText(Selectors.viewReturnsButton) shouldBe "View return deadlines"
      }

      "have the correct GA tag for the graceful error content" in {
        element(Selectors.nextReturnDate).attr("data-metrics") shouldBe "error:recovered:next-return"
      }
    }
  }
}
