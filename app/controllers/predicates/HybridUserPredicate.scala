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

package controllers.predicates

import javax.inject.{Inject, Singleton}
import models.User
import play.api.Logger
import play.api.mvc.{AnyContent, Request, Result}
import services.AccountDetailsService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class HybridUserPredicate @Inject()(val accountDetailsService: AccountDetailsService) extends FrontendController {

  def authoriseMigratedUserAction(f: Request[AnyContent] => User => Future[Result])
                                 (implicit request: Request[AnyContent], user: User): Future[Result] = {
    accountDetailsService.getAccountDetails(user.vrn) flatMap {
      case Right(userDetails) if userDetails.isHybridUser =>
        Logger.debug("[HybridCheckPredicate][bounceHybridToHome] User has a partial migration. Redirecting to Overview page")
        Future.successful(Redirect(controllers.routes.VatDetailsController.details()))
      case Right(_) => f(request)(user)
      case Left(error) =>
        Logger.debug(s"[HybridCheckPredicate][bounceHybridToHome] Error returned from accountDetailsService: $error")
        Future.successful(InternalServerError)
    }
  }
}
