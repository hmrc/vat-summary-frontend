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

import config.AppConfig
import javax.inject.Inject
import models.viewModels.AccountDetailsModel
import models.{CustomerInformation, User}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.{AccountDetailsService, EnrolmentsAuthService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

class AccountDetailsController @Inject()(val messagesApi: MessagesApi,
                                         authorisedController: AuthorisedController,
                                         val enrolmentsAuthService: EnrolmentsAuthService,
                                         val detailsService: AccountDetailsService,
                                         implicit val appConfig: AppConfig)
  extends FrontendController with I18nSupport {

  def accountDetails(): Action[AnyContent] = authorisedController.authorisedAction { implicit request => user =>
    handleAccountDetailsModel(user).map(accountModel => {
      Ok(views.html.account.accountDetails(user, accountModel))
    })
  }

  private[controllers] def handleAccountDetailsModel(user: User)(implicit hc: HeaderCarrier): Future[AccountDetailsModel] = {
      detailsService.getAccountDetails(user.vrn).map {
        case Right(details: CustomerInformation) =>
          AccountDetailsModel(
            details.entityName.get,
            details.correspondenceAddress,
            details.businessAddress,
            details.businessPrimaryPhoneNumber.get,
            details.businessMobileNumber.get,
            details.correspondencePrimaryPhoneNumber.get,
            details.businessEmailAddress.get
          )
        case Left(error) => throw new Exception(error.message)
    }
  }
}
