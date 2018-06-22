/*
 * Copyright 2018 HM Revenue & Customs
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
import models.viewModels.PaymentsHistoryModel
import views.templates.TemplateBaseSpec
import play.twirl.api.Html

class ChargeTypesTemplateSpec extends TemplateBaseSpec {

  "The chargeTypes template" when {

    "there is a vat return debit charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        LocalDate.parse("2018-01-12"),
        LocalDate.parse("2018-03-23"),
        234556,
        LocalDate.parse("2018-02-14")
      )

      val expectedMarkup = Html(
        s"""
           |
           | <tr class="error">
           |   <td scope="row">error</td>
           |   <td>there has been an error</td>
           |   <td class="numeric">1234</td>
           | </tr>
           |
         """.stripMargin
      )

      val markup = views.html.templates.chargeTypes(model)

      "display the correct table row content" in {
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }

    "there is a vat return credit charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        LocalDate.parse("2018-01-12"),
        LocalDate.parse("2018-03-23"),
        234556,
        LocalDate.parse("2018-02-14")
      )

      val expectedMarkup = Html(
        s"""
           |
           | <tr class="error">
           |   <td scope="row">error</td>
           |   <td>there has been an error</td>
           |   <td class="numeric">1234</td>
           | </tr>
           |
         """.stripMargin
      )

      val markup = views.html.templates.chargeTypes(model)

      "display the correct table row content" in {
        formatHtml(markup) shouldBe formatHtml(expectedMarkup)
      }
    }
  }
}
