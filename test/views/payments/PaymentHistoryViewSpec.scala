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

package views.payments

import models.User

import java.time.LocalDate
import models.payments._
import models.viewModels.{PaymentsHistoryModel, PaymentsHistoryViewModel}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.PaymentHistory
import common.MessageLookup.PaymentHistoryMessages.insetText

class PaymentHistoryViewSpec extends ViewBaseSpec {

  val paymentHistoryView: PaymentHistory = injector.instanceOf[PaymentHistory]

  object Selectors {
    val pageHeading = "h1"
    val backLink = ".govuk-back-link"
    val caption = ".govuk-caption-xl"
    val insetText = ".govuk-inset-text"
    val btaBreadcrumb = "li.govuk-breadcrumbs__list-item:nth-child(1)"
    val btaBreadcrumbLink = "li.govuk-breadcrumbs__list-item:nth-child(1) a"
    val vatBreadcrumb = "li.govuk-breadcrumbs__list-item:nth-child(2)"
    val vatBreadcrumbLink = "li.govuk-breadcrumbs__list-item:nth-child(2) a"
    val currentYearTab = "li.govuk-tabs__list-item:nth-child(1) a"
    val previousYearTab = "li.govuk-tabs__list-item:nth-child(2) a"
    val twoYearsAgoTab = "li.govuk-tabs__list-item:nth-child(3) a"
    val previousPaymentsTab = "li.govuk-tabs__list-item:nth-child(4) a"
    val previousYearNoPayments = "#past-payments-2017 > p"
    val prevPaymentsHeading = "#previous-payments > h1"
    val prevPaymentsParagraph = "#previous-payments > p"
    val paymentDateTableHeading = "tr th:nth-of-type(1) div"
    val paymentDateTableContent = "tr td:nth-of-type(1)"
    val descriptionTableChargeType = "tr:nth-child(1) > td > span:nth-child(1)"
    val descriptionTableContent = "td.govuk-table__cell:nth-child(2) p.vatvc-grey-paragraph-text"
    val amountPaidTableContent = "tr td:nth-of-type(3)"
    val amountRepaidTableContent = "tr td:nth-of-type(4)"
    val insolvencyBanner = "div.govuk-form-group"
    val whatYouOweLink = ".govuk-body > a"
    def columnHeaading(col: Int): String = s".govuk-table__header:nth-of-type($col)"
  }

  val currentYear = 2018
  val exampleAmount = 100

  val model: PaymentsHistoryViewModel = PaymentsHistoryViewModel(
    currentYear,
    Some(currentYear - 1),
    Some(currentYear - 2),
    previousPaymentsTab = true,
    Seq(PaymentsHistoryModel(
      clearingSAPDocument = Some("002828853334"),
      chargeType = ReturnDebitCharge,
      taxPeriodFrom = Some(LocalDate.parse(s"2018-01-01")),
      taxPeriodTo = Some(LocalDate.parse(s"2018-02-01")),
      amount = exampleAmount,
      clearedDate = Some(LocalDate.parse(s"2018-03-01"))
    )),
    showInsolvencyContent = false,
    None
  )

  "The payments history page for a principal user" when {

    "the user is not insolvent" should {

      lazy val view: Html = paymentHistoryView(model, Html(""), migratedWithinThreeYears = false)
      lazy val viewAsString = view.toString
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "Payment history - Manage your VAT account - GOV.UK"
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe "Payment history"
      }

      "not have a client name caption" in {
        elementExtinct(Selectors.caption)
      }

      "have the link to check what you owe" which {

        "has the correct text" in {
          elementText(Selectors.whatYouOweLink) shouldBe "Find out if you owe anything to HMRC"
        }

        "has the correct href" in {
          element(Selectors.whatYouOweLink).attr("href") shouldBe controllers.routes.WhatYouOweController.show.url
        }
      }

      "render breadcrumbs" which {

        "have the text 'Business tax account'" in {
          elementText(Selectors.btaBreadcrumb) shouldBe "Business tax account"
        }

        "link to bta" in {
          element(Selectors.btaBreadcrumbLink).attr("href") shouldBe "bta-url"
        }

        "have the text 'Your VAT account'" in {
          elementText(Selectors.vatBreadcrumb) shouldBe "Your VAT account"
        }

        s"link to ${controllers.routes.VatDetailsController.details.url}" in {
          element(Selectors.vatBreadcrumbLink).attr("href") shouldBe controllers.routes.VatDetailsController.details.url
        }
      }

      "display a current year tab" in {
        elementText(Selectors.currentYearTab) shouldBe currentYear.toString
      }

      "display a previous year tab" in {
        elementText(Selectors.previousYearTab) shouldBe (currentYear - 1).toString
      }

      "display a tab for 2 years ago" in {
        elementText(Selectors.twoYearsAgoTab) shouldBe (currentYear - 2).toString
      }

      "display a previous payments tab" in {
        elementText(Selectors.previousPaymentsTab) shouldBe "Previous payments"
      }

      "have the correct current year tab content" which {

        "has the correct heading in the first column" in {
          elementText(Selectors.columnHeaading(1)) shouldBe "Date"
        }

        "has the correct heading in the second column" in {
          elementText(Selectors.columnHeaading(2)) shouldBe "Payment description"
        }

        "has the correct heading in the third column" in {
          elementText(Selectors.columnHeaading(3)) shouldBe "You paid HMRC"
        }

        "has the correct heading in the fourth column" in {
          elementText(Selectors.columnHeaading(4)) shouldBe "HMRC paid you"
        }

        "has the correct date in the first column" in {
          elementText(Selectors.paymentDateTableContent) shouldBe "1 Mar"
        }

        "use a non breaking space to display the date in the first column" in {
          viewAsString.contains("1\u00a0Mar")
        }

        "has the correct charge description in the second column" in {
          elementText(Selectors.descriptionTableChargeType) shouldBe "VAT"
          elementText(Selectors.descriptionTableContent) shouldBe "for period 1 Jan to 1 Feb 2018"
        }

        "use a non breaking space to display the charge description in the second column" in {
          viewAsString.contains("VAT for period 1\u00a0Jan to 1\u00a0Feb\u00a02018")
        }

        "has the correct payment amount in the third column" in {
          elementText(Selectors.amountPaidTableContent) shouldBe "£100.00"
        }

        "has the correct repayment amount in the fourth column" in {
          elementText(Selectors.amountRepaidTableContent) shouldBe "£0.00"
        }
      }

      "have the correct previous year tab content" in {
        elementText(Selectors.previousYearNoPayments) shouldBe
          "You have not made or received any payments using the new VAT service this year."
      }

      "have the correct previous payments tab content" which {

        "has the correct heading" in {
          elementText(Selectors.prevPaymentsHeading) shouldBe "Service has closed"
        }

        "has the correct message" in {
          elementText(Selectors.prevPaymentsParagraph) shouldBe
            "You can no longer view previous payments. Please refer to your own business records and accounts."
        }
      }

      "not display the insolvency banner" in {
        elementExtinct(Selectors.insolvencyBanner)
      }
    }

    "the user is insolvent" should {

      val insolventViewModel = model.copy(showInsolvencyContent = true)
      lazy val view: Html = paymentHistoryView(insolventViewModel, Html(""), migratedWithinThreeYears = false)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "display the insolvency banner" in {
        elementText(Selectors.insolvencyBanner) shouldBe "You cannot view payments made before the insolvency date."
      }
    }
  }

  "The payments history page for an agent user" when {

    "the client's hybridToFullMigrationDate is not within the last three years" should {

      implicit val user: User = agentUser
      val entityName = "Capgemini"
      lazy val view: Html = paymentHistoryView(model.copy(clientName = Some(entityName)), Html(""), migratedWithinThreeYears = false)
      lazy val viewAsString = view.toString
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "Payment history - Your client’s VAT details - GOV.UK"
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe "Payment history"
      }

      "have the client name caption" in {
        elementText(Selectors.caption) shouldBe entityName
      }

      "not display the inset text" in {
        elementExtinct(Selectors.insetText)
      }

      "have the link to check what you owe" which {

        "has the correct text" in {
          elementText(Selectors.whatYouOweLink) shouldBe "Find out if your client owes anything to HMRC"
        }

        "has the correct href" in {
          element(Selectors.whatYouOweLink).attr("href") shouldBe controllers.routes.WhatYouOweController.show.url
        }
      }

      "render a Back link" which {

        "has the correct text" in {
          elementText(Selectors.backLink) shouldBe "Back"
        }

        "has the correct href" in {
          element(Selectors.backLink).attr("href") shouldBe mockConfig.agentClientLookupHubUrl
        }
      }

      "display a current year tab" in {
        elementText(Selectors.currentYearTab) shouldBe currentYear.toString
      }

      "display a previous year tab" in {
        elementText(Selectors.previousYearTab) shouldBe (currentYear - 1).toString
      }

      "display a tab for 2 years ago" in {
        elementText(Selectors.twoYearsAgoTab) shouldBe (currentYear - 2).toString
      }

      "not display a 'Previous payments' tab, despite the previousPaymentsTab boolean being set to true" in {
        elementExtinct(Selectors.previousPaymentsTab)
      }

      "have the correct current year tab content" which {

        "has the correct heading in the first column" in {
          elementText(Selectors.columnHeaading(1)) shouldBe "Date"
        }

        "has the correct heading in the second column" in {
          elementText(Selectors.columnHeaading(2)) shouldBe "Payment description"
        }

        "has the correct heading in the third column" in {
          elementText(Selectors.columnHeaading(3)) shouldBe "Your client paid HMRC"
        }

        "has the correct heading in the fourth column" in {
          elementText(Selectors.columnHeaading(4)) shouldBe "HMRC paid your client"
        }

        "has the correct date in the first column" in {
          elementText(Selectors.paymentDateTableContent) shouldBe "1 Mar"
        }

        "use a non breaking space to display the date in the first column" in {
          viewAsString.contains("1\u00a0Mar")
        }

        "has the correct charge description in the second column" in {
          elementText(Selectors.descriptionTableChargeType) shouldBe "VAT"
          elementText(Selectors.descriptionTableContent) shouldBe "for period 1 Jan to 1 Feb 2018"
        }

        "use a non breaking space to display the charge description in the second column" in {
          viewAsString.contains("VAT for period 1\u00a0Jan to 1\u00a0Feb\u00a02018")
        }

        "has the correct payment amount in the third column" in {
          elementText(Selectors.amountPaidTableContent) shouldBe "£100.00"
        }

        "has the correct repayment amount in the fourth column" in {
          elementText(Selectors.amountRepaidTableContent) shouldBe "£0.00"
        }
      }

      "have the correct previous year tab content" in {
        elementText(Selectors.previousYearNoPayments) shouldBe
          "You have not made or received any payments using the new VAT service this year."
      }

      "not have the 'Previous payments' tab content, despite the previousPaymentsTab boolean being set to true" in {
        elementExtinct(Selectors.previousPaymentsTab)
      }

      "not display the insolvency banner" in {
        elementExtinct(Selectors.insolvencyBanner)
      }
    }
  }

  "the client's hybridToFullMigrationDate is within the last three years" should {

    implicit val user: User = agentUser
    val entityName = "Capgemini"
    lazy val view: Html = paymentHistoryView(model.copy(clientName = Some(entityName)), Html(""), migratedWithinThreeYears = true)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct inset text" in {
      elementText(Selectors.insetText) shouldBe insetText
    }
  }

  "The webchat link is displayed" when {
    "the webchatEnabled feature switch is switched on for principal user" in {
      lazy val view = {
        mockConfig.features.webchatEnabled(true)
        paymentHistoryView(model, Html(""), migratedWithinThreeYears = false)
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      document.select("#webchatLink-id").text() shouldBe "Ask HMRC (opens in a new tab)"
      document.select("#webchatLink-id").attr("href") shouldBe "/ask-hmrc/chat/vat-online?ds"
    }

    "the webchatEnabled feature switch is switched on for an agent" in {
      lazy val view = {
        mockConfig.features.webchatEnabled(true)
        paymentHistoryView(model, Html(""), migratedWithinThreeYears = true)(request, messages, mockConfig, agentUser)
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      document.select("#webchatLink-id").text() shouldBe "Ask HMRC (opens in a new tab)"
      document.select("#webchatLink-id").attr("href") shouldBe "/ask-hmrc/chat/vat-online?ds"
    }
  }

  "The webchat link is not displayed" when {
    "the webchatEnabled feature switch is switched off for principal user" in {
      lazy val view = {
        mockConfig.features.webchatEnabled(false)
        paymentHistoryView(model, Html(""), migratedWithinThreeYears = false)
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      document.select("#webchatLink-id").size shouldBe 0
    }

    "the webchatEnabled feature switch is switched off for an agent" in {
      lazy val view = {
        mockConfig.features.webchatEnabled(false)
        paymentHistoryView(model, Html(""), migratedWithinThreeYears = true)(request, messages, mockConfig, agentUser)
      }
      lazy implicit val document: Document = Jsoup.parse(view.body)

      document.select("#webchatLink-id").size shouldBe 0
    }
  }
}
