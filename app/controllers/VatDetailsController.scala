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
import connectors.httpParsers.CustomerInfoHttpParser.HttpGetResult
import models.viewModels.VatDetailsViewModel
import models.{CustomerInformation, VatDetailsModel}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.{EnrolmentsAuthService, VatDetailsService}

@Singleton
class VatDetailsController @Inject()(val messagesApi: MessagesApi,
                                     val enrolmentsAuthService: EnrolmentsAuthService,
                                     implicit val appConfig: AppConfig,
                                     vatDetailsService: VatDetailsService)
  extends AuthorisedController with I18nSupport {

  def details(): Action[AnyContent] = authorisedAction { implicit request =>
    user =>

      val nextActionsCall = vatDetailsService.getVatDetails(user)
      val customerInfoCall = vatDetailsService.getCustomerInfo(user)

      for {
        nextActions <- nextActionsCall
        customerInfo <- customerInfoCall
      } yield {
        val viewModel = constructViewModel(customerInfo, nextActions)
        Ok(views.html.vatDetails.details(user, viewModel))
      }
  }

  private[controllers] def constructViewModel(customerInfo: HttpGetResult[CustomerInformation],
                                              nextActions: HttpGetResult[VatDetailsModel]): VatDetailsViewModel = {
    // TODO: REVIEW - Handle failures properly
    val model = nextActions match {
      case Right(detailsModel) => detailsModel
      case Left(_) => VatDetailsModel(None, None)
    }

    // TODO: REVIEW - Handle failures properly
    val tradingName: String = customerInfo match {
      case Right(customerInformation) => customerInformation.tradingName
      case Left(_) => ""
    }

    val paymentDueDate: Option[LocalDate] = model.payment.map(_.due)

    val obligationDueDate: Option[LocalDate] = model.vatReturn.map(_.due)

    VatDetailsViewModel(paymentDueDate, obligationDueDate, Some(tradingName))
  }
}
