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

package controllers

import common.{EnrolmentKeys => Keys}
import common.SessionKeys
import config.AppConfig
import controllers.predicates.{AgentPredicate, FinancialPredicate}

import javax.inject.{Inject, Singleton}
import models.User
import play.api.i18n.I18nSupport
import play.api.mvc._
import services._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.errors.{Unauthorised, UserInsolventError}
import config.ServiceErrorHandler
import utils.LoggerUtil

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthorisedController @Inject()(mcc: MessagesControllerComponents,
                                     enrolmentsAuthService: EnrolmentsAuthService,
                                     financialPredicate: FinancialPredicate,
                                     agentPredicate: AgentPredicate,
                                     accountDetailsService: AccountDetailsService,
                                     serviceErrorHandler: ServiceErrorHandler,
                                     unauthorised: Unauthorised,
                                     userInsolvent: UserInsolventError)
                                    (implicit appConfig: AppConfig,
                                     ec: ExecutionContext) extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  def authorisedAction(block: Request[AnyContent] => User => Future[Result],
                       financialRequest: Boolean = false,
                       allowAgentAccess: Boolean = false): Action[AnyContent] = Action.async {
    implicit request =>

      enrolmentsAuthService
        .authorised
        .retrieve(Retrievals.allEnrolments and Retrievals.affinityGroup) {
          case _ ~ Some(AffinityGroup.Agent) =>
            if(allowAgentAccess) {
              agentPredicate.authoriseAsAgent(block, financialRequest)
            } else {
              logger.debug("[AuthorisedController][authorisedAction] User is agent and agent access is forbidden. Redirecting to VACLUF")
              Future.successful(Redirect(appConfig.agentClientLookupHubUrl))
            }
          case enrolments ~ Some(_) => authoriseAsNonAgent(block, enrolments, financialRequest)
          case _ =>
            logger.warn("[AuthorisedController][authorisedAction] - Missing affinity group")
            Future.successful(serviceErrorHandler.showInternalServerError)
        } recoverWith {
          case _: NoActiveSession => Future.successful(Redirect(appConfig.signInUrl))
          case _: InsufficientEnrolments =>
            logger.warn(s"[AuthorisedController][authorisedAction] insufficient enrolment exception encountered")
            Future.successful(Forbidden(unauthorised()))
          case _: AuthorisationException =>
            logger.warn(s"[AuthorisedController][authorisedAction] encountered unauthorisation exception")
            Future.successful(Forbidden(unauthorised()))
        }
  }

  private def authoriseAsNonAgent(block: Request[AnyContent] => User => Future[Result],
                                  enrolments: Enrolments,
                                  financialRequest: Boolean)
                                 (implicit request: Request[AnyContent]): Future[Result] = {

    val vatEnrolments: Set[Enrolment] = User.extractVatEnrolments(enrolments)

    if(vatEnrolments.exists(_.key == Keys.mtdVatEnrolmentKey)) {
      val containsNonMtdVat: Boolean = User.containsNonMtdVat(vatEnrolments)

      vatEnrolments.collectFirst {
        case Enrolment(Keys.mtdVatEnrolmentKey, Seq(EnrolmentIdentifier(Keys.mtdVatIdentifierKey, vrn)), status, _) =>

          implicit val user: User = User(vrn, status == Keys.activated, containsNonMtdVat)

          request.session.get(SessionKeys.insolventWithoutAccessKey) match {
            case Some("true") => Future.successful(Forbidden(userInsolvent()))
            case Some("false") => checkHybridAndInsolvency(block, financialRequest)
            case _ => accountDetailsService.getAccountDetails(user.vrn).flatMap {
              case Right(response) if response.details.isInsolventWithoutAccess =>
                logger.debug("[AuthorisedController][authoriseAsNonAgent] - User is insolvent and not continuing to trade")
                Future.successful(Forbidden(userInsolvent()).addingToSession(SessionKeys.insolventWithoutAccessKey -> "true"))
              case Right(_) =>
                logger.debug("[AuthorisedController][authoriseAsNonAgent] - Authenticated as principle")
                checkHybridAndInsolvency(block, financialRequest)
              case _ =>
                logger.warn("[AuthorisedController][authoriseAsNonAgent] - Failure obtaining insolvency status from Customer Info API")
                Future.successful(serviceErrorHandler.showInternalServerError)
            }
          }

      } getOrElse {
        logger.warn("[AuthPredicate][authoriseAsNonAgent] Non-agent with invalid VRN")
        Future.successful(serviceErrorHandler.showInternalServerError)
      }
    } else {
      logger.debug("[AuthPredicate][authoriseAsNonAgent] Non-agent with no HMRC-MTD-VAT enrolment. Rendering unauthorised view.")
      Future.successful(Forbidden(unauthorised()))
    }
  }

  def financialAction(block: Request[AnyContent] => User => Future[Result]): Action[AnyContent] = authorisedAction(
    block,
    financialRequest = true,
    allowAgentAccess = true
  )

  def authorisedActionAllowAgents(block: Request[AnyContent] => User => Future[Result]): Action[AnyContent] = authorisedAction(
    block,
    allowAgentAccess = true
  )

  def checkHybridAndInsolvency(block: Request[AnyContent] => User => Future[Result], financialRequest: Boolean)
                              (implicit request: Request[AnyContent], user: User): Future[Result] =
    if(financialRequest) {
      financialPredicate.authoriseFinancialAction(block)(request, user)
    } else {
      block(request)(user).map(result => result.addingToSession(SessionKeys.insolventWithoutAccessKey -> "false"))
    }
}