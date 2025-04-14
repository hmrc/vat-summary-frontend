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
import org.scalatest.matchers.should.Matchers._

class PaymentsOnAccountViewModelSpec extends SpecBase {

  val fixedClock: Clock = Clock.fixed(LocalDate.parse("2025-02-24").atStartOfDay(ZoneId.systemDefault()).toInstant, ZoneId.systemDefault())
  val today: LocalDate = LocalDate.now(fixedClock)

  val vatPeriods: List[VatPeriod] = List(
    VatPeriod(LocalDate.parse("2024-02-01"), LocalDate.parse("2024-04-30"),
      List(
        PaymentDetail(PaymentType.FirstPayment,  DueDate(Some(LocalDate.parse("2024-03-31"))), Some(BigDecimal(22945.23))),
        PaymentDetail(PaymentType.SecondPayment,  DueDate(Some(LocalDate.parse("2024-04-30"))), Some(BigDecimal(22945.23))),
        PaymentDetail(PaymentType.ThirdPayment,  DueDate(None), None)
      ),
      isCurrent = false,
      isPast = false
    ),
    VatPeriod(LocalDate.parse("2024-11-01"), LocalDate.parse("2025-01-31"),
      List(
        PaymentDetail(PaymentType.FirstPayment, DueDate(Some(LocalDate.parse("2024-12-31"))), Some(BigDecimal(22945.23))),
        PaymentDetail(PaymentType.SecondPayment,  DueDate(Some(LocalDate.parse("2025-01-31"))), Some(BigDecimal(22945.23))),
        PaymentDetail(PaymentType.ThirdPayment,  DueDate(None), None)
      ),
      isCurrent = false,
      isPast = false
    ),
    VatPeriod(LocalDate.parse("2025-02-01"), LocalDate.parse("2025-04-30"),
      List(
        PaymentDetail(PaymentType.FirstPayment,  DueDate(Some(LocalDate.parse("2025-03-31"))), Some(BigDecimal(122945.23))),
        PaymentDetail(PaymentType.SecondPayment,  DueDate(Some(LocalDate.parse("2025-04-30"))), Some(BigDecimal(122945.23))),
        PaymentDetail(PaymentType.ThirdPayment, DueDate(None), None)
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
    nextPayment = None
  )

  "PaymentsOnAccountViewModel" should {
    "correctly filter current and upcoming periods" in {
      viewModel.currentPeriods should contain theSameElementsAs vatPeriods.filter(_.isCurrentOrUpcoming)
    }

    "correctly filter past periods" in {
      viewModel.pastPeriods should contain theSameElementsAs vatPeriods.filter(_.isPast)
    }

    "format changedOn date correctly" in {
      viewModel.changedOnFormattedOpt shouldBe Some("24 February 2025")
    }

    "handle missing next payment correctly" in {
      viewModel.nextPayment shouldBe None
    }

  }
}