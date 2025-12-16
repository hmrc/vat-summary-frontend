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

package testOnly.controllers

import config.AppConfig
import forms.FeatureSwitchForm
import models.FeatureSwitchModel
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import testOnly.views.html.featureSwitch.FeatureSwitch
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import javax.inject.Inject

class FeatureSwitchController @Inject()(implicit val appConfig: AppConfig,
                                        mcc: MessagesControllerComponents,
                                        featureSwitch: FeatureSwitch)
  extends FrontendController(mcc) with I18nSupport {

  def featureSwitch: Action[AnyContent] = Action { implicit request =>
    Ok(featureSwitch(FeatureSwitchForm.form.fill(
      FeatureSwitchModel(
        staticDateEnabled = appConfig.features.staticDateEnabled(),
        webchatEnabled = appConfig.features.webchatEnabled(),
        showUserResearchBannerEnabled = appConfig.features.showUserResearchBannerEnabled(),
        futureDateOffsetEnabled = appConfig.features.futureDateOffsetEnabled(),
        poaActiveFeatureEnabled = appConfig.features.poaActiveFeatureEnabled(),
        annualAccountingFeatureEnabled = appConfig.features.annualAccountingFeatureEnabled()
      )
    )))
  }

  def submitFeatureSwitch: Action[AnyContent] = Action { implicit request =>
    FeatureSwitchForm.form.bindFromRequest().fold(
      _ => Redirect(routes.FeatureSwitchController.featureSwitch),
      success = handleSuccess
    )
  }

  def handleSuccess(model: FeatureSwitchModel): Result = {
    appConfig.features.staticDateEnabled(model.staticDateEnabled)
    appConfig.features.webchatEnabled(model.webchatEnabled)
    appConfig.features.showUserResearchBannerEnabled(model.showUserResearchBannerEnabled)
    appConfig.features.poaActiveFeatureEnabled(model.poaActiveFeatureEnabled)
    appConfig.features.annualAccountingFeatureEnabled(model.annualAccountingFeatureEnabled)
    Redirect(routes.FeatureSwitchController.featureSwitch)
  }
}
