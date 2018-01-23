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
import models.viewModels.OpenPaymentsModel
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.{BtaHeaderPartialService, EnrolmentsAuthService}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class OpenPaymentsController @Inject()(val messagesApi: MessagesApi,
                                     val enrolmentsAuthService: EnrolmentsAuthService,
                                     implicit val appConfig: AppConfig)
  extends AuthorisedController with I18nSupport {

  def openPayments(): Action[AnyContent] = authorisedAction { implicit request =>
    user =>
      for {
        paymentsModel <- handleOpenPaymentsModel(user)
      } yield Ok(views.html.payments.openPayments(user, paymentsModel))
  }

  private[controllers] def handleOpenPaymentsModel(user: User)(implicit hc: HeaderCarrier): Future[Seq[OpenPaymentsModel]] = {
    Future.successful(Seq(
      OpenPaymentsModel(
        "Return",
        543.21,
        LocalDate.parse("2000-04-08"),
        LocalDate.parse("2000-01-01"),
        LocalDate.parse("2000-03-31")
      ),
      OpenPaymentsModel(
        "Return",
        123.45,
        LocalDate.parse("2000-08-08"),
        LocalDate.parse("2000-04-01"),
        LocalDate.parse("2000-07-30")
      )
    ))
  }
}
