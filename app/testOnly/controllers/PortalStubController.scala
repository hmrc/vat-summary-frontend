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

import config.AppConfig
import controllers.AuthorisedController
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.EnrolmentsAuthService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

class PortalStubController @Inject()(val messagesApi: MessagesApi,
                                     val enrolmentsAuthService: EnrolmentsAuthService,
                                     val authorisedController: AuthorisedController,
                                     implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  def show(vrn: String): Action[AnyContent] = authorisedController.authorisedAction { implicit request =>
    _ =>
      Future.successful(Ok(testOnly.views.html.portalStub()))
  }

}
