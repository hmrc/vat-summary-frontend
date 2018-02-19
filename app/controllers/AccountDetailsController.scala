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

import java.time.LocalDate
import javax.inject.Inject

import config.AppConfig
import models.User
import models.viewModels.AccountDetailsModel
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.{EnrolmentsAuthService, VatDetailsService}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class AccountDetailsController @Inject()(val messagesApi: MessagesApi,
                                         val enrolmentsAuthService: EnrolmentsAuthService,
                                         val detailsService: VatDetailsService,
                                         val appConfig: AppConfig)
  extends AuthorisedController with I18nSupport {

  def accountDetails(): Action[AnyContent] = authorisedAction { implicit request => user =>
    handleAccountDetailsModel(user).map(accountModel => {
      Ok(views.html.account.accountDetails(user, accountModel))
    })
  }

  private[controllers] def handleAccountDetailsModel(user: User)(implicit hc: HeaderCarrier): Future[AccountDetailsModel] = {
      detailsService.getVatDetails(user.vrn)
  }


    /*Future.successful(
      AccountDetailsModel(
        "Betty Jones",
        "Bedrock Quarry, Bedrock, Graveldon",
        "GV2 4BB",
        "13 Pebble lane, Bedrock, Graveldon",
        "GV13 4BJ",
        "01632 982028",
        "07700 900018",
        "01632 960026",
        "bettylucknexttime@gmail.com"
      )
    )*/
  }
}
