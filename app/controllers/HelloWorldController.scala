/*
 * Copyright 2017 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}

import config.AppConfig
import play.api.mvc._
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future
import play.api.i18n.{I18nSupport, MessagesApi}
import services.{ErrorModel, TestService}

@Singleton
class HelloWorldController @Inject()(val appConfig: AppConfig, val messagesApi: MessagesApi, service: TestService) extends FrontendController with I18nSupport {

  val helloWorld: Action[AnyContent] = Action.async { implicit request =>
    service.call("error2").map(bm => Ok(views.html.helloworld.hello_world(appConfig)))
      .recover {
        case ErrorModel(message) => Ok(views.html.error_template(appConfig, "WoopsieDoo", "Error", message))
        case _ => InternalServerError
      }

    //Future.successful(Ok(views.html.helloworld.hello_world(appConfig)))
  }
}
