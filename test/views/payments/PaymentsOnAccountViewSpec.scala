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

class PaymentsOnAccountViewSpec extends ViewBaseSpec {

  val paymentsOnAccountView: PaymentsOnAccountView = injector.instanceOf[PaymentsOnAccountView]

  val fixedClock: Clock = Clock.fixed(LocalDate.parse("2025-02-24").atStartOfDay(ZoneId.systemDefault()).toInstant, ZoneId.systemDefault())
  val today: LocalDate = LocalDate.now(fixedClock)

  val vatPeriods: List[VatPeriod] = List(
    VatPeriod("1 Feb 2024 to April 2024", LocalDate.parse("2024-02-01"), LocalDate.parse("2024-04-30"),
      List(
        PaymentDetail(PaymentType.FirstPayment, Some(LocalDate.parse("2024-03-31")), "£22,945.23"),
        PaymentDetail(PaymentType.SecondPayment, Some(LocalDate.parse("2024-04-30")), "£22,945.23"),
        PaymentDetail(PaymentType.ThirdPayment, None, "Balance")
      )
    ),
    VatPeriod("1 Nov 2024 to January 2025", LocalDate.parse("2024-11-01"), LocalDate.parse("2025-01-31"),
      List(
        PaymentDetail(PaymentType.FirstPayment, Some(LocalDate.parse("2024-12-31")), "£22,945.23"),
        PaymentDetail(PaymentType.SecondPayment, Some(LocalDate.parse("2025-01-31")), "£22,945.23"),
        PaymentDetail(PaymentType.ThirdPayment, None, "Balance")
      )
    ),
    VatPeriod("1 Feb 2025 to April 2025", LocalDate.parse("2025-02-01"), LocalDate.parse("2025-04-30"),
      List(
        PaymentDetail(PaymentType.FirstPayment, Some(LocalDate.parse("2025-03-31")), "£122,945.23"),
        PaymentDetail(PaymentType.SecondPayment, Some(LocalDate.parse("2025-04-30")), "£122,945.23"),
        PaymentDetail(PaymentType.ThirdPayment, None, "Balance")
      )
    )
  )
  val viewModel = PaymentsOnAccountViewModel(
    breathingSpace = false,
    periods = vatPeriods,
    changedOn = Some(LocalDate.parse("2025-02-24"))
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

    "display the correct next payment message when balancing payment is due" in {
      val balancingViewModel = viewModel.copy(
        periods = List(
          VatPeriod("1 Nov 2024 to January 2025", LocalDate.parse("2024-11-01"), LocalDate.parse("2025-01-31"),
            List(
              PaymentDetail(PaymentType.SecondPayment, Some(LocalDate.parse("2025-01-31")), "£22,945.23"),
              PaymentDetail(PaymentType.ThirdPayment, Some(LocalDate.parse("2025-02-28")), "Balance")
            )
          )
        )
      )
      lazy val view = paymentsOnAccountView(balancingViewModel, Html(""))
      lazy implicit val document: Document = Jsoup.parse(view.body)
      document.select("p.govuk-body strong").text() shouldBe "Your next payment due is your balancing payment. VAT return due date"
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
      document.select(".govuk-summary-card__title").text() should include("1 Feb 2024 to April 2024")
    }
  }
}
