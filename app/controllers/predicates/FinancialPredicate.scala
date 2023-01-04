/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.mvc.{AnyContent, MessagesControllerComponents, Request, Result}
import services.{AccountDetailsService, DateService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import common.SessionKeys
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FinancialPredicate @Inject()(val accountDetailsService: AccountDetailsService,
                                   errorHandler: ServiceErrorHandler,
                                   val mcc: MessagesControllerComponents,
                                   dateService: DateService)
                                  (implicit val ec: ExecutionContext) extends FrontendController(mcc) with LoggerUtil {

  def authoriseFinancialAction(block: Request[AnyContent] => User => Future[Result])
                              (implicit request: Request[AnyContent], user: User): Future[Result] = {
    request.session.get(SessionKeys.financialAccess) match {
      case Some("true") => block(request)(user)
      case _ => accountDetailsService.getAccountDetails(user.vrn) flatMap {
        case Right(userDetails) if userDetails.isHybridUser =>
          logger.debug("[FinancialPredicate][authoriseFinancialAction] " +
            "User has a partial migration. Redirecting to Overview page")
          Future.successful(Redirect(controllers.routes.VatDetailsController.details))
        case Right(userDetails) if userDetails.details.insolvencyDateFutureUserBlocked(dateService.now()) =>
          logger.warn("[FinancialPredicate][authoriseFinancialAction] " +
            "User has a future insolvency date. Rendering technical difficulties.")
          Future.successful(errorHandler.showInternalServerError)
        case Right(_) =>
          block(request)(user).map(result => result.addingToSession(
            SessionKeys.insolventWithoutAccessKey -> "false", SessionKeys.financialAccess -> "true")
          )
        case Left(error) =>
          logger.warn(s"[FinancialPredicate][authoriseFinancialAction] Error returned from accountDetailsService: $error")
          Future.successful(errorHandler.showInternalServerError)
      }
    }
  }
}
