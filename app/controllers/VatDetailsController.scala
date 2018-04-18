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

import audit.AuditingService
import audit.models.{NextOpenObligationAuditModel, NextPaymentAuditModel}
import javax.inject.{Inject, Singleton}
import config.AppConfig
import models.viewModels.VatDetailsViewModel
import models.{User, VatDetailsModel}
import models.errors.HttpError
import models.obligations.{Obligation, VatReturnObligation}
import models.payments.Payment
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.{AccountDetailsService, DateService, EnrolmentsAuthService, VatDetailsService}
import uk.gov.hmrc.http.HeaderCarrier

@Singleton
class VatDetailsController @Inject()(val messagesApi: MessagesApi,
                                     val enrolmentsAuthService: EnrolmentsAuthService,
                                     implicit val appConfig: AppConfig,
                                     vatDetailsService: VatDetailsService,
                                     accountDetailsService: AccountDetailsService,
                                     dateService: DateService,
                                     auditingService: AuditingService)
  extends AuthorisedController with I18nSupport {

  def details(): Action[AnyContent] = authorisedAction { implicit request =>
    user =>
      val nextActionsCall = vatDetailsService.getVatDetails(user, dateService.now())
      val entityNameCall = accountDetailsService.getEntityName(user.vrn)

      val viewModel = for {
        nextActions <- nextActionsCall
        customerInfo <- entityNameCall
      } yield {
        auditEvents(user, nextActions)
        constructViewModel(nextActions, customerInfo)
      }

      viewModel.map { model =>
        Ok(views.html.vatDetails.details(user, model))
      }
  }

  private[controllers] def constructViewModel(vatDetailsModel: VatDetailsModel, entityName: Option[String]): VatDetailsViewModel = {

    val getDueDate: Either[HttpError, Option[Obligation]] => Option[LocalDate] = _.fold(_ => None, _.map(_.due))
    val getIsOverdue: Option[LocalDate] => Boolean = _.fold(false)(d => dateService.now().isAfter(d))
    val payment: Option[LocalDate] = getDueDate(vatDetailsModel.payment)
    val paymentIsOverdue = getIsOverdue(payment)
    val paymentHasError = vatDetailsModel.payment.isLeft
    val obligation: Option[LocalDate] = getDueDate(vatDetailsModel.vatReturn)
    val obligationIsOverdue = getIsOverdue(obligation)
    val obligationHasError = vatDetailsModel.vatReturn.isLeft

    VatDetailsViewModel(
      payment,
      obligation,
      entityName,
      dateService.now().getYear,
      obligationIsOverdue,
      paymentIsOverdue,
      obligationHasError,
      paymentHasError
    )
  }

  private[controllers] def auditEvents(user: User, details: VatDetailsModel)(implicit hc: HeaderCarrier): Unit = {
    val obligation: Option[VatReturnObligation] = details.vatReturn match {
      case Right(data) => data
      case _ => None
    }

    val payment: Option[Payment] = details.payment match {
      case Right(data) => data
      case _ => None
    }

    auditingService.audit(NextOpenObligationAuditModel(user, obligation))
    auditingService.audit(NextPaymentAuditModel(user, payment))
  }
}
