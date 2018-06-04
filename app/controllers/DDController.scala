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
import config.AppConfig
import connectors.DDConnector
import javax.inject.{Inject, Singleton}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.EnrolmentsAuthService
import views.html.errors.paymentsError

@Singleton
class DDController @Inject()(val messagesApi: MessagesApi,
                             val enrolmentsAuthService: EnrolmentsAuthService,
                             ddConnector: DDConnector,
                             implicit val appConfig: AppConfig,
                             auditingService: AuditingService)
  extends AuthorisedController with I18nSupport {

  def directDebits(): Action[AnyContent] = authorisedAction { implicit request =>

    user => ddConnector.startJourney(user.vrn).map {
        case Right(url) => Redirect(url)
        case Left(_) => InternalServerError(paymentsError())

      }
  }
}
