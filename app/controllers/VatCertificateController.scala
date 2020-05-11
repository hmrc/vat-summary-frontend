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

package controllers

import common.SessionKeys
import config.AppConfig
import javax.inject.{Inject, Singleton}
import models.viewModels.VatCertificateViewModel
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{AccountDetailsService, ServiceInfoService}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.certificate.VatCertificate
import views.html.errors.NotFound

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VatCertificateController @Inject()(serviceInfoService: ServiceInfoService,
                                         authorisedController: AuthorisedController,
                                         accountDetailsService: AccountDetailsService,
                                         val mcc: MessagesControllerComponents,
                                         vatCertificate: VatCertificate,
                                         notFound: NotFound
                                        )(implicit val appConfig: AppConfig,
                                          implicit val ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  def show(): Action[AnyContent] = authorisedController.authorisedActionAllowAgents { implicit request =>
    implicit user =>
      val vrn = user.vrn
      serviceInfoService.getPartial.flatMap { serviceInfoContent =>
        accountDetailsService.getAccountDetails(vrn).map {
          case Right(customerInformation) =>
            Ok(vatCertificate(serviceInfoContent, VatCertificateViewModel.fromCustomerInformation(vrn, customerInformation)))
          case Left(_) =>
            InternalServerError
        }
      }
  }

  def changeClient: Action[AnyContent] = authorisedController.authorisedActionAllowAgents { implicit request =>
    user =>
      if (user.isAgent) {
        Future.successful(Redirect(appConfig.agentClientLookupStartUrl(routes.VatCertificateController.show().url))
          .removingFromSession(SessionKeys.agentSessionVrn))
      } else {
        Future.successful(NotFound(notFound()))
      }
  }

  def changeClientAction: Action[AnyContent] = authorisedController.authorisedActionAllowAgents { implicit request =>
    user =>
      if (user.isAgent) {
        Future.successful(Redirect(appConfig.agentClientLookupActionUrl)
          .removingFromSession(SessionKeys.agentSessionVrn))
      } else {
        Future.successful(NotFound(notFound()))
      }
  }
}
