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

import models.viewModels.{
  PaymentDetail,
  PaymentType,
  PaymentsOnAccountViewModel,
  VatPeriod,
  DueDate
}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.PaymentsOnAccountView
import java.time.{LocalDate, Clock, ZoneId}
import scala.jdk.CollectionConverters._

class PaymentsOnAccountViewSpec extends ViewBaseSpec {

  val paymentsOnAccountView: PaymentsOnAccountView =
    injector.instanceOf[PaymentsOnAccountView]

  val fixedClock: Clock = Clock.fixed(
    LocalDate
      .parse("2025-02-24")
      .atStartOfDay(ZoneId.systemDefault())
      .toInstant,
    ZoneId.systemDefault()
  )
  val today: LocalDate = LocalDate.now(fixedClock)

  val vatPeriods: List[VatPeriod] = List(
    VatPeriod(
      LocalDate.parse("2024-02-01"),
      LocalDate.parse("2024-04-30"),
      List(
        PaymentDetail(
          PaymentType.FirstPayment,
          DueDate(Some(LocalDate.parse("2024-03-31"))),
          Some(BigDecimal(22945.23))
        ),
        PaymentDetail(
          PaymentType.SecondPayment,
          DueDate(Some(LocalDate.parse("2024-04-30"))),
          Some(BigDecimal(22945.23))
        ),
        PaymentDetail(PaymentType.ThirdPayment, DueDate(None), None)
      ),
      isCurrent = true,
      isPast = false
    ),
    VatPeriod(
      LocalDate.parse("2024-11-01"),
      LocalDate.parse("2025-01-31"),
      List(
        PaymentDetail(
          PaymentType.FirstPayment,
          DueDate(Some(LocalDate.parse("2024-12-31"))),
          Some(BigDecimal(22945.23))
        ),
        PaymentDetail(
          PaymentType.SecondPayment,
          DueDate(Some(LocalDate.parse("2025-01-31"))),
          Some(BigDecimal(22945.23))
        ),
        PaymentDetail(PaymentType.ThirdPayment, DueDate(None), None)
      ),
      isCurrent = false,
      isPast = false
    ),
    VatPeriod(
      LocalDate.parse("2025-02-01"),
      LocalDate.parse("2025-04-30"),
      List(
        PaymentDetail(
          PaymentType.FirstPayment,
          DueDate(Some(LocalDate.parse("2025-03-31"))),
          Some(BigDecimal(122945.23))
        ),
        PaymentDetail(
          PaymentType.SecondPayment,
          DueDate(Some(LocalDate.parse("2025-04-30"))),
          Some(BigDecimal(122945.23))
        ),
        PaymentDetail(PaymentType.ThirdPayment, DueDate(None), None)
      ),
      isCurrent = false,
      isPast = false
    )
  )

  def currentPeriods: List[VatPeriod] =
    vatPeriods.filter(_.isCurrentOrUpcoming).toList
  def pastPeriods: List[VatPeriod] = vatPeriods.filter(_.isPast).toList

  val viewModel = PaymentsOnAccountViewModel(
    breathingSpace = false,
    periods = vatPeriods,
    changedOn = Some(today),
    currentPeriods = currentPeriods,
    pastPeriods = pastPeriods,
    nextPayment = Some(
      PaymentDetail(
        PaymentType.FirstPayment,
        DueDate(Some(LocalDate.parse("2025-03-31"))),
        Some(BigDecimal(122945.23))
      )
    ),
    displayName = Some("testName")
  )

  "PaymentsOnAccountView" should {

    "have the correct title" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      document.title shouldBe "Payments on account - Manage your VAT account - GOV.UK"
    }

    "have the correct heading" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      document.select("h1").text() shouldBe "Payments on account"
    }

    "have a back link with correct attributes" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      val backLink = document.select("#back-link")

      backLink.text() shouldBe "Back"
      backLink.attr("href") shouldBe "#back-link"
    }

    "have a link to 'Find out how to pay'" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      val link = document.select("#find-out-how-to-pay-link")

      link.text() shouldBe "Find out how to pay (opens in a new tab)"
      link.attr(
        "href"
      ) shouldBe "https://www.gov.uk/guidance/vat-payments-on-account#how-to-pay"
      link.attr("target") shouldBe "_blank"
    }

    "display the correct next payment message when balancing payment is due" in {
      lazy val view = paymentsOnAccountView(
        viewModel.copy(nextPayment =
          Some(PaymentDetail(PaymentType.ThirdPayment, DueDate(None), None))
        ),
        Html("")
      )
      lazy implicit val document: Document = Jsoup.parse(view.body)
      document
        .select("#next-payment-text")
        .text() shouldBe "Your next payment due is your balancing payment. This is due on the same date as your VAT return due date."
    }

    "display the correct changedOn date message" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      document
        .select("#changed-on-date")
        .text() shouldBe "The amounts due for your payments on account were changed on 24 February 2025."
    }

    "display the correct next payment message when a normal payment is due" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      document
        .select("#next-payment-text")
        .text() shouldBe "Your next payment of £122,945.23 is due on 31 Mar 2025."
    }

    "display changedOn date when present" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      document
        .select(".govuk-inset-text")
        .text() shouldBe "The amounts due for your payments on account were changed on 24 February 2025."
    }

    "display tabs for current and past schedules" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      document.select(".govuk-tabs__tab").size() shouldBe 2
      document
        .select(".govuk-tabs__tab")
        .get(0)
        .text() shouldBe "Current and upcoming schedule"
      document
        .select(".govuk-tabs__tab")
        .get(1)
        .text() shouldBe "Past schedules"
    }

    "display VAT periods correctly" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      document.select(".govuk-summary-card__title").text() should include(
        "1 November 2024 to 31 January 2025"
      )
      document.select(".govuk-summary-card__title").text() should include(
        "1 February 2025 to 30 April 2025"
      )
    }

    "list VAT periods in chronological order" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      val periodTitles =
        document.select(".govuk-summary-card__title").eachText().asScala.toSeq

      periodTitles shouldBe Seq(
        "VAT period:",
        "1 February 2024 to 30 April 2024",
        "VAT period:",
        "1 November 2024 to 31 January 2025",
        "VAT period:",
        "1 February 2025 to 30 April 2025"
      )
    }

    "navigate to the return deadlines page from the current schedule tab" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))(
        request,
        messages,
        mockConfig,
        user
      )
      lazy implicit val document: Document = Jsoup.parse(view.body)

      val returnDeadlineLink = document.select("#vat-returns-link")

      returnDeadlineLink.attr("href") shouldBe mockConfig.vatReturnDeadlinesUrl
    }

    "navigate to the submitted return page from the past schedule tab" in {
      val updatedViewModel = viewModel.copy(
        pastPeriods = List(
          VatPeriod(
            startDate = LocalDate.parse("2024-02-01"),
            endDate = LocalDate.parse("2024-04-30"),
            payments = List(
              PaymentDetail(
                PaymentType.FirstPayment,
                DueDate(Some(LocalDate.parse("2024-03-31"))),
                Some(BigDecimal(22945.23))
              ),
              PaymentDetail(
                PaymentType.SecondPayment,
                DueDate(Some(LocalDate.parse("2024-04-30"))),
                Some(BigDecimal(22945.23))
              ),
              PaymentDetail(PaymentType.ThirdPayment, DueDate(None), None)
            ),
            isCurrent = false,
            isPast = true
          )
        )
      )

      lazy val view = paymentsOnAccountView(updatedViewModel, Html(""))(
        request,
        messages,
        mockConfig,
        user
      )
      lazy implicit val document: Document = Jsoup.parse(view.body)

      val submittedReturnLink =
        document.select("#past-schedule #vat-submitted-link")

      submittedReturnLink.attr(
        "href"
      ) shouldBe mockConfig.vatSubmittedReturnsUrl
    }

    "move the first VAT period from the current schedule to the past schedule after 35 days" in {
      val updatedClock: Clock = Clock.fixed(
        LocalDate
          .parse("2024-06-05")
          .atStartOfDay(ZoneId.systemDefault())
          .toInstant,
        ZoneId.systemDefault()
      )
      val updatedToday: LocalDate = LocalDate.now(updatedClock)

      val updatedVatPeriods = vatPeriods.map { period =>
        if (period.endDate.plusDays(35).isBefore(updatedToday))
          period.copy(isCurrent = false, isPast = true)
        else period
      }

      val updatedViewModel = viewModel.copy(
        currentPeriods = updatedVatPeriods.filter(_.isCurrentOrUpcoming).toList,
        pastPeriods = updatedVatPeriods.filter(_.isPast).toList
      )

      lazy val view = paymentsOnAccountView(updatedViewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      val currentScheduleTitles = document
        .select("#current-schedule .govuk-summary-card__title")
        .eachText()
        .asScala
      val pastScheduleTitles = document
        .select("#past-schedule .govuk-summary-card__title")
        .eachText()
        .asScala

      currentScheduleTitles should not contain "1 February 2024 to 30 April 2024"

      pastScheduleTitles should contain("1 February 2024 to 30 April 2024")
    }

    "show 'Current and Upcoming Schedule' as the default active tab" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      val activeTab = document.select(".govuk-tabs__list-item--selected").text()

      activeTab shouldBe "Current and upcoming schedule"
    }

    "have a 'Contact HMRC' details section" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      val detailsSummary =
        document.select(".govuk-details__summary-text").text()
      val detailsContent = document.select(".govuk-details__text").text()

      detailsSummary shouldBe "Contact HMRC"
      detailsContent should include("Payments on Account Team")
      detailsContent should include("poateam@hmrc.gov.uk")
    }

    "display 'Pending' for payments without a due date" in {
      val viewModelWithNoDueDate = viewModel.copy(nextPayment =
        Some(
          PaymentDetail(
            PaymentType.FirstPayment,
            DueDate(None),
            Some(BigDecimal(122945.23))
          )
        )
      )
      lazy val view = paymentsOnAccountView(viewModelWithNoDueDate, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      document.select("#next-payment-text").text() should include("Pending")
    }

    "display 'Balance' for each third cell when the payment amount is missing" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      val paymentAmounts =
        document.select("#payment-amount").eachText().asScala.toSeq

      val isEveryThirdBalance =
        paymentAmounts.sliding(3, 3).forall(_.lastOption.contains("Balance"))

      isEveryThirdBalance shouldBe true
    }

    "display a maximum of 5 VAT periods in the current schedule" in {
      val extraVatPeriods = (1 to 10).map { i =>
        val month = if (i <= 12) i else (i % 12) + 1
        val startDate = LocalDate.of(2025, month, 1)
        val endDate = startDate.plusMonths(2).minusDays(1)

        VatPeriod(
          startDate = startDate,
          endDate = endDate,
          payments = List(
            PaymentDetail(
              PaymentType.FirstPayment,
              DueDate(Some(startDate.plusDays(15))),
              Some(BigDecimal(100 * i))
            ),
            PaymentDetail(
              PaymentType.SecondPayment,
              DueDate(Some(endDate)),
              Some(BigDecimal(200 * i))
            ),
            PaymentDetail(PaymentType.ThirdPayment, DueDate(None), None)
          ),
          isCurrent = true,
          isPast = false
        )
      }.toList

      val updatedViewModel = viewModel.copy(
        currentPeriods = extraVatPeriods
      )

      lazy val view = paymentsOnAccountView(updatedViewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      document.select("#current-schedule .govuk-summary-card").size() shouldBe 5
    }

    "display VAT return link for an current schedule when it has no past periods" in {
      val upcomingVatPeriod = List(
        VatPeriod(
          startDate = LocalDate.parse("2025-04-01"),
          endDate = LocalDate.parse("2025-06-30"),
          payments = List(
            PaymentDetail(
              PaymentType.FirstPayment,
              DueDate(Some(LocalDate.parse("2025-05-01"))),
              Some(BigDecimal(50000.00))
            ),
            PaymentDetail(
              PaymentType.SecondPayment,
              DueDate(Some(LocalDate.parse("2025-06-01"))),
              Some(BigDecimal(50000.00))
            ),
            PaymentDetail(PaymentType.ThirdPayment, DueDate(None), None)
          ),
          isCurrent = true,
          isPast = false
        )
      )

      val updatedViewModel = viewModel.copy(
        currentPeriods = upcomingVatPeriod,
        pastPeriods = List.empty
      )

      lazy val view = paymentsOnAccountView(updatedViewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      val vatReturnLink = document.select("#vat-returns-link")

      vatReturnLink should not be empty
      vatReturnLink.attr("href") shouldBe mockConfig.vatReturnDeadlinesUrl
    }

    "render link to VAT return deadlines if third payment has no fallback" in {
      val testViewModel = viewModel.copy(
        currentPeriods = List(
          VatPeriod(
            startDate = LocalDate.parse("2025-04-01"),
            endDate = LocalDate.parse("2025-06-30"),
            payments = List(
              PaymentDetail(
                PaymentType.FirstPayment,
                DueDate(Some(LocalDate.parse("2025-05-01"))),
                Some(BigDecimal(123))
              ),
              PaymentDetail(
                PaymentType.SecondPayment,
                DueDate(Some(LocalDate.parse("2025-06-01"))),
                Some(BigDecimal(123))
              ),
              PaymentDetail(
                PaymentType.ThirdPayment,
                DueDate(None, None),
                None
              ) 
            ),
            isCurrent = true,
            isPast = false
          )
        )
      )

      lazy val view = paymentsOnAccountView(testViewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      val fallbackLink = document.select("#vat-returns-link")

      fallbackLink.text() shouldBe "VAT return due date"
      fallbackLink.attr("href") shouldBe mockConfig.vatReturnDeadlinesUrl
    }

  }

  "render the correct message when next payment is a balancing payment" in {
  val balancingDueDate = LocalDate.parse("2025-07-01")

  val balancingPaymentViewModel = viewModel.copy(
    nextPayment = Some(
      PaymentDetail(
        PaymentType.ThirdPayment,
        DueDate(None, Some(balancingDueDate)), 
        None
      )
    )
  )

  lazy val view = paymentsOnAccountView(balancingPaymentViewModel, Html(""))
  lazy implicit val document: Document = Jsoup.parse(view.body)

  val nextPaymentText = document.select("#next-payment-text").text()

  nextPaymentText shouldBe
    "Your next payment due is your balancing payment. This is due on 1 Jul 2025."
}
}
