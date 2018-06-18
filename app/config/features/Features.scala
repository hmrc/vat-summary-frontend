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

package config.features

import config.ConfigKeys
import javax.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class Features @Inject()(config: Configuration) {

  val simpleAuth = new Feature(ConfigKeys.simpleAuthFeature, config)
  val userResearchBanner = new Feature(ConfigKeys.userResearchBannerFeature, config)
  val allowPayments = new Feature(ConfigKeys.allowPaymentsFeature, config)
  val allowDirectDebits = new Feature(ConfigKeys.allowDirectDebitsFeature, config)
  val staticDateEnabled = new Feature(ConfigKeys.staticDateEnabledFeature, config)
  val accountDetails = new Feature(ConfigKeys.accountDetailsFeature, config)
  val allowNineBox = new Feature(ConfigKeys.allowNineBoxFeature, config)
  val enabledAuditing = new Feature(ConfigKeys.enableAuditingFeature, config)
  val allowPaymentHistory = new Feature(ConfigKeys.allowPaymentHistoryFeature, config)
  val enableVatObligationsService = new Feature(ConfigKeys.useVatObligationsService, config)
  val useDirectDebitDummyPage = new Feature(ConfigKeys.useDirectDebitDummyPageFeature, config)
}
