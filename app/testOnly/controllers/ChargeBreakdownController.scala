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
import models.viewModels.{CrystallisedInterestViewModel, CrystallisedLPP1ViewModel, EstimatedInterestViewModel, StandardChargeViewModel}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.ServiceInfoService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.errors.PaymentsError
import views.html.payments.{ChargeTypeDetailsView, CrystallisedInterestView, EstimatedInterestView, CrystallisedLPP1View}
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ChargeBreakdownController @Inject()(authorisedController: AuthorisedController,
                                          DDInterrupt: DDInterruptPredicate,
                                          mcc: MessagesControllerComponents,
                                          serviceInfoService: ServiceInfoService,
                                          chargeBreakdownView: ChargeTypeDetailsView,
                                          estimatedInterestView: EstimatedInterestView,
                                          errorView: PaymentsError,
                                          crystallisedInterestView: CrystallisedInterestView,
                                          crystallisedLPP1View: CrystallisedLPP1View,
                                          serviceErrorHandler: ServiceErrorHandler)
                                         (implicit ec: ExecutionContext,
                                          appConfig: AppConfig) extends
  FrontendController(mcc) with I18nSupport with LoggerUtil {

  def chargeBreakdown: Action[AnyContent] = authorisedController.financialAction { implicit request =>
    implicit user => DDInterrupt.interruptCheck { _ =>
      serviceInfoService.getPartial.map { navLinks =>
        StandardChargeViewModel.form.bindFromRequest.fold(
          errorForm => {
            logger.warn(s"[ChargeBreakdownController][chargeBreakdown] - Unexpected error when binding form: $errorForm")
            InternalServerError(errorView())
          },
          model => Ok(chargeBreakdownView(model, navLinks))
        )
      }
    }
  }

  def estimatedInterestBreakdown: Action[AnyContent] = authorisedController.financialAction { implicit request =>
    implicit user => DDInterrupt.interruptCheck { _ =>
      if(appConfig.features.interestBreakdownEnabled()) {
        serviceInfoService.getPartial.map { navLinks =>
          EstimatedInterestViewModel.form.bindFromRequest.fold(
            errorForm => {
              logger.warn(s"[ChargeBreakdownController][estimatedInterestBreakdown] - Unexpected error when binding form: $errorForm")
              InternalServerError(errorView())
            },
            model => Ok(estimatedInterestView(model, navLinks))
          )
        }
      } else {
        Future.successful(NotFound(serviceErrorHandler.notFoundTemplate))
      }
    }
  }

  def crystallisedInterestBreakdown: Action[AnyContent] = authorisedController.financialAction { implicit request =>
    implicit user => DDInterrupt.interruptCheck { _ =>
      if(appConfig.features.interestBreakdownEnabled()) {
        serviceInfoService.getPartial.map { navLinks =>
          CrystallisedInterestViewModel.form.bindFromRequest.fold(
            errorForm => {
              logger.warn("[ChargeBreakdownController][crystallisedInterestBreakdown] - " +
                s"Unexpected error when binding form: $errorForm")
              InternalServerError(errorView())
            },
            model => Ok(crystallisedInterestView(model, navLinks))
          )
        }
      } else {
        Future.successful(NotFound(serviceErrorHandler.notFoundTemplate))
      }
    }
  }

  def crystallisedLPP1Breakdown: Action[AnyContent] = authorisedController.financialAction { implicit request =>
    implicit user => DDInterrupt.interruptCheck { _ =>
      if(appConfig.features.interestBreakdownEnabled()) {
        serviceInfoService.getPartial.map { navLinks =>
          CrystallisedLPP1ViewModel.form.bindFromRequest.fold(
            errorForm => {
              logger.warn("[ChargeBreakdownController][crystallisedLPP1Breakdown] - " +
                s"Unexpected error when binding form: $errorForm")
              InternalServerError(errorView())
            },
            model => Ok(crystallisedLPP1View(model, navLinks))
          )
        }
      } else {
        Future.successful(NotFound(serviceErrorHandler.notFoundTemplate))
      }
    }
  }
}
