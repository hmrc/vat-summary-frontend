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

package audit.models

import config.AppConfig
import models.User
import play.api.libs.json.{JsValue, Json, Writes}

case class HybridWYOModel(user: User)(implicit appConfig: AppConfig) extends ExtendedAuditModel {

  override val auditType: String = "HandoffToPortalCheckWhatYouOwe"

  override val transactionName: String = "handoff-to-portal-check-what-you-owe"

  override val detail: JsValue = Json.toJson(this)

  def portalUrl(): String = appConfig.portalMakePaymentUrl(user.vrn)

}

object HybridWYOModel {
  implicit val writes: Writes[HybridWYOModel] = Writes { model =>
    Json.obj(
      "vrn" -> model.user.vrn,
      "portalUrl" -> model.portalUrl
    )
  }
}
