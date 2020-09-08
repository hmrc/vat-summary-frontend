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

package controllers.partials

import common.EnrolmentKeys
import config.AppConfig
import javax.inject.{Inject, Singleton}
import models.User
import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._
import services.EnrolmentsAuthService
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.{AuthorisationException, Enrolment, EnrolmentIdentifier, NoActiveSession}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.partials.btaHome.{ClaimEnrolment, PartialMigration, VatSection}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BtaHomeController @Inject()(enrolmentsAuthService: EnrolmentsAuthService,
                                  implicit val appConfig: AppConfig,
                                  val mcc: MessagesControllerComponents,
                                  implicit val ec: ExecutionContext,
                                  claimEnrolmentView: ClaimEnrolment,
                                  vatSectionView: VatSection,
                                  partialMigrationView: PartialMigration)
  extends FrontendController(mcc) with I18nSupport {

  def vatSection(): Action[AnyContent] = enrolledAction { implicit request => _ =>
    Future.successful(Ok(vatSectionView()))
  }

  def claimEnrolment(): Action[AnyContent] = enrolledActionNonMtd { implicit request =>
    user =>
      Future.successful(Ok(claimEnrolmentView(user.vrn)))
  }

  def partialMigration(): Action[AnyContent] = enrolledActionNonMtd { implicit request => _ =>
    Future.successful(Ok(partialMigrationView()))
  }

  private def enrolledAction(block: Request[AnyContent] => User => Future[Result]): Action[AnyContent] = Action.async { implicit request =>
    enrolmentsAuthService
      .authorised(Enrolment("HMRC-MTD-VAT"))
      .retrieve(Retrievals.authorisedEnrolments) {
        enrolments =>
          enrolments.enrolments.collectFirst {
          case Enrolment(EnrolmentKeys.mtdVatEnrolmentKey, EnrolmentIdentifier(_, vrn) :: _, status, _) =>
            val user = User(vrn, status == EnrolmentKeys.activated)
            block(request)(user)
        } getOrElse {
            Logger.warn("[BtaHomeController][enrolledAction] Error retrieving VRN")
            Future.successful(InternalServerError)
        }
      } recover {
        case _: NoActiveSession => Unauthorized
        case _: AuthorisationException => Forbidden
      }
  }

  private def enrolledActionNonMtd(block: Request[AnyContent] => User => Future[Result]): Action[AnyContent] = Action.async { implicit request =>
    enrolmentsAuthService
      .authorised(Enrolment("HMCE-VATDEC-ORG"))
      .retrieve(Retrievals.authorisedEnrolments) {
        enrolments => enrolments.enrolments.collectFirst {
          case Enrolment(EnrolmentKeys.vatDecEnrolmentKey, EnrolmentIdentifier(_, vrn) :: _, status, _) =>
            val user = User(vrn, status == EnrolmentKeys.activated, hasNonMtdVat = true)
            block(request)(user)
        } getOrElse {
          Logger.warn("[BtaHomeController][enrolledActionNonMtd] Error retrieving VRN")
          Future.successful(InternalServerError)
        }
      } recover {
        case _: NoActiveSession => Unauthorized
        case _: AuthorisationException => Forbidden
      }
  }
}
