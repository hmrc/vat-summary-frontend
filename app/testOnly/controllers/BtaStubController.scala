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

package testOnly.controllers

import javax.inject.{Inject, Singleton}

import config.AppConfig
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.EnrolmentsAuthService
import testOnly.services.BtaStubService
import uk.gov.hmrc.auth.core.NoActiveSession
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class BtaStubController @Inject()(val messagesApi: MessagesApi, enrolmentsAuthService: EnrolmentsAuthService,
                                  btaStubService: BtaStubService, implicit val appConfig: AppConfig)
  extends FrontendController with I18nSupport {

  val viewVatPartial: String = appConfig.viewVatPartial
  val claimEnrolmentPartial: String = appConfig.claimEnrolmentPartial
  val partialMigrationPartial: String = appConfig.partialMigrationPartial

  def viewVat(): Action[AnyContent] = showPartial(viewVatPartial)
  def claimEnrolment(): Action[AnyContent] = showPartial(claimEnrolmentPartial)
  def partialMigration(): Action[AnyContent] = showPartial(partialMigrationPartial)

  private def showPartial(partialUrl: String): Action[AnyContent] = Action.async { implicit request =>
    enrolmentsAuthService.authorised() {
      btaStubService.getPartial(partialUrl).map { partial =>
        Ok(testOnly.views.html.btaStub(partial))
      }
    }.recoverWith {
      case _: NoActiveSession => Future.successful(Unauthorized(views.html.errors.sessionTimeout()))
    }
  }
}
