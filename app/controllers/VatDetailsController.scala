/*
 * Copyright 2017 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}

import config.AppConfig
import models.{User, VatDetailsModel}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Request, Result}
import services.{BtaHeaderPartialService, EnrolmentsAuthService, VatDetailsService}
import uk.gov.hmrc.auth.core.retrieve.Retrievals
import uk.gov.hmrc.auth.core.{Enrolment, NoActiveSession}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class VatDetailsController @Inject()(val messagesApi: MessagesApi, enrolmentsAuthService: EnrolmentsAuthService,
                                     btaHeaderPartialService: BtaHeaderPartialService,
                                     implicit val appConfig: AppConfig, vatDetailsService: VatDetailsService)
  extends FrontendController with I18nSupport {

  def details(): Action[AnyContent] = detailsInternal { implicit request => user =>
    for {
      detailsModel <- handleVatDetailsModel(user)
      serviceInfo <- btaHeaderPartialService.btaHeaderPartial()
    } yield Ok(views.html.vatDetails.details(user, detailsModel, serviceInfo))
  }

  private[controllers] def handleVatDetailsModel(user: User)(implicit hc: HeaderCarrier): Future[VatDetailsModel] = {
    vatDetailsService.getVatDetails(user).map {
      case Right(detailsModel) => detailsModel
      case Left(_) => VatDetailsModel(None, None)
    }
  }

  private def detailsInternal(block: Request[AnyContent] => User => Future[Result]): Action[AnyContent] = Action.async { implicit request =>
    enrolmentsAuthService.authorised(Enrolment("HMRC-MTD-VAT")).retrieve(Retrievals.authorisedEnrolments) {
      enrolments => {
        val user = User(enrolments)
        block(request)(user)
      }

    }.recover {
      case _: NoActiveSession => Redirect(controllers.routes.ErrorsController.sessionTimeout())
    }
  }
}
