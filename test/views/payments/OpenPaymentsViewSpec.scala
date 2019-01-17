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
import models.viewModels.OpenPaymentsViewModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class OpenPaymentsViewSpec extends ViewBaseSpec {

  mockConfig.features.allowDirectDebits(true)

  object Selectors {
    val pageHeading = "h1"
    val paymentLink = "#payments a"
    val correctErrorLink = "details p:nth-of-type(1) a"
    val btaBreadcrumb = "div.breadcrumbs li:nth-of-type(1)"
    val btaBreadcrumbLink = "div.breadcrumbs li:nth-of-type(1) a"
    val vatBreadcrumb = "div.breadcrumbs li:nth-of-type(2)"
    val vatBreadcrumbLink = "div.breadcrumbs li:nth-of-type(2) a"
    val paymentBreadcrumb = "div.breadcrumbs li:nth-of-type(3)"

    val paymentSection: Int => String = number => s"#payment-section-$number"
    val paymentAmount: Int => String = number => s"#payment-section-$number span:nth-of-type(1)"
    val paymentAmountData: Int => String = number => s"#payment-section-$number span[data-amount]"
    val paymentDue: Int => String = number => s"#payment-row-$number span:nth-of-type(1)"
    val paymentDueData: Int => String = number => s"#payment-row-$number span[data-due]"
    val paymentPayLink: Int => String = number => s"#payment-row-$number a"
    val paymentPayContext: Int => String = number => s"#payment-row-$number span.float--right"
    val paymentPayNowLinkText: Int => String = number => s"#payment-row-$number div:nth-of-type(2) span:nth-of-type(1)"
    val paymentPayNowContext: Int => String = number => s"#payment-row-$number div:nth-of-type(2) span:nth-of-type(2)"

    lazy val paymentViewReturnLink: Int => String = number => s"#links-section-$number div:nth-of-type(2) a"
    val paymentPeriod: Int => String = number => s"#payment-row-$number span:nth-of-type(3)"
    val paymentViewReturnText: Int => String = number => s"#links-section-$number div:nth-of-type(2) a span:nth-of-type(1)"
    val paymentViewReturnContext: Int => String = number => s"#links-section-$number div:nth-of-type(2) a"
    val secondPaymentPeriod = "#payment-row-2 span:nth-of-type(2)"

    val processingTime = "#processing-time"
    val processingTimeOld = "#processing-time-old"
    val directDebit = "#direct-debits"
    val directDebitText = "#check-direct-debit p:nth-of-type(1)"
    val directDebitLink = "#check-direct-debit a:nth-of-type(1)"
    val helpText = "details p:nth-of-type(1)"
    val helpMakePayment = "details p:nth-of-type(2)"
    val helpSummaryRevealLink = "summary span:nth-of-type(1)"
    val overdueLabel = ".task-overdue"
    val makePayment = "#vatPaymentsLink"
  }

  private val user = User("1111")
  val noPayment = Seq()
  val payments = Seq(
    OpenPaymentsModelWithPeriod(
      chargeType = ReturnDebitCharge,
      amount = 2000000000.01,
      due = LocalDate.parse("2001-04-08"),
      start = LocalDate.parse("2001-01-01"),
      end = LocalDate.parse("2001-03-31"),
      periodKey = "#001",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      ReturnDebitCharge,
      100.00,
      LocalDate.parse("2002-05-10"),
      LocalDate.parse("2002-02-01"),
      LocalDate.parse("2002-03-28"),
      "#002"
    ),
    OpenPaymentsModelWithPeriod(
      AAInterestCharge,
      300.00,
      LocalDate.parse("2003-04-05"),
      LocalDate.parse("2003-01-01"),
      LocalDate.parse("2003-03-31"),
      "#003",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      AAFurtherInterestCharge,
      400.00,
      LocalDate.parse("2004-04-05"),
      LocalDate.parse("2004-01-01"),
      LocalDate.parse("2004-03-31"),
      "#004",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      AACharge,
      500.00,
      LocalDate.parse("2005-04-05"),
      LocalDate.parse("2005-01-01"),
      LocalDate.parse("2005-03-31"),
      "#005",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      OACharge,
      600.00,
      LocalDate.parse("2006-04-05"),
      LocalDate.parse("2006-01-01"),
      LocalDate.parse("2006-03-31"),
      "#006",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      BnpRegPre2010Charge,
      700.00,
      LocalDate.parse("2007-04-05"),
      LocalDate.parse("2007-01-01"),
      LocalDate.parse("2007-03-31"),
      "#007",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      BnpRegPost2010Charge,
      800.00,
      LocalDate.parse("2008-04-05"),
      LocalDate.parse("2008-01-01"),
      LocalDate.parse("2008-03-31"),
      "#008",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      FtnMatPre2010Charge,
      900.00,
      LocalDate.parse("2009-04-05"),
      LocalDate.parse("2009-01-01"),
      LocalDate.parse("2009-03-31"),
      "#009",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      FtnMatPost2010Charge,
      1000.00,
      LocalDate.parse("2010-04-05"),
      LocalDate.parse("2010-01-01"),
      LocalDate.parse("2010-03-31"),
      "#010",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      MiscPenaltyCharge,
      1100.00,
      LocalDate.parse("2011-04-05"),
      LocalDate.parse("2011-01-01"),
      LocalDate.parse("2011-03-31"),
      "#011",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      FtnEachPartnerCharge,
      1200.00,
      LocalDate.parse("2012-04-05"),
      LocalDate.parse("2012-01-01"),
      LocalDate.parse("2012-03-31"),
      "#012",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      MpPre2009Charge,
      1300.00,
      LocalDate.parse("2013-04-05"),
      LocalDate.parse("2013-01-01"),
      LocalDate.parse("2013-03-31"),
      "#013",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      MpRepeatedPre2009Charge,
      1400.00,
      LocalDate.parse("2014-04-05"),
      LocalDate.parse("2014-01-01"),
      LocalDate.parse("2014-03-31"),
      "#014",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      CivilEvasionPenaltyCharge,
      1500.00,
      LocalDate.parse("2015-04-05"),
      LocalDate.parse("2015-01-01"),
      LocalDate.parse("2015-03-31"),
      "#015",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      VatInaccuraciesInECSalesCharge,
      1600.00,
      LocalDate.parse("2016-04-05"),
      LocalDate.parse("2016-01-01"),
      LocalDate.parse("2016-03-31"),
      "#016",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      VatFailureToSubmitECSalesCharge,
      1700.00,
      LocalDate.parse("2017-04-05"),
      LocalDate.parse("2017-01-01"),
      LocalDate.parse("2017-03-31"),
      "#017",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      VatECDefaultInterestCharge,
      1800.00,
      LocalDate.parse("2017-04-05"),
      LocalDate.parse("2017-01-01"),
      LocalDate.parse("2017-03-31"),
      "#018",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      VatECFurtherInterestCharge,
      1900.00,
      LocalDate.parse("2017-04-05"),
      LocalDate.parse("2017-01-01"),
      LocalDate.parse("2017-03-31"),
      "#019",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      VatSecurityDepositRequestCharge,
      2000.00,
      LocalDate.parse("2018-04-05"),
      LocalDate.parse("2018-01-01"),
      LocalDate.parse("2018-03-31"),
      "#020",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      VatProtectiveAssessmentCharge,
      2100.00,
      LocalDate.parse("2018-04-05"),
      LocalDate.parse("2018-01-01"),
      LocalDate.parse("2018-03-31"),
      "#021",
      overdue = true
    ),
    OpenPaymentsModelWithPeriod(
      VatPADefaultInterestCharge,
      2200.00,
      LocalDate.parse("2018-04-05"),
      LocalDate.parse("2018-01-01"),
      LocalDate.parse("2018-03-31"),
      "#022",
      overdue = true
    ),
    OpenPaymentsModel(
      VatPaFurtherInterestCharge,
      1800.00,
      LocalDate.parse("2018-04-05"),
      LocalDate.parse("2018-01-01"),
      LocalDate.parse("2018-03-31"),
      "#018",
      overdue = true
    )
  )

  "Rendering the open payments page when a user has a direct debit" should {

    val hasDirectDebit = Some(true)
    val viewModel = OpenPaymentsViewModel(payments, hasDirectDebit)
    lazy val view = views.html.payments.openPayments(user, viewModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "have the correct document title" in {
      document.title shouldBe "What you owe"
    }

    "have the correct page heading" in {
      elementText(Selectors.pageHeading) shouldBe "What you owe"
    }

    "render breadcrumbs which" should {

      "have the text 'Business tax account'" in {
        elementText(Selectors.btaBreadcrumb) shouldBe "Business tax account"
      }

      "link to bta" in {
        element(Selectors.btaBreadcrumbLink).attr("href") shouldBe "bta-url"
      }

      "have the text 'VAT'" in {
        elementText(Selectors.vatBreadcrumb) shouldBe "Your VAT details"
      }

      s"link to ${controllers.routes.VatDetailsController.details().url}" in {
        element(Selectors.vatBreadcrumbLink).attr("href") shouldBe controllers.routes.VatDetailsController.details().url
      }

      s"link to https://www.gov.uk/vat-corrections" in {
        element(Selectors.correctErrorLink).attr("href") shouldBe "https://www.gov.uk/vat-corrections"
      }

      "have the text 'What you owe'" in {
        elementText(Selectors.paymentBreadcrumb) shouldBe "What you owe"
      }
    }

    "for the first payment" should {

      "render the correct amount for the first payment" in {
        elementText(Selectors.paymentAmount(1)) shouldBe "£2,000,000,000.01"
      }

      "render the correct amount for the first payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(1)) shouldBe "£2,000,000,000.01"
      }

      "render the correct due period for the first payment" in {
        elementText(Selectors.paymentDue(1)) shouldBe "due by 8 April 2001"
      }

      "render the correct due period for the first payment period data attribute" in {
        elementText(Selectors.paymentDueData(1)) shouldBe "due by 8 April 2001"
      }

      "render the Direct Debit text for the first payment" in {
        elementText(Selectors.paymentPayContext(1)) shouldBe "You pay by direct debit"
      }

      "render the correct due period for the first payment period" in {
        elementText(Selectors.paymentPeriod(1)) shouldBe "for the period 1 January to 31 March 2001"
      }

      "render the correct view return link text for the first payment" in {
        elementText(Selectors.paymentViewReturnText(1)) shouldBe "View return"
      }

      "render the correct view return link text for the first payment with hidden text for context" in {
        elementText(Selectors.paymentViewReturnContext(1)) shouldBe "View return for the period 1 January to 31 March 2001"
      }

      "render the correct view return href for the first payment" in {
        element(Selectors.paymentViewReturnLink(1)).attr("href") shouldBe "/submitted/%23001"
      }
    }

    "for the second payment" should {

      "render the correct amount for the second payment" in {
        elementText(Selectors.paymentAmount(2)) shouldBe "£100"
      }

      "render the correct amount for the second payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(2)) shouldBe "£100"
      }

      "render the correct due period for the second payment" in {
        elementText(Selectors.paymentDue(2)) shouldBe "due by 10 May 2002"
      }

      "render the correct due period for the second payment period data attribute" in {
        elementText(Selectors.paymentDueData(2)) shouldBe "due by 10 May 2002"
      }

      "render the correct due period for the second payment period" in {
        elementText(Selectors.secondPaymentPeriod) shouldBe "for the period 1 February to 28 March 2002"
      }

      "render the correct view return link text for the second payment" in {
        elementText(Selectors.paymentViewReturnText(2)) shouldBe "View return"
      }

      "render the correct view return link text for the second payment with hidden text for context" in {
        elementText(Selectors.paymentViewReturnContext(2)) shouldBe "View return for the period 1 February to 28 March 2002"
      }

      "render the correct view return href for the second payment" in {
        element(Selectors.paymentViewReturnLink(2)).attr("href") shouldBe "/submitted/%23002"
      }
    }

    "for the third payment" should {

      "render the correct amount for the third payment" in {
        elementText(Selectors.paymentAmount(3)) shouldBe "£300"
      }

      "render the correct amount for the third payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(3)) shouldBe "£300"
      }

      "render the correct due period for the third payment" in {
        elementText(Selectors.paymentDue(3)) shouldBe "due by 5 April 2003"
      }

      "render the correct due period for the third payment period data attribute" in {
        elementText(Selectors.paymentDueData(3)) shouldBe "due by 5 April 2003"
      }

      "not display a view return link text for the third payment" in {
        document.select(Selectors.paymentViewReturnText(3)).size shouldBe 0
      }
    }

    "for the 4th payment" should {

      "render the correct amount for the 4th payment" in {
        elementText(Selectors.paymentAmount(4)) shouldBe "£400"
      }

      "render the correct amount for the 4th payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(4)) shouldBe "£400"
      }

      "render the correct due period for the 4th payment" in {
        elementText(Selectors.paymentDue(4)) shouldBe "due by 5 April 2004"
      }

      "render the correct due period for the 4th payment period data attribute" in {
        elementText(Selectors.paymentDueData(4)) shouldBe "due by 5 April 2004"
      }

      "not display a view return link text for the 4th payment" in {
        document.select(Selectors.paymentViewReturnText(4)).size shouldBe 0
      }
    }

    "for the 5th payment" should {

      "render the correct amount for the 5th payment" in {
        elementText(Selectors.paymentAmount(5)) shouldBe "£500"
      }

      "render the correct amount for the 5th payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(5)) shouldBe "£500"
      }

      "render the correct due period for the 5th payment" in {
        elementText(Selectors.paymentDue(5)) shouldBe "due by 5 April 2005"
      }

      "render the correct due period for the 5th payment period data attribute" in {
        elementText(Selectors.paymentDueData(5)) shouldBe "due by 5 April 2005"
      }

      "not display a view return link text for the 5th payment" in {
        document.select(Selectors.paymentViewReturnText(5)).size shouldBe 0
      }
    }

    "for the 6th payment" should {

      "render the correct amount for the 6th payment" in {
        elementText(Selectors.paymentAmount(6)) shouldBe "£600"
      }

      "render the correct amount for the 6th payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(6)) shouldBe "£600"
      }

      "render the correct due period for the 6th payment" in {
        elementText(Selectors.paymentDue(6)) shouldBe "due by 5 April 2006"
      }

      "render the correct due period for the 6th payment period data attribute" in {
        elementText(Selectors.paymentDueData(6)) shouldBe "due by 5 April 2006"
      }

      "not display a view return link text for the 6th payment" in {
        document.select(Selectors.paymentViewReturnText(6)).size shouldBe 0
      }
    }

    "for the 7th payment" should {

      "render the correct amount for the 7th payment" in {
        elementText(Selectors.paymentAmount(7)) shouldBe "£700"
      }

      "render the correct amount for the 7th payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(7)) shouldBe "£700"
      }

      "render the correct due period for the 7th payment" in {
        elementText(Selectors.paymentDue(7)) shouldBe "due by 5 April 2007"
      }

      "render the correct due period for the 7th payment period data attribute" in {
        elementText(Selectors.paymentDueData(7)) shouldBe "due by 5 April 2007"
      }

      "not display a view return link text for the 7th payment" in {
        document.select(Selectors.paymentViewReturnText(7)).size shouldBe 0
      }
    }

    "for the 8th payment" should {

      "render the correct amount for the 8th payment" in {
        elementText(Selectors.paymentAmount(8)) shouldBe "£800"
      }

      "render the correct amount for the 8th payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(8)) shouldBe "£800"
      }

      "render the correct due period for the 8th payment" in {
        elementText(Selectors.paymentDue(8)) shouldBe "due by 5 April 2008"
      }

      "render the correct due period for the 8th payment period data attribute" in {
        elementText(Selectors.paymentDueData(8)) shouldBe "due by 5 April 2008"
      }

      "not display a view return link text for the 8th payment" in {
        document.select(Selectors.paymentViewReturnText(8)).size shouldBe 0
      }
    }

    "for the 9th payment" should {

      "render the correct amount for the 9th payment" in {
        elementText(Selectors.paymentAmount(9)) shouldBe "£900"
      }

      "render the correct amount for the 9th payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(9)) shouldBe "£900"
      }

      "render the correct due period for the 9th payment" in {
        elementText(Selectors.paymentDue(9)) shouldBe "due by 5 April 2009"
      }

      "render the correct due period for the 9th payment period data attribute" in {
        elementText(Selectors.paymentDueData(9)) shouldBe "due by 5 April 2009"
      }

      "not display a view return link text for the 9th payment" in {
        document.select(Selectors.paymentViewReturnText(9)).size shouldBe 0
      }
    }

    "for the 10th payment" should {

      "render the correct amount for the 10th payment" in {
        elementText(Selectors.paymentAmount(10)) shouldBe "£1,000"
      }

      "render the correct amount for the 10th payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(10)) shouldBe "£1,000"
      }

      "render the correct due period for the 10th payment" in {
        elementText(Selectors.paymentDue(10)) shouldBe "due by 5 April 2010"
      }

      "render the correct due period for the 10th payment period data attribute" in {
        elementText(Selectors.paymentDueData(10)) shouldBe "due by 5 April 2010"
      }

      "not display a view return link text for the 10th payment" in {
        document.select(Selectors.paymentViewReturnText(10)).size shouldBe 0
      }
    }

    "for the 11th payment" should {

      "render the correct amount for the 11th payment" in {
        elementText(Selectors.paymentAmount(11)) shouldBe "£1,100"
      }

      "render the correct amount for the 11th payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(11)) shouldBe "£1,100"
      }

      "render the correct due period for the 11th payment" in {
        elementText(Selectors.paymentDue(11)) shouldBe "due by 5 April 2011"
      }

      "render the correct due period for the 11th payment period data attribute" in {
        elementText(Selectors.paymentDueData(11)) shouldBe "due by 5 April 2011"
      }

      "not display a view return link text for the 11th payment" in {
        document.select(Selectors.paymentViewReturnText(11)).size shouldBe 0
      }
    }

    "for the 12th payment" should {

      "render the correct amount for the 12th payment" in {
        elementText(Selectors.paymentAmount(12)) shouldBe "£1,200"
      }

      "render the correct amount for the 12th payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(12)) shouldBe "£1,200"
      }

      "render the correct due period for the 12th payment" in {
        elementText(Selectors.paymentDue(12)) shouldBe "due by 5 April 2012"
      }

      "render the correct due period for the 12th payment period data attribute" in {
        elementText(Selectors.paymentDueData(12)) shouldBe "due by 5 April 2012"
      }

      "not display a view return link text for the 12th payment" in {
        document.select(Selectors.paymentViewReturnText(12)).size shouldBe 0
      }
    }

    "for the 13th payment" should {

      "render the correct amount for the 13th payment" in {
        elementText(Selectors.paymentAmount(13)) shouldBe "£1,300"
      }

      "render the correct amount for the 13th payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(13)) shouldBe "£1,300"
      }

      "render the correct due period for the 13th payment" in {
        elementText(Selectors.paymentDue(13)) shouldBe "due by 5 April 2013"
      }

      "render the correct due period for the 13th payment period data attribute" in {
        elementText(Selectors.paymentDueData(13)) shouldBe "due by 5 April 2013"
      }

      "not display a view return link text for the 13th payment" in {
        document.select(Selectors.paymentViewReturnText(13)).size shouldBe 0
      }
    }

    "for the 14th payment" should {

      "render the correct amount for the 14th payment" in {
        elementText(Selectors.paymentAmount(14)) shouldBe "£1,400"
      }

      "render the correct amount for the 14th payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(14)) shouldBe "£1,400"
      }

      "render the correct due period for the 14th payment" in {
        elementText(Selectors.paymentDue(14)) shouldBe "due by 5 April 2014"
      }

      "render the correct due period for the 14th payment period data attribute" in {
        elementText(Selectors.paymentDueData(14)) shouldBe "due by 5 April 2014"
      }

      "not display a view return link text for the 14th payment" in {
        document.select(Selectors.paymentViewReturnText(14)).size shouldBe 0
      }
    }

    "for the 15th payment" should {

      "render the correct amount for the 15th payment" in {
        elementText(Selectors.paymentAmount(15)) shouldBe "£1,500"
      }

      "render the correct amount for the 15th payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(15)) shouldBe "£1,500"
      }

      "render the correct due period for the 15th payment" in {
        elementText(Selectors.paymentDue(15)) shouldBe "due by 5 April 2015"
      }

      "render the correct due period for the 15th payment period data attribute" in {
        elementText(Selectors.paymentDueData(15)) shouldBe "due by 5 April 2015"
      }

      "not display a view return link text for the 15th payment" in {
        document.select(Selectors.paymentViewReturnText(15)).size shouldBe 0
      }
    }

    "for the 16th payment" should {

      "render the correct amount for the 16th payment" in {
        elementText(Selectors.paymentAmount(16)) shouldBe "£1,600"
      }

      "render the correct amount for the 16th payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(16)) shouldBe "£1,600"
      }

      "render the correct due period for the 16th payment" in {
        elementText(Selectors.paymentDue(16)) shouldBe "due by 5 April 2016"
      }

      "render the correct due period for the 16th payment period data attribute" in {
        elementText(Selectors.paymentDueData(16)) shouldBe "due by 5 April 2016"
      }

      "not display a view return link text for the 16th payment" in {
        document.select(Selectors.paymentViewReturnText(16)).size shouldBe 0
      }
    }

    "for the 17th payment" should {

      "render the correct amount for the 17th payment" in {
        elementText(Selectors.paymentAmount(17)) shouldBe "£1,700"
      }

      "render the correct amount for the 17th payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(17)) shouldBe "£1,700"
      }

      "render the correct due period for the 17th payment" in {
        elementText(Selectors.paymentDue(17)) shouldBe "due by 5 April 2017"
      }

      "render the correct due period for the 17th payment period data attribute" in {
        elementText(Selectors.paymentDueData(17)) shouldBe "due by 5 April 2017"
      }

      "not display a view return link text for the 16th payment" in {
        document.select(Selectors.paymentViewReturnText(17)).size shouldBe 0
      }
    }

    "for the 18th payment" should {

    "render the correct amount for the 18th payment amount data attribute" in {
      elementText(Selectors.paymentAmountData(18)) shouldBe "£1,800"
    }

    "render the correct due period for the 17th payment" in {
      elementText(Selectors.paymentDue(18)) shouldBe "due by 5 April 2017"
    }

    "render the correct due period for the 17th payment period data attribute" in {
      elementText(Selectors.paymentDueData(18)) shouldBe "due by 5 April 2017"
    }

    "not display a view return link text for the 16th payment" in {
      document.select(Selectors.paymentViewReturnText(18)).size shouldBe 0
    }
  }

    "for the 19th payment" should {

      "render the correct amount for the 19th payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(19)) shouldBe "£1,900"
      }

      "render the correct due period for the 19th payment" in {
        elementText(Selectors.paymentDue(19)) shouldBe "due by 5 April 2017"
      }

      "render the correct due period for the 19th payment period data attribute" in {
        elementText(Selectors.paymentDueData(19)) shouldBe "due by 5 April 2017"
      }

      "not display a view return link text for the 19th payment" in {
        document.select(Selectors.paymentViewReturnText(19)).size shouldBe 0
      }
    }

    "for the 20th payment" should {

      "render the correct amount for the 20th payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(20)) shouldBe "£2,000"
      }

      "render the correct due period for the 20th payment" in {
        elementText(Selectors.paymentDue(20)) shouldBe "due by 5 April 2018"
      }

      "render the correct due period for the 20th payment period data attribute" in {
        elementText(Selectors.paymentDueData(20)) shouldBe "due by 5 April 2018"
      }

      "not display a view return link text for the 20th payment" in {
        document.select(Selectors.paymentViewReturnText(20)).size shouldBe 0
      }
    }

    "for the 21st payment" should {

      "render the correct amount for the 21st payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(21)) shouldBe "£2,100"
      }

      "render the correct due period for the 21st payment" in {
        elementText(Selectors.paymentDue(21)) shouldBe "due by 5 April 2018"
      }

      "render the correct due period for the 21st payment period data attribute" in {
        elementText(Selectors.paymentDueData(21)) shouldBe "due by 5 April 2018"
      }

      "not display a view return link text for the 21st payment" in {
        document.select(Selectors.paymentViewReturnText(21)).size shouldBe 0
      }
    }


    "for the 22nd payment" should {

      "render the correct amount for the 22nd payment amount data attribute" in {
        elementText(Selectors.paymentAmountData(22)) shouldBe "£2,200"
      }

      "render the correct due period for the 22nd payment" in {
        elementText(Selectors.paymentDue(22)) shouldBe "due by 5 April 2018"
      }

      "render the correct due period for the 22nd payment period data attribute" in {
        elementText(Selectors.paymentDueData(22)) shouldBe "due by 5 April 2018"
      }

      "not display a view return link text for the 22nd payment" in {
        document.select(Selectors.paymentViewReturnText(22)).size shouldBe 0
      }
    }

    "render the correct heading for the direct debits" in {
      elementText(Selectors.directDebit) shouldBe "Direct debits"
    }

    "render the correct text for the direct debits" in {
      elementText(Selectors.directDebitText) shouldBe "You can view your direct debit details."
    }

    "render the correct link text for the direct debits" in {
      elementText(Selectors.directDebitLink) shouldBe "view your direct debit details"
    }

    "have the correct link destination to the direct debits service" in {
      element(Selectors.directDebitLink).attr("href") shouldBe "/vat-through-software/direct-debit?status=true"
    }

    "render the correct help revealing link text" in {
      elementText(Selectors.helpSummaryRevealLink) shouldBe "What I owe is incorrect or missing"
    }

    "render the correct help text" in {
      elementText(Selectors.helpText) shouldBe
        "If what you owe is incorrect, check if you can correct errors on your VAT Return (opens in a new tab)."
    }

    "render the correct make payment help text" in {
      elementText(Selectors.helpMakePayment) shouldBe
        "After you have submitted a return, it can take 24 hours for what you owe to show here. " +
          "You can still make a payment (opens in a new tab) even if a payment is not shown."
    }

    "render the overdue label" in {
      elementText(Selectors.overdueLabel) shouldBe "overdue"
    }

    "have the correct destination for the make a payment link" in {
      element(Selectors.makePayment).attr("href") shouldBe "unauthenticated-payments-url"
    }
  }

  "Rendering the open payments page when a user does not have a direct debit" should {

    val hasDirectDebit = Some(false)
    val viewModel = OpenPaymentsViewModel(payments, hasDirectDebit)
    lazy val view = views.html.payments.openPayments(user, viewModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the correct link text for the first payment" in {
      elementText(Selectors.paymentPayNowLinkText(1)) shouldBe "Pay now"
    }

    "render the correct pay now href for the first payment" in {
      element(Selectors.paymentPayLink(1)).attr("href") should endWith(
        "200000000001/3/2001/VAT%20Return%20Debit%20Charge/2001-04-08"
      )
    }

    "render a hidden label for the button for the first payment" in {
      elementText(Selectors.paymentPayNowContext(1)) shouldBe
        "£2,000,000,000.01 overdue for the period 1 January to 31 March 2001"
    }

    "render the correct text for the processing time" in {
      elementText(Selectors.processingTime) shouldBe "Payments can take up to 5 days to process."
    }

    "render the correct text for the direct debit paragraph" in {
      elementText(Selectors.directDebitText) shouldBe
        "You can set up a direct debit to pay your VAT Returns."
    }

    "render the correct check direct debit link text" in {
      elementText(Selectors.directDebitLink) shouldBe "set up a direct debit"
    }

    "have the correct link destination to the direct debits service" in {
      element(Selectors.directDebitLink).attr("href") shouldBe "/vat-through-software/direct-debit?status=false"
    }
  }

  "Rendering the open payments page when the direct debit service can not be reached" should {

    val hasDirectDebit = None
    val viewModel = OpenPaymentsViewModel(payments, hasDirectDebit)
    lazy val view = views.html.payments.openPayments(user, viewModel)
    lazy implicit val document: Document = Jsoup.parse(view.body)

    "render the correct link text for the first payment" in {
      elementText(Selectors.paymentPayNowLinkText(1)) shouldBe "Pay now"
    }

    "render the correct pay now href for the first payment" in {
      element(Selectors.paymentPayLink(1)).attr("href") should endWith(
        "200000000001/3/2001/VAT%20Return%20Debit%20Charge/2001-04-08"
      )
    }

    "render a hidden label for the button for the first payment" in {
      elementText(Selectors.paymentPayNowContext(1)) shouldBe
        "£2,000,000,000.01 overdue for the period 1 January to 31 March 2001"
    }

    "render the correct text for the processing time" in {
      elementText(Selectors.processingTimeOld) shouldBe
        "Your payment could take up to 5 days to process. You may be fined if it is late."
    }

    "render the correct text for the direct debit paragraph" in {
      elementText(Selectors.directDebitText) shouldBe
        "If you have already set up a direct debit, you do not need to pay now. You can view your direct debits if you are not sure."
    }

    "render the correct check direct debit link text" in {
      elementText(Selectors.directDebitLink) shouldBe "view your direct debits"
    }

    "have the correct link destination to the direct debits service" in {
      element(Selectors.directDebitLink).attr("href") shouldBe "/vat-through-software/direct-debit"
    }
  }
}
