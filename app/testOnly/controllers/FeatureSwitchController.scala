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

package testOnly.controllers

import config.AppConfig
import forms.FeatureSwitchForm
import javax.inject.Inject
import models.FeatureSwitchModel
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import testOnly.views.html.featureSwitch.FeatureSwitch
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

class FeatureSwitchController @Inject()(implicit val appConfig: AppConfig,
                                        mcc: MessagesControllerComponents,
                                        featureSwitch: FeatureSwitch)
  extends FrontendController(mcc) with I18nSupport {

  def featureSwitch: Action[AnyContent] = Action { implicit request =>
    Ok(featureSwitch(FeatureSwitchForm.form.fill(
      FeatureSwitchModel(
        userResearchBannerEnabled = appConfig.features.userResearchBanner(),
        vatCertNSTPsEnabled = appConfig.features.vatCertNSTPs(),
        staticDateEnabled = appConfig.features.staticDateEnabled(),
        paymentsAndRepaymentsEnabled = appConfig.features.paymentsAndRepaymentsEnabled(),
        vatOptOutEnabled = appConfig.features.vatOptOutEnabled(),
        useDirectDebitDummyPage = appConfig.features.useDirectDebitDummyPage(),
        useLanguageSelector = appConfig.features.useLanguageSelector(),
        submitReturnFeatures = appConfig.features.submitReturnFeatures(),
        agentAccess = appConfig.features.agentAccess(),
        mtdSignUp = appConfig.features.mtdSignUp(),
        ddCollectionInProgress = appConfig.features.ddCollectionInProgressEnabled(),
        displayCovid = appConfig.features.displayCovidMessage(),
        missingTraderAddressIntercept = appConfig.features.missingTraderAddressIntercept(),
        r17Content = appConfig.features.r17Content(),
        directDebitInterrupt = appConfig.features.directDebitInterrupt()
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
    appConfig.features.vatCertNSTPs(model.vatCertNSTPsEnabled)
    appConfig.features.staticDateEnabled(model.staticDateEnabled)
    appConfig.features.paymentsAndRepaymentsEnabled(model.paymentsAndRepaymentsEnabled)
    appConfig.features.vatOptOutEnabled(model.vatOptOutEnabled)
    appConfig.features.useDirectDebitDummyPage(model.useDirectDebitDummyPage)
    appConfig.features.useLanguageSelector(model.useLanguageSelector)
    appConfig.features.submitReturnFeatures(model.submitReturnFeatures)
    appConfig.features.agentAccess(model.agentAccess)
    appConfig.features.mtdSignUp(model.mtdSignUp)
    appConfig.features.ddCollectionInProgressEnabled(model.ddCollectionInProgress)
    appConfig.features.displayCovidMessage(model.displayCovid)
    appConfig.features.missingTraderAddressIntercept(model.missingTraderAddressIntercept)
    appConfig.features.r17Content(model.r17Content)
    appConfig.features.directDebitInterrupt(model.directDebitInterrupt)
    Redirect(routes.FeatureSwitchController.featureSwitch())
  }
}
