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
import javax.inject.{Inject, Singleton}

import config.AppConfig
import models.viewModels.VatDetailsViewModel
import models.VatDetailsModel
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.{AccountDetailsService, DateService, EnrolmentsAuthService, VatDetailsService}

@Singleton
class VatDetailsController @Inject()(val messagesApi: MessagesApi,
                                     val enrolmentsAuthService: EnrolmentsAuthService,
                                     implicit val appConfig: AppConfig,
                                     vatDetailsService: VatDetailsService,
                                     accountDetailsService: AccountDetailsService,
                                     dateService: DateService)
  extends AuthorisedController with I18nSupport {

  def details(): Action[AnyContent] = authorisedAction { implicit request =>
    user =>
      val nextActionsCall = vatDetailsService.getVatDetails(user, dateService.now())
      val entityNameCall = accountDetailsService.getEntityName(user.vrn)

      for {
        nextActions <- nextActionsCall
        customerInfo <- entityNameCall
      } yield {
        nextActions match {
          case Right(actions) => Ok(views.html.vatDetails.details(user, constructViewModel(actions, customerInfo)))
          case Left(_) => Ok(views.html.vatDetails.details(user, constructViewModel(VatDetailsModel(None, None), customerInfo)))
        }
      }
  }

  private[controllers] def constructViewModel(vatDetailsModel: VatDetailsModel, entityName: Option[String]): VatDetailsViewModel = {
    val paymentDueDate: Option[LocalDate] = vatDetailsModel.payment.map(_.due)
    val obligationDueDate: Option[LocalDate] = vatDetailsModel.vatReturn.map(_.due)

    val (returnOverdue: Boolean, paymentOverdue: Boolean) = (obligationDueDate, paymentDueDate) match {
      case (Some(returnDate), Some(paymentDate)) => (dateService.now().isAfter(returnDate), dateService.now().isAfter(paymentDate))
      case (Some(returnDate), _) => (dateService.now().isAfter(returnDate), false)
      case (_, Some(paymentDate)) => (false, dateService.now().isAfter(paymentDate))
      case (_ , _) => (false, false)
    }

    VatDetailsViewModel(paymentDueDate, obligationDueDate, entityName, returnOverdue, paymentOverdue, dateService.now().getYear)
  }
}
