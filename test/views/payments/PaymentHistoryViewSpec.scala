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

import models.payments._
import models.viewModels.{PaymentsHistoryModel, PaymentsHistoryViewModel}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec
import views.templates.PaymentsHistoryChargeHelper
import views.templates.PaymentsHistoryChargeHelper._
import views.templates.formatters.dates.DisplayDateRangeHelper.displayDateRange

class PaymentHistoryViewSpec extends ViewBaseSpec {

  lazy val datePeriodString: String = displayDateRange(LocalDate.parse("2018-01-01"), LocalDate.parse("2018-02-01"), useShortDayFormat = true)
  lazy val chargeDetailsForTest: Map[String, (String, String)] = Map(
    VatReturnCreditCharge.name -> (("Repayment from HMRC", s"for your $datePeriodString return")),
    VatReturnDebitCharge.name -> (("Return", s"for the period $datePeriodString")),
    VatOfficerAssessmentCreditCharge.name -> (("VAT officer's investigation", "for overpaying by this amount")),
    VatOfficerAssessmentDebitCharge.name -> (("VAT officer's investigation", "for underpaying by this amount")),
    VatCentralAssessment.name -> (("Estimate", s"for your $datePeriodString return")),
    VatDefaultSurcharge.name -> (("Surcharge", s"for late payment of your $datePeriodString return")),
    VatErrorCorrectionDebitCharge.name -> (("Error correction payment", s"for correcting your $datePeriodString return")),
    VatErrorCorrectionCreditCharge.name -> (("Error correction repayment from HMRC", s"for correcting your $datePeriodString return")),
    VatRepaymentSupplement.name -> (("Late repayment compensation from HMRC", s"we took too long to repay your $datePeriodString return")),
    OADefaultInterest.name -> (("VAT officer's assessment interest", s"interest charged on the officer's assessment")),
    VatBnpRegPre2010Charge.name -> (("Penalty for late registration", "because you should have been registered for VAT earlier")),
    VatBnpRegPost2010Charge.name -> (("Penalty for late registration", "because you should have been registered for VAT earlier")),
    VatFtnMatPre2010Charge.name -> (("Failure to notify penalty", "you did not tell us you are no longer exempt from VAT registration")),
    VatFtnMatPost2010Charge.name -> (("Failure to notify penalty", "you did not tell us you are no longer exempt from VAT registration")),
    VatMiscPenaltyCharge.name -> (("VAT general penalty", "")),
    VatOfficersAssessmentFurtherInterest.name -> (("VAT officer’s assessment further interest", "further interest charged on the officer’s assessment")),
    VatAdditionalAssessment.name -> (("Additional assessment", s"additional assessment based on further information for the period $datePeriodString")),
    VatAADefaultInterest.name -> (("Additional assessment interest", s"interest charged on additional tax assessed for the period $datePeriodString")),
    VatAAFurtherInterest.name -> ((
      "Additional assessment further interest",
      s"further interest charged on additional tax assessed for the period $datePeriodString")),
    VatStatutoryInterestCharge.name -> (("Statutory interest", "interest paid because of an error by HMRC")),
    VatSecurityDepositRequest.name -> (("Security deposit requirement", "because you have not paid VAT in your current or previous business(es)")),
    VatEcNoticeFurtherInterest.name -> (("Error correction further interest", "further interest charged on assessed amount")),
    CivilEvasionPenalty.name -> (("VAT civil evasion penalty", "because we have identified irregularities involving dishonesty")),
    VatInaccuraciesInECSales.name -> (("Inaccuracies penalty", "because you have provided inaccurate information in your EC sales list")),
    VatFailureToSubmitECSales.name -> (("EC sales list penalty", "because you have not submitted an EC sales list or you have submitted it late")),
    FtnEachPartner.name -> (("Failure to notify penalty", "because you did not tell us about all the partners and changes in your partnership")),
    VatOAInaccuracies2009.name -> (("Inaccuracies penalty", s"because you submitted an inaccurate document for the period $datePeriodString")),
    VatInaccuracyAssessmentsPenCharge.name -> (("Inaccuracies penalty", s"because you submitted an inaccurate document for the period $datePeriodString")),
    VatMpPre2009Charge.name -> (("Misdeclaration penalty", "because you have made an incorrect declaration")),
    VatMpRepeatedPre2009Charge.name -> (("Misdeclaration repeat penalty", "because you have repeatedly made incorrect declarations"))
  )

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
    val tabOneHiddenText = ".tabs-nav li:nth-of-type(1) span"
    val tabTwoHiddenText = ".tabs-nav li:nth-of-type(2) span"
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
          ))
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
          Seq.empty
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
          Seq.empty
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
      case class testModel(chargeType: ChargeType, expectedTitle: String, expectedDescription: String)

      PaymentsHistoryChargeHelper.values.map { case historyChargeHelper =>
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
            ))
        ),
          ChargeType.apply(historyChargeHelper.name).value,
          chargeDetailsForTest(historyChargeHelper.name)._1,
          chargeDetailsForTest(historyChargeHelper.name)._2)
      }.foreach { case (paymentHistoryModel, chargeTypeTitle, expectedTitle, expectedDescription) =>
        lazy val view = views.html.payments.paymentHistory(paymentHistoryModel)
        lazy implicit val document: Document = Jsoup.parse(view.body)

        s"contain a ${chargeTypeTitle} that" should {

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

    "supplying with a miscellaneous charge type" should {

      val paymentHistoryModel: PaymentsHistoryViewModel = PaymentsHistoryViewModel(
        historyYears,
        historyYears.head,
        Seq(PaymentsHistoryModel(
          chargeType = MiscPenaltyCharge,
          taxPeriodFrom = Some(LocalDate.parse("2018-01-01")),
          taxPeriodTo = Some(LocalDate.parse("2018-02-01")),
          amount = 1000.00,
          clearedDate = Some(LocalDate.parse("2018-03-01"))
        ))
      )

      lazy val view = views.html.payments.paymentHistory(paymentHistoryModel)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "contain a MiscPenaltyCharge" should {

        "contain the correct amount" in {
          elementText(Selectors.amountPaidTableContent(1)) shouldBe "- £1,000"
        }

        "contain the correct title" in {
          elementText(Selectors.descriptionTableChargeType(1)) shouldBe "VAT general penalty"
        }

        "not contain a description" in {
          document.select(Selectors.descriptionTableContent(1)).size() shouldBe 0
        }

        "contain the correct date" in {
          elementText(Selectors.paymentDateTableContent(1)) shouldBe "1 Mar 2018"
        }
      }
    }

    "supplying with a Vat Statutory Interest charge type" should {

      val paymentHistoryModel: PaymentsHistoryViewModel = PaymentsHistoryViewModel(
        historyYears,
        historyYears.head,
        Seq(PaymentsHistoryModel(
          chargeType = StatutoryInterestCharge,
          taxPeriodFrom = Some(LocalDate.parse("2018-01-01")),
          taxPeriodTo = Some(LocalDate.parse("2018-02-01")),
          amount = -111.00,
          clearedDate = Some(LocalDate.parse("2018-03-01"))
        ))
      )

      lazy val view = views.html.payments.paymentHistory(paymentHistoryModel)
      lazy implicit val document: Document = Jsoup.parse(view.body)

      "contain a VatStatutoryInterest charge" should {

        "contain the correct title" in {
          elementText(Selectors.descriptionTableChargeType(1)) shouldBe "Statutory interest"
        }

        "contain the correct description" in {
          elementText(Selectors.descriptionTableContent(1)) shouldBe "interest paid because of an error by HMRC"
        }

        "contain the correct amount" in {
          elementText(Selectors.amountPaidTableContent(1)) shouldBe "+ £111"
        }

        "contain the correct date" in {
          elementText(Selectors.paymentDateTableContent(1)) shouldBe "1 Mar 2018"
        }
      }
    }
  }
}
