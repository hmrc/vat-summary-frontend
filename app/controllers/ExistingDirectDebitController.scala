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

import audit.AuditingService
import audit.models.{PayFullChargeAuditModel, PayGenericChargeAuditModel, PayVatReturnChargeAuditModel, WhatYouOweAuditModel}
import common.SessionKeys
import config.AppConfig
import controllers.AuthorisedController
import models.User
import models.payments.{ChargeType, PaymentDetailsModel, PaymentDetailsModelGeneric, PaymentDetailsModelNoPeriod, PaymentDetailsModelWithPeriod, ReturnDebitCharge}
import models.viewModels.ExistingDirectDebitViewModel
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request, Result}
import services.{DateService, PaymentsService, ServiceInfoService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.payments.ExistingDirectDebit
import services.{AccountDetailsService, ServiceInfoService}
import config.{AppConfig, ServiceErrorHandler}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ExistingDirectDebitController @Inject()(paymentsService: PaymentsService,
                                      authorisedController: AuthorisedController,
                                      auditingService: AuditingService,
                                              accountDetailsService: AccountDetailsService,
                                      mcc: MessagesControllerComponents,
                                              serviceInfoService: ServiceInfoService,
                                              view: ExistingDirectDebit,
                                              serviceErrorHandler: ServiceErrorHandler
                                             )(implicit ec: ExecutionContext,
                                       appConfig: AppConfig,
                                       dateService: DateService)
  extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  def show(earliestDueDate: Option[String], linkId: String, ddStatus: Boolean): Action[AnyContent] = authorisedController.financialAction {
    implicit request =>
      implicit user =>
        val vrn = user.vrn
        serviceInfoService.getPartial.flatMap { serviceInfoContent =>
          accountDetailsService.getAccountDetails(vrn).map {
            case Right(customerInformation) =>
              var model: ExistingDirectDebitViewModel = new ExistingDirectDebitViewModel(true);
              Ok(view(model, serviceInfoContent))
            case Left(_) =>
              serviceErrorHandler.showInternalServerError
          }
        }
      }
  }
