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
import audit.models.WhatYouOweAuditModel
import com.google.inject.Inject
import common.SessionKeys
import config.AppConfig
import models.payments.{ChargeType, Payment, PaymentWithPeriod, VatLateSubmissionPen, VatOverpaymentForRPI}
import models.penalties.{LPPDetails, PenaltyDetails}
import models.viewModels.StandardChargeViewModel.{periodFrom, periodTo}
import models.viewModels._
import models._
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import services._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.errors.PaymentsError
import views.html.payments.{PaymentsOnScheduleView}
import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class PaymentsOnScheduleController @Inject()(authorisedController: AuthorisedController,
                                     dateService: DateService,
                                     paymentsService: PaymentsService,
                                     serviceInfoService: ServiceInfoService,
                                     mcc: MessagesControllerComponents,
                                     paymentsError: PaymentsError,
                                     view: PaymentsOnScheduleView,
                                     accountDetailsService: AccountDetailsService,
                                     penaltyDetailsService: PenaltyDetailsService,
                                     WYOSessionService: WYOSessionService,
                                     auditService: AuditingService)
                                    (implicit ec: ExecutionContext,
                                     appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  def show: Action[AnyContent] = authorisedController.financialAction { implicit request =>

    implicit user =>
      serviceInfoService.getPartial.flatMap { serviceInfoContent =>
        sampleEtmpCall.flatMap { model => 
          val viewModel = PaymentsOnScheduleViewModel.fromViewModelFromVATStandingRequest(model)
          Future.successful(InternalServerError(view(viewModel,serviceInfoContent)))
        }
      }
    }

  // TODO: Delete
  val sampleEtmpCall: Future[VATStandingRequest] = VATStandingRequestParser.parseJson(VATStandingRequestParser.jsonString) match {
    case Right(parsedModel) => Future.successful(parsedModel) 
    case Left(errorMessage)  => Future.failed(new RuntimeException(errorMessage))
  }
}