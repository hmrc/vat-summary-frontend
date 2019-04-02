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

import common.MessageLookup.PaymentMessages
import config.AppConfig
import models.payments._
import models.viewModels.{PaymentsHistoryModel, PaymentsHistoryViewModel}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.templates.payments.PaymentMessageHelper

class PaymentHistoryViewSpec extends ViewBaseSpec {

  val appConfig = app.injector.instanceOf[AppConfig]

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
    val tabPreviousPayments = ".tabs-nav li:nth-of-type(3)"
    val tabOneHiddenText = ".tabs-nav li:nth-of-type(1) span"
    val tabTwoHiddenText = ".tabs-nav li:nth-of-type(2) span"
    val tabPreviousPaymentsHiddenText = ".tabs-nav li:nth-of-type(3) span"
    val tabHeading = "h2"
    val paymentDateTableHeading = "tr th:nth-of-type(1) div"
    val descriptionTableHeading = "tr th:nth-of-type(2) div"
    val amountPaidTableHeading = "tr th:nth-of-type(3) div"
    val paymentDateTableContent = "tr td:nth-of-type(1)"
    val noPaymentsText = ".column-two-thirds > p"

    def paymentDateTableContent(row: Int): String = s"tr:nth-of-type($row) td:nth-of-type(1)"

    val descriptionTableChargeType = "tr td:nth-of-type(2) span.bold"

    def descriptionTableChargeType(row: Int): String = s"tr:nth-of-type($row) td:nth-of-type(2) span.bold"

    val descriptionTableContent = "tr td:nth-of-type(2) span:nth-of-type(2)"

    def descriptionTableContent(row: Int): String = s"tr:nth-of-type($row) td:nth-of-type(2) span:nth-of-type(2)"

    val amountPaidTableContent = "tr td:nth-of-type(3)"

    def amountPaidTableContent(row: Int): String = s"tr:nth-of-type($row) td:nth-of-type(3)"

    val noHistoryContent = "div.column-two-thirds p:nth-of-type(1)"
    val noHistoryWillShowContent = "div.column-two-thirds p:nth-of-type(2)"
    val noHistoryBullet1 = "div.column-two-thirds li:nth-of-type(1)"
    val noHistoryBullet2 = "div.column-two-thirds li:nth-of-type(2)"
  }

  "Rendering the payments history page" when {

    val currentYear = 2018
    val previousYear = 2017
    val historyYears = Seq(currentYear, previousYear)
    val vrn = "999999999"

    "there are multiple payment histories to display" should {

      val paymentHistoryModel: PaymentsHistoryViewModel = PaymentsHistoryViewModel(
        historyYears,
        historyYears.head,
        Seq(PaymentsHistoryModel(
          chargeType = ReturnDebitCharge,
          taxPeriodFrom = Some(LocalDate.parse(s"2018-01-01")),
          taxPeriodTo = Some(LocalDate.parse(s"2018-02-01")),
          amount = 123456789,
          clearedDate = Some(LocalDate.parse(s"2018-03-01"))
        ),
          PaymentsHistoryModel(
            chargeType = ReturnDebitCharge,
            taxPeriodFrom = Some(LocalDate.parse(s"2018-03-01")),
            taxPeriodTo = Some(LocalDate.parse(s"2018-04-01")),
            amount = 987654321,
            clearedDate = Some(LocalDate.parse(s"2018-03-01"))
          )),
        hasHmrcVatDecOrg = true,
        userVrn = vrn,
        customerMigratedToETMPDateWithin15M = true
      )

      lazy val view = views.html.payments.paymentHistory(paymentHistoryModel)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "have the correct document title" in {
        document.title shouldBe "Payment history"
      }

      "have the correct page heading" in {
        elementText(Selectors.pageHeading) shouldBe "Payment history"
      }

      "render breadcrumbs which" should {

        "have the text 'Business tax account'" in {
          elementText(Selectors.btaBreadcrumb) shouldBe "Business tax account"
        }

        "link to bta" in {
          element(Selectors.btaBreadcrumbLink).attr("href") shouldBe "bta-url"
        }

        "have the text 'Your VAT details'" in {
          elementText(Selectors.vatBreadcrumb) shouldBe "Your VAT details"
        }

        s"link to ${controllers.routes.VatDetailsController.details().url}" in {
          element(Selectors.vatBreadcrumbLink).attr("href") shouldBe controllers.routes.VatDetailsController.details().url
        }

        "have the text 'Payment history'" in {
          elementText(Selectors.paymentHistoryBreadcrumb) shouldBe "Payment history"
        }
      }

      "have tabs for each return year" should {

        "tab one" should {

          "have the text '2018'" in {
            elementText(Selectors.tabOne) should include("2018")
          }

          "contain visually hidden text" in {
            elementText(Selectors.tabOneHiddenText) shouldBe "Currently viewing payment history from 2018"
          }
        }

        "tab two" should {

          "have the text '2017'" in {
            elementText(Selectors.tabTwo) should include("2017")
          }

          s"contain a link to ${controllers.routes.PaymentHistoryController.paymentHistory(previousYear).url}" in {
            element(Selectors.tabTwo).select("a").attr("href") shouldBe
              controllers.routes.PaymentHistoryController.paymentHistory(previousYear).url
          }

          "contain visually hidden text" in {
            elementText(Selectors.tabTwoHiddenText) shouldBe "View payment history from 2017"
          }
        }
      }

      "have a Previous payments tab if the customer is migrated to ETMP within 15 months " +
        "and the customer has the HMCE-VATDEC-ORG enrolment. The tab" should {

        "have the correct text" in {
          elementText(Selectors.tabPreviousPayments) should startWith("Previous payments")
        }

        "contain visually hidden text" in {
          elementText(Selectors.tabPreviousPaymentsHiddenText) shouldBe "View previous payments"
        }

        "contain a link to the portal payments page" in {
          element(Selectors.tabPreviousPayments).select("a").attr("href") shouldBe "/paymentHistoryNonHybridPortal"
        }

      }

      "have the correct tab heading" in {
        elementText(Selectors.tabHeading) shouldBe "2018"
      }

      "have the correct payment date table heading" in {
        elementText(Selectors.paymentDateTableHeading) shouldBe "Payment received"
      }

      "have the correct description table heading" in {
        elementText(Selectors.descriptionTableHeading) shouldBe "Description"
      }

      "have the correct amount paid table heading" in {
        elementText(Selectors.amountPaidTableHeading) shouldBe "Amount"
      }

      "have the visuallyhidden attribute on the payment date table heading" in {
        element(Selectors.paymentDateTableHeading).attr("class") shouldBe "visuallyhidden"
      }

      "have the visuallyhidden attribute on the description table heading" in {
        element(Selectors.descriptionTableHeading).attr("class") shouldBe "visuallyhidden"
      }

      "have the visuallyhidden attribute on the amount paid table heading" in {
        element(Selectors.amountPaidTableHeading).attr("class") shouldBe "visuallyhidden"
      }

      "have the correct payment date table content" in {
        elementText(Selectors.paymentDateTableContent) shouldBe "1 Mar 2018"
      }

      "have the correct description table charge type" in {
        elementText(Selectors.descriptionTableChargeType) shouldBe "Return"
      }

      "have the correct description table content" in {
        elementText(Selectors.descriptionTableContent) shouldBe "for the period 1 Jan to 1 Feb 2018"
      }

      "have the correct amount paid table content" in {
        elementText(Selectors.amountPaidTableContent) shouldBe "- £123,456,789"
      }
    }

    "there are no payments" when {

      "the previous year tab is selected" should {
        val paymentHistoryModel: PaymentsHistoryViewModel = PaymentsHistoryViewModel(
          historyYears,
          previousYear,
          Seq.empty,
          hasHmrcVatDecOrg = true,
          "999999999",
          customerMigratedToETMPDateWithin15M = true
        )

        lazy val view = views.html.payments.paymentHistory(paymentHistoryModel)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have tabs for each return year" should {

          "tab one" should {

            "have the text '2018'" in {
              elementText(Selectors.tabOne) should include("2018")
            }

            s"contain a link to ${controllers.routes.PaymentHistoryController.paymentHistory(currentYear).url}" in {
              element(Selectors.tabOne).select("a").attr("href") shouldBe
                controllers.routes.PaymentHistoryController.paymentHistory(currentYear).url
            }

            "contain visually hidden text" in {
              elementText(Selectors.tabOneHiddenText) shouldBe "View payment history from 2018"
            }
          }

          "tab two" should {

            "have the text '2017'" in {
              elementText(Selectors.tabTwo) should include("2017")
            }

            "contain visually hidden text" in {
              elementText(Selectors.tabTwoHiddenText) shouldBe "Currently viewing payment history from 2017"
            }
          }
        }

        "have the correct tab heading" in {
          elementText(Selectors.tabHeading) shouldBe "2017"
        }

        "have the correct text in past tense" in {
          elementText(Selectors.noPaymentsText) shouldBe "You did not make or receive any payments this year."
        }
      }

      "the current year tab is selected" should {

        val paymentHistoryModel: PaymentsHistoryViewModel = PaymentsHistoryViewModel(
          historyYears,
          currentYear,
          Seq.empty,
          hasHmrcVatDecOrg = true,
          "999999999",
          customerMigratedToETMPDateWithin15M = true
        )

        lazy val view = views.html.payments.paymentHistory(paymentHistoryModel)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        "have tabs for each return year" should {

          "tab one" should {

            "have the text '2018'" in {
              elementText(Selectors.tabOne) should include("2018")
            }

            "contain visually hidden text" in {
              elementText(Selectors.tabOneHiddenText) shouldBe "Currently viewing payment history from 2018"
            }
          }

          "tab two" should {

            "have the text '2017'" in {
              elementText(Selectors.tabTwo) should include("2017")
            }

            s"contain a link to ${controllers.routes.PaymentHistoryController.paymentHistory(previousYear).url}" in {
              element(Selectors.tabTwo).select("a").attr("href") shouldBe
                controllers.routes.PaymentHistoryController.paymentHistory(previousYear).url
            }

            "contain visually hidden text" in {
              elementText(Selectors.tabTwoHiddenText) shouldBe "View payment history from 2017"
            }
          }
        }

        "have the correct tab heading" in {
          elementText(Selectors.tabHeading) shouldBe "2018"
        }

        "have the correct text in present tense" in {
          elementText(Selectors.noPaymentsText) shouldBe "You have not yet made or received any payments this year."
        }
      }
    }

    "supplying with the following charge types" should {

      PaymentMessageHelper.values.map { historyChargeHelper =>
        (PaymentsHistoryViewModel(
          historyYears,
          historyYears.head,
          Seq(PaymentsHistoryModel(
            chargeType = ChargeType.apply(historyChargeHelper.name),
            taxPeriodFrom = Some(LocalDate.parse(s"2018-01-01")),
            taxPeriodTo = Some(LocalDate.parse(s"2018-02-01")),
            amount = 1000.00,
            clearedDate = Some(LocalDate.parse(s"2018-03-01"))
          ),
            PaymentsHistoryModel(
              chargeType = ChargeType.apply(historyChargeHelper.name),
              taxPeriodFrom = Some(LocalDate.parse(s"2018-01-01")),
              taxPeriodTo = Some(LocalDate.parse(s"2018-02-01")),
              amount = 500.00,
              clearedDate = Some(LocalDate.parse(s"2018-03-01"))
            )),
          hasHmrcVatDecOrg = true,
          "999999999",
          customerMigratedToETMPDateWithin15M = true
        ),
          ChargeType.apply(historyChargeHelper.name).value,
          PaymentMessages.getMessagesForChargeType(historyChargeHelper.name)._1,
          PaymentMessages.getMessagesForChargeType(historyChargeHelper.name)._2)
      }.foreach { case (paymentHistoryModel, chargeTypeTitle, expectedTitle, expectedDescription) =>
        lazy val view = views.html.payments.paymentHistory(paymentHistoryModel)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        s"contain a $chargeTypeTitle that" should {

          "contain the correct amount in row 1" in {
            elementText(Selectors.amountPaidTableContent(1)) shouldBe "- £1,000"
          }
          "contain the correct title in row 1" in {
            elementText(Selectors.descriptionTableChargeType(1)) shouldBe expectedTitle
          }
          if (expectedDescription.nonEmpty) {
            "contain the correct description in row 1" in {
              elementText(Selectors.descriptionTableContent(1)) shouldBe expectedDescription
            }
          }
          "contain the correct date in row 1" in {
            elementText(Selectors.paymentDateTableContent(1)) shouldBe "1 Mar 2018"
          }

          "contain the correct amount in row 2" in {
            elementText(Selectors.amountPaidTableContent(2)) shouldBe "- £500"
          }
          "contain the correct title in row 2" in {
            elementText(Selectors.descriptionTableChargeType(2)) shouldBe expectedTitle
          }
          if (expectedDescription.nonEmpty) {
            "contain the correct description in row 2" in {
              elementText(Selectors.descriptionTableContent(2)) shouldBe expectedDescription
            }
          }
          "contain the correct date in row 2" in {
            elementText(Selectors.paymentDateTableContent(2)) shouldBe "1 Mar 2018"
          }
        }
      }
    }
  }
}
