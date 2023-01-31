/*
 * Copyright 2023 HM Revenue & Customs
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

package models.errors

sealed trait ServiceError

case object PaymentSetupError extends ServiceError
case object VatLiabilitiesError extends ServiceError
case object PaymentsError extends ServiceError
case object ObligationsError extends ServiceError
case object NextPaymentError extends ServiceError
case object CustomerInformationError extends ServiceError
case object DirectDebitStatusError extends ServiceError
case object TimeToPayRedirectError extends ServiceError
