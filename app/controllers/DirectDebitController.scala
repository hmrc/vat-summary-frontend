/*
 * Copyright 2021 HM Revenue & Customs
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
import audit.models.DirectDebitAuditModel
import config.{AppConfig, ServiceErrorHandler}
import javax.inject.{Inject, Singleton}
import models.DirectDebitDetailsModel
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{EnrolmentsAuthService, PaymentsService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext

@Singleton
class DirectDebitController @Inject()(val mcc: MessagesControllerComponents,
                                      val enrolmentsAuthService: EnrolmentsAuthService,
                                      implicit val appConfig: AppConfig,
                                      implicit val ec: ExecutionContext,
                                      paymentsService: PaymentsService,
                                      authorisedController: AuthorisedController,
                                      auditingService: AuditingService,
                                      serviceErrorHandler: ServiceErrorHandler)
  extends FrontendController(mcc) with I18nSupport {

  def directDebits(hasActiveDirectDebit: Option[Boolean] = None): Action[AnyContent] = authorisedController.authorisedAction {
    implicit request =>
      user =>

        val directDebitDetails = DirectDebitDetailsModel(
          userId = user.vrn,
          userIdType = "VRN",
          returnUrl = appConfig.directDebitReturnUrl,
          backUrl = appConfig.directDebitBackUrl
        )

        paymentsService.setupDirectDebitJourney(directDebitDetails).map {
          case Right(url) =>
            auditingService.audit(
              DirectDebitAuditModel(directDebitDetails, hasActiveDirectDebit, url),
              routes.DirectDebitController.directDebits(hasActiveDirectDebit).url
            )

            Redirect(url)

          case Left(error) =>
            Logger.warn("[DirectDebitController][directDebits] error: " + error.toString)
            serviceErrorHandler.showInternalServerError
       }
  }
}
