/*
 * Copyright 2022 HM Revenue & Customs
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

import com.google.inject.Inject
import common.SessionKeys
import config.AppConfig
import controllers.AuthorisedController
import controllers.predicates.DDInterruptPredicate
import models.User
import models.payments.{Payment, PaymentOnAccount}
import models.viewModels.WhatYouOweChargeModel._
import models.viewModels.{WhatYouOweChargeModel, WhatYouOweViewModel}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{AccountDetailsService, DateService, PaymentsService, ServiceInfoService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.LoggerUtil
import views.html.errors.PaymentsError
import views.html.payments.{NoPayments, WhatYouOwe}
import scala.concurrent.ExecutionContext

class WhatYouOweController @Inject()(authorisedController: AuthorisedController,
                                     ddInterrupt: DDInterruptPredicate,
                                     dateService: DateService,
                                     paymentsService: PaymentsService,
                                     serviceInfoService: ServiceInfoService,
                                     mcc: MessagesControllerComponents,
                                     paymentsError: PaymentsError,
                                     view: WhatYouOwe,
                                     noPayments: NoPayments,
                                     accountDetailsService: AccountDetailsService)
                                    (implicit ec: ExecutionContext,
                                     appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  def show: Action[AnyContent] = authorisedController.financialAction { implicit request =>

    implicit user => ddInterrupt.interruptCheck { _ =>

      for {
        serviceInfoContent <- serviceInfoService.getPartial
        payments <- paymentsService.getOpenPayments(user.vrn)
        mandationStatusCall <- accountDetailsService.getAccountDetails(user.vrn).map(_.map(_.mandationStatus))

      } yield {
        payments match {
          case Right(Some(payments)) =>

            val mandationStatus = mandationStatusCall.getOrElse("")

            constructViewModel(payments.financialTransactions.filterNot(_.chargeType equals PaymentOnAccount), mandationStatus) match {
              case Some(model) =>
                Ok(view(model, serviceInfoContent))
              case None =>
                logger.warn("[WhatYouOweController][show] originalAmount field missing from payment; failed to render view")
                InternalServerError(paymentsError())
            }
          case Right(_) =>
            val clientName = request.session.get(SessionKeys.mtdVatvcAgentClientName)
            Ok(noPayments(user, serviceInfoContent, clientName, mandationStatusCall.getOrElse("")))
          case Left(error) =>
            logger.warn(s"[WhatYouOweController][show] Payments error: $error")
            InternalServerError(paymentsError())
        }
      }

    }
  }

  def constructViewModel(payments: Seq[Payment], mandationStatus: String)(implicit user: User, messages: Messages): Option[WhatYouOweViewModel] = {

    val totalAmount = payments.map(_.outstandingAmount).sum
    val chargeModels: Seq[WhatYouOweChargeModel] = payments.collect {
      case payment if payment.originalAmount.isDefined =>
        WhatYouOweChargeModel(
          chargeValue = payment.chargeType.value,
          chargeDescription = description(payment, user.isAgent).getOrElse(""),
          chargeTitle = title(payment.chargeType.value),
          outstandingAmount = payment.outstandingAmount,
          originalAmount = payment.originalAmount.get,
          clearedAmount = payment.clearedAmount,
          dueDate = payment.due,
          periodKey = if (payment.periodKey == "0000") None else Some(payment.periodKey),
          isOverdue = payment.due.isBefore(dateService.now()) && !payment.ddCollectionInProgress,
          chargeReference = payment.chargeReference,
          makePaymentRedirect = makePaymentRedirect(payment),
          periodFrom = periodFrom(payment),
          periodTo = periodTo(payment)
        )
    }
    if(payments.length == chargeModels.length) {
      Some(WhatYouOweViewModel(totalAmount, chargeModels, mandationStatus))
    } else {
      None
    }
  }
}