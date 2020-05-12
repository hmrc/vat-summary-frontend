/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.LocalDate

case class VatDetailsViewModel(paymentsData: Option[String],
                               obligationData: Option[String],
                               entityName: Option[String],
                               hasMultipleReturnObligations: Boolean = false,
                               returnObligationOverdue: Boolean = false,
                               returnObligationError: Boolean = false,
                               hasMultiplePayments: Boolean = false,
                               paymentOverdue: Boolean = false,
                               paymentError: Boolean = false,
                               isHybridUser: Boolean = false,
                               isNonMTDfBUser: Option[Boolean] = Some(false),
                               isNonMTDfBOrNonDigitalUser: Option[Boolean] = Some(false),
                               customerInfoError: Boolean = false,
                               pendingOptOut: Boolean = false,
                               deregDate: Option[LocalDate] = None,
                               pendingDereg: Boolean = false,
                               currentDate: LocalDate,
                               partyType: Option[String],
                               isExempt: Option[Boolean] = Some(false))
