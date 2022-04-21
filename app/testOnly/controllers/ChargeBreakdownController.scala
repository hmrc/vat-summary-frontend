/*
 * Copyright 2022 HM Revenue & Customs
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

import config.{AppConfig, ServiceErrorHandler}
import controllers.AuthorisedController
import controllers.predicates.DDInterruptPredicate
import models.viewModels.{InterestChargeViewModel, WhatYouOweChargeModel}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.ServiceInfoService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.errors.PaymentsError
import views.html.payments.{ChargeTypeDetailsView, InterestChargeDetailsView}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ChargeBreakdownController @Inject()(authorisedController: AuthorisedController,
                                          DDInterrupt: DDInterruptPredicate,
                                          mcc: MessagesControllerComponents,
                                          serviceInfoService: ServiceInfoService,
                                          chargeBreakdownView: ChargeTypeDetailsView,
                                          interestBreakdownView: InterestChargeDetailsView,
                                          errorView: PaymentsError,
                                          serviceErrorHandler: ServiceErrorHandler)
                                         (implicit ec: ExecutionContext,
                                          appConfig: AppConfig) extends
  FrontendController(mcc) with I18nSupport with LoggerUtil {

  def chargeBreakdown: Action[AnyContent] = authorisedController.financialAction { implicit request =>
    implicit user => DDInterrupt.interruptCheck { _ =>
      serviceInfoService.getPartial.map { navLinks =>
        WhatYouOweChargeModel.form.bindFromRequest.fold(
          errorForm => {
            logger.warn(s"[ChargeBreakdownController][chargeBreakdown] - Unexpected error when binding form: $errorForm")
            InternalServerError(errorView())
          },
          model => Ok(chargeBreakdownView(model, navLinks))
        )
      }
    }
  }

  def interestBreakdown: Action[AnyContent] = authorisedController.financialAction { implicit request =>
    implicit user => DDInterrupt.interruptCheck { _ =>
      if(appConfig.features.interestBreakdownEnabled()) {
        serviceInfoService.getPartial.map { navLinks =>
          InterestChargeViewModel.form.bindFromRequest.fold(
            errorForm => {
              logger.warn(s"[ChargeBreakdownController][interestBreakdown] - Unexpected error when binding form: $errorForm")
              InternalServerError(errorView())
            },
            model => Ok(interestBreakdownView(model, navLinks))
          )
        }
      } else {
        Future.successful(NotFound(serviceErrorHandler.notFoundTemplate))
      }
    }
  }
}
