/*
 * Copyright 2025 HM Revenue & Customs
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

package views.payments

import forms.ExistingDirectDebitFormProvider
import models.viewModels.{ExistingDDContinuePayment, ExistingDirectDebitFormModel, ExistingDirectDebitViewModel}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.data.Form
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.ExistingDirectDebit

class ExistingDirectDebitViewSpec extends ViewBaseSpec {

  val existingDirectDebitView: ExistingDirectDebit = injector.instanceOf[ExistingDirectDebit]
  val formProvider = new ExistingDirectDebitFormProvider()
  val form: Form[ExistingDirectDebitFormModel] = formProvider()

  val model: ExistingDirectDebitViewModel = ExistingDirectDebitViewModel(
    dueDateOrUrl = Some("2017-12-07"),
    linkId = "wyo",
    directDebitMandateFound = true
  )

  val radioOptions = ExistingDDContinuePayment.options

  lazy val view: Html = existingDirectDebitView(model, form, radioOptions, Html(""))(request, messages, mockConfig, user)
  lazy implicit val document: Document = Jsoup.parse(view.body)

  "The ExistingDirectDebit view" when {

    "rendered for a principal user" should {

      "have the correct page title" in {
        document.title shouldBe "You have a Direct Debit Instruction in place - Manage your VAT account - GOV.UK"
      }

      "have the correct main heading" in {
        elementText("h1") shouldBe "You have a Direct Debit Instruction in place"
      }

      "have breadcrumbs" in {
        elementText("li.govuk-breadcrumbs__list-item:nth-child(1) > a") shouldBe "Business tax account"
        element("li.govuk-breadcrumbs__list-item:nth-child(1) > a").attr("href") shouldBe mockConfig.btaHomeUrl

        elementText("li.govuk-breadcrumbs__list-item:nth-child(2) > a") shouldBe "Your VAT account"
        element("li.govuk-breadcrumbs__list-item:nth-child(2) > a").attr("href") shouldBe
          controllers.routes.VatDetailsController.details.url

        elementText("li.govuk-breadcrumbs__list-item:nth-child(3) > a") shouldBe "What you owe"
        element("li.govuk-breadcrumbs__list-item:nth-child(3) > a").attr("href") shouldBe
          controllers.routes.WhatYouOweController.show.url
      }

      "have hidden fields with correct values" in {
        element("#dueDateOrUrl").attr("value") shouldBe model.dueDateOrUrl.get
        element("#linkId").attr("value") shouldBe model.linkId
        element("#directDebitMandateFound").attr("value") shouldBe model.directDebitMandateFound.toString
      }

      "have a radio button group with the correct options" in {
        radioOptions.foreach { option =>
          document.select(s"input[type=radio][value=${option.value}]").size() mustBe 1
        }
      }

      "have a submit button" in {
        elementText(".govuk-button") shouldBe "Continue"
      }

      "display details sections" in {
        elementText("#what-does-my-account-show-span") shouldBe messages("existingDD.details1.summary")
        elementText("#why-have-i-been-charged-span") shouldBe messages("existingDD.details2.summary")
      }

    }
  }
}
