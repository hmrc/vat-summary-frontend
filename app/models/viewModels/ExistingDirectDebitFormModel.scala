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

import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl

case class ExistingDirectDebitFormModel (dueDateOrUrl: Option[String],
                                         linkId: String,
                                         directDebitMandateFound: Boolean,
                                         value: ExistingDDContinuePayment,
                                         redirectUrl: Option[RedirectUrl])

object ExistingDirectDebitFormModel {

  def formApply(dueDateOrUrl: Option[String],
                linkId: String,
                directDebitMandateFound: Boolean,
                value: ExistingDDContinuePayment,
                redirectUrl: Option[RedirectUrl]) = ExistingDirectDebitFormModel(dueDateOrUrl, linkId, directDebitMandateFound, value, redirectUrl)

  def formUnapply(arg: ExistingDirectDebitFormModel): Option[(Option[String], String, Boolean, ExistingDDContinuePayment, Option[RedirectUrl])] = Some((
    arg.dueDateOrUrl,
    arg.linkId,
    arg.directDebitMandateFound,
    arg.value,
    arg.redirectUrl
  ))

}