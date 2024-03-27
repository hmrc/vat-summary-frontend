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

package views.templates.penalties

import models.penalties.PenaltiesSummary
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.html.templates.penalties.PenaltiesBanner
import views.ViewBaseSpec

class PenaltiesBannerSpec extends ViewBaseSpec {

  val injectedView: PenaltiesBanner = injector.instanceOf[PenaltiesBanner]
  val model: PenaltiesSummary = PenaltiesSummary.empty

  "The PenaltiesBanner template" when {

    "there are no active penalties" should {

      lazy val view = injectedView(Some(model))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the upcoming penalty changes banner" which {

        "has the correct heading" in {
          elementText("#upcoming-penalties-banner") shouldBe "Important"
        }

        "has the correct announcement information" in {
          elementText("#announcement-information") shouldBe "From January 2023, we’re launching a new penalty system to replace Default Surcharge."
        }

        "has the correct date information" in {
          elementText("#date-information") shouldBe "The change affects late returns and late payments for VAT periods starting on or after 1 January 2023."
        }

        "has the correct calculation information" in {
          elementText("#calculation-information") shouldBe "We’re also changing how we calculate interest on late payments and repayment returns."
        }

        "has a link" which {

          "has the correct link message" in {
            elementText(".govuk-notification-banner__link") shouldBe "Read the guidance on GOV.UK to find out more (opens in a new tab)"
          }

          "has the correct link location" in {
            element(".govuk-notification-banner__link").attr("href") shouldBe mockConfig.penaltiesChangesUrl
          }
        }
      }

      "not display the penalties banner which details penalty points and penalties owed" in {
        elementExtinct("#penalties-banner")
      }
    }

    "there are active penalties" when {

      "there are only penalty points" when {

        "there is one penalty point" should {

          lazy val view = injectedView(Some(model.copy(noOfPoints = 1)))
          lazy implicit val document: Document = Jsoup.parse(view.body)

          "have the correct heading" in {
            elementText("#penalties-banner-title") shouldBe "Late submission and late payment penalties"
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

          "not display the upcoming changes to penalties banner" in {
            elementExtinct("#upcoming-penalties-banner")
          }
        }

        "there is more than one penalty point" should {

          lazy val view = injectedView(Some(model.copy(noOfPoints = 2)))
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

          "not display the upcoming changes to penalties banner" in {
            elementExtinct("#upcoming-penalties-banner")
          }
        }
      }

      "there are only crystallised penalties" when {

        "there is one crystallised penalty" should {

          lazy val view = injectedView(Some(model.copy(noOfPoints = 0, noOfCrystalisedPenalties = 1,
            crystalisedPenaltyAmountDue = 100.00)))
          lazy implicit val document: Document = Jsoup.parse(view.body)

          "have the correct heading" in {
            elementText("#penalties-banner-title") shouldBe "Late submission and late payment penalties"
          }

          "have the correct penalty information" in {
            elementText(".govuk-notification-banner__content > div") shouldBe "Penalty amount to pay: £100.00"
          }

          "have a link to the penalties service" which {

            "has the correct text" in {
              elementText("a") shouldBe "Find out why you have a penalty"
            }

            "has the correct link destination" in {
              element("a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
            }
          }

          "not display the upcoming changes to penalties banner" in {
            elementExtinct("#upcoming-penalties-banner")
          }
        }

        "there is more than one crystallised penalty" should {

          lazy val view = injectedView(Some(model.copy(noOfCrystalisedPenalties = 2, crystalisedPenaltyAmountDue = 200.9)))
          lazy implicit val document: Document = Jsoup.parse(view.body)

          "have the correct penalty information" in {
            elementText(".govuk-notification-banner__content > div") shouldBe "Penalty amount to pay: £200.90"
          }

          "have the correct link text" in {
            elementText("a") shouldBe "Find out why you have penalties"
          }

          "has the correct link destination" in {
            element("a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
          }

          "not display the upcoming changes to penalties banner" in {
            elementExtinct("#upcoming-penalties-banner")
          }
        }
      }

      "there are only estimated penalties" when {

        "there is one estimated penalty" should {

          lazy val view = injectedView(Some(model.copy(noOfEstimatedPenalties = 1, estimatedPenaltyAmount = 100.0)))
          lazy implicit val document: Document = Jsoup.parse(view.body)

          "have the correct heading" in {
            elementText("#penalties-banner-title") shouldBe "Late submission and late payment penalties"
          }

          "have the correct penalty information" in {
            elementText(".govuk-notification-banner__content > div") shouldBe "Estimated penalty amount: £100.00"
          }

          "have a link to the penalties service" which {

            "has the correct text" in {
              elementText("a") shouldBe "Find out why you have a penalty"
            }

            "has the correct link destination" in {
              element("a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
            }
          }

          "not display the upcoming changes to penalties banner" in {
            elementExtinct("#upcoming-penalties-banner")
          }
        }

        "there is more than one estimated penalty" should {

          lazy val view = injectedView(Some(model.copy(noOfEstimatedPenalties = 2, estimatedPenaltyAmount = 200.99)))
          lazy implicit val document: Document = Jsoup.parse(view.body)

          "have the correct penalty information" in {
            elementText(".govuk-notification-banner__content > div") shouldBe "Estimated penalty amount: £200.99"
          }

          "have the correct link text" in {
            elementText("a") shouldBe "Find out why you have penalties"
          }

          "has the correct link destination" in {
            element("a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
          }

          "not display the upcoming changes to penalties banner" in {
            elementExtinct("#upcoming-penalties-banner")
          }
        }
      }

      "there are both crystallised penalties and penalty points, but no estimated penalties" should {

        lazy val view = injectedView(Some(model.copy(noOfPoints = 1, noOfCrystalisedPenalties = 1, crystalisedPenaltyAmountDue = 100.00)))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct penalty information" in {
          elementText(".govuk-notification-banner__content > div") shouldBe "Penalty amount to pay: £100.00 Total penalty points: 1"
        }

        "have the correct link text" in {
          elementText("a") shouldBe "Find out why you have penalties"
        }

        "has the correct link destination" in {
          element("a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
        }

        "not display the upcoming changes to penalties banner" in {
          elementExtinct("#upcoming-penalties-banner")
        }
      }

      "there are both crystallised penalties and estimated penalties, but no penalty points" should {

        lazy val view = injectedView(Some(model.copy(noOfCrystalisedPenalties = 1, crystalisedPenaltyAmountDue = 100.00,
          noOfEstimatedPenalties = 1, estimatedPenaltyAmount = 150.00)))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct penalty information" in {
          elementText(".govuk-notification-banner__content > div") shouldBe
            "Penalty amount to pay: £100.00 Estimated further penalty amount: £150.00"
        }

        "have the correct link text" in {
          elementText("a") shouldBe "Find out why you have penalties"
        }

        "has the correct link destination" in {
          element("a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
        }

        "not display the upcoming changes to penalties banner" in {
          elementExtinct("#upcoming-penalties-banner")
        }
      }

      "there are both estimated penalties and penalty points, but no crystallised penalties" should {

        lazy val view = injectedView(Some(model.copy(noOfPoints = 1, noOfEstimatedPenalties = 1, estimatedPenaltyAmount = 150.00)))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct penalty information" in {
          elementText(".govuk-notification-banner__content > div") shouldBe "Estimated penalty amount: £150.00 Total penalty points: 1"
        }

        "have the correct link text" in {
          elementText("a") shouldBe "Find out why you have penalties"
        }

        "has the correct link destination" in {
          element("a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
        }

        "not display the upcoming changes to penalties banner" in {
          elementExtinct("#upcoming-penalties-banner")
        }
      }

      "there are crystallised penalties, estimated penalties and active points" should {

        lazy val view = injectedView(Some(model.copy(noOfPoints = 1, noOfCrystalisedPenalties = 1, crystalisedPenaltyAmountDue = 100.00,
          noOfEstimatedPenalties = 1, estimatedPenaltyAmount = 150.00)))
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct penalty information" in {
          elementText(".govuk-notification-banner__content > div") shouldBe
            "Penalty amount to pay: £100.00 Estimated further penalty amount: £150.00 Total penalty points: 1"
        }

        "have the correct link text" in {
          elementText("a") shouldBe "Find out why you have penalties"
        }

        "has the correct link destination" in {
          element("a").attr("href") shouldBe mockConfig.penaltiesFrontendUrl
        }

        "not display the upcoming changes to penalties banner" in {
          elementExtinct("#upcoming-penalties-banner")
        }
      }
    }
  }
}
