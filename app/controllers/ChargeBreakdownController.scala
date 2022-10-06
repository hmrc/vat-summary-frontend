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

package controllers

import common.{ChargeViewModelTypes => types}
import config.AppConfig
import models.viewModels._
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{ServiceInfoService, WYOSessionService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.errors.{NotFound, PaymentsError}
import views.html.payments._

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ChargeBreakdownController @Inject()(authorisedController: AuthorisedController,
                                          mcc: MessagesControllerComponents,
                                          serviceInfoService: ServiceInfoService,
                                          wyoSessionService: WYOSessionService,
                                          chargeBreakdownView: ChargeTypeDetailsView,
                                          estimatedInterestView: EstimatedInterestView,
                                          estimatedLPP1View: EstimatedLPP1View,
                                          estimatedLPP2View: EstimatedLPP2View,
                                          lateSubmissionPenaltyView: LateSubmissionPenaltyView,
                                          errorView: PaymentsError,
                                          notFound: NotFound,
                                          crystallisedInterestView: CrystallisedInterestView,
                                          crystallisedLPP1View: CrystallisedLPP1View,
                                          crystallisedLPP2View: CrystallisedLPP2View)
                                         (implicit ec: ExecutionContext,
                                          appConfig: AppConfig) extends
  FrontendController(mcc) with I18nSupport with LoggerUtil {

  def showBreakdown(id: String): Action[AnyContent] = authorisedController.financialAction {
    implicit request => implicit user =>
        for {
          navLinks <- serviceInfoService.getPartial
          model <- wyoSessionService.retrieveViewModel(id)
        } yield {
          model match {
            case Some(m) => m.modelType match {
              case types.standard => Ok(chargeBreakdownView(m.data.as[StandardChargeViewModel], navLinks))
              case types.estimatedInterest => Ok(estimatedInterestView(m.data.as[EstimatedInterestViewModel], navLinks))
              case types.crystallisedInterest => Ok(crystallisedInterestView(m.data.as[CrystallisedInterestViewModel], navLinks))
              case types.crystallisedLPP1 => Ok(crystallisedLPP1View(m.data.as[CrystallisedLPP1ViewModel], navLinks))
              case types.crystallisedLPP2 => Ok(crystallisedLPP2View(m.data.as[CrystallisedLPP2ViewModel], navLinks))
              case types.estimatedLPP1 => Ok(estimatedLPP1View(m.data.as[EstimatedLPP1ViewModel], navLinks))
              case types.estimatedLPP2 => Ok(estimatedLPP2View(m.data.as[EstimatedLPP2ViewModel], navLinks))
              case types.lsp => Ok(lateSubmissionPenaltyView(m.data.as[LateSubmissionPenaltyViewModel], navLinks))
              case _ =>
                logger.warn("[ChargeBreakdownController][showBreakdown] Retrieved model type was unknown")
                InternalServerError(errorView())
            }
            case _ => NotFound(notFound())
          }
        }
  }

}
