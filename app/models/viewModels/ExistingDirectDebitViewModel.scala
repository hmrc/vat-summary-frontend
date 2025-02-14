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

package models.viewModels

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl


case class ExistingDirectDebitViewModel(dueDateOrUrl: Option[String],
                                        linkId: String,
                                        directDebitMandateFound: Boolean,
                                        redirectUrl: Option[RedirectUrl]) {

}


object ExistingDirectDebitViewModel {

  def formApply(dueDateOrUrl: Option[String],
                linkId: String,
                directDebitMandateFound: Boolean,
                redirectUrl: Option[RedirectUrl]) = ExistingDirectDebitViewModel(dueDateOrUrl, linkId, directDebitMandateFound, redirectUrl)

  def formUnapply(arg: ExistingDirectDebitViewModel): Option[(Option[String], String, Boolean, Option[RedirectUrl])] = Some((
    arg.dueDateOrUrl,
    arg.linkId,
    arg.directDebitMandateFound,
    arg.redirectUrl
  ))
}
