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

package controllers.predicates

import common.{EnrolmentKeys, SessionKeys, FinancialTransactionsConstants => keys}
import config.AppConfig
import javax.inject.Inject
import models.{MandationStatus, User}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{AnyContent, Request, Result}
import services.{EnrolmentsAuthService, MandationStatusService}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.Retrievals.allEnrolments
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import scala.concurrent.Future

class AgentPredicate @Inject()(authService: EnrolmentsAuthService,
                               val messagesApi: MessagesApi,
                               mandationStatusService: MandationStatusService,
                               implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  def authoriseAsAgent(block: Request[AnyContent] => User => Future[Result])
                      (implicit request: Request[AnyContent]): Future[Result] = {

    val agentDelegatedAuthorityRule: String => Enrolment = vrn =>
      Enrolment(EnrolmentKeys.mtdVatEnrolmentKey)
        .withIdentifier(EnrolmentKeys.mtdVatIdentifierKey, vrn)
        .withDelegatedAuthRule(EnrolmentKeys.agentDelegatedAuthRuleKey)

    request.session.get(SessionKeys.agentSessionVrn) match {
      case Some(vrn) =>
        authService
          .authorised(agentDelegatedAuthorityRule(vrn))
          .retrieve(allEnrolments) {
            enrolments =>
              enrolments.enrolments.collectFirst {
                case Enrolment(EnrolmentKeys.agentEnrolmentKey, EnrolmentIdentifier(_, arn) :: _, EnrolmentKeys.activated, _) => arn
              } match {
                case Some(_) => checkMandationStatus(block, enrolments, vrn)
                case None =>
                  Logger.debug("[AuthPredicate][authoriseAsAgent] - Agent with no HMRC-AS-AGENT enrolment. Rendering unauthorised view.")
                  //TODO: need error page
                  Future.successful(Forbidden)
              }
          } recover {
          case _: NoActiveSession =>
            Logger.debug(s"AuthoriseAsAgentWithClient][authoriseAsAgent] - No active session. Redirecting to ${appConfig.signInUrl}")
            Redirect(appConfig.signInUrl)
          case _: AuthorisationException =>
            Logger.debug(s"[AuthoriseAsAgentWithClient][authoriseAsAgent] - Agent does not have delegated authority for Client. " +
              s"Redirecting to ${appConfig.agentClientUnauthorisedUrl(request.uri)}")
            Redirect(appConfig.agentClientUnauthorisedUrl(request.uri))
        }
      case None =>
        Logger.debug(s"[AuthPredicate][authoriseAsAgent] - No Client VRN in session. Redirecting to ${appConfig.agentClientLookupStartUrl(request.uri)}")
        Future.successful(Redirect(appConfig.agentClientLookupStartUrl(request.uri)))
    }
  }

  private def checkMandationStatus(block: Request[AnyContent] => User => Future[Result], enrolments: Enrolments, vrn: String)
                                  (implicit request: Request[AnyContent], hc: HeaderCarrier): Future[Result] = {

    mandationStatusService.getMandationStatus(vrn) flatMap {
      case Right(MandationStatus(keys.nonMTDfB)) =>
        val user = User(enrolments, Some(vrn))
        block(request)(user)
      case Right(_) =>
        //TODO: add page content
        Logger.debug("[AuthPredicate][checkMandationStatus] - Agent acting for MTDfB client")
        Future.successful(Forbidden)
      case Left(error) =>
        Logger.warn(s"[AuthPredicate][checkMandationStatus] - Error returned from mandationStatusService: $error")
        Future.successful(InternalServerError)
    }
  }
}
