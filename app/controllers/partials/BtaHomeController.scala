/*
 * Copyright 2019 HM Revenue & Customs
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

package controllers.partials

import config.AppConfig
import javax.inject.{Inject, Singleton}
import models.User
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Request, Result}
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.auth.core.{AuthorisationException, Enrolment, NoActiveSession}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class BtaHomeController @Inject()(val messagesApi: MessagesApi,
                                  enrolmentsAuthService: EnrolmentsAuthService,
                                  implicit val appConfig: AppConfig)
  extends FrontendController with I18nSupport {

  def vatSection(): Action[AnyContent] = enrolledAction { implicit request => _ =>
    Future.successful(Ok(views.html.partials.btaHome.vatSection()))
  }

  def claimEnrolment(): Action[AnyContent] = enrolledActionNonMtd { implicit request =>
    user =>
      Future.successful(Ok(views.html.partials.btaHome.claimEnrolment(user.vrn)))
  }

  def partialMigration(): Action[AnyContent] = enrolledActionNonMtd { implicit request => _ =>
    Future.successful(Ok(views.html.partials.btaHome.partialMigration()))
  }

  private def enrolledAction(block: Request[AnyContent] => User => Future[Result]): Action[AnyContent] = Action.async { implicit request =>
    enrolmentsAuthService.authorised(Enrolment("HMRC-MTD-VAT")).retrieve(Retrievals.authorisedEnrolments) {
      enrolments => {
        val user = User(enrolments)
        block(request)(user)
      }
    }.recover {
      case _: NoActiveSession => Unauthorized
      case _: AuthorisationException => Forbidden
    }
  }

  private def enrolledActionNonMtd(block: Request[AnyContent] => User => Future[Result]): Action[AnyContent] = Action.async { implicit request =>
    enrolmentsAuthService.authorised(Enrolment("HMCE-VATDEC-ORG")).retrieve(Retrievals.authorisedEnrolments) {
      enrolments => {
        val user = User(enrolments)
        block(request)(user)
      }
    }.recover {
      case _: NoActiveSession => Unauthorized
      case _: AuthorisationException => Forbidden
    }
  }
}
