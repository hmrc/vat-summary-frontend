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

package views.templates.payments.wyoCharges

import common.TestModels.{chargeModel1, chargeModel2}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.templates.payments.wyoCharges.StandardCharge

class StandardChargeViewSpec extends ViewBaseSpec {

  val injectedView: StandardCharge = injector.instanceOf[StandardCharge]

  val viewReturnLinkSelector = ".view-return-link"

  "The StandardCharge template" when {

    "a charge is overdue and view return is enabled" should {

      lazy val view = injectedView(chargeModel1, false)
      lazy val viewAsString = view.toString
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct charge description text" in {
        elementText("a") shouldBe "VAT for period 1 Jan to 1 Feb 2018"
      }

      "use a non breaking space for the charge description text" in {
        viewAsString.contains("VAT for period 1\u00a0Jan to 1\u00a0Feb\u00a02018")
      }

      "have a link to the breakdown page" in {
        element("a").attr("href") shouldBe
          controllers.routes.ChargeBreakdownController.showBreakdown(chargeModel1.generateHash(user.vrn)).url
      }

      "have an overdue label" in {
        elementText(".govuk-tag") shouldBe "overdue"
      }

      "have the correct due hint text" in {
        elementText(".what-you-owe-due-date") + " " + elementText(".what-you-owe-view-return") shouldBe "due 1 March 2018 View VAT Return"
      }

      "use a non breaking space for the due hint text" in {
        viewAsString.contains("due 1\u00a0March\u00a02018 View VAT Return")
      }

      "have a link to view the VAT return" which {

        "has the correct text" in {
          elementText(viewReturnLinkSelector + "> .what-you-owe-view-return") shouldBe "View VAT Return"
        }

        "has the correct link destination" in {
          element(viewReturnLinkSelector).attr("href") shouldBe mockConfig.vatReturnUrl(chargeModel1.periodKey.get)
        }

        "has the correct hidden text" in {
          elementText(".what-you-owe-view-return-hidden-text") shouldBe "View VAT Return for the period 1 January to 1 February 2018"
        }

        "use a non breaking space for the hidden text" in {
          viewAsString.contains("View VAT Return for the period 1\u00a0January to 1\u00a0February\u00a02018")
        }
      }
    }

    "a charge is not overdue and view return is not enabled" should {

      lazy val view = injectedView(chargeModel2, false)
      lazy val viewAsString = view.toString
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct charge description text" in {
        elementText("a") shouldBe "Penalty for not filing correctly because you did not use " +
          "the correct digital channel for the period 1 Jan to 1 Feb 2018"
      }

      "use a non breaking space for the charge description text" in {
        viewAsString.contains("Penalty for not filing correctly because you did not use " +
          "the correct digital channel for the period 1\u00a0Jan to 1\u00a0Feb\u00a02018")
      }

      "have a link to the breakdown page" in {
        element("a").attr("href") shouldBe
          controllers.routes.ChargeBreakdownController.showBreakdown(chargeModel2.generateHash(user.vrn)).url
      }

      "not have an overdue label" in {
        elementExtinct(".govuk-tag")
      }

      "have the correct due hint text" in {
        elementText("span") shouldBe "due 1 December 2018"
      }

      "use a non breaking space for the due hint text" in {
        viewAsString.contains("due 1\u00a0December\u00a02018")
      }

      "not have a link to view the VAT return" in {
        elementExtinct(viewReturnLinkSelector)
      }
    }
  }
}
