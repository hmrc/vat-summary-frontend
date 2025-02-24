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

import audit.AuditingService
import com.google.inject.Inject
import config.AppConfig
import models.viewModels._
import play.api.i18n.I18nSupport
import services._
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.payments.{PaymentsOnAccountView}
import scala.concurrent.{ExecutionContext, Future}
import config.ServiceErrorHandler

class PaymentsOnAccountController @Inject()(authorisedController: AuthorisedController,
                                     dateService: DateService,
                                     paymentsOnAccountService: PaymentsOnAccountService,
                                     serviceInfoService: ServiceInfoService,
                                     serviceErrorHandler: ServiceErrorHandler,
                                     mcc: MessagesControllerComponents,
                                     view: PaymentsOnAccountView,
                                     auditService: AuditingService)
                                    (implicit ec: ExecutionContext,
                                     appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  def show: Action[AnyContent] = authorisedController.authorisedAction { implicit request =>

    implicit user =>
      serviceInfoService.getPartial.flatMap { serviceInfoContent => {
        val regime: String = ""
        val taxpayerIdType: String = ""
        val taxpayerIdValue: String = ""

        paymentsOnAccountService.getPaymentsOnAccounts(regime, taxpayerIdType, taxpayerIdValue).flatMap {
          case Some(model) => 
            val viewModel = PaymentsOnAccountViewModel.fromViewModelFromVATStandingRequest(model)
            println(viewModel)
            Future.successful(Ok(view(viewModel,serviceInfoContent)))
          case None => Future.successful(serviceErrorHandler.showInternalServerError)
        }
      }
    }
  }
}