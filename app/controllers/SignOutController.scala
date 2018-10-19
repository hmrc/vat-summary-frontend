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

import com.google.inject.{Inject, Singleton}
import config.AppConfig
import controllers.predicates.HybridUserPredicate
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.Future

@Singleton
class SignOutController @Inject()(val messagesApi: MessagesApi,
                                  implicit val appConfig: AppConfig,
                                  val hybridUserPredicate: HybridUserPredicate) extends BaseController with I18nSupport {

  def signOut(authorised: Boolean): Action[AnyContent] = Action.async { implicit request =>
    val redirectUrl: String = if (authorised) appConfig.signOutUrl else appConfig.unauthorisedSignOutUrl
    Future.successful(Redirect(redirectUrl))
  }

  val timeout: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Redirect(appConfig.unauthorisedSignOutUrl))
  }
}
