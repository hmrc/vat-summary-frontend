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

import common.{SpecBase, TestModels}

import java.time.LocalDate

class PaymentsOnScheduleViewModelSpec extends SpecBase {

  // val vatPeriods: List[VatPeriod] = List(
  //   VatPeriod(
  //     title = "Feb 2024 - Apr 2024",
  //     startDate = LocalDate.parse("2024-02-01"),
  //     endDate = LocalDate.parse("2024-04-30"),
  //     payments = List(
  //       PaymentDetail(PaymentType.FirstPayment, LocalDate.parse("2024-03-31"), "£22,945.23"),
  //       PaymentDetail(PaymentType.SecondPayment, LocalDate.parse("2024-04-30"), "£22,945.23")
  //     )
  //   ),
  //   VatPeriod(
  //     title = "May 2024 - Jul 2024",
  //     startDate = LocalDate.parse("2024-05-01"),
  //     endDate = LocalDate.parse("2024-07-31"),
  //     payments = List(
  //       PaymentDetail(PaymentType.FirstPayment, LocalDate.parse("2024-06-30"), "£22,945.23"),
  //       PaymentDetail(PaymentType.SecondPayment, LocalDate.parse("2024-07-31"), "£22,945.23")
  //     )
  //   ),
  //   VatPeriod(
  //     title = "Aug 2024 - Oct 2024",
  //     startDate = LocalDate.parse("2024-08-01"),
  //     endDate = LocalDate.parse("2024-10-31"),
  //     payments = List(
  //       PaymentDetail(PaymentType.FirstPayment, LocalDate.parse("2024-09-30"), "£22,945.23"),
  //       PaymentDetail(PaymentType.SecondPayment, LocalDate.parse("2024-10-31"), "£22,945.23")
  //     )
  //   ),
  //   VatPeriod(
  //     title = "Feb 2025 - Apr 2025",
  //     startDate = LocalDate.parse("2025-02-01"),
  //     endDate = LocalDate.parse("2025-04-30"),
  //     payments = List(
  //       PaymentDetail(PaymentType.FirstPayment, LocalDate.parse("2025-03-31"), "£122,945.23"),
  //       PaymentDetail(PaymentType.SecondPayment, LocalDate.parse("2025-04-30"), "£122,945.23")
  //     )
  //   )
  // )

  // val viewModel = PaymentsOnScheduleViewModel(
  //   breathingSpace = false,
  //   periods = vatPeriods
  // )

  // "PaymentsOnScheduleViewModel" should {

  //   "return the correct current or upcoming periods" in {
  //     val expectedPeriods = vatPeriods.filter(_.isCurrentOrUpcoming)
  //     viewModel.currentPeriods mustBe expectedPeriods
  //   }

  //   "return the correct past periods" in {
  //     val expectedPeriods = vatPeriods.filter(_.isPast)
  //     viewModel.pastPeriods mustBe expectedPeriods
  //   }

  //   "return the next upcoming payment when available" in {
  //     val expectedPayment = Some(PaymentDetail(PaymentType.FirstPayment, LocalDate.parse("2024-03-31"), "£22,945.23"))
  //     viewModel.nextPayment mustBe expectedPayment
  //   }

  //   "return None when no upcoming payments are available" in {
  //     val emptyViewModel = PaymentsOnScheduleViewModel(
  //       breathingSpace = false,
  //       periods = List.empty
  //     )
  //     emptyViewModel.nextPayment mustBe None
  //   }

  //   "correctly identify a balancing payment when the 3rd payment is due and 2nd payment is overdue" in {
  //     val balancingVatPeriod = VatPeriod(
  //       title = "Nov 2024 - Jan 2025",
  //       startDate = LocalDate.parse("2024-11-01"),
  //       endDate = LocalDate.parse("2025-01-31"),
  //       payments = List(
  //         PaymentDetail(PaymentType.SecondPayment, LocalDate.parse("2024-12-31"), "£22,945.23"),
  //         PaymentDetail("3rd payment due", LocalDate.parse("2025-01-31"), "Balance")
  //       )
  //     )

  //     val balancingViewModel = PaymentsOnScheduleViewModel(
  //       breathingSpace = false,
  //       periods = List(balancingVatPeriod)
  //     )

  //     balancingViewModel.isBalancingPayment mustBe true
  //   }

  //   "return false for isBalancingPayment when conditions are not met" in {
  //     val nonBalancingViewModel = PaymentsOnScheduleViewModel(
  //       breathingSpace = false,
  //       periods = List(vatPeriods.head)
  //     )
  //     nonBalancingViewModel.isBalancingPayment mustBe false
  //   }
  // }
}