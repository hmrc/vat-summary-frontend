/*
 * Copyright 2018 HM Revenue & Customs
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
import config.AppConfig
import javax.inject.{Inject, Singleton}
import models.DirectDebitDetailsModel
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.{EnrolmentsAuthService, PaymentsService}

@Singleton
class DirectDebitController @Inject()(val messagesApi: MessagesApi,
                                     val enrolmentsAuthService: EnrolmentsAuthService,
                                     implicit val appConfig: AppConfig,
                                     paymentsService: PaymentsService,
                                     auditingService: AuditingService)
  extends AuthorisedController with I18nSupport {

  def directDebits(hasActiveDirectDebit: Option[Boolean] = None): Action[AnyContent] = authorisedAction {
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

          case Left(_) => InternalServerError(views.html.errors.standardError(appConfig,
            messagesApi.apply("standardError.title"),
            messagesApi.apply("standardError.heading"),
            messagesApi.apply("standardError.message"))
          )
       }
  }
}
