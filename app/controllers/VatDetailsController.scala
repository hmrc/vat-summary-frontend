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

package controllers

import javax.inject.{Inject, Singleton}

import config.AppConfig
import models.{User, VatDetailsModel}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.{BtaHeaderPartialService, EnrolmentsAuthService, VatDetailsService}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

@Singleton
class VatDetailsController @Inject()(val messagesApi: MessagesApi,
                                     val enrolmentsAuthService: EnrolmentsAuthService,
                                     btaHeaderPartialService: BtaHeaderPartialService,
                                     implicit val appConfig: AppConfig,
                                     vatDetailsService: VatDetailsService)
  extends AuthorisedController with I18nSupport {

  def details(): Action[AnyContent] = authorisedAction { implicit request =>
    user =>
      for {
        detailsModel <- handleVatDetailsModel(user)
        serviceInfo <- btaHeaderPartialService.btaHeaderPartial()
        tradingName <- vatDetailsService.getTradingName(user)
      } yield Ok(views.html.vatDetails.details(user, detailsModel, serviceInfo, tradingName))
  }

  private[controllers] def handleVatDetailsModel(user: User)(implicit hc: HeaderCarrier): Future[VatDetailsModel] = {
    vatDetailsService.getVatDetails(user).map {
      case Right(detailsModel) => detailsModel
      case Left(_) => VatDetailsModel(None, None)
    }
  }
}
