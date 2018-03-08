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
import models.payments.{Payment, Payments}
import models.viewModels.OpenPaymentsModel
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.{DateService, EnrolmentsAuthService, PaymentsService}

import javax.inject.Inject

class OpenPaymentsController @Inject()(val messagesApi: MessagesApi,
                                       val enrolmentsAuthService: EnrolmentsAuthService,
                                       val paymentsService: PaymentsService,
                                       val dateService: DateService,
                                       implicit val appConfig: AppConfig)
  extends AuthorisedController with I18nSupport {

  def openPayments(): Action[AnyContent] = authorisedAction { implicit request =>
    user =>
      paymentsService.getOpenPayments(user.vrn).map {
        case Some(Payments(payments)) if payments.nonEmpty => Ok(views.html.payments.openPayments(user, getModel(payments)))
        case Some(_) => Ok(views.html.payments.noPayments(user))
        case None => InternalServerError(views.html.payments.paymentsError(user))
      }
  }

  private[controllers] def getModel(payments: Seq[Payment]): Seq[OpenPaymentsModel] = payments.map { payment =>
    OpenPaymentsModel(
      "Return",
      payment.outstandingAmount,
      payment.due,
      payment.start,
      payment.end,
      payment.periodKey,
      payment.due.isBefore(dateService.now())
    )
  }
}
