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

import models.viewModels.{PaymentDetail, PaymentType, PaymentsOnAccountViewModel, VatPeriod}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.payments.PaymentsOnAccountView
import java.time.{LocalDate, Clock, ZoneId}
import scala.jdk.CollectionConverters._

class PaymentsOnAccountViewSpec extends ViewBaseSpec {

  val paymentsOnAccountView: PaymentsOnAccountView = injector.instanceOf[PaymentsOnAccountView]

  val fixedClock: Clock = Clock.fixed(LocalDate.parse("2025-02-24").atStartOfDay(ZoneId.systemDefault()).toInstant, ZoneId.systemDefault())
  val today: LocalDate = LocalDate.now(fixedClock)

  val vatPeriods: List[VatPeriod] = List(
    VatPeriod(LocalDate.parse("2024-02-01"), LocalDate.parse("2024-04-30"),
      List(
        PaymentDetail(PaymentType.FirstPayment, Some(LocalDate.parse("2024-03-31")), Some(BigDecimal(22945.23))),
        PaymentDetail(PaymentType.SecondPayment, Some(LocalDate.parse("2024-04-30")), Some(BigDecimal(22945.23))),
        PaymentDetail(PaymentType.ThirdPayment, None, None)
      ),
      isCurrent = false,
      isPast = false
    ),
    VatPeriod(LocalDate.parse("2024-11-01"), LocalDate.parse("2025-01-31"),
      List(
        PaymentDetail(PaymentType.FirstPayment, Some(LocalDate.parse("2024-12-31")), Some(BigDecimal(22945.23))),
        PaymentDetail(PaymentType.SecondPayment, Some(LocalDate.parse("2025-01-31")), Some(BigDecimal(22945.23))),
        PaymentDetail(PaymentType.ThirdPayment, None, None)
      ),
      isCurrent = false,
      isPast = false
    ),
    VatPeriod(LocalDate.parse("2025-02-01"), LocalDate.parse("2025-04-30"),
      List(
        PaymentDetail(PaymentType.FirstPayment, Some(LocalDate.parse("2025-03-31")), Some(BigDecimal(122945.23))),
        PaymentDetail(PaymentType.SecondPayment, Some(LocalDate.parse("2025-04-30")), Some(BigDecimal(122945.23))),
        PaymentDetail(PaymentType.ThirdPayment, None, None)
      ),
      isCurrent = false,
      isPast = false
    )
  )

  def currentPeriods: List[VatPeriod] = vatPeriods.filter(_.isCurrentOrUpcoming).toList
  def pastPeriods: List[VatPeriod] = vatPeriods.filter(_.isPast).toList

  val viewModel = PaymentsOnAccountViewModel(
    breathingSpace = false,
    periods = vatPeriods,
    changedOn = Some(today),
    currentPeriods = currentPeriods,
    pastPeriods = pastPeriods,
    nextPayment = Some(PaymentDetail(PaymentType.FirstPayment, Some(LocalDate.parse("2025-03-31")), Some(BigDecimal(122945.23))))
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
      link.attr("href") shouldBe "https://www.gov.uk/guidance/vat-payments-on-account#how-to-pay"
      link.attr("target") shouldBe "_blank"
    }

    "display the correct next payment message when balancing payment is due" in {
      lazy val view = paymentsOnAccountView(viewModel.copy(nextPayment = Some(PaymentDetail(PaymentType.ThirdPayment, None, None))), Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      document.select("#next-payment-text").text() shouldBe "Your next payment due is your balancing payment. This is due on the same date as your VAT return due date."
    }


    "display the correct changedOn date message" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      document.select("#changed-on-date").text() shouldBe "The amounts due for your payments on account were changed on 24 February 2025"
    }

    "display the correct next payment message when a normal payment is due" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      document.select("p.govuk-body").text() shouldBe "Your next payment of £122,945.23 is due on 31 Mar 2025. Find out how to pay (opens in a new tab) You can find more information about payments on account here (opens in a new tab)."
    }

    "display changedOn date when present" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      document.select(".govuk-inset-text").text() shouldBe "The amounts due for your payments on account were changed on 24 February 2025"
    }

    "display tabs for current and past schedules" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      document.select(".govuk-tabs__tab").size() shouldBe 2
      document.select(".govuk-tabs__tab").get(0).text() shouldBe "Current and upcoming schedule"
      document.select(".govuk-tabs__tab").get(1).text() shouldBe "Past schedules"
    }

    "display VAT periods correctly" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      document.select(".govuk-summary-card__title").text() should include("1 November 2024 to 31 January 2025")
      document.select(".govuk-summary-card__title").text() should include("1 February 2025 to 30 April 2025")
    }

    "list VAT periods in chronological order" in {
      lazy val view = paymentsOnAccountView(viewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      val periodTitles = document.select(".govuk-summary-card__title").eachText().asScala.toSeq

      periodTitles shouldBe Seq(
        "VAT period:", "1 February 2024 to 30 April 2024",
        "VAT period:", "1 November 2024 to 31 January 2025",
        "VAT period:", "1 February 2025 to 30 April 2025"
      )
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
      val detailsSummary = document.select(".govuk-details__summary-text").text()
      val detailsContent = document.select(".govuk-details__text").text()

      detailsSummary shouldBe "Contact HMRC"
      detailsContent should include("Payments on Account Team")
      detailsContent should include("poateam@hmrc.gov.uk")
    }

    "display 'Pending' for payments without a due date" in {
      val viewModelWithNoDueDate = viewModel.copy(nextPayment = Some(PaymentDetail(PaymentType.FirstPayment, None, Some(BigDecimal(122945.23)))))
      lazy val view = paymentsOnAccountView(viewModelWithNoDueDate, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)

      document.select("#next-payment-text").text() should include("Pending")
    }
  }
}
