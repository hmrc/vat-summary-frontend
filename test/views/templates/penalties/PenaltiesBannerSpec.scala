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

package views.templates.penalties

import models.penalties.PenaltiesSummary
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.templates.penalties.PenaltiesBanner
import views.ViewBaseSpec

class PenaltiesBannerSpec extends ViewBaseSpec {

  val injectedView: PenaltiesBanner = injector.instanceOf[PenaltiesBanner]
  val model: PenaltiesSummary = PenaltiesSummary.empty

  "The Penalties banner" when {

    "there are no active penalties" should {

      lazy val view = injectedView(model)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "be hidden" in {
        elementExtinct(".govuk-notification-banner")
      }
    }

    "there are active penalties" when {

      "there is one penalty point" should {

        lazy val view = injectedView(model.copy(noOfPoints = 1, noOfCrystalisedPenalties = 0))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct heading" in {
          elementText("h2") shouldBe "Late submission and late payment penalties"
        }

        "have the correct penalty information" in {
          elementText(".govuk-notification-banner__content > div") shouldBe "Total penalty points: 1"
        }

        "have a link to the penalties service" which {

          "has the correct text" in {
            elementText("a") shouldBe "Find out why you have a penalty"
          }

          "has the correct link destination" in {
            element("a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
          }
        }
      }

      "there is more than one penalty point" should {

        lazy val view = injectedView(model.copy(noOfPoints = 2, noOfCrystalisedPenalties = 0))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct penalty information" in {
          elementText(".govuk-notification-banner__content > div") shouldBe "Total penalty points: 2"
        }

        "have the correct link text" in {
          elementText("a") shouldBe "Find out why you have penalties"
        }

        "has the correct link destination" in {
          element("a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
        }
      }

      "there is one crystalised penalty point" should {

        lazy val view = injectedView(model.copy(noOfPoints = 0, noOfCrystalisedPenalties = 1, crystalisedPenaltyAmountDue = 100.00))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct heading" in {
          elementText("h2") shouldBe "Late submission and late payment penalties"
        }

        "have the correct penalty information" in {
          elementText(".govuk-notification-banner__content > div") shouldBe "Penalty amount to pay: £100"
        }

        "have a link to the penalties service" which {

          "has the correct text" in {
            elementText("a") shouldBe "Find out why you have a penalty"
          }

          "has the correct link destination" in {
            element("a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
          }
        }
      }

      "there is more than one crystalised penalty point" should {

        lazy val view = injectedView(model.copy(noOfPoints = 0, noOfCrystalisedPenalties = 2, crystalisedPenaltyAmountDue = 200.99))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct penalty information" in {
          elementText(".govuk-notification-banner__content > div") shouldBe "Penalty amount to pay: £200.99"
        }

        "have the correct link text" in {
          elementText("a") shouldBe "Find out why you have penalties"
        }

        "has the correct link destination" in {
          element("a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
        }
      }

      "there are both crystalised penalties and penalty points" should {

        lazy val view = injectedView(model.copy(noOfPoints = 1, noOfCrystalisedPenalties = 1, crystalisedPenaltyAmountDue = 100.00))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct penalty information" in {
          elementText(".govuk-notification-banner__content > div") shouldBe "Penalty amount to pay: £100 Total penalty points: 1"
        }

        "have the correct link text" in {
          elementText("a") shouldBe "Find out why you have penalties"
        }

        "has the correct link destination" in {
          element("a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
        }
      }

    }
  }
}
