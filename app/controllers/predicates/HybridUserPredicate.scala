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

package controllers.predicates

import config.ServiceErrorHandler
import javax.inject.{Inject, Singleton}
import models.User
import play.api.Logger
import play.api.mvc.{AnyContent, MessagesControllerComponents, Request, Result}
import services.AccountDetailsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import common.SessionKeys

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HybridUserPredicate @Inject()(val accountDetailsService: AccountDetailsService,
                                    errorHandler: ServiceErrorHandler,
                                    val mcc: MessagesControllerComponents,
                                    implicit val ec: ExecutionContext) extends FrontendController(mcc) {

  def authoriseMigratedUserAction(f: Request[AnyContent] => User => Future[Result])
                                 (implicit request: Request[AnyContent], user: User): Future[Result] = {
    accountDetailsService.getAccountDetails(user.vrn) flatMap {
      case Right(userDetails) if userDetails.isHybridUser =>
        Logger.debug("[HybridCheckPredicate][bounceHybridToHome] User has a partial migration. Redirecting to Overview page")
        Future.successful(Redirect(controllers.routes.VatDetailsController.details()))
      case Right(_) => f(request)(user).map(result => result.addingToSession(SessionKeys.insolventWithoutAccessKey -> "false"))
      case Left(error) =>
        Logger.warn(s"[HybridCheckPredicate][bounceHybridToHome] Error returned from accountDetailsService: $error")
        Future.successful(errorHandler.showInternalServerError)
    }
  }
}
