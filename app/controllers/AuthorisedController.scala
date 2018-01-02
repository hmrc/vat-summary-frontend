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

import common.EnrolmentKeys
import config.AppConfig
import models.User
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Request, Result}
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core.authorise.EmptyPredicate
import uk.gov.hmrc.auth.core.{AuthorisationException, Enrolment, NoActiveSession}
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

abstract class AuthorisedController extends FrontendController with I18nSupport {

  val messagesApi: MessagesApi
  val enrolmentsAuthService: EnrolmentsAuthService
  implicit val appConfig: AppConfig

  def authorisedAction(block: Request[AnyContent] => User => Future[Result]): Action[AnyContent] = Action.async {
    implicit request =>

      val predicate = if (appConfig.features.simpleAuth()) EmptyPredicate else Enrolment(EnrolmentKeys.vatEnrolmentId)

      enrolmentsAuthService.authorised(predicate).retrieve(Retrievals.authorisedEnrolments) {
        enrolments => {
          val user = if (appConfig.features.simpleAuth()) User("123456789") else User(enrolments)
          block(request)(user)
        }
      } recoverWith {
        case _: NoActiveSession => Future.successful(Unauthorized(views.html.errors.sessionTimeout()))
        case _: AuthorisationException => Future.successful(Forbidden(views.html.errors.unauthorised()))
      }
  }
}
