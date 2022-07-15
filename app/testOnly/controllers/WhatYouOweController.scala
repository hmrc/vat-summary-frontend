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
import models.payments.{ChargeType, Payment, PaymentOnAccount, PaymentWithPeriod}
import models.viewModels._
import play.api.i18n.I18nSupport
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
                logger.warn("[WhatYouOweController][show] required field(s) missing from payment(s); failed to render view")
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

  private[controllers] def categoriseCharges(payments: Seq[Payment]): Seq[ChargeDetailsViewModel] = {

    payments collect {
      case p: PaymentWithPeriod if
        p.chargeType.isInterest
        && p.chargeReference.isDefined
        && p.originalAmount.isDefined => buildCrystallisedIntViewModel(p)

      case p if
        p.originalAmount.isDefined
        && p.chargeType.notInterest => buildStandardChargeViewModel(p)
    }

  }

  private[controllers] def buildCrystallisedIntViewModel(payment: PaymentWithPeriod): CrystallisedInterestViewModel = {
    CrystallisedInterestViewModel(
      periodFrom = payment.periodFrom,
      periodTo = payment.periodTo,
      chargeType = payment.chargeType.value,
      interestRate = 5.00, // TODO replace with API field
      dueDate = payment.due,
      interestAmount = payment.originalAmount.get,
      amountReceived = payment.clearedAmount.getOrElse(0),
      leftToPay = payment.outstandingAmount,
      isOverdue = payment.isOverdue(dateService.now()),
      chargeReference = payment.chargeReference.get,
      isPenalty = ChargeType.penaltyChargeTypes.contains(payment.chargeType)
    )
  }

  private[controllers] def buildStandardChargeViewModel(payment: Payment): StandardChargeViewModel = {
    import StandardChargeViewModel._
    StandardChargeViewModel(
      chargeType = payment.chargeType.value,
      outstandingAmount = payment.outstandingAmount,
      originalAmount = payment.originalAmount.get,
      clearedAmount = payment.clearedAmount.getOrElse(0),
      dueDate = payment.due,
      periodKey = payment.periodKey,
      isOverdue = payment.isOverdue(dateService.now()),
      chargeReference = payment.chargeReference,
      periodFrom = periodFrom(payment),
      periodTo = periodTo(payment)
    )
  }

  def constructViewModel(payments: Seq[Payment], mandationStatus: String): Option[WhatYouOweViewModel] = {

    val totalAmount = payments.map(_.outstandingAmount).sum
    val chargeModels = categoriseCharges(payments)
    if(payments.length == chargeModels.length) {
      Some(WhatYouOweViewModel(totalAmount, chargeModels, mandationStatus))
    } else {
      None
    }
  }
}
