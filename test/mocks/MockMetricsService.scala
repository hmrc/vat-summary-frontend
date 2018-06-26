/*
 * Copyright 2018 HM Revenue & Customs
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

package mocks

import com.codahale.metrics.{Counter, Timer}
import org.scalamock.scalatest.MockFactory
import services.MetricsService

object MockMetricsService extends MetricsService with MockFactory {

  val counter: Counter = mock[Counter]
  val timer: Timer = mock[Timer]

  override val getObligationsTimer: Timer = timer
  override val getObligationsCallFailureCounter: Counter = counter
  override val getOpenPaymentsTimer: Timer = timer
  override val getOpenPaymentsCallFailureCounter: Counter = counter
  override val getCustomerInfoTimer: Timer = timer
  override val getCustomerInfoCallFailureCounter: Counter = counter
  override val getVatReturnTimer: Timer = timer
  override val getVatReturnCallFailureCounter: Counter = counter
  override val postSetupPaymentsJourneyTimer: Timer = timer
  override val postSetupPaymentsJourneyCounter: Counter = counter
  override val getDirectDebitStatusTimer: Timer = timer
  override val getDirectDebitStatusFailureCounter: Counter = counter
  override val postSetupDirectDebitJourneyTimer: Timer = timer
  override val postSetupDirectDebitJourneyCounter: Counter = counter
  override val getPaymentHistoryTimer: Timer = timer
  override val getPaymentHistoryFailureCounter: Counter = counter
}
