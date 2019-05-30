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

package controllers

import config.AppConfig
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

class VatCertificateController @Inject()(val messagesApi: MessagesApi,
                                         authorisedController: AuthorisedController)
                                        (implicit val appConfig: AppConfig)
  extends FrontendController with I18nSupport {

  def show(): Action[AnyContent] = authorisedController.authorisedAction { implicit request => _ =>
    if(appConfig.features.vatCertificateEnabled()) {
      Future.successful(Ok(views.html.certificate.vatCertificate()))
    } else {
      Future.successful(NotFound(views.html.errors.notFound()))
    }
  }
}
