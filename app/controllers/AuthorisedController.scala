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

package controllers

import common.{EnrolmentKeys => Keys}
import config.AppConfig
import controllers.predicates.{AgentPredicate, HybridUserPredicate}
import javax.inject.{Inject, Singleton}
import models.User
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._
import services._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.errors.Unauthorised

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthorisedController @Inject()(val mcc: MessagesControllerComponents,
                                     val enrolmentsAuthService: EnrolmentsAuthService,
                                     val hybridUserPredicate: HybridUserPredicate,
                                     val agentPredicate: AgentPredicate,
                                     implicit val appConfig: AppConfig,
                                     implicit val ec: ExecutionContext,
                                     unauthorised: Unauthorised) extends FrontendController(mcc) with I18nSupport {

  def authorisedAction(block: Request[AnyContent] => User => Future[Result],
                       checkMigrationStatus: Boolean = false,
                       allowAgentAccess: Boolean = false): Action[AnyContent] = Action.async {
    implicit request =>

      enrolmentsAuthService
        .authorised
        .retrieve(Retrievals.allEnrolments and Retrievals.affinityGroup) {
          case _ ~ Some(AffinityGroup.Agent) =>
            if(allowAgentAccess) {
              agentPredicate.authoriseAsAgent(block)
            } else {
              Logger.debug("[AuthorisedController][authorisedAction] User is agent and agent access is forbidden. Redirecting to VACLUF")
              Future.successful(Redirect(appConfig.agentClientLookupHubUrl))
            }
          case enrolments ~ Some(_) => authoriseAsNonAgent(block, enrolments, checkMigrationStatus)
          case _ =>
            Logger.warn("[AuthorisedController][authorisedAction] - Missing affinity group")
            Future.successful(InternalServerError)
        } recoverWith {
          case _: NoActiveSession => Future.successful(Redirect(appConfig.signInUrl))
          case _: InsufficientEnrolments =>
            Logger.warn(s"[AuthorisedController][authorisedAction] insufficient enrolment exception encountered")
            Future.successful(Forbidden(unauthorised()))
          case _: AuthorisationException =>
            Logger.warn(s"[AuthorisedController][authorisedAction] encountered unauthorisation exception")
            Future.successful(Forbidden(unauthorised()))
        }
  }

  private def authoriseAsNonAgent(block: Request[AnyContent] => User => Future[Result], enrolments: Enrolments, checkMigrationStatus: Boolean)
                                 (implicit request: Request[AnyContent]): Future[Result] = {

    val vatEnrolments: Set[Enrolment] = User.extractVatEnrolments(enrolments)

    if(vatEnrolments.exists(_.key == Keys.mtdVatEnrolmentKey)) {
      val containsNonMtdVat: Boolean = User.containsNonMtdVat(vatEnrolments)

      vatEnrolments.collectFirst {
        case Enrolment(Keys.mtdVatEnrolmentKey, EnrolmentIdentifier(Keys.mtdVatIdentifierKey, vrn) :: _, status, _) =>

          val user = User(vrn, status == Keys.activated, containsNonMtdVat)

          if(checkMigrationStatus) {
            hybridUserPredicate.authoriseMigratedUserAction(block)(request, user)
          } else {
            block(request)(user)
          }
      } getOrElse {
        Logger.warn("[AuthPredicate][authoriseAsNonAgent] Non-agent with invalid VRN")
        Future.successful(InternalServerError)
      }
    } else {
      Logger.debug("[AuthPredicate][authoriseAsNonAgent] Non-agent with no HMRC-MTD-VAT enrolment. Rendering unauthorised view.")
      Future.successful(Forbidden(unauthorised()))
    }
  }

  def authorisedMigratedUserAction(block: Request[AnyContent] => User => Future[Result]): Action[AnyContent] = authorisedAction(
    block,
    checkMigrationStatus = true
  )

  def authorisedActionAllowAgents(block: Request[AnyContent] => User => Future[Result]): Action[AnyContent] = authorisedAction(
    block,
    allowAgentAccess = appConfig.features.agentAccess()
  )
}
