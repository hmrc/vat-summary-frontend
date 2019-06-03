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

package controllers

import common.EnrolmentKeys._
import config.AppConfig
import controllers.predicates.{AgentPredicate, HybridUserPredicate}
import javax.inject.Inject
import models.User
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Request, Result}
import services._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.{Retrievals, ~}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import scala.concurrent.Future

class AuthorisedController @Inject()(val messagesApi: MessagesApi,
                                     val enrolmentsAuthService: EnrolmentsAuthService,
                                     val hybridUserPredicate: HybridUserPredicate,
                                     val agentPredicate: AgentPredicate,
                                     implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  def authorisedAction(block: Request[AnyContent] => User => Future[Result],
                       checkMigrationStatus: Boolean = false,
                       allowAgentAccess: Boolean = false): Action[AnyContent] = Action.async {
    implicit request =>

      val predicate = ((Enrolment(vatDecEnrolmentKey) or Enrolment(vatVarEnrolmentKey)) and Enrolment(mtdVatEnrolmentKey))
        .or(Enrolment(mtdVatEnrolmentKey))

      enrolmentsAuthService.authorised(predicate).retrieve(Retrievals.authorisedEnrolments and Retrievals.affinityGroup) {

        case _ ~ Some(AffinityGroup.Agent) if allowAgentAccess => agentPredicate.authoriseAsAgent(block)
        case enrolments ~ Some(_) =>
          val user = User(enrolments, None)

          if(checkMigrationStatus) {
            hybridUserPredicate.authoriseMigratedUserAction(block)(request, user)
          } else {
            block(request)(user)
          }
        case _ =>
          Logger.warn("[AuthorisedController][authorisedAction] - Missing affinity group")
          Future.successful(InternalServerError)
      } recoverWith {
        case _: NoActiveSession => Future.successful(Unauthorized(views.html.errors.sessionTimeout()))
        case _: InsufficientEnrolments => {
          Logger.warn(s"[AuthorisedController][authorisedAction] insufficient enrolment exception encountered")
          Future.successful(Forbidden(views.html.errors.unauthorised()))
        }
        case _: AuthorisationException => {
          Logger.warn(s"[AuthorisedController][authorisedAction] encountered unauthorisation exception")
          Future.successful(Forbidden(views.html.errors.unauthorised()))
        }
      }
  }

  def authorisedMigratedUserAction(block: Request[AnyContent] => User => Future[Result]): Action[AnyContent] = authorisedAction(
    block,
    checkMigrationStatus = true
  )

  def authorisedVatCertificateAction(block: Request[AnyContent] => User => Future[Result]): Action[AnyContent] = authorisedAction(
    block,
    allowAgentAccess = appConfig.features.agentAccess()
  )
}
