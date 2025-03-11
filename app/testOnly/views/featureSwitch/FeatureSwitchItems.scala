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

package testOnly.views.featureSwitch

import com.google.inject.Inject
import config.ConfigKeys
import models.FeatureSwitchModel
import play.api.data.Form
import uk.gov.hmrc.govukfrontend.views.Aliases.{CheckboxItem, Text}

class FeatureSwitchItems @Inject() () {

  private def formCheckBoxItem(form: Form[FeatureSwitchModel], configKey: String, description: String): CheckboxItem = {
    CheckboxItem(
      id = Some(form(configKey).name),
      name = Some(form(configKey).name),
      content = Text(description),
      value = "true",
      checked = form(configKey).value.contains("true")
    )
  }

  def items(form: Form[FeatureSwitchModel]): Seq[CheckboxItem] = {
    Seq(
      formCheckBoxItem(form, ConfigKeys.staticDateEnabledFeature, "Use static date (2018-05-01)"),
      formCheckBoxItem(form, ConfigKeys.webchatEnabled, "Enable the web chat link"),
      formCheckBoxItem(form, ConfigKeys.showUserResearchBannerEnabled, "Enable the user research banner"),
      formCheckBoxItem(form, ConfigKeys.futureDateOffsetEnabled, "Enable the user future date to 35 days"),
      formCheckBoxItem(form, ConfigKeys.poaActiveFeatureEnabled, "Enable Payment On Account Schedule link")
    )
  }
}
