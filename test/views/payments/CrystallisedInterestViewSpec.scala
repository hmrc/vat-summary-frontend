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

package views.payments

import java.time.LocalDate

import models.viewModels.CrystallisedInterestViewModel
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.CrystallisedInterestView
import org.jsoup.nodes.Document
import org.jsoup.Jsoup

class CrystallisedInterestViewSpec extends ViewBaseSpec {

  val injectedView: CrystallisedInterestView = injector.instanceOf[CrystallisedInterestView]
  val whatYouOweLink: String = testOnly.controllers.routes.WhatYouOweController.show.url

  val viewModel : CrystallisedInterestViewModel = CrystallisedInterestViewModel(
    LocalDate.parse("2022-10-01"),
    LocalDate.parse("2022-12-31"),
    "Interest on VAT",
    2.6,
    LocalDate.parse("2023-03-30"),
    7.71,
    0.00,
    7.71,
    isOverdue = true,
    "chargeRef"
  )

  "Rendering the Crystallised Interest Page for a principal user" when {

    "the user has interest on VAT charge" should {

      lazy val view = injectedView(viewModel, Html(""))(request, messages, mockConfig, user)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {

        document.title shouldBe "Interest on VAT - Manage your VAT account - GOV.UK"

      }

      "have the correct page heading" in {
        elementText("h1") shouldBe "Interest on VAT"
      }

      "have a period caption" in {
        elementText(".govuk-caption-xl") shouldBe "1 October 2022 to 31 December 2022"
      }

      "render breadcrumbs which" should  {

        "have the text 'Business tax account'" in {
          elementText("body > div > div.govuk-breadcrumbs > ol > li:nth-child(1) > a") shouldBe "Business tax account"
        }
        "link to bta" in {
          element("body > div > div.govuk-breadcrumbs > ol > li:nth-child(1) > a").attr("href") shouldBe "bta-url"
        }
        "have the text 'Your VAT account'" in {
          elementText("body > div > div.govuk-breadcrumbs > ol > li:nth-child(2) > a") shouldBe "Your VAT account"
        }
        "link to VAT overview page" in {
          element("body > div > div.govuk-breadcrumbs > ol > li:nth-child(2) > a").attr("href") shouldBe
            controllers.routes.VatDetailsController.details.url
        }
        "have the text 'What You Owe'" in {
          elementText("body > div > div.govuk-breadcrumbs > ol > li:nth-child(3) > a") shouldBe "What you owe"
        }
        "link to the what you owe page" in {
          element("body > div > div.govuk-breadcrumbs > ol > li:nth-child(3) > a").attr("href") shouldBe
            whatYouOweLink
        }
      }
    }
  }

}
