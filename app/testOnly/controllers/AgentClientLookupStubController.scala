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

package testOnly.controllers

import common.SessionKeys
import config.AppConfig
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import testOnly.forms.StubAgentClientLookupForm
import testOnly.views.html.agentClientLookup.{AgentAction, EnterVrn, Unauthorised}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

class AgentClientLookupStubController @Inject()(implicit val appConfig: AppConfig,
                                                mcc: MessagesControllerComponents,
                                                agentActionView: AgentAction,
                                                enterVrnView: EnterVrn,
                                                unauthorisedView: Unauthorised
                                               )
  extends FrontendController(mcc) with I18nSupport {

  def show(redirectUrl: String): Action[AnyContent] = Action { implicit request =>
    Ok(enterVrnView(StubAgentClientLookupForm.form, redirectUrl))
  }

  def unauthorised: Action[AnyContent] = Action { implicit request =>
    Ok(unauthorisedView()).removingFromSession(SessionKeys.agentSessionVrn)
  }

  def agentAction: Action[AnyContent] = Action { implicit request =>
    Ok(agentActionView())
  }


  def post: Action[AnyContent] = Action { implicit request =>
    StubAgentClientLookupForm.form.bindFromRequest().fold(
      error => InternalServerError(s"Failed to bind model. Error: $error"),
      success => Redirect(success.redirectUrl)
        .addingToSession(SessionKeys.agentSessionVrn -> success.vrn)
    )
  }
}
