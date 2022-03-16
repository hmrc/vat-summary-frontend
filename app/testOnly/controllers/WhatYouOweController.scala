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

import java.time.LocalDate

import com.google.inject.Inject
import common.SessionKeys
import config.AppConfig
import controllers.AuthorisedController
import controllers.predicates.DDInterruptPredicate
import models.User
import models.payments.Payments
import models.viewModels.WhatYouOweChargeModel._
import models.viewModels.{WhatYouOweChargeModel, WhatYouOweViewModel}
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc.Results.{InternalServerError, Ok}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{DateService, PaymentsService, ServiceInfoService}
import uk.gov.hmrc.http.HeaderCarrier
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
                                     noPayments: NoPayments)
                                    (implicit ec: ExecutionContext,
                                     appConfig: AppConfig)
  extends FrontendController(mcc) with I18nSupport with LoggerUtil {

  def show: Action[AnyContent] = authorisedController.financialAction { implicit request =>
    implicit user => ddInterrupt.interruptCheck { _ =>

      for {
        serviceInfoContent <- serviceInfoService.getPartial
        payments <- paymentsService.getOpenPayments(user.vrn)
      } yield {
        payments match {
          case Right(Some(payments)) =>
            constructViewModel(payments) match {
              case Some(model) =>
                Ok(view(model, serviceInfoContent))
              case None =>
                InternalServerError(paymentsError())
            }
          case Right(_) =>
            val clientName = request.session.get(SessionKeys.mtdVatvcAgentClientName)
            Ok(noPayments(user, serviceInfoContent, clientName))
          case Left(_) =>
            InternalServerError(paymentsError())
        }
      }
    }
  }

  val viewModel = WhatYouOweViewModel(
    totalAmount = 2000.00,
    charges = Seq(WhatYouOweChargeModel(
      chargeDescription = "VAT OA Debit Charge",
      chargeTitle = "VAT OA Debit Charge",
      outstandingAmount = 1000.00,
      originalAmount = 1000.00,
      clearedAmount = Some(00.00),
      dueDate = LocalDate.parse("2017-03-08"),
      periodKey = Some("#001"),
      isOverdue = false,
      chargeReference = Some("XD002750002155"),
      makePaymentRedirect = "/vat-through-software/make-payment",
      periodFrom = Some(LocalDate.parse("2017-01-01")),
      periodTo = Some(LocalDate.parse("2017-03-01"))
    ),
      WhatYouOweChargeModel(
        chargeDescription = "VAT OA Debit Charge",
        chargeTitle = "VAT OA Debit Charge",
        outstandingAmount = 1000.00,
        originalAmount = 1000.00,
        clearedAmount = Some(00.00),
        dueDate = LocalDate.parse("2017-03-08"),
        periodKey = Some("#001"),
        isOverdue = true,
        chargeReference = Some("XD002750002156"),
        makePaymentRedirect = "/vat-through-software/make-payment",
        periodFrom = Some(LocalDate.parse("2017-01-01")),
        periodTo = Some(LocalDate.parse("2017-03-01"))
      ))
  )

  def constructViewModel(payments: Payments)(implicit user: User): Option[WhatYouOweViewModel] = {
    val totalAmount = payments.financialTransactions.map(_.outstandingAmount).sum
    val chargeModels: Seq[WhatYouOweChargeModel] = payments.financialTransactions.collect {
      case payment if payment.originalAmount.isDefined && description(payment).isDefined =>
        WhatYouOweChargeModel(
          chargeDescription = description(payment).get,
          chargeTitle = payment.chargeType.value,
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
    if(payments.financialTransactions.length == chargeModels.length) {
      Some(WhatYouOweViewModel(totalAmount, chargeModels))
    } else {
      None
    }
  }
}
