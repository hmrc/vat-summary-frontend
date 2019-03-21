/*
 * Copyright 2019 HM Revenue & Customs
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

package testOnly.controllers

import config.AppConfig
import forms.FeatureSwitchForm
import javax.inject.Inject
import models.FeatureSwitchModel
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Result}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

class FeatureSwitchController @Inject()(val messagesApi: MessagesApi, implicit val appConfig: AppConfig)
  extends FrontendController with I18nSupport {

  def featureSwitch: Action[AnyContent] = Action { implicit request =>
    Ok(testOnly.views.html.featureSwitch(FeatureSwitchForm.form.fill(
      FeatureSwitchModel(
        userResearchBannerEnabled = appConfig.features.userResearchBanner(),
        allowDirectDebitsEnabled = appConfig.features.allowDirectDebits(),
        staticDateEnabled = appConfig.features.staticDateEnabled(),
        vatCertificateEnabled = appConfig.features.vatCertificateEnabled(),
        enableVatObligationsService = appConfig.features.enableVatObligationsService(),
        useDirectDebitDummyPage = appConfig.features.useDirectDebitDummyPage(),
        useLanguageSelector = appConfig.features.useLanguageSelector(),
        submitReturnFeatures = appConfig.features.submitReturnFeatures(),
        checkMandationStatus = appConfig.features.checkMandationStatus()
      )
    )))
  }

  def submitFeatureSwitch: Action[AnyContent] = Action { implicit request =>
    FeatureSwitchForm.form.bindFromRequest().fold(
      _ => Redirect(routes.FeatureSwitchController.featureSwitch()),
      success = handleSuccess
    )
  }

  def handleSuccess(model: FeatureSwitchModel): Result = {
    appConfig.features.userResearchBanner(model.userResearchBannerEnabled)
    appConfig.features.allowDirectDebits(model.allowDirectDebitsEnabled)
    appConfig.features.staticDateEnabled(model.staticDateEnabled)
    appConfig.features.vatCertificateEnabled(model.vatCertificateEnabled)
    appConfig.features.enableVatObligationsService(model.enableVatObligationsService)
    appConfig.features.useDirectDebitDummyPage(model.useDirectDebitDummyPage)
    appConfig.features.useLanguageSelector(model.useLanguageSelector)
    appConfig.features.submitReturnFeatures(model.submitReturnFeatures)
    appConfig.features.checkMandationStatus(model.checkMandationStatus)
    Redirect(routes.FeatureSwitchController.featureSwitch())
  }
}
