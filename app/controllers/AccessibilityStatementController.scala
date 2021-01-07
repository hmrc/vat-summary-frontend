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

package controllers

import java.net.URI

import config.AppConfig
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.AccessibilityStatement

import scala.concurrent.Future

class AccessibilityStatementController @Inject()(authorisedController: AuthorisedController,
                                                 val mcc: MessagesControllerComponents,
                                                 accessibilityStatement: AccessibilityStatement)
                                                (implicit appConfig: AppConfig) extends FrontendController(mcc) with I18nSupport {

  def show: Action[AnyContent] = authorisedController.authorisedActionAllowAgents { implicit request =>
    implicit user =>
      val refererUri = new URI(request.headers.get(REFERER).getOrElse("")).getPath
      Future.successful(Ok(accessibilityStatement(refererUri, fullyCompliant = false)))
  }
}
