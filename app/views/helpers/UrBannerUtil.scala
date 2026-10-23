/*
 * Copyright 2026 HM Revenue & Customs
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

package views.helpers

import config.AppConfig
import play.api.i18n.Messages
import uk.gov.hmrc.hmrcfrontend.views.Aliases.{En, UserResearchBanner}
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.language.Cy

object UrBannerUtil {

  def getUrBanner(hideCloseButton: Boolean = true)(implicit appConfig: AppConfig, messages: Messages): UserResearchBanner = {
    val isWelsh = messages.lang.code == "cy"
    val urlBase = appConfig.urBannerUrl

    val language = if (isWelsh) Cy else En
    val url = if (isWelsh) s"$urlBase&Q_Language=CY" else urlBase

    UserResearchBanner(language, url, hideCloseButton)
  }
}
