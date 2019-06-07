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

import java.time.LocalDate

import config.AppConfig
import javax.inject.Inject
import models.Address
import models.viewModels.VatCertificateViewModel
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

class VatCertificateController @Inject()(val messagesApi: MessagesApi,
                                         authorisedController: AuthorisedController)
                                        (implicit val appConfig: AppConfig)
  extends FrontendController with I18nSupport {

  def show(): Action[AnyContent] = authorisedController.authorisedVatCertificateAction { implicit request => _ =>
    if(appConfig.features.vatCertificateEnabled()) {
      Future.successful(Ok(views.html.certificate.vatCertificate(VatCertificateViewModel(
        "1231231231", LocalDate.now(), LocalDate.now(), Some("ABC Studios"), Some("ABC Trading studios"), "Digital",
        "Agriculture", Address("Line 1", "Line 2", Some("Line 3"), None, Some("TF4 3ER")), Some("ABCStudios@exmaple.com"), Some("******21"), Some("50****"),
        "returnPeriod.MM"
      ))))
    } else {
      Future.successful(NotFound(views.html.errors.notFound()))
    }
  }
}
