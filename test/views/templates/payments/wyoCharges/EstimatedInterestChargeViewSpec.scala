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

import common.TestModels.estimatedLPIModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.html.templates.payments.wyoCharges.EstimatedCharge

class EstimatedInterestChargeViewSpec extends ViewBaseSpec {

  val injectedView: EstimatedCharge = injector.instanceOf[EstimatedCharge]

  "The estimated interest charge template" should {

    lazy val view = injectedView(estimatedLPIModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct charge description text" in {
      elementText("a") shouldBe "Interest on central assessment of VAT for period 1\u00a0Jan to 2\u00a0Feb\u00a02018"
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
          estimatedLPIModel.generateHash(user.vrn)
        ).url
    }
  }
}
