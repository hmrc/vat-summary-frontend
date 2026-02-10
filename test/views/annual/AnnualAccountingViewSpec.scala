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

package views.annual

import models.viewModels._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import views.ViewBaseSpec
import views.html.annual.AnnualAccountingView
import java.time.LocalDate

class AnnualAccountingViewSpec extends ViewBaseSpec {

  val view: AnnualAccountingView = injector.instanceOf[AnnualAccountingView]

  val start = LocalDate.parse("2025-02-01")
  val end = LocalDate.parse("2026-01-31")
  val nextDue = LocalDate.parse("2025-11-30")

  private val currentPeriod = AASchedulePeriod(
    startDate = start,
    endDate = end,
    payments = List(
      AAPayment(isBalancing = false, dueDate = LocalDate.parse("2025-05-31"), amount = Some(BigDecimal(1345.00)), status = AAPaymentStatus.Paid),
      AAPayment(isBalancing = false, dueDate = LocalDate.parse("2025-08-31"), amount = Some(BigDecimal(1345.00)), status = AAPaymentStatus.Paid),
      AAPayment(isBalancing = false, dueDate = nextDue, amount = Some(BigDecimal(1345.00)), status = AAPaymentStatus.Upcoming),
      AAPayment(isBalancing = true, dueDate = LocalDate.parse("2026-03-31"), amount = None, status = AAPaymentStatus.Upcoming)
    ),
    isCurrent = true,
    isPast = false
  )

  private val pastPeriod = currentPeriod.copy(
    startDate = start.minusYears(1),
    endDate = end.minusYears(1),
    payments = List(
      AAPayment(isBalancing = false, dueDate = LocalDate.parse("2024-05-31"), amount = Some(BigDecimal(1345.00)), status = AAPaymentStatus.Paid),
      AAPayment(isBalancing = false, dueDate = LocalDate.parse("2024-08-31"), amount = Some(BigDecimal(1345.00)), status = AAPaymentStatus.Paid),
      AAPayment(isBalancing = false, dueDate = LocalDate.parse("2024-11-30"), amount = Some(BigDecimal(1345.00)), status = AAPaymentStatus.Paid),
      AAPayment(isBalancing = true, dueDate = LocalDate.parse("2025-03-31"), amount = None, status = AAPaymentStatus.Paid)
    ),
    isCurrent = false,
    isPast = true
  )

  val model = AnnualAccountingViewModel(
    changedOn = Some(LocalDate.parse("2025-06-02")),
    currentPeriods = List(currentPeriod),
    pastPeriods = List(pastPeriod),
    nextPayment = Some(AAPayment(isBalancing = false, dueDate = nextDue, amount = Some(BigDecimal(1345.00)), status = AAPaymentStatus.Upcoming)),
    isAgent = false,
    hasDirectDebit = Some(false),
    displayName = None
  )

  "AnnualAccountingView" should {
    "have the correct title" in {
      val html = view(model, Html(""))
      implicit val doc: Document = Jsoup.parse(html.body)
      doc.title shouldBe "Interim payments - Manage your VAT account - GOV.UK"
    }

    "have the correct heading" in {
      val html = view(model, Html(""))
      implicit val doc: Document = Jsoup.parse(html.body)
      doc.select("h1").text() shouldBe "Interim payments"
    }

    "render the next payment line with amount and date" in {
      val html = view(model, Html(""))
      implicit val doc: Document = Jsoup.parse(html.body)
      val text = doc.select("#next-payment-text").text()
      text should include ("Your next upcoming payment")
      text should include ("£1,345.00")
    }

    "render the changed on hint" in {
      val html = view(model, Html(""))
      implicit val doc: Document = Jsoup.parse(html.body)
      doc.select("#changed-on-date").text() should include ("were last updated on")
    }

    "show both tabs when past periods exist" in {
      val html = view(model, Html(""))
      implicit val doc: Document = Jsoup.parse(html.body)
      doc.select(".govuk-tabs__tab").size() shouldBe 2
    }

    "show only the current tab when there are no past periods" in {
      val html = view(model.copy(pastPeriods = Nil), Html(""))
      implicit val doc: Document = Jsoup.parse(html.body)
      doc.select(".govuk-tabs__tab").size() shouldBe 1
      doc.select("#past-schedule").size() shouldBe 0
    }

    "render frequency sentence for monthly" in {
      val html = view(model.copy(frequency = Some(PaymentFrequency.Monthly)), Html(""))
      implicit val doc: Document = Jsoup.parse(html.body)
      doc.text() should include("9 monthly interim payments")
    }

    "render frequency sentence for quarterly" in {
      val html = view(model.copy(frequency = Some(PaymentFrequency.Quarterly)), Html(""))
      implicit val doc: Document = Jsoup.parse(html.body)
      doc.text() should include("3 quarterly interim payments")
    }
  }
}


