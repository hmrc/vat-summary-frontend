/*
 * Copyright 2025 HM Revenue & Customs
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

import models.viewModels.ExistingDirectDebitViewModel
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.ServiceInfoService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.payments.ExistingDirectDebit
import config.AppConfig

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ExistingDirectDebitController @Inject()(authorisedController: AuthorisedController,
                                              mcc: MessagesControllerComponents,
                                              serviceInfoService: ServiceInfoService,
                                              view: ExistingDirectDebit
                                             )(implicit ec: ExecutionContext, appConfig: AppConfig)
                                              extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  def show(dueDateOrUrl: String, linkId: String, ddStatus: Boolean): Action[AnyContent] = authorisedController.financialAction {
    implicit request =>
      implicit user =>
        serviceInfoService.getPartial.flatMap {
          serviceInfoContent =>
            val model : ExistingDirectDebitViewModel = new ExistingDirectDebitViewModel(Some(dueDateOrUrl), linkId, ddStatus)
              Future.successful(Ok(view(model, serviceInfoContent)))
          }
      }
}
