/*
 * Copyright 2020 HM Revenue & Customs
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

package config

import javax.inject.Inject
import play.api.i18n.MessagesApi
import play.api.mvc.Results.InternalServerError
import play.api.mvc.{Request, Result}
import play.twirl.api.Html
import uk.gov.hmrc.play.bootstrap.frontend.http.FrontendErrorHandler
import views.html.errors.StandardError

class ServiceErrorHandler @Inject()(val messagesApi: MessagesApi,
                                    appConfig: AppConfig,
                                    standardError: StandardError) extends FrontendErrorHandler {

  override def standardErrorTemplate(pageTitle: String, heading: String, message: String)(implicit request: Request[_]): Html = {
    standardError(appConfig, "standardError.title", "standardError.heading", "standardError.message")
  }

  def showInternalServerError(implicit request: Request[_]): Result = InternalServerError(internalServerErrorTemplate)

  override def notFoundTemplate(implicit request: Request[_]): Html =
    standardError(appConfig, "notFound.title", "notFound.heading", "notFound.message")
}