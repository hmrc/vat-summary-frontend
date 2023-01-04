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

package controllers

import config.{AppConfig, ServiceErrorHandler}

import javax.inject.{Inject, Singleton}
import models.viewModels.VatCertificateViewModel
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{AccountDetailsService, ServiceInfoService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.certificate.VatCertificate

import scala.concurrent.ExecutionContext

@Singleton
class VatCertificateController @Inject()(serviceInfoService: ServiceInfoService,
                                         authorisedController: AuthorisedController,
                                         accountDetailsService: AccountDetailsService,
                                         mcc: MessagesControllerComponents,
                                         vatCertificate: VatCertificate,
                                         serviceErrorHandler: ServiceErrorHandler)
                                        (implicit appConfig: AppConfig,
                                         ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  def show: Action[AnyContent] = authorisedController.authorisedActionAllowAgents { implicit request =>
    implicit user =>
      val vrn = user.vrn
      serviceInfoService.getPartial.flatMap { serviceInfoContent =>
        accountDetailsService.getAccountDetails(vrn).map {
          case Right(customerInformation) =>
            Ok(vatCertificate(serviceInfoContent, VatCertificateViewModel.fromCustomerInformation(vrn, customerInformation)))
          case Left(_) =>
            serviceErrorHandler.showInternalServerError
        }
      }
  }
}
