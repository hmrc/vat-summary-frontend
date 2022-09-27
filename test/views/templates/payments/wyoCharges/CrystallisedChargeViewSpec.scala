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

package views.templates.payments.wyoCharges

import common.TestModels.{crystallisedInterestCharge, overdueCrystallisedInterestCharge}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.templates.payments.wyoCharges.CrystallisedCharge

class CrystallisedChargeViewSpec extends ViewBaseSpec {

  val injectedView: CrystallisedCharge = injector.instanceOf[CrystallisedCharge]

  "the crystallised charge template" when {

    "a charge is overdue" should {

      lazy val view = injectedView(overdueCrystallisedInterestCharge)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct charge description text" in {
        elementText("a") shouldBe
          "Interest on central assessment of VAT for period 1 Jan to 1 Mar 2021"
      }

      "have an overdue label" in {
        elementText(".govuk-tag") shouldBe "overdue"
      }

      "have the correct due hint text" in {
        elementText("span") shouldBe "due 8 April 2021"
      }

      "have a link with the correct href" in {
        element("a").attr("href") shouldBe
          testOnly.controllers.routes.ChargeBreakdownController.showBreakdown(
            overdueCrystallisedInterestCharge.generateHash(user.vrn)
          ).url
      }
    }

    "the charge is not overdue" should {
      lazy val view = injectedView(crystallisedInterestCharge)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct charge description text" in {
        elementText("a") shouldBe
          "Interest on central assessment of VAT for period 1 Jan to 1 Mar 2021"
      }

      "not have an overdue label" in {
        elementExtinct(".govuk-tag")
      }

      "have the correct due hint text" in {
        elementText("span") shouldBe "due 8 April 2021"
      }

      "have a link with the correct href" in {
        element("a").attr("href") shouldBe
          testOnly.controllers.routes.ChargeBreakdownController.showBreakdown(
            crystallisedInterestCharge.generateHash(user.vrn)
          ).url
      }
    }
  }
}
