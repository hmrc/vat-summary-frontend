/*
 * Copyright 2019 HM Revenue & Customs
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

import models.User
import models.payments._
import models.viewModels.{PaymentsHistoryModel, PaymentsHistoryViewModel}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec

class PaymentHistoryViewSpec extends ViewBaseSpec {

  object Selectors {
    val pageHeading = "h1"
    val paymentLink = "#payments a"
    val btaBreadcrumb = "div.breadcrumbs li:nth-of-type(1)"
    val btaBreadcrumbLink = "div.breadcrumbs li:nth-of-type(1) a"
    val vatBreadcrumb = "div.breadcrumbs li:nth-of-type(2)"
    val vatBreadcrumbLink = "div.breadcrumbs li:nth-of-type(2) a"
    val paymentHistoryBreadcrumb = "div.breadcrumbs li:nth-of-type(3)"
    val tabOne = ".tabs-nav li:nth-of-type(1)"
    val tabTwo = ".tabs-nav li:nth-of-type(2)"
    val tabThree = ".tabs-nav li:nth-of-type(3)"
    val tabOneHiddenText = ".tabs-nav li:nth-of-type(1) span"
    val tabTwoHiddenText = ".tabs-nav li:nth-of-type(2) span"
    val tabPreviousPaymentsHiddenText = ".tabs-nav li:nth-of-type(3) span"
    val subheading = "h2"
    val paymentDateTableHeading = "tr th:nth-of-type(1) div"
    val descriptionTableHeading = "tr th:nth-of-type(2) div"
    val amountPaidTableHeading = "tr th:nth-of-type(3) div"
    val paymentDateTableContent = "tr td:nth-of-type(1)"
    val mainParagraph = "#content > article > div.grid-row > div > p"
    val mainParagraphLink: String = mainParagraph + " > a"
    val descriptionTableChargeType = "tr td:nth-of-type(2) span.bold"
    val descriptionTableContent = "tr td:nth-of-type(2) span:nth-of-type(2)"
    val amountPaidTableContent = "tr td:nth-of-type(3)"
  }

  val currentYear = 2018
  val exampleAmount = 100

  val baseModel: PaymentsHistoryViewModel = PaymentsHistoryViewModel(
    None,
    None,
    previousPaymentsTab = false,
    Some(currentYear),
    Seq.empty,
    currentYear
  )

  override implicit val user: User = User("123456789")
  val vatDecUser = User("123456789", hasNonMtdVat = true)

  "Rendering the payments history page" when {

    "there is a single year of history retrieved" when {

      "there was a payment in the year" when {

        val model = baseModel.copy(transactions = Seq(PaymentsHistoryModel(
          chargeType = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.parse(s"2018-01-01")),
          taxPeriodTo = Some(LocalDate.parse(s"2018-02-01")),
          amount = exampleAmount,
          clearedDate = Some(LocalDate.parse(s"2018-03-01"))
        )))

        "the user does not have the VATDEC enrolment" should {

          lazy val view: Html = views.html.payments.paymentHistory(model)
          lazy implicit val document: Document = Jsoup.parse(view.body)

          "have the correct document title" in {
            document.title shouldBe "Payment history - Business tax account - GOV.UK"
          }

          "have the correct page heading" in {
            elementText(Selectors.pageHeading) shouldBe "Payment history"
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

            s"link to ${controllers.routes.VatDetailsController.details().url}" in {
              element(Selectors.vatBreadcrumbLink).attr("href") shouldBe controllers.routes.VatDetailsController.details().url
            }

            "have the text 'Payment history'" in {
              elementText(Selectors.paymentHistoryBreadcrumb) shouldBe "Payment history"
            }
          }

          "not display a current year tab" in {
            elementExtinct(Selectors.tabOne)
          }

          "not display a previous payments tab" in {
            elementExtinct(Selectors.tabTwo)
          }

          "have the current year subheading" in {
            elementText(Selectors.subheading) shouldBe currentYear.toString
          }

          "contain a charge type" which {

            "has the correct amount in the row" in {
              elementText(Selectors.amountPaidTableContent) shouldBe "- £100"
            }

            "has the correct title in the row" in {
              elementText(Selectors.descriptionTableChargeType) shouldBe "Return"
            }

            "has the correct description in the row" in {
              elementText(Selectors.descriptionTableContent) shouldBe "for the period 1 Jan to 1 Feb 2018"
            }

            "has the correct date in the row" in {
              elementText(Selectors.paymentDateTableContent) shouldBe "1 Mar 2018"
            }
          }
        }

        "the user has the VATDEC enrolment" when {

          "they migrated to MTD less than 15 months ago" should {

            lazy val view: Html = views.html.payments.paymentHistory(
              model.copy(tabOne = Some(currentYear), previousPaymentsTab = true)
            )(request, messages, mockConfig, messages.lang, vatDecUser)
            lazy implicit val document: Document = Jsoup.parse(view.body)

            "display the current year tab" in {
              element(Selectors.tabOne)
            }

            "display a previous payments tab" in {
              elementText(Selectors.tabTwo) should include("Previous payments")
            }

            "have the current year subheading" in {
              elementText(Selectors.subheading) shouldBe currentYear.toString
            }

            "contain a charge type" in {
              element(Selectors.paymentDateTableHeading)
            }
          }

          "they migrated to MTD 15 months ago or longer" should {

            lazy val view: Html =
              views.html.payments.paymentHistory(model)(request, messages, mockConfig, messages.lang, vatDecUser)
            lazy implicit val document: Document = Jsoup.parse(view.body)

            "not display a current year tab" in {
              elementExtinct(Selectors.tabOne)
            }

            "not display a previous payments tab" in {
              elementExtinct(Selectors.tabTwo)
            }

            "have the current year subheading" in {
              elementText(Selectors.subheading) shouldBe currentYear.toString
            }

            "contain a charge type" in {
              element(Selectors.paymentDateTableHeading)
            }
          }
        }
      }

      "there were no payments in the year" when {

        "the user does not have the VATDEC enrolment" when {

          lazy val view: Html = views.html.payments.paymentHistory(baseModel)
          lazy implicit val document: Document = Jsoup.parse(view.body)

          "not display a current year tab" in {
            elementExtinct(Selectors.tabOne)
          }

          "not display a previous payments tab" in {
            elementExtinct(Selectors.tabTwo)
          }

          "have the current year subheading" in {
            elementText(Selectors.subheading) shouldBe currentYear.toString
          }

          "not contain a charge type" in {
            elementExtinct(Selectors.paymentDateTableHeading)
          }

          "have the no payments message" in {
            elementText(Selectors.mainParagraph) shouldBe
              "You have not made or received any payments using the new VAT service this year."
          }
        }

        "the user has the VATDEC enrolment" when {

          "they migrated to MTD less than 15 months ago" should {

            lazy val view: Html = views.html.payments.paymentHistory(
              baseModel.copy(tabOne = Some(currentYear), previousPaymentsTab = true)
            )(request, messages, mockConfig, messages.lang, vatDecUser)
            lazy implicit val document: Document = Jsoup.parse(view.body)

            "display a current year tab" in {
              element(Selectors.tabOne)
            }

            "display a previous payments tab" in {
              elementText(Selectors.tabTwo) should include("Previous payments")
            }

            "have the current year subheading" in {
              elementText(Selectors.subheading) shouldBe currentYear.toString
            }

            "not contain a charge type" in {
              elementExtinct(Selectors.paymentDateTableHeading)
            }

            "have the no payments message" in {
              elementText(Selectors.mainParagraph) shouldBe
                "You have not made or received any payments using the new VAT service this year."
            }
          }

          "they migrated to MTD 15 months ago or longer" should {

            lazy val view: Html =
              views.html.payments.paymentHistory(baseModel)(request, messages, mockConfig, messages.lang, vatDecUser)
            lazy implicit val document: Document = Jsoup.parse(view.body)

            "not display a current year tab" in {
              elementExtinct(Selectors.tabOne)
            }

            "not display a previous payments tab" in {
              elementExtinct(Selectors.tabTwo)
            }

            "have the current year subheading" in {
              elementText(Selectors.subheading) shouldBe currentYear.toString
            }

            "not contain a charge type" in {
              elementExtinct(Selectors.paymentDateTableHeading)
            }

            "have the no payments message" in {
              elementText(Selectors.mainParagraph) shouldBe
                "You have not made or received any payments using the new VAT service this year."
            }
          }
        }
      }

      "the user has clicked the Previous Payments tab" should {

        lazy val view: Html = views.html.payments.paymentHistory(
          baseModel.copy(tabOne = Some(currentYear), selectedYear = None, previousPaymentsTab = true)
        )(request, messages, mockConfig, messages.lang, vatDecUser)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have the correct subheading" in {
          elementText(Selectors.subheading) shouldBe "Previous payments"
        }

        "have the previous payments message" in {
          elementText(Selectors.mainParagraph) shouldBe "You can view your previous payments (opens in new tab) " +
            "if you made payments before joining Making Tax Digital."
        }

        "have a link to the old VAT portal" which {

          "has the correct link text" in {
            elementText(Selectors.mainParagraphLink) shouldBe "view your previous payments (opens in new tab)"
          }

          "has the correct link href" in {
            element(Selectors.mainParagraphLink).attr("href") shouldBe
              mockConfig.portalNonHybridPreviousPaymentsUrl(vatDecUser.vrn)
          }

        }
      }
    }

    "there are multiple years retrieved" when {

      val model: PaymentsHistoryViewModel =
        baseModel.copy(tabOne = Some(currentYear), tabTwo = Some(currentYear - 1), transactions = Seq.empty)

      "the user does not have the VATDEC enrolment" when {

        lazy val view: Html = views.html.payments.paymentHistory(model)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "display a current year tab" in {
          element(Selectors.tabOne)
        }

        "display a previous year tab" in {
          element(Selectors.tabTwo)
        }

        "not display a previous payments tab" in {
          elementExtinct(Selectors.tabThree)
        }

        "have the current year subheading" in {
          elementText(Selectors.subheading) shouldBe currentYear.toString
        }

        "have the no payments message" in {
          elementText(Selectors.mainParagraph) shouldBe
            "You have not made or received any payments using the new VAT service this year."
        }
      }

      "the user has the VATDEC enrolment" when {

        "they migrated to MTD less than 15 months ago" should {

          lazy val view: Html = views.html.payments.paymentHistory(model.copy(previousPaymentsTab = true))(
            request, messages, mockConfig, messages.lang, vatDecUser)
          lazy implicit val document: Document = Jsoup.parse(view.body)

          "display a current year tab" in {
            element(Selectors.tabOne)
          }

          "display a previous year tab" in {
            element(Selectors.tabTwo)
          }

          "display a previous payments tab" in {
            elementText(Selectors.tabThree) should include("Previous payments")
          }

          "have the current year subheading" in {
            elementText(Selectors.subheading) shouldBe currentYear.toString
          }

          "have the no history message" in {
            elementText(Selectors.mainParagraph) shouldBe
              "You have not made or received any payments using the new VAT service this year."
          }
        }

        "they migrated to MTD 15 months ago or longer" should {

          lazy val view: Html =
            views.html.payments.paymentHistory(model)(request, messages, mockConfig, messages.lang, vatDecUser)
          lazy implicit val document: Document = Jsoup.parse(view.body)

          "display a current year tab" in {
            element(Selectors.tabOne)
          }

          "display a previous year tab" in {
            element(Selectors.tabTwo)
          }

          "not display a previous payments tab" in {
            elementExtinct(Selectors.tabThree)
          }

          "have the current year subheading" in {
            elementText(Selectors.subheading) shouldBe currentYear.toString
          }

          "not contain a charge type" in {
            elementExtinct(Selectors.paymentDateTableHeading)
          }
        }
      }
    }
  }
}
