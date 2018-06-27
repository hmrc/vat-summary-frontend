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

import org.jsoup.Jsoup
import java.time.LocalDate
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import models.viewModels.PaymentsHistoryModel

class ChargeTypesTemplateSpec extends ViewBaseSpec {

  object Selectors {
    val tableRow = "tr"
    val clearedDate = "tr td:nth-of-type(1)"
    val description = "tr td:nth-of-type(2)"
    val amount = "tr td:nth-of-type(3)"
  }

  "The chargeTypes template" when {

    "there is a vat return debit charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        "VAT Return Debit Charge",
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        123456,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = views.html.templates.chargeTypes(model)
      lazy implicit val document: Document = Jsoup.parse(
        s"<table>${template.body}</table>"
      )

      "display the correct table row class" in {
        element(Selectors.tableRow).attr("class") shouldBe "debit-charge"
      }

      "display the correct cleared date" in {
        elementText(Selectors.clearedDate) shouldBe "14 February 2018"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "12 Jan to 23 Mar 2018 return"
      }

      "display the correct amount" in {
        elementText(Selectors.amount) shouldBe "£123,456"
      }
    }

    "there is a vat return credit charge" should {

      val model: PaymentsHistoryModel = PaymentsHistoryModel(
        "VAT Return Credit Charge",
        Some(LocalDate.parse("2018-01-12")),
        Some(LocalDate.parse("2018-03-23")),
        123456,
        Some(LocalDate.parse("2018-02-14"))
      )

      lazy val template = views.html.templates.chargeTypes(model)
      lazy implicit val document: Document = Jsoup.parse(
        s"<table>${template.body}</table>"
      )

      "display the correct table row class" in {
        element(Selectors.tableRow).attr("class") shouldBe "credit-charge"
      }

      "display the correct cleared date" in {
        elementText(Selectors.clearedDate) shouldBe "14 February 2018"
      }

      "display the correct description" in {
        elementText(Selectors.description) shouldBe "Repayment for 12 Jan to 23 Mar 2018 return"
      }

      "display the correct amount" in {
        elementText(Selectors.amount) shouldBe "£123,456"
      }
    }
  }
}
