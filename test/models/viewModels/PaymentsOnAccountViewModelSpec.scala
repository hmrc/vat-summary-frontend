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

package models.viewModels

import common.{SpecBase}
import java.time.{LocalDate, Clock, ZoneId}

class PaymentsOnAccountViewModelSpec extends SpecBase {

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
    changedOn = Some(today)
  )

  "PaymentsOnAccountViewModel" should {

    "return the correct current or upcoming periods" in {
      viewModel.currentPeriods mustBe vatPeriods.filter(_.isCurrentOrUpcoming)
    }

    "return the correct past periods" in {
      viewModel.pastPeriods mustBe vatPeriods.filter(_.isPast)
    }

    "return the next upcoming payment when available" in {
      val expectedPayment = Some(PaymentDetail(PaymentType.FirstPayment, Some(LocalDate.parse("2025-03-31")), "£122,945.23"))
      viewModel.nextPayment mustBe expectedPayment
    }

    "return None when no periods are available" in {
      val emptyViewModel = PaymentsOnAccountViewModel(
        breathingSpace = false,
        periods = List.empty,
        changedOn = Some(today)
      )
      emptyViewModel.nextPayment mustBe None
    }

    "return None when no upcoming payments are available" in {
      val emptyViewModel = PaymentsOnAccountViewModel(
        breathingSpace = false,
        periods = List.empty,
        changedOn = Some(today)
      )
      emptyViewModel.nextPayment mustBe None
    }

    "identify a balancing payment correctly" in {
      val balancingViewModel = PaymentsOnAccountViewModel(
        breathingSpace = false,
        periods = List(
          VatPeriod("1 Nov 2024 to January 2025", LocalDate.parse("2024-11-01"), LocalDate.parse("2025-01-31"),
            List(
              PaymentDetail(PaymentType.SecondPayment, Some(LocalDate.parse("2025-01-31")), "£22,945.23"),
              PaymentDetail(PaymentType.ThirdPayment, Some(LocalDate.parse("2025-02-28")), "Balance")
            )
          )
        ),
        changedOn = Some(today)
      )

      balancingViewModel.isBalancingPayment mustBe true
    }

    "return false for isBalancingPayment when conditions are not met" in {
      val nonBalancingViewModel = PaymentsOnAccountViewModel(
        breathingSpace = false,
        periods = List(vatPeriods.head),
        changedOn = Some(today)
      )
      nonBalancingViewModel.isBalancingPayment mustBe false
    }
  }
}