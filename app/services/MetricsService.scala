/*
 * Copyright 2022 HM Revenue & Customs
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

package services

import com.codahale.metrics.{Counter, Timer}
import com.kenshoo.play.metrics.Metrics
import javax.inject.{Inject, Singleton}

trait MetricsService {

  val getObligationsTimer: Timer
  val getObligationsCallFailureCounter: Counter

  val getOpenPaymentsTimer: Timer
  val getOpenPaymentsCallFailureCounter: Counter

  val getPaymentHistoryTimer: Timer
  val getPaymentHistoryFailureCounter: Counter

  val getCustomerInfoTimer: Timer
  val getCustomerInfoCallFailureCounter: Counter

  val getVatReturnTimer: Timer
  val getVatReturnCallFailureCounter: Counter

  val postSetupPaymentsJourneyTimer: Timer
  val postSetupPaymentsJourneyCounter: Counter

  val getDirectDebitStatusTimer: Timer
  val getDirectDebitStatusFailureCounter: Counter

}

@Singleton
class MetricsServiceImpl @Inject()(metrics: Metrics) extends MetricsService {

  val getObligationsTimer: Timer = metrics.defaultRegistry.timer("get-obligations-from-vat-api-timer")
  val getObligationsCallFailureCounter: Counter = metrics.defaultRegistry.counter("get-obligations-from-vat-api-failure-counter")

  val getOpenPaymentsTimer: Timer = metrics.defaultRegistry.timer("get-open-payments-from-financial-transactions-timer")
  val getOpenPaymentsCallFailureCounter: Counter = metrics.defaultRegistry.counter("get-open-payments-from-financial-transactions-failure-counter")

  val getPaymentHistoryTimer: Timer = metrics.defaultRegistry.timer("get-payment-history-from-financial-transactions-timer")
  val getPaymentHistoryFailureCounter: Counter = metrics.defaultRegistry.counter("get-payments-history-from-financial-transactions-failure-counter")

  val getCustomerInfoTimer: Timer = metrics.defaultRegistry.timer("get-customer-info-from-vat-subscription-timer")
  val getCustomerInfoCallFailureCounter: Counter = metrics.defaultRegistry.counter("get-customer-info-from-vat-subscription-failure-counter")

  val getVatReturnTimer: Timer = metrics.defaultRegistry.timer("get-vat-return-from-vat-api-timer")
  val getVatReturnCallFailureCounter: Counter = metrics.defaultRegistry.counter("get-vat-return-from-vat-api-failure-counter")

  val postSetupPaymentsJourneyTimer: Timer = metrics.defaultRegistry.timer("post-setup-payments-journey-payment-api-timer")
  val postSetupPaymentsJourneyCounter: Counter = metrics.defaultRegistry.counter("post-setup-payments-journey-payment-api-failure-counter")

  val getDirectDebitStatusTimer: Timer = metrics.defaultRegistry.timer("get-direct-debit-status-from-financial-transactions-timer")
  val getDirectDebitStatusFailureCounter: Counter = metrics.defaultRegistry.counter("get-open-payments-from-financial-transactions-failure-counter")

}
