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

      val viewModel = for {
        nextActions <- nextActionsCall
        customerInfo <- entityNameCall
      } yield constructViewModel(nextActions, customerInfo)

      viewModel.map { model =>
        Ok(views.html.vatDetails.details(user, model))
      }
  }

  private[controllers] def constructViewModel(vatDetailsModel: VatDetailsModel, entityName: Option[String]): VatDetailsViewModel = {
    def getIsOverdue(date: Option[LocalDate]): Boolean = date.fold(false)(d => dateService.now().isAfter(d))

    (vatDetailsModel.payment, vatDetailsModel.vatReturn) match {
      case (Right(maybePayment), Right(maybeVatReturnObligation)) =>
        val paymentDate = maybePayment.map(_.due)
        val returnDate = maybeVatReturnObligation.map(_.due)
        val paymentOverdue = getIsOverdue(paymentDate)
        val returnOverdue = getIsOverdue(returnDate)
        VatDetailsViewModel(paymentDate, returnDate, entityName, dateService.now().getYear, returnOverdue, paymentOverdue)

      case (Left(_), Right(maybeVatReturnObligation)) =>
        val returnDate = maybeVatReturnObligation.map(_.due)
        val returnOverdue = getIsOverdue(returnDate)
        VatDetailsViewModel(None, returnDate, entityName, dateService.now().getYear, returnOverdue, paymentError = true)

      case (Right(maybePayment), Left(_)) =>
        val paymentDate = maybePayment.map(_.due)
        val paymentOverdue = getIsOverdue(paymentDate)
        VatDetailsViewModel(paymentDate, None, entityName, dateService.now().getYear, paymentOverdue, returnError = true)

      case (Left(_), Left(_)) =>
        VatDetailsViewModel(None, None, entityName, dateService.now().getYear, returnError = true, paymentError = true)
    }
  }
}
