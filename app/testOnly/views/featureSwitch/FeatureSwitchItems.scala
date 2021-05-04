/*
 * Copyright 2021 HM Revenue & Customs
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
      checked = form(configKey).value match {
        case Some("true") => true
        case _ => false
      }
    )
  }

  def items(form: Form[FeatureSwitchModel]): Seq[CheckboxItem] = {
    Seq(
      formCheckBoxItem(form, ConfigKeys.userResearchBannerFeature, "User research banner"),
      formCheckBoxItem(form, ConfigKeys.vatCertNSTPsFeature, "Show NSTPs on VAT certificate"),
      formCheckBoxItem(form, ConfigKeys.staticDateEnabledFeature, "Use static date (2018-05-01)"),
      formCheckBoxItem(form, ConfigKeys.paymentsAndRepaymentsFeature, "Show payments and repayments tile"),
      formCheckBoxItem(form, ConfigKeys.vatOptOutServiceFeature, "Show vat opt-out section"),
      formCheckBoxItem(form, ConfigKeys.useVatObligationsService, "Use vat-obligations microservice"),
      formCheckBoxItem(form, ConfigKeys.useDirectDebitDummyPageFeature, "Use direct debit hand-off dummy page"),
      formCheckBoxItem(form, ConfigKeys.useLanguageSelectorFeature, "Show welsh content"),
      formCheckBoxItem(form, ConfigKeys.submitReturnFeatures, "User research banner"),
      formCheckBoxItem(form, ConfigKeys.agentAccessFeature, "Agent access enabled"),
      formCheckBoxItem(form, ConfigKeys.mtdSignUpFeature, "MTD sign up enabled"),
      formCheckBoxItem(form, ConfigKeys.ddCollectionInProgressFeature, "Direct debit collection in progress enabled"),
      formCheckBoxItem(form, ConfigKeys.displayCovidMessageFeature, "Display Covid-19 message"),
      formCheckBoxItem(form, ConfigKeys.missingTraderAddressIntercept, "Intercept missing traders on entry to confirm address"),
      formCheckBoxItem(form, ConfigKeys.r17Content, "Display email unverified nudge")
    )
  }
}
