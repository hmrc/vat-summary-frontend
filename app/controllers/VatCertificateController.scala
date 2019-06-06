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

  def redirect(): Action[AnyContent] = authorisedController.authorisedVatCertificateAction { implicit request =>
    implicit user =>
      Future.successful(
        if(appConfig.features.vatCertificateEnabled()) {
          val userType = if(user.isAgent) "agent" else "non-agent"
          Redirect(controllers.routes.VatCertificateController.show(userType))
        } else {
          NotFound(views.html.errors.notFound())
        }
    )
  }

  def show(userType: String): Action[AnyContent] = authorisedController.authorisedVatCertificateAction { implicit request => _ =>
    Future.successful(
      if(appConfig.features.vatCertificateEnabled()) {
        Ok(views.html.certificate.vatCertificate())
      } else {
        NotFound(views.html.errors.notFound())
      }
    )
  }
}
