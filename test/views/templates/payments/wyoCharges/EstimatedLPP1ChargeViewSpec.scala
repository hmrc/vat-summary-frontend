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

import common.TestModels.estimatedLPP1Model
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.templates.payments.wyoCharges.EstimatedCharge

class EstimatedLPP1ChargeViewSpec extends ViewBaseSpec {

  val injectedView: EstimatedCharge = injector.instanceOf[EstimatedCharge]

  "The estimated LPP1 charge template" should {

    lazy val view = injectedView(estimatedLPP1Model)
    lazy val viewAsString = view.toString
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct charge description text" in {
      elementText("a") shouldBe "Penalty for late payment of VAT for period 1 Jan to 2 Feb 2019"
    }

    "use non breaking spaces to display the dates in the charge description text" in {
      viewAsString.contains("Penalty for late payment of VAT for period 1\u00a0Jan to 2\u00a0Feb\u00a02019")
    }

    "not have an overdue label" in {
      elementExtinct(".govuk-tag")
    }

    "have the correct due hint text" in {
      elementText("span") shouldBe "estimate"
    }

    "have a link with the correct href" in {
      element("a").attr("href") shouldBe
        controllers.routes.ChargeBreakdownController.showBreakdown(
          estimatedLPP1Model.generateHash(user.vrn)
        ).url
    }
  }
}
